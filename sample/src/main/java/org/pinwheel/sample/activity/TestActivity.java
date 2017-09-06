package org.pinwheel.sample.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
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

    private PhotoPicker picker;
    private ImageView imageView;

    @Override
    protected View getContentView() {
        imageView = new ImageView(this);
        return imageView;
    }

    @Override
    protected void beforeInitView() {
        picker = new PhotoPicker(this);
        picker.setOnPickListener((file, err) -> {
            if (null != file) {
                Toast.makeText(this, file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                imageView.setImageBitmap(bitmap);
            } else {
                Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void afterInitView() {
        requestPermissions((isSuccess) -> {
            imageView.setOnClickListener(v -> {
                picker.show(PhotoPicker.Type.PICK_PHOTO);
            });
            imageView.setOnLongClickListener(view -> {
                picker.show(PhotoPicker.Type.TAKE_PHOTO);
                return false;
            });
        }, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        picker.onActivityResult(requestCode, resultCode, data);
    }
}
