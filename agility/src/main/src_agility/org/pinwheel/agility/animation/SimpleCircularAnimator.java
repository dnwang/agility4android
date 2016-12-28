package org.pinwheel.agility.animation;

import android.view.View;

import org.pinwheel.agility.view.SweetCircularView;

public class SimpleCircularAnimator extends SweetCircularView.AnimationAdapter {

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
    protected void onLayout(boolean changed) {
        // 重置默认效果
        onScrolled(0);
    }

    @Override
    protected void onScrolled(final int offset) {
        final int size = getSize();
        float percent, scalePercent, alphaPercent;
        for (int i = 0; i < size; i++) {
            percent = Math.min(1, getOffsetPercent(i));
            scalePercent = 1 - (1 - scale) * percent;
            alphaPercent = 1 - (1 - alpha) * percent;
            View view = getView(i);
            if (null != view) {
                view.setScaleX(scalePercent);
                view.setScaleY(scalePercent);
                view.setAlpha(alphaPercent);
            }
        }
    }

}