package org.pinwheel.agility.view.drag;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;

import org.pinwheel.agility.util.UIUtils;
import org.pinwheel.agility.view.ProgressCircular;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
class SimpleHeaderIndicator extends BaseDragIndicator {

    private ProgressCircular progressCircular;

    public SimpleHeaderIndicator(Context context) {
        super(context);
        this.init();
    }

    public SimpleHeaderIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public SimpleHeaderIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    private void init() {
        progressCircular = new ProgressCircular(getContext());
        progressCircular.setBarColor(Color.GRAY);
        progressCircular.setProgress(0);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(-2, -2);
        params.gravity = Gravity.CENTER;
        int dp8 = UIUtils.dip2px(getContext(), 8);
        params.setMargins(dp8, dp8, dp8, dp8);
        addView(progressCircular, params);
    }

    @Override
    public void onMove(float distance, float offset) {
        if (isHolding() || getDraggable().getPosition() != Draggable.EDGE_TOP) {
            return;
        }
        final int topHoldDy = getDraggable().getTopHoldDistance();
        final float percent = Math.min(Math.abs(distance), topHoldDy) / topHoldDy;
        progressCircular.setProgress(percent);
        progressCircular.setAlpha(percent);
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
        progressCircular.setAlpha(0);
    }

}
