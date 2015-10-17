package org.pinwheel.demo4agility.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import org.pinwheel.agility.util.UIUtils;

/**
 * 版权所有 (C), 2014 <br>
 * <br>
 * All rights reserved
 *
 * @author dnwang
 * @date 2015/3/12 23:02
 * @description
 */
public class CustomViewActivity extends Activity {

    private int radius = 160;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View contentView = createView();

        setContentView(contentView);
    }

    private View createView() {
        FrameLayout container = new FrameLayout(this);

        radius = UIUtils.dip2px(this, radius);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(radius * 2, radius * 2);
        params.gravity = Gravity.CENTER;
        container.addView(new CustomView(this), params);

        return container;
    }

    private class CustomView extends View {

        private final int focus_top = org.pinwheel.demo4agility.R.drawable.bg_focus_top;
        private final int focus_down = org.pinwheel.demo4agility.R.drawable.bg_focus_down;
        private final int focus_right = org.pinwheel.demo4agility.R.drawable.bg_focus_right;
        private final int focus_left = org.pinwheel.demo4agility.R.drawable.bg_focus_left;
        private final int focus_null = org.pinwheel.demo4agility.R.drawable.bg_focus_null;
        private final int focus_ok = org.pinwheel.demo4agility.R.drawable.bg_focus_ok;

        private int radius_center = 46;

        public CustomView(Context context) {
            super(context);
            radius_center = UIUtils.dip2px(context, radius_center);
            resetEvent(null);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            MeasureSpec.getSize(widthMeasureSpec);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downEvent(event);
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_OUTSIDE:
                    resetEvent(event);
                    return true;
            }
            return super.onTouchEvent(event);
        }

        private void downEvent(MotionEvent event) {
            if (toucheCenter(event)) {
                setBackgroundResource(focus_ok);
            } else {
                int location = toucheOutSide(event);
                switch (location) {
                    case 1:
                        setBackgroundResource(focus_left);
                        break;
                    case 2:
                        setBackgroundResource(focus_top);
                        break;
                    case 3:
                        setBackgroundResource(focus_right);
                        break;
                    case 4:
                        setBackgroundResource(focus_down);
                        break;
                    default:
                        resetEvent(event);
                        break;
                }
            }
        }

        private void resetEvent(MotionEvent event) {
            setBackgroundResource(focus_null);
        }

        private boolean toucheCenter(MotionEvent event) {
            Rect bound = new Rect();
//            getGlobalVisibleRect(bound);
            bound.set(
                    0,
                    0,
                    radius * 2,
                    radius * 2
            );

            int dx = Math.abs(bound.centerX() - (int) event.getX());
            int dy = Math.abs(bound.centerY() - (int) event.getY());
            int distance = (int) Math.sqrt(dx * dx + dy * dy);

            if (distance < radius_center) {
                return true;
            } else {
                return false;
            }
        }

        private int toucheOutSide(MotionEvent event) {
            Rect bound = new Rect();
//            getGlobalVisibleRect(bound);
            bound.set(
                    0,
                    0,
                    radius * 2,
                    radius * 2
            );

            int radius = Math.abs(bound.right - bound.left) / 2;

            int dx = Math.abs((int) event.getX() - bound.centerX());
            int dy = Math.abs((int) event.getY() - bound.centerY());
            int distance = (int) Math.sqrt(dx * dx + dy * dy);

            if (distance > radius) {
                // outside
                return 0;
            }

            float tan = (bound.centerY() - event.getY()) / (bound.centerX() - event.getX());

            if (tan > -0.7 && tan < 0.7) {
                if ((int) event.getX() - bound.centerX() > 0) {
                    return 3;// right
                } else {
                    return 1;// left
                }
            } else {
                if ((int) event.getY() - bound.centerY() > 0) {
                    return 4;// bottom
                } else {
                    return 2;// top
                }
            }

        }
    }

}
