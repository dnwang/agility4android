package org.pinwheel.agility.view;

import android.view.View;

/**
 * 版权所有 (C), 2014 <br>
 * <br>
 * All rights reserved
 *
 * @author dnwang
 * @date 2014/9/29 21:28
 * @description
 */
interface Swipeable {

    public static final int RATIO_LOWx2 = 0x01;
    public static final int RATIO_LOW = 0x02;
    public static final int RATIO_MEDIUM = 0x04;
    public static final int RATIO_HIGH = 0x08;
    public static final int RATIO_HIGHx2 = 0x10; //16

    //    public static final int STATE_HOLD = 0x0A;
    public static final int STATE_STOP = 0x00;
    public static final int STATE_DOWN = 0x01;
    public static final int STATE_UP = 0x02;

    public void addSwipeMarkById(int id, int place, int hold_dy);

    public void setOnSwipeListener(OnSwipeListener listener);

    /**
     * @param isTopHold true:顶部悬停；false:底部悬停
     * @param delay     启动延迟
     * @author denan.wang
     * @date 2014/9/29
     * @description 主动滑动到悬停
     */
    public void doSwipeToHold(boolean isTopHold, int delay);

    /**
     * @param top_hold_dy    顶部悬停相对顶部的距离
     * @param bottom_hold_dy 底部悬停相对顶部的距离
     * @author denan.wang
     * @date 2014/9/29
     * @description 设置需要悬停
     */
    public void setNeedHold(int top_hold_dy, int bottom_hold_dy);

    /**
     * @author denan.wang
     * @date 2014/9/29
     * @description 复位
     */
    public void reset(String... args);

    /**
     * @author denan.wang
     * @date 2014/9/29
     * @description 设置拖动阻尼
     */
    public void setSwipeRatio(int ratio);

    public int getSwipeRatio();

    /**
     * @author denan.wang
     * @date 2014/9/29
     * @description 设置回弹阻尼
     */
    public void setResetRatio(int ratio);

    public int getResetRatio();

    /**
     * @author denan.wang
     * @date 2014/9/29
     * @description 获取当前状态
     */
    public int getState();

    /**
     * @author denan.wang
     * @date 2014/9/29
     * @description 当前是否悬停
     */
    public boolean isHold();

    public boolean isOverHold();

    /**
     * @author denan.wang
     * @date 2014/9/29
     * @description
     */
    public static interface OnSwipeListener {

        public void onSwipeDownStart(View v);

        public void onSwipeUpStart(View v);

        public void onSwipe(View v, int dy, int offset, boolean isAuto);

        public void onSwipeDownHold(View v);

        public void onSwipeUpHold(View v);

        public void onSwipeDownComplete(View v);

        public void onSwipeUpComplete(View v);

    }

    public static abstract class SwipeAdapter implements OnSwipeListener {

        @Override
        public void onSwipeDownStart(View v) {
        }

        @Override
        public void onSwipeUpStart(View v) {
        }

        @Override
        public void onSwipe(View v, int dy, int offset, boolean isAuto) {
        }

        @Override
        public void onSwipeDownHold(View v) {
        }

        @Override
        public void onSwipeUpHold(View v) {
        }

        @Override
        public void onSwipeDownComplete(View v) {
        }

        @Override
        public void onSwipeUpComplete(View v) {
        }
    }

    /**
     * @author denan.wang
     * @date 2014/12/10
     * @description
     */
    public static interface OnSwipeMarkCallBack extends OnSwipeListener {

        public static final int PLACE_TOP = 0x01;
        public static final int PLACE_BOTTOM = 0x02;

        public void setSwipeMarkPlace(int place);

        public void onReset(View v, String... args);

    }

}
