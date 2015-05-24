package org.pinwheel.agility.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import org.pinwheel.agility.animation.TouchAnimator;

/**
 * 版权所有 (C), 2014 <br>
 * <br>
 * All rights reserved
 *
 * @author dnwang
 * @date 2015/3/10 19:19
 * @description
 */
public class AnimatorWrapper extends FrameLayout {

    protected TouchAnimator touchAnimator;

    public AnimatorWrapper(Context context) {
        super(context);
        init(context);
    }

    public AnimatorWrapper(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AnimatorWrapper(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        touchAnimator = new TouchAnimator(this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (touchAnimator != null) {
            touchAnimator.onTouchEvent(event);
        }
        return super.dispatchTouchEvent(event);
    }

}
