package com.ran3000.notefication;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ColorManager {

    public static final int GREEN = R.color.green;
    public static final int RED = R.color.red;
    public static final int BLUE = R.color.blue;
    public static final int ORANGE = R.color.orange;
    public static final int PURPLE = R.color.purple;

    @IntDef({GREEN, RED, BLUE, ORANGE, PURPLE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface NoteficationColor {}

    @NoteficationColor
    private int currentColor = GREEN;

    @NoteficationColor
    public int getCurrentColor() {
        return currentColor;
    }

    public void nextColor() {
        switch (currentColor) {
            case GREEN:
                currentColor = RED;
                break;
            case RED:
                currentColor = BLUE;
                break;
            case BLUE:
                currentColor = ORANGE;
                break;
            case ORANGE:
                currentColor = PURPLE;
                break;
            case PURPLE:
                currentColor = GREEN;
                break;
        }
    }

}
