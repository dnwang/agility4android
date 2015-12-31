package org.pinwheel.agility.view.swiperefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;

import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
abstract class AbsSwipeView<T extends View> extends LinearLayout {

    public interface OnRefreshListener {
        void onPullDownToRefresh();

        void onPullUpToRefresh();
    }

    private static final int SCROLL_DURATION = 150;
    private static final float OFFSET_RADIO = 2.5f;
    private float mLastMotionY = -1;
    protected OnRefreshListener mRefreshListener;
    protected AbsSwipeLoadView mHeaderLayout;
    protected AbsSwipeLoadView mFooterLayout;
    private int mHeaderHeight;
    private int mFooterHeight;
    private boolean mPullRefreshEnabled = true;
    private boolean mPullLoadEnabled = true;
    private boolean mScrollLoadEnabled = true;
    private boolean mInterceptEventEnable = true;
    private boolean mIsHandledTouchEvent = false;
    private int mTouchSlop;
    private AbsSwipeLoadView.State mPullDownState = AbsSwipeLoadView.State.NONE;
    private AbsSwipeLoadView.State mPullUpState = AbsSwipeLoadView.State.NONE;
    protected T mRefreshableView;
    private SmoothScrollRunnable mSmoothScrollRunnable;
//    private FrameLayout mRefreshableViewWrapper;

    public AbsSwipeView(Context context) {
        super(context);
        init(context, null);
    }

    public AbsSwipeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public AbsSwipeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setOrientation(LinearLayout.VERTICAL);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        mHeaderLayout = createHeaderLoadingLayout(context, attrs);
        mFooterLayout = createFooterLoadingLayout(context, attrs);
        mRefreshableView = createRefreshableView(context, attrs);

//        if (null == mRefreshableView) {
//            throw new NullPointerException("Refreshable view can not be null.");
//        }
        if (mRefreshableView != null) {
            addRefreshableView(context, mRefreshableView);
        }
        addHeaderAndFooter(context);

        getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (getChildCount() > 4) {
                    throw new ExceptionInInitializerError("SwipeView must have only one child view");
                }
                AbsSwipeView.this.onGlobalLayout();
                getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    protected void onGlobalLayout() {
        refreshLoadingViewsSize();
    }

    private void refreshLoadingViewsSize() {
        int headerHeight = (null != mHeaderLayout) ? mHeaderLayout.getContentSize() : 0;
        int footerHeight = (null != mFooterLayout) ? mFooterLayout.getContentSize() : 0;

        if (headerHeight < 0) {
            headerHeight = 0;
        }

        if (footerHeight < 0) {
            footerHeight = 0;
        }

        mHeaderHeight = headerHeight;
        mFooterHeight = footerHeight;

        headerHeight = (null != mHeaderLayout) ? mHeaderLayout.getMeasuredHeight() : 0;
        footerHeight = (null != mFooterLayout) ? mFooterLayout.getMeasuredHeight() : 0;
        if (0 == footerHeight) {
            footerHeight = mFooterHeight;
        }

        int pLeft = getPaddingLeft();
        int pTop = getPaddingTop();
        int pRight = getPaddingRight();
        int pBottom = getPaddingBottom();

        pTop = -headerHeight;
        pBottom = -footerHeight;

        setPadding(pLeft, pTop, pRight, pBottom);
    }

    @Override
    protected final void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // We need to update the header/footer when our size changes
        refreshLoadingViewsSize();

        refreshRefreshableViewSize(w, h);

