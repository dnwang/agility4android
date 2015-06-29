package org.pinwheel.demo4agility.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import org.pinwheel.agility.adapter.SimplePagerAdapter;
import org.pinwheel.agility.view.SweetListView;
import org.pinwheel.demo4agility.R;

import java.util.ArrayList;

public class OverListActivity extends Activity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    SweetListView swipeList;
    ArrayAdapter<String> adapter;

    private int[] colors = new int[]{
            Color.BLUE,
            Color.YELLOW,
            Color.RED,
            Color.GREEN,
            Color.GRAY,
            Color.CYAN
    };

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        this.setContentView(R.layout.over_list);
        this.init();
    }

    private void init() {
        swipeList = (SweetListView) findViewById(R.id.swipe);
        swipeList.setOnItemClickListener(this);
        ArrayList<String> datas = new ArrayList<String>();
        for (int index = 0; index < 22; index++) {
            datas.add(index + "," + index + "," + index);
        }
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, datas);
        swipeList.setOnItemClickListener(this);
        swipeList.setOnItemLongClickListener(this);
        swipeList.setNeedHold(200, 200);
        swipeList.doSwipeToHold(true, 1000);
        initHeader();
    }

    private void initHeader() {
        final ViewPager headerPager = new ViewPager(this);
        ArrayList<View> pagers = new ArrayList<View>(4);
        for (int i = 0; i < 4; i++) {
            TextView page = new TextView(this);
            page.setBackgroundColor(colors[(int) (Math.random() * colors.length)]);
            page.setText("page_" + i);
            page.setTextColor(Color.BLACK);
            pagers.add(page);
        }
        PagerAdapter adapter = new SimplePagerAdapter(pagers);
        headerPager.setAdapter(adapter);
        swipeList.addHeaderView(headerPager);
        swipeList.setAdapter(this.adapter);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ViewGroup.LayoutParams params = headerPager.getLayoutParams();
                params.height = 300;
                params.width = -1;
                headerPager.setLayoutParams(params);
            }
        },1000);
    }

//    Handler mHandler = new Handler();
//    Runnable reset = new Runnable() {
//        @Override
//        public void run() {
//            swipeList.reset();
//        }
//    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this, position + "", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this, position + "", Toast.LENGTH_SHORT).show();
        return false;
    }
}