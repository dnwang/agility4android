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

    public static final int DEFAULT_MAX_DISK_CACHE = 32 * 1024 * 1024;//32M
    public static final int DEFAULT_MAX_MEMORY_CACHE = 4 * 1024 * 1024;//4M

    public void setString(String key, String string);

    public String getString(String key);

    public void setBitmap(String key, Bitmap bitmap);

    public Bitmap getBitmap(String key);

    public void release();

}
