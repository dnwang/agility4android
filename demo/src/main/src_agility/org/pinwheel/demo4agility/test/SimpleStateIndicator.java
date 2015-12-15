package org.pinwheel.demo4agility.test;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import org.pinwheel.agility.view.ProgressCircular;
import org.pinwheel.agility.view.drag.Draggable;
import org.pinwheel.agility.view.drag.IStateIndicator;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public class SimpleStateIndicator extends FrameLayout implements IStateIndicator {

    private ProgressCircular progressCircular;

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
        progressCircular = new ProgressCircular(getContext());
        progressCircular.setBarColor(Color.GRAY);
        progressCircular.setProgress(0);
        addView(progressCircular);
    }

    protected void onReset(Draggable draggable) {
        setTranslationY(-draggable.getTopHoldDistance());
        progressCircular.setProgress(0);
    }

    protected void onHold(Draggable draggable) {
        progressCircular.spin();
    }

    protected void onMove(Draggable draggable, final float distance, final float offset) {
        final int position = draggable.getPosition();
        final int topHoldDy = draggable.getTopHoldDistance();

        if (position == Draggable.EDGE_TOP) {
            if ((distance * offset > 0) || (distance * offset < 0 && Math.abs(distance + offset) < topHoldDy)) {
                final float currentOffsetY = getTranslationY() + offset;
                setTranslationY(Math.min(currentOffsetY, 0));
            }

            final float percent = 1 - Math.abs(getTranslationY()) / topHoldDy;
            progressCircular.setProgress(Math.min(percent, 1));
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
