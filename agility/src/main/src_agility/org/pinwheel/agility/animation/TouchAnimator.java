package org.pinwheel.agility.animation;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.MotionEvent;
import android.view.View;
import org.pinwheel.agility.util.UIUtils;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public final class TouchAnimator {

    private static final long ANIM_DURATION = 200l;
    private int zoom_offset = 4;
    private float current_scaleX = 1.0f;
    private float current_scaleY = 1.0f;

    private AnimatorSet down_anim;
    private AnimatorSet up_anim;

    private Animator.AnimatorListener downAnimatorListener;
    private Animator.AnimatorListener upAnimatorListener;

    private View animObject;

    public TouchAnimator(View view) {
        zoom_offset = UIUtils.dip2px(view.getContext(), zoom_offset);
        down_anim = new AnimatorSet();
        up_anim = new AnimatorSet();
        animObject = view;
    }

    public void onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startDownAnim();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
                startUpAnim();
                break;
        }
    }

    public void setOnDownAnimatorListener(Animator.AnimatorListener downAnimatorListener) {
        this.downAnimatorListener = downAnimatorListener;
    }

    public void setOnUpAnimatorListener(Animator.AnimatorListener upAnimatorListener) {
        this.upAnimatorListener = upAnimatorListener;
    }

    private void startDownAnim() {
        if (animObject == null) {
            return;
        }

        cancelAll();

        int width = animObject.getWidth();
        int height = animObject.getHeight();
        float to_scaleX = (width - zoom_offset * 2.0f) / width;
        float to_scaleY = (height - zoom_offset * 2.0f) / height;

        ObjectAnimator anim_scaleX = ObjectAnimator.ofFloat(animObject, "scaleX", current_scaleX, to_scaleX);
        anim_scaleX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                current_scaleX = (Float) animation.getAnimatedValue();
            }
        });
        ObjectAnimator anim_scaleY = ObjectAnimator.ofFloat(animObject, "scaleY", current_scaleY, to_scaleY);
        anim_scaleY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                current_scaleY = (Float) animation.getAnimatedValue();
            }
        });

        down_anim.playTogether(anim_scaleX, anim_scaleY);
        down_anim.setDuration(ANIM_DURATION);
        if (downAnimatorListener != null) {
            down_anim.addListener(downAnimatorListener);
        } else {
            down_anim.removeAllListeners();
        }
        down_anim.start();
    }

    private void startUpAnim() {
        if (animObject == null) {
            return;
        }

        cancelAll();

        ObjectAnimator anim_scaleX = ObjectAnimator.ofFloat(animObject, "scaleX", current_scaleX, 1.0f);
        anim_scaleX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                current_scaleX = (Float) animation.getAnimatedValue();
            }
        });
        ObjectAnimator anim_scaleY = ObjectAnimator.ofFloat(animObject, "scaleY", current_scaleY, 1.0f);
        anim_scaleY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                current_scaleY = (Float) animation.getAnimatedValue();
            }
        });

        up_anim.playTogether(anim_scaleX, anim_scaleY);
        up_anim.setDuration(ANIM_DURATION);
        if (upAnimatorListener != null) {
            up_anim.addListener(upAnimatorListener);
        } else {
            up_anim.removeAllListeners();
        }
        up_anim.start();
    }

    private void cancelAll() {
        if (up_anim != null && up_anim.isRunning()) {
            up_anim.cancel();
        }
        if (down_anim != null && down_anim.isRunning()) {
            down_anim.cancel();
        }
    }

}
