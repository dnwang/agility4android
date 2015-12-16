package org.pinwheel.agility.view.drag;

import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

import org.pinwheel.agility.util.UIUtils;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public class DragScrollView extends ScrollView implements Draggable {

    private static final int INERTIA_SLOP = 5;

    private DragHelper dragHelper;

    private final Movable mover = new Movable() {
        @Override
        public void move(float offset) {
            if (getChildCount() == 1) {
                View view = getChildAt(0);
                view.setTranslationY(offset);
            }
        }
    };

    public DragScrollView(Context context) {
        super(context);
        init();
    }

    public DragScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DragScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        this.dragHelper = new DragHelper(mover);
        this.setOverScrollMode(OVER_SCROLL_NEVER);
        this.setMaxInertiaDistance(UIUtils.dip2px(getContext(), 48));
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastPoint.set(event.getRawX(), event.getRawY());
                if (dragHelper.isHolding()) {
//                    resetToBorder();
                    return true;
                } else {
                    return super.dispatchTouchEvent(event);
                }
            default:
                return super.dispatchTouchEvent(event);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int state = getState();
        final float oldDy = getDistance();
        final float absOldDy = Math.abs(oldDy);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return super.onTouchEvent(event);
            case MotionEvent.ACTION_MOVE:
                final float yDiff = event.getRawY() - lastPoint.y;
//                final float xDiff = event.getRawX() - lastPoint.x;
//                final float absYDiff = Math.abs(yDiff);
//                final float absXDiff = Math.abs(xDiff);
                lastPoint.set(event.getRawX(), event.getRawY());
                final boolean isArrivedTop = isArrivedTop();
                final boolean isArrivedBottom = isArrivedBottom();

                if (isArrivedTop || isArrivedBottom) {
                    // BEGIN;顶／底 越界 状态设置
                    if (isArrivedTop && (int) absOldDy == 0) {
                        if (yDiff < 0) {
                            return super.onTouchEvent(event);
                        } else if (yDiff > 0) {
                            setState(STATE_DRAGGING_TOP);
                        }
                    } else if (isArrivedBottom && (int) absOldDy == 0) {
                        if (yDiff > 0) {
                            return super.onTouchEvent(event);
                        } else if (yDiff < 0) {
                            setState(STATE_DRAGGING_BOTTOM);
                        }
                    }
                    // END;顶／底 越界 附加判断

                    float offset;
                    if (oldDy * yDiff < 0) {
                        offset = yDiff;
                        if (Math.abs(yDiff) > absOldDy) {
                            offset = (yDiff > 0 ? absOldDy : -absOldDy);
                        }
                    } else {
                        offset = yDiff / (Math.abs(oldDy) / 100 + getRatio());
                    }

                    final float newDy = oldDy + offset;
                    if (state != STATE_NONE && ((Math.abs(newDy) < 1.0f && absOldDy > 0) || (newDy * oldDy < 0 && absOldDy > 0))) {
                        move(-oldDy);
                        setState(STATE_NONE);
                        return super.onTouchEvent(event);
                    } else {
                        move(offset);
                        return true;
                    }
                } else {
                    return super.onTouchEvent(event);
                }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
                if (absOldDy > 0) {
                    if (isOverHoldPosition()) {
                        switch (state) {
                            case STATE_DRAGGING_TOP:
                                hold(true);
                                break;
                            case STATE_DRAGGING_BOTTOM:
                                hold(false);
                                break;
                            default:
                                resetToBorder();
                                break;
                        }
                    } else {
                        resetToBorder();
                    }
                }
                return super.onTouchEvent(event);
            default:
                return super.onTouchEvent(event);
        }
    }

    private boolean isArrivedTop() {
        if (getChildCount() == 0) {
            return false;
        }
        return getScrollY() == 0;
    }

    private boolean isArrivedBottom() {
        if (getChildCount() == 0) {
            return false;
        }
        return getScrollY() == getChildAt(0).getBottom() + getPaddingBottom() - getMeasuredHeight();
    }

    private final PointF lastPoint = new PointF();

    private int deltaY;
    private boolean isTouchEvent;

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        this.deltaY = deltaY;
        this.isTouchEvent = isTouchEvent;
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        final int maxInertiaDistance = getMaxInertiaDistance();
        if (Math.abs((int) getDistance()) == 0 && clampedY && !isTouchEvent && maxInertiaDistance > 0) {
            deltaY /= getInertiaWeight();
            if (Math.abs(deltaY) > INERTIA_SLOP) {
                deltaY = deltaY < 0 ? Math.max(-maxInertiaDistance, deltaY) : Math.min(deltaY, maxInertiaDistance);
                inertial(-deltaY);
            }
        }
    }

    @Override
    public int getMaxInertiaDistance() {
        return dragHelper.getMaxInertiaDistance();
    }

    @Override
    public void setMaxInertiaDistance(int maxInertiaDistance) {
        dragHelper.setMaxInertiaDistance(maxInertiaDistance);
    }

    @Override
    public float getResetVelocity() {
        return dragHelper.getResetVelocity();
    }

    @Override
    public void setResetVelocity(float resetVelocity) {
        dragHelper.setResetVelocity(resetVelocity);
    }

    @Override
    public float getInertiaVelocity() {
        return dragHelper.getInertiaVelocity();
    }

    @Override
    public void setInertiaVelocity(float inertiaVelocity) {
        dragHelper.setInertiaVelocity(inertiaVelocity);
    }

    @Override
    public float getInertiaWeight() {
        return dragHelper.getInertiaWeight();
    }

    @Override
    public void setInertiaWeight(float inertiaWeight) {
        dragHelper.setInertiaWeight(inertiaWeight);
    }

    @Override
    public float getInertiaResetVelocity() {
        return dragHelper.getInertiaResetVelocity();
    }

    @Override
    public void setInertiaResetVelocity(float inertiaResetVelocity) {
        dragHelper.setInertiaResetVelocity(inertiaResetVelocity);
    }

    @Override
    public void setRatio(int ratio) {
        dragHelper.setRatio(ratio);
    }

    @Override
    public float getRatio() {
        return dragHelper.getRatio();
    }

    @Override
    public boolean isOverHoldPosition() {
        return dragHelper.isOverHoldPosition();
    }

    @Override
    public void hold(boolean isTopPosition) {
        if (isTopPosition) {
            fullScroll(FOCUS_UP);
        } else {
            fullScroll(FOCUS_DOWN);
        }
        dragHelper.hold(isTopPosition);
    }

    @Override
    public void resetToBorder() {
        dragHelper.resetToBorder();
    }

    @Override
    public void inertial(int distance) {
        dragHelper.inertial(distance);
    }

    @Override
    public void move(float offset) {
        dragHelper.move(offset);
    }

    @Override
    public void stopMove() {
        dragHelper.stopMove();
    }

    @Override
    public void addOnDragListener(Draggable.OnDragListener listener) {
        dragHelper.addOnDragListener(listener);
    }

    @Override
    public void removeOnDragListener(Draggable.OnDragListener listener) {
        dragHelper.removeOnDragListener(listener);
    }

    @Override
    public void setOrientation(int orientation) {
        dragHelper.setOrientation(orientation);
    }

    @Override
    public int getOrientation() {
        return dragHelper.getOrientation();
    }

    @Override
    public void setHoldDistance(int dTop, int dBottom) {
        dragHelper.setHoldDistance(dTop, dBottom);
    }

    @Override
    public int getTopHoldDistance() {
        return dragHelper.getTopHoldDistance();
    }

    @Override
    public int getBottomHoldDistance() {
        return dragHelper.getBottomHoldDistance();
    }

    @Override
    public void setState(int state) {
        dragHelper.setState(state);
    }

    @Override
    public int getState() {
        return dragHelper.getState();
    }

    @Deprecated
    @Override
    public void setPosition(int position) {
        dragHelper.setPosition(position);
    }

    @Override
    public int getPosition() {
        return dragHelper.getPosition();
    }

    @Override
    public float getDistance() {
        return dragHelper.getDistance();
    }

}
