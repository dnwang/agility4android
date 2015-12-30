package org.pinwheel.agility.view.controller;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Checkable;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.pinwheel.agility.adapter.SimplePagerAdapter;

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
public final class TabController {

    private ArrayList<Pair<View, View>> tabs;

    private ViewGroup itemBar;
    private ViewPager pager;
    private ISelectable selector;

    private SimplePagerAdapter adapter;
    private OnTabChangeListener listener;

    private boolean isPageSmoothScroll;
    private boolean isSelectorSmoothScroll;

    private View.OnClickListener onSelectorClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            for (Pair<View, View> pair : tabs) {
                if (pair.first == v) {
                    int pageIndex = adapter.getIndexOfItem(pair.second);
                    if (pageIndex == pager.getCurrentItem()) {
                        if (v instanceof Checkable) {
                            ((Checkable) v).setChecked(true);
                        }
                    } else {
                        pager.setCurrentItem(pageIndex, isPageSmoothScroll);
                    }
                    break;
                }
            }
        }
    };

    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            View page = adapter.getItem(position);

            int tab_size = tabs.size();
            for (int index = 0; index < tab_size; index++) {
                Pair<View, View> tab = tabs.get(index);
                if (page == tab.second) {
                    setCurrentTab(index);
                    // TODO denan.wang; 2015/2/13;
                    if (page instanceof Pageable) {
                        ((Pageable) page).onSelected(TabController.this);
                    }
                    // END
                    // TODO denan.wang; 2015/2/13;
                    if (listener != null) {
                        listener.onTabChanged(TabController.this);
                    }
                    // END
                    return;
                }
            }
            clearItemState();
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    public TabController(ViewGroup itemContainer, ViewPager pagerContainer, ISelectable selector) {
        if (itemContainer == null || pagerContainer == null) {
            throw new NullPointerException("itemContainer or pagerContainer must be not null !");
        }

        this.tabs = new ArrayList<Pair<View, View>>(0);
        this.itemBar = itemContainer;
        this.pager = pagerContainer;
        this.selector = selector;
        this.isSelectorSmoothScroll = true;
        if (pagerContainer instanceof org.pinwheel.agility.view.ViewPagerNoScrollable) {
            this.isPageSmoothScroll = false;
        } else {
            this.isPageSmoothScroll = true;
        }

        this.adapter = new SimplePagerAdapter();
        this.pager.setAdapter(adapter);
        this.pager.setOnPageChangeListener(onPageChangeListener);

        // clear all
//        removeAll();
    }

    private void onGlobalLayout() {
        this.itemBar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (selector != null) {
                    selector.removeAll();
                    ArrayList<Rect> rect_list = new ArrayList<Rect>(3);
                    for (Pair<View, View> tab : tabs) {
                        Rect rect = new Rect(
                                tab.first.getLeft(),
                                tab.first.getTop(),
                                tab.first.getRight(),
                                tab.first.getBottom());
                        rect_list.add(rect);
                    }
                    selector.replaceAll(rect_list);
                }

                int current_tab = getCurrentTab();
                if (current_tab == -1) {
                    clearItemState();
                } else {
                    setCurrentTab(current_tab);
                }

                // TODO denan.wang; 2015/2/14;
                itemBar.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    @Deprecated
    public void addTab(TabInfo tabInfo) {
        addTab(-1, tabInfo);
    }

    public void addTab(int index, TabInfo tabInfo) {
        if (tabInfo == null) {
            return;
        }

        View page = tabInfo.getPage();
        View item = tabInfo.getItem();

        item.setOnClickListener(onSelectorClickListener);
        if (page instanceof Pageable) {
            Pageable pageable = (Pageable) page;
            pageable.onInit(this);
        }

        if (index < 0 || index >= adapter.getCount()) {
            ViewGroup.LayoutParams itemParams = tabInfo.getItemParams();
            if (itemParams == null) {
                itemBar.addView(item);
            } else {
                itemBar.addView(item, itemParams);
            }
            adapter.add(page);
        } else {
            ViewGroup.LayoutParams itemParams = tabInfo.getItemParams();
            if (itemParams == null) {
                itemBar.addView(item, index);
            } else {
                itemBar.addView(item, index, itemParams);
            }
            adapter.add(index, page);
        }

        tabs.add(new Pair<View, View>(item, page));

        onGlobalLayout();
    }

    public void addTabs(TabInfo... tabInfos) {
        if (tabInfos == null || tabInfos.length < 1) {
            return;
        }

        for (TabInfo tabInfo : tabInfos) {
            if (tabInfo == null) {
                break;
            }

            View page = tabInfo.getPage();
            View item = tabInfo.getItem();

            item.setOnClickListener(onSelectorClickListener);
            if (page instanceof Pageable) {
                Pageable pageable = (Pageable) page;
                pageable.onInit(this);
            }

            ViewGroup.LayoutParams itemParams = tabInfo.getItemParams();
            if (itemParams == null) {
                itemBar.addView(item);
            } else {
                itemBar.addView(item, itemParams);
            }
            adapter.add(page);

            tabs.add(new Pair<View, View>(item, page));
        }

        onGlobalLayout();
    }

    public void addTabs(List<TabInfo> tabInfos) {
        if (tabInfos == null || tabInfos.size() < 1) {
            return;
        }
        addTabs(tabInfos.toArray(new TabInfo[tabInfos.size()]));
    }

    public void setSelector(int res_id) {
        setSelector(itemBar.getContext().getResources().getDrawable(res_id));
    }

    public void setSelector(Drawable selector) {
        if (this.selector != null) {
            this.selector.setSelector(selector);
        }
    }

    public void setSelectorDuration(long duration) {
        if (this.selector != null) {
            this.selector.setDuration(duration);
        }
    }

    public void addDivider(View divider, ViewGroup.LayoutParams params) {
        addDivider(divider, -1, params);
    }

    public void addDivider(View divider, int index, ViewGroup.LayoutParams params) {
        if (index < 0 || index >= itemBar.getChildCount()) {
            itemBar.addView(divider, params);
        } else {
            itemBar.addView(divider, index, params);
        }
        onGlobalLayout();
    }

    public boolean removeDivider(int index) {
        if (index < 0 || index >= itemBar.getChildCount()) {
            return false;
        }
        View child_view = itemBar.getChildAt(index);

        for (Pair<View, View> tab : tabs) {
            if (child_view == tab.first) {
                return false;
            }
        }
        itemBar.removeView(child_view);
        onGlobalLayout();
        return true;
    }

    public boolean removeTab(int index) {
        if (index < 0 || index >= tabs.size()) {
            return false;
        }

        Pair<View, View> tab = tabs.remove(index);
        if (tab != null) {
            itemBar.removeView(tab.first);
            adapter.remove(adapter.getIndexOfItem(tab.second));
            if (selector != null) {
                selector.remove(index);
            }
            onGlobalLayout();
            return true;
        }
        return false;
    }

    public boolean removeAll() {
        tabs.clear();
        itemBar.removeAllViews();
        adapter.removeAll();
        if (selector != null) {
            selector.removeAll();
        }
        onGlobalLayout();
        return true;
    }

    public int getTabCount() {
        return tabs.size();
    }

    public void setOnTabChangeListener(OnTabChangeListener listener) {
        this.listener = listener;
    }

    public void setPageSmoothScroll(boolean is) {
        this.isPageSmoothScroll = is;
    }

    public void setSelectorSmoothScroll(boolean is) {
        this.isSelectorSmoothScroll = is;
    }

    public void setCurrentTab(int index) {
        Pair<View, View> tab = tabs.get(index);
        clearItemState();
        // END
        // FIXME denan.wang; 2015/2/25;
//        tab.first.callOnClick();
//        tab.first.performClick();
        onSelectorClickListener.onClick(tab.first);
        // END

        if (selector != null) {
            selector.moveTo(index, isSelectorSmoothScroll);
        }
    }

    private void clearItemState() {
        int child_size = itemBar.getChildCount();
        for (int i = 0; i < child_size; i++) {
            View childView = itemBar.getChildAt(i);
            if (childView instanceof Checkable) {
                ((Checkable) childView).setChecked(false);
            }
        }
    }

    public int getCurrentTab() {
        int index = -1;
        int size = tabs.size();
        View page = adapter.getItem(pager.getCurrentItem());
        for (int i = 0; i < size; i++) {
            Pair<View, View> tab = tabs.get(i);
            if (tab.second == page) {
                index = i;
                break;
            }
        }
        return index;
    }

    public View getTabItem(int index) {
        if (index < 0 || index > tabs.size()) {
            return null;
        }
        return tabs.get(index).first;
    }

    public View getTabPage(int index) {
        if (index < 0 || index > tabs.size()) {
            return null;
        }
        return tabs.get(index).second;
    }

    public void setHorizontalEqually() {
        for (Pair<View, View> tab : tabs) {
            ViewGroup.LayoutParams params = tab.first.getLayoutParams();
            if (params instanceof LinearLayout.LayoutParams) {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) params;
                lp.weight = 1;
                lp.width = 0;
                lp.height = -1;
                tab.first.setLayoutParams(lp);
            }
        }
        onGlobalLayout();
    }

    public void setVerticalEqually() {
        for (Pair<View, View> tab : tabs) {
            ViewGroup.LayoutParams params = tab.first.getLayoutParams();
            if (params instanceof LinearLayout.LayoutParams) {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) params;
                lp.weight = 1;
                lp.height = 0;
                lp.width = -1;
                tab.first.setLayoutParams(lp);
            }
        }
        onGlobalLayout();
    }

    public static interface OnTabChangeListener {
        public void onTabChanged(TabController tabController);
    }

    public static interface Pageable {
        public void onInit(TabController tabController);

        public void onSelected(TabController tabController);

//        public void onHidden(TabController tabController);
    }

    public final static class TabInfo {

        private View item;
        private View page;
        private ViewGroup.LayoutParams itemParams;

        public TabInfo(int item_layout, View page) {
            this(LayoutInflater.from(page.getContext()).inflate(item_layout, null), page);
        }

        public TabInfo(View item, View page) {
            this(item, null, page);
        }

        public TabInfo(View item, ViewGroup.LayoutParams itemParams, View page) {
            if (item == null || page == null) {
                throw new NullPointerException("item or page must be not null !");
            }

            this.item = item;
            this.page = page;
            this.itemParams = itemParams;
        }

        public View getItem() {
            return item;
        }

        public void setItemText(CharSequence txt) {
            if (item instanceof TextView) {
                ((TextView) item).setText(txt);
            }
        }

        public void setItemText(int res_id) {
            if (item instanceof TextView) {
                ((TextView) item).setText(res_id);
            }
        }

        public void setItemBackgroundResource(int res_id) {
            item.setBackgroundResource(res_id);
        }

        public void setItemBackgroundDrawable(Drawable drawable) {
            item.setBackgroundDrawable(drawable);
        }

        public void setItemCompoundDrawable(int res_id, String position) {
            Drawable drawable = item.getContext().getResources().getDrawable(res_id);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            setItemCompoundDrawable(drawable, position);
        }

        public void setItemCompoundDrawable(Drawable drawable, String position) {
            if (item instanceof TextView) {
                if ("left".equalsIgnoreCase(position)) {
                    ((TextView) item).setCompoundDrawables(drawable, null, null, null);
                } else if ("top".equalsIgnoreCase(position)) {
                    ((TextView) item).setCompoundDrawables(null, drawable, null, null);
                } else if ("right".equalsIgnoreCase(position)) {
                    ((TextView) item).setCompoundDrawables(null, null, drawable, null);
                } else if ("bottom".equalsIgnoreCase(position)) {
                    ((TextView) item).setCompoundDrawables(null, null, null, drawable);
                }
            }
        }

        public ViewGroup.LayoutParams getItemParams() {
            return itemParams;
        }

        public View getPage() {
            return page;
        }

        public void setItemParams(ViewGroup.LayoutParams params) {
            this.itemParams = params;
        }

    }

    public static interface ISelectable {

        public void moveTo(int index, boolean smoothScroll);

        public void moveTo(int from_index, int to_index, float positionOffset);

        public void reset(Object args);

        public void removeAll();

        public void remove(int index);

        public void replaceAll(ArrayList<Rect> rectList);

        public void addRect(int index, Rect rect);

        public int getCurrentRect(Rect copy_rect);

        public void setSelector(Drawable src);

        public void setDuration(long duration);

    }

}
