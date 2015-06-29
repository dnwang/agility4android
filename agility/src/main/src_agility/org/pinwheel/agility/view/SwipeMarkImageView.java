package org.pinwheel.agility.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

/**
 * All rights reserved
 *
 * @author dnwang
 * @date 2014/12/9 16:52
 * @description
 */
@Deprecated
class SwipeMarkImageView extends ImageView implements Swipeable.OnSwipeMarkCallBack {

    private static final int ROTATE_RATE = 10;

    private int rotat_radian;
    private int dy;

    public SwipeMarkImageView(Context context) {
        super(context);
        this.init();
    }

    public SwipeMarkImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    private void init() {

    }

    private Runnable autoRotate = new Runnable() {
        @Override
        public void run() {
            rotat_radian += 15;
            setRotation(rotat_radian);
            postDelayed(autoRotate, ROTATE_RATE);
        }
    };

    private boolean isNeedSwipe = false;

    @Override
    public void onSwipeDownStart(View v) {
        removeCallbacks(autoRotate);
        if (place == Swipeable.OnSwipeMarkCallBack.PLACE_TOP) {
            isNeedSwipe = true;
        } else {
            isNeedSwipe = false;
        }
    }

    @Override
    public void onSwipeUpStart(View v) {
        removeCallbacks(autoRotate);
        if (place == Swipeable.OnSwipeMarkCallBack.PLACE_BOTTOM) {
            isNeedSwipe = true;
        } else {
            isNeedSwipe = false;
        }
    }

    @Override
    public void onSwipe(View v, int dy, int offset, boolean isAuto) {
        if (!isNeedSwipe) {
            return;
        }
        this.rotat_radian += offset;
        this.dy = dy;

        this.offsetTopAndBottom(offset);
        this.setRotation(rotat_radian);
    }

    @Override
    public void onSwipeDownHold(View v) {
        removeCallbacks(autoRotate);
        post(autoRotate);
    }

    @Override
    public void onSwipeUpHold(View v) {
        removeCallbacks(autoRotate);
        post(autoRotate);
    }

    @Override
    public void onSwipeDownComplete(View v) {
        removeCallbacks(autoRotate);
    }

    @Override
    public void onSwipeUpComplete(View v) {
        removeCallbacks(autoRotate);
    }

    @Override
    public void onReset(View v, String... args) {
        removeCallbacks(autoRotate);
    }

    private int place;

    @Override
    public void setSwipeMarkPlace(int place) {
        this.place = place;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        this.offsetTopAndBottom(-dy);
    }
}
