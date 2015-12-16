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
    /**
     * 状态:无
     */
    int STATE_NONE = 100;
    /**
     * 状态:悬停
     */
    int STATE_HOLD = 101;
    /**
     * 状态:顶部拖动
     */
    int STATE_DRAGGING_TOP = 110;
    /**
     * 状态:底部拖动
     */
    int STATE_DRAGGING_BOTTOM = 111;
    /**
     * 状态:正在返回至悬停位置
     */
    int STATE_RESTING_TO_HOLD = 120;
    /**
     * 状态:正在返回至边界
     */
    int STATE_RESTING_TO_BORDER = 121;
    /**
     * 状态:越界
     */
    int STATE_INERTIAL = 130;
    /**
     * 边界位置:无
     */
    int EDGE_NONE = 200;
    /**
     * 边界位置:顶部
     */
    int EDGE_TOP = 201;
    /**
     * 边界位置:底部
     */
    int EDGE_BOTTOM = 202;
    /**
     * 阻尼系数:低
     */
    float RATIO_LOW = 1.0f;
    /**
     * 阻尼系数:普通
     */
    float RATIO_NORMAL = 2.0f;
    /**
     * 阻尼系数:高(手势拖动距离远大于视图移动距离)
     */
    float RATIO_HEIGHT = 4.0f;
    /**
     * 越界权重:低
     */
    float WIGHT_INERTIA_LOW = 1.0f;
    /**
     * 越界权重:普通
     */
    float WIGHT_INERTIA_NORMAL = 2.0f;
    /**
     * 越界权重:高(越界程度不明显)
     */
    float WIGHT_INERTIA_HEIGHT = 3.0f;
    /**
     * 自动移动速度:慢
     */
    float VELOCITY_SLOW = 0.2f;
    /**
     * 自动移动速度:普通
     */
    float VELOCITY_NORMAL = 0.4f;
    /**
     * 自动移动速度:快(在很短的事件内移动指定距离)
     */
    float VELOCITY_FAST = 0.6f;

    // Drag behavior

    /**
     * 悬停
     *
     * @param isTopPosition 是否头部悬停
     */
    void hold(boolean isTopPosition);

    /**
     * 返回到边界
     */
    void resetToBorder();

    /**
     * 越界
     *
     * @param distance 越界距离
     */
    void inertial(int distance);

    /**
     * 移动
     *
     * @param offset 偏移量
     */
    void move(float offset);

    /**
     * 停止自动移动
     */
    void stopMove();

    /**
     * 添加拖动事件监听
     *
     * @param listener 监听器
     */
    void addOnDragListener(OnDragListener listener);

    /**
     * 移除拖动事件监听
     *
     * @param listener 监听器
     */
    void removeOnDragListener(OnDragListener listener);

    /**
     * 设置拖动反向(预留)
     *
     * @param orientation 反向
     */
    @Deprecated
    void setOrientation(int orientation);

    /**
     * 获取拖动反向
     */
    @Deprecated
    int getOrientation();

    /**
     * 设置悬停距离
     *
     * @param dTop    顶部悬停距离
     * @param dBottom 底部悬停距离
     */
    void setHoldDistance(int dTop, int dBottom);

    /**
     * 获取顶部悬停距离
     */
    int getTopHoldDistance();

    /**
     * 获取底部悬停距离
     */
    int getBottomHoldDistance();

    /**
     * 是否越过悬停位置
     */
    boolean isOverHoldPosition();

    /**
     * 设置拖动事件状态
     */
    void setState(int state);

    /**
     * 获取拖动事件状态
     */
    int getState();

    /**
     * 设置拖动位置
     */
    void setPosition(int position);

    /**
     * 获取拖动位置
     */
    int getPosition();

    /**
     * 获取拖动距离
     */
    float getDistance();

    // Drag params

    /**
     * 获取最大越界距离
     */
    int getMaxInertiaDistance();

    /**
     * 设置最大越界距离
     */
    void setMaxInertiaDistance(int maxInertiaDistance);

    /**
     * 获取拖动释放返回速度
     */
    float getResetVelocity();

    /**
     * 设置拖动释放返回速度
     */
    void setResetVelocity(float resetVelocity);

    /**
     * 获取越界速度
     */
    float getInertiaVelocity();

    /**
     * 设置越界速度
     */
    void setInertiaVelocity(float inertiaVelocity);

    /**
     * 获取越界权重
     */
    float getInertiaWeight();

    /**
     * 设置越界权重
     */
    void setInertiaWeight(float inertiaWeight);

    /**
     * 获取越界返回速度
     */
    float getInertiaResetVelocity();

    /**
     * 设置越界返回速度
     */
    void setInertiaResetVelocity(float inertiaResetVelocity);

    /**
     * 设置阻尼系数
     */
    void setRatio(int ratio);

    /**
     * 获取阻尼系数
     */
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
        /**
         * 拖动状态改变回调
         *
         * @param draggable 当前拖动对象
         * @param position  当前位置
         * @param state     当前状态
         */
        void onDragStateChanged(Draggable draggable, int position, int state);

        /**
         * 拖动距离变化回调
         *
         * @param draggable 当前拖动对象
         * @param distance  当前拖动总距离
         * @param offset    当前移动偏移量
         */
        void onDragging(Draggable draggable, float distance, float offset);
    }

}
