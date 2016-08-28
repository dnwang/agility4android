package org.pinwheel.agility.view;

import android.animation.Animator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.OvershootInterpolator;

/**
 * Copyright (C), 2016 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 * @version 2016/6/22,9:59
 * @see
 */
public class DraggableBubbleView extends View {

    public static float degrees(PointF center, PointF p) {
        float dy = p.y - center.y;
        float dx = p.x - center.x;
        float r = (float) Math.sqrt(dy * dy + dx * dx);
        float degrees = (float) Math.toDegrees(Math.acos(dx / r));
        if (dy / r < 0) {
            degrees = 360 - degrees;
        }
        return degrees;
    }

    public static float distance(PointF p0, PointF p1) {
        if (p0 == null || p1 == null) {
            return 0.0f;
        } else {
            float dy = p1.y - p0.y;
            float dx = p1.x - p0.x;
            return (float) Math.sqrt(dy * dy + dx * dx);
        }
    }

    private enum State {
        none, dragging, split
    }

    public DraggableBubbleView(Context context) {
        super(context);
        this.init(null);
    }

    public DraggableBubbleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(attrs);
    }

    public DraggableBubbleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init(attrs);
    }

    private Paint bgPaint;
    private Paint txtPaint;
    private Paint.FontMetricsInt fontMetrics;

    private int originalCenterRadius = 6;// dp
    private int originalPointRadius = 11;// dp
    private int originalTextSize = 12;// dp

    private OnSplitListener listener;

    private void init(AttributeSet attrs) {
        bgPaint = new Paint();
        bgPaint.setAntiAlias(true);
        txtPaint = new Paint();
        txtPaint.setFakeBoldText(true);
        txtPaint.setColor(Color.WHITE);
        txtPaint.setAntiAlias(true);
        txtPaint.setTextAlign(Paint.Align.CENTER);
        originalCenterRadius = dip2px(originalCenterRadius);
        originalPointRadius = dip2px(originalPointRadius);
        originalTextSize = dip2px(originalTextSize);
        maxDistance = dip2px(maxDistance);
        setTextSize(originalTextSize);

        int defaultColor = Color.RED;
        if (null != attrs) {
            Drawable drawable = getBackground();
            if (drawable instanceof ColorDrawable) {
                defaultColor = ((ColorDrawable) drawable).getColor();
            }
        }
        bgPaint.setColor(defaultColor);
    }

    private int count = 0;
    private int maxCount = 99;
    private boolean isShowEllipsis = true;

    public void setMaxCount(int maxCount) {
        maxCount = Math.max(0, maxCount);
        boolean isSame = this.maxCount == maxCount;
        this.maxCount = maxCount;
        if (!isSame) {
            invalidate();
        }
    }

    public int getMaxCount() {
        return maxCount;
    }

    public void setCount(int count) {
        count = Math.max(0, count);
        boolean isSame = this.count == count;
        this.count = count;
        if (!isSame) {
            invalidate();
        }
    }

    public int getCount() {
        return count;
    }

    public void setOnSplitListener(OnSplitListener listener) {
        this.listener = listener;
    }

    public void setTextSize(int size) {
        size = Math.max(0, size);
        txtPaint.setTextSize(size);
        fontMetrics = txtPaint.getFontMetricsInt();
        invalidate();
    }

    public void setTextColor(int color) {
        txtPaint.setColor(color);
        invalidate();
    }

    @Override
    public void setBackgroundColor(int color) {
        bgPaint.setColor(color);
        invalidate();
    }

    public void setOriginalCenterRadius(int radius) {
        this.originalCenterRadius = radius;
        invalidate();
    }

    public void setOriginalPointRadius(int originalPointRadius) {
        this.originalPointRadius = originalPointRadius;
        invalidate();
    }

    private static final String ELLIPSIS = "~~";

    /**
     * 超过最大数值显示"~~"
     */
    public void showEllipsis(boolean is) {
        this.isShowEllipsis = is;
    }

    private void setCenter(float x, float y) {
        center.set(x, y);
        resetParams();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width, height;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = Math.max(originalCenterRadius, originalPointRadius) * 2 + getPaddingLeft() + getPaddingRight();
            if (widthMode == MeasureSpec.AT_MOST) {
                width = Math.min(width, widthSize);
            }
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = Math.max(originalCenterRadius, originalPointRadius) * 2 + getPaddingTop() + getPaddingBottom();
            if (widthMode == MeasureSpec.AT_MOST) {
                height = Math.min(height, heightSize);
            }
        }
        setMeasuredDimension(width, height);
        if (centerInScreen == null) {
            setCenter(width / 2, height / 2);
        } else {
            setCenter(centerInScreen.x, centerInScreen.y);
        }
    }

    private PointF center = new PointF();
    private PointF finger = new PointF();

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isAnimating()) {
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                stopAnimateRelease();
                move2DecorView();
                moveTo(event.getRawX(), event.getRawY());
                break;
            case MotionEvent.ACTION_MOVE:
                moveTo(event.getRawX(), event.getRawY());
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (alreadySplit && currentDistance > maxDistance / 2) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                    restore2Layout();
                    resetParams();
                    if (listener != null) {
                        listener.onSplit();
                    }
                } else {
                    animateRelease(new Runnable() {
                        @Override
                        public void run() {
                            getParent().requestDisallowInterceptTouchEvent(false);
                            restore2Layout();
                            resetParams();
                        }
                    });
                }
                break;
        }
        return true;
    }

    private ValueAnimator releaseAnimator;

    private void stopAnimateRelease() {
        if (isAnimating()) {
            releaseAnimator.cancel();
        }
    }

    private boolean isAnimating() {
        return releaseAnimator != null && releaseAnimator.isStarted() && releaseAnimator.isRunning();
    }

    private void animateRelease(final Runnable afterAnimate) {
        stopAnimateRelease();
        releaseAnimator = ValueAnimator.ofPropertyValuesHolder(
                PropertyValuesHolder.ofFloat("x", finger.x, center.x),
                PropertyValuesHolder.ofFloat("y", finger.y, center.y))
                .setDuration(200);
        releaseAnimator.setInterpolator(new OvershootInterpolator());
        releaseAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                moveTo((Float) animation.getAnimatedValue("x"), (Float) animation.getAnimatedValue("y"));
            }
        });
        releaseAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                releaseAnimator.removeAllUpdateListeners();
                afterAnimate.run();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                releaseAnimator.removeAllUpdateListeners();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        releaseAnimator.start();
    }

    private ViewGroup.LayoutParams oldParams = null;
    private ViewGroup oldParent = null;
    private PointF centerInScreen = null;

    private void move2DecorView() {
        int[] location = new int[2];
        getLocationOnScreen(location);
        centerInScreen = new PointF(center.x + location[0], center.y + location[1]);

        oldParams = getLayoutParams();
        ViewParent parent = getParent();
        if (parent != null) {
            oldParent = (ViewGroup) parent;
            oldParent.removeView(this);
        }
        ViewGroup rootView = (ViewGroup) ((Activity) getContext()).getWindow().getDecorView();
        rootView.addView(this, -1, -1);
    }

    private void restore2Layout() {
        ViewGroup rootView = (ViewGroup) ((Activity) getContext()).getWindow().getDecorView();
        rootView.removeView(this);
        centerInScreen = null;
        if (oldParent != null && oldParams != null) {
            oldParent.addView(this, oldParams);
        }
    }

    private void moveTo(float x, float y) {
        finger.set(x, y);
        currentDistance = distance(center, finger);
        cR = originalCenterRadius - currentDistance * STRENGTH;

        if (currentDistance > maxDistance) {
            state = State.split;
            alreadySplit = true;
        } else {
            state = State.dragging;
        }
        invalidate();
    }

    private void resetParams() {
        moveTo(center.x, center.y);
        path.reset();
        ovalRect.setEmpty();
        cR = originalCenterRadius;
        fR = originalPointRadius;
        state = State.none;
        currentDistance = 0f;
        alreadySplit = false;
    }

    private final static float STRENGTH = 0.04f;// 中心点半径衰减系数
    private int maxDistance = 96;// dp

    private float cR, fR;
    private float currentDistance;
    private State state = State.none;
    private RectF ovalRect = new RectF();
    private Path path = new Path();
    private boolean alreadySplit = false;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        ovalRect.set(finger.x - fR, finger.y - fR, finger.x + fR, finger.y + fR);
        canvas.drawOval(ovalRect, bgPaint);

        if (state == State.dragging && !alreadySplit) {
            path.reset();
            double angle = Math.atan(Math.abs((finger.y - center.y) / (finger.x - center.x)));
            float sin = (float) Math.sin(angle);
            float cos = (float) Math.cos(angle);
            if ((center.y - finger.y) * (center.x - finger.x) < 0) {
                path.moveTo(finger.x - sin * fR, finger.y - cos * fR);
                path.quadTo((center.x + finger.x) / 2, (center.y + finger.y) / 2, center.x - sin * cR, center.y - cos * cR);
                path.lineTo(center.x + sin * cR, center.y + cos * cR);
                path.quadTo((center.x + finger.x) / 2, (center.y + finger.y) / 2, finger.x + sin * fR, finger.y + cos * fR);
            } else {
                path.moveTo(finger.x - sin * fR, finger.y + cos * fR);
                path.quadTo((center.x + finger.x) / 2, (center.y + finger.y) / 2, center.x - sin * cR, center.y + cos * cR);
                path.lineTo(center.x + sin * cR, center.y - cos * cR);
                path.quadTo((center.x + finger.x) / 2, (center.y + finger.y) / 2, finger.x + sin * fR, finger.y - cos * fR);
            }
            path.close();
            canvas.drawPath(path, bgPaint);
            ovalRect.set(center.x - cR, center.y - cR, center.x + cR, center.y + cR);
            canvas.drawOval(ovalRect, bgPaint);
        }

        int baseline = (int) ((finger.y + fR + finger.y - fR - fontMetrics.bottom - fontMetrics.top) / 2);
        canvas.drawText(getText(), finger.x, baseline, txtPaint);
    }

    private String getText() {
        if (isShowEllipsis && count > maxCount) {
            return ELLIPSIS;
        } else {
            return String.valueOf(Math.min(count, maxCount));
        }
    }

    private int dip2px(float dpValue) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5F);
    }

    public interface OnSplitListener {
        void onSplit();
    }

}
