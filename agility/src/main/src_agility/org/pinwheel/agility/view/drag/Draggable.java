package org.pinwheel.agility.view.drag;

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
    int STATE_INERTIAL = 130;

    int EDGE_NONE = 200;
    int EDGE_TOP = 201;
    int EDGE_BOTTOM = 202;

    float RATIO_LOW = 1.0f;
    float RATIO_NORMAL = 2.0f;
    float RATIO_HEIGHT = 4.0f;

    float WIGHT_INERTIA_LOW = 1.0f;
    float WIGHT_INERTIA_NORMAL = 2.0f;
    float WIGHT_INERTIA_HEIGHT = 3.0f;


    float VELOCITY_SLOW = 0.2f;
    float VELOCITY_NORMAL = 0.4f;
    float VELOCITY_FAST = 0.6f;

    // Drag behavior

    void hold(boolean isTopPosition);

    void resetToBorder();

    void inertial(int distance);

    void move(float offset);

    void stopMove();

    void addOnDragListener(OnDragListener listener);

    void removeOnDragListener(OnDragListener listener);

    @Deprecated
    void setOrientation(int orientation);

    @Deprecated
    int getOrientation();

    void setHoldDistance(int dTop, int dBottom);

    int getTopHoldDistance();

    int getBottomHoldDistance();

    boolean isOverHoldPosition();

    void setState(int state);

    int getState();

    void setPosition(int position);

    int getPosition();

    float getDistance();

    // Drag params

    int getMaxInertiaDistance();

    void setMaxInertiaDistance(int maxInertiaDistance);

    float getResetVelocity();

    void setResetVelocity(float resetVelocity);

    float getInertiaVelocity();

    void setInertiaVelocity(float inertiaVelocity);

    float getInertiaWeight();

    void setInertiaWeight(float inertiaWeight);

    float getInertiaResetVelocity();

    void setInertiaResetVelocity(float inertiaResetVelocity);

    void setRatio(int ratio);

    float getRatio();

    // Drag callback

    /**
     * Copyright (C), 2015 <br>
     * <br>
     * All rights reserved <br>
     * <br>
     *
     * @author dnwang
     */
    interface OnDragListener {
        void onDragStateChanged(Draggable draggable, int position, int state);

        void onDragging(Draggable draggable, float distance, float offset);
    }

}
