package org.pinwheel.demo4agility.activity;

import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import org.pinwheel.agility.adapter.SimplePagerAdapter;
import org.pinwheel.agility.view.drag.DragRefreshWrapper;
import org.pinwheel.demo4agility.R;

import java.util.ArrayList;

public class DragViewActivity extends AbsTestActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

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
        View contentView = LayoutInflater.from(this).inflate(R.layout.activity_draggable, null);

        final AbsListView dragView = (AbsListView) contentView.findViewById(R.id.drag_view);
        initHeader((ListView) dragView);
        initAdapter(dragView);

        final DragRefreshWrapper dragRefreshWrapper = (DragRefreshWrapper) contentView.findViewById(R.id.drag_wrapper);
        dragRefreshWrapper.getHeaderIndicator().setBackgroundColor(Color.RED);
//        dragRefreshWrapper.setFooterVisibility(false);
        dragRefreshWrapper.setOnRefreshListener(new DragRefreshWrapper.OnRefreshListener() {
            @Override
            public void onRefresh() {
                delay(3000, new Runnable() {
                    @Override
                    public void run() {
                        dragRefreshWrapper.onRefreshComplete();
                        adapter.insert("Im refresh", 0);
                    }
                });
            }

            @Override
            public void onLoadMore() {
                delay(1000, new Runnable() {
                    @Override
                    public void run() {
                        dragRefreshWrapper.onLoadComplete();
                        adapter.add("Im added");
                    }
                });
            }
        });

        return contentView;
    }

    @Override
    protected void doTest() {

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

    private void initAdapter(AbsListView absListView) {
        final int size = 30;
        ArrayList<String> data = new ArrayList<>(size);
        for (int index = 0; index < size; index++) {
            data.add(index + ", " + index + ", " + index);
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, data);
        absListView.setOnItemClickListener(this);
        absListView.setOnItemLongClickListener(this);
        absListView.setAdapter(adapter);
    }

    private void delay(long delay, Runnable runnable) {
        new Handler(Looper.getMainLooper()).postDelayed(runnable, delay);
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

}