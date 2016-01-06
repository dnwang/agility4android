package org.pinwheel.agility.cache;

import android.content.Context;
import android.text.TextUtils;

import java.io.InputStream;
import java.io.Serializable;

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

    private MemoryCache memoryCache;
    private DiskCache diskCache;

    private DataCacheManager(Context context) {
        this.diskCache = new DiskCache(CacheUtils.getDiskCacheDir(context, PATH), 0, CacheUtils.DEFAULT_MAX_DISK_CACHE);
        this.memoryCache = new MemoryCache(CacheUtils.DEFAULT_MAX_MEMORY_CACHE);
    }

    public DiskCache getDiskCache() {
        return this.diskCache;
    }

    public MemoryCache getMemoryCache() {
        return this.memoryCache;
    }

    public synchronized static void release() {
        if (instance != null) {
            if (instance.memoryCache != null) {
                instance.memoryCache.release();
                instance.memoryCache = null;
            }
            if (instance.diskCache != null) {
                instance.diskCache.release();
                instance.diskCache = null;
            }
        }
        instance = null;
    }

    public void setObject(String key, Serializable obj) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        if (memoryCache == null || diskCache == null) {
            return;
        }
        key = CacheUtils.convertKey(key);
        ObjectEntity<Serializable> value = new ObjectEntity<>();
        value.decodeFrom(obj);
        memoryCache.setCache(key, value);
        diskCache.setCache(key, value.getInputStream());
    }

    public Object getObject(String key) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        if (memoryCache == null || diskCache == null) {
            return null;
        }
        key = CacheUtils.convertKey(key);
        ObjectEntity cache = memoryCache.getCache(key);
        if (cache != null) {
            return cache.get();
        } else {
            InputStream inputStream = diskCache.getCache(key);
            if (inputStream != null) {
                ObjectEntity value = new ObjectEntity();
                value.decodeFrom(inputStream);
                memoryCache.setCache(key, value);
                return value.get();
            } else {
                return null;
            }
        }
    }

    public void remove(String key) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        if (memoryCache == null || diskCache == null) {
            return;
        }
        memoryCache.remove(key);
        diskCache.remove(key);
    }

    public void clearAllCache() {
        if (memoryCache == null || diskCache == null) {
            return;
        }
        memoryCache.clear();
        diskCache.delete();
        System.gc();
    }

}
