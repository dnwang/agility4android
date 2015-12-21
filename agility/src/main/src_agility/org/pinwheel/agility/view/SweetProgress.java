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

    private float percent;
    private float angleOffset;
    private float currentOffset;
    //    private Timer spinTimer;
    private boolean isStarted;

    private Point center;
    private Paint paint;
    private int pointSize;
    private int outerRadius;
    private int innerRadius;

    private int pointWidth;

    private Runnable loop = new Runnable() {
        @Override
        public void run() {
            currentOffset += angleOffset;
            if (currentOffset > 359) {
                currentOffset = 0;
            }
            invalidate();
            if (isShown()) {
                postDelayed(this, INTERVAL);
            }
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
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        final int dp10 = UIUtils.dip2px(getContext(), 10);

        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();

        center.set(width / 2, height / 2);
        outerRadius = Math.min(width - (getPaddingLeft() + getPaddingRight()), height - (getPaddingTop() + getPaddingBottom())) / 2 - 8;

        if (outerRadius <= dp10) {
            innerRadius = outerRadius / 3;
            pointWidth = 2;
            pointSize = 8;
        } else {
            innerRadius = (int) (outerRadius / 2.5);
            pointWidth = Math.max(2, outerRadius / (dp10 / 2));
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
        spin();
    }

    public void setProgress(float percent) {
        if (percent == this.percent) {
            return;
        }
        cancel();
        this.percent = Math.max(0.0f, Math.min(percent, 1.0f));
        invalidate();
    }

    public final void spin() {
//        // state sync
//        post(new Runnable() {
//            @Override
//            public void run() {
//                start();
//            }
//        });

        percent = -1;
        removeCallbacks(loop);
        post(loop);
    }

    public final void cancel() {
//        // state sync
//        post(new Runnable() {
//            @Override
//            public void run() {
//                stop();
//            }
//        });
        removeCallbacks(loop);
    }

//    @Deprecated
//    private void start() {
//        if (spinTimer != null) {
//            return;
//        }
//        percent = -1;
//        spinTimer = new Timer();
//        spinTimer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                currentOffset += angleOffset;
//                if (currentOffset > 359) {
//                    currentOffset = 0;
//                }
//                postInvalidate();
//            }
//        }, 0, INTERVAL);
//    }
//
//    @Deprecated
//    private void stop() {
//        if (spinTimer == null) {
//            return;
//        }
//        spinTimer.cancel();
//        spinTimer = null;
//    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        final int showPointIndex = (int) (percent * pointSize);
        float angle;
        for (int i = 0; i < pointSize; i++) {
            if (percent > 0) {
                angle = i * angleOffset;
                paint.setAlpha(i < showPointIndex ? 255 : 0);
            } else {
                angle = i * angleOffset + currentOffset;// must be "+currentOffset"
                paint.setAlpha((int) (255 * (i * 1.0f / (pointSize - 1))));
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
