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

//    private final Object lock = new Object();

    private LruCache<String, ObjectEntity> memoryCache;

    public MemoryCache(int cacheSize) {
        this.memoryCache = new LruCache<String, ObjectEntity>(Math.max(0, cacheSize)) {
            @Override
            protected int sizeOf(String key, ObjectEntity value) {
                return value.sizeOf();
            }
        };
    }

    public ObjectEntity getCache(String key) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        return memoryCache == null ? null : memoryCache.get(key);
    }

    public void setCache(String key, ObjectEntity value) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        if (memoryCache != null) {
            memoryCache.put(key, value);
        }
    }

    public void remove(String key) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        if (memoryCache != null) {
            memoryCache.remove(key);
        }
    }

    public long size() {
        return memoryCache == null ? 0 : memoryCache.size();
    }

    public void clear() {
        if (memoryCache != null) {
            memoryCache.evictAll();
        }
    }

    public void release() {
        clear();
        memoryCache = null;
    }

}
