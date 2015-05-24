package org.pinwheel.agility.view.controller;

import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import org.pinwheel.agility.adapter.SimplePagerAdapter;

/**
 * 版权所有 (C), 2014 <br>
 * <br>
 * All rights reserved
 *
 * @author dnwang
 * @date 2014/11/1 12:43
 * @description
 */
@Deprecated
final class TabManager implements ViewPager.OnPageChangeListener, View.OnClickListener {

    private RadioGroup mNaviBar;
    private ViewPager mPager;
    private OnPageChangeListener mListener;

    private SimplePagerAdapter mAdapter;

    private boolean isNeedAnim;

    public TabManager(RadioGroup naviBar, ViewPager pager) {
        this.mNaviBar = naviBar;
        this.mPager = pager;

        if (mPager instanceof org.pinwheel.agility.view.ViewPagerNoScrollable) {
            isNeedAnim = false;
        } else {
            isNeedAnim = true;
        }
        this.init();
    }

    private int navBtn_size;

    private void init() {
        navBtn_size = 0;
        mAdapter = new SimplePagerAdapter();
        mPager.setAdapter(mAdapter);
        mPager.setOnPageChangeListener(this);
        int size = mNaviBar.getChildCount();
        for (int index = 0; index < size; index++) {
            View v = mNaviBar.getChildAt(index);
            if (v instanceof RadioButton) {
                RadioButton navBtn = (RadioButton) v;
                // save index
                navBtn.setTag(navBtn_size);
                navBtn.setOnClickListener(this);
                navBtn_size++;

                if (navBtn_size == 1) {
                    navBtn.setChecked(true);
                }
            } else {
                v.setTag(null);
            }
        }
    }

    private void resetNaviBtnTagIndex() {
        navBtn_size = 0;
        int size = mNaviBar.getChildCount();
        for (int index = 0; index < size; index++) {
            View v = mNaviBar.getChildAt(index);
            v.setTag(null);
            if (v instanceof RadioButton) {
                RadioButton navBtn = (RadioButton) v;
                // save index
                navBtn.setTag(navBtn_size);
                navBtn_size++;
            }
        }
    }

    public void addPage(View page) {
        if (page == null || mAdapter.getCount() > navBtn_size - 1) {
            return;
        }
        if (page instanceof Pageable) {
            Pageable pageable = (Pageable) page;
            pageable.onInit(this);
        }
        mAdapter.add(page);
    }

    public void addPageAndItem(View page, RadioButton button) {
        addPageAndItem(page, button, null);
    }

    public void addPageAndItem(View page, RadioButton button, LinearLayout.LayoutParams params) {
        addViewToNaviBar(button, params);
        addPage(page);
    }

    public void addViewToNaviBar(View view) {
        addViewToNaviBar(view, null);
    }

    public void addViewToNaviBar(View view, LinearLayout.LayoutParams params) {
        if (view == null) {
            return;
        }
        view.setOnClickListener(this);
        if (params == null) {
            mNaviBar.addView(view);
        } else {
            mNaviBar.addView(view, params);
        }
        resetNaviBtnTagIndex();
    }

    public void removeAllPageAndNaviView() {
        clearAllPage();
        clearNaviBar();
    }

    public void removePageAndItem(int index) {
        if (index > -1 && index < navBtn_size) {
            View item = getItemInNavi(index);
            mNaviBar.removeView(item);
            resetNaviBtnTagIndex();
        }
        if (index > -1 && index < mAdapter.getCount()) {
            mAdapter.remove(index);
        }
    }

    public void clearAllPage() {
        if (mAdapter == null) {
            return;
        }
        mAdapter.removeAll();
    }

    public void clearNaviBar() {
        if (mNaviBar == null) {
            return;
        }
        mNaviBar.removeAllViews();
        resetNaviBtnTagIndex();
    }

    public void setOnPageChangeListener(OnPageChangeListener listener) {
        this.mListener = listener;
    }

    public void setNeedChangeAnim(boolean isNeedAnim) {
        this.isNeedAnim = isNeedAnim;
    }

    public void setCurrentPage(int index) {
        View view = mNaviBar.findViewWithTag(index);
        if (view != null && view instanceof RadioButton) {
            ((RadioButton) view).setChecked(true);
        }

        mPager.setCurrentItem(index, isNeedAnim);
    }

    public int getCurrentPage() {
        return mPager.getCurrentItem();
    }

    public View getViewInPages(int index) {
        return mAdapter.getItem(index);
    }

    public RadioButton getItemInNavi(int index) {
        View view = mNaviBar.findViewWithTag(index);
        if (view != null && view instanceof RadioButton) {
            return (RadioButton) view;
        }
        return null;
    }

    public RadioButton getCurrentNaviItem() {
        return getItemInNavi(getCurrentPage());
    }

    public int getPageSize() {
        return mAdapter.getCount();
    }

    public int getItemSize() {
        return navBtn_size;
    }

    private void selectPage(int from_index, int to_index) {
        int size = mAdapter.getCount();
        if (from_index >= size) {
            from_index = size - 1;
        }
        if (to_index >= size) {
            to_index = size - 1;
        }

        View fromPage = null;
        View toPage = null;

        if (from_index > 0) {
            // from_index maybe -1 (first select)
            fromPage = mAdapter.getItem(from_index);
        }
        toPage = mAdapter.getItem(to_index);
        if (toPage != null && toPage instanceof Pageable) {
            Pageable pageable = (Pageable) toPage;
            pageable.onSelected(this, from_index);
        }
        if (fromPage != null && fromPage instanceof Pageable) {
            Pageable pageable = (Pageable) fromPage;
            pageable.onHidden(this, to_index);
        }
        if (mListener != null) {
            mListener.onPageChanged(this, from_index, to_index);
        }
    }

    @Override
    public void onClick(View v) {
        if (v instanceof RadioButton) {
            int nav_index = Integer.parseInt(v.getTag().toString());
            if (nav_index > mAdapter.getCount() - 1) {
                return;
            } else {
                mPager.setCurrentItem(nav_index, isNeedAnim);
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    private int old_position = -1;

    @Override
    public void onPageSelected(int current_position) {
        if (old_position != current_position) {
            selectPage(old_position, current_position);
            old_position = current_position;
        } else {
            return;
        }
        ((RadioButton) mNaviBar.findViewWithTag(current_position)).setChecked(true);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public static interface OnPageChangeListener {

        public void onPageChanged(TabManager tabManager, int from_index, int to_index);

    }

    public static interface Pageable {

        public void onInit(TabManager tabManager);

        public void onSelected(TabManager tabManager, int from_index);

        public void onHidden(TabManager tabManager, int next_index);

    }

}
