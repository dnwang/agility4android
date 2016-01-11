package org.pinwheel.agility.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import org.pinwheel.agility.util.UIUtils;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public class SweetProgress extends View {

    private static final int INTERVAL = 100;

    @Deprecated
    private float percent;
    private int showPointIndex;

    private float angleOffset;
    private float currentOffset;

    private boolean isSpinMode;

    private Point center;
    private Paint paint;
    private int pointSize;
    private float outerRadius;
    private float innerRadius;

    private float pointWidth;

    private final Runnable loop = new Runnable() {
        @Override
        public void run() {
            if (isSpinMode && isShown()) {
                postDelayed(this, INTERVAL);
            }
            currentOffset += angleOffset;
            if (currentOffset > 359) {
                currentOffset = 0;
            }
            invalidate();
        }
    };

    public SweetProgress(Context context) {
        super(context);
        init();
    }

    public SweetProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        Drawable drawable = getBackground();
        if (drawable != null && drawable instanceof ColorDrawable) {
            setBackgroundColor(((ColorDrawable) drawable).getColor());
        }
        super.setBackgroundDrawable(null);
    }

    public SweetProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        Drawable drawable = getBackground();
        if (drawable != null && drawable instanceof ColorDrawable) {
            setBackgroundColor(((ColorDrawable) drawable).getColor());
        }
        super.setBackgroundDrawable(null);
    }

    private void init() {
        center = new Point();
        paint = new Paint();
        paint.setColor(Color.GRAY);
        isSpinMode = true;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        final int dp12 = UIUtils.dip2px(getContext(), 12);

        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();

        center.set(width / 2, height / 2);
        outerRadius = Math.min(width - (getPaddingLeft() + getPaddingRight()), height - (getPaddingTop() + getPaddingBottom())) / 2.0f - 8;

        if (outerRadius <= dp12) {
            innerRadius = outerRadius / 2.0f;
            pointWidth = 4.5f;
            pointSize = 8;
        } else {
            innerRadius = outerRadius / 2.0f;
            pointWidth = Math.max(4.5f, outerRadius / (dp12 / 2.0f));
            pointSize = 12;
        }

        angleOffset = 360 / pointSize;
    }

    @Override
    public void setBackgroundColor(int color) {
        paint.setColor(color);
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (getVisibility() == VISIBLE && isSpinMode) {
            spin();
        } else {
            cancel();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (getVisibility() == VISIBLE && isSpinMode) {
            spin();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancel();
    }

    public void setProgress(float percent) {
        cancel();
        this.percent = Math.max(0.0f, Math.min(percent, 1.0f));
        int temp = showPointIndex;
        showPointIndex = (int) (this.percent * pointSize);
        if (getVisibility() == VISIBLE && temp != showPointIndex) {
            invalidate();
        }
    }

    public final void spin() {
        removeCallbacks(loop);
        if (getVisibility() == VISIBLE) {
            post(loop);
        }
        isSpinMode = true;
    }

    public final void cancel() {
        removeCallbacks(loop);
        isSpinMode = false;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        float angle;
        for (int i = 0; i < pointSize; i++) {
            if (isSpinMode) {
                angle = i * angleOffset + currentOffset;// must be "+currentOffset"
                paint.setAlpha((int) (255 * (i * 1.0f / (pointSize - 1))));
            } else {
                angle = i * angleOffset;
                paint.setAlpha(i < showPointIndex ? 255 : 0);
            }
            canvas.save();
            canvas.rotate(angle, center.x, center.y);
            RectF rectF = new RectF(center.x - pointWidth / 2, center.y - outerRadius, center.x + pointWidth / 2, center.y - innerRadius);
//            canvas.drawOval(rectF, paint);
            canvas.drawRoundRect(rectF, 5, 5, paint);
            canvas.restore();
        }
    }

}
