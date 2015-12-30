package org.pinwheel.agility.view;

import android.content.Context;
import android.graphics.Color;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Adapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
@Deprecated
public class SweetListView extends ListView implements Swipeable, GestureDetector.OnGestureListener {

    private boolean inSwipe;
    private int current_state;

    private int standard_top;
    private int standard_bottom;

    private GestureDetector gestureDetector;

    public SweetListView(Context context) {
        super(context);
        this.init(context);
    }

    public SweetListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(context);
    }

    public SweetListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.init(context);
    }

    private void init(Context context) {
        this.setCacheColorHint(Color.TRANSPARENT);
        this.setOverScrollMode(OVER_SCROLL_NEVER);
        this.gestureDetector = new GestureDetector(context, this);
        this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                setStandard(getTop(), getBottom());
                getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    private void setStandard(int top, int bottom) {
        standard_top = top;
        standard_bottom = bottom - standard_top;
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
        if (mAutoScroll != null) {
            removeCallbacks(mAutoScroll);
        }

        if (isAtTop() && distanceY < 0 && (Math.abs(distanceX) < Math.abs(distanceY))) {
            doSwipeDown((int) distanceY);
            return true;
        }
        if (isAtBottom() && distanceY > 0 && (Math.abs(distanceX) < Math.abs(distanceY))) {
            doSwipeUp((int) distanceY);
            return true;
        }
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
        if (standard_top != t || standard_bottom != b) {
            setStandard(t, b);
        }

        this.offsetTopAndBottom(offsetY);
    }

    private void doSwipeDown(int dy) {
        if (mOnSwipeListeners != null && !inSwipe) {
            for (OnSwipeListener listener : mOnSwipeListeners) {
                listener.onSwipeDownStart(this);
            }
        }
        current_state = STATE_DOWN;
        doSwipe(dy);
    }

    private void doSwipeUp(int dy) {
        if (mOnSwipeListeners != null && !inSwipe) {
            for (OnSwipeListener listener : mOnSwipeListeners) {
                listener.onSwipeUpStart(this);
            }
        }
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
        this.removeCallbacks(resetSwipe);
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
//        inSwipe = false;
//        current_state = STATE_HOLD;
    }

    private int last_top;

    private void doSwipe(int dy) {
        this.removeCallbacks(resetSwipe);

        last_top = getTop();
        int target_top = last_top - dy;
        if (inSwipe && (last_top - standard_top) * (target_top - standard_top) <= 0) {
            dy = last_top - standard_top;
        }

        inSwipe = true;
        if (Math.abs(dy) > mSwipeRatio) {
            dy = dy / mSwipeRatio;
        }

        this.scrollOffset(-dy);

        if (mOnSwipeListeners != null) {
            for (OnSwipeListener listeners : mOnSwipeListeners) {
                listeners.onSwipe(this, standard_top - getTop(), -dy, false);
            }
        }

        if (standard_top == getTop()) {
            this.onSwipeComplete();
        }
    }

    private void onSwipeComplete() {
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
        inSwipe = false;
        current_state = STATE_STOP;
    }

    private static final int DELAY = 10;

    private Runnable resetSwipe = new Runnable() {
        public int reset_to_target;

        @Override
        public void run() {
            int all_dy = standard_top - getTop();

            if (isNeedHold && isNotOverHoldTarget(all_dy)) {
                reset_to_target = all_dy > 0 ? standard_top - bottom_hold_dy : standard_top + top_hold_dy;
            } else {
                reset_to_target = standard_top;
                isNeedHold = true;
            }

            int dy = reset_to_target - getTop();
            if (dy == 0) {
                if (reset_to_target == standard_top) {
                    onSwipeComplete();
                } else {
                    onHold();
                }
                return;
            } else if (Math.abs(dy) > mResetRatio) {
                dy = dy / mResetRatio;
            } else {
                dy = dy > 0 ? 1 : -1;
            }

            scrollOffset(dy);

            if (mOnSwipeListeners != null) {
                for (OnSwipeListener listener : mOnSwipeListeners) {
                    listener.onSwipe(SweetListView.this, standard_top - getTop(), dy, true);
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

        @Override
        public void run() {
            if (isFirstScroll) {
                isFirstScroll = false;
                if (getAdapter() != null && getAdapter().getCount() > 1) {
                    if (isScrollToTop) {
                        setSelection(0);
                    } else {
                        setSelection(getAdapter().getCount() - 1);
                    }
                }
            }

            if (isScrollToTop && top_hold_dy != 0) {
                int dy = standard_top - getTop();
//                int offset = -AUTO_SCROLL_SPEED;

                if (dy == -top_hold_dy) {
                    onHold();
                    return;
                } else {
//                    doSwipeDown(offset);
                    doSwipeDown(-AUTO_SCROLL_SPEED);
                    postDelayed(mAutoScroll, DELAY);
                }
            }
            if (!isScrollToTop && bottom_hold_dy != 0) {
                int dy = standard_top - getTop();
//                int offset = AUTO_SCROLL_SPEED;

                if (dy == bottom_hold_dy) {
                    onHold();
                    return;
                } else {
//                    doSwipeUp(offset);
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

        final Adapter adapter = this.getAdapter();
        if (null == adapter || adapter.isEmpty()) {
            return true;
        }

        final int lastItemPosition = adapter.getCount() - 1;
        final int lastVisiblePosition = getLastVisiblePosition();
        if (lastVisiblePosition >= lastItemPosition) {
            final int childIndex = lastVisiblePosition - getFirstVisiblePosition();
            final int childCount = getChildCount();
            final int index = Math.min(childIndex, childCount - 1);
            final View lastVisibleChild = getChildAt(index);
            if (lastVisibleChild != null) {
                return lastVisibleChild.getBottom() <= standard_bottom - getPaddingBottom(); //padding
            }
        }
        return false;
    }

    private boolean isAtTop() {
        if (current_state == STATE_UP) {
            return false;
        }

        final Adapter adapter = this.getAdapter();
        if (null == adapter || adapter.isEmpty()) {
            return true;
        }
        int firstPos = getFirstVisiblePosition();
        if (firstPos == 0 && getChildAt(firstPos).getTop() >= getPaddingTop()) {//padding
            return true;
        }
        return false;
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
//        if (mOnSwipeListeners != null) {
//            mOnSwipeListeners.clear();
//            mOnSwipeListeners = null;
//        }
    }

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
