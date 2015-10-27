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

    public void setString(String key, String string);

    public String getString(String key);

    public void setBitmap(String key, Bitmap bitmap);

    public Bitmap getBitmap(String key);

    public Bitmap getBitmap(String key, int width, int height);

}
