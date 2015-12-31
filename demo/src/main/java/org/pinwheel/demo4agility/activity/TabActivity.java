package org.pinwheel.demo4agility.activity;

import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SlidingPaneLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.pinwheel.agility.view.TabSelectorView;
import org.pinwheel.agility.view.controller.TabController;
import org.pinwheel.demo4agility.R;

import java.util.ArrayList;
import java.util.List;

public class TabActivity extends AbsTestActivity {

    private TabController controller;

    private SlidingPaneLayout slidingPane;

    private int[] colors = new int[]{
            Color.BLUE,
            Color.YELLOW,
            Color.RED,
            Color.GREEN,
            Color.GRAY,
            Color.CYAN
    };

    @Override
    protected void onInitInCreate() {

    }

    @Override
    protected View getContentView() {
        View contentView = LayoutInflater.from(this).inflate(R.layout.activity_tab_left, null);
        slidingPane = (SlidingPaneLayout) contentView.findViewById(R.id.sliding);
        slidingPane.setShadowResourceLeft(R.drawable.shadow_right);

        controller = new TabController(
                (ViewGroup) contentView.findViewById(R.id.item_group),
                (ViewPager) contentView.findViewById(R.id.pager),
                (TabController.ISelectable) contentView.findViewById(R.id.selector));

        this.init();

        return contentView;
    }

    @Override
    protected void doTest() {

    }

    private void init() {

        TabController.TabInfo tab1 = new TabController.TabInfo(org.pinwheel.demo4agility.R.layout.item_checkbox, createTestPage1());
        tab1.setItemText("Top");
        tab1.setItemCompoundDrawable(android.R.drawable.ic_dialog_info, "top");
        tab1.setItemBackgroundResource(android.R.drawable.ic_dialog_info);
        TabController.TabInfo tab3 = new TabController.TabInfo(org.pinwheel.demo4agility.R.layout.item_checkbox, createTestPage3());
        tab3.setItemText("Bottom");
        tab3.setItemCompoundDrawable(android.R.drawable.ic_dialog_info, "right");
        TabController.TabInfo tab2 = new TabController.TabInfo(org.pinwheel.demo4agility.R.layout.item_checkbox, createTestPage2());
        tab2.setItemText("FlowLayout");
        tab2.setItemCompoundDrawable(android.R.drawable.ic_dialog_info, "bottom");
        controller.addTabs(tab1, tab3, tab2);

        List<TabController.TabInfo> tabInfos0 = new ArrayList<TabController.TabInfo>(5);
        for (int i = 0; i < 5; i++) {
            TextView page = new TextView(this);
            page.setText("page " + i);
            page.setBackgroundColor(colors[(int) (Math.random() * colors.length)]);
            TabController.TabInfo testTab = new TabController.TabInfo(org.pinwheel.demo4agility.R.layout.item_checkbox, page);
            testTab.setItemText("item " + i);
//            controller.addTabs(testTab);
            tabInfos0.add(testTab);

//            View testDivider = new View(this);
//            testDivider.setBackgroundColor(Color.GRAY);
//            controller.addDivider(testDivider, new ViewGroup.LayoutParams(-1, 1));
        }
        controller.addTabs(tabInfos0);

//        controller.setVerticalEqually();

//        //插断pager序列test
//        ((SimplePagerAdapter) ((ViewPager) findViewById(R.id.pager)).getAdapter()).add(2, new TextView(this));
//
//        //删除test
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                controller.removeTab(2);
//
//                controller.removeDivider(4);
//            }
//        }, 3000);
//
//        //添加test
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                TextView page = new TextView(TabController.this);
//                page.setText("page add");
//                page.setBackgroundColor(colors[(int) (Math.random() * colors.length)]);
//                TabController.TabInfo tabInfo = new TabController.TabInfo(R.layout.item_checkbox, page);
//                tabInfo.setItemText("item add");
//
//                controller.addTab(4, tabInfo);
//
//                Button divider = new Button(TabController.this);
//                divider.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        controller.setCurrentTab(5);
//                    }
//                });
//                controller.addDivider(divider, 5, new ViewGroup.LayoutParams(-1, -2));
//
//            }
//        }, 10000);

    }

