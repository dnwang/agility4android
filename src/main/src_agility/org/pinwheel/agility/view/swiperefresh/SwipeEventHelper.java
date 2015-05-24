package org.pinwheel.agility.view.swiperefresh;

import android.view.ViewTreeObserver;
import android.widget.AbsListView;

/**
 * Created by dnwang on 4/14/15.
 */
public final class SwipeEventHelper implements SwipeListView.OnRefreshListener, AbsListView.OnScrollListener {

    public static final int TYPE_SCROLL = 0x00;
    public static final int TYPE_PULL_UP = 0x10;
    public static final int TYPE_PULL_DOWN = 0x11;

    private boolean isRefreshing;
    private boolean isLoadingMore;
    private boolean hasNoData;

    private int lastLine = -1;

    private int columns;

    private AbsSwipeAbsListView absListView;
    private OnSwipeAdapter adapter;

    public SwipeEventHelper(AbsSwipeAbsListView absListView, OnSwipeAdapter adapter) {
        this.absListView = absListView;
        this.adapter = adapter;

        this.absListView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (SwipeEventHelper.this.absListView instanceof SwipeGridView) {
                    columns = ((SwipeGridView) SwipeEventHelper.this.absListView).getNumColumns();
                } else {
                    columns = 1;
                }
                SwipeEventHelper.this.absListView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    @Override
    public void onPullDownToRefresh() {
        if (adapter != null) {
            adapter.onPullDownToRefresh();
        }
        onLoading(TYPE_PULL_DOWN);
    }

    @Override
    public void onPullUpToRefresh() {
        if (adapter != null) {
            adapter.onPullUpToRefresh();
        }
        onLoading(TYPE_PULL_UP);
    }

    public void onLoading(int type) {
        if (adapter == null) {
            return;
        }

        if (type == TYPE_PULL_DOWN) {
            if (isRefreshing) {
                return;
            }
            isRefreshing = true;
        } else if (type == TYPE_PULL_UP || type == TYPE_SCROLL) {
            if (isLoadingMore) {
                return;
            } else if (hasNoData) {
                absListView.setHasMoreData(false);
                absListView.onPullUpRefreshComplete();
                isLoadingMore = false;
                return;
            }
            isLoadingMore = true;
        }
        adapter.onLoading(type);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (adapter != null) {
            adapter.onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (adapter != null) {
            adapter.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
        if (lastLine < 0 && adapter != null) {
            if (totalItemCount - (firstVisibleItem + visibleItemCount) <= lastLine * columns) {
                onLoading(TYPE_SCROLL);
            }
        }
    }

    public void setLoadingMoreInLastLine(int line) {
        lastLine = line;
    }

    public synchronized void onLoadComplete(boolean hasNoData) {
        this.hasNoData = hasNoData;
        absListView.setHasMoreData(!this.hasNoData);

        isRefreshing = false;
        isLoadingMore = false;

        absListView.onPullUpRefreshComplete();
        absListView.onPullDownRefreshCompleteAndUpdateTime();
    }

    public boolean isRefreshing() {
        return isRefreshing;
    }

    public boolean isLoadingMore() {
        return isLoadingMore;
    }


    public static abstract class OnSwipeAdapter implements AbsSwipeView.OnRefreshListener, AbsListView.OnScrollListener {

        @Override
        public void onPullDownToRefresh() {
        }

        @Override
        public void onPullUpToRefresh() {
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        }

        public void onLoading(int type) {
        }

    }

}
