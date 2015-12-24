package org.pinwheel.agility.cache;

import android.graphics.Bitmap;
import android.text.TextUtils;

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

    public void setMemoryCache(MemoryCache memoryCache) {
        if (memoryCache != null) {
            if (this.memoryCache != null) {
                this.memoryCache.release();
            }
            this.memoryCache = memoryCache;
        }
    }

    public DiskCache getDiskCache() {
        return diskCache;
    }

    public void setDiskCache(DiskCache diskCache) {
        if (diskCache != null) {
            if (this.diskCache != null) {
                this.diskCache.release();
            }
            this.diskCache = diskCache;
        }
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
                ObjectEntity data = new ObjectEntity();
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
        return getBitmap(key, false);
    }

    protected final Bitmap getBitmap(String key, boolean lowMemoryMode) {
        if (memoryCache == null || diskCache == null) {
            return null;
        }
        CacheEntity value = memoryCache.getCache(key);
        if (value != null && value instanceof BitmapEntity) {
            return ((BitmapEntity) value).get();
        } else {
            InputStream inputStream = diskCache.getCache(key);
            if (inputStream != null) {
                BitmapEntity data = new BitmapEntity(lowMemoryMode);
                data.decodeFrom(inputStream);
                memoryCache.setCache(key, data);
                return data.get();
            } else {
                return null;
            }
        }
    }

    @Override
    public void remove(String key) {
        if (memoryCache == null || diskCache == null || TextUtils.isEmpty(key)) {
            return;
        }
        memoryCache.remove(key);
        diskCache.remove(key);
    }

    @Override
    public void clear() {
        if (memoryCache == null || diskCache == null) {
            return;
        }
        memoryCache.clear();
        diskCache.delete();
    }
}
