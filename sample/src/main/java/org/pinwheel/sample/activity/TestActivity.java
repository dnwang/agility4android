package org.pinwheel.sample.activity;

import android.view.View;

import org.pinwheel.sample.R;
import org.pinwheel.sample.test.RandomScrollerView;

import java.util.Random;

/**
 * Copyright (C), 2016 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public class TestActivity extends AbsTesterActivity {

    RandomScrollerView[] customView;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_test);
    }

    @Override
    protected void beforeInitView() {
    }

    @Override
    protected void afterInitView() {
        customView = new RandomScrollerView[4];
        customView[0] = holder.getView(R.id.view0);
        customView[1] = holder.getView(R.id.view1);
        customView[2] = holder.getView(R.id.view2);
        customView[3] = holder.getView(R.id.view3);
        holder.select(R.id.btn_start).setOnClickListener(v -> {
            holder.select(R.id.btn_stop).setText("stop");
            for (int i = 0; i < customView.length; i++) {
                final int index = i;
                postDelayed(() -> customView[index].start(), i * 726);
            }
        });

        holder.select(R.id.btn_stop).setOnClickListener(v -> {
            final Random random = new Random();
            String txt = "";
            for (int i = 0; i < customView.length; i++) {
                final int index = i;
                final int target = random.nextInt(10);
                txt += String.valueOf(target);
                postDelayed(() -> customView[index].stop(target), i * 536);
            }
            holder.select(R.id.btn_stop).setText(txt);
        });
    }
}
