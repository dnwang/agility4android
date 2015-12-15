package org.pinwheel.demo4agility.test;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import org.pinwheel.agility.view.ProgressCircular;
import org.pinwheel.agility.view.drag.BaseDragIndicator;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public class HeaderDragIndicator extends BaseDragIndicator {

    private ProgressCircular progressCircular;

    public HeaderDragIndicator(Context context) {
        super(context);
        this.init();
    }

    public HeaderDragIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public HeaderDragIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    private void init() {
        progressCircular = new ProgressCircular(getContext());
        progressCircular.setBarColor(Color.GRAY);
        progressCircular.setProgress(0);
        addView(progressCircular);
    }

    @Override
    public void onMove(float distance, float offset) {
        super.onMove(distance, offset);

        if (isHolding()) {
            return;
        }
        final int topHoldDy = getDraggable().getTopHoldDistance();

        final float percent = 1 - Math.abs(getTranslationY()) / topHoldDy;
        progressCircular.setProgress(Math.min(percent, 1));
    }

    @Override
    public void onHold() {
        super.onHold();
        progressCircular.spin();
    }

    @Override
    public void reset() {
        super.reset();
        progressCircular.setProgress(0);
    }

}
