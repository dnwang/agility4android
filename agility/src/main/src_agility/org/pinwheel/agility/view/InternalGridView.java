package org.pinwheel.agility.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved
 *
 * @author dnwang
 */
public class InternalGridView extends GridView {

    public InternalGridView(Context context) {
        super(context);
    }

    public InternalGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InternalGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

}
