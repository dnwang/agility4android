package org.pinwheel.agility.cache;

import java.io.InputStream;
import java.io.Serializable;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public abstract class CacheEntity<T> implements Serializable {

    protected T obj;

    public CacheEntity() {
        this.obj = null;
    }

    public CacheEntity(T obj) {
        this.obj = obj;
    }

    protected abstract int sizeOf();

    protected abstract void decodeFrom(InputStream inputStream);

    protected abstract InputStream getInputStream();

    public T get() {
        return obj;
    }

}
