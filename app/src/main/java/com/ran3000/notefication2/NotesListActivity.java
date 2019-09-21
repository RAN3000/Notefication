package com.ran3000.notefication2;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class NotesListActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int darkColor = getIntent().getIntExtra("darkColor", ColorManager.DARK_GREEN);

        // no title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // clear FLAG_TRANSLUCENT_STATUS flag:
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, darkColor));

        setContentView(R.layout.activity_notes_list);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.notes_list_up_button)
    public void goToScreenUp() {
        onBackPressed();
    }
}
