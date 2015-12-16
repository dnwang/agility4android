package org.pinwheel.demo4agility.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import org.pinwheel.agility.adapter.SimplePagerAdapter;
import org.pinwheel.agility.view.drag.Draggable;
import org.pinwheel.demo4agility.R;

import java.util.ArrayList;

public class DragViewActivity extends Activity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drag_view);
        Draggable dragView = (Draggable) findViewById(R.id.drag_view);
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

    private void initListAdapter(AbsListView absListView) {
        final int size = 60;
        ArrayList<String> data = new ArrayList<>(size);
        for (int index = 0; index < size; index++) {
            data.add(index + ", " + index + ", " + index);
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, data);
        absListView.setOnItemClickListener(this);
        absListView.setOnItemLongClickListener(this);
        absListView.setAdapter(adapter);
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