package org.pinwheel.agility.view.swiperefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public class SwipeScrollView extends AbsSwipeView<ScrollView> {

    private ScrollView mScrollView;

    public SwipeScrollView(Context context) {
        this(context, null);
    }

    public SwipeScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected ScrollView createRefreshableView(Context context, AttributeSet attrs) {
        mScrollView = new ScrollView(context);
        return mScrollView;
    }

    @Override
    protected void onGlobalLayout() {
        super.onGlobalLayout();
        View contentView = SwipeScrollView.this.getChildAt(3);
        replaceContentView(contentView);
        setOnlyOverScroll(true);
        setNormalStyle();
    }

    @Override
    protected AbsSwipeLoadView createHeaderLoadingLayout(Context context, AttributeSet attrs) {
        return new SimpleSwipeHeaderView(context, attrs);
    }

    @Override
    protected AbsSwipeLoadView createFooterLoadingLayout(Context context, AttributeSet attrs) {
        return new SimpleSwipeFooterView(context, attrs);
    }

    @Override
    protected boolean isReadyForPullDown() {
        return mRefreshableView.getScrollY() == 0;
    }

    @Override
    protected boolean isReadyForPullUp() {
        View scrollViewChild = mRefreshableView.getChildAt(0);
        if (null != scrollViewChild) {
            return mRefreshableView.getScrollY() >= (scrollViewChild.getHeight() - getHeight());
        }
        return false;
    }

    public void setNormalStyle() {
        mScrollView.setHorizontalScrollBarEnabled(false);
        mScrollView.setVerticalScrollBarEnabled(false);
    }

    private void replaceContentView(View contentView) {
        if (contentView == null) {
            return;
        }
        if (contentView.getParent() != null) {
            ((ViewGroup) contentView.getParent()).removeView(contentView);
        }
        mRefreshableView.removeAllViews();
        mRefreshableView.addView(contentView);
    }

    @Override
    public void doPullRefreshing(boolean smoothScroll, long delayMillis) {
        post(new Runnable() {
            @Override
            public void run() {
                mRefreshableView.fullScroll(FOCUS_UP);
            }
        });
        super.doPullRefreshing(smoothScroll, delayMillis);
    }
}
