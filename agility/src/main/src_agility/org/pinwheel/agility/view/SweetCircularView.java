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
import java.util.Collections;
import java.util.Comparator;

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
    private long intervalOnAutoCycle = 4000;
    private long durationOnAutoScroll = 300;
    private long durationOnTouchRelease = 200;
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
        align(currentItemIndex, dataIndex);
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

        int size = getChildCount();
        for (int i = 0; i < size; i++) {
            View v = getChildAt(i);
            LayoutParams params = v.getLayoutParams();
            params.width = getMeasuredWidth() - leftIndent - rightIndent;
            params.height = getMeasuredHeight() - topIndent - bottomIndent;
            v.setLayoutParams(params);
        }
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
        for (ItemWrapper item : items) {
            item.recycle();
        }
        items.clear();
        for (int itemIndex = 0; itemIndex < size; itemIndex++) {
            items.add(new ItemWrapper());
        }
        setCurrentDataIndex(0);
    }

//    protected final void recycleItem(ItemWrapper item, int targetDataIndex) {
//        if (adapter == null) {
//            return;
//        }
//        if (item.getDataIndex() == targetDataIndex && item.isUsing()) {
//            return;
//        }
//        // refresh item
//        if (targetDataIndex >= adapter.getCount()) {
//            item.setView(null, targetDataIndex);
//        } else {
//            View convertView = adapter.getView(targetDataIndex, item.getView(), this);
//            item.setView(convertView, targetDataIndex);
//        }
//    }

    public final int getRecycleItemSize() {
        return items.size();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
    }

    protected final void align(int centerItemIndex, int centerDataIndex) {
        alignAllItemDataIndex(centerItemIndex, centerDataIndex);
        alignAllItemView();
        alignAllItemPosition(centerItemIndex);
    }

    protected final void alignAllItemDataIndex(int centerItemIndex, int centerDataIndex) {
        final int sizeOfItem = getRecycleItemSize();
        if (sizeOfItem == 0) {
            return;
        }
        centerItemIndex = cycleItemIndex(centerItemIndex);
        centerDataIndex = cycleDataIndex(centerDataIndex);
        // center
        items.get(centerItemIndex).setDataIndex(centerDataIndex);

        final int sizeOfSideItem = sizeOfItem / 2; // 3,5,7,9 ...
        int dataIndex;
        int itemIndex;
        // left
        for (int i = 1; i < sizeOfSideItem; i++) {
            dataIndex = cycleDataIndex(centerDataIndex - i);
            itemIndex = cycleItemIndex(centerItemIndex - i);
            items.get(itemIndex).setDataIndex(dataIndex);
        }
        // right
        for (int i = 1; i < sizeOfSideItem; i++) {
            dataIndex = cycleDataIndex(centerDataIndex + i);
            itemIndex = cycleItemIndex(centerItemIndex + i);
            items.get(itemIndex).setDataIndex(dataIndex);
        }
    }

    protected final void alignAllItemView() {
        if (adapter == null) {
            return;
        }
        for (ItemWrapper item : items) {
            item.refreshView();
        }
    }

    protected final void alignAllItemPosition(int centerItemIndex) {
        final int sizeOfItem = getRecycleItemSize();
        if (sizeOfItem == 0) {
            return;
        }
        // sort
        Collections.sort(items, COMPARATOR);

        centerItemIndex = cycleItemIndex(centerItemIndex);
        ItemWrapper centerItem = items.get(centerItemIndex);

        final int itemWidth = centerItem.getView().getMeasuredWidth();
        final int itemHeight = centerItem.getView().getMeasuredHeight();
        final int centerX = leftIndent;
        final int centerY = topIndent;

        // center
        if (orientation == LinearLayout.HORIZONTAL) {
            centerItem.moveX(centerX);
        } else if (orientation == LinearLayout.VERTICAL) {
            centerItem.moveY(centerY);
        }

        final int sizeOfSideItem = sizeOfItem / 2; // 3,5,7,9 ...
        int itemIndex;
        int x, y;
        // left/top
        for (int i = 1; i < sizeOfSideItem; i++) {
            itemIndex = cycleItemIndex(centerItemIndex - i);
            ItemWrapper item = items.get(cycleItemIndex(itemIndex));
            if (orientation == LinearLayout.HORIZONTAL) {
                // left
                x = centerX + (-i) * (itemWidth + spaceBetweenItems);
                item.moveX(x);
            } else if (orientation == LinearLayout.VERTICAL) {
                // top
                y = centerY + (-i) * (itemHeight + spaceBetweenItems);
                item.moveY(y);
            }
        }
        // right/bottom
        for (int i = 1; i < sizeOfSideItem; i++) {
            itemIndex = cycleItemIndex(centerItemIndex + i);
            ItemWrapper item = items.get(cycleItemIndex(itemIndex));
            if (orientation == LinearLayout.HORIZONTAL) {
                // right
                x = centerX + (i) * (itemWidth + spaceBetweenItems);
                item.moveX(x);
            } else if (orientation == LinearLayout.VERTICAL) {
                // bottom
                y = centerY + (i) * (itemHeight + spaceBetweenItems);
                item.moveY(y);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int sizeOfItem = getRecycleItemSize();
        if (sizeOfItem == 0) {
            return;
        }
        alignAllItemDataIndex(currentItemIndex, items.get(currentItemIndex).getDataIndex());
        alignAllItemView();


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
            item.moveX(0.0f);
            item.moveY(0.0f);
        }

        int offset;
        //middle align
        ItemWrapper middleItem = items.get(currentItemIndex);
//        recycleItem(middleItem, cycleDataIndex(currentDataIndex));
        if (middleItem.getView() != null) {
            middleItem.getView().layout(middleItemLeft, middleItemTop, middleItemRight, middleItemBottom);
        }
        // left/top align
        int sizeOfLeft = currentItemIndex - sizeOfSideItem;
        offset = 0;
        for (int leftIndex = currentItemIndex - 1; leftIndex >= sizeOfLeft; leftIndex--) {
            offset--;
            ItemWrapper item = items.get(cycleItemIndex(leftIndex));
//            recycleItem(item, cycleDataIndex(currentDataIndex + offset));

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
//            recycleItem(item, cycleDataIndex(currentDataIndex + offset));

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

    protected final void moveX(float offset) {
        isMoving = true;
        for (ItemWrapper item : items) {
            item.moveX(item.getOffsetX() + offset);
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
            item.moveY(item.getOffsetY() + offset);
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
        if (autoScroller != null && autoScroller.isStarted()) {
            autoScroller.cancel();
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

                align(newItemIndex, newDataIndex);
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

    public final View getItemView(int itemIndex) {
        return items.get(cycleItemIndex(itemIndex)).getView();
    }

    protected LayoutParams generateDefaultLayoutParams() {
        int width = getMeasuredWidth() - leftIndent - rightIndent;
        int height = getMeasuredHeight() - topIndent - bottomIndent;
        return new ViewGroup.LayoutParams(width, height);
    }

    private final class AdapterDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            currentItemIndex = 0;
            for (ItemWrapper item : items) {
                item.recycle();
            }
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
            this.dataIndex = 0;
            this.view = null;
        }

        public void moveX(float x) {
            if (view != null) {
                view.setTranslationX(x);
            }
        }

        public void moveY(float y) {
            if (view != null) {
                view.setTranslationY(y);
            }
        }

        public float getOffsetX() {
            return view == null ? 0 : view.getTranslationX();
        }

        public float getOffsetY() {
            return view == null ? 0 : view.getTranslationY();
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
            if (adapter != null && state == NONE) {
                View convertView = adapter.getView(dataIndex, view, SweetCircularView.this);
                if (convertView == view) {
                    // nothing to do
                } else {
                    // remove old view
                    if (view != null && view.getParent() != null) {
                        ((ViewGroup) view.getParent()).removeView(view);
                    }
                    // add new view
                    if (convertView != null) {
                        if (convertView.getParent() != SweetCircularView.this) {
                            addView(convertView);
                        }
                        LayoutParams params = convertView.getLayoutParams();
                        convertView.measure(MeasureSpec.makeMeasureSpec(params.width, MeasureSpec.EXACTLY),
                                MeasureSpec.makeMeasureSpec(params.height, MeasureSpec.EXACTLY));
                    }
                }
                view = convertView;
            }
        }

        public void recycle() {
            if (view != null && view.getParent() != null) {
                ((ViewGroup) view.getParent()).removeView(view);
            }
            view = null;
            state = NONE;
            dataIndex = 0;
        }
    }

    private static final Comparator<ItemWrapper> COMPARATOR = new Comparator<ItemWrapper>() {
        @Override
        public int compare(ItemWrapper lhs, ItemWrapper rhs) {
            return lhs.dataIndex - rhs.dataIndex;
        }
    };

    /**
     * Event callback
     */
    public interface OnItemSwitchListener {

        void onItemSelected(int newDataIndex, int oldDataIndex);

        void onItemScrolled(int dataIndex, float offset);

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

        protected final SweetCircularView getView() {
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
