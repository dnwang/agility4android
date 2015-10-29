package org.pinwheel.agility.cache;

import android.content.Context;
import android.graphics.Bitmap;

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
    private static final int CACHE_SIZE_OF_DISK = 100 * 1024 * 1024;//100M
    private static final int CACHE_SIZE_OF_MEMORY = (int) (Runtime.getRuntime().maxMemory() / 1024 / 10);// 1/10 total memory size

    private static DataCacheManager instance = null;

    public synchronized static DataCacheManager getInstance(Context context) {
        if (instance == null) {
            instance = new DataCacheManager(context);
        }
        return instance;
    }

    private Context context;
    private SimpleCacheLoader cacheLoader;

    private DataCacheManager(Context context) {
        DiskCache diskCache = new DiskCache(ImageLoaderUtils.getDiskCacheDir(context, PATH), 0, CACHE_SIZE_OF_DISK);
        MemoryCache memoryCache = new MemoryCache(CACHE_SIZE_OF_MEMORY);
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

    public Bitmap getBitmap(String key) {
        if (cacheLoader == null) {
            return null;
        }
        return cacheLoader.getBitmap(key);
    }

}
