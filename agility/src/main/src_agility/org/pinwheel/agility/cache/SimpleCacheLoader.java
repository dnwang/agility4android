package org.pinwheel.agility.cache;

import android.graphics.Bitmap;
import android.widget.ImageView;

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

    public void release() {
        memoryCache.release();
        diskCache.release();
        memoryCache = null;
        diskCache = null;
    }

    @Override
    public void setString(String key, String string) {
        if (memoryCache == null || diskCache == null) {
            return;
        }
        StringEntity data = new StringEntity(string);
        memoryCache.setCache(key, data);
        diskCache.setCache(key, data.getInputStream());
    }

    @Override
    public String getString(String key) {
        if (memoryCache == null || diskCache == null) {
            return null;
        }
        CacheEntity value = memoryCache.getCache(key);
        if (value != null) {
            return ((StringEntity) value).get();
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
        if (value != null) {
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

    @Override
    public Bitmap getBitmap(String key, int width, int height) {
        if (memoryCache == null || diskCache == null) {
            return null;
        }
        CacheEntity value = memoryCache.getCache(key);
        if (value != null) {
            return ((BitmapEntity) value).get();
        } else {
            InputStream inputStream = diskCache.getCache(key);
            if (inputStream != null) {
                BitmapEntity data = new BitmapEntity();
                data.decodeFrom(inputStream, width, height);
                memoryCache.setCache(key, data);
                return data.get();
            } else {
                return null;
            }
        }
    }

    public Bitmap getBitmap(String key, ImageView.ScaleType scaleType, int maxWidth, int maxHeight) {
        if (memoryCache == null || diskCache == null) {
            return null;
        }
        CacheEntity value = memoryCache.getCache(key);
        if (value != null) {
            return ((BitmapEntity) value).get();
        } else {
            InputStream inputStream = diskCache.getCache(key);
            if (inputStream != null) {
                BitmapEntity data = new BitmapEntity();
                data.decodeFrom(inputStream, scaleType, maxWidth, maxHeight);
                memoryCache.setCache(key, data);
                return data.get();
            } else {
                return null;
            }
        }
    }

}
