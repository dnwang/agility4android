package org.pinwheel.agility.animation;

import android.view.View;

import org.pinwheel.agility.view.SweetCircularView;

public class SimpleCircularAnimator extends SweetCircularView.AnimationAdapter {

    private static final String TAG = SimpleCircularAnimator.class.getSimpleName();

    private float scale = 0.8f;
    private float alpha = 0.8f;

    public SimpleCircularAnimator() {
        this.scale = 0.8f;
        this.alpha = 0.8f;
    }

    public SimpleCircularAnimator scale(float scale) {
        this.scale = Math.min(1.0f, Math.max(scale, 0));
        return this;
    }

    public SimpleCircularAnimator alpha(float alpha) {
        this.alpha = Math.min(1.0f, Math.max(alpha, 0));
        return this;
    }

    @Override
    protected void onScrolled(int offset) {
        final int size = getSize();
        View center = getView(0);
        final int width = center.getMeasuredWidth();
        final int maxOffset = getCircularView().getMeasuredWidth() / 2;

        for (int i = 0; i < size; i++) {
            int centerOffset = getOffset(i);
            float percent = Math.abs(centerOffset) / maxOffset;
            View view = getView(i);
            if (null != view) {
                view.setScaleX(1 - percent);
                view.setScaleY(1 - percent);
            }
        }

    }

}