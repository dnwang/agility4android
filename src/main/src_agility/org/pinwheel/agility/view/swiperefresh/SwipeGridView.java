package org.pinwheel.agility.view.swiperefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * 版权所有 (C), 2014 <br>
 * <br>
 * All rights reserved
 *
 * @author dnwang
 * @date 2014/8/12 17:03
 * @description
 */
public final class SwipeGridView extends AbsSwipeAbsListView {

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
