package org.pinwheel.sample.activity;

import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import org.pinwheel.agility.view.InternalListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved
 *
 * @author dnwang
 */
public class InternalListActivity extends AbsTesterActivity {

    @Override
    protected void beforeInitView() {

    }

    @Override
    protected View getContentView() {
        ScrollView scrollView = new ScrollView(getBaseContext());
        LinearLayout container = new LinearLayout(getBaseContext());
        container.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(container);

        container.addView(createHeader());
        container.addView(createListView());
        container.addView(createFooter());
        return scrollView;
    }

    @Override
    protected void afterInitView() {

    }

    private View createHeader() {
        LinearLayout header_container = new LinearLayout(this);
        header_container.setOrientation(LinearLayout.VERTICAL);
        for (int i = 0; i < 3; i++) {
            Button button = new Button(this);
            button.setText("header " + i);
            button.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
            header_container.addView(button, params);
        }
        return header_container;
    }

    private View createFooter() {
        LinearLayout footer_container = new LinearLayout(this);
        footer_container.setOrientation(LinearLayout.VERTICAL);
        for (int i = 0; i < 3; i++) {
            Button button = new Button(this);
            button.setText("footer " + i);
            button.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
            footer_container.addView(button, params);
        }
        return footer_container;
    }

    private ListView createListView() {
        List<String> data = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            data.add("" + i);
        }
        BaseAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, data);
        InternalListView listView = new InternalListView(getBaseContext());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> Toast.makeText(InternalListActivity.this, "" + position, Toast.LENGTH_SHORT).show());
        return listView;
    }

}
