package org.pinwheel.agility.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import org.pinwheel.agility.util.ex.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Copyright (C), 2016 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public class SweetCircularView extends ViewGroup {

    private static final String TAG = SweetCircularView.class.getSimpleName();
    private static final boolean debug = false;

    private static void log(String log) {
        if (debug) {
            Log.d(TAG, log);
        }
    }

    private static final int MOVE_SLOP = 10;

    private static final int DEFAULT_ITEM_SIZE = 5;

    /**
     * 是否自动循环
     */
    private boolean isAutoCycle = false;
    /**
     * 自动循环方向；true向后/false向前
     */
    private boolean isAutoCycleToNext = true;
    /**
     * 自动循环时间间隔
     */
    private long intervalOnAutoCycle = 4000;
    /**
     * 自动循环滑动耗时
     */
    private long durationOnAutoScroll = 300;
    /**
     * 手动滑动释放之后自动归位耗时
     */
    private long durationOnTouchRelease = 200;
    /**
     * 判断滑过一个视图的敏感度；默认0.5f滑动超过视图一半释放即自动滑过
     */
    private float sensibility = 0.5f;
    /**
     * 布局方向
     */
    private int orientation = LinearLayout.HORIZONTAL;
    /**
     * 视图之间的间隔
     */
    private int spaceBetweenItems = 0;
    /**
     * 中心视图 相对与父控件的间隔；默认0铺满整个父控件
     */
    private int leftIndent, topIndent, rightIndent, bottomIndent;
    /**
     * 点击非选中视图，自动选中
     */
    private boolean isClick2Selected = true;

    private OnItemScrolledListener onItemScrolledListener;
    private OnItemSelectedListener onItemSelectedListener;
    private AnimationAdapter animationAdapter;
    private IIndicator indicator;

    private AdapterDataSetObserver dataSetObserver;
    private BaseAdapter adapter;
    private ArrayList<ItemWrapper> items = null;

    /**
     * 基准位置，在任意试图滑动结束之后，重新排列的位置，
     * 用centerItemIndex形成对应使用
     */
    private Rect[] itemsBounds = null;

    private final Runnable autoCycleRunnable = new Runnable() {
        @Override
        public void run() {
            if (isShown() && adapter != null && adapter.getCount() > 0) {
                moveItems(isAutoCycleToNext ? 1 : -1);
            }
            postDelayed(this, intervalOnAutoCycle);
        }
    };

    public SweetCircularView(Context context) {
        super(context);
        init();
    }

    public SweetCircularView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SweetCircularView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        resetItems(DEFAULT_ITEM_SIZE);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        isAttachedToWindow = true;
        resumeAutoCycle();
    }

    private boolean isAttachedToWindow = false;

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isAttachedToWindow = false;
        interceptAutoCycle();
    }

    public final int getCurrentIndex() {
        ItemWrapper centerItem = findItem(getRecycleItemSize() / 2);
        return null != centerItem ? centerItem.dataIndex : -1;
    }

    public final SweetCircularView setCurrentIndex(int dataIndex) {
        updateAllItemView(dataIndex);
        // notify
        notifyOnItemSelected();
        return this;
    }

    public SweetCircularView setAdapter(BaseAdapter cycleAdapter) {
        if (adapter != null) {
            adapter.unregisterDataSetObserver(dataSetObserver);
        }
        if (cycleAdapter != null) {
            dataSetObserver = new AdapterDataSetObserver();
            cycleAdapter.registerDataSetObserver(dataSetObserver);
        }
        adapter = cycleAdapter;
        return this;
    }

    public BaseAdapter getAdapter() {
        return adapter;
    }

    public SweetCircularView setSensibility(float sensibility) {
        this.sensibility = Math.max(0, Math.min(1.0f, sensibility));
        return this;
    }

    public float getSensibility() {
        return this.sensibility;
    }

    public SweetCircularView setOrientation(int orientation) {
        this.orientation = orientation == LinearLayout.VERTICAL ? orientation : LinearLayout.HORIZONTAL;
        requestLayout();
        return this;
    }

    public int getOrientation() {
        return orientation;
    }

    public SweetCircularView setDurationOnTouchRelease(long duration) {
        durationOnTouchRelease = Math.max(0, duration);
        return this;
    }

    public long getDurationOnTouchRelease() {
        return durationOnTouchRelease;
    }

    public SweetCircularView setDurationOnAutoScroll(long duration) {
        durationOnAutoScroll = Math.max(0, duration);
        return this;
    }

    public long getDurationOnAutoScroll() {
        return durationOnAutoScroll;
    }

    public SweetCircularView setIntervalOnAutoCycle(long interval) {
        intervalOnAutoCycle = Math.max(0, interval);
        return this;
    }

    public long getIntervalOnAutoCycle() {
        return intervalOnAutoCycle;
    }

    public boolean isAutoCycle() {
        return isAutoCycle;
    }

    public SweetCircularView setAutoCycle(boolean is, boolean moveToNext) {
        isAutoCycle = is;
        isAutoCycleToNext = moveToNext;
        if (is) {
            if (isAttachedToWindow) {
                // auto start when already attached to window
                resumeAutoCycle();
            }
        } else {
            interceptAutoCycle();
        }
        return this;
    }

    public SweetCircularView setIndent(int left, int top, int right, int bottom) {
        leftIndent = left;
        topIndent = top;
        rightIndent = right;
        bottomIndent = bottom;
        requestLayout();
        return this;
    }

    public SweetCircularView setSpaceBetweenItems(int space) {
        spaceBetweenItems = space;
        requestLayout();
        return this;
    }

    public int getSpaceBetweenItems() {
        return spaceBetweenItems;
    }

    public SweetCircularView setClick2Selected(boolean isClick2Selected) {
        this.isClick2Selected = isClick2Selected;
        return this;
    }

    public boolean isClick2Selected() {
        return isClick2Selected;
    }

    /**
     * < 0 向前
     * > 0 向后
     */
    public final SweetCircularView moveItems(final int changed) {
        if (!isMoving && 0 != changed) {
            int offset = (getItemBoundWidth() + spaceBetweenItems) * changed;
            log("moveItems: 主动: offset:" + (offset));
            autoMove(offset, durationOnAutoScroll, new Runnable() {
                @Override
                public void run() {
                    autoPacking();
                }
            });
        }
        return this;
    }

    public SweetCircularView setOnItemScrolledListener(OnItemScrolledListener scrolledListener) {
        this.onItemScrolledListener = scrolledListener;
        return this;
    }

    public SweetCircularView setOnItemSelectedListener(OnItemSelectedListener selectedListener) {
        this.onItemSelectedListener = selectedListener;
        return this;
    }

    /**
     * 设置滑动动画适配器
     */
    public SweetCircularView setAnimationAdapter(AnimationAdapter animationAdapter) {
        if (null != this.animationAdapter) {
            this.animationAdapter.circularView = null;
        }
        this.animationAdapter = animationAdapter;
        if (null != this.animationAdapter) {
            this.animationAdapter.circularView = this;
        }
        return this;
    }

    /**
     * 设置指示器
     */
    public SweetCircularView setIndicator(IIndicator indicator) {
        this.indicator = indicator;
        return this;
    }

    protected void notifyOnItemScrolled(int offset) {
        ItemWrapper centerItem = findItem(getRecycleItemSize() / 2);
        if (null == centerItem) {
            return;
        }
        // 回调监听器
        if (null != onItemScrolledListener) {
            onItemScrolledListener.onItemScrolled(this, centerItem.dataIndex, offset);
        }
        // 回调动画适配器
        if (null != animationAdapter) {
            animationAdapter.onScrolled(offset);
        }
    }

    /**
     * 仅用于在回调时去重
     */
    private int lastCenterItemDataIndex = -1;

    protected void notifyOnItemSelected() {
        ItemWrapper centerItem = findItem(getRecycleItemSize() / 2);
        if (null == centerItem) {
            return;
        }
        int centerDataIndex = centerItem.dataIndex;
        log("选中: " + centerDataIndex);
        if (lastCenterItemDataIndex != centerDataIndex) {
            // 回调监听器
            if (null != onItemSelectedListener) {
                onItemSelectedListener.onItemSelected(this, centerDataIndex);
            }
            // 回调指示器
            if (null != indicator) {
                indicator.setCurrentIndex(centerDataIndex);
            }
            lastCenterItemDataIndex = centerDataIndex;
        }
    }

    private void resumeAutoCycle() {
        if (isAutoCycle) {
            removeCallbacks(autoCycleRunnable);
            postDelayed(autoCycleRunnable, intervalOnAutoCycle);
        }
    }

    private void interceptAutoCycle() {
        removeCallbacks(autoCycleRunnable);
    }

    protected final void resetItems(int size) {
        items = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            items.add(new ItemWrapper(i));
        }
    }

    /**
     * 平铺所有子试图，根据中心试图的left，top，right，bottom左右平均分布每个子试图
     */
    private void resetItemsBounds(int centerLeft, int centerTop, int centerRight, int centerBottom, int space) {
        itemsBounds = new Rect[getRecycleItemSize()];
        final int centerIndex = itemsBounds.length / 2;
        int left, top, right, bottom;
        int m;
        for (int i = 0; i < itemsBounds.length; i++) {
            Rect rect = new Rect();
            m = centerIndex - i;
            // 0,1,2, center, 4,5,6
            if (orientation == LinearLayout.VERTICAL) {
                left = centerLeft;
                top = centerTop - m * (centerBottom - centerTop + space);
                right = centerRight;
                bottom = top + (centerBottom - centerTop);
            } else { // LinearLayout.HORIZONTAL
                left = centerLeft - m * (centerRight - centerLeft + space);
                top = centerTop;
                right = left + (centerRight - centerLeft);
                bottom = centerBottom;
            }
            rect.set(left, top, right, bottom);
            itemsBounds[i] = rect;
        }
    }

    private int getItemBoundWidth() {
        return itemsBounds[0].width();
    }

    private int getItemBoundHeight() {
        return itemsBounds[0].height();
    }

    protected final int getRecycleItemSize() {
        return items.size();
    }

    protected final ItemWrapper findItem(int itemIndex) {
        for (int i = 0; i < getRecycleItemSize(); i++) {
            ItemWrapper item = items.get(i);
            if (item.itemIndex == itemIndex) {
                return item;
            }
        }
        return null;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 初始化基准位置
        resetItemsBounds(
                0 + leftIndent,
                0 + topIndent,
                (getRight() - getLeft()) - rightIndent,
                (getBottom() - getTop()) - bottomIndent,
                spaceBetweenItems);

        int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(getItemBoundWidth(), MeasureSpec.EXACTLY);
        int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(getItemBoundHeight(), MeasureSpec.EXACTLY);
        final int size = getChildCount();
        for (int i = 0; i < size; i++) {
            getChildAt(i).measure(childWidthMeasureSpec, childHeightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        alignAllItemPosition();
    }

    /**
     * 根据视图索引，依次重新排列布局对齐
     */
    private void alignAllItemPosition() {
        int size = Math.min(itemsBounds.length, getRecycleItemSize());
        for (int i = 0; i < size; i++) {
            Rect bounds = itemsBounds[i];
            findItem(i).layout(bounds);
        }
    }

    /**
     * 以中心视图开始，向左右两边依次设置数据索引，并更新视图
     */
    private void updateAllItemView(int centerDataIndex) {
        if (adapter == null || getRecycleItemSize() == 0 || centerDataIndex < 0) {
            return;
        }
        final int centerIndex = getRecycleItemSize() / 2;
        ItemWrapper item;
        //center
        item = findItem(centerIndex);
        item.setDataIndex(centerDataIndex);
        item.updateView();
        // left/top
        for (int i = 1; i <= centerIndex; i++) {
            item = findItem(centerIndex - i);
            item.setDataIndex(cycleDataIndex(centerDataIndex - i));
            item.updateView();
        }
        // right/bottom
        for (int i = 1; i <= centerIndex; i++) {
            item = findItem(centerIndex + i);
            item.setDataIndex(cycleDataIndex(centerDataIndex + i));
            item.updateView();
        }
    }

    private final PointF lastPoint = new PointF();
    private boolean isMoving = false;
    private boolean needIntercept = false;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        boolean superState = super.dispatchTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                needIntercept = false;
                lastPoint.set(event.getX(), event.getY());
                getParent().requestDisallowInterceptTouchEvent(true);
                return true;// can not return superState.
            case MotionEvent.ACTION_MOVE:
                float absXDiff = Math.abs(event.getX() - lastPoint.x);
                float absYDiff = Math.abs(event.getY() - lastPoint.y);
                if (orientation == LinearLayout.HORIZONTAL) {
                    if (absXDiff > absYDiff && absXDiff > MOVE_SLOP) {
                        needIntercept = true;
                    } else if (absYDiff > absXDiff && absYDiff > MOVE_SLOP) {
                        // restore touch event in parent
                        getParent().requestDisallowInterceptTouchEvent(false);
                    }
                } else if (orientation == LinearLayout.VERTICAL) {
                    if (absYDiff > absXDiff && absYDiff > MOVE_SLOP) {
                        needIntercept = true;
                    } else if (absXDiff > absYDiff && absXDiff > MOVE_SLOP) {
                        // restore touch event in parent
                        getParent().requestDisallowInterceptTouchEvent(false);
                    }
                }
                // pause auto switch
                interceptAutoCycle();
                return superState;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                // restart auto switch
                resumeAutoCycle();
                return superState;
            default:
                return superState;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        boolean superState = super.onInterceptTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return superState;
            case MotionEvent.ACTION_MOVE:
                return needIntercept;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                return superState;
            default:
                return superState;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean superState = super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastPoint.set(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                if (getRecycleItemSize() != 0 || getChildCount() != 0) {
                    float xDiff = event.getX() - lastPoint.x;
                    float yDiff = event.getY() - lastPoint.y;
                    float absXDiff = Math.abs(xDiff);
                    float absYDiff = Math.abs(yDiff);
                    if (orientation == LinearLayout.HORIZONTAL && absXDiff > absYDiff) {
                        move((int) -xDiff);
                    } else if (orientation == LinearLayout.VERTICAL && absYDiff > absXDiff) {
                        move((int) -yDiff);
                    }
                }
                lastPoint.set(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                // touch release
                if (isMoving) {
                    int direction = 0;
                    final int offset, maxOffset;
                    if (orientation == LinearLayout.VERTICAL) {
                        offset = getScrollY();
                        maxOffset = getItemBoundHeight() + spaceBetweenItems;
                    } else {
                        offset = getScrollX();
                        maxOffset = getItemBoundWidth() + spaceBetweenItems;
                    }

                    if (offset < -maxOffset * sensibility) {
                        direction = -1;
                    } else if (offset > maxOffset * sensibility) {
                        direction = 1;
                    }
                    if (direction == 0) {
                        autoPacking();
                    } else {
                        autoMove((maxOffset - Math.abs(offset)) * direction, durationOnTouchRelease, new Runnable() {
                            @Override
                            public void run() {
                                autoPacking();
                            }
                        });
                    }
                }
                break;
            default:
                return superState;
        }
        return true;
    }

    protected final void move(final int offset) {
        isMoving = true;

        int scrolled, maxOffset;
        if (orientation == LinearLayout.VERTICAL) {
            scrollBy(0, offset);
            scrolled = getScrollY();
            maxOffset = getItemBoundHeight() + spaceBetweenItems;
        } else { // HORIZONTAL
            scrollBy(offset, 0);
            scrolled = getScrollX();
            maxOffset = getItemBoundWidth() + spaceBetweenItems;
        }

        notifyOnItemScrolled(offset);
        // 判断视图切换
        log("move: scrolled: " + scrolled + ", maxOffset: " + maxOffset);
        if (Math.abs(scrolled) >= maxOffset) {
            final int size = getRecycleItemSize();
            ItemWrapper item;
            if (scrolled > 0) {
                for (int i = 0; i < size; i++) {
                    item = findItem(i);
                    item.itemIndex -= 1;
                }
            } else if (scrolled < 0) {
                // 从大到小，避免findItem找到重复的index
                for (int i = size - 1; i >= 0; i--) {
                    item = findItem(i);
                    item.itemIndex += 1;
                }
            }
            // 要先设置，在应用，否则由于是循环的下标findItem()会错乱
            for (ItemWrapper tmp : items) {
                tmp.itemIndex = cycleItemIndex(tmp.itemIndex);
            }
            // 重置滑动偏移
            scrollTo(0, 0);
            log("move: 归 0,0");
            // 根据新的中心视图位置，重新设置视图的数据索引，并且更新视图
            updateAllItemView(getCurrentIndex());
            // 重新排列布局
            alignAllItemPosition();
        }
    }

    private ValueAnimator autoScroller = null;

    protected final void autoMove(final int offset, final long duration, final Runnable callback) {
        if (autoScroller != null && autoScroller.isStarted()) {
            autoScroller.cancel();
            autoScroller = null;
        }
        if (Math.abs(offset) < 10) {
            // 滑动距离过小，不需要动画
            if (offset != 0) {
                move(offset);
            }
            isMoving = false;
            resumeAutoCycle();
            if (null != callback) {
                callback.run();
            }
            log("autoMove: 无动画 offset: " + offset);
        } else {
            log("autoMove: 创建动画 offset: " + offset);
            autoScroller = ValueAnimator.ofInt(0, offset);
            autoScroller.setDuration(duration);
            autoScroller.setInterpolator(new DecelerateInterpolator());
            autoScroller.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                private int lastValue;

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int currentValue = (int) animation.getAnimatedValue();
                    move(currentValue - lastValue);
                    lastValue = currentValue;
                }
            });
            autoScroller.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    isMoving = true;
                    interceptAutoCycle();
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    isMoving = false;
                    resumeAutoCycle();
                    if (null != callback) {
                        callback.run();
                    }
                }
            });
            autoScroller.start();
        }
    }

    protected final void autoPacking() {
        int offset, maxOffset;
        if (orientation == LinearLayout.VERTICAL) {
            offset = getScrollY();
            maxOffset = getItemBoundHeight() + spaceBetweenItems;
        } else {
            offset = getScrollX();
            maxOffset = getItemBoundWidth() + spaceBetweenItems;
        }
        if (0 != offset) {
            final int absOffset = Math.abs(offset);
            if (absOffset >= maxOffset / 2) {
                // 已经越过视图一半，此时不归位，同向继续滑动到下一个视图
                offset = (maxOffset - absOffset) * (offset / absOffset);
                log("autoPacking: 停靠 下一个 offset: " + offset);
            } else {
                // 未越过一半，归位
                offset = -offset;
                log("autoPacking: 停靠 归位 offset: " + offset);
            }
        }
        autoMove(offset, durationOnTouchRelease, new Runnable() {
            @Override
            public void run() {
                notifyOnItemSelected();
            }
        });
    }

    protected final int cycleDataIndex(int dataIndex) {
        if (adapter == null) {
            return -1;
        }
        int count = adapter.getCount();
        if (count < 2) {
            return 0;
        }
        if (dataIndex > count - 1) {
            dataIndex = dataIndex % count;
        } else if (dataIndex < 0) {
            dataIndex = (count + dataIndex % count) % count;
        }
        return dataIndex;
    }

    protected final int cycleItemIndex(int itemIndex) {
        final int count = getRecycleItemSize();
        if (count < 2) {
            return 0;
        }
        if (itemIndex > count - 1) {
            itemIndex = itemIndex % count;
        } else if (itemIndex < 0) {
            itemIndex = (count + itemIndex % count) % count;
        }
        return itemIndex;
    }

    private final class AdapterDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            final int index = getCurrentIndex();
            for (ItemWrapper item : items) {
                item.recycle();
            }
            lastCenterItemDataIndex = -1;// 保证重新通知一次
            final int newIndex = (index >= 0 && index < adapter.getCount()) ? index : 0;
            setCurrentIndex(newIndex);
            requestLayout();
            if (null != indicator) {
                indicator.setPointerSize(adapter.getCount());
                indicator.setCurrentIndex(newIndex);
            }
        }

        @Override
        public void onInvalidated() {
            invalidate();
        }
    }

    /**
     * Wrapper (contains data id and state)
     */
    private final class ItemWrapper {

        private static final int NONE = 0x00;
        private static final int USING = 0x01;

        private int state;

        /**
         * 视图索引
         */
        private int itemIndex;
        /**
         * 数据索引
         */
        private int dataIndex;
        private View view;

        ItemWrapper(int itemIndex) {
            this.state = NONE;
            this.itemIndex = itemIndex;
            this.dataIndex = -1;
            this.view = null;
        }

        void layout(Rect bounds) {
            if (null != view) {
                view.layout(bounds.left, bounds.top, bounds.right, bounds.bottom);
            }
        }

        void setDataIndex(int index) {
            if (index != dataIndex) {
                state = NONE;
            }
            this.dataIndex = index;
        }

        void updateView() {
            if (adapter != null && dataIndex >= 0 && dataIndex < adapter.getCount() && state == NONE) {
                state = USING;
                View convertView = adapter.getView(dataIndex, view, SweetCircularView.this);
                if (convertView == view) {
                    // nothing to do
                } else {
                    // remove old view
                    removeView();
                    // add new view
                    if (convertView != null) {
                        if (convertView.getParent() != SweetCircularView.this) {
                            addView(convertView);
                        }
                    }
                }
                view = convertView;
                // 点击非中心试图立即选中
                view.setOnClickListener(new OnClickListenerWrapper(view) {
                    @Override
                    public void onClick(View view) {
                        if (isClick2Selected()) {
                            moveItems(itemIndex - getRecycleItemSize() / 2);
                        }
                        super.onClick(view);
                    }
                });
            }
        }

        void removeView() {
            if (view != null && view.getParent() != null) {
                ((ViewGroup) view.getParent()).removeView(view);
            }
            view = null;
        }

        void recycle() {
            removeView();
            state = NONE;
            dataIndex = -1;
        }

    }

    /**
     * 视图外部OnClick事件包装
     */
    private static abstract class OnClickListenerWrapper implements OnClickListener {

        private OnClickListener listener;

        private OnClickListenerWrapper(View view) {
            if (null == view) {
                return;
            }
            try {
                Object listenerInfo = ReflectionUtils.getProperty(view, "mListenerInfo");
                if (null != listenerInfo) {
                    Class ListenerInfoCls = listenerInfo.getClass();
                    Field field = ListenerInfoCls.getField("mOnClickListener");
                    field.setAccessible(true);
                    listener = (OnClickListener) field.get(listenerInfo);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onClick(View view) {
            if (null != listener) {
                listener.onClick(view);
            }
        }
    }

    public interface OnItemScrolledListener {
        void onItemScrolled(SweetCircularView v, int dataIndex, float offset);
    }

    public interface OnItemSelectedListener {
        void onItemSelected(SweetCircularView v, int dataIndex);
    }

    /**
     * 动画适配器
     */
    public static abstract class AnimationAdapter {

        private SweetCircularView circularView;

        protected final SweetCircularView getCircularView() {
            return circularView;
        }

        /**
         * 根据中心视图索引，向左右两侧获取视图
         *
         * @param indexOffset 0:中心, <0:左(上)侧, >0:右(下)侧
         */
        protected final View getView(int indexOffset) {
            if (null == circularView) {
                return null;
            }
            final int centerIndex = circularView.getRecycleItemSize() / 2;
            ItemWrapper targetItem = circularView.findItem(cycleIndex(centerIndex + indexOffset));
            return (null != targetItem) ? targetItem.view : null;
        }

        /**
         * 根据中心视图索引，获取两侧视图距离中心位置的偏移
         *
         * @param indexOffset 0:中心, <0:左(上)侧, >0:右(下)侧
         */
        protected final int getOffset(int indexOffset) {
            if (null == circularView) {
                return 0;
            }
            final View centerView = getView(0);
            final View targetView = getView(indexOffset);
            int offset = 0;
            if (null != centerView && null != targetView) {
                if (LinearLayout.VERTICAL == circularView.getOrientation()) {
                    offset = targetView.getTop() - centerView.getTop() - circularView.getScrollY();
                } else { // HORIZONTAL
                    offset = targetView.getLeft() - centerView.getLeft() - circularView.getScrollX();
                }
            }
            return offset;
        }

        /**
         * 根据中心视图索引，获取两侧视图距离中心位置的偏移百分比
         * indexOffset=0 中心，即偏移 0%
         *
         * @param indexOffset 0:中心, <0:左(上)侧, >0:右(下)侧
         * @return >0, 距离中心越远值越大
         */
        protected final float getOffsetPercent(int indexOffset) {
            final int maxOffset = circularView.getMeasuredWidth() / 2;
            int targetOffset = getOffset(indexOffset);
            return Math.abs(targetOffset) * 1.0f / maxOffset;
        }

        /**
         * 获取视图宽度
         */
        protected final int getItemWidth() {
            if (null == circularView) {
                return 0;
            }
            if (null != circularView.itemsBounds && circularView.itemsBounds.length > 0) {
                return circularView.itemsBounds[0].width();
            } else {
                return 0;
            }
        }

        protected final int cycleIndex(int indexOffset) {
            if (null == circularView) {
                return indexOffset;
            }
            return circularView.cycleItemIndex(indexOffset);
        }

        protected final int getSize() {
            return circularView.getRecycleItemSize();
        }

        protected abstract void onScrolled(int offset);

    }

}
