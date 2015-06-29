package org.pinwheel.demo4agility.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.FrameLayout;

import org.pinwheel.demo4agility.view.IndicatorView;
import org.pinwheel.demo4agility.view.PosterView;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved
 *
 * @author dnwang
 */
public class IndicatorActivity extends Activity {

    PosterView posterView;
    IndicatorView indicatorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initPoster();
        initIndicator();

        FrameLayout container = new FrameLayout(this);
        FrameLayout.LayoutParams posterParams = new FrameLayout.LayoutParams(-1, -1);
        container.addView(posterView, posterParams);

        FrameLayout.LayoutParams indicatorParams = new FrameLayout.LayoutParams(-2, -2);
        indicatorParams.gravity = Gravity.BOTTOM;
        container.addView(indicatorView, indicatorParams);

        setContentView(container);
    }

    private void initPoster() {
        posterView = new PosterView(this);
        posterView.addImageRes("http://b.hiphotos.baidu.com/image/pic/item/5fdf8db1cb134954181e506c544e9258d1094ab1.jpg");
        posterView.addImageRes("http://h.hiphotos.baidu.com/image/pic/item/960a304e251f95ca5e4880b7cb177f3e67095275.jpg");
        posterView.addImageRes("http://b.hiphotos.baidu.com/image/pic/item/dcc451da81cb39dbbf3c9d54d2160924ab183037.jpg");
        posterView.addImageRes("http://d.hiphotos.baidu.com/image/pic/item/10dfa9ec8a136327814fb12b938fa0ec08fac70c.jpg");
        posterView.addImageRes("http://g.hiphotos.baidu.com/image/pic/item/faedab64034f78f041c591f27b310a55b2191ce0.jpg");
    }

    private void initIndicator() {
        indicatorView = new IndicatorView(this);
    }

}
