package org.pinwheel.agility.view.swiperefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public class SwipeListView extends AbsSwipeAbsListView {

    private ListView mListView;

    public SwipeListView(Context context) {
        this(context, null);
    }

    public SwipeListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected ListView createRefreshableView(Context context, AttributeSet attrs) {
        this.mListView = new ListView(context);
        return this.mListView;
    }

    @Override
    public void setNormalStyle() {
        super.setNormalStyle();
        this.mListView.setDivider(null);
    }

    public void addHeaderView(View view) {
        this.mListView.addHeaderView(view);
    }

    public void addFooterView(View view) {
        this.mListView.addFooterView(view);
    }

    @Override
    public ListView getRefreshableView() {
        return mListView;
    }
}
