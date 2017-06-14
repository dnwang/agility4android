package org.pinwheel.agility.adapter;

import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Collection;
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

    private ArrayList<T> data;

    public SimpleArrayAdapter() {
        super();
        data = new ArrayList<>(0);
    }

    public SimpleArrayAdapter(List<T> list) {
        super();
        data = new ArrayList<>(list);
    }

    public SimpleArrayAdapter(T... list) {
        this();
        addAll(list);
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
        if (obj != null) {
            if (obj instanceof Collection) {
                if (obj instanceof List) {
                    addAll((List<T>) obj);
                } else {
                    throw new ClassCastException("SimpleArrayAdapter addItem(T)");
                }
            } else {
                this.data.add(obj);
            }
        }
        return this;
    }

    public SimpleArrayAdapter<T> addItem(T obj, int index) {
        if (obj != null && index >= 0 && index < data.size()) {
            this.data.add(index, obj);
        }
        return this;
    }

    public SimpleArrayAdapter<T> addAll(List<T> list) {
        if (list != null && list.size() > 0) {
            data.addAll(list);
        }
        return this;
    }

    public SimpleArrayAdapter<T> addAll(T... list) {
        if (list != null && list.length > 0) {
            for (T t : list) {
                if (t instanceof Collection) {
                    if (t instanceof List) {
                        addAll((List<T>) t);
                    } else {
                        throw new ClassCastException("SimpleArrayAdapter addAll(T...)");
                    }
                } else {
                    data.add(t);
                }
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

    public SimpleArrayAdapter<T> removeAll(List<T> list) {
        this.data.removeAll(list);
        return this;
    }

}
