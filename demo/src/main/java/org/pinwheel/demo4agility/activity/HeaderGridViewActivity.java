package org.pinwheel.demo4agility.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import org.pinwheel.agility.adapter.SimpleArrayAdapter;
import org.pinwheel.agility.adapter.SimplePagerAdapter;
import org.pinwheel.demo4agility.R;

import java.util.ArrayList;

public class HeaderGridViewActivity extends Activity implements AdapterView.OnItemClickListener {

    org.pinwheel.agility.view.HeaderGridView headerGrid;

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
        this.setContentView(R.layout.header_grid);
        this.init();
    }

    private void init() {
        headerGrid = (org.pinwheel.agility.view.HeaderGridView) findViewById(R.id.grid);
        initHeader();
        Adapter adapter = new Adapter();
        headerGrid.setAdapter(adapter);
        headerGrid.setOnItemClickListener(this);
        for (int index = 0; index < 50; index++) {
            adapter.addItem(index + "," + index + "," + index);
        }
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
        headerGrid.addHeaderView(headerPager);
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
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

    }

    class Adapter extends SimpleArrayAdapter<String> {
        LayoutInflater inflater = LayoutInflater.from(HeaderGridViewActivity.this);

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = inflater.inflate(android.R.layout.simple_list_item_1, null);
            TextView txt = (TextView) v.findViewById(android.R.id.text1);
            txt.setText(getItem(position));
            return v;
        }
    }

}