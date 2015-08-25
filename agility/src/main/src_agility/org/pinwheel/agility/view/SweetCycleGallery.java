package org.pinwheel.agility.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.SystemClock;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public class SweetCycleGallery extends FrameLayout {

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    private static final int TOUCH_SLOP = 10;
    private static final long DURATION = 200l;
    private static final int VIEW_SIZE = 3; // 3,5,7,9

    private BaseAdapter adapter;
    private View[] views = new View[VIEW_SIZE];

    private int viewIndex;

    private ViewDragHelper dragHelper;
    private boolean isMoving = false;
    private boolean isSmoothing = false;
    private Rect snapShot = new Rect();

    private int orientation = HORIZONTAL;
    private float inertial = 0.35f;

    public SweetCycleGallery(Context context) {
        super(context);
        init(context);
    }

    public SweetCycleGallery(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SweetCycleGallery(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        dragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {

            private static final int DIRECTION_VERTICAL = 2;
            private static final int DIRECTION_HORIZONTAL = 1;

            private int direction; // x:-1,1; y:-2,2
            private int xDiff, yDiff;

            @Override
            public boolean tryCaptureView(View view, int i) {
                if (!isMoving) {
                    getMiddleRect(snapShot);
                }
                return !isMoving;
            }

            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                if (Math.abs(direction) == DIRECTION_HORIZONTAL) {
                    int width = snapShot.width();
                    View middleView = views[getMiddleIndex()];
                    if ((middleView.getLeft() < snapShot.left - width * inertial) || (middleView.getRight() > snapShot.right + width * inertial)) {
                        // move out
                        moveAllHorizontalSmooth((width - Math.abs(middleView.getLeft() - snapShot.left)) * direction / DIRECTION_HORIZONTAL);
                    } else {
                        moveAllHorizontalSmooth(Math.abs(middleView.getLeft() - snapShot.left) * direction / -DIRECTION_HORIZONTAL);
                    }
                } else if (Math.abs(direction) == DIRECTION_VERTICAL) {
                    int height = snapShot.height();
                    View middleView = views[getMiddleIndex()];
                    if ((middleView.getTop() < snapShot.top - height * inertial) || (middleView.getBottom() > snapShot.bottom + height * inertial)) {
                        // move out
                        moveAllVerticalSmooth((height - Math.abs(middleView.getTop() - snapShot.top)) * direction / DIRECTION_VERTICAL);
                    } else {
                        moveAllVerticalSmooth(Math.abs(middleView.getTop() - snapShot.top) * direction / -DIRECTION_VERTICAL);
                    }
                } else {
                    isMoving = false;
                }
                direction = 0;
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                xDiff = dx;
                if (orientation == VERTICAL || Math.abs(yDiff) > Math.abs(dx)) {
                    return child.getLeft();
                }
                if (Math.abs(direction) == DIRECTION_HORIZONTAL) {
                    isMoving = true;
                    for (int i = 0; i < views.length; i++) {
                        if (child != views[i]) {
                            views[i].setLeft(views[i].getLeft() + dx);
                            views[i].setRight(views[i].getRight() + dx);
                        }
                    }
                    return left;
                } else {
                    if (direction == 0 && Math.abs(dx) > TOUCH_SLOP) {
                        direction = dx < 0 ? -DIRECTION_HORIZONTAL : DIRECTION_HORIZONTAL;
                    }
                    return child.getLeft();
                }
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                yDiff = dy;
                if (orientation == HORIZONTAL || Math.abs(xDiff) > Math.abs(dy)) {
                    return child.getTop();
                }
                if (Math.abs(direction) == DIRECTION_VERTICAL) {
                    isMoving = true;
                    for (int i = 0; i < views.length; i++) {
                        if (child != views[i]) {
                            views[i].setTop(views[i].getTop() + dy);
                            views[i].setBottom(views[i].getBottom() + dy);
                        }
                    }
                    return top;
                } else {
                    if (direction == 0 && Math.abs(dy) > TOUCH_SLOP) {
                        direction = dy < 0 ? -DIRECTION_VERTICAL : DIRECTION_VERTICAL;
                    }
                    return child.getTop();
                }
            }
        });
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public void setAutoScrollInertial(float inertial) {
        this.inertial = inertial;
    }

    public int getCurrentItem() {
        return viewIndex;
    }

    public void setAdapter(BaseAdapter cycleAdapter) {
        adapter = cycleAdapter;
        for (int i = 0; i < views.length; i++) {
            views[i] = adapter.getView(cycleIndex(i), null, this);// !! convertView = null
            if (views[i].getParent() == null) {
                addView(views[i]);
            }
        }
        viewIndex = getMiddleIndex();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        snapShot.set(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());

        if (orientation == HORIZONTAL) {
            alignHorizontal(snapShot);
        } else if (orientation == VERTICAL) {
            alignVertical(snapShot);
        }
    }

    private PointF lastPoint = new PointF();
    private boolean isSendCancel = false;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        dragHelper.processTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isMoving || isSmoothing) {
                    return true;
                }
                isSendCancel = false;
                lastPoint.set(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                float xDiff = Math.abs(event.getX() - lastPoint.x);
                float yDiff = Math.abs(event.getY() - lastPoint.y);
                if (!isSendCancel && (orientation == VERTICAL ? yDiff > xDiff : xDiff > yDiff)) {
                    final long now = SystemClock.uptimeMillis();
                    event = MotionEvent.obtain(now, now, MotionEvent.ACTION_CANCEL, event.getX(), event.getY(), 0);
                    super.dispatchTouchEvent(event);
                    isSendCancel = true;
                    lastPoint.set(0, 0);
                    return true;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                dragHelper.cancel();
                isSendCancel = false;
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    public void moveLeft() {
        if (isMoving || orientation == VERTICAL) {
            return;
        }
        getMiddleRect(snapShot);
        moveAllHorizontalSmooth(-snapShot.width());
    }

    public void moveRight() {
        if (isMoving || orientation == VERTICAL) {
            return;
        }
        getMiddleRect(snapShot);
        moveAllHorizontalSmooth(snapShot.width());
    }

    public void moveTop() {
        if (isMoving || orientation == HORIZONTAL) {
            return;
        }
        getMiddleRect(snapShot);
        moveAllVerticalSmooth(-snapShot.height());
    }

    public void moveBottom() {
        if (isMoving || orientation == HORIZONTAL) {
            return;
        }
        getMiddleRect(snapShot);
        moveAllVerticalSmooth(snapShot.height());
    }

    public int getMiddleIndex() {
        return views.length / 2;
    }

    private void getMiddleRect(Rect rect) {
        if (isMoving) {
            return;
        }
        View middleView = views[getMiddleIndex()];
        rect.set(middleView.getLeft(), middleView.getTop(), middleView.getRight(), middleView.getBottom());
    }

    @Deprecated
    private void moveAll(int dx, int dy) {
        isMoving = true;
        for (int i = 0; i < views.length; i++) {
            views[i].setLeft(views[i].getLeft() + dx);
            views[i].setTop(views[i].getTop() + dy);
            views[i].setRight(views[i].getRight() + dx);
            views[i].setBottom(views[i].getBottom() + dy);
        }
        isMoving = false;
    }

    private void moveAllVerticalSmooth(final int dy) {
        if (isSmoothing || orientation == HORIZONTAL) {
            return;
        }
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, dy);
        valueAnimator.setDuration(DURATION);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            int lastValue;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (Integer) animation.getAnimatedValue();
                int vary = value - lastValue;
                for (View v : views) {
                    v.setTop(v.getTop() + vary);
                    v.setBottom(v.getBottom() + vary);
                }
                lastValue = value;
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                isMoving = true;
                isSmoothing = false;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isMoving = false;
                isSmoothing = false;
                exchangeIndex(snapShot);
                alignVertical(snapShot);
            }
        });
        valueAnimator.start();
    }

    private void moveAllHorizontalSmooth(int dx) {
        if (isSmoothing || orientation == VERTICAL) {
            return;
        }
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, dx);
        valueAnimator.setDuration(DURATION);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            int lastValue;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (Integer) animation.getAnimatedValue();
                int vary = value - lastValue;
                for (View v : views) {
                    v.setLeft(v.getLeft() + vary);
                    v.setRight(v.getRight() + vary);
                }
                lastValue = value;
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                isMoving = true;
                isSmoothing = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isMoving = false;
                isSmoothing = false;
                exchangeIndex(snapShot);
                alignHorizontal(snapShot);
            }
        });
        valueAnimator.start();
    }

    private void exchangeIndex(Rect middleRect) {
        if (isMoving) {
            return;
        }
        View middleView = views[getMiddleIndex()];
        if (middleView.getLeft() < middleRect.left || middleView.getTop() < middleRect.top) {
            viewIndex = cycleIndex(++viewIndex);//1. reset middle index
            // 0 -> end
            View tmp = views[0];
            for (int i = 0; i < views.length - 1; i++) {
                views[i] = views[i + 1];
            }
            // get new view into the last position
            View convertView = adapter.getView(cycleIndex(viewIndex + views.length / 2), tmp, this);//2.get view
            if (convertView.getParent() == null) {
                addView(convertView);
                removeView(tmp);
            }
            views[views.length - 1] = convertView;
        } else if (middleView.getLeft() > middleRect.left || middleView.getTop() > middleRect.top) {
            viewIndex = cycleIndex(--viewIndex);
            // end -> 0
            View tmp = views[views.length - 1];
            for (int i = views.length - 1; i > 0; i--) {
                views[i] = views[i - 1];
            }
            // get new view into the first position
            View convertView = adapter.getView(cycleIndex(viewIndex - views.length / 2), tmp, this);
            if (convertView.getParent() == null) {
                addView(convertView);
                removeView(tmp);
            }
            views[0] = convertView;
        }
    }

    private int cycleIndex(int dataIndex) {
        int count = adapter.getCount();
        if (dataIndex > count - 1) {
            dataIndex = dataIndex % count;
        } else if (dataIndex < 0) {
            dataIndex = count + dataIndex % count;
        }
        return dataIndex;
    }

    private void alignHorizontal(Rect targetMiddleRect) {
        if (isMoving || orientation == VERTICAL) {
            return;
        }
        int newMiddleIndex = getMiddleIndex();
        View newMiddleView = views[newMiddleIndex];
        newMiddleView.setLeft(targetMiddleRect.left);
        newMiddleView.setTop(targetMiddleRect.top);
        newMiddleView.setRight(targetMiddleRect.right);
        newMiddleView.setBottom(targetMiddleRect.bottom);
        for (int i = newMiddleIndex - 1; i >= 0; i--) {
            int offset_x = (newMiddleIndex - i) * targetMiddleRect.width();
            views[i].setLeft(targetMiddleRect.left - offset_x);
            views[i].setRight(targetMiddleRect.right - offset_x);
            views[i].setTop(targetMiddleRect.top);
            views[i].setBottom(targetMiddleRect.bottom);
        }
        for (int i = newMiddleIndex + 1; i < views.length; i++) {
            int offset_x = (i - newMiddleIndex) * targetMiddleRect.width();
            views[i].setLeft(targetMiddleRect.left + offset_x);
            views[i].setRight(targetMiddleRect.right + offset_x);
            views[i].setTop(targetMiddleRect.top);
            views[i].setBottom(targetMiddleRect.bottom);
        }
    }

    private void alignVertical(Rect targetMiddleRect) {
        if (isMoving || orientation == HORIZONTAL) {
            return;
        }
        int newMiddleIndex = getMiddleIndex();
        View newMiddleView = views[newMiddleIndex];
        newMiddleView.setLeft(targetMiddleRect.left);
        newMiddleView.setTop(targetMiddleRect.top);
        newMiddleView.setRight(targetMiddleRect.right);
        newMiddleView.setBottom(targetMiddleRect.bottom);
        for (int i = newMiddleIndex - 1; i >= 0; i--) {
            int offset_y = (newMiddleIndex - i) * targetMiddleRect.height();
            views[i].setTop(targetMiddleRect.top - offset_y);
            views[i].setBottom(targetMiddleRect.bottom - offset_y);
            views[i].setLeft(targetMiddleRect.left);
            views[i].setRight(targetMiddleRect.right);
        }
        for (int i = newMiddleIndex + 1; i < views.length; i++) {
            int offset_y = (i - newMiddleIndex) * targetMiddleRect.height();
            views[i].setTop(targetMiddleRect.top + offset_y);
            views[i].setBottom(targetMiddleRect.bottom + offset_y);
            views[i].setLeft(targetMiddleRect.left);
            views[i].setRight(targetMiddleRect.right);
        }
    }

}
