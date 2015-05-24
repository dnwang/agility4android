package org.pinwheel.agility.view.swiperefresh;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

/**
 * 这个类定义了Header和Footer的共通行为
 *
 * @author Li Hong
 * @since 2013-8-16
 */
public abstract class AbsSwipeLoadView extends RelativeLayout {

    /**
     * 容器布局
     */
    protected View mContainer;
    /**
     * 当前的状态
     */
    private State mCurState = State.NONE;
    /**
     * 前一个状态
     */
    private State mPreState = State.NONE;

    protected int headerMinHeight = (int) (getResources().getDisplayMetrics().density * 40);
    protected int footerMinHeight = (int) (getResources().getDisplayMetrics().density * 40);

    protected boolean isHasMoreData = true;

    public void setHasMoreData(boolean is) {
        this.isHasMoreData = is;
    }

    /**
     * 构造方法
     *
     * @param context context
     */
    public AbsSwipeLoadView(Context context) {
        this(context, null);
    }

    /**
     * 构造方法
     *
     * @param context context
     * @param attrs   attrs
     */
    public AbsSwipeLoadView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 构造方法
     *
     * @param context  context
     * @param attrs    attrs
     * @param defStyle defStyle
     */
    public AbsSwipeLoadView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    /**
     * 初始化
     *
     * @param context context
     * @param attrs   attrs
     */
    protected void init(Context context, AttributeSet attrs) {
        mContainer = createLoadingView(context, attrs);

        // FIXME denan.wang; 2015/3/3; 避免在项目中使用SwipeView时xml始终提示警告
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

    /**
     * 显示或隐藏这个布局
     *
     * @param show flag
     */
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

    /**
     * 设置最后更新的时间文本
     *
     * @param label 文本
     */
    public void setLastUpdatedLabel(CharSequence label) {

    }

    /**
     * 设置加载中的图片
     *
     * @param drawable 图片
     */
    public void setLoadingDrawable(Drawable drawable) {

    }

    /**
     * 设置拉动的文本，典型的是“下拉可以刷新”
     *
     * @param pullLabel 拉动的文本
     */
    public void setPullLabel(CharSequence pullLabel) {

    }

    /**
     * 设置正在刷新的文本，典型的是“正在刷新”
     *
     * @param refreshingLabel 刷新文本
     */
    public void setRefreshingLabel(CharSequence refreshingLabel) {

    }

    /**
     * 设置释放的文本，典型的是“松开可以刷新”
     *
     * @param releaseLabel 释放文本
     */
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

    /**
     * 在拉动时调用
     *
     * @param scale 拉动的比例
     */
    public void onPull(float scale) {

    }

    /**
     * 得到前一个状态
     *
     * @return 状态
     */
    protected State getPreState() {
        return mPreState;
    }

    /**
     * 当状态改变时调用
     *
     * @param curState 当前状态
     * @param oldState 老的状态
     */
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

    /**
     * 当状态设置为{@link State#RESET}时调用
     */
    protected void onReset() {

    }

    /**
     * 当状态设置为{@link State#PULL_TO_REFRESH}时调用
     */
    protected void onPullToRefresh() {

    }

    /**
     * 当状态设置为{@link State#RELEASE_TO_REFRESH}时调用
     */
    protected void onReleaseToRefresh() {

    }

    /**
     * 当状态设置为{@link State#REFRESHING}时调用
     */
    protected void onRefreshing() {

    }

    /**
     * 当状态设置为{@link State#NO_MORE_DATA}时调用
     */
    protected void onNoMoreData() {

    }

    /**
     * 得到当前Layout的内容大小，它将作为一个刷新的临界点
     *
     * @return 高度
     */
    public abstract int getContentSize();

    /**
     * 创建Loading的View
     *
     * @param context context
     * @param attrs   attrs
     * @return Loading的View
     */
    protected abstract View createLoadingView(Context context, AttributeSet attrs);

    /**
     * 当前的状态
     */
    public enum State {

        /**
         * Initial state
         */
        NONE,

        /**
         * When the UI is in a state which means that user is not interacting
         * with the Pull-to-Refresh function.
         */
        RESET,

        /**
         * When the UI is being pulled by the user, but has not been pulled far
         * enough so that it refreshes when released.
         */
        PULL_TO_REFRESH,

        /**
         * When the UI is being pulled by the user, and <strong>has</strong>
         * been pulled far enough so that it will refresh when released.
         */
        RELEASE_TO_REFRESH,

        /**
         * When the UI is currently refreshing, caused by a pull gesture.
         */
        REFRESHING,

        /**
         * When the UI is currently refreshing, caused by a pull gesture.
         */
        @Deprecated
        LOADING,

        /**
         * No more data
         */
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
