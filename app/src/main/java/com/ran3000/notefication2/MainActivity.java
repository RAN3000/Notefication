package com.ran3000.notefication2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ran3000.notefication2.data.Note;
import com.ran3000.notefication2.data.NoteDatabase;
import com.ran3000.notefication2.noteslist.NotesListActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.main_layout)
    ConstraintLayout mainLayout;
    @BindView(R.id.main_background)
    Button mainBackground;
    @BindView(R.id.notification_edit_layout)
    ConstraintLayout editLayout;
    @BindView(R.id.notification_edit_edittext)
    EditText mainEditText;
    @BindView(R.id.main_down_button)
    ImageButton mainDownButton;
    @BindView(R.id.main_clear_all)
    ImageView mainClearAllButton;
    @BindView(R.id.main_color_preview)
    ImageView mainColorPreview;
    @BindView(R.id.main_text)
    TextView mainText;
    @BindView(R.id.main_send_sticky)
    Button mainSendSticky;
    @BindView(R.id.main_send_non_sticky)
    Button mainSendNonSticky;

    private ColorManager colorManager;
    private NoteDatabase database;
    private AppExecutors executors;
    private NoteficationManager notificationManager;
    private SharedPreferences sharedPreferences;

    private boolean darkMode = false;

    @SuppressLint("ClickableViewAccessibility")
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
        ViewCompat.setTranslationZ(editLayout, 20);
        ViewCompat.setTranslationZ(mainDownButton, 20);
        ViewCompat.setTranslationZ(mainClearAllButton, 20);
        ViewCompat.setTranslationZ(mainSendSticky, 20);
        ViewCompat.setTranslationZ(mainSendNonSticky, 20);


        // edit text action send
        mainEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendNoteSticky();
                    return true;
                }
                return false;
            }
        });

        mainEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() >= 100) {
                    Toast.makeText(MainActivity.this, "Your note is too long", Toast.LENGTH_SHORT).show();
                }
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
        long noteTappedId = getIntent().getLongExtra("note_id", -1);
        if (noteTappedId != -1) {
            executors.diskIO().execute(() -> {
               Note tappedNote = database.noteDao().getById(noteTappedId);

               executors.mainThread().execute(() -> {
                   ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                   ClipData clip = ClipData.newPlainText("Notefication tapped note", tappedNote.getText());
                   clipboard.setPrimaryClip(clip);

                   Toast.makeText(MainActivity.this, "Copied to clipboard.", Toast.LENGTH_SHORT).show();
               });
            });
        }

        sharedPreferences = this.getSharedPreferences(
                "notefications_user_settings", Context.MODE_PRIVATE);
        darkMode = sharedPreferences.getBoolean("dark_mode", false);
        if (darkMode) {
            setDarkMode(true);
        }
    }

    @OnClick(R.id.main_send_sticky)
    public void sendNoteSticky() {
        Timber.d("Note sent: %s", mainEditText.getText());

        if (mainEditText.getText().toString().equalsIgnoreCase("dark")) {
            setDarkMode(!darkMode);
        }

        Note note = new Note();
        note.setText(mainEditText.getText().toString());
        note.setColor(colorManager.getCurrentColor());

        executors.diskIO().execute(() -> {
            long newId = database.noteDao().insert(note);

            // order gets the id, when you need to switch order of two elements just switch their
            // orderId.
            database.noteDao().updateOrder(newId, newId);

            Intent serviceIntent = new Intent(this, NoteficationForegroundService.class);
            ContextCompat.startForegroundService(this, serviceIntent);
        });

        // assume the user is writing a thread, the user can change color and the thread deleted.
        mainEditText.setText("_");
        mainEditText.setSelection(1);
    }

    @OnClick(R.id.main_background)
    public void changeColor() {
        colorManager.nextColor();
        Timber.d("Background changed color.");
        editLayout.setBackgroundResource(colorManager.getCurrentColor());
        mainColorPreview.setImageResource(ColorManager.getCircleFor(colorManager.getCurrentColor()));
        mainSendSticky.setTextColor(getResources().getColor(colorManager.getCurrentColor()));
        mainSendNonSticky.setTextColor(getResources().getColor(colorManager.getCurrentColor()));
        getWindow().setStatusBarColor(ContextCompat.getColor(this, colorManager.getCurrentDarkColor()));

        // change color changes thread
        if (mainEditText.getText().toString().equals("_")) {
            mainEditText.setText("");
        }
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

    @OnClick(R.id.main_down_button)
    public void goToScreenDown() {
        hideSoftKeyboard();
        Intent intent = new Intent(this, NotesListActivity.class);
        intent.putExtra("darkColor", colorManager.getCurrentDarkColor());
        ActivityOptions options = ActivityOptions
                .makeSceneTransitionAnimation(this, mainDownButton, "downMenu");
        startActivity(intent);
    }

    @OnClick(R.id.main_clear_all)
    public void promptClearAllNotifications() {
        new AlertDialog.Builder(this)
                .setTitle("Clear all")
                .setMessage("Are you sure you want to clear all the note-fications?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    // Continue with delete operation
                    executors.diskIO().execute( () -> {
                        database.noteDao().deleteAll();

                        NoteficationManager manager = new NoteficationManager(MainActivity.this);
                        manager.deleteAllNotifications();

                        executors.mainThread().execute(() -> {
                            // no notefications -> stop service
                            Intent serviceIntent = new Intent(MainActivity.this, NoteficationForegroundService.class);
                            MainActivity.this.stopService(serviceIntent);
                        });
                        }
                    );
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(R.drawable.ic_clear_all_black_24dp)
                .show();
    }

    // from https://stackoverflow.com/a/17789187
    private void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = this.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        mainEditText.clearFocus();
    }


    private void setDarkMode(boolean on) {
        this.darkMode = on;
        if (on) {
            mainLayout.setBackgroundResource(android.R.color.black);
            mainClearAllButton.setImageResource(R.drawable.ic_clear_all_white_24dp);
            mainDownButton.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
            mainDownButton.setBackgroundResource(android.R.color.white);
            mainText.setTextColor(getResources().getColor(R.color.transparent_white));
        } else { // reset normal mode
            mainLayout.setBackgroundResource(android.R.color.white);
            mainClearAllButton.setImageResource(R.drawable.ic_clear_all_black_24dp);
            mainDownButton.setImageResource(R.drawable.ic_keyboard_arrow_down_white_24dp);
            mainDownButton.setBackgroundResource(android.R.color.black);
            mainText.setTextColor(getResources().getColor(R.color.transparent_black));
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("dark_mode", on);
        editor.apply();
    }
}
