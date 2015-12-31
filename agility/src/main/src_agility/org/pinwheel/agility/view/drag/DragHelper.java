package org.pinwheel.agility.view.drag;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
class DragHelper implements Draggable {

    private int topHoldDistance;
    private int bottomHoldDistance;
    private int orientation;
    private int currentPosition;
    private int currentState;
    private int maxInertiaDistance;
    private float resetVelocity;
    private float inertiaVelocity;
    private float inertiaResetVelocity;
    private float inertiaWeight;
    private float ratio;
    private List<OnDragListener> onDragListeners;
    private Movable mover;

    private float distance;
    private ValueAnimator animator;

    public DragHelper(Movable mover) {
        this.mover = mover;
        this.topHoldDistance = 0;
        this.bottomHoldDistance = 0;
        this.orientation = LinearLayout.VERTICAL;
        this.currentPosition = EDGE_NONE;
        this.currentState = STATE_NONE;
        this.distance = 0;
        this.maxInertiaDistance = 0;
        this.resetVelocity = VELOCITY_FAST;
        this.inertiaVelocity = VELOCITY_FAST;
        this.inertiaResetVelocity = VELOCITY_NORMAL;
        this.inertiaWeight = WIGHT_INERTIA_LOW;
        this.ratio = RATIO_NORMAL;
    }

    private void setDistance(final float distance) {
        this.distance = distance;
    }