    public View createTestPage1() {
        LinearLayout contentLayout = new LinearLayout(this);
        contentLayout.setOrientation(LinearLayout.VERTICAL);

        FrameLayout itemAndSelector = new FrameLayout(this);
        TabSelectorView selectorView = new TabSelectorView(this);
        LinearLayout itemGroup = new LinearLayout(this);
        itemGroup.setOrientation(LinearLayout.HORIZONTAL);

        itemAndSelector.addView(selectorView, -1, -1);
        itemAndSelector.addView(itemGroup, -1, -2);

        ViewPager pager = new ViewPager(this);
        LinearLayout.LayoutParams pager_params = new LinearLayout.LayoutParams(-2, -2);
        pager_params.weight = 1;

        contentLayout.addView(itemAndSelector, -1, -2);
        contentLayout.addView(pager, pager_params);

        TabController page1_controller = new TabController(itemGroup, pager, selectorView);
//        page1_controller.setSelector(org.pinwheel.demo.R.drawable.ic_load);

        List<TabController.TabInfo> tabInfos1 = new ArrayList<TabController.TabInfo>(5);
        for (int i = 0; i < 5; i++) {
            TextView page = new TextView(this);
            page.setText("page " + i);
            page.setBackgroundColor(colors[(int) (Math.random() * colors.length)]);
            TabController.TabInfo testTab = new TabController.TabInfo(org.pinwheel.demo4agility.R.layout.item_checkbox, page);
            testTab.setItemText("item " + i);
//            page1_controller.addTabs(testTab);
            tabInfos1.add(testTab);
        }
        page1_controller.addTabs(tabInfos1);

        page1_controller.setHorizontalEqually();

        return contentLayout;
    }

    public View createTestPage2() {
        View contentLayout = LayoutInflater.from(this).inflate(org.pinwheel.demo4agility.R.layout.view_tab_top, null, false);

        TabController page2_controller = new TabController(
                (ViewGroup) contentLayout.findViewById(org.pinwheel.demo4agility.R.id.item_group),
                (ViewPager) contentLayout.findViewById(org.pinwheel.demo4agility.R.id.pager),
                (TabController.ISelectable) contentLayout.findViewById(org.pinwheel.demo4agility.R.id.selector));

//        page2_controller.setSelector(org.pinwheel.demo.R.drawable.ic_sex_man);

        List<TabController.TabInfo> tabInfos2 = new ArrayList<TabController.TabInfo>(20);
        for (int i = 0; i < 20; i++) {
            TextView page = new TextView(this);
            page.setText("page " + i);
            page.setGravity(Gravity.CENTER);
            page.setBackgroundColor(colors[(int) (Math.random() * colors.length)]);
            TabController.TabInfo testTab = new TabController.TabInfo(org.pinwheel.demo4agility.R.layout.item_checkbox, page);
            testTab.setItemText("item " + i);
//            page2_controller.addTabs(testTab);
            tabInfos2.add(testTab);
        }
        page2_controller.addTabs(tabInfos2);

        return contentLayout;
    }

    public View createTestPage3() {
        LinearLayout contentLayout = new LinearLayout(this);
        contentLayout.setOrientation(LinearLayout.VERTICAL);

        FrameLayout itemAndSelector = new FrameLayout(this);
        TabSelectorView selectorView = new TabSelectorView(this);
        LinearLayout itemGroup = new LinearLayout(this);
        itemGroup.setOrientation(LinearLayout.HORIZONTAL);

        itemAndSelector.addView(selectorView, -1, -1);
        itemAndSelector.addView(itemGroup, -1, -2);

        ViewPager pager = new ViewPager(this);
        LinearLayout.LayoutParams pager_params = new LinearLayout.LayoutParams(-2, -2);
        pager_params.weight = 1;

        contentLayout.addView(pager, pager_params);
        contentLayout.addView(itemAndSelector, -1, -2);

        TabController page3_controller = new TabController(itemGroup, pager, selectorView);
//        page3_controller.setSelector(org.pinwheel.demo.R.drawable.ic_sex_woman);

        List<TabController.TabInfo> tabInfos3 = new ArrayList<TabController.TabInfo>(5);
        for (int i = 0; i < 4; i++) {
            TextView page = new TextView(this);
            page.setText("page " + i);
            page.setBackgroundColor(colors[(int) (Math.random() * colors.length)]);
            TabController.TabInfo testTab = new TabController.TabInfo(org.pinwheel.demo4agility.R.layout.item_checkbox, page);
            if (i % 2 == 0) {
                testTab.setItemText("itemitem " + i);
            } else {
                testTab.setItemText("" + i);
            }
//            page3_controller.addTabs(testTab);
            tabInfos3.add(testTab);
        }
        page3_controller.addTabs(tabInfos3);

        return contentLayout;
    }

}