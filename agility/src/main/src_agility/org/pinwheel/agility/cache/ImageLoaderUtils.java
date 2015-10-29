package org.pinwheel.agility.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.*;
import java.lang.ref.WeakReference;

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

    public static String convertUrl(String url) {
        return String.valueOf(url.hashCode());
//        String cacheKey;
//        try {
//            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
//            mDigest.update(url.getBytes());
//            cacheKey = bytesToHexString(mDigest.digest());
//        } catch (NoSuchAlgorithmException e) {
//            cacheKey = String.valueOf(url.hashCode());
//        }
//        return cacheKey;
    }

    private static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    public static File getDiskCacheDir(Context context, String path) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            return new File(context.getExternalCacheDir(), path);
        } else {
            return new File(context.getCacheDir(), path);
        }
    }

    public static void setBitmap(WeakReference<? extends View> viewReference, Bitmap bitmap) {
        if (bitmap == null){
            Log.e("--------->", "setBitmap bitmap bitmap = null!");
        }

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
        }else{
            Log.e("--------->", "setBitmap bitmap view = null!");
        }
    }

    public static void setBitmap(WeakReference<? extends View> viewReference, int res) {
        if (res <= 0) {
            Log.e("--------->", "setBitmap res res<0!");
            setBitmap(viewReference, null);
        } else {
            View v = viewReference.get();
            if (v != null) {
                if (v instanceof ImageView) {
                    ((ImageView) v).setImageResource(res);
                } else {
                    v.setBackgroundResource(res);
                }
            }else{
                Log.e("--------->", "setBitmap res view = null!");
            }
        }
    }

    public static void setBitmapInUIThread(final WeakReference<? extends View> viewReference, final Bitmap bitmap) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                setBitmap(viewReference, bitmap);
            }
        });
    }

    public static void setBitmapInUIThread(final WeakReference<? extends View> viewReference, final int res) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                setBitmap(viewReference, res);
            }
        });
    }

    public static byte[] stream2Byte(InputStream inputStream) {
        byte[] content = null;
        BufferedInputStream in = null;
        ByteArrayOutputStream out = null;
        try {
            in = new BufferedInputStream(inputStream);
            out = new ByteArrayOutputStream(1024);
            byte[] temp = new byte[1024];
            int size = 0;
            while ((size = in.read(temp)) != -1) {
                out.write(temp, 0, size);
            }
            content = out.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return content;
    }

}
