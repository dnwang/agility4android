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

    private long durationOnAutoScroll = 350l;
    private long durationOnTouchRelease = 200l;
    private float sensibility = 0.5f;
    private int orientation = LinearLayout.HORIZONTAL;
    private OnItemSwitchListener onItemSwitchListener;

    protected AdapterDataSetObserver dataSetObserver;
    protected BaseAdapter adapter;
    protected ArrayList<ItemWrapper> items = new ArrayList<ItemWrapper>();
    protected int currentItemIndex = 0;

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

    public int getCurrentIndex() {
        if (items.size() == 0) {
            return 0;
        }
        return items.get(currentItemIndex).getDataIndex();
    }

    public void setCurrentIndex(int dataIndex) {
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

    public void setOnItemSwitchListener(OnItemSwitchListener listener) {
        onItemSwitchListener = listener;
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
                View convertView = adapter.getView(cycleDataIndex(item.getDataIndex()), null, this);
                addView(convertView);

                item.setDataIndex(dataIndex);
                item.setView(convertView);
            }
            items.add(item);
        }
        noNeedLayout = false;
        setCurrentIndex(0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int sizeOfChild = getChildCount();
        for (int i = 0; i < sizeOfChild; i++) {
            getChildAt(i).measure(getMeasuredWidth(), getMeasuredHeight());
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

        int itemWidth = r - l;
        int itemHeight = b - t;
        int middleItemLeft = 0;
        int middleItemTop = 0;
        int middleItemRight = itemWidth;
        int middleItemBottom = itemHeight;

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
                    int left = middleItemLeft + offset * itemWidth;
                    item.getView().layout(left, middleItemTop, left + itemWidth, middleItemBottom);
                } else if (orientation == LinearLayout.VERTICAL) {
                    // top
                    int top = middleItemTop + offset * itemHeight;
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
                    int right = middleItemRight + offset * itemWidth;
                    item.getView().layout(right - itemWidth, middleItemTop, right, middleItemBottom);
                } else if (orientation == LinearLayout.VERTICAL) {
                    // bottom
                    int bottom = middleItemBottom + offset * itemHeight;
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
//                lastPoint.set(event.getX(), event.getY());// must be remove this
                return super.dispatchTouchEvent(event);
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
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
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
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
                        maxOffset = centerView.getWidth();
                    } else if (orientation == LinearLayout.VERTICAL) {
                        offset = centerView.getTranslationY();
                        maxOffset = centerView.getHeight();
                    }
                    if (offset < -maxOffset * sensibility) {
                        changeIndex = 1;
                    } else if (offset > maxOffset * sensibility) {
                        changeIndex = -1;
                    }
                    if (changeIndex == 0) {
                        autoMove(-offset, durationOnTouchRelease, 0);
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
            onItemSwitchListener.onItemScrolled(items.get(currentItemIndex).getDataIndex(), currentItemIndex, offset);
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
            onItemSwitchListener.onItemScrolled(items.get(currentItemIndex).getDataIndex(), currentItemIndex, offset);
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
                    onItemSwitchListener.onItemSelected(newDataIndex, oldDataIndex, newItemIndex, oldItemIndex);
                }
                currentItemIndex = newItemIndex;
                requestLayout();
            }
        });
        autoScroller.start();
    }

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
            offset = centerView.getWidth();
        } else if (orientation == LinearLayout.VERTICAL) {
            offset = centerView.getHeight();
        }
        autoMove(-offset, durationOnAutoScroll, 1);
    }

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
            offset = centerView.getWidth();
        } else if (orientation == LinearLayout.VERTICAL) {
            offset = centerView.getHeight();
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
            dataIndex = count + dataIndex % count;
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
            itemIndex = count + itemIndex % count;
        }
        return itemIndex;
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

    public static interface OnItemSwitchListener {

        public void onItemSelected(int newDataIndex, int oldDataIndex, int newItemIndex, int oldItemIndex);

        public void onItemScrolled(int dataIndex, int itemIndex, float offset);

    }

}
