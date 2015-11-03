package org.pinwheel.agility.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import org.pinwheel.agility.util.BaseUtils;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public final class DataCacheManager {

    private static final String PATH = "data";

    private static DataCacheManager instance = null;

    public synchronized static DataCacheManager getInstance(Context context) {
        if (instance == null) {
            instance = new DataCacheManager(context);
        }
        return instance;
    }

    private CacheLoader cacheLoader;

    private DataCacheManager(Context context) {
        DiskCache diskCache = new DiskCache(
                CacheUtils.getDiskCacheDir(context, PATH),
                BaseUtils.getVersionCode(context),
                CacheLoader.DEFAULT_MAX_DISK_CACHE);
        MemoryCache memoryCache = new MemoryCache(CacheLoader.DEFAULT_MAX_MEMORY_CACHE);
        cacheLoader = new SimpleCacheLoader(memoryCache, diskCache);
    }

    public CacheLoader getCacheLoader() {
        return cacheLoader;
    }

    public synchronized static void release() {
        if (instance != null) {
            instance.cacheLoader.release();
            instance.cacheLoader = null;
        }
        instance = null;
    }

    public void setObject(String key, Object obj) {
        if (TextUtils.isEmpty(key) || cacheLoader == null) {
            return;
        }
        cacheLoader.setObject(CacheUtils.convertKey(key), obj);
    }

    public Object getObject(String key) {
        if (cacheLoader == null) {
            return null;
        }
        return cacheLoader.getObject(CacheUtils.convertKey(key));
    }

    /**
     * {@link ImageLoader}
     *
     * @param key    key
     * @param bitmap bitmap
     */
    @Deprecated
    public void setBitmap(String key, Bitmap bitmap) {
        if (TextUtils.isEmpty(key) || cacheLoader == null) {
            return;
        }
        cacheLoader.setBitmap(CacheUtils.convertKey(key), bitmap);
    }

    /**
     * {@link ImageLoader}
     *
     * @param key key
     */
    @Deprecated
    public Bitmap getBitmap(String key) {
        if (cacheLoader == null) {
            return null;
        }
        return cacheLoader.getBitmap(CacheUtils.convertKey(key));
    }

}
