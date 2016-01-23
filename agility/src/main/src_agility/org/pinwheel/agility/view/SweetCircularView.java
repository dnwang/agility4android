package org.pinwheel.agility.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.PointF;
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
public class SweetCircularView extends ViewGroup {
    private static final String TAG = SweetCircularView.class.getSimpleName();

    private static final int MOVE_SLOP = 10;

    private static final int DEFAULT_ITEM_SIZE = 3;

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
    private int leftIndent, topIndent, rightIndent, bottomIndent;

    private AdapterDataSetObserver dataSetObserver;
    private BaseAdapter adapter;
    private ArrayList<ItemWrapper> items = null;
    private int currentDataIndex = 0;
    private int currentItemIndex = 0;

    private Runnable autoCycleRunnable = new Runnable() {
        @Override
        public void run() {
            if (isShown() && adapter != null && adapter.getCount() > 0) {
                if (isAutoCycleToNext) {
                    moveNext();
                } else {
                    movePrevious();
                }
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
        currentDataIndex = 0;
        currentItemIndex = 0;
        items = new ArrayList<>(DEFAULT_ITEM_SIZE);
        for (int i = 0; i < DEFAULT_ITEM_SIZE; i++) {
            items.add(new ItemWrapper());
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        isAttachedToWindow = true;
        if (isAutoCycle) {
            removeCallbacks(autoCycleRunnable);
            postDelayed(autoCycleRunnable, intervalOnAutoCycle);
        }
    }

    private boolean isAttachedToWindow = false;

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isAttachedToWindow = false;
        removeCallbacks(autoCycleRunnable);
    }

    public int getCurrentDataIndex() {
        return currentDataIndex;
    }

    public int getCurrentItemIndex() {
        return currentItemIndex;
    }

    /**
     * @param dataIndex dataIndex
     */
    public void setCurrentDataIndex(int dataIndex) {
        if (adapter == null || items.size() == 0 || dataIndex < 0) {
            Log.i(TAG, "setCurrentDataIndex() params error");
            return;
        }
        dataIndex = cycleDataIndex(dataIndex);
        currentDataIndex = dataIndex;
        currentItemIndex = alignAndRefreshItems(currentDataIndex);
        layoutItems(currentItemIndex, getLeft(), getTop(), getRight(), getBottom());
    }

    /**
     * @param cycleAdapter cycleAdapter
     */
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

    /**
     * @param sensibility sensibility
     */
    public void setSensibility(float sensibility) {
        this.sensibility = Math.max(0, Math.min(1.0f, sensibility));
    }

    public float getSensibility() {
        return this.sensibility;
    }

    /**
     * @param orientation orientation
     */
    public void setOrientation(int orientation) {
        this.orientation = orientation == LinearLayout.VERTICAL ? orientation : LinearLayout.HORIZONTAL;
        requestLayout();
    }

    public int getOrientation() {
        return orientation;
    }

    /**
     * @param duration duration
     */
    public void setDurationOnTouchRelease(long duration) {
        durationOnTouchRelease = Math.max(0, duration);
    }

    public long getDurationOnTouchRelease() {
        return durationOnTouchRelease;
    }

    /**
     * @param duration duration
     */
    public void setDurationOnAutoScroll(long duration) {
        durationOnAutoScroll = Math.max(0, duration);
    }

    public long getDurationOnAutoScroll() {
        return durationOnAutoScroll;
    }

    /**
     * @param interval duration
     */
    public void setIntervalOnAutoCycle(long interval) {
        intervalOnAutoCycle = Math.max(0, interval);
    }

    public long getIntervalOnAutoCycle() {
        return intervalOnAutoCycle;
    }

    public boolean isAutoCycle() {
        return isAutoCycle;
    }

    /**
     * @param is         enable
     * @param moveToNext direction
     */
    public void setAutoCycle(boolean is, boolean moveToNext) {
        isAutoCycle = is;
        isAutoCycleToNext = moveToNext;
        if (is) {
            if (isAttachedToWindow) {
                // auto start when already attached to window
                removeCallbacks(autoCycleRunnable);
                postDelayed(autoCycleRunnable, intervalOnAutoCycle);
            }
        } else {
            removeCallbacks(autoCycleRunnable);
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

    /**
     * @param left   the left padding in pixels
     * @param top    the top padding in pixels
     * @param right  the right padding in pixels
     * @param bottom the bottom padding in pixels
     */
    public void setIndent(int left, int top, int right, int bottom) {
        if (left == leftIndent && top == topIndent && right == rightIndent && bottom == bottomIndent) {
            Log.i(TAG, "setIndent() l,t,r,b have not changed");
            return;
        }
        leftIndent = left;
        topIndent = top;
        rightIndent = right;
        bottomIndent = bottom;
        requestLayout();
    }

    public int getLeftIndent() {
        return leftIndent;
    }

    public int getTopIndent() {
        return topIndent;
    }

    public int getRightIndent() {
        return rightIndent;
    }

    public int getBottomIndent() {
        return bottomIndent;
    }

    /**
     * @param space space between items
     */
    public void setSpaceBetweenItems(int space) {
        spaceBetweenItems = space;
    }

    public int getSpaceBetweenItems() {
        return spaceBetweenItems;
    }

    /**
     * @param itemIndex
     */
    public final View getView(int itemIndex) {
        if (itemIndex < 0 || itemIndex >= items.size()) {
            return null;
        }
        return items.get(itemIndex).getView();
    }

    /**
     * @param listener listener
     */
    public final void addOnItemSwitchListener(OnItemSwitchListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    /**
     * @param listener
     */
    public final void removeOnItemSwitchListener(OnItemSwitchListener listener) {
        if (listener != null) {
            listeners.remove(listener);
        }
    }

    protected void onItemScrolled(int dataIndex, float offset) {
        if (listeners != null) {
            for (OnItemSwitchListener listener : listeners) {
                listener.onItemScrolled(this, dataIndex, offset);
            }
        }
    }

    protected void onItemSelected(int dataIndex) {
        if (listeners != null) {
            for (OnItemSwitchListener listener : listeners) {
                listener.onItemSelected(this, dataIndex);
            }
        }
    }

    /**
     * @param size 3,5,7,9 ...
     */
    public void setRecycleItemSize(int size) {
        if (size < 3 || size % 2 == 0) {
            throw new IllegalStateException("setRecycleItemSize(): the size should be more than 3 of the odd number (3,5,7,9 ...) !");
        }
        for (ItemWrapper item : items) {
            item.recycle();
        }
        items.clear();
        for (int itemIndex = 0; itemIndex < size; itemIndex++) {
            items.add(new ItemWrapper());
        }
        setCurrentDataIndex(0);
    }

    public final int getRecycleItemSize() {
        return items.size();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth() - leftIndent - rightIndent, MeasureSpec.EXACTLY);
        int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight() - topIndent - bottomIndent, MeasureSpec.EXACTLY);
        int size = getChildCount();
        for (int i = 0; i < size; i++) {
            getChildAt(i).measure(childWidthMeasureSpec, childHeightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        layoutItems(currentItemIndex, l, t, r, b);
    }

    public final int findItemIndex(int dataIndex) {
        final int sizeOfItem = getRecycleItemSize();
        for (int i = 0; i < sizeOfItem; i++) {
            ItemWrapper item = items.get(i);
            if (item.dataIndex == dataIndex) {
                return i;
            }
        }
        return -1;
    }

    private int alignAndRefreshItems(final int centerDataIndex) {
        final int sizeOfItem = getRecycleItemSize();
        if (sizeOfItem == 0) {
            Log.i(TAG, "alignItemDataIndex() have no move item");
            return -1;
        }
        final int sizeOfSideItem = sizeOfItem / 2; // 3,5,7,9 ...
        final int centerItemIndex = cycleItemIndex(findItemIndex(centerDataIndex));

        ItemWrapper item;
        //center
        item = items.get(centerItemIndex);
        item.setDataIndex(centerDataIndex);
        item.refreshView();
        // left/top
        for (int i = 1; i <= sizeOfSideItem; i++) {
            item = items.get(cycleItemIndex(centerItemIndex - i));
            item.setDataIndex(cycleDataIndex(centerDataIndex - i));
            item.refreshView();
        }
        // right/bottom
        for (int i = 1; i <= sizeOfSideItem; i++) {
            item = items.get(cycleItemIndex(centerItemIndex + i));
            item.setDataIndex(cycleDataIndex(centerDataIndex + i));
            item.refreshView();
        }
        return centerItemIndex;
    }

    private void layoutItems(final int centerItemIndex, int l, int t, int r, int b) {
        if (centerItemIndex < 0) {
            Log.i(TAG, "layoutChildByDataIndex() params error");
            return;
        }
        final int sizeOfItem = getRecycleItemSize();
        if (sizeOfItem == 0) {
            Log.i(TAG, "layoutChildByDataIndex() have no move item");
            return;
        }
        // reset offset
        for (ItemWrapper item : items) {
            item.moveX(0.0f);
            item.moveY(0.0f);
        }

        final int sizeOfSideItem = sizeOfItem / 2; // 3,5,7,9 ...

        final int itemWidth = (r - rightIndent) - (l + leftIndent);
        final int itemHeight = (b - bottomIndent) - (t + topIndent);
        final int centerItemLeft = 0 + leftIndent;
        final int centerItemTop = 0 + topIndent;
        final int centerItemRight = (r - l) - rightIndent;
        final int centerItemBottom = (b - t) - bottomIndent;

        ItemWrapper item;
        int left, top, right, bottom;
        //center
        item = items.get(centerItemIndex);
        if (item.getView() != null) {
            item.getView().layout(centerItemLeft, centerItemTop, centerItemRight, centerItemBottom);
        }
        // left/top
        for (int i = 1; i <= sizeOfSideItem; i++) {
            item = items.get(cycleItemIndex(centerItemIndex - i));
            if (item.getView() != null) {
                if (orientation == LinearLayout.HORIZONTAL) {
                    // left
                    left = centerItemLeft + (-i) * (itemWidth + spaceBetweenItems);
                    item.getView().layout(left, centerItemTop, left + itemWidth, centerItemBottom);
                } else if (orientation == LinearLayout.VERTICAL) {
                    // top
                    top = centerItemTop + (-i) * (itemHeight + spaceBetweenItems);
                    item.getView().layout(centerItemLeft, top, centerItemRight, top + itemHeight);
                }
            }
        }
        // right/bottom
        for (int i = 1; i <= sizeOfSideItem; i++) {
            item = items.get(cycleItemIndex(centerItemIndex + i));
            if (item.getView() != null) {
                if (orientation == LinearLayout.HORIZONTAL) {
                    // right
                    right = centerItemRight + (i) * (itemWidth + spaceBetweenItems);
                    item.getView().layout(right - itemWidth, centerItemTop, right, centerItemBottom);
                } else if (orientation == LinearLayout.VERTICAL) {
                    // bottom
                    bottom = centerItemBottom + (i) * (itemHeight + spaceBetweenItems);
                    item.getView().layout(centerItemLeft, bottom - itemHeight, centerItemRight, bottom);
                }
            }
        }
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
                if (isAutoCycle) {
                    removeCallbacks(autoCycleRunnable);
                }
                return superState;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                // restart auto switch
                if (isAutoCycle) {
                    removeCallbacks(autoCycleRunnable);
                    postDelayed(autoCycleRunnable, intervalOnAutoCycle);
                }
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
                    if (currentItemIndex < 0) {
                        break;
                    }
                    ItemWrapper item = items.get(currentItemIndex);
                    int changeIndex = 0;
                    float offset = 0;
                    float maxOffset = 0;
                    if (orientation == LinearLayout.HORIZONTAL) {
                        offset = item.getOffsetX();
                        maxOffset = item.getView().getMeasuredWidth() + spaceBetweenItems;
                    } else if (orientation == LinearLayout.VERTICAL) {
                        offset = item.getOffsetY();
                        maxOffset = item.getView().getMeasuredHeight() + spaceBetweenItems;
                    }
                    if (offset < -maxOffset * sensibility) {
                        changeIndex = 1;
                    } else if (offset > maxOffset * sensibility) {
                        changeIndex = -1;
                    }
                    if (changeIndex == 0) {
                        autoMove(-offset, durationOnTouchRelease, changeIndex);
                    } else {
                        autoMove((maxOffset - Math.abs(offset)) * -changeIndex, durationOnTouchRelease, changeIndex);
                    }
                }
                break;
            default:
                return superState;
        }
        return true;
    }

    private void moveX(float offset) {
        isMoving = true;
        for (ItemWrapper item : items) {
            item.moveX(item.getOffsetX() + offset);
        }
        onItemScrolled(currentDataIndex, offset);
    }

    private void moveY(float offset) {
        isMoving = true;
        for (ItemWrapper item : items) {
            item.moveY(item.getOffsetY() + offset);
        }
        onItemScrolled(currentDataIndex, offset);
    }

    private ValueAnimator autoScroller = null;

    private void autoMove(float offset, long duration, final int changeIndex) {
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
            public void onAnimationEnd(Animator animation) {
                isMoving = false;
                // only this change currentDataIndex
                currentDataIndex = cycleDataIndex(currentDataIndex + changeIndex);
                currentItemIndex = alignAndRefreshItems(currentDataIndex);
                layoutItems(currentItemIndex, getLeft(), getTop(), getRight(), getBottom());

                onItemSelected(currentDataIndex);
            }
        });
        autoScroller.start();
    }

    /**
     * Auto scroll to next
     */
    public void moveNext() {
        if (items.size() == 0) {
            Log.i(TAG, "moveNext() have not move item");
            return;
        }
        if (currentItemIndex < 0) {
            Log.i(TAG, "moveNext() can not find centerDataIndex");
            return;
        }
        View centerView = items.get(currentItemIndex).getView();
        if (centerView == null) {
            Log.i(TAG, "moveNext() can not find centerView");
            return;
        }
        int offset = 0;
        if (orientation == LinearLayout.HORIZONTAL) {
            offset = centerView.getWidth() + spaceBetweenItems;
        } else if (orientation == LinearLayout.VERTICAL) {
            offset = centerView.getHeight() + spaceBetweenItems;
        }
        autoMove(offset, durationOnAutoScroll, -1);
    }

    /**
     * Auto scroll to previous
     */
    public void movePrevious() {
        if (items.size() == 0) {
            Log.i(TAG, "movePrevious() have not move item");
            return;
        }
        if (currentItemIndex < 0) {
            Log.i(TAG, "movePrevious() can not find centerDataIndex");
            return;
        }
        View centerView = items.get(currentItemIndex).getView();
        if (centerView == null) {
            Log.i(TAG, "movePrevious() can not find centerView");
            return;
        }
        int offset = 0;
        if (orientation == LinearLayout.HORIZONTAL) {
            offset = centerView.getWidth() + spaceBetweenItems;
        } else if (orientation == LinearLayout.VERTICAL) {
            offset = centerView.getHeight() + spaceBetweenItems;
        }
        autoMove(-offset, durationOnAutoScroll, 1);
    }

    public final int cycleDataIndex(int dataIndex) {
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

    public final int cycleItemIndex(int itemIndex) {
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
            currentDataIndex = 0;
            currentItemIndex = alignAndRefreshItems(currentDataIndex);

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
        private int dataIndex;
        private View view;

        public ItemWrapper() {
            this.state = NONE;
            this.dataIndex = -1;
            this.view = null;
        }

        public void moveX(float offset) {
            if (view != null && getOffsetX() != offset) {
                view.setTranslationX(offset);
            }
        }

        public void moveY(float offset) {
            if (view != null && getOffsetY() != offset) {
                view.setTranslationY(offset);
            }
        }

        public float getOffsetX() {
            return view == null ? 0.0f : view.getTranslationX();
        }

        public float getOffsetY() {
            return view == null ? 0.0f : view.getTranslationY();
        }

        public View getView() {
            return view;
        }

        public int getDataIndex() {
            return dataIndex;
        }

        public void setDataIndex(int index) {
            if (index != dataIndex) {
                state = NONE;
            }
            this.dataIndex = index;
        }

        public void refreshView() {
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
            }
        }

        private void removeView() {
            if (view != null && view.getParent() != null) {
                ((ViewGroup) view.getParent()).removeView(view);
            }
            view = null;
        }

        public void recycle() {
            removeView();
            state = NONE;
            dataIndex = -1;
        }
    }

    /**
     * Event listener
     */
    public interface OnItemSwitchListener {

        void onItemSelected(SweetCircularView v, int dataIndex);

        void onItemScrolled(SweetCircularView v, int dataIndex, float offset);

    }

}
