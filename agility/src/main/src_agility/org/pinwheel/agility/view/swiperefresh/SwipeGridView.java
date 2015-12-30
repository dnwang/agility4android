package org.pinwheel.agility.view.swiperefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public class SwipeGridView extends AbsSwipeAbsListView {

    private GridView mGridView;

    public SwipeGridView(Context context) {
        this(context, null);
    }

    public SwipeGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected GridView createRefreshableView(Context context, AttributeSet attrs) {
        this.mGridView = new GridView(context);
        return this.mGridView;
    }

    public void setNumColumnsAndNormalStyle(int numColumns) {
        mGridView.setNumColumns(numColumns);
        this.setNormalStyle();
    }

    public void setNumColumns(int numColumns) {
        mGridView.setNumColumns(numColumns);
    }

    public int getNumColumns(){
        return mGridView.getNumColumns();
    }

}
