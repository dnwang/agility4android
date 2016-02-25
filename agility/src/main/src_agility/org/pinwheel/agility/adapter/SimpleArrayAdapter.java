package org.pinwheel.agility.adapter;

import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public abstract class SimpleArrayAdapter<T> extends BaseAdapter {

    protected ArrayList<T> data;

    protected SimpleArrayAdapter() {
        super();
        data = new ArrayList<>(0);
    }

    protected SimpleArrayAdapter(List<T> datas) {
        super();
        data = new ArrayList<>(datas);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public T getItem(int position) {
        if (position < 0 || position >= data.size()) {
            return null;
        }
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public ArrayList<T> getDatas() {
        return data;
    }

    public SimpleArrayAdapter<T> addItem(T obj) {
        this.data.add(obj);
        return this;
    }

    public SimpleArrayAdapter<T> addItem(T obj, int index) {
        this.data.add(index, obj);
        return this;
    }

    public SimpleArrayAdapter<T> addAll(List<T> datas) {
        this.data.addAll(datas);
        return this;
    }

    public SimpleArrayAdapter<T> addAll(T... datas) {
        if (datas != null && datas.length > 0) {
            for (T t : datas) {
                data.add(t);
            }
        }
        return this;
    }

    public T remove(int index) {
        if (index < 0 || index >= data.size()) {
            return null;
        }
        return this.data.remove(index);
    }

    public SimpleArrayAdapter<T> removeAll() {
        this.data.clear();
        return this;
    }

    public SimpleArrayAdapter<T> removeAll(List<T> datas) {
        this.data.removeAll(datas);
        return this;
    }

}
