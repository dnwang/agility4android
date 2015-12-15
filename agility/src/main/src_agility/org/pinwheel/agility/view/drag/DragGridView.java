package org.pinwheel.agility.view.drag;

import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

import org.pinwheel.agility.util.UIUtils;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public class DragGridView extends GridView implements Draggable {

    private static final int INERTIA_SLOP = 5;

    private int maxInertiaDistance;
    private float resetVelocity;
    private float inertiaVelocity;
    private float inertiaResetVelocity;
    private float inertiaWeight;
    private float ratio;

    private DragHelper dragHelper;

    private final Movable mover = new Movable() {
        @Override
        public void move(float offset) {
            DragGridView.this.setTranslationY(offset);
        }
    };

    public DragGridView(Context context) {
        super(context);
        init();
    }

    public DragGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DragGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        this.maxInertiaDistance = UIUtils.dip2px(getContext(), 48);
        this.resetVelocity = VELOCITY_FAST;
        this.inertiaVelocity = VELOCITY_FAST;
        this.inertiaResetVelocity = VELOCITY_NORMAL;
        this.inertiaWeight = WIGHT_INERTIA_LOW;
        this.ratio = RATIO_NORMAL;
        this.dragHelper = new DragHelper(mover);
        this.setOverScrollMode(OVER_SCROLL_NEVER);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastPoint.set(event.getRawX(), event.getRawY());
                if (dragHelper.isHolding()) {
                    resetToBorder(resetVelocity);
                    return false;
                } else {
                    super.dispatchTouchEvent(event);
                    return true;
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
                final boolean downSuperState = super.onTouchEvent(event);
                isTouchDragging = false;
                return downSuperState;
            case MotionEvent.ACTION_MOVE:
                final float yDiff = event.getRawY() - lastPoint.y;
//                final float xDiff = event.getRawX() - lastPoint.x;
//                final float absYDiff = Math.abs(yDiff);
//                final float absXDiff = Math.abs(xDiff);
                lastPoint.set(event.getRawX(), event.getRawY());

                if (isTouchDragging) {
                    float offset;
                    if (oldDy * yDiff < 0) {
                        offset = yDiff;
                        if (Math.abs(yDiff) > absOldDy) {
                            offset = (yDiff > 0 ? absOldDy : -absOldDy);
                        }
                    } else {
                        offset = yDiff / (Math.abs(oldDy) / 100 + ratio);
                    }

                    final float newDy = oldDy + offset;
                    if (state != STATE_NONE && ((Math.abs(newDy) < 1.0f && absOldDy > 0) || (newDy * oldDy < 0 && absOldDy > 0))) {
                        move(-oldDy);
                        setState(STATE_NONE);
                        final boolean moveSuperState = super.onTouchEvent(event);
                        isTouchDragging = false;
                        return moveSuperState;
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
                if (isTouchDragging && absOldDy > 0) {
                    if (isOverHoldPosition()) {
                        switch (state) {
                            case STATE_DRAGGING_TOP:
                                hold(true, resetVelocity);
                                break;
                            case STATE_DRAGGING_BOTTOM:
                                hold(false, resetVelocity);
                                break;
                            default:
                                resetToBorder(resetVelocity);
                                break;
                        }
                    } else {
                        resetToBorder(resetVelocity);
                    }
                }
                final boolean cancelSuperState = super.onTouchEvent(event);
                isTouchDragging = false;
                return cancelSuperState;
            default:
                return super.onTouchEvent(event);
        }
    }

    private final PointF lastPoint = new PointF();
    private boolean isTouchDragging;

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        if (isTouchDragging || Math.abs((int) getDistance()) > 0) {
            return false;
        }
        int absDeltaY = Math.abs(deltaY);
        if (absDeltaY <= 0) {
            return false;
        }
        isTouchDragging = isTouchEvent;
        if (STATE_NONE == getState()) {
            setState(deltaY > 0 ? STATE_DRAGGING_BOTTOM : STATE_DRAGGING_TOP);
        }
        if (isTouchEvent) {
        } else if (maxInertiaDistance > 0) {
            deltaY /= inertiaWeight;
            if (Math.abs(deltaY) > INERTIA_SLOP) {
                deltaY = deltaY < 0 ? Math.max(-maxInertiaDistance, deltaY) : Math.min(deltaY, maxInertiaDistance);
                inertial(-deltaY, inertiaVelocity, inertiaResetVelocity);
            }
        }
        return false;
    }

    public int getMaxInertiaDistance() {
        return maxInertiaDistance;
    }

    public void setMaxInertiaDistance(int maxInertiaDistance) {
        this.maxInertiaDistance = Math.max(0, maxInertiaDistance);
    }

    public float getResetVelocity() {
        return resetVelocity;
    }

    public void setResetVelocity(float resetVelocity) {
        this.resetVelocity = Math.max(0, resetVelocity);
    }

    public float getInertiaVelocity() {
        return inertiaVelocity;
    }

    public void setInertiaVelocity(float inertiaVelocity) {
        this.inertiaVelocity = Math.max(0, inertiaVelocity);
    }

    public float getInertiaWeight() {
        return inertiaWeight;
    }

    public void setInertiaWeight(float inertiaWeight) {
        this.inertiaWeight = Math.max(0, inertiaWeight);
    }

    public void setRatio(int ratio) {
        this.ratio = Math.max(0, ratio);
    }

    public float getRatio() {
        return ratio;
    }

    @Override
    public boolean isOverHoldPosition() {
        return dragHelper.isOverHoldPosition();
    }

    @Override
    public void hold(boolean isTopPosition, float velocity) {
        dragHelper.hold(isTopPosition, velocity);
    }

    @Override
    public void resetToBorder(float velocity) {
        dragHelper.resetToBorder(velocity);
    }

    @Override
    public void inertial(int distance, float inertialVelocity, float resetVelocity) {
        dragHelper.inertial(distance, inertialVelocity, resetVelocity);
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
    public void setOnDragListener(Draggable.OnDragListener listener) {
        dragHelper.setOnDragListener(listener);
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

    @Override
    public void addIndicator(Indicator indicator) {
        dragHelper.addIndicator(indicator);
    }

}
