package org.pinwheel.demo4agility.activity;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.litesuits.common.io.FileUtils;
import com.litesuits.http.LiteHttpClient;
import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.DataBase;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.assit.WhereBuilder;
import com.litesuits.orm.db.model.ConflictAlgorithm;

import org.pinwheel.agility.adapter.SimpleArrayAdapter;
import org.pinwheel.agility.view.InternalListView;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public class LiteSuitActivity extends AbsTestActivity implements View.OnClickListener {

    private final static int ID_QUERY = 4;
    private final static int ID_DELETE = 3;
    private final static int ID_UPDATE = 2;
    private final static int ID_SAVE = 1;

    private SimpleArrayAdapter adapter;
    private DataBase db;

    @Override
    protected void onInitInCreate() {
        db = LiteOrm.newInstance(getBaseContext(), "test");
    }

    @Override
    protected View getContentView() {
        ScrollView scrollView = new ScrollView(getBaseContext());
        LinearLayout container = new LinearLayout(getBaseContext());
        scrollView.addView(container);
        container.setOrientation(LinearLayout.VERTICAL);
        Button save = new Button(getBaseContext());
        save.setText("save: new random");
        save.setId(ID_SAVE);
        save.setOnClickListener(this);
        container.addView(save, -1, -2);
        Button delete = new Button(getBaseContext());
        delete.setText("delete: int < 6");
        delete.setId(ID_DELETE);
        delete.setOnClickListener(this);
        container.addView(delete, -1, -2);
        Button update = new Button(getBaseContext());
        update.setText("update: int = 5");
        update.setId(ID_UPDATE);
        update.setOnClickListener(this);
        container.addView(update, -1, -2);
        Button query = new Button(getBaseContext());
        query.setText("query: DBStruct");
        query.setId(ID_QUERY);
        query.setOnClickListener(this);
        container.addView(query, -1, -2);

        InternalListView listView = new InternalListView(getBaseContext());
        adapter = new SimpleArrayAdapter<DBStruct>() {
            LayoutInflater inflater = LayoutInflater.from(getBaseContext());

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                convertView = inflater.inflate(android.R.layout.simple_list_item_1, null);
                ((TextView) convertView.findViewById(android.R.id.text1)).setText(getItem(position).toString());
                return convertView;
            }
        };
        listView.setAdapter(adapter);
        container.addView(listView);
        return scrollView;
    }

    @Override
    protected void doTest() {
        findViewById(ID_QUERY).performClick();
        testHttp();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case ID_SAVE:
                int size = new Random().nextInt(4) + 1;
                DBStruct data = new DBStruct(32);
                data.test_string = "string";
                for (int i = 0; i < size; i++) {
                    data.addList(new DBStruct.DBStruct2());
                }
                db.save(data);
                findViewById(ID_QUERY).performClick();
                break;
            case ID_DELETE:
                db.delete(DBStruct.class, new WhereBuilder("test_int < ?", new Integer[]{6}));
                findViewById(ID_QUERY).performClick();
                break;
            case ID_UPDATE:
                List<DBStruct> datas = db.query(QueryBuilder.create(DBStruct.class).where("test_int > ?", new Integer[]{5}));
                for (DBStruct dataStruct : datas) {
                    dataStruct.test_int = 5;
                }
                db.update(datas, ConflictAlgorithm.Fail);
                findViewById(ID_QUERY).performClick();
                break;
            case ID_QUERY:
                adapter.removeAll();
                adapter.addAll(db.queryAll(DBStruct.class));
                adapter.notifyDataSetChanged();
                break;
        }
    }

    private void testHttp() {
        logout("-------------- Http ---------------");
        new Thread() {
            @Override
            public void run() {
                final String name = "/sdcard/baidu.html";
                // lite http
                String html = LiteHttpClient.newApacheHttpClient(LiteSuitActivity.this).post("http://www.baidu.com");

                try {
                    // lite fileutils
                    FileUtils.write(new File(name), html, "utf-8");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {

                        String string_from_file = null;
                        try {
                            // lite fileutils
                            string_from_file = FileUtils.readFileToString(new File(name));
                            logout(string_from_file);
                        } catch (IOException e) {
                            logout("loading error");
                            e.printStackTrace();
                        }

                    }
                });

            }
        }.start();
    }

}
