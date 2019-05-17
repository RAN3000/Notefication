package com.ran3000.notefication2;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ColorManager {

    private static final int GREEN = R.color.green;
    private static final int RED = R.color.red;
    private static final int BLUE = R.color.blue;
    private static final int ORANGE = R.color.orange;
    private static final int PURPLE = R.color.purple;

    private static final int DARK_GREEN = R.color.dark_green;
    private static final int DARK_RED = R.color.dark_red;
    private static final int DARK_BLUE = R.color.dark_blue;
    private static final int DARK_ORANGE = R.color.dark_orange;
    private static final int DARK_PURPLE = R.color.dark_purple;

    @IntDef({GREEN, RED, BLUE, ORANGE, PURPLE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface NoteficationColor {}

    @IntDef({DARK_GREEN, DARK_RED, DARK_BLUE, DARK_ORANGE, DARK_PURPLE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface NoteficationDarkColor {}

    @NoteficationColor
    private int currentColor = GREEN;

    @NoteficationColor
    int getCurrentColor() {
        return currentColor;
    }

    @NoteficationDarkColor
    int getCurrentDarkColor() {
        switch (currentColor) {
            case GREEN:
                return DARK_GREEN;
            case RED:
                return DARK_RED;
            case BLUE:
                return DARK_BLUE;
            case ORANGE:
                return DARK_ORANGE;
            case PURPLE:
                return DARK_PURPLE;
            default:
                return DARK_GREEN;
        }
    }

    void nextColor() {
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
            default:
                currentColor = GREEN;
        }
    }

}
