package org.pinwheel.sample.activity;

import android.Manifest;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.pinwheel.agility.compat.PhotoPicker;

/**
 * Copyright (C), 2016 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public class TestActivity extends AbsTesterActivity {


    private PhotoPicker photoPicker;

    @Override
    protected View getContentView() {
        Button button = new Button(this);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoPicker.show(PhotoPicker.Type.PICK_PHOTO);
            }
        });
        return button;
    }

    @Override
    protected void beforeInitView() {
        photoPicker = new PhotoPicker(this);
        photoPicker.setOnPickListener((file, err) -> {
            if (null != file) {
                Toast.makeText(TestActivity.this, file.getName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void afterInitView() {
        requestPermissions((isSuccess) -> {
            Toast.makeText(this, String.valueOf(isSuccess), Toast.LENGTH_SHORT).show();
        }, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_WIFI_STATE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        photoPicker.onActivityResult(requestCode, resultCode, data);
    }
}
