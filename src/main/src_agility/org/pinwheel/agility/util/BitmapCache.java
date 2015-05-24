package org.pinwheel.agility.util;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import com.android.volley.toolbox.ImageLoader;

/**
 * 版权所有 (C), 2014<br>
 * <br>
 * All rights reserved
 *
 * @author dnwang
 * @date 2015/3/14 21:50
 * @description
 */
public class BitmapCache implements ImageLoader.ImageCache {

    private LruCache<String, Bitmap> mCache;

    public BitmapCache(int maxSize) {
        mCache = new LruCache<String, Bitmap>(maxSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };
    }

    @Override
    public Bitmap getBitmap(String key) {
        return mCache.get(key);
    }

    @Override
    public void putBitmap(String key, Bitmap arg1) {
        mCache.put(key, arg1);
    }

    public void clear() {
        if (mCache != null) {
            mCache.evictAll();
        }
    }

}