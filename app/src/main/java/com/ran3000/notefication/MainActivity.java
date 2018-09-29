package com.ran3000.notefication;

import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.main_layout)
    ConstraintLayout mainLayout;
    @BindView(R.id.main_background)
    Button mainBackground;

    private ColorManager colorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        colorManager = new ColorManager();

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
    }

    @OnClick(R.id.main_background)
    public void changeColor() {
        colorManager.nextColor();
        Timber.d("Background changed color.");
        mainLayout.setBackgroundResource(colorManager.getCurrentColor());
        getWindow().setStatusBarColor(ContextCompat.getColor(this, colorManager.getCurrentDarkColor()));
    }
}