    protected void autoMove(float distance, final long duration, AnimatorListenerAdapter adapter) {
        if (mover != null && distance != 0) {
            stopMove();
            animator = ValueAnimator.ofFloat(0, distance);
            animator.setDuration(duration);
            animator.setInterpolator(new DecelerateInterpolator());
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                private float lastValue;

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float currentValue = (Float) animation.getAnimatedValue();
                    move(currentValue - lastValue);
                    lastValue = currentValue;
                }
            });
            animator.addListener(adapter);
            animator.start();
        }
    }

    public boolean hasTopHold() {
        return this.topHoldDistance > 0;
    }

    public boolean hasBottomHold() {
        return this.bottomHoldDistance > 0;
    }

    public boolean isDragging() {
        final int state = getState();
        return state == STATE_DRAGGING_TOP || state == STATE_DRAGGING_BOTTOM;
    }

    public boolean isHolding() {
        final int state = getState();
        return state == STATE_HOLD || state == STATE_RESTING_TO_HOLD;
    }

    @Override
    public boolean isOverHoldPosition() {
        float distance = getDistance();
        if (distance == 0 || (!hasBottomHold() && !hasTopHold())) {
            return false;
        }
        if (distance > 0 && distance > getTopHoldDistance()) {
            return true;
        } else if (distance < 0 && distance < -getBottomHoldDistance()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void hold(final boolean isTopPosition) {
        float newDy = 0;
        if (isTopPosition && hasTopHold()) {
            newDy = getTopHoldDistance();
        } else if (!isTopPosition && hasBottomHold()) {
            newDy = -getBottomHoldDistance();
        }

        if (newDy != 0) {
            final float offset = newDy - getDistance();
            autoMove(offset, (long) (Math.abs(offset) / getResetVelocity()), new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    setState(STATE_RESTING_TO_HOLD);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    setState(STATE_HOLD);
                }
            });
        }
    }

    protected void resetToBorder(final float resetVelocity) {
        final float offset = getDistance();
        autoMove(-offset, (long) (Math.abs(offset) / resetVelocity), new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                setState(STATE_RESTING_TO_BORDER);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setState(STATE_NONE);
            }
        });
    }

    @Override
    public void resetToBorder() {
        resetToBorder(getResetVelocity());
    }

    @Override
    public void inertial(final int distance) {
        autoMove(distance, (long) (Math.abs(distance) / getInertiaVelocity()), new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                setState(STATE_INERTIAL);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                final float offset = getDistance();
                autoMove(-offset, (long) (Math.abs(offset) / inertiaResetVelocity), new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        setState(STATE_NONE);
                    }
                });
            }
        });
    }

    @Override
    public void move(final float offset) {
        if (mover != null && offset != 0) {
            final float oldDy = getDistance();
            final float newDy = oldDy + offset;
            setDistance(newDy);
            mover.move(newDy);
            if (onDragListeners != null && onDragListeners.size() > 0) {
                for (OnDragListener listener : onDragListeners) {
                    listener.onDragging(this, newDy, offset);
                }
            }
        }
    }

    @Override
    public void stopMove() {
        if (mover != null && animator != null) {
            animator.cancel();
        }
    }

    @Override
    public void addOnDragListener(OnDragListener listener) {
        if (listener == null) {
            return;
        }
        if (onDragListeners == null) {
            onDragListeners = new ArrayList<>(3);// header,footer,callback
        }
        onDragListeners.add(listener);
    }

    @Override
    public void removeOnDragListener(OnDragListener listener) {
        if (listener == null) {
            return;
        }
        if (onDragListeners != null) {
            onDragListeners.remove(listener);
        }
    }

    @Override
    public void setOrientation(final int orientation) {
        this.orientation = orientation == LinearLayout.VERTICAL ? orientation : LinearLayout.HORIZONTAL;
    }

    @Override
    public int getOrientation() {
        return this.orientation;
    }

    @Override
    public void setHoldDistance(final int dTop, final int dBottom) {
        this.topHoldDistance = Math.max(0, dTop);
        this.bottomHoldDistance = Math.max(0, dBottom);
    }

    @Override
    public int getTopHoldDistance() {
        return this.topHoldDistance;
    }

    @Override
    public int getBottomHoldDistance() {
        return this.bottomHoldDistance;
    }

    @Override
    public void setState(final int state) {
        switch (state) {
            case STATE_NONE:
                setPosition(EDGE_NONE);
                break;
            case STATE_DRAGGING_TOP:
                setPosition(EDGE_TOP);
                break;
            case STATE_DRAGGING_BOTTOM:
                setPosition(EDGE_BOTTOM);
                break;
        }
        final int oldState = currentState;
        this.currentState = state;
        if (oldState != currentState) {
            if (onDragListeners != null && onDragListeners.size() > 0) {
                for (OnDragListener listener : onDragListeners) {
                    listener.onDragStateChanged(this, currentPosition, currentState);
                }
            }
        }
    }

    @Override
    public int getState() {
        return currentState;
    }

    @Deprecated
    @Override
    public void setPosition(int position) {
        this.currentPosition = position;
    }

    @Override
    public int getPosition() {
        return currentPosition;
    }

    @Override
    public float getDistance() {
        return this.distance;
    }

    @Override
    public int getMaxInertiaDistance() {
        return maxInertiaDistance;
    }

    @Override
    public void setMaxInertiaDistance(int maxInertiaDistance) {
        this.maxInertiaDistance = Math.max(0, maxInertiaDistance);
    }

    @Override
    public float getResetVelocity() {
        return resetVelocity;
    }

    @Override
    public void setResetVelocity(float resetVelocity) {
        this.resetVelocity = Math.max(0, resetVelocity);
    }

    @Override
    public float getInertiaVelocity() {
        return inertiaVelocity;
    }

    @Override
    public void setInertiaVelocity(float inertiaVelocity) {
        this.inertiaVelocity = Math.max(0, inertiaVelocity);
    }

    @Override
    public float getInertiaWeight() {
        return inertiaWeight;
    }

    @Override
    public void setInertiaWeight(float inertiaWeight) {
        this.inertiaWeight = Math.max(0, inertiaWeight);
    }

    @Override
    public float getInertiaResetVelocity() {
        return inertiaResetVelocity;
    }

    @Override
    public void setInertiaResetVelocity(float inertiaResetVelocity) {
        this.inertiaResetVelocity = inertiaResetVelocity;
    }

    @Override
    public void setRatio(int ratio) {
        this.ratio = Math.max(0, ratio);
    }

    @Override
    public float getRatio() {
        return ratio;
    }

    protected static String convertState(int state) {
        switch (state) {
            case Draggable.STATE_NONE:
                return "NONE";
            case Draggable.STATE_HOLD:
                return "HOLD";
            case Draggable.STATE_INERTIAL:
                return "INERTIAL";
            case Draggable.STATE_DRAGGING_TOP:
                return "DRAGGING_TOP";
            case Draggable.STATE_DRAGGING_BOTTOM:
                return "DRAGGING_BOTTOM";
            case Draggable.STATE_RESTING_TO_HOLD:
                return "RESTING_TO_HOLD";
            case Draggable.STATE_RESTING_TO_BORDER:
                return "RESTING_TO_BORDER";
        }
        return "";
    }

    protected static String convertPosition(int position) {
        switch (position) {
            case Draggable.EDGE_NONE:
                return "NONE";
            case Draggable.EDGE_TOP:
                return "TOP";
            case Draggable.EDGE_BOTTOM:
                return "BOTTOM";
        }
        return "";
    }

}
