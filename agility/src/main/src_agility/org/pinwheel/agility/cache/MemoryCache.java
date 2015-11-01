package org.pinwheel.agility.cache;

import android.support.v4.util.LruCache;
import android.text.TextUtils;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public class MemoryCache {

    private LruCache<String, CacheEntity> memoryCache;

    public MemoryCache(int cacheSize) {
        this.memoryCache = new LruCache<String, CacheEntity>(Math.max(0, cacheSize)) {
            @Override
            protected int sizeOf(String key, CacheEntity value) {
                return value.sizeOf();
            }
        };
    }

    public CacheEntity getCache(String key) {
        if (memoryCache == null || TextUtils.isEmpty(key)) {
            return null;
        }
        return memoryCache.get(key);
    }

    public synchronized void setCache(String key, CacheEntity value) {
        if (memoryCache == null || TextUtils.isEmpty(key)) {
            return;
        }
        memoryCache.put(key, value);
    }

    public synchronized void remove(String key) {
        if (memoryCache == null || TextUtils.isEmpty(key)) {
            return;
        }
        memoryCache.remove(key);
    }

    public long size() {
        if (memoryCache == null) {
            return 0;
        }
        return memoryCache.size();
    }

    public synchronized void release() {
        if (memoryCache != null) {
            memoryCache.evictAll();
            memoryCache = null;
        }
    }

}
