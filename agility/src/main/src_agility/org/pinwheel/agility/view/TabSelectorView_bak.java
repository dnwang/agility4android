package org.pinwheel.agility.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import org.pinwheel.agility.util.BitmapUtils;
import org.pinwheel.agility.view.controller.TabController;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 版权所有 (C), 2014 <br>
 * <br>
 * All rights reserved
 *
 * @author dnwang
 * @date 2015/2/13 19:25
 * @description
 */
@Deprecated
class TabSelectorView_bak extends View implements TabController.ISelectable {

    private ArrayList<Rect> rectList;
    private int currentIndex;
    private Bitmap selector;
    private int refresh_interval = 10;

    public TabSelectorView_bak(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        rectList = new ArrayList<Rect>(0);

        try {
            setSelector(getBackground());
        } catch (Exception e) {
        }
    }

    @Override
    public void reset(Object args) {

    }

    @Override
    public void replaceAll(ArrayList<Rect> rectList) {
        removeAll();
        this.rectList.addAll(rectList);
    }

    @Override
    public void addRect(int index, Rect rect) {
        rectList.add(rect);
    }

    @Override
    public void remove(int index) {
        rectList.remove(index);
    }

    @Override
    public void removeAll() {
        rectList.clear();
    }

    @Override
    public int getCurrentRect(Rect copy_rect) {
        if (copy_rect != null) {
            copy_rect.set(rectList.get(currentIndex));
        }
        return currentIndex;
    }

    @Override
    public void setSelector(Drawable selector) {
//        Bitmap bitmap = ((BitmapDrawable) selector).getBitmap();
        this.selector = ((BitmapDrawable) selector).getBitmap();
//        this.selector = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), false);
//        if (!bitmap.isRecycled()) {
//            bitmap.recycle();
//        }
    }

    @Override
    public void setDuration(long duration) {

    }

    private Rect tempRect = new Rect();
    private Rect toRect = new Rect();

    private int rate_left, rate_top, rate_right, rate_bottom;

    private Timer moveTimer = null;
    private MoveTask moveTask = null;

    @Override
    public void moveTo(int from_index, int to_index, float positionOffset) {
        // TODO denan.wang; 2015/2/14;
    }

    @Override
    public void moveTo(int to_index, boolean smoothScroll) {
        if (to_index < 0 || to_index >= rectList.size() || to_index == currentIndex) {
            return;
        }
//        tempRect.set(
//                tempRect.left,
//                tempRect.top,
//                tempRect.left + selector.getWidth(),
//                tempRect.top + selector.getHeight());

        toRect = rectList.get(to_index);

        Rect parentRect = new Rect();
        getGlobalVisibleRect(parentRect);

        rate_left = (toRect.left - tempRect.left) / refresh_interval;
        rate_top = (toRect.top - tempRect.top) / refresh_interval;
        rate_right = (toRect.right - tempRect.right) / refresh_interval;
        rate_bottom = (toRect.bottom - tempRect.bottom) / refresh_interval;

        stopMove();
        moveTask = new MoveTask();
        moveTimer = new Timer();
        moveTimer.schedule(moveTask, 0, refresh_interval);

        currentIndex = to_index;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width;
        int height;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            if (selector == null) {
                width = 0;
            } else {
                float textWidth = selector.getWidth();
                width = (int) (getPaddingLeft() + textWidth + getPaddingRight());
            }
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            if (selector == null) {
                height = 0;
            } else {
                float textHeight = selector.getHeight();
                height = (int) (getPaddingTop() + textHeight + getPaddingBottom());
            }
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (selector != null && !rectList.isEmpty()) {
            int dx = toRect.centerX() - tempRect.centerX();
            int dy = toRect.centerY() - tempRect.centerY();

            if (Math.abs(dx) <= refresh_interval && Math.abs(dy) <= refresh_interval) {
                tempRect.set(
                        toRect.left,
                        toRect.top,
                        toRect.right,
                        toRect.bottom);
                stopMove();
            } else {
                tempRect.set(
                        tempRect.left + rate_left,
                        tempRect.top + rate_top,
                        tempRect.right + rate_right,
                        tempRect.bottom + rate_bottom);
            }

//            canvas.drawColor(Color.RED);

            Bitmap tempBitmap = BitmapUtils.setScale(selector, tempRect.right - tempRect.left, tempRect.bottom - tempRect.top);

//            int width = tempRect.right - tempRect.left;
//            int height = tempRect.bottom - tempRect.top;

//            selector.setWidth(width > 0 ? width : 1);
//            selector.setHeight(height > 0 ? height : 1);
            if (tempBitmap != null) {
                canvas.drawBitmap(tempBitmap, tempRect.left, tempRect.top, null);
            }
        }
    }

    private void stopMove() {
        if (moveTimer != null) {
            moveTimer.cancel();
            moveTimer = null;
        }
        if (moveTask != null) {
            moveTask.cancel();
            moveTask = null;
        }
    }

    private class MoveTask extends TimerTask {
        @Override
        public void run() {
            postInvalidate();
        }
    }

}
