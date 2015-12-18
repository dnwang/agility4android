package org.pinwheel.agility.view.drag;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.animation.DecelerateInterpolator;

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
class SimpleFooterIndicator extends BaseDragIndicator {

    private static final long DURATION = 500l;

    private ProgressCircular progressCircular;

    public SimpleFooterIndicator(Context context) {
        super(context);
        this.init();
    }

    public SimpleFooterIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public SimpleFooterIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    private void init() {
        progressCircular = new ProgressCircular(getContext());
        progressCircular.setBarColor(Color.GRAY);
        progressCircular.setProgress(0);
        LayoutParams params = new LayoutParams(-2, -2);
        params.gravity = Gravity.CENTER;
        int dp8 = UIUtils.dip2px(getContext(), 8);
        params.setMargins(dp8, dp8, dp8, dp8);
        addView(progressCircular, params);
    }

    @Override
    public void onMove(float distance, float offset) {
        if (isHolding() || getDraggable().getPosition() != Draggable.EDGE_BOTTOM) {
            return;
        }
        final int topHoldDy = getDraggable().getTopHoldDistance();
        final float percent = Math.min(Math.abs(distance), topHoldDy) / topHoldDy;

        setTranslationY(getMeasuredHeight() * (1 - percent));

        progressCircular.setProgress(percent);
        progressCircular.setAlpha(percent);
        progressCircular.setScaleX(percent);
        progressCircular.setScaleY(percent);
    }

    @Override
    public void onHold() {
        super.onHold();
        progressCircular.spin();
    }

    @Override
    public void reset() {
        super.reset();
        ValueAnimator animator = ValueAnimator.ofFloat(1.0f, 0.0f);
        animator.setDuration(DURATION);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float percent = (Float) animation.getAnimatedValue();

                setTranslationY(getMeasuredHeight() * (1 - percent));

                progressCircular.setProgress(percent);
                progressCircular.setAlpha(percent);
                progressCircular.setScaleX(percent);
                progressCircular.setScaleY(percent);
            }
        });
        animator.start();
    }

}
