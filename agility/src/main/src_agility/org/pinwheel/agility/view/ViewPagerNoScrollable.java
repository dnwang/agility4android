package org.pinwheel.agility.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 版权所有 (C), 2014 <br>
 * <br>
 * All rights reserved
 *
 * @author dnwang
 * @date 2014/11/3 20:46
 * @description 不可左右滑动的ViewPager
 */
public class ViewPagerNoScrollable extends android.support.v4.view.ViewPager {

    public ViewPagerNoScrollable(Context context) {
        super(context);
    }

    public ViewPagerNoScrollable(Context context, AttributeSet attr) {
        super(context, attr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }
}
