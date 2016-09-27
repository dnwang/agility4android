package org.pinwheel.agility.view.swiperefresh;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.ListAdapter;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
abstract class AbsSwipeAbsListView extends AbsSwipeView<AbsListView> {

    private AbsListView mAbsListView;

    public AbsSwipeAbsListView(Context context) {
        this(context, null);
    }

    public AbsSwipeAbsListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AbsSwipeAbsListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mAbsListView = (AbsListView) mRefreshableView;
        mAbsListView.setCacheColorHint(Color.TRANSPARENT);
    }

    public void setHasMoreData(boolean hasMoreData) {
        if (null != mFooterLayout) {
            if (!hasMoreData) {
                mFooterLayout.setState(AbsSwipeLoadView.State.NO_MORE_DATA);
            }
            mFooterLayout.setHasMoreData(hasMoreData);
        }
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
    protected boolean isReadyForPullUp() {
        final Adapter adapter = mAbsListView.getAdapter();
        if (null == adapter || adapter.isEmpty()) {
            return true;
        }
        final int lastItemPosition = adapter.getCount() - 1;
        final int lastVisiblePosition = mAbsListView.getLastVisiblePosition();
        if (lastVisiblePosition >= lastItemPosition - 1) {
            final int childIndex = lastVisiblePosition - mAbsListView.getFirstVisiblePosition();
            final int childCount = mAbsListView.getChildCount();
            final int index = Math.min(childIndex, childCount - 1);
            final View lastVisibleChild = mAbsListView.getChildAt(index);
            if (lastVisibleChild != null) {
                return lastVisibleChild.getBottom() <= mAbsListView.getBottom();
            }
        }
        return false;
    }

    @Override
    protected boolean isReadyForPullDown() {
        final Adapter adapter = mAbsListView.getAdapter();
        if (null == adapter || adapter.isEmpty()) {
            return true;
        }
        int mostTop = (mAbsListView.getChildCount() > 0) ? mAbsListView.getChildAt(0).getTop() : 0;
        if (mostTop >= 0) {
            return true;
        }
        return false;
    }

    @Override
    public void setScrollLoadEnabled(boolean scrollLoadEnabled) {
        super.setScrollLoadEnabled(scrollLoadEnabled);
        if (scrollLoadEnabled) {
            // 设置Footer
            if (null == mFooterLayout) {
                mFooterLayout = new SimpleSwipeFooterView(getContext());
            }
            //mAbsListView.removeFooterView(mFooterLayout);
            //mAbsListView.addFooterView(mFooterLayout, null, false);
        } else {
            if (null != mFooterLayout) {
                //mAbsListView.removeFooterView(mFooterLayout);
            }
        }
    }

    private boolean hasMoreData() {
        if ((null != mFooterLayout) && (mFooterLayout.getState() == AbsSwipeLoadView.State.NO_MORE_DATA)) {
            return false;
        }
        return true;
    }

    public void setSwipeEventHelper(SwipeEventHelper eventHelper) {
        setOnRefreshListener(eventHelper);
        setOnScrollListener(eventHelper);
    }

    public void setNormalStyle() {
        mAbsListView.setSelector(new BitmapDrawable());
        mAbsListView.setCacheColorHint(0);
        mAbsListView.setHorizontalScrollBarEnabled(false);
        mAbsListView.setVerticalScrollBarEnabled(false);
        mAbsListView.setOverScrollMode(OVER_SCROLL_NEVER);
    }

    public void setAdapter(ListAdapter adapter) {
        mAbsListView.setAdapter(adapter);
    }

    public ListAdapter getAdapter() {
        return mAbsListView.getAdapter();
    }

    public void setOnItemClickListener(AbsListView.OnItemClickListener listener) {
        mAbsListView.setOnItemClickListener(listener);
    }

    public void setOnItemLongClickListener(AbsListView.OnItemLongClickListener listener) {
        mAbsListView.setOnItemLongClickListener(listener);
    }

    public void setOnScrollListener(AbsListView.OnScrollListener listener) {
        mAbsListView.setOnScrollListener(listener);
    }

    @Override
    public void doPullRefreshing(boolean smoothScroll, long delayMillis) {
        mAbsListView.setSelection(0);
        super.doPullRefreshing(smoothScroll, delayMillis);
    }
}
