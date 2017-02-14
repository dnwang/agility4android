package org.pinwheel.sample4agility.activity;

import android.content.res.Configuration;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;

import org.pinwheel.agility.view.BouncyEditText;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved
 *
 * @author dnwang
 */
public class BouncyEditAndTextActivity extends AbsTesterActivity {

    BouncyEditText bouncyEditText;

    @Override
    protected void beforeInitView() {

    }

    @Override
    protected View getContentView() {
        bouncyEditText = new BouncyEditText(this);
        bouncyEditText.setHintText("im hint");
        bouncyEditText.setHintTextColor(Color.BLUE);

        LinearLayout container = new LinearLayout(this);
        container.setVerticalGravity(LinearLayout.VERTICAL);
        container.addView(bouncyEditText, -1, -2);
        return container;
    }

    @Override
    protected void afterInitView() {

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

}
