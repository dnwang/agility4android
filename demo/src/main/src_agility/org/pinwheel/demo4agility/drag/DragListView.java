package org.pinwheel.demo4agility.drag;

import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;
import org.pinwheel.agility.util.UIUtils;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public class DragListView extends ListView implements Draggable {

    private int maxInertiaDistance; // 惯性越界最大距离
    private float restVelocity; // 复位速度
    private float inertiaVelocity; // 越界效果速度
    private float inertiaWeight; // 越界效果权重
    private float ratio; // 拖动阻尼 基数

    private DragHelper dragHelper;

    private final Movable mover = new Movable() {
        @Override
        public void move(float offset) {
            DragListView.this.setTranslationY(offset);
        }
    };

    public DragListView(Context context) {
        super(context);
        init();
    }

    public DragListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DragListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        this.maxInertiaDistance = UIUtils.dip2px(getContext(), 48);
        this.restVelocity = VELOCITY_NORMAL;
        this.inertiaVelocity = VELOCITY_FAST;
        this.inertiaWeight = WIGHT_INERTIA_NORMAL;
        this.ratio = RATIO_NORMAL;
        this.dragHelper = new DragHelper(mover);
        this.setOverScrollMode(OVER_SCROLL_NEVER);
    }

    @Deprecated
    @Override
    public void setOnItemLongClickListener(OnItemLongClickListener l) {
        super.setOnItemLongClickListener(l);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isTouchSwipe = false; // 暂时没用
                lastPoint.set(event.getRawX(), event.getRawY());
                if (dragHelper.isHolding()) {
                    // 正在Hold时,点击应该rest,并且无事件响应
                    resetToBorder(restVelocity);
                    return false;
                } else {
                    super.dispatchTouchEvent(event);
                    return true;
                }
            default:
                return super.dispatchTouchEvent(event);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 将DOWN的处理放置dispatchTouchEvent中,点击header时才有效
                return super.onTouchEvent(event);
            case MotionEvent.ACTION_MOVE:
                // BEGIN;计算当前手势偏移距离
                final float yDiff = event.getRawY() - lastPoint.y;
                final float absYDiff = Math.abs(yDiff);
                lastPoint.set(event.getRawX(), event.getRawY());
                // END;计算当前手势偏移距离

                if (isTouchSwipe && dragHelper.isDragging()) {
                    // 是手动越界拖动 并且 状态已经定义为 滑动
                    final float oldDy = getDistance();

                    // BEGIN;计算真实需要滑动的距离
                    float offset;
                    if (oldDy * yDiff < 0) {
                        // 与之前的滑动发现 逆向,说明是 "下拉中上拉"/"上拉中下拉"
                        offset = yDiff;
                    } else {
                        // 已经滑动总距离 与 即将滑动的距离 同向,说明是滑动的延续,需要计算阻尼
                        offset = yDiff / (Math.abs(oldDy) / 100 + ratio);
                    }
                    // END;计算真实需要滑动的距离

                    // 提前计算新的总距离,并且判断新距离时候可以被应用,若达到边界需要响应 list 本身的滚动
                    final float newDy = oldDy + offset;
                    if ((Math.abs(newDy) < 1.0f) || (newDy * oldDy < 0)) { // 下一个距离 和 当前距离 反向 也 视为 到边界
                        // 即将滑到 边界位置,此时应该响应 list 本身的滑动事件
                        return super.onTouchEvent(event);
                    } else {
                        // 应用新的距离,产生滑动
                        move(offset);
                        return true;
                    }
                } else {
                    return super.onTouchEvent(event);
                }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
                if (isTouchSwipe && dragHelper.isDragging()) {
                    // 手动拖动 释放
                    if (dragHelper.isOverHoldPosition()) {
                        switch (getState()) {
                            case STATE_DRAGGING_TOP:
                                hold(true, restVelocity);
                                break;
                            case STATE_DRAGGING_BOTTOM:
                                hold(false, restVelocity);
                                break;
                            default:
                                resetToBorder(restVelocity);
                                break;
                        }
                    } else {
                        resetToBorder(restVelocity);
                    }
                }
                isTouchSwipe = false;
                return super.onTouchEvent(event);
            default:
                return super.onTouchEvent(event);
        }
    }

    private final PointF lastPoint = new PointF();
    private boolean isTouchSwipe;

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        int absDeltaY = Math.abs(deltaY);
        if (absDeltaY <= 0) {
            return false;
        }
        isTouchSwipe = isTouchEvent;
        // 通过deltaY判断是下拉还是上拉,设置好越界状态,在onTouchEvent中需要用到
        if (!dragHelper.isDragging()) {
            // 添加判断，确保不重复设置状态，重复通知
            setState(deltaY > 0 ? Draggable.STATE_DRAGGING_BOTTOM : Draggable.STATE_DRAGGING_TOP);
        }
        // 无论是手动越界 还是 惯性越界 都说明有边界触发,并且deltaY值越大 越界效果越强
        if (isTouchEvent) {
            // 手动拖动越界 在toucheEvent中处理
        } else {
            deltaY /= inertiaWeight;
            if (Math.abs(deltaY) > 10) {
                // 惯性越界在这里根据 deltaY的值 计算自动滑动的距离
                // 惯性越界 需要最大限制,某些时候系统会返回很大的值,此时需要屏蔽
                deltaY = deltaY < 0 ? Math.max(-maxInertiaDistance, deltaY) : Math.min(deltaY, maxInertiaDistance);
                inertial(-deltaY, inertiaVelocity);
            }
        }
        return false;
    }

    public int getMaxInertiaDistance() {
        return maxInertiaDistance;
    }

    public void setMaxInertiaDistance(int maxInertiaDistance) {
        this.maxInertiaDistance = Math.max(0, maxInertiaDistance);
    }

    public float getRestVelocity() {
        return restVelocity;
    }

    public void setRestVelocity(float restVelocity) {
        this.restVelocity = Math.max(0, restVelocity);
    }

    public float getInertiaVelocity() {
        return inertiaVelocity;
    }

    public void setInertiaVelocity(float inertiaVelocity) {
        this.inertiaVelocity = Math.max(0, inertiaVelocity);
    }

    public float getInertiaWeight() {
        return inertiaWeight;
    }

    public void setInertiaWeight(float inertiaWeight) {
        this.inertiaWeight = Math.max(0, inertiaWeight);
    }

    public void setRatio(int ratio) {
        this.ratio = Math.max(0, ratio);
    }

    public float getRatio() {
        return ratio;
    }

    @Override
    public void hold(boolean isTopPosition, float velocity) {
        dragHelper.hold(isTopPosition, velocity);
    }

    @Override
    public void resetToBorder(float velocity) {
        dragHelper.resetToBorder(velocity);
    }

    @Override
    public void inertial(int distance, float velocity) {
        dragHelper.inertial(distance, velocity);
    }

    @Override
    public void move(float offset) {
        dragHelper.move(offset);
    }

    @Override
    public void stopMove() {
        dragHelper.stopMove();
    }

    @Override
    public void setOnDragListener(Draggable.OnDragListener listener) {
        dragHelper.setOnDragListener(listener);
    }

    @Override
    public void setOrientation(int orientation) {
        dragHelper.setOrientation(orientation);
    }

    @Override
    public int getOrientation() {
        return dragHelper.getOrientation();
    }

    @Override
    public void setHoldDistance(int dTop, int dBottom) {
        dragHelper.setHoldDistance(dTop, dBottom);
    }

    @Override
    public int getTopHoldDistance() {
        return dragHelper.getTopHoldDistance();
    }

    @Override
    public int getBottomHoldDistance() {
        return dragHelper.getBottomHoldDistance();
    }

    @Override
    public void setState(int state) {
        dragHelper.setState(state);
    }

    @Override
    public int getState() {
        return dragHelper.getState();
    }

    @Override
    public void setPosition(int position) {
        dragHelper.setPosition(position);
    }

    @Override
    public int getPosition() {
        return dragHelper.getPosition();
    }

    @Override
    public float getDistance() {
        return dragHelper.getDistance();
    }

}
