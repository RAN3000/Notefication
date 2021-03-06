package com.ran3000.notefication2;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ColorManager {

    public static final int GREEN = R.color.green;
    public static final int RED = R.color.red;
    public static final int BLUE = R.color.blue;
    public static final int ORANGE = R.color.orange;
    public static final int PURPLE = R.color.purple;

    public static final int DARK_GREEN = R.color.dark_green;
    public static final int DARK_RED = R.color.dark_red;
    public static final int DARK_BLUE = R.color.dark_blue;
    public static final int DARK_ORANGE = R.color.dark_orange;
    public static final int DARK_PURPLE = R.color.dark_purple;


    public static final int GREEN_CIRCLE = R.drawable.shape_color_round_green;
    public static final int RED_CIRCLE = R.drawable.shape_color_round_red;
    public static final int BLUE_CIRCLE = R.drawable.shape_color_round_blue;
    public static final int ORANGE_CIRCLE = R.drawable.shape_color_round_orange;
    public static final int PURPLE_CIRCLE = R.drawable.shape_color_round_purple;

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
        return getDarkColorFor(currentColor);
    }

    public static int getDarkColorFor(int lightColor) {
        switch (lightColor) {
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

    public static int getCircleFor(int lightColor) {
        switch (lightColor) {
            case GREEN:
                return GREEN_CIRCLE;
            case RED:
                return RED_CIRCLE;
            case BLUE:
                return BLUE_CIRCLE;
            case ORANGE:
                return ORANGE_CIRCLE;
            case PURPLE:
                return PURPLE_CIRCLE;
            default:
                return GREEN_CIRCLE;
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
