package org.pinwheel.agility.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.widget.ListView;
import android.widget.ScrollView;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved
 *
 * @author dnwang
 */
public class InternalListView extends ListView {

    public InternalListView(Context context) {
        super(context);
        registerScrollToTop();
    }

    public InternalListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        registerScrollToTop();
    }

    public InternalListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        registerScrollToTop();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public InternalListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        registerScrollToTop();
    }

    private void registerScrollToTop() {
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (scrollView == null) {
                    ViewParent container = getParent();
                    if (container != null) {
                        ViewParent scrollview = container.getParent();
                        if (scrollview != null && scrollview instanceof ScrollView) {
                            InternalListView.this.scrollView = (ScrollView) scrollview;
                        }
                    }
                }
                if (scrollView != null) {
                    post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.scrollTo(0, 0);
                            scrollView = null;
                        }
                    });
                }
                getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    private ScrollView scrollView;

    public void setConatainer(ScrollView scrollView) {
        this.scrollView = scrollView;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
