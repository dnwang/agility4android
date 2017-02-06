package org.pinwheel.agility.animation;

import android.view.View;
import android.widget.LinearLayout;

import org.pinwheel.agility.view.SweetCircularView;

public class SimpleCircularAnimator extends SweetCircularView.AnimationAdapter {

    private float scale, alpha;
    private int rotation;

    public SimpleCircularAnimator() {
        this.scale = 0.8f;
        this.alpha = 0.8f;
        this.rotation = 0;
    }

    public SimpleCircularAnimator scale(float scale) {
        this.scale = Math.min(1.0f, Math.max(scale, 0));
        return this;
    }

    public SimpleCircularAnimator alpha(float alpha) {
        this.alpha = Math.min(1.0f, Math.max(alpha, 0));
        return this;
    }

    public SimpleCircularAnimator setRotation(int rotation) {
        this.rotation = rotation;
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
        float tmp;
        View view;
        for (int i = 0; i < size; i++) {
            percent = getOffsetPercent(i);
            tmp = Math.min(1.0f, percent);
            scalePercent = 1 - (1 - scale) * tmp;
            alphaPercent = 1 - (1 - alpha) * tmp;
            view = getView(i);
            if (null != view) {
                // 放缩
                view.setScaleX(scalePercent);
                view.setScaleY(scalePercent);
                // 透明度
                view.setAlpha(alphaPercent);
                // 斜度
                if (0 != rotation) {
                    tmp = getOffset(i);
                    if (tmp > 0) {
                        if (LinearLayout.HORIZONTAL == getCircularView().getOrientation()) {
                            view.setRotationY(Math.max(-240, -(rotation * percent)));
                        } else {
                            view.setRotationX(Math.min(60, rotation * percent));
                        }
                    } else if (tmp < 0) {
                        if (LinearLayout.HORIZONTAL == getCircularView().getOrientation()) {
                            view.setRotationY(Math.min(60, rotation * percent));
                        } else {
                            view.setRotationX(Math.max(-240, -(rotation * percent)));
                        }
                    } else {
                        if (LinearLayout.HORIZONTAL == getCircularView().getOrientation()) {
                            view.setRotationY(0);
                        } else {
                            view.setRotationX(0);
                        }
                    }
                }
            }
        }
    }

}