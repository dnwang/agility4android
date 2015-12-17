package org.pinwheel.agility.view.drag;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public class DragRefreshWrapper extends FrameLayout implements Draggable.OnDragListener {

    private OnRefreshListener listener;
    private Draggable draggable;
    private BaseDragIndicator headerIndicator;
    private BaseDragIndicator footerIndicator;

    public DragRefreshWrapper(Context context) {
        super(context);
        this.init();
    }

    public DragRefreshWrapper(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public DragRefreshWrapper(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    private void init() {
        footerIndicator = new SimpleFooterIndicator(getContext());
        FrameLayout.LayoutParams footerParams = new FrameLayout.LayoutParams(-1, -2);
        footerParams.gravity = Gravity.BOTTOM;
        addView(footerIndicator, 0, footerParams);// index

        headerIndicator = new SimpleHeaderIndicator(getContext());
        FrameLayout.LayoutParams headerParams = new FrameLayout.LayoutParams(-1, -2);
        headerParams.gravity = Gravity.TOP;
        addView(headerIndicator, 0, headerParams);// index

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                DragRefreshWrapper.this.onGlobalLayout();
                getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    protected void onGlobalLayout() {
        draggable = findDraggable();
        if (draggable == null) {
            throw new IllegalStateException(getClass().getSimpleName() + " must contains draggable view.");
        }
        headerIndicator.bindDraggable(draggable);
        footerIndicator.bindDraggable(draggable);
        draggable.addOnDragListener(this);
        draggable.setHoldDistance(headerIndicator.getMeasuredHeight(), footerIndicator.getMeasuredHeight());
    }

    private Draggable findDraggable() {
        int size = getChildCount();
        for (int i = 0; i < size; i++) {
            View view = getChildAt(i);
            if (view instanceof Draggable) {
                return (Draggable) view;
            }
        }
        return null;
    }

    @Override
    public void onDragStateChanged(Draggable draggable, int position, int state) {
        if (state == Draggable.STATE_HOLD) {
            if (position == Draggable.EDGE_TOP && !headerIndicator.isHolding()) {
                headerIndicator.onHold();
                if (listener != null) {
                    listener.onRefresh();
                }
            } else if (position == Draggable.EDGE_BOTTOM && !footerIndicator.isHolding()) {
                footerIndicator.onHold();
                if (listener != null) {
                    listener.onLoad();
                }
            }
        }
    }

    @Override
    public void onDragging(Draggable draggable, float distance, float offset) {
        if (draggable.getPosition() != Draggable.EDGE_NONE) {
            headerIndicator.onMove(distance, offset);
            footerIndicator.onMove(distance, offset);
        }
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        this.listener = listener;
    }

//    public void setHeaderIndicator(BaseDragIndicator indicator) {
//    }
//    public void setFooterIndicator(BaseDragIndicator indicator) {
//    }

    public void doRefresh() {
        draggable.hold(true);
    }

    public void onRefreshComplete() {
        headerIndicator.reset();
        draggable.resetToBorder();
    }

    public void onLoadComplete() {
        footerIndicator.reset();
        draggable.resetToBorder();
    }

    public interface OnRefreshListener {
        void onRefresh();

        void onLoad();
    }

}
