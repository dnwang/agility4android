package org.pinwheel.agility.view.swiperefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
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
final class SimpleSwipeHeaderView extends AbsSwipeLoadView {

    private static final int ROTATE_ANIM_DURATION = 150;
    private ImageView mArrowImageView;
    private ProgressBar mProgressBar;
    private TextView mHintTextView;
    private TextView mHeaderTimeView;
    private Animation mRotateUpAnim;
    private Animation mRotateDownAnim;

    public SimpleSwipeHeaderView(Context context) {
        super(context);
        init(context);
    }

    public SimpleSwipeHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mArrowImageView = (ImageView) findViewById(getId("swipe_img_header_arrow"));
        mHintTextView = (TextView) findViewById(getId("swipe_txt_header_hint"));
        mProgressBar = (ProgressBar) findViewById(getId("swipe_progress_header_progressbar"));
        mHeaderTimeView = (TextView) findViewById(getId("swipe_txt_header_time"));

        float pivotValue = 0.5f;    // SUPPRESS CHECKSTYLE
        float toDegree = -180f;     // SUPPRESS CHECKSTYLE
        mRotateUpAnim = new RotateAnimation(0.0f, toDegree, Animation.RELATIVE_TO_SELF, pivotValue,
                Animation.RELATIVE_TO_SELF, pivotValue);
        mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateUpAnim.setFillAfter(true);
        mRotateDownAnim = new RotateAnimation(toDegree, 0.0f, Animation.RELATIVE_TO_SELF, pivotValue,
                Animation.RELATIVE_TO_SELF, pivotValue);
        mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateDownAnim.setFillAfter(true);
        setState(State.PULL_TO_REFRESH);
    }

    @Override
    public void setLastUpdatedLabel(CharSequence label) {
//        mHeaderTimeViewTitle.setVisibility(TextUtils.isEmpty(label) ? INVISIBLE : VISIBLE);
        mHeaderTimeView.setText(label);
    }

    @Override
    public int getContentSize() {
        int height = 0;
        if (null != mContainer) {
            height = mContainer.getHeight();
        }
        return height < headerMinHeight ? headerMinHeight : height;
    }

    @Override
    protected View createLoadingView(Context context, AttributeSet attrs) {
        return LayoutInflater.from(context).inflate(getLayout("swipe_layout_header"), null);
    }

    @Override
    protected void onStateChanged(State curState, State oldState) {
        if (mArrowImageView != null) {
            mArrowImageView.setVisibility(VISIBLE);
        }
        if (mProgressBar != null) {
            mProgressBar.setVisibility(INVISIBLE);
        }

        super.onStateChanged(curState, oldState);
    }

    @Override
    protected void onReset() {
        mArrowImageView.clearAnimation();
        mHintTextView.setText(getString("swipe_pull_to_refresh"));
    }

    @Override
    protected void onPullToRefresh() {
        if (State.RELEASE_TO_REFRESH == getPreState()) {
            mArrowImageView.clearAnimation();
            mArrowImageView.startAnimation(mRotateDownAnim);
        }
        if (mHintTextView != null) {
            mHintTextView.setText(getString("swipe_pull_to_refresh"));
        }
    }

    @Override
    protected void onReleaseToRefresh() {
        mArrowImageView.clearAnimation();
        mArrowImageView.startAnimation(mRotateUpAnim);
        mHintTextView.setText(getString("swipe_release_to_refresh"));
    }

    @Override
    protected void onRefreshing() {
        mArrowImageView.clearAnimation();
        mArrowImageView.setVisibility(INVISIBLE);
        mProgressBar.setVisibility(VISIBLE);
        mHintTextView.setText(getString("swipe_refreshing"));
    }

}
