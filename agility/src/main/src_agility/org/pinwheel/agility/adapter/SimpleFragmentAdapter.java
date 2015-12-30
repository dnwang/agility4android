package org.pinwheel.agility.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

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
public final class SimpleFragmentAdapter extends android.support.v4.app.FragmentPagerAdapter {
    private ArrayList<Fragment> fragments;

    public SimpleFragmentAdapter(FragmentManager fm) {
        super(fm);
        this.fragments = new ArrayList<Fragment>(0);
    }

    public SimpleFragmentAdapter(FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);
        this.fragments = new ArrayList<Fragment>(fragmentList);
    }

    public void addFragment(Fragment fragment){
        this.fragments.add(fragment);
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }
}
