package org.pinwheel.agility.cache;

import android.graphics.Bitmap;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public interface CacheLoader {

    public static final int DEFAULT_MAX_DISK_CACHE = 128 * 1024 * 1024;//128M
//    public static final int DEFAULT_MAX_MEMORY_CACHE = 12 * 1024 * 1024;//12M
    public static final int DEFAULT_MAX_MEMORY_CACHE = (int) (Runtime.getRuntime().maxMemory() / 8);

    public void setObject(String key, Object obj);

    public Object getObject(String key);

    public void setBitmap(String key, Bitmap bitmap);

    public Bitmap getBitmap(String key);

    public void remove(String key);

    public void clear();

    public void release();

}
