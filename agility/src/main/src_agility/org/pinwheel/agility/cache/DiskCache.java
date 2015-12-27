package org.pinwheel.agility.cache;

import android.text.TextUtils;

import org.pinwheel.agility.cache.lru.DiskLruCache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public class DiskCache {

//    private final Object lock = new Object();

    private DiskLruCache diskCache;

    public DiskCache(File path, int version, int cacheSize) {
        try {
            this.diskCache = DiskLruCache.open(path, version, 1, cacheSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Deprecated
    public boolean isContains(String key) {
        if (TextUtils.isEmpty(key)) {
            return false;
        }
        if (diskCache == null) {
            return false;
        }
        String[] fileName = diskCache.getDirectory().list();
        for (String name : fileName) {
            if (name.startsWith(key + ".")) {// unknown suffix
                return true;
            }
        }
        return false;
    }

    public InputStream getCache(String key) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        if (diskCache == null) {
            return null;
        }
        try {
            DiskLruCache.Snapshot snapShot = diskCache.get(key);
            if (snapShot != null) {
                return snapShot.getInputStream(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setCache(String key, InputStream inputStream) {
        DiskLruCache.Editor editor = null;
        OutputStream outputStream = null;
        try {
            editor = diskCache.edit(key);
            if (editor != null) {
                outputStream = editor.newOutputStream(0);
                byte[] buff = new byte[1024];
                int size = 0;
                while ((size = inputStream.read(buff)) != -1) {
                    outputStream.write(buff, 0, size);
                }
                editor.commit();
                diskCache.flush(); // no need call it every times;
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (editor != null) {
                try {
                    editor.abort();
                    diskCache.flush();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void remove(String key) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        if (diskCache == null) {
            return;
        }
        try {
            diskCache.remove(key);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public long size() {
        return diskCache == null ? 0 : diskCache.size();
    }

    public void delete() {
        if (diskCache == null) {
            return;
        }
        try {
            diskCache.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void release() {
        if (diskCache == null) {
            return;
        }
        try {
            diskCache.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (!diskCache.isClosed()) {
                try {
                    diskCache.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            diskCache = null;
        }
    }

}
