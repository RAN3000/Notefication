package com.ran3000.notefication2;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ran3000.notefication2.data.Note;
import com.ran3000.notefication2.data.NoteDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.main_layout)
    ConstraintLayout mainLayout;
    @BindView(R.id.main_background)
    Button mainBackground;
    @BindView(R.id.main_edittext)
    EditText mainEditText;
    @BindView(R.id.main_send_button)
    ImageButton mainButtonSend;

    private ColorManager colorManager;
    private NoteDatabase database;
    private AppExecutors executors;
    private NoteficationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        colorManager = new ColorManager();
        database = NoteDatabase.getAppDatabase(this);
        executors = new AppExecutors();
        notificationManager = new NoteficationManager(this);
        notificationManager.createNotificationChannel();

        checkFirstRun();

        // check if there are notefication to be shown
        executors.diskIO().execute(() -> {
            if (database.noteDao().getNotesCount() > 0) {
                Intent serviceIntent = new Intent(MainActivity.this, NoteficationForegroundService.class);
                ContextCompat.startForegroundService(MainActivity.this, serviceIntent);
            }
        });

        // no title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // clear FLAG_TRANSLUCENT_STATUS flag:
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, colorManager.getCurrentDarkColor()));

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Timber.d("Activity created.");

        ViewCompat.setTranslationZ(mainBackground, 1);
        ViewCompat.setTranslationZ(mainEditText, 20);
        ViewCompat.setTranslationZ(mainButtonSend, 20);


        // edit text action send
        mainEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendNote();
                    return true;
                }
                return false;
            }
        });

        if (Intent.ACTION_SEND.equals(getIntent().getAction()) && getIntent().getType() != null) {
            if ("text/plain".equals(getIntent().getType())) {
                String sharedText = getIntent().getStringExtra(Intent.EXTRA_TEXT);
                if (sharedText != null) {
                    mainEditText.setText(sharedText);
                }
            }
        }
    }

    @OnClick(R.id.main_send_button)
    public void sendNote() {
        Timber.d("Note sent: %s", mainEditText.getText());

        if (mainEditText.getText().toString().length() > 100) {
            Toast.makeText(this, "Your note is too long", Toast.LENGTH_SHORT).show();
            return;
        }

        Note note = new Note();
        note.setText(mainEditText.getText().toString());
        note.setColor(colorManager.getCurrentColor());

        executors.diskIO().execute(() -> {
            long newId = database.noteDao().insert(note);

            Intent serviceIntent = new Intent(this, NoteficationForegroundService.class);
            ContextCompat.startForegroundService(this, serviceIntent);
        });

        mainEditText.getText().clear();
    }

    @OnClick(R.id.main_background)
    public void changeColor() {
        colorManager.nextColor();
        Timber.d("Background changed color.");
        mainLayout.setBackgroundResource(colorManager.getCurrentColor());
        getWindow().setStatusBarColor(ContextCompat.getColor(this, colorManager.getCurrentDarkColor()));
    }

    public void checkFirstRun() {
        boolean isFirstRun = getSharedPreferences("Notefication_core", MODE_PRIVATE).getBoolean("isFirstRun", true);
        if (isFirstRun){
            new AlertDialog.Builder(this)
                    .setTitle("Devices from Xiaomi or Huawei.")
                    .setMessage("To make your note-fications really sticky make sure you give Autostart permission to Note-fication. Otherwise note-fications will disappear when you'll swipe this app from recents.")
                    .setIcon(R.drawable.ic_warning_yellow_24dp)
                    .setPositiveButton("Got it", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .show();


            getSharedPreferences("Notefication_core", MODE_PRIVATE)
                    .edit()
                    .putBoolean("isFirstRun", false)
                    .apply();
        }
    }
}
