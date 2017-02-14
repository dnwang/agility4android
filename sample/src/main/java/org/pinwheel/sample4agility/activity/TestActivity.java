package org.pinwheel.sample4agility.activity;

import android.Manifest;
import android.view.View;
import android.widget.Toast;

/**
 * Copyright (C), 2016 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public class TestActivity extends AbsTesterActivity {

    @Override
    protected View getContentView() {
        return null;
    }

    @Override
    protected void beforeInitView() {

    }

    @Override
    protected void afterInitView() {
        requestPermissions((isSuccess) -> {
            Toast.makeText(this, String.valueOf(isSuccess), Toast.LENGTH_SHORT).show();
        }, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_WIFI_STATE);
    }

}
