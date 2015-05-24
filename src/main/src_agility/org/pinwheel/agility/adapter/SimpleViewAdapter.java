package org.pinwheel.agility.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public final class SimpleViewAdapter extends BaseAdapter {

    private ArrayList<View> views;

    public SimpleViewAdapter() {
        views = new ArrayList<View>(0);
    }

    public SimpleViewAdapter(List<? extends View> datas) {
        views = new ArrayList<View>(datas);
    }

    public void add(View v) {
        views.add(v);
    }

    public void add(int index, View v) {
        views.add(index, v);
    }

    public void addAll(List<View> v) {
        views.addAll(v);
    }

    public void addAll(int index, List<View> v) {
        views.addAll(index, v);
    }

    public void remove(int index) {
        views.remove(index);
    }

    public void removeAll(List<View> v) {
        views.removeAll(v);
    }

    public void removeAll() {
        views.clear();
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
