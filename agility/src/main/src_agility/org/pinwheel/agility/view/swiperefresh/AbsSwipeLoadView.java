package org.pinwheel.agility.view.swiperefresh;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public abstract class AbsSwipeLoadView extends RelativeLayout {

    protected View mContainer;
    private State mCurState = State.NONE;
    private State mPreState = State.NONE;

    protected int headerMinHeight = (int) (getResources().getDisplayMetrics().density * 40);
    protected int footerMinHeight = (int) (getResources().getDisplayMetrics().density * 40);

    protected boolean isHasMoreData = true;

    public void setHasMoreData(boolean is) {
        this.isHasMoreData = is;
    }

    public AbsSwipeLoadView(Context context) {
        this(context, null);
    }

    public AbsSwipeLoadView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AbsSwipeLoadView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    protected void init(Context context, AttributeSet attrs) {
        mContainer = createLoadingView(context, attrs);

//        if (null == mContainer) {
//            throw new NullPointerException("Loading view can not be null.");
//        }
        if (mContainer == null) {
            return;
        }

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        addView(mContainer, params);
    }

    public void show(boolean show) {
        // If is showing, do nothing.
        if (show == (View.VISIBLE == getVisibility())) {
            return;
        }

        ViewGroup.LayoutParams params = mContainer.getLayoutParams();
        if (null != params) {
            if (show) {
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            } else {
                params.height = 0;
            }
            setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        }
    }

    public void setLastUpdatedLabel(CharSequence label) {

    }

    public void setLoadingDrawable(Drawable drawable) {

    }

    public void setPullLabel(CharSequence pullLabel) {

    }

    public void setRefreshingLabel(CharSequence refreshingLabel) {

    }

    public void setReleaseLabel(CharSequence releaseLabel) {

    }

    public void setState(State state) {
        if (mCurState != state) {
            mPreState = mCurState;
            mCurState = state;
            onStateChanged(state, mPreState);
        }
    }

    public State getState() {
        return mCurState;
    }

    public void onPull(float scale) {

    }

    protected State getPreState() {
        return mPreState;
    }

    protected void onStateChanged(State curState, State oldState) {
        switch (curState) {
            case RESET:
                onReset();
                break;

            case RELEASE_TO_REFRESH:
                onReleaseToRefresh();
                break;

            case PULL_TO_REFRESH:
                onPullToRefresh();
                break;

            case REFRESHING:
                onRefreshing();
                break;

            case NO_MORE_DATA:
                onNoMoreData();
                break;

            default:
                break;
        }
    }

    protected void onReset() {

    }

    protected void onPullToRefresh() {

    }

    protected void onReleaseToRefresh() {

    }

    protected void onRefreshing() {

    }

    protected void onNoMoreData() {

    }

    public abstract int getContentSize();

    protected abstract View createLoadingView(Context context, AttributeSet attrs);

    public enum State {

        NONE,
        RESET,
        PULL_TO_REFRESH,
        RELEASE_TO_REFRESH,
        REFRESHING,
        @Deprecated
        LOADING,
        NO_MORE_DATA,
    }

    protected int getString(String id_name) {
        return getResources().getIdentifier(id_name, "string", getContext().getPackageName());
    }

    protected int getLayout(String id_name) {
        return getResources().getIdentifier(id_name, "layout", getContext().getPackageName());
    }

    protected int getId(String id_name) {
        return getResources().getIdentifier(id_name, "id", getContext().getPackageName());
    }
}
