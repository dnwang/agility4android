package org.pinwheel.agility.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.lang.ref.SoftReference;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
final class ImageLoaderUtils {

    private ImageLoaderUtils() {

    }

    public static String convertUrl(String key) {
//        return String.valueOf(key.hashCode());
        return md5(key);
    }

    private static String md5(String data) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(data.getBytes());

            byte[] digest = mDigest.digest();
            StringBuilder sb = new StringBuilder();
            for (byte aDigest : digest) {
                String hex = Integer.toHexString(0xFF & aDigest);
                if (hex.length() == 1) {
                    sb.append('0');
                }
                sb.append(hex);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(data.hashCode());
        }
        return cacheKey;
    }

    public static File getDiskCacheDir(Context context, String path) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            return new File(context.getExternalCacheDir(), path);
        } else {
            return new File(context.getCacheDir(), path);
        }
    }

    public static void setBitmap(SoftReference<? extends View> viewReference, Bitmap bitmap) {
        View v = viewReference.get();
        if (v != null) {
            if (v instanceof ImageView) {
                ((ImageView) v).setImageBitmap(bitmap);
            } else {
                if (bitmap == null) {
                    v.setBackgroundDrawable(null);
                } else {
                    v.setBackgroundDrawable(new BitmapDrawable(bitmap));
                }
            }
        }
    }

    public static void setBitmap(SoftReference<? extends View> viewReference, int res) {
        if (res <= 0) {
            setBitmap(viewReference, null);
        } else {
            View v = viewReference.get();
            if (v != null) {
                if (v instanceof ImageView) {
                    ((ImageView) v).setImageResource(res);
                } else {
                    v.setBackgroundResource(res);
                }
            }
        }
    }

}
