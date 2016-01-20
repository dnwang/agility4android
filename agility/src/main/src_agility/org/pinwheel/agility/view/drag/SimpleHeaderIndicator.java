package org.pinwheel.agility.view.drag;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewTreeObserver;

import org.pinwheel.agility.util.UIUtils;
import org.pinwheel.agility.view.SweetProgress;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
class SimpleHeaderIndicator extends BaseDragIndicator {

    private SweetProgress progress;

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
        progress = new SweetProgress(getContext());
        final int edges = UIUtils.dip2px(getContext(), 32);
        LayoutParams params = new LayoutParams(edges, edges);
        params.gravity = Gravity.CENTER;
        final int margin = UIUtils.dip2px(getContext(), 8);
        params.setMargins(0, margin, 0, margin);
        addView(progress, params);

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                moveTo(0.0f);
                getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        progress.setVisibility(visibility);
    }

    @Override
    public void setBackgroundColor(int color) {
        progress.setBackgroundColor(color);
    }

    @Override
    public void onMove(float distance, float offset) {
        final int position = getDraggable().getPosition();
        final int state = getDraggable().getState();
        if (position != Draggable.EDGE_TOP || state == Draggable.STATE_INERTIAL) {
            return;
        }
        final int height = getMeasuredHeight();
        final float percent = Math.min(Math.abs(distance), height) / height;

        moveTo(percent);
    }

    @Override
    public void onHold() {
        super.onHold();
        progress.spin();
    }

    @Override
    public void reset() {
        super.reset();
        moveTo(0.0f);
    }

    private void moveTo(float percent) {
        setTranslationY(-getMeasuredHeight() * (1 - percent));
        if (!isHolding()) {
            progress.setProgress(percent);
        }
    }

}