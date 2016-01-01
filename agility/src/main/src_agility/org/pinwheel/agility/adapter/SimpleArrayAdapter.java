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

    protected ArrayList<T> mDatas;

    protected SimpleArrayAdapter() {
        super();
        mDatas = new ArrayList<T>(0);
    }

    protected SimpleArrayAdapter(List<T> datas) {
        super();
        mDatas = new ArrayList<T>(datas);
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public T getItem(int position) {
        if (position < 0 || position >= mDatas.size()) {
            return null;
        }
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public ArrayList<T> getDatas() {
        return mDatas;
    }

    public void addItem(T obj) {
        this.mDatas.add(obj);
    }

    public void addItem(T obj, int index) {
        this.mDatas.add(index, obj);
    }

    public void addAll(List<T> datas) {
        this.mDatas.addAll(datas);
    }

    public void remove(int index) {
        if (index < 0 || index >= mDatas.size()) {
            return;
        }
        this.mDatas.remove(index);
    }

    public void removeAll() {
        this.mDatas.clear();
    }

    public void removeAll(List<T> datas) {
        this.mDatas.removeAll(datas);
    }

}
