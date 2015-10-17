package org.pinwheel.demo4agility.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import org.pinwheel.agility.view.SweetGridView;

import java.util.Arrays;

public class OverGridActivity extends Activity implements AdapterView.OnItemClickListener {

    SweetGridView swipeList;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        this.setContentView(org.pinwheel.demo4agility.R.layout.over_grid);
        this.init();
    }

    private void init() {
        swipeList = (SweetGridView) findViewById(org.pinwheel.demo4agility.R.id.swipe);
        swipeList.setOnItemClickListener(this);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1,
                Arrays.asList("test1", "test2", "test2",
                        "test1", "test2", "test2", "test2",
                        "test1", "test2", "test2", "test2",
                        "test1", "test2", "test2", "test2",
                        "test1", "test2", "test2", "test2",
                        "test1", "test2", "test2", "test2",
                        "test1", "test2", "test2", "test2",
                        "test1", "test2", "test2", "test2",
                        "test1", "test2", "test2", "test2",
                        "test1", "test2", "test2", "test2",
                        "test1", "test2", "test2", "test2",
                        "test1", "test2", "test2", "test2",
                        "test1", "test2", "test2", "test2",
                        "test1", "test2", "test2", "test2",
                        "test1", "test2", "test2", "test2",
                        "test1", "test2", "test2", "test2",
                        "test1", "test2", "test2", "test2",
                        "test1", "test2", "test2", "test2"));
        swipeList.setAdapter(adapter);
        swipeList.setOnItemClickListener(this);
        swipeList.setNeedHold(150, 100);
        swipeList.doSwipeToHold(false, 1000);
    }

    Handler mHandler = new Handler();
    Runnable reset = new Runnable() {
        @Override
        public void run() {
            swipeList.reset();
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this, position + "", Toast.LENGTH_SHORT).show();
    }
}