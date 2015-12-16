package org.pinwheel.agility.view.drag;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public abstract class BaseDragIndicator extends FrameLayout implements Indicator {

    private Draggable draggable;
    private int state;

    public BaseDragIndicator(Context context) {
        super(context);
        this.init();
    }

    public BaseDragIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public BaseDragIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    public final void bindDraggable(Draggable draggable) {
        this.draggable = draggable;
        this.state = Draggable.STATE_NONE;
    }

    public final Draggable getDraggable() {
        return draggable;
    }

    private void init() {

    }

    @Override
    public void onHold() {
        setState(Draggable.STATE_HOLD);
    }

    @Override
    public void reset() {
        setState(Draggable.STATE_NONE);
    }

    @Override
    public final int getState() {
        return state;
    }

    @Override
    public void setState(int state) {
        this.state = state;
    }

    protected final boolean isHolding() {
        return getState() == Draggable.STATE_HOLD;
    }

    protected final boolean isNone() {
        return getState() == Draggable.STATE_NONE;
    }

    protected final int compareHoldDistance(boolean isCompareTop, float target) {
        if (draggable == null) {
            return -1;
        }
        final int holdDistance = isCompareTop ? draggable.getTopHoldDistance() : draggable.getBottomHoldDistance();
        final float absTarget = Math.abs(target);
        return absTarget >= holdDistance ? 1 : -1;
    }

}
