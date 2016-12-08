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

import java.util.ArrayList;

/**
 * Copyright (C), 2016 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public class SweetCircularView2 extends ViewGroup {
    private static final String TAG = SweetCircularView2.class.getSimpleName();

    private static final int MOVE_SLOP = 10;

    private static final int DEFAULT_ITEM_SIZE = 5;

    private boolean isClick2Selected = false;
    private boolean isRecyclable = true;
    private boolean isAutoCycle = false;
    private boolean isAutoCycleToNext = true;
    private long intervalOnAutoCycle = 4000;
    private long durationOnAutoScroll = 300;
    private long durationOnTouchRelease = 200;
    private float sensibility = 0.5f;
    private int orientation = LinearLayout.HORIZONTAL;
    private ArrayList<OnItemSwitchListener> listeners = new ArrayList<>(2);

    private int spaceBetweenItems;

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
                if (isAutoCycleToNext) {
                    moveItems(-1);
                } else {
                    moveItems(1);
                }
            }
            postDelayed(this, intervalOnAutoCycle);
        }
    };

    public SweetCircularView2(Context context) {
        super(context);
        init();
    }

    public SweetCircularView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SweetCircularView2(Context context, AttributeSet attrs, int defStyle) {
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

    public final void setCurrentIndex(int dataIndex) {
        if (adapter == null || items.size() == 0 || dataIndex < 0) {
            Log.i(TAG, "setCurrentDataIndex() params error");
            return;
        }

        final int centerIndex = getRecycleItemSize() / 2;
        ItemWrapper item;
        //center
        item = findItem(centerIndex);
        item.setDataIndex(dataIndex);
        item.refreshView();
        // left/top
        for (int i = 1; i <= centerIndex; i++) {
            item = findItem(centerIndex - i);
            item.setDataIndex(dataIndex - i);
            item.refreshView();
        }
        // right/bottom
        for (int i = 1; i <= centerIndex; i++) {
            item = findItem(centerIndex + i);
            item.setDataIndex(dataIndex + i);
            item.refreshView();
        }
    }

    public void setAdapter(BaseAdapter cycleAdapter) {
        if (adapter != null) {
            adapter.unregisterDataSetObserver(dataSetObserver);
        }
        if (cycleAdapter != null) {
            dataSetObserver = new AdapterDataSetObserver();
            cycleAdapter.registerDataSetObserver(dataSetObserver);
        }
        adapter = cycleAdapter;
    }

    public BaseAdapter getAdapter() {
        return adapter;
    }

    public void setSensibility(float sensibility) {
        this.sensibility = Math.max(0, Math.min(1.0f, sensibility));
    }

    public float getSensibility() {
        return this.sensibility;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation == LinearLayout.VERTICAL ? orientation : LinearLayout.HORIZONTAL;
        requestLayout();
    }

    public int getOrientation() {
        return orientation;
    }

    public void setDurationOnTouchRelease(long duration) {
        durationOnTouchRelease = Math.max(0, duration);
    }

    public long getDurationOnTouchRelease() {
        return durationOnTouchRelease;
    }

    public void setDurationOnAutoScroll(long duration) {
        durationOnAutoScroll = Math.max(0, duration);
    }

    public long getDurationOnAutoScroll() {
        return durationOnAutoScroll;
    }

    public void setIntervalOnAutoCycle(long interval) {
        intervalOnAutoCycle = Math.max(0, interval);
    }

    public long getIntervalOnAutoCycle() {
        return intervalOnAutoCycle;
    }

    public boolean isAutoCycle() {
        return isAutoCycle;
    }

    public void setAutoCycle(boolean is, boolean moveToNext) {
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
    }

    /**
     * @param is is recyclable
     */
    public void setRecyclable(boolean is) {
        isRecyclable = is;
    }

    public boolean isRecyclable() {
        return isRecyclable;
    }

    private int leftIndent, topIndent, rightIndent, bottomIndent;

    /**
     * @param left   the left padding in pixels
     * @param top    the top padding in pixels
     * @param right  the right padding in pixels
     * @param bottom the bottom padding in pixels
     */
    public void setIndent(int left, int top, int right, int bottom) {
        leftIndent = left;
        topIndent = top;
        rightIndent = right;
        bottomIndent = bottom;
        requestLayout();
    }

    public void setSpaceBetweenItems(int space) {
        spaceBetweenItems = space;
    }

    public int getSpaceBetweenItems() {
        return spaceBetweenItems;
    }

    public void setClick2Selected(boolean is) {
        isClick2Selected = is;
    }

    public final void addOnItemSwitchListener(OnItemSwitchListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    public final void removeOnItemSwitchListener(OnItemSwitchListener listener) {
        if (listener != null) {
            listeners.remove(listener);
        }
    }

    protected void onItemScrolled(int index, float offset) {
        if (listeners != null) {
            for (OnItemSwitchListener listener : listeners) {
                listener.onItemScrolled(this, index, offset);
            }
        }
    }

    protected void onItemSelected(int index) {
        if (listeners != null) {
            for (OnItemSwitchListener listener : listeners) {
                listener.onItemSelected(this, index);
            }
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

    private void resetItems(int size) {
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


            } else { // LinearLayout.HORIZONTAL
                left = centerLeft - m * (centerRight - centerLeft);
                top = centerTop;
                right = left + (centerRight - centerLeft);
                bottom = centerBottom;
                rect.set(left, top, right, bottom);
            }
            itemsBounds[i] = rect;
        }
    }

    private int getItemWidth() {
        return itemsBounds[0].width();
    }

    private int getItemHeight() {
        return itemsBounds[0].height();
    }

    @Deprecated
    public void setRecycleItemSize(int size) {
        if (size < 3 || size % 2 == 0) {
            throw new IllegalStateException("setRecycleItemSize(): the size should be more than 3 of the odd number (3,5,7,9 ...) !");
        }
        for (ItemWrapper item : items) {
            item.recycle();
        }
        items.clear();
        for (int i = 0; i < size; i++) {
            items.add(new ItemWrapper(i));
        }
        setCurrentIndex(0);
    }

    public final int getRecycleItemSize() {
        return items.size();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // 初始化基准位置
        resetItemsBounds(
                getLeft() + leftIndent,
                getTop() + topIndent,
                (getRight() - getLeft()) - rightIndent,
                (getBottom() - getTop()) - bottomIndent,
                spaceBetweenItems);

        int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(getItemWidth(), MeasureSpec.EXACTLY);
        int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(getItemHeight(), MeasureSpec.EXACTLY);
        int size = getChildCount();
        for (int i = 0; i < size; i++) {
            getChildAt(i).measure(childWidthMeasureSpec, childHeightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.e(TAG, "onLayout: changed:" + changed);
        layoutItems();
    }

    private void layoutItems() {
        // 布局试图位置
        int size = Math.min(itemsBounds.length, items.size());
        for (int i = 0; i < size; i++) {
            Rect bounds = itemsBounds[i];
            findItem(i).layout(bounds);
        }
    }

    private ItemWrapper findItem(int itemIndex) {
        for (int i = 0; i < items.size(); i++) {
            ItemWrapper item = items.get(i);
            if (item.itemIndex == itemIndex) {
                return item;
            }
        }
        return null;
    }

    private PointF lastPoint = new PointF();
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
                        // x
                        moveX(xDiff);
                    } else if (orientation == LinearLayout.VERTICAL && absYDiff > absXDiff) {
                        // y
                        moveY(yDiff);
                    }
                }
                lastPoint.set(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                // touch release
                if (isMoving) {
                    if (getRecycleItemSize() == 0 || getChildCount() == 0) {
                        isMoving = false;
                        break;
                    }

//                    ItemWrapper item = items.get(currentItemIndex);
//                    int changeIndex = 0;
//                    float offset = 0;
//                    float maxOffset = 0;
//                    if (orientation == LinearLayout.HORIZONTAL) {
//                        offset = item.getOffsetX();
//                        maxOffset = item.getView().getMeasuredWidth() + spaceBetweenItems;
//                    } else if (orientation == LinearLayout.VERTICAL) {
//                        offset = item.getOffsetY();
//                        maxOffset = item.getView().getMeasuredHeight() + spaceBetweenItems;
//                    }
//                    if (offset < -maxOffset * sensibility) {
//                        changeIndex = 1;
//                    } else if (offset > maxOffset * sensibility) {
//                        changeIndex = -1;
//                    }
//                    if (changeIndex == 0) {
//                        autoMove(-offset, durationOnTouchRelease, changeIndex, null);
//                    } else {
//                        autoMove((maxOffset - Math.abs(offset)) * -changeIndex, durationOnTouchRelease, changeIndex, null);
//                    }
                }
                break;
            default:
                return superState;
        }
        return true;
    }

    private void moveX(float offset) {
        isMoving = true;

        scrollBy((int) -offset, 0);
        final int width = getItemWidth();
        final int scrollX = getScrollX();
        if (Math.abs(scrollX) >= width) {
            if (scrollX > 0) {
                ItemWrapper item;
                for (int i = 0; i < items.size(); i++) {
                    item = findItem(i);
                    item.itemIndex -= 1;
                }
            } else if (scrollX < 0) {
                ItemWrapper item;
                // 从大到小，避免findItem找到重复的index
                for (int i = items.size() - 1; i >= 0; i--) {
                    item = findItem(i);
                    item.itemIndex += 1;
                }
            }
            // 要先设置，在应用，否则由于是循环的下标findItem()会错乱
            for (ItemWrapper tmp : items) {
                tmp.itemIndex = cycleItemIndex(tmp.itemIndex);
            }
            scrollTo(0, 0);
            layoutItems();
        }
    }

    private void moveY(float offset) {
        isMoving = true;

        scrollBy((int) -offset, 0);
        // TODO: 2016/12/8

    }

    private ValueAnimator autoScroller = null;

    private void autoMove(final float offset, final long duration, final int changeIndex, final Runnable callback) {
        if (autoScroller != null && autoScroller.isStarted()) {
            autoScroller.cancel();
            autoScroller = null;
        }
        if (offset == 0) {
            return;
        }
        autoScroller = ValueAnimator.ofFloat(0, offset);
        autoScroller.setDuration(duration);
        autoScroller.setInterpolator(new DecelerateInterpolator());
        autoScroller.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            private float lastValue;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float currentValue = (Float) animation.getAnimatedValue();
                if (orientation == LinearLayout.HORIZONTAL) {
                    moveX(currentValue - lastValue);
                } else if (orientation == LinearLayout.VERTICAL) {
                    moveY(currentValue - lastValue);
                }
                lastValue = currentValue;
            }
        });
        autoScroller.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                interceptAutoCycle();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isMoving = false;
                resumeAutoCycle();
