package org.pinwheel.agility.cache;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public final class DataCacheManager {

    private static final int DEFAULT_MAX_DISK_CACHE = 128 * 1024 * 1024;//128M
    private static final int DEFAULT_MAX_MEMORY_CACHE = 8 * 1024 * 1024;//8M

    private static DataCacheManager instance = null;

    public static DataCacheManager getInstance(Context context) {
        if (instance == null) {
            synchronized (DataCacheManager.class) {
                if (instance == null) {
                    instance = new DataCacheManager(context);
                }
            }
        }
        return instance;
    }

    private MemoryCache memoryCache;
    private DiskCache diskCache;

    private DataCacheManager(Context context) {
        this.diskCache = new DiskCache(getDiskCacheDir(context), 0, DEFAULT_MAX_DISK_CACHE);
        this.memoryCache = new MemoryCache(DEFAULT_MAX_MEMORY_CACHE);
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
        key = getDiskKey(key);
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
        key = getDiskKey(key);
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

    private String getDiskKey(String url) {
        return md5(url);
    }

    private static File getDiskCacheDir(Context context) {
        final String fileName = "cache4data";
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            return new File(context.getExternalCacheDir(), fileName);
        } else {
            return new File(context.getCacheDir(), fileName);
        }
    }

    private static String md5(String data) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(data.getBytes());
            byte[] digest = mDigest.digest();
            StringBuilder sb = new StringBuilder();
            for (byte aDigest : digest) {
                String hex = Integer.toHexString(0xFF & aDigest);
                if (hex.length() == 1) {
                    sb.append('0');
                }
                sb.append(hex);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(data.hashCode());
        }
        return cacheKey;
    }

}
