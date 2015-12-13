package org.pinwheel.demo4agility.test;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import org.pinwheel.agility.view.drag.Draggable;
import org.pinwheel.agility.view.drag.IStateIndicator;
import org.pinwheel.demo4agility.R;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public class SimpleStateIndicator extends ImageView implements IStateIndicator {

    public SimpleStateIndicator(Context context) {
        super(context);
        this.init();
    }

    public SimpleStateIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public SimpleStateIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    private void init() {
        setBackgroundDrawable(new ColorDrawable());
    }

    protected void onReset(Draggable draggable) {
        setImageResource(R.drawable.swipe_ic_arrow_down);
    }

    protected void onHold(Draggable draggable) {
        setImageResource(R.drawable.swipe_ic_loading);
    }

    protected void onMove(Draggable draggable, final float distance, final float offset) {
        final int position = draggable.getPosition();
        if (position == Draggable.EDGE_TOP) {
            if (distance > draggable.getTopHoldDistance()) {
                setRotation(180);
            } else {
                setRotation(0);
            }
        } else if (position == Draggable.EDGE_BOTTOM) {

        }
    }

    @Override
    public void onDragStateChanged(Draggable draggable, final int position, final int state) {
        if (state == Draggable.STATE_NONE || state == Draggable.STATE_INERTIAL) {
            setVisibility(INVISIBLE);
            if (state == Draggable.STATE_NONE) {
                onReset(draggable);
            }
        } else {
            if (state == Draggable.STATE_DRAGGING_TOP || state == Draggable.STATE_DRAGGING_BOTTOM) {
                setVisibility(VISIBLE);
            }
            if (state == Draggable.STATE_HOLD) {
                onHold(draggable);
            }
        }
    }

    @Override
    public void onDragging(Draggable draggable, float distance, float offset) {
        if (getVisibility() == VISIBLE) {
            onMove(draggable, distance, offset);
        }
    }

}
