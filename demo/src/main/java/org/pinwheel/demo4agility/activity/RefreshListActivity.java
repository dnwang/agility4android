package org.pinwheel.demo4agility.activity;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import org.pinwheel.agility.adapter.SimpleArrayAdapter;
import org.pinwheel.agility.view.swiperefresh.SwipeEventHelper;
import org.pinwheel.agility.view.swiperefresh.SwipeGridView;
import org.pinwheel.demo4agility.R;

public class RefreshListActivity extends AbsTesterActivity implements AdapterView.OnItemClickListener {

    SwipeEventHelper eventHelper;

    Adapter adapter;

    @Override
    protected void beforeInitView() {

    }

    @Override
    protected View getContentView() {
        View contentView = inflate(R.layout.activity_refresh_list);

        SwipeGridView swipeList = (SwipeGridView) contentView.findViewById(R.id.swipe);
        adapter = new Adapter();
        eventHelper = new SwipeEventHelper(swipeList, onSwipeAdapter);
        eventHelper.setLoadingMoreInLastLine(1);
        swipeList.setAdapter(adapter);
        swipeList.setNumColumnsAndNormalStyle(3);
        swipeList.setOnItemClickListener(this);
        swipeList.setSwipeEventHelper(eventHelper);

        addTestItems(50);

        return contentView;
    }

    @Override
    protected void afterInitView() {

    }

    private void addTestItems(int size) {
        for (int index = 0; index < size; index++) {
            adapter.addItem(index + " " + index + " " + index);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        Toast.makeText(this, position + "", Toast.LENGTH_SHORT).show();
    }

    private SwipeEventHelper.OnSwipeAdapter onSwipeAdapter = new SwipeEventHelper.OnSwipeAdapter() {
        int size;
        int loadmore_times;

        @Override
        public void onLoading(final int type) {
            System.out.println("onLoading() type:" + type);

            if (loadmore_times > 3 && type != SwipeEventHelper.TYPE_PULL_DOWN) {
                eventHelper.onLoadComplete(true);
                return;
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (type == SwipeEventHelper.TYPE_PULL_DOWN) {
                        loadmore_times = 0;
                        size = 40;
                        adapter.removeAll();
                        adapter.notifyDataSetChanged();
                    } else {
                        size = 4;
                        loadmore_times++;
                    }

                    addTestItems(size);
                    eventHelper.onLoadComplete(false);
                }
            }, 1000);
        }
    };

    class Adapter extends SimpleArrayAdapter<String> {
        LayoutInflater inflater = LayoutInflater.from(RefreshListActivity.this);

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = inflater.inflate(android.R.layout.simple_list_item_1, null);
            TextView txt = (TextView) v.findViewById(android.R.id.text1);
            txt.setText(getItem(position));
            return v;
        }
    }

}