package org.pinwheel.agility.view;

import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;

import java.util.ArrayList;

/**
 * 版权所有 (C), 2014 <br>
 * <br>
 * All rights reserved
 *
 * @author dnwang
 * @date 2014/9/29 21:27
 * @description
 */
public class SweetScrollView extends ScrollView implements Swipeable, GestureDetector.OnGestureListener {

    private boolean inSwipe;// 是否正在滑动中
    private int current_state;// 当前状态

    private int standard_top; //返回时作为标准值
    private int standard_bottom; //返回时作为标准值

    private GestureDetector gestureDetector;

    public SweetScrollView(Context context) {
        super(context);
        this.init(context);
    }

    public SweetScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(context);
    }

    public SweetScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.init(context);
    }

    private void init(Context context) {
        this.setOverScrollMode(OVER_SCROLL_NEVER);
        this.gestureDetector = new GestureDetector(context, this);
        this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // 记录开始拖动前的参考值
                setStandard(getTop(), getBottom());
                getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    private void setStandard(int top, int bottom) {
        standard_top = top;
        standard_bottom = bottom - standard_top; // 去除listview顶部的其它距离
        last_top = standard_top;
    }

    private boolean isNeedSendCancel;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        boolean isHandled = gestureDetector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                if (isHandled && isNeedSendCancel) {
                    isNeedSendCancel = false;
                    // 不能只发一次cancel,在hold后手动滑回也要cancel
                    final long now = SystemClock.uptimeMillis();
                    event = MotionEvent.obtain(now, now, MotionEvent.ACTION_CANCEL, event.getX(), event.getY(), 0);
                    super.dispatchTouchEvent(event);
                    return true;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            case MotionEvent.ACTION_UP:
                if (isHold()) {
                    //在悬停位置时,可点击不回弹
                } else {
                    reset("noCallBack");
                }
                break;
        }
        isNeedSendCancel = true;
        boolean superHandle = false;
        try {
            superHandle = super.dispatchTouchEvent(event);
        } catch (IllegalArgumentException e) {
            //TODO java.lang.IllegalArgumentException: pointerIndex out of range
        }
        return superHandle;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        //如果有自动滑动，在手动滑动之前先清除
        if (mAutoScroll != null) {
            removeCallbacks(mAutoScroll);
        }

        if (isAtTop() && distanceY < 0) {
            // 可以下拉 并且 向下手势 = 拖动1
            doSwipeDown((int) distanceY);
            return true;
        }
        if (isAtBottom() && distanceY > 0) {
            // 可以上拉 并且 向上手势 = 拖动2
            doSwipeUp((int) distanceY);
            return true;
        }
        // FIXME denan.wang; 2014/9/30; 重点在于下拉过程中的上滑手势，屏蔽上滑过度的doSwipe调用，同理上拉
        if (inSwipe) {
            doSwipe((int) distanceY);
            if (getTop() == standard_top) {
                final long now = SystemClock.uptimeMillis();
                MotionEvent event = MotionEvent.obtain(now, now, MotionEvent.ACTION_DOWN, e2.getX(), e2.getY(), 0);
                super.dispatchTouchEvent(event);
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//        Log.e("--> onFling", "velocityY:" + velocityY);
        return false;
    }

    private int offsetY;

    private void scrollOffset(int dy) {
        offsetY += dy;
        this.offsetTopAndBottom(dy);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        // 重新定位参考值, 同时可以弥补有时OnGlobalLayoutListener()中getTop==0
        if (standard_top != t || standard_bottom != b) {
            setStandard(t, b);
        }
        this.offsetTopAndBottom(offsetY);
    }

    private void doSwipeDown(int dy) {
        // 通知 开始下拉 (“ && !inSwipe ” 开始状态 通知一次)
        if (mOnSwipeListeners != null && !inSwipe) {
            for (OnSwipeListener listener : mOnSwipeListeners) {
                listener.onSwipeDownStart(this);
            }
        }
        // 设置当前是下拉状态
        current_state = STATE_DOWN;
        doSwipe(dy);
    }

    private void doSwipeUp(int dy) {
        // 通知 开始上拉 (“ && !inSwipe ” 开始状态 通知一次)
        if (mOnSwipeListeners != null && !inSwipe) {
            for (OnSwipeListener listener : mOnSwipeListeners) {
                listener.onSwipeUpStart(this);
            }
        }
        // 设置当前是上拉状态
        current_state = STATE_UP;
        doSwipe(dy);
    }

    private boolean isNeedHold(int dy) {
        if (top_hold_dy != 0 && Math.abs(dy + top_hold_dy) < mResetRatio) {
            return true;
        }
        if (bottom_hold_dy != 0 && Math.abs(dy - bottom_hold_dy) < mResetRatio) {
            return true;
        }
        return false;
    }

    private boolean isNotOverHoldTarget(int dy) {
        if (top_hold_dy != 0 && -dy >= top_hold_dy) {
            return true;
        }
        if (bottom_hold_dy != 0 && dy >= bottom_hold_dy) {
            return true;
        }
        return false;
    }

    private void onHold() {
        // 取消自动复位
        this.removeCallbacks(resetSwipe);
        // 通知 滑动悬停
        if (mOnSwipeListeners != null) {
            if (current_state == STATE_UP) {
                for (OnSwipeListener listener : mOnSwipeListeners) {
                    listener.onSwipeUpHold(this);
                }
            } else if (current_state == STATE_DOWN) {
                for (OnSwipeListener listener : mOnSwipeListeners) {
                    listener.onSwipeDownHold(this);
                }
            }
        }
        // 悬停不改变状态
//        inSwipe = false;
//        current_state = STATE_HOLD;
    }


    private int last_top;

    /**
     * 带符号的偏移距离
     *
     * @param dy
     */
    private void doSwipe(int dy) {
        // 取消自动复位
        this.removeCallbacks(resetSwipe);

        last_top = getTop();
        // 预测滑动距离,修正
        int target_top = last_top - dy;
        // 设置偏移
        if (inSwipe && (last_top - standard_top) * (target_top - standard_top) <= 0) {
            //在滑动过程中,目标位置和上一次的位置在 临界点两边 时,修正滑动距离到临界点
            dy = last_top - standard_top;
        }

        inSwipe = true;
        //要判断对于大小，如果小于 系数，除出来小于0 就始终不会被偏移，就一直滑不动了
        if (Math.abs(dy) > mSwipeRatio) {
            dy = dy / mSwipeRatio;
        }

        this.scrollOffset(-dy);

        // 通知滑动
        if (mOnSwipeListeners != null) {
            // 返回与起始位置的实时距离
            for (OnSwipeListener listener : mOnSwipeListeners) {
                listener.onSwipe(this, standard_top - getTop(), -dy, false);
            }
        }

        //在滑动的过程中主动滑到了停止的临界值,
        if (standard_top == getTop()) {
            // 停止
            this.onSwipeComplete();
        }
    }

    private void onSwipeComplete() {
        // 通知 滑动停止
        if (mOnSwipeListeners != null) {
            if (current_state == STATE_UP) {
                for (OnSwipeListener listener : mOnSwipeListeners) {
                    listener.onSwipeUpComplete(this);
                }
            } else if (current_state == STATE_DOWN) {
                for (OnSwipeListener listener : mOnSwipeListeners) {
                    listener.onSwipeDownComplete(this);
                }
            }
        }
        // 设置当前是停止状态
        inSwipe = false;
        current_state = STATE_STOP;
    }

    private static final int DELAY = 10;

    /**
     * 自动复位
     */
    private Runnable resetSwipe = new Runnable() {
        public int reset_to_target;

        @Override
        public void run() {
            // 与起始位置的总距离
            int all_dy = standard_top - getTop();

            // 先判断是否需要悬停
            if (isNeedHold && isNotOverHoldTarget(all_dy)) {
                // 如果 需要悬停 并且 没有越过悬停位置,将此次复位的目标设为 悬停位置 (bottom 要为负)
                // 返回初始位置时都是以old_top为基准的
                reset_to_target = all_dy > 0 ? standard_top - bottom_hold_dy : standard_top + top_hold_dy;
            } else {
                // 否则还是 起始位置
                reset_to_target = standard_top;
                // 置为需要，不然置过一次之后便一直忽略了
                isNeedHold = true;
            }

            // 算偏移,是否停止
            int dy = reset_to_target - getTop();
            if (dy == 0) {
                if (reset_to_target == standard_top) {
                    // 停止
                    onSwipeComplete();
                } else {
                    // 悬停
                    onHold();
                }
                return;
            } else if (Math.abs(dy) > mResetRatio) {
                // 减缓复位速度,并且是dy能准确地减为0
                dy = dy / mResetRatio;
            } else {
                dy = dy > 0 ? 1 : -1;
            }

            scrollOffset(dy);

            // 通知滑动
            if (mOnSwipeListeners != null) {
                // 始终返回与起始位置的实时距离（无视目前的目标距离）
                for (OnSwipeListener listener : mOnSwipeListeners) {
                    listener.onSwipe(SweetScrollView.this, standard_top - getTop(), dy, true);
                }
            }

            postDelayed(this, DELAY);
        }
    };

    private static final int AUTO_SCROLL_SPEED = 20;

    private class AutoScroll implements Runnable {
        private boolean isScrollToTop;
        private boolean isFirstScroll;

        public AutoScroll(boolean isScrollToTop) {
            this.isScrollToTop = isScrollToTop;
            this.isFirstScroll = true;
        }

        //模拟下拉上拉手势，发送doSwipeDown，doSwipeUp消息，类似ACTION_MOVE
        @Override
        public void run() {
            if (isFirstScroll) {
                isFirstScroll = false;
                if (isScrollToTop) {
                    // 先滑到顶部
                    fullScroll(FOCUS_UP);
                } else {
                    // 先滑到底部
                    fullScroll(FOCUS_DOWN);
                }
            }

            if (isScrollToTop && top_hold_dy != 0) {
                int dy = standard_top - getTop();

                //下拉的距离都是负数
                if (dy == -top_hold_dy) {
                    onHold();
                    return;
                } else {
                    doSwipeDown(-AUTO_SCROLL_SPEED);
                    postDelayed(mAutoScroll, DELAY);
                }
            }
            if (!isScrollToTop && bottom_hold_dy != 0) {
                int dy = standard_top - getTop();

                if (dy == bottom_hold_dy) {
                    onHold();
                    return;
                } else {
                    doSwipeUp(AUTO_SCROLL_SPEED);
                    postDelayed(mAutoScroll, DELAY);
                }
            }
        }
    }

    private boolean isAtBottom() {
        if (current_state == STATE_DOWN) {
            return false;
        }
        return getScrollY() >= getChildAt(0).getBottom() + getPaddingBottom() - getHeight();
    }

    private boolean isAtTop() {
        if (current_state == STATE_UP) {
            return false;
        }

        return getScrollY() <= 0;
    }

    private ArrayList<OnSwipeListener> mOnSwipeListeners = null;

    @Override
    public void addSwipeMarkById(int id, int place, int hold_dy) {
        View v = getRootView().findViewById(id);
        if (v instanceof OnSwipeMarkCallBack) {
            OnSwipeMarkCallBack callback = (OnSwipeMarkCallBack) v;
            callback.setSwipeMarkPlace(place);
            this.setOnSwipeListener(callback);
            if (OnSwipeMarkCallBack.PLACE_TOP == place) {
                this.top_hold_dy = hold_dy;
            } else if (OnSwipeMarkCallBack.PLACE_BOTTOM == place) {
                this.bottom_hold_dy = hold_dy;
            }
        }
    }

    @Override
    public void setOnSwipeListener(OnSwipeListener listener) {
        if (mOnSwipeListeners == null) {
            mOnSwipeListeners = new ArrayList<OnSwipeListener>(1);
        }

        for (OnSwipeListener callback : mOnSwipeListeners) {
            if (callback instanceof OnSwipeMarkCallBack) {

            } else {
                mOnSwipeListeners.remove(callback);
            }
        }

        mOnSwipeListeners.add(listener);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mOnSwipeListeners != null) {
            mOnSwipeListeners.clear();
            mOnSwipeListeners = null;
        }
    }

    // 保持引用,如果在自动滑动的时候,发生主动拖动,要取消自动
    private AutoScroll mAutoScroll = null;

    @Override
    public void doSwipeToHold(boolean isTopHold, int delay) {
        if (isTopHold && top_hold_dy != 0) {
            mAutoScroll = new AutoScroll(isTopHold);
            removeCallbacks(resetSwipe);
            postDelayed(mAutoScroll, delay);
            return;
        }
        if (!isTopHold && bottom_hold_dy != 0) {
            mAutoScroll = new AutoScroll(isTopHold);
            removeCallbacks(resetSwipe);
            postDelayed(mAutoScroll, delay);
            return;
        }
    }

    private int top_hold_dy = 0, bottom_hold_dy = 0;

    @Override
    public void setNeedHold(int top_hold_dy, int bottom_hold_dy) {
        this.top_hold_dy = top_hold_dy;
        this.bottom_hold_dy = bottom_hold_dy;
    }

    // 在自动回弹的时候，是否忽略悬停
    boolean isNeedHold = true;

    @Override
    public void reset(String... args) {
        if (inSwipe) {
            isNeedHold = false;
            if (mAutoScroll != null) {
                removeCallbacks(mAutoScroll);
            }
            removeCallbacks(resetSwipe);
            postDelayed(resetSwipe, 0);
            if (args != null && args.length == 1 && args[0].equals("noCallBack")) {
                // 不回调
            } else {
                for (OnSwipeListener callback : mOnSwipeListeners) {
                    if (callback instanceof OnSwipeMarkCallBack) {
                        ((OnSwipeMarkCallBack) callback).onReset(this, args);
                    }
                }
            }
        }
    }

    private int mSwipeRatio = RATIO_LOW;

    @Override
    public void setSwipeRatio(int ratio) {
        mSwipeRatio = ratio;
    }

    @Override
    public int getSwipeRatio() {
        return mSwipeRatio;
    }

    private int mResetRatio = RATIO_HIGH;

    @Override
    public void setResetRatio(int ratio) {
        mResetRatio = ratio;
    }

    @Override
    public int getResetRatio() {
        return mResetRatio;
    }

    @Override
    public boolean isHold() {
        int dy = getTop() - standard_top;
        return (top_hold_dy != 0 && top_hold_dy == dy) || (bottom_hold_dy != 0 && bottom_hold_dy == -dy);
    }

    @Override
    public int getState() {
        return current_state;
    }

    @Override
    public boolean isOverHold() {
        return isNotOverHoldTarget(standard_top - getTop());
    }

}
