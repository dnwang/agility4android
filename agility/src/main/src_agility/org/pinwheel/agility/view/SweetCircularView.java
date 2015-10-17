package org.pinwheel.agility.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public class SweetCircularView extends ViewGroup {

    private static final int MOVE_SLOP = 10;

    private boolean isRecyclable = true;
    private boolean isAutoCycle = false;
    private boolean isAutoCycleToNext = true;
    private long intervalOnAutoCycle = 4000l;
    private long durationOnAutoScroll = 300l;
    private long durationOnTouchRelease = 200l;
    private float sensibility = 0.5f;
    private int orientation = LinearLayout.HORIZONTAL;
    private OnItemSwitchListener onItemSwitchListener;

    private int spaceBetweenItems;
    private int leftIndent, topIndent, rightIndent, bottomIndent;

    protected AdapterDataSetObserver dataSetObserver;
    protected BaseAdapter adapter;
    protected AnimatorAdapter animatorAdapter;
    protected ArrayList<ItemWrapper> items = new ArrayList<ItemWrapper>(3);
    protected int currentItemIndex = 0;

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
        setRecycleItemSize(3);// set default size
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
        if (items.size() == 0) {
            return 0;
        }
        return items.get(currentItemIndex).getDataIndex();
    }

    /**
     * @param dataIndex dataIndex
     */
    public void setCurrentDataIndex(int dataIndex) {
        if (adapter == null || items.size() == 0) {
            // show something
            return;
        }
        dataIndex = cycleDataIndex(dataIndex);
        currentItemIndex = 0;
        items.get(currentItemIndex).setDataIndex(dataIndex);
        items.get(currentItemIndex).setStatus(ItemWrapper.FORCE);
        noNeedLayout = false;
        requestLayout();
    }

    /**
     * @param cycleAdapter cycleAdapter
     */
    public void setAdapter(BaseAdapter cycleAdapter) {
        if (cycleAdapter == null) {
            return;
        }
        adapter = cycleAdapter;
        if (dataSetObserver != null) {
            adapter.unregisterDataSetObserver(dataSetObserver);
        }
        dataSetObserver = new AdapterDataSetObserver();
        adapter.registerDataSetObserver(dataSetObserver);
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
            return;
        }
        leftIndent = left;
        topIndent = top;
        rightIndent = right;
        bottomIndent = bottom;

        // TODO: 9/20/15 Waiting test, when call setIndent() after view measured.
        if (isAttachedToWindow) {
            requestLayout();
        }
        // END
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
     * @param listener listener
     */
    public void setOnItemSwitchListener(OnItemSwitchListener listener) {
        onItemSwitchListener = listener;
    }

    public void setAnimatorAdapter(AnimatorAdapter adapter) {
        if (adapter != null) {
            adapter.onBind(this);
        }
        if (animatorAdapter != null) {
            animatorAdapter.unBind();
        }
        animatorAdapter = adapter;
    }

    /**
     * @param size 3,5,7,9 ...
     */
    public void setRecycleItemSize(int size) {
        if (size < 3 || size % 2 == 0) {
            throw new IllegalStateException("setRecycleItemSize(): the size should be more than 3 of the odd number (3,5,7,9 ...) !");
        }
        noNeedLayout = true;
        resetAllItem();
        items.clear();
        for (int itemIndex = 0; itemIndex < size; itemIndex++) {
            ItemWrapper item = new ItemWrapper();
            if (adapter != null && adapter.getCount() > 0) {
                int dataIndex = cycleDataIndex(itemIndex);
                View convertView = adapter.getView(dataIndex, null, this);
                addView(convertView);

                item.setDataIndex(dataIndex);
                item.setView(convertView);
            }
            items.add(item);
        }
        noNeedLayout = false;
        setCurrentDataIndex(0);
    }

    public int getRecycleItemSize() {
        return items.size();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int sizeOfChild = getChildCount();
        for (int i = 0; i < sizeOfChild; i++) {
            int childWidth = getMeasuredWidth() - leftIndent - rightIndent;
            int childHeight = getMeasuredHeight() - topIndent - bottomIndent;
            getChildAt(i).measure(childWidth, childHeight);
        }
    }

    private boolean noNeedLayout = false;

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (noNeedLayout) {
            return;
        }

        int sizeOfItem = items.size();
        if (sizeOfItem == 0) {
            return;
        }
        int sizeOfSideItem = sizeOfItem / 2; // 3,5,7,9 ...
        int currentDataIndex = items.get(currentItemIndex).getDataIndex();

        int itemWidth = (r - rightIndent) - (l + leftIndent);
        int itemHeight = (b - bottomIndent) - (t + topIndent);
        int middleItemLeft = 0 + leftIndent;
        int middleItemTop = 0 + topIndent;
        int middleItemRight = (r - l) - rightIndent;
        int middleItemBottom = (b - t) - bottomIndent;

        // reset offset
        for (ItemWrapper item : items) {
            if (item.getView() != null) {
                if (orientation == LinearLayout.HORIZONTAL) {
                    item.getView().setTranslationX(0);
                } else if (orientation == LinearLayout.VERTICAL) {
                    item.getView().setTranslationY(0);
                }
            }
        }

        int offset;
        //middle align
        ItemWrapper middleItem = items.get(currentItemIndex);
        recycleItem(middleItem, cycleDataIndex(currentDataIndex));
        if (middleItem.getView() != null) {
            middleItem.getView().layout(middleItemLeft, middleItemTop, middleItemRight, middleItemBottom);
        }
        // left/top align
        int sizeOfLeft = currentItemIndex - sizeOfSideItem;
        offset = 0;
        for (int leftIndex = currentItemIndex - 1; leftIndex >= sizeOfLeft; leftIndex--) {
            offset--;
            ItemWrapper item = items.get(cycleItemIndex(leftIndex));
            recycleItem(item, cycleDataIndex(currentDataIndex + offset));

            if (item.getView() != null) {
                if (orientation == LinearLayout.HORIZONTAL) {
                    // left
                    int left = middleItemLeft + offset * (itemWidth + spaceBetweenItems);
                    item.getView().layout(left, middleItemTop, left + itemWidth, middleItemBottom);
                } else if (orientation == LinearLayout.VERTICAL) {
                    // top
                    int top = middleItemTop + offset * (itemHeight + spaceBetweenItems);
                    item.getView().layout(middleItemLeft, top, middleItemRight, top + itemHeight);
                }
            }
        }
        // right/bottom align
        int sizeOfRight = currentItemIndex + sizeOfSideItem;
        offset = 0;
        for (int rightIndex = currentItemIndex + 1; rightIndex <= sizeOfRight; rightIndex++) {
            offset++;
            ItemWrapper item = items.get(cycleItemIndex(rightIndex));
            recycleItem(item, cycleDataIndex(currentDataIndex + offset));

            if (item.getView() != null) {
                if (orientation == LinearLayout.HORIZONTAL) {
                    // right
                    int right = middleItemRight + offset * (itemWidth + spaceBetweenItems);
                    item.getView().layout(right - itemWidth, middleItemTop, right, middleItemBottom);
                } else if (orientation == LinearLayout.VERTICAL) {
                    // bottom
                    int bottom = middleItemBottom + offset * (itemHeight + spaceBetweenItems);
                    item.getView().layout(middleItemLeft, bottom - itemHeight, middleItemRight, bottom);
                }
            }
        }
    }

    protected final void recycleItem(ItemWrapper item, int targetDataIndex) {
        if (adapter == null || adapter.getCount() < 1) {
            return;
        }
        int itemStatus = item.getStatus();
        if (itemStatus != ItemWrapper.FORCE) {
            if (item.getDataIndex() == targetDataIndex && itemStatus == ItemWrapper.USING) {
                return;
            }
        }
        // refresh item
        noNeedLayout = true;
        item.setDataIndex(targetDataIndex);
        View convertView = adapter.getView(item.getDataIndex(), item.getView(), this);
        if (convertView.getParent() != this) {
            item.setView(null);// release and remove self in group
            addView(convertView);
            convertView.measure(getMeasuredWidth(), getMeasuredHeight());// the new view must be measure first
        }
        item.setView(convertView);
        noNeedLayout = false;
    }

    private PointF lastPoint = new PointF();
    private boolean isMoving = false;
    private boolean needIntercept = false;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                needIntercept = false;
                lastPoint.set(event.getX(), event.getY());
                return super.dispatchTouchEvent(event);
            case MotionEvent.ACTION_MOVE:
                float absXDiff = Math.abs(event.getX() - lastPoint.x);
                float absYDiff = Math.abs(event.getY() - lastPoint.y);
                if (orientation == LinearLayout.HORIZONTAL && absXDiff > absYDiff && absXDiff > MOVE_SLOP) {
                    needIntercept = true;
                } else if (orientation == LinearLayout.VERTICAL && absYDiff > absXDiff && absYDiff > MOVE_SLOP) {
                    needIntercept = true;
                }
                if (needIntercept) {
                    // intercept parent view gesture
                    getParent().requestDisallowInterceptTouchEvent(true);
                    // pause auto switch
                    if (isAutoCycle) {
                        removeCallbacks(autoCycleRunnable);
                    }
                }
                return super.dispatchTouchEvent(event);
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                // restart auto switch
                if (isAutoCycle) {
                    removeCallbacks(autoCycleRunnable);
                    postDelayed(autoCycleRunnable, intervalOnAutoCycle);
                }
                return super.dispatchTouchEvent(event);
            default:
                return super.dispatchTouchEvent(event);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return super.onInterceptTouchEvent(event);
            case MotionEvent.ACTION_MOVE:
                return needIntercept;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                return super.onInterceptTouchEvent(event);
            default:
                return super.onInterceptTouchEvent(event);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastPoint.set(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
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
                lastPoint.set(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                // touch release
                if (isMoving) {
                    if (items.size() == 0) {
                        isMoving = false;
                        break;
                    }
                    View centerView = items.get(currentItemIndex).getView();
                    if (centerView == null) {
                        break;
                    }
                    int changeIndex = 0;
                    float offset = 0;
                    float maxOffset = 0;
                    if (orientation == LinearLayout.HORIZONTAL) {
                        offset = centerView.getTranslationX();
                        maxOffset = centerView.getWidth() + spaceBetweenItems;
                    } else if (orientation == LinearLayout.VERTICAL) {
                        offset = centerView.getTranslationY();
                        maxOffset = centerView.getHeight() + spaceBetweenItems;
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
                return super.onTouchEvent(event);
        }
        return true;
    }

    protected final void moveX(float offset) {
        isMoving = true;
        for (ItemWrapper item : items) {
            View view = item.getView();
            if (view != null) {
                view.setTranslationX(view.getTranslationX() + offset);
            }
        }
        if (onItemSwitchListener != null) {
            onItemSwitchListener.onItemScrolled(items.get(currentItemIndex).getDataIndex(), offset);
        }
        if (animatorAdapter != null) {
            animatorAdapter.onItemScrolled(currentItemIndex, offset);
        }
    }

    protected final void moveY(float offset) {
        isMoving = true;
        for (ItemWrapper item : items) {
            View view = item.getView();
            if (view != null) {
                view.setTranslationY(view.getTranslationY() + offset);
            }
        }
        if (onItemSwitchListener != null) {
            onItemSwitchListener.onItemScrolled(items.get(currentItemIndex).getDataIndex(), offset);
        }
        if (animatorAdapter != null) {
            animatorAdapter.onItemScrolled(currentItemIndex, offset);
        }
    }

    private ValueAnimator autoScroller;

    protected final void autoMove(float offset, long duration, final int changeIndex) {
        if (offset == 0) {
            return;
        }
        if (autoScroller != null && autoScroller.isStarted()) {
            autoScroller.cancel();
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
                // only this change currentItemIndex
                int oldDataIndex = items.get(currentItemIndex).getDataIndex();
                int newDataIndex = cycleDataIndex(oldDataIndex + changeIndex);
                int oldItemIndex = currentItemIndex;
                int newItemIndex = cycleItemIndex(currentItemIndex + changeIndex);
                if (onItemSwitchListener != null) {
                    onItemSwitchListener.onItemSelected(newDataIndex, oldDataIndex);
                }
                if (animatorAdapter != null) {
                    animatorAdapter.onItemSelected(newItemIndex, oldItemIndex);
                }
                currentItemIndex = newItemIndex;
                requestLayout();
            }
        });
        autoScroller.start();
    }

    /**
     * Auto scroll to next
     */
    public void moveNext() {
        if (items.size() == 0) {
            return;
        }
        View centerView = items.get(currentItemIndex).getView();
        if (centerView == null) {
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

    /**
     * Auto scroll to previous
     */
    public void movePrevious() {
        if (items.size() == 0) {
            return;
        }
        View centerView = items.get(currentItemIndex).getView();
        if (centerView == null) {
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

    protected final int cycleDataIndex(int dataIndex) {
        if (adapter == null) {
            return 0;
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

    protected final View getItemView(int itemIndex) {
        if (itemIndex < 0 || itemIndex > items.size() - 1) {
            return null;
        }
        return items.get(itemIndex).getView();
    }

    protected final void resetAllItem() {
        noNeedLayout = true;
        for (ItemWrapper item : items) {
            item.recycle();
        }
        noNeedLayout = false;
        currentItemIndex = 0;
    }

    private final class AdapterDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            resetAllItem();
            requestLayout();
        }

        @Override
        public void onInvalidated() {
            resetAllItem();
            requestLayout();
        }
    }

    /**
     * Wrapper (contains data id and status)
     */
    private final class ItemWrapper {

        public static final int NONE = 0x0;
        public static final int USING = 0x1;
        public static final int FORCE = 0x2;

        private int status;
        private int dataIndex;
        private View view;

        public ItemWrapper() {
            this.status = NONE;
            this.dataIndex = 0;
            this.view = null;
        }

        public ItemWrapper(int index, View v) {
            this.dataIndex = index;
            this.view = v;
            this.status = NONE;
        }

        public void recycle() {
            setView(null);
            setDataIndex(0);
            setStatus(NONE);
        }

        public View getView() {
            return view;
        }

        public void setView(View v) {
            if (v == null && view != null) {
                ((ViewGroup) view.getParent()).removeView(view);
            }
            this.view = v;
        }

        public void setDataIndex(int dataIndex) {
            this.dataIndex = dataIndex;
            this.status = USING;
        }

        public int getDataIndex() {
            return dataIndex;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

    }

    /**
     * Event callback
     */
    public static interface OnItemSwitchListener {

        public void onItemSelected(int newDataIndex, int oldDataIndex);

        public void onItemScrolled(int dataIndex, float offset);

    }

    /**
     * Item view animator adapter
     */
    public static abstract class AnimatorAdapter {

        private SweetCircularView circularView;

        protected void onBind(SweetCircularView circularView) {
            this.circularView = circularView;
        }

        protected void unBind() {
            this.circularView = null;
        }

        public final SweetCircularView getView() {
            return circularView;
        }

        protected final int cycleItemIndex(int itemIndex) {
            return circularView.cycleItemIndex(itemIndex);
        }

        protected final View getItemView(int itemIndex) {
            return circularView.getItemView(itemIndex);
        }

        public abstract void onItemSelected(int newItemIndex, int oldItemIndex);

        public abstract void onItemScrolled(int itemIndex, float offset);

    }

}