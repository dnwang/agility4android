package org.pinwheel.demo4agility.activity;

import android.graphics.Color;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.pinwheel.agility.adapter.SimplePagerAdapter;
import org.pinwheel.agility.util.UIUtils;
import org.pinwheel.agility.view.drag.DragListView;
import org.pinwheel.agility.view.drag.Draggable;

import java.util.ArrayList;

public class DragListActivity extends AbsTestActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private static final int SIZE = 30;

    private ArrayAdapter<String> adapter;

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
        DragListView list = new DragListView(this);

        ArrayList<String> data = new ArrayList<>(SIZE);
        for (int index = 0; index < SIZE; index++) {
            data.add(index + ", " + index + ", " + index);
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, data);
        list.setOnItemClickListener(this);
        list.setOnItemLongClickListener(this);
        initHeader(list);

        list.setHoldDistance(UIUtils.dip2px(this, 84), UIUtils.dip2px(this, 64));

        list.setOnDragListener(new Draggable.OnDragListener() {
            @Override
            public void onDragStateChanged(int position, int state) {
                Log.d("OnSwipeListener", "onSwipeStateChanged() position:[" + convertPosition(position) + "], state:[" + convertState(state) + "]");
            }

            @Override
            public void onDragging(float distance, float offset) {
                Log.d("OnSwipeListener", "onSwipe() distance:[" + distance + "], offset:[" + offset + "]");
            }
        });

        return list;
    }

    private void initHeader(ListView list) {
        final ViewPager headerPager = new ViewPager(this);
        ArrayList<View> pagers = new ArrayList<View>(4);
        for (int i = 0; i < 4; i++) {
            TextView page = new TextView(this);
            page.setId(i);
            page.setBackgroundColor(colors[(int) (Math.random() * colors.length)]);
            page.setGravity(Gravity.CENTER);
            page.setText("" + i);
            page.setTextColor(Color.BLACK);
            pagers.add(page);
            page.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "Page: " + v.getId(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        PagerAdapter adapter = new SimplePagerAdapter(pagers);
        headerPager.setAdapter(adapter);
        list.addHeaderView(headerPager);
        list.setAdapter(this.adapter);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ViewGroup.LayoutParams params = headerPager.getLayoutParams();
                params.height = 300;
                params.width = -1;
                headerPager.setLayoutParams(params);
            }
        }, 1000);
    }

    @Override
    protected void doTest() {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this, "Click: " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this, "Long: " + position, Toast.LENGTH_SHORT).show();
        return false;
    }

    private String convertState(int state) {
        switch (state) {
            case Draggable.STATE_NONE:
                return "NONE";
            case Draggable.STATE_HOLD:
                return "HOLD";
            case Draggable.STATE_DRAGGING_TOP:
                return "DRAGGING_TOP";
            case Draggable.STATE_DRAGGING_BOTTOM:
                return "DRAGGING_BOTTOM";
            case Draggable.STATE_RESTING_TO_HOLD:
                return "RESTING_TO_HOLD";
            case Draggable.STATE_RESTING_TO_BORDER:
                return "RESTING_TO_BORDER";
        }
        return "";
    }

    private String convertPosition(int position) {
        switch (position) {
            case Draggable.EDGE_NONE:
                return "NONE";
            case Draggable.EDGE_TOP:
                return "TOP";
            case Draggable.EDGE_BOTTOM:
                return "BOTTOM";
        }
        return "";
    }

}