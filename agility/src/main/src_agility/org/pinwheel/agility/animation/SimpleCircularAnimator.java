package org.pinwheel.agility.animation;

import android.util.Log;
import android.view.View;

import org.pinwheel.agility.view.SweetCircularView;

public class SimpleCircularAnimator extends SweetCircularView.AnimationAdapter {

    private static final String TAG = SimpleCircularAnimator.class.getSimpleName();
    protected static boolean debug = false;

    protected static void log(String log) {
        if (debug) {
            Log.d(TAG, log);
        }
    }

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
            percent = getOffsetPercent(i);
            log("[onScrolled] percent:" + i + " >> " + percent);
            scalePercent = 1 - Math.min(percent, 1 - scale);
            alphaPercent = 1 - Math.min(percent, 1 - alpha);
            View view = getView(i);
            if (null != view) {
                view.setScaleX(scalePercent);
                view.setScaleY(scalePercent);
                view.setAlpha(alphaPercent);
            }
        }
    }

}