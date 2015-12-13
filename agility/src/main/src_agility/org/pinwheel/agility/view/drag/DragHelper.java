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
    private OnDragListener listener;
    private List<IStateIndicator> stateIndicators;
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
    }

    private void setDistance(final float distance) {
        this.distance = distance;
    }

    private void autoMove(float distance, final long duration, AnimatorListenerAdapter adapter) {
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

    public final boolean hasTopHold() {
        return this.topHoldDistance > 0;
    }

    public final boolean hasBottomHold() {
        return this.bottomHoldDistance > 0;
    }

    public final boolean isDragging() {
        final int state = getState();
        return state == STATE_DRAGGING_TOP || state == STATE_DRAGGING_BOTTOM;
    }

    public final boolean isHolding() {
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
    public final void hold(final boolean isTopPosition, final float velocity) {
        float newDy = 0;
        if (isTopPosition && hasTopHold()) {
            newDy = getTopHoldDistance();
        } else if (!isTopPosition && hasBottomHold()) {
            newDy = -getBottomHoldDistance();
        }

        if (newDy != 0) {
            final float offset = newDy - getDistance();
            autoMove(offset, (long) (Math.abs(offset) / velocity), new AnimatorListenerAdapter() {
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

    @Override
    public final void resetToBorder(final float velocity) {
        final float offset = getDistance();
        autoMove(-offset, (long) (Math.abs(offset) / velocity), new AnimatorListenerAdapter() {
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
    public final void inertial(final int distance, final float velocity) {
        autoMove(distance, (long) (Math.abs(distance) / velocity), new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                setState(STATE_INERTIAL);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                resetToBorder(velocity);
            }
        });
    }

    @Override
    public final void move(final float offset) {
        if (mover != null && offset != 0) {
            final float oldDy = getDistance();
            final float newDy = oldDy + offset;
            setDistance(newDy);
            mover.move(newDy);
            if (listener != null) {
                listener.onDragging(this, newDy, offset);
            }
            if (stateIndicators != null && stateIndicators.size() > 0) {
                for (IStateIndicator stateIndicator : stateIndicators) {
                    stateIndicator.onDragging(this, newDy, offset);
                }
            }
        }
    }

    @Override
    public final void stopMove() {
        if (mover != null && animator != null) {
            animator.cancel();
        }
    }

    @Override
    public final void setOnDragListener(OnDragListener listener) {
        this.listener = listener;
    }

    @Override
    public final void setOrientation(final int orientation) {
        this.orientation = orientation == LinearLayout.VERTICAL ? orientation : LinearLayout.HORIZONTAL;
    }

    @Override
    public final int getOrientation() {
        return this.orientation;
    }

    @Override
    public final void setHoldDistance(final int dTop, final int dBottom) {
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
    public final void setState(final int state) {
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
        this.currentState = state;
        if (listener != null) {
            listener.onDragStateChanged(this, currentPosition, currentState);
        }
        if (stateIndicators != null && stateIndicators.size() > 0) {
            for (IStateIndicator stateIndicator : stateIndicators) {
                stateIndicator.onDragStateChanged(this, currentPosition, currentState);
            }
        }
    }

    @Override
    public final int getState() {
        return currentState;
    }

    @Deprecated
    @Override
    public final void setPosition(int position) {
        this.currentPosition = position;
    }

    @Override
    public final int getPosition() {
        return currentPosition;
    }

    @Override
    public final float getDistance() {
        return this.distance;
    }

    @Override
    public final void addStateIndicator(IStateIndicator stateIndicator) {
        if (stateIndicator == null) {
            return;
        }
        if (stateIndicators == null) {
            stateIndicators = new ArrayList<>(2);
        }
        stateIndicators.add(stateIndicator);
    }

    @Override
    public final void removeStateIndicator(IStateIndicator stateIndicator) {
        if (stateIndicator == null || stateIndicators == null) {
            return;
        }
        stateIndicators.remove(stateIndicator);
    }
}
