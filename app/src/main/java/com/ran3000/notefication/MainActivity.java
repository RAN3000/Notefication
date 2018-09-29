package com.ran3000.notefication;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.main_layout)
    ConstraintLayout mainLayout;

    private ColorManager colorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        colorManager = new ColorManager();

        // no title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);
    }

    @OnClick(R.id.main_layout)
    public void changeColor() {
        colorManager.nextColor();
        mainLayout.setBackgroundColor(colorManager.getCurrentColor());
    }
}
