package org.pinwheel.agility.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public final class SimplePagerAdapter extends PagerAdapter {

    private ArrayList<View> mViews;

    public SimplePagerAdapter() {
        mViews = new ArrayList<View>(0);
    }

    public SimplePagerAdapter(List<? extends View> views) {
        mViews = new ArrayList<View>(views);
    }

    public void add(View v) {
        mViews.add(v);
        notifyDataSetChanged();
    }

    public void add(int index, View v) {
        mViews.add(index, v);
        notifyDataSetChanged();
    }

    public void addAll(int index, List<View> v) {
        mViews.addAll(index, v);
        notifyDataSetChanged();
    }

    public void remove(int index) {
        mViews.remove(index);
        notifyDataSetChanged();
    }

    public void removeAll() {
        mViews.clear();
        notifyDataSetChanged();
    }

    public View getItem(int index) {
        return mViews.get(index);
    }

    public int getIndexOfItem(View view){
        return mViews.indexOf(view);
    }

    @Override
    public int getCount() {
        return mViews.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View v = mViews.get(position);
        container.addView(v);
        return v;
    }
}
