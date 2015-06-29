package org.pinwheel.demo4agility.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public class PosterView extends ViewGroup {

    private NetworkImageView[] views;
    private List<Object> images;

    int current_view;
    int current_image;

    public PosterView(Context context) {
        super(context);
        this.init(context);
    }

    public PosterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(context);
    }

    public PosterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PosterView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.init(context);
    }

    private void init(Context context) {
        int size_of_views = 3;
        images = new ArrayList<>();
        views = new NetworkImageView[size_of_views];
        LayoutParams fillParams = new ViewGroup.LayoutParams(-1, -1);
        for (int i = 0; i < size_of_views; i++) {
            views[i] = new NetworkImageView(context);
            addView(views[i], fillParams);
        }
        current_image = 0;
        current_view = 0;

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                PosterView.this.onGlobalLayout();
                getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    private void onGlobalLayout() {

    }

    @Override
    public boolean onDragEvent(DragEvent event) {
        return super.onDragEvent(event);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        if(!changed) {
//            return;
//        }
//        for (int x = 0; x < column; x++) {
//            for (int y = 0; y < row; y++) {
//                View item = items[x][y] ;
//                if(item == null)
//                    continue ;
//                int ll = x*width ;
//                int tt = y*height ;
//                item.layout(ll, tt, ll+width, tt+height) ;
//            }
//        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
        // all child's bound is
        int size = getChildCount();
        for (int i = 0; i < size; i++) {
            getChildAt(i).measure(width, height);
        }
    }


    public void addImageRes(Object res) {
        if (res == null) {
            return;
        }
        images.add(res);
        notifyDataSetChanged();
    }

    public void addImageRes(List res) {
        if (res == null || res.isEmpty()) {
            return;
        }
        images.addAll(res);
        notifyDataSetChanged();
    }

    public void removeRes(int index) {
        int size_of_images = images.size();
        if (index < 0 || index > size_of_images - 1) {
            return;
        }
        images.remove(index);
        notifyDataSetChanged();
    }

    public void notifyDataSetChanged() {
        //todo reset poster
    }

    private void resetViewsLocation() {

    }

}
