package org.pinwheel.agility.view.drag;

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

    private static final int INERTIA_SLOP = 5;

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
        this.dragHelper = new DragHelper(mover);
        // 必须设置此属性,否则对overScrollBy调用次数产生影响
        this.setOverScrollMode(OVER_SCROLL_NEVER);
        this.setMaxInertiaDistance(UIUtils.dip2px(getContext(), 48));
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 必须在dispatchTouchEvent记录DOWN位置,点击header时才有效
                lastPoint.set(event.getRawX(), event.getRawY());
                if (dragHelper.isHolding()) {
                    // 正在Hold时,点击应该rest,并且无事件响应
                    resetToBorder();
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
        final int state = getState();
        final float oldDy = getDistance();
        final float absOldDy = Math.abs(oldDy);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                final boolean downSuperState = super.onTouchEvent(event);
                isTouchDragging = false;
                return downSuperState;
            case MotionEvent.ACTION_MOVE:
                // BEGIN;计算当前手势偏移距离
                final float yDiff = event.getRawY() - lastPoint.y;
//                final float xDiff = event.getRawX() - lastPoint.x;
//                final float absYDiff = Math.abs(yDiff);
//                final float absXDiff = Math.abs(xDiff);
                lastPoint.set(event.getRawX(), event.getRawY());
                // END;计算当前手势偏移距离

                // 是手动越界拖动
                if (isTouchDragging) {
                    // BEGIN;计算真实需要滑动的距离
                    float offset;
                    if (oldDy * yDiff < 0) {
                        // 与之前的滑动发现 逆向,说明是 "下拉中上拉"/"上拉中下拉"
                        offset = yDiff;
                        // 如果即将产生的偏移大于 目前拖动的总距离, 那移动的距离不能超过总距离（当超过时，后续的判断会将滑动出现bug）
                        if (Math.abs(yDiff) > absOldDy) {
                            offset = (yDiff > 0 ? absOldDy : -absOldDy);
                        }
                    } else {
                        // 已经滑动总距离 与 即将滑动的距离 同向,说明是滑动的延续,需要计算阻尼
                        offset = yDiff / (Math.abs(oldDy) / 100 + getRatio());
                    }
                    // END;计算真实需要滑动的距离

                    // 提前计算新的总距离,并且判断新距离时候可以被应用,若达到边界需要响应 list 本身的滚动
                    final float newDy = oldDy + offset;
                    // 下一个距离 和 当前距离 反向 都视为到边界
                    if (state != STATE_NONE && ((Math.abs(newDy) < 1.0f && absOldDy > 0) || (newDy * oldDy < 0 && absOldDy > 0))) {
                        // 即将放弃滑动而响应list事件,将还未滑到边界的部分清0
                        move(-oldDy);
                        // 滑动到边界时将状态置为NONE
                        setState(STATE_NONE);
                        // 即将滑到 边界位置,此时应该响应 list 本身的滑动事件
                        // 这里注意调用顺序,1-2-3;2在1之后是保证overScrollBy函数中第一句判断起效,因为调用super之后回响应到overScrollBy,而恰好这次调用应该被屏蔽
                        // 3是保证返回super的响应状态
                        final boolean moveSuperState = super.onTouchEvent(event); // (1)
                        isTouchDragging = false; // (2)
                        return moveSuperState; // (3)
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
                if (isTouchDragging && absOldDy > 0) {
                    // 手动拖动 释放
                    if (isOverHoldPosition()) {
                        switch (state) {
                            case STATE_DRAGGING_TOP:
                                hold(true);
                                break;
                            case STATE_DRAGGING_BOTTOM:
                                hold(false);
                                break;
                            default:
                                resetToBorder();
                                break;
                        }
                    } else {
                        resetToBorder();
                    }
                }
                // 清除 手动拖动 状态
                final boolean cancelSuperState = super.onTouchEvent(event);
                isTouchDragging = false;
                return cancelSuperState;
            default:
                return super.onTouchEvent(event);
        }
    }

    private final PointF lastPoint = new PointF();
    private boolean isTouchDragging;

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        if (isTouchDragging || Math.abs((int) getDistance()) > 0) {
            return false;
        }
        int absDeltaY = Math.abs(deltaY);
        if (absDeltaY <= 0) {
            return false;
        }
        isTouchDragging = isTouchEvent;
        // 通过deltaY判断是下拉还是上拉,设置好越界状态,在onTouchEvent中需要用到
        if (STATE_NONE == getState()) {
            // 添加判断,确保不重复设置状态,重复通知
            setState(deltaY > 0 ? STATE_DRAGGING_BOTTOM : STATE_DRAGGING_TOP);
        }
        // 无论是手动越界 还是 惯性越界 都说明有边界触发,并且deltaY值越大 越界效果越强
        if (isTouchEvent) {
            // 手动拖动越界 在toucheEvent中处理
        } else {
            final int maxInertiaDistance = getMaxInertiaDistance();
            if (maxInertiaDistance > 0) {
                deltaY /= getInertiaWeight();
                if (Math.abs(deltaY) > INERTIA_SLOP) {
                    // 惯性越界在这里根据 deltaY的值 计算自动滑动的距离
                    // 惯性越界 需要最大限制,某些时候系统会返回很大的值,此时需要屏蔽
                    deltaY = deltaY < 0 ? Math.max(-maxInertiaDistance, deltaY) : Math.min(deltaY, maxInertiaDistance);
                    inertial(-deltaY);
                }
            }
        }
        return false;
    }

    @Override
    public int getMaxInertiaDistance() {
        return dragHelper.getMaxInertiaDistance();
    }

    @Override
    public void setMaxInertiaDistance(int maxInertiaDistance) {
        dragHelper.setMaxInertiaDistance(maxInertiaDistance);
    }

    @Override
    public float getResetVelocity() {
        return dragHelper.getResetVelocity();
    }

    @Override
    public void setResetVelocity(float resetVelocity) {
        dragHelper.setResetVelocity(resetVelocity);
    }

    @Override
    public float getInertiaVelocity() {
        return dragHelper.getInertiaVelocity();
    }

    @Override
    public void setInertiaVelocity(float inertiaVelocity) {
        dragHelper.setInertiaVelocity(inertiaVelocity);
    }

    @Override
    public float getInertiaWeight() {
        return dragHelper.getInertiaWeight();
    }

    @Override
    public void setInertiaWeight(float inertiaWeight) {
        dragHelper.setInertiaWeight(inertiaWeight);
    }

    @Override
    public float getInertiaResetVelocity() {
        return dragHelper.getInertiaResetVelocity();
    }

    @Override
    public void setInertiaResetVelocity(float inertiaResetVelocity) {
        dragHelper.setInertiaResetVelocity(inertiaResetVelocity);
    }

    @Override
    public void setRatio(int ratio) {
        dragHelper.setRatio(ratio);
    }

    @Override
    public float getRatio() {
        return dragHelper.getRatio();
    }

    @Override
    public boolean isOverHoldPosition() {
        return dragHelper.isOverHoldPosition();
    }

    @Override
    public void hold(boolean isTopPosition) {
        setSelection(isTopPosition ? 0 : (getAdapter() == null ? 0 : getAdapter().getCount() - 1));
        dragHelper.hold(isTopPosition);
    }

    @Override
    public void resetToBorder() {
        dragHelper.resetToBorder();
    }

    @Override
    public void inertial(int distance) {
        dragHelper.inertial(distance);
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
    public void addOnDragListener(Draggable.OnDragListener listener) {
        dragHelper.addOnDragListener(listener);
    }

    @Override
    public void removeOnDragListener(Draggable.OnDragListener listener) {
        dragHelper.removeOnDragListener(listener);
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

    @Deprecated
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