        /**
         * As we're currently in a Layout Pass, we need to schedule another one
         * to layout any changes we've made here
         */
        post(new Runnable() {
            @Override
            public void run() {
                requestLayout();
            }
        });
    }

    @Override
    public void setOrientation(int orientation) {
        if (LinearLayout.VERTICAL != orientation) {
            throw new IllegalArgumentException("This class only supports VERTICAL orientation.");
        }
        // Only support vertical orientation
        super.setOrientation(orientation);
    }

    @Override
    public final boolean onInterceptTouchEvent(MotionEvent event) {
        if (!isInterceptTouchEventEnabled()) {
            return false;
        }

        if (!isPullLoadEnabled() && !isPullRefreshEnabled()) {
            return false;
        }

        final int action = event.getAction();
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mIsHandledTouchEvent = false;
            return false;
        }

        if (action != MotionEvent.ACTION_DOWN && mIsHandledTouchEvent) {
            return true;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionY = event.getY();
                mIsHandledTouchEvent = false;
                break;

            case MotionEvent.ACTION_MOVE:
                final float deltaY = event.getY() - mLastMotionY;
                final float absDiff = Math.abs(deltaY);
                if (absDiff > mTouchSlop || isPullRefreshing() || isPullLoading()) {
                    mLastMotionY = event.getY();
                    if (isPullRefreshEnabled() && isReadyForPullDown()) {
                        mIsHandledTouchEvent = (Math.abs(getScrollYValue()) > 0 || deltaY > 0.5f);
                        if (mIsHandledTouchEvent) {
                            mRefreshableView.onTouchEvent(event);
                        }
                    } else if (isPullLoadEnabled() && isReadyForPullUp()) {
                        mIsHandledTouchEvent = (Math.abs(getScrollYValue()) > 0 || deltaY < -0.5f);
                    }
                }
                break;
            default:
                break;
        }
        return mIsHandledTouchEvent;
    }

    @Override
    public final boolean onTouchEvent(MotionEvent ev) {
        boolean handled = false;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionY = ev.getY();
                mIsHandledTouchEvent = false;
                break;

            case MotionEvent.ACTION_MOVE:
                final float deltaY = ev.getY() - mLastMotionY;
                mLastMotionY = ev.getY();
                if (isPullRefreshEnabled() && isReadyForPullDown()) {
                    pullHeaderLayout(deltaY / OFFSET_RADIO);
                    handled = true;
                } else if (isPullLoadEnabled() && isReadyForPullUp()) {
                    pullFooterLayout(deltaY / OFFSET_RADIO);
                    handled = true;
                } else {
                    mIsHandledTouchEvent = false;
                }
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mIsHandledTouchEvent) {
                    mIsHandledTouchEvent = false;
                    if (isReadyForPullDown()) {
                        if (mPullRefreshEnabled && (mPullDownState == AbsSwipeLoadView.State.RELEASE_TO_REFRESH)) {
                            startRefreshing();
                            handled = true;
                        }
                        resetHeaderLayout();
                    } else if (isReadyForPullUp()) {
                        if (isPullLoadEnabled() && (mPullUpState == AbsSwipeLoadView.State.RELEASE_TO_REFRESH)) {
                            startLoading();
                            handled = true;
                        }
                        resetFooterLayout();
                    }
                }
                break;

            default:
                break;
        }

        return handled;
    }

    public void setPullRefreshEnabled(boolean pullRefreshEnabled) {
        mPullRefreshEnabled = pullRefreshEnabled;
    }

    public void setPullLoadEnabled(boolean pullLoadEnabled) {
        mPullLoadEnabled = pullLoadEnabled;
    }

    public void setScrollLoadEnabled(boolean scrollLoadEnabled) {
        mScrollLoadEnabled = scrollLoadEnabled;
    }

    public boolean isPullRefreshEnabled() {
        return mPullRefreshEnabled && (null != mHeaderLayout);
    }

    public boolean isPullLoadEnabled() {
        return mPullLoadEnabled && (null != mFooterLayout);
    }

    public boolean isScrollLoadEnabled() {
        return mScrollLoadEnabled;
    }

    public void setOnRefreshListener(OnRefreshListener refreshListener) {
        mRefreshListener = refreshListener;
    }

    public void onPullDownRefreshComplete() {
        if (isPullRefreshing()) {
            mPullDownState = AbsSwipeLoadView.State.RESET;
            onStateChanged(AbsSwipeLoadView.State.RESET, true);

            postDelayed(new Runnable() {
                @Override
                public void run() {
                    setInterceptTouchEventEnabled(true);
                    mHeaderLayout.setState(AbsSwipeLoadView.State.RESET);
                }
            }, getSmoothScrollDuration());

            resetHeaderLayout();
            setInterceptTouchEventEnabled(false);
        }
    }

    public void onPullDownRefreshCompleteAndUpdateTime() {
        onPullDownRefreshComplete();
        setLastUpdateLabelAuto();
    }

    public void onPullUpRefreshComplete() {
        if (isPullLoading()) {
            mPullUpState = AbsSwipeLoadView.State.RESET;
            onStateChanged(AbsSwipeLoadView.State.RESET, false);

            postDelayed(new Runnable() {
                @Override
                public void run() {
                    setInterceptTouchEventEnabled(true);
                    mFooterLayout.setState(AbsSwipeLoadView.State.RESET);
                }
            }, getSmoothScrollDuration());

            resetFooterLayout();
            setInterceptTouchEventEnabled(false);
        }
    }

    public T getRefreshableView() {
        return mRefreshableView;
    }

    public AbsSwipeLoadView getHeaderLoadingLayout() {
        return mHeaderLayout;
    }

    public AbsSwipeLoadView getFooterLoadingLayout() {
        return mFooterLayout;
    }

    public void setLastUpdatedLabel(CharSequence label) {
        if (null != mHeaderLayout) {
            mHeaderLayout.setLastUpdatedLabel(label);
        }

        if (null != mFooterLayout) {
            mFooterLayout.setLastUpdatedLabel(label);
        }
    }

    public void setLastUpdateLabelAuto() {
        String time = new SimpleDateFormat("MM-dd HH:mm").format(new Date(System.currentTimeMillis()));
        setLastUpdatedLabel(time);
    }

    public void doPullRefreshing(final boolean smoothScroll, final long delayMillis) {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                int newScrollValue = -mHeaderHeight;
                int duration = smoothScroll ? SCROLL_DURATION : 0;
                startRefreshing();
                smoothScrollTo(newScrollValue, duration, 0);
            }
        }, delayMillis);
    }

    protected abstract T createRefreshableView(Context context, AttributeSet attrs);

    protected abstract boolean isReadyForPullDown();

    protected abstract boolean isReadyForPullUp();

    protected abstract AbsSwipeLoadView createHeaderLoadingLayout(Context context, AttributeSet attrs);

    protected abstract AbsSwipeLoadView createFooterLoadingLayout(Context context, AttributeSet attrs);

    protected long getSmoothScrollDuration() {
        return SCROLL_DURATION;
    }

    protected void refreshRefreshableViewSize(int width, int height) {
//        if (null != mRefreshableViewWrapper) {
//            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mRefreshableViewWrapper.getLayoutParams();
//            if (lp.height != height) {
//                lp.height = height;
//                mRefreshableViewWrapper.requestLayout();
//            }
//        }
        if (null != mRefreshableView) {
            ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) mRefreshableView.getLayoutParams();
            if (lp.height != height) {
                lp.height = height;
                mRefreshableView.requestLayout();
            }
        }
    }

    protected void addRefreshableView(Context context, T refreshableView) {
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.MATCH_PARENT;

//        mRefreshableViewWrapper = new FrameLayout(context);
//        mRefreshableViewWrapper.addView(refreshableView, width, height);

        height = 1;
        addView(mRefreshableView, new LinearLayout.LayoutParams(width, height));
    }

    protected void addHeaderAndFooter(Context context) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        final AbsSwipeLoadView headerLayout = mHeaderLayout;
        final AbsSwipeLoadView footerLayout = mFooterLayout;

        if (null != headerLayout) {
            if (this == headerLayout.getParent()) {
                removeView(headerLayout);
            }

            addView(headerLayout, 0, params);
        }

        if (null != footerLayout) {
            if (this == footerLayout.getParent()) {
                removeView(footerLayout);
            }

            addView(footerLayout, -1, params);
        }
    }

    protected void pullHeaderLayout(float delta) {
        int oldScrollY = getScrollYValue();
        if (delta < 0 && (oldScrollY - delta) >= 0) {
            setScrollTo(0, 0);
            return;
        }

        setScrollBy(0, -(int) delta);

        if (null != mHeaderLayout && 0 != mHeaderHeight) {
            float scale = Math.abs(getScrollYValue()) / (float) mHeaderHeight;
            mHeaderLayout.onPull(scale);
        }

        int scrollY = Math.abs(getScrollYValue());
        if (isPullRefreshEnabled() && !isPullRefreshing()) {
            if (scrollY > mHeaderHeight) {
                mPullDownState = AbsSwipeLoadView.State.RELEASE_TO_REFRESH;
            } else {
                mPullDownState = AbsSwipeLoadView.State.PULL_TO_REFRESH;
            }

            mHeaderLayout.setState(mPullDownState);
            onStateChanged(mPullDownState, true);
        }
    }

    protected void pullFooterLayout(float delta) {
        int oldScrollY = getScrollYValue();
        if (delta > 0 && (oldScrollY - delta) <= 0) {
            setScrollTo(0, 0);
            return;
        }

        setScrollBy(0, -(int) delta);

        if (null != mFooterLayout && 0 != mFooterHeight) {
            float scale = Math.abs(getScrollYValue()) / (float) mFooterHeight;
            mFooterLayout.onPull(scale);
        }

        int scrollY = Math.abs(getScrollYValue());
        if (isPullLoadEnabled() && !isPullLoading()) {
            if (scrollY > mFooterHeight) {
                mPullUpState = AbsSwipeLoadView.State.RELEASE_TO_REFRESH;
            } else {
                mPullUpState = AbsSwipeLoadView.State.PULL_TO_REFRESH;
            }

            mFooterLayout.setState(mPullUpState);
            onStateChanged(mPullUpState, false);
        }
    }

    protected void resetHeaderLayout() {
        final int scrollY = Math.abs(getScrollYValue());
        final boolean refreshing = isPullRefreshing();

        if (refreshing && scrollY <= mHeaderHeight) {
            smoothScrollTo(0);
            return;
        }

        if (refreshing) {
            smoothScrollTo(-mHeaderHeight);
        } else {
            smoothScrollTo(0);
        }
    }

    protected void resetFooterLayout() {
        int scrollY = Math.abs(getScrollYValue());
        boolean isPullLoading = isPullLoading();

        if (isPullLoading && scrollY <= mFooterHeight) {
            smoothScrollTo(0);
            return;
        }

        if (isPullLoading) {
            smoothScrollTo(mFooterHeight);
        } else {
            smoothScrollTo(0);
        }
    }

    protected boolean isPullRefreshing() {
        return (mPullDownState == AbsSwipeLoadView.State.REFRESHING);
    }

    protected boolean isPullLoading() {
        return (mPullUpState == AbsSwipeLoadView.State.REFRESHING);
    }

    protected void startRefreshing() {
        if (isPullRefreshing()) {
            return;
        }

        mPullDownState = AbsSwipeLoadView.State.REFRESHING;
        onStateChanged(AbsSwipeLoadView.State.REFRESHING, true);

        if (null != mHeaderLayout) {
            mHeaderLayout.setState(AbsSwipeLoadView.State.REFRESHING);
        }

        if (isOnlyOverScroll) {
            onPullDownRefreshCompleteAndUpdateTime();
        } else if (null != mRefreshListener) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    mRefreshListener.onPullDownToRefresh();
                }
            }, getSmoothScrollDuration());
        }
    }

    protected void startLoading() {
        if (isPullLoading()) {
            return;
        }

        mPullUpState = AbsSwipeLoadView.State.REFRESHING;
        onStateChanged(AbsSwipeLoadView.State.REFRESHING, false);

        if (null != mFooterLayout) {
            mFooterLayout.setState(AbsSwipeLoadView.State.REFRESHING);
        }

        if (isOnlyOverScroll) {
            onPullUpRefreshComplete();
        } else if (null != mRefreshListener) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    mRefreshListener.onPullUpToRefresh();
                }
            }, getSmoothScrollDuration());
        }
    }

    protected void onStateChanged(AbsSwipeLoadView.State state, boolean isPullDown) {
//        System.out.println("onStateChanged() --> " + state + ", " + isPullDown);
    }

    private void setScrollTo(int x, int y) {
        scrollTo(x, y);
    }

    private void setScrollBy(int x, int y) {
        scrollBy(x, y);
    }

    private int getScrollYValue() {
        return getScrollY();
    }

    private void smoothScrollTo(int newScrollValue) {
        smoothScrollTo(newScrollValue, getSmoothScrollDuration(), 0);
    }

    private void smoothScrollTo(int newScrollValue, long duration, long delayMillis) {
        if (null != mSmoothScrollRunnable) {
            mSmoothScrollRunnable.stop();
        }

        int oldScrollValue = this.getScrollYValue();
        boolean post = (oldScrollValue != newScrollValue);
        if (post) {
            mSmoothScrollRunnable = new SmoothScrollRunnable(oldScrollValue, newScrollValue, duration);
        }

        if (post) {
            if (delayMillis > 0) {
                postDelayed(mSmoothScrollRunnable, delayMillis);
            } else {
                post(mSmoothScrollRunnable);
            }
        }
    }

    private void setInterceptTouchEventEnabled(boolean enabled) {
        mInterceptEventEnable = enabled;
    }

    private boolean isInterceptTouchEventEnabled() {
        return mInterceptEventEnable;
    }

    final class SmoothScrollRunnable implements Runnable {
        private final Interpolator mInterpolator;
        private final int mScrollToY;
        private final int mScrollFromY;
        private final long mDuration;
        private boolean mContinueRunning = true;
        private long mStartTime = -1;
        private int mCurrentY = -1;

        public SmoothScrollRunnable(int fromY, int toY, long duration) {
            mScrollFromY = fromY;
            mScrollToY = toY;
            mDuration = duration;
            mInterpolator = new DecelerateInterpolator();
        }

        @Override
        public void run() {
            /**
             * If the duration is 0, we scroll the view to target y directly.
             */
            if (mDuration <= 0) {
                setScrollTo(0, mScrollToY);
                return;
            }

            /**
             * Only set mStartTime if this is the first time we're starting,
             * else actually calculate the Y delta
             */
            if (mStartTime == -1) {
                mStartTime = System.currentTimeMillis();
            } else {

                /**
                 * We do do all calculations in long to reduce software float
                 * calculations. We use 1000 as it gives us good accuracy and
                 * small rounding errors
                 */
                final long oneSecond = 1000;    // SUPPRESS CHECKSTYLE
                long normalizedTime = (oneSecond * (System.currentTimeMillis() - mStartTime)) / mDuration;
                normalizedTime = Math.max(Math.min(normalizedTime, oneSecond), 0);

                final int deltaY = Math.round((mScrollFromY - mScrollToY)
                        * mInterpolator.getInterpolation(normalizedTime / (float) oneSecond));
                mCurrentY = mScrollFromY - deltaY;

                setScrollTo(0, mCurrentY);
            }

            // If we're not at the target Y, keep going...
            if (mContinueRunning && mScrollToY != mCurrentY) {
                AbsSwipeView.this.postDelayed(this, 16);// SUPPRESS CHECKSTYLE
            }
        }

        public void stop() {
            mContinueRunning = false;
            removeCallbacks(this);
        }
    }

    /**************************************************************************/

    private boolean isOnlyOverScroll = false;

    public void setOnlyOverScroll(boolean is) {
        isOnlyOverScroll = is;
        if (is) {
            if (null != mHeaderLayout) {
                mHeaderLayout.setVisibility(INVISIBLE);
            }
            if (null != mFooterLayout) {
                mFooterLayout.setVisibility(INVISIBLE);
            }
        } else {
            if (null != mHeaderLayout) {
                mHeaderLayout.setVisibility(VISIBLE);
            }
            if (null != mFooterLayout) {
                mFooterLayout.setVisibility(VISIBLE);
            }
        }
    }

}
