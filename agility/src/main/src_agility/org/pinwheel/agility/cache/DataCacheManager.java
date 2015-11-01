package org.pinwheel.agility.cache;

import android.content.Context;
import android.graphics.Bitmap;

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
                ImageLoaderUtils.getDiskCacheDir(context, PATH),
                BaseUtils.getVersionCode(context),
                CacheLoader.DEFAULT_MAX_DISK_CACHE);
        MemoryCache memoryCache = new MemoryCache(CacheLoader.DEFAULT_MAX_MEMORY_CACHE);
        cacheLoader = new SimpleCacheLoader(memoryCache, diskCache);
    }

    public CacheLoader getCacheLoader() {
        return cacheLoader;
    }

    public void release() {
        if (cacheLoader != null) {
            cacheLoader.release();
            cacheLoader = null;
        }
        instance = null;
    }

    public String getString(String key) {
        if (cacheLoader == null) {
            return null;
        }
        return cacheLoader.getString(key);
    }

    @Deprecated
    public Bitmap getBitmap(String key) {
        if (cacheLoader == null) {
            return null;
        }
        return cacheLoader.getBitmap(key);
    }

}