//                setCurrentDataIndex(cycleDataIndex(currentDataIndex + changeIndex));
                if (null != callback) {
                    callback.run();
                }
            }
        });
        autoScroller.start();
    }

    /**
     * <0 Previous;
     * >0 Next
     */
    public final void moveItems(final int changed) {
        if (isMoving) {
            return;
        }
        // TODO: 2016/12/8

    }

    private int cycleDataIndex(int dataIndex) {
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

    private int cycleItemIndex(int itemIndex) {
        int count = items.size();
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
            for (ItemWrapper item : items) {
                item.recycle();
            }
            setCurrentIndex(0);
            requestLayout();
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

        void refreshView() {
            if (adapter != null && dataIndex >= 0 && dataIndex < adapter.getCount() && state == NONE) {
                state = USING;
                View convertView = adapter.getView(dataIndex, view, SweetCircularView2.this);
                if (convertView == view) {
                    // nothing to do
                } else {
                    // remove old view
                    removeView();
                    // add new view
                    if (convertView != null) {
                        if (convertView.getParent() != SweetCircularView2.this) {
                            addView(convertView);
                        }
                    }
                }
                view = convertView;
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
     * Event listener
     */
    public interface OnItemSwitchListener {

        void onItemSelected(SweetCircularView2 v, int index);

        void onItemScrolled(SweetCircularView2 v, int index, float offset);

    }

}
