package org.pinwheel.agility.adapter;

import android.view.View;
import android.view.ViewGroup;
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
public final class SimpleViewAdapter extends BaseAdapter {

    private ArrayList<View> views;

    public SimpleViewAdapter() {
        views = new ArrayList<View>(0);
    }

    public SimpleViewAdapter(List<? extends View> datas) {
        views = new ArrayList<View>(datas);
    }

    public SimpleViewAdapter add(View v) {
        views.add(v);
        return this;
    }

    public SimpleViewAdapter add(int index, View v) {
        views.add(index, v);
        return this;
    }

    public SimpleViewAdapter addAll(List<View> v) {
        views.addAll(v);
        return this;
    }

    public SimpleViewAdapter addAll(int index, List<View> v) {
        views.addAll(index, v);
        return this;
    }

    public SimpleViewAdapter remove(int index) {
        views.remove(index);
        return this;
    }

    public SimpleViewAdapter removeAll(List<View> v) {
        views.removeAll(v);
        return this;
    }

    public SimpleViewAdapter removeAll() {
        views.clear();
        return this;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return views.get(position);
    }

    @Override
    public int getCount() {
        return views.size();
    }

    @Override
    public View getItem(int position) {
        return views.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
