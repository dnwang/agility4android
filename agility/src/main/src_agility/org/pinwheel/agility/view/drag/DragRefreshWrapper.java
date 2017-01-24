package org.pinwheel.agility.view.drag;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
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
        resetFooterIndicator(new SimpleFooterIndicator(getContext()));
        resetHeaderIndicator(new SimpleHeaderIndicator(getContext()));
    }

    private void initDraggable() {
        Draggable tmp = findDraggable(this);
        if (tmp != draggable) {
            draggable = tmp;
            if (draggable == null) {
                throw new IllegalStateException(getClass().getSimpleName() + " must contains draggable view.");
            }
            draggable.addOnDragListener(this);
            headerIndicator.bindDraggable(draggable);
            footerIndicator.bindDraggable(draggable);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        initDraggable();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (draggable != null) {
            int topHold = headerIndicator.getVisibility() == VISIBLE ? headerIndicator.getMeasuredHeight() : 0;
            int bottomHold = footerIndicator.getVisibility() == VISIBLE ? footerIndicator.getMeasuredHeight() : 0;
            draggable.setHoldDistance(topHold, bottomHold);
        }
    }

    private void resetHeaderIndicator(BaseDragIndicator indicator) {
        if (indicator == null) {
            return;
        }
        if (headerIndicator != null) {
            headerIndicator.bindDraggable(null);
            removeView(headerIndicator);
        }
        headerIndicator = indicator;
        FrameLayout.LayoutParams headerParams = new FrameLayout.LayoutParams(-1, -2);
        headerParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        addView(headerIndicator, 0, headerParams);
    }

    private void resetFooterIndicator(BaseDragIndicator indicator) {
        if (indicator == null) {
            throw new IllegalStateException("Can not set empty indicator.");
        }
        if (footerIndicator != null) {
            removeView(footerIndicator);
        }
        footerIndicator = indicator;
        FrameLayout.LayoutParams footerParams = new FrameLayout.LayoutParams(-1, -2);
        footerParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        addView(footerIndicator, 0, footerParams);
    }

    private Draggable findDraggable(View v) {
        if (null == v) {
            return null;
        }
        if (v instanceof Draggable) {
            return (Draggable) v;
        } else if (v instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) v;
            final int size = group.getChildCount();
            for (int i = 0; i < size; i++) {
                Draggable draggable = findDraggable(group.getChildAt(i));
                if (null != draggable) {
                    return draggable;
                }
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
                    listener.onTopRefresh();
                }
            } else if (position == Draggable.EDGE_BOTTOM && !footerIndicator.isHolding()) {
                footerIndicator.onHold();
                if (listener != null) {
                    listener.onBottomLoad();
                }
            }
        }
    }

    @Override
    public void onDragging(Draggable draggable, float distance, float offset) {
//        final int position = draggable.getPosition();
        headerIndicator.onMove(distance, offset);
        footerIndicator.onMove(distance, offset);
    }

    public DragRefreshWrapper setOnRefreshListener(OnRefreshListener listener) {
        this.listener = listener;
        return this;
    }

    public DragRefreshWrapper setHeaderIndicator(BaseDragIndicator indicator) {
        resetHeaderIndicator(indicator);
        headerIndicator.bindDraggable(draggable);
        return this;
    }

    public BaseDragIndicator getHeaderIndicator() {
        return headerIndicator;
    }

    public DragRefreshWrapper setFooterIndicator(BaseDragIndicator indicator) {
        resetFooterIndicator(indicator);
        footerIndicator.bindDraggable(draggable);
        return this;
    }

    public BaseDragIndicator getFooterIndicator() {
        return footerIndicator;
    }

    public DragRefreshWrapper setHeaderVisibility(boolean isVisible) {
        headerIndicator.setVisibility(isVisible ? VISIBLE : INVISIBLE);
        if (draggable != null) {
            draggable.setHoldDistance(isVisible ? headerIndicator.getMeasuredHeight() : 0, draggable.getBottomHoldDistance());
        }
        return this;
    }

    public DragRefreshWrapper setFooterVisibility(boolean isVisible) {
        footerIndicator.setVisibility(isVisible ? VISIBLE : INVISIBLE);
        if (draggable != null) {
            draggable.setHoldDistance(draggable.getTopHoldDistance(), isVisible ? footerIndicator.getMeasuredHeight() : 0);
        }
        return this;
    }

    public DragRefreshWrapper doRefresh() {
        if (draggable != null) {
            draggable.hold(true);
        }
        return this;
    }

    public DragRefreshWrapper onRefreshComplete() {
        headerIndicator.reset();
        if (draggable != null) {
            draggable.resetToBorder();
        }
        return this;
    }

    public DragRefreshWrapper onLoadComplete() {
        footerIndicator.reset();
        if (draggable != null) {
            draggable.resetToBorder();
        }
        return this;
    }

    public interface OnRefreshListener {
        void onTopRefresh();

        void onBottomLoad();
    }

}
