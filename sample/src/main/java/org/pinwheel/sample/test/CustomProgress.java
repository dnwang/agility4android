package org.pinwheel.sample.test;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DrawFilter;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.util.AttributeSet;
import android.view.View;

/**
 * Copyright (C), 2016 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public class CustomProgress extends View {

    private static final int DELAY = 100;

    private static final int[] COLORS = new int[]{Color.RED, Color.WHITE, Color.rgb(23, 132, 215)};

    private static final int ANGLE_INTERVAL = 20;//angle
    private static final float OFFSET = 0.5f;//angle
    private static final int RADIUS = 10;//4dp
    private static final int MARGIN = 14;//4dp
    private static final float SCALE = 0.4f;

    private float current;
    private Paint paint;

    private DrawFilter drawFilter;

    private final Runnable loop = new Runnable() {
        @Override
        public void run() {
            if (getVisibility() == VISIBLE) {
                removeCallbacks(this);
                postDelayed(this, DELAY);
            }
            current += OFFSET;
            if (current > 359) {
                current = 0;
            }
            invalidate();
        }
    };

    public CustomProgress(Context context) {
        super(context);
        init();
    }

    public CustomProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setLayerType(LAYER_TYPE_SOFTWARE, paint);
        drawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        paint = new Paint();
        paint.setShadowLayer(3, 0, 0, Color.BLACK);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        canvasWidth = COLORS.length * (RADIUS * 2 + MARGIN);
        canvasHeight = RADIUS * 2;

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        int mode = MeasureSpec.getMode(widthMeasureSpec);
        if (MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.EXACTLY) {
            width = (int) canvasWidth;
        }
        if (MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.EXACTLY) {
            height = (int) canvasHeight;
        }

        setMeasuredDimension(width + getPaddingLeft() + getPaddingRight(), height + getPaddingTop() + getPaddingBottom());
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (getVisibility() == VISIBLE) {
            spin();
        } else {
            cancel();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (getVisibility() == VISIBLE) {
            spin();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancel();
    }

    public final void spin() {
        removeCallbacks(loop);
        if (getVisibility() == VISIBLE) {
            post(loop);
        }
    }

    public final void cancel() {
        removeCallbacks(loop);
    }

    private float canvasWidth, canvasHeight;

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.setDrawFilter(drawFilter);
        float centerX;
        float centerY = canvasHeight / 2;
        float radius;
        float sinAngle;
        float percent;
        for (int i = 0; i < COLORS.length; i++) {
            paint.setColor(COLORS[i]);
            centerX = RADIUS + MARGIN + i * (MARGIN + 2 * RADIUS);
            sinAngle = (float) (Math.sin(Math.toDegrees(current + i * ANGLE_INTERVAL)));
            percent = SCALE + Math.abs(sinAngle) * (1 - SCALE);
            radius = percent * RADIUS;
            paint.setAlpha((int) (255 * percent));
            canvas.drawCircle(centerX, centerY, radius, paint);
        }
    }

}
