package org.pinwheel.agility.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;

import org.pinwheel.agility.view.controller.TabController;

import java.util.ArrayList;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
@Deprecated
public final class TabSelectorView extends FrameLayout implements TabController.ISelectable {

    private ArrayList<Rect> rectList;
    private ImageView selector;

    private int restore_index = -1;
    private int currentIndex = -1;

    private Runnable resetRunnable = new Runnable() {
        @Override
        public void run() {
            moveTo(restore_index, false);
            restore_index = -1;
        }
    };

    @Deprecated
    public TabSelectorView(Context context) {
        super(context);
        init(context);
    }

    public TabSelectorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        rectList = new ArrayList<Rect>(0);
        selector = new ImageView(context);
        selector.setBackgroundColor(Color.BLACK);
        addView(selector, -2, -2);

        reset(null);
    }

    @Override
    public int getCurrentRect(Rect copy_rect) {
        if (copy_rect != null) {
            copy_rect.set(rectList.get(currentIndex));
        }
        return currentIndex;
    }

    @Override
    public void reset(Object args) {
        currentIndex = -1;
        selector.setLeft(0);
        selector.setRight(0);
        selector.setTop(0);
        selector.setBottom(0);

        removeCallbacks(resetRunnable);
        postDelayed(resetRunnable, 100);
        // END
    }

    @Override
    public void addRect(int index, Rect rect) {
        if (index < 0 || index >= rectList.size()) {
            return;
        }

        rectList.add(index, rect);
        if (index <= currentIndex) {
            currentIndex++;
//            restore_index = currentIndex;
//            reset(null);
        }
    }

    @Override
    public void replaceAll(ArrayList<Rect> rectList) {
        removeAll();
        this.rectList.addAll(rectList);
    }

    @Override
    public void remove(int index) {
        if (index < 0) {
            return;
        }

        rectList.remove(index);
        if (index == currentIndex) {
            currentIndex = -1;
//            reset(null);
        } else if (index < currentIndex) {
            currentIndex--;
//            restore_index = currentIndex;
//            reset(null);
        }
    }

    @Override
    public void removeAll() {
        rectList.clear();
        reset(null);
    }

    @Override
    public void setSelector(Drawable src) {
        selector.setBackgroundDrawable(src);
    }

    @Override
    public void moveTo(int from_index, int to_index, float positionOffset) {
        // TODO denan.wang; 2015/2/14;
    }

    private AnimatorSet animatorSet = null;
    private Rect tempRect = new Rect();

    @Override
    public void moveTo(int to_index, boolean smoothScroll) {
        if (to_index < 0 || to_index >= rectList.size() || to_index == currentIndex) {
            return;
        }
        stopMove();

        tempRect.set(
                selector.getLeft(),
                selector.getTop(),
                selector.getRight(),
                selector.getBottom());

        Rect toRect = rectList.get(to_index);

        animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofInt(selector, "left", tempRect.left, toRect.left),
                ObjectAnimator.ofInt(selector, "top", tempRect.top, toRect.top),
                ObjectAnimator.ofInt(selector, "right", tempRect.right, toRect.right),
                ObjectAnimator.ofInt(selector, "bottom", tempRect.bottom, toRect.bottom));
        animatorSet.setDuration((selector.getVisibility() == VISIBLE && smoothScroll) ? duration : 0);
        animatorSet.start();

        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
//                if (selector.getVisibility() == INVISIBLE) {
//                    selector.setVisibility(VISIBLE);
//                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        currentIndex = to_index;
    }

    private void stopMove() {
        if (animatorSet != null) {
            animatorSet.cancel();
            animatorSet = null;
        }
    }

    private long duration = 200l;

    @Override
    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable parcelable = super.onSaveInstanceState();
        Bundle bundle = new Bundle();
        bundle.putParcelable("super_state", parcelable);
        bundle.putInt("current_index", currentIndex);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle bundle = (Bundle) state;
        restore_index = bundle.getInt("current_index", 0);
        state = bundle.getParcelable("super_state");
        super.onRestoreInstanceState(state);
    }

}
