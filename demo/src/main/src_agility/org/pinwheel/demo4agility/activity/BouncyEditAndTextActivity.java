package org.pinwheel.demo4agility.activity;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;

import org.pinwheel.agility.view.BouncyEditText;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved
 *
 * @author dnwang
 */
public class BouncyEditAndTextActivity extends Activity {

    BouncyEditText bouncyEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();

        LinearLayout container = new LinearLayout(this);
        container.setVerticalGravity(LinearLayout.VERTICAL);
        container.addView(bouncyEditText, -1, -2);

        setContentView(container);
    }

    private void init() {
        bouncyEditText = new BouncyEditText(this);
        bouncyEditText.setHintText("im hint");
        bouncyEditText.setHintTextColor(Color.BLUE);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
