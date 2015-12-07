package org.pinwheel.demo4agility.drag;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public interface Draggable {

    int STATE_NONE = 100;
    int STATE_HOLD = 101;
    int STATE_DRAGGING_TOP = 110;
    int STATE_DRAGGING_BOTTOM = 111;
    int STATE_RESTING_TO_HOLD = 120;
    int STATE_RESTING_TO_BORDER = 121;

    int EDGE_NONE = 200;
    int EDGE_TOP = 201;
    int EDGE_BOTTOM = 202;

    float RATIO_LOW = 1.0f;
    float RATIO_NORMAL = 2.0f;
    float RATIO_HEIGHT = 4.0f;

    float WIGHT_INERTIA_LOW = 1.0f;
    float WIGHT_INERTIA_NORMAL = 2.0f;
    float WIGHT_INERTIA_HEIGHT = 3.0f;

    float VELOCITY_SLOW = 0.4f;
    float VELOCITY_NORMAL = 0.6f;
    float VELOCITY_FAST = 0.8f;

    void hold(boolean isTopPosition, float velocity);

    void resetToBorder(float velocity);

    void inertial(int distance, float velocity);

    void move(float offset);

    void stopMove();

    void setOnDragListener(OnDragListener listener);

    void setOrientation(int orientation);

    int getOrientation();

    void setHoldDistance(int dTop, int dBottom);

    int getTopHoldDistance();

    int getBottomHoldDistance();

    void setState(int state);

    int getState();

    void setPosition(int position);

    int getPosition();

    float getDistance();

    /**
     * Copyright (C), 2015 <br>
     * <br>
     * All rights reserved <br>
     * <br>
     *
     * @author dnwang
     */
    interface OnDragListener {
        void onDragStateChanged(int position, int state);

        void onDragging(float distance, float offset);
    }
}
