package com.ran3000.notefication2;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

import java.lang.ref.WeakReference;

import timber.log.Timber;

public class NoteDragShadowBuilder extends View.DragShadowBuilder {

    private final WeakReference<View> view;
    private float tapX;
    private float tapY;

    public NoteDragShadowBuilder(View view, float tapX, float tapY) {
        super(view);
        this.view = new WeakReference<View>(view);
        this.tapX = tapX;
        this.tapY = tapY;
    }

    @Override
    public void onDrawShadow(Canvas canvas) {
//        super.onDrawShadow(canvas);

        final View v = view.get();
        if (v != null) {
            drawViewOnCanvas(v, canvas);
        } else {
            Timber.e("Asked to draw drag shadow but no view");
        }
    }

    @Override
    public void onProvideShadowMetrics(Point outShadowSize, Point outShadowTouchPoint) {
        final View v = view.get();
        if (v != null) {
            outShadowSize.set(v.getWidth(), v.getHeight());
            outShadowTouchPoint.set((int) tapX, (int) tapY);
        } else {
            Timber.e("Asked for drag thumb metrics but no view");
        }
    }

    private void drawViewOnCanvas(View view, Canvas canvas) {
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable!=null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
    }
}
