package org.pinwheel.demo4agility.test;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import org.pinwheel.agility.view.drag.Draggable;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public class DragRefreshWrapper extends FrameLayout {

    private Draggable draggable;

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

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        this.draggable = null;
        int size = getChildCount();
        for (int i = 0; i < size; i++) {
            View view = getChildAt(i);
            if (view instanceof Draggable) {
                this.draggable = (Draggable) view;
                break;
            }
        }
        if (draggable == null) {
            throw new IllegalStateException(getClass().getSimpleName() + " must contains Draggable view.");
        }
    }

    private void init() {

    }

    public void onRefreshComplete() {

    }

    public void onLoadComplete() {

    }

    public interface OnRefreshListener {
        void onRefresh();

        void onLoad();
    }

}
