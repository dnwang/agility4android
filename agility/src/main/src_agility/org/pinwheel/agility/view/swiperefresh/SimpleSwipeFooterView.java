package org.pinwheel.agility.view.swiperefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
final class SimpleSwipeFooterView extends AbsSwipeLoadView {

    private ProgressBar mProgressBar;
    private TextView mHintView;

    public SimpleSwipeFooterView(Context context) {
        super(context);
        init(context);
    }

    public SimpleSwipeFooterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mProgressBar = (ProgressBar) findViewById(getId("swipe_progress_footer_progressbar"));
        mHintView = (TextView) findViewById(getId("swipe_txt_footer_hint"));
        setState(State.RESET);
    }

    @Override
    protected View createLoadingView(Context context, AttributeSet attrs) {
        return LayoutInflater.from(context).inflate(getLayout("swipe_layout_footer"), null);
    }

    @Override
    public void setLastUpdatedLabel(CharSequence label) {
    }

    @Override
    public int getContentSize() {
        //当返回值为0时，意外有道底部直接更多的效果...
        int height = 0;
        if (null != mContainer) {
            height = mContainer.getHeight();
        }
        return height < footerMinHeight ? footerMinHeight : height;
    }

    @Override
    protected void onStateChanged(State curState, State oldState) {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(GONE);
        }
        if (mHintView != null) {
            mHintView.setVisibility(INVISIBLE);
        }
        super.onStateChanged(curState, oldState);
    }

    @Override
    protected void onReset() {
        if (!isHasMoreData) {
            if (mHintView != null) {
                mHintView.setText(getString("swipe_no_more_data"));
            }
        } else {
            if (mHintView != null) {
                mHintView.setText(getString("swipe_pull_to_load"));
            }
        }
    }

    @Override
    protected void onPullToRefresh() {
        mHintView.setVisibility(VISIBLE);
        if (!isHasMoreData) {
            mHintView.setText(getString("swipe_no_more_data"));
        } else {
            mHintView.setText(getString("swipe_pull_to_load"));
        }
    }

    @Override
    protected void onReleaseToRefresh() {
        mHintView.setVisibility(VISIBLE);
        if (!isHasMoreData) {
            mHintView.setText(getString("swipe_no_more_data"));
        } else {
            mHintView.setText(getString("swipe_release_to_load"));
        }
    }

    @Override
    protected void onRefreshing() {
        mHintView.setVisibility(VISIBLE);
        if (!isHasMoreData) {
            mHintView.setText(getString("swipe_no_more_data"));
        } else {
            mProgressBar.setVisibility(VISIBLE);
            mHintView.setText(getString("swipe_loading"));
        }
    }

    @Override
    protected void onNoMoreData() {
        mHintView.setVisibility(VISIBLE);
        mHintView.setText(getString("swipe_no_more_data"));
    }

}
