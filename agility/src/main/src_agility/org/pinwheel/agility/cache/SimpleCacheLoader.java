package org.pinwheel.agility.cache;

import android.graphics.Bitmap;

import java.io.InputStream;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public class SimpleCacheLoader implements CacheLoader {

    private MemoryCache memoryCache;
    private DiskCache diskCache;

    public SimpleCacheLoader(MemoryCache memoryCache, DiskCache diskCache) {
        this.memoryCache = memoryCache;
        this.diskCache = diskCache;
    }

    public MemoryCache getMemoryCache() {
        return memoryCache;
    }

    public DiskCache getDiskCache() {
        return diskCache;
    }

    @Override
    public void release() {
        if (memoryCache != null) {
            memoryCache.release();
        }
        if (diskCache != null) {
            diskCache.release();
        }
        memoryCache = null;
        diskCache = null;
    }

    @Override
    public void setObject(String key, Object obj) {
        if (memoryCache == null || diskCache == null) {
            return;
        }
        ObjectEntity data = new ObjectEntity(obj);
        memoryCache.setCache(key, data);
        diskCache.setCache(key, data.getInputStream());
    }

    @Override
    public Object getObject(String key) {
        if (memoryCache == null || diskCache == null) {
            return null;
        }
        CacheEntity value = memoryCache.getCache(key);
        if (value != null && value instanceof ObjectEntity) {
            return ((ObjectEntity) value).get();
        } else {
            InputStream inputStream = diskCache.getCache(key);
            if (inputStream != null) {
                StringEntity data = new StringEntity();
                data.decodeFrom(inputStream);
                memoryCache.setCache(key, data);
                return data.get();
            } else {
                return null;
            }
        }
    }

    @Override
    public void setBitmap(String key, Bitmap bitmap) {
        if (memoryCache == null || diskCache == null) {
            return;
        }
        BitmapEntity data = new BitmapEntity(bitmap);
        memoryCache.setCache(key, data);
        diskCache.setCache(key, data.getInputStream());
    }

    @Override
    public Bitmap getBitmap(String key) {
        if (memoryCache == null || diskCache == null) {
            return null;
        }
        CacheEntity value = memoryCache.getCache(key);
        if (value != null && value instanceof BitmapEntity) {
            return ((BitmapEntity) value).get();
        } else {
            InputStream inputStream = diskCache.getCache(key);
            if (inputStream != null) {
                BitmapEntity data = new BitmapEntity();
                data.decodeFrom(inputStream);
                memoryCache.setCache(key, data);
                return data.get();
            } else {
                return null;
            }
        }
    }

}
