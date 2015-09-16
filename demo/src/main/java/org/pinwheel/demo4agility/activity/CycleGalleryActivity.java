package org.pinwheel.demo4agility.activity;

import android.graphics.Color;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import org.pinwheel.agility.adapter.SimpleArrayAdapter;
import org.pinwheel.agility.view.SweetCircularView;

import java.util.Arrays;


public class CycleGalleryActivity extends AbsTestActivity {

    private SweetCircularView gallery;

    private SimpleArrayAdapter adapter = new SimpleArrayAdapter<Integer>() {
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
//            if (convertView == null) {
//            convertView = createView(position);
//            }
            if (convertView == null) {
                convertView = new Button(CycleGalleryActivity.this);
            }

            Button btn = (Button) convertView;
            btn.setGravity(Gravity.CENTER);
            btn.setTextSize(32);
            btn.setText("" + position);
            btn.setBackgroundColor(getResources().getColor(getItem(position)));
//            convertView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    logout(position);
//                }
//            });
            logout("getView() position:" + position);
            return convertView;
        }

        private View createView(final int position) {
            HorizontalScrollView scrollView = new HorizontalScrollView(CycleGalleryActivity.this);
            scrollView.setBackgroundColor(Color.WHITE);
            LinearLayout linearLayout = new LinearLayout(CycleGalleryActivity.this);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            TextView textView = new TextView(CycleGalleryActivity.this);
            textView.setBackgroundColor(getResources().getColor(getItem(position)));
            textView.setText("sdfasfasdfafafaf\n\n\n\nadfadfafdafasfasf\n\n\n\nsdfsfsfsdf\n\n\n\nsdfsfsf\n\n\n\nsdfsfsf\n\n\n\nsdfsfsfsfds");
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    logout(position);
                }
            });
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(1500, -1);
            linearLayout.addView(textView, params);
            scrollView.addView(linearLayout);
            return scrollView;
        }

    };

    @Override
    protected void onInitInCreate() {
        adapter.addAll(Arrays.asList(
//                android.R.color.holo_red_dark,
//                android.R.color.holo_orange_dark,
//                android.R.color.holo_blue_dark,
//                android.R.color.holo_green_dark,
//                android.R.color.darker_gray
        ));
    }

    Handler handler = new Handler();

    @Override
    protected View getContentView() {
        FrameLayout container = new FrameLayout(this);

        gallery = new SweetCircularView(this);
        gallery.setAdapter(adapter);
        gallery.setOrientation(LinearLayout.HORIZONTAL);
        gallery.setSensibility(0.25f);
        FrameLayout.LayoutParams galleryParams = new FrameLayout.LayoutParams(-1, -1);
        galleryParams.setMargins(50, 50, 50, 50);
        container.addView(gallery, galleryParams);

        FrameLayout.LayoutParams left = new FrameLayout.LayoutParams(-2, -2, Gravity.LEFT | Gravity.CENTER_VERTICAL);
        Button leftBtn = new Button(this);
        leftBtn.setText("Fun0");
        container.addView(leftBtn, left);

        FrameLayout.LayoutParams right = new FrameLayout.LayoutParams(-2, -2, Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        Button rightBtn = new Button(this);
        rightBtn.setText("Fun1");
        container.addView(rightBtn, right);

        FrameLayout.LayoutParams top = new FrameLayout.LayoutParams(-2, -2, Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        Button topBtn = new Button(this);
        topBtn.setText("Fun2");
        container.addView(topBtn, top);

        FrameLayout.LayoutParams bottom = new FrameLayout.LayoutParams(-2, -2, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        Button bottomBtn = new Button(this);
        bottomBtn.setText("Fun3");
        container.addView(bottomBtn, bottom);

        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                gallery.setCurrentIndex(gallery.getCurrentIndex() - 1);
                adapter.addItem(android.R.color.holo_red_dark);
                adapter.addItem(android.R.color.holo_orange_dark);
                adapter.addItem(android.R.color.holo_blue_dark);
                adapter.addItem(android.R.color.holo_green_dark);
                adapter.notifyDataSetChanged();
            }
        });
        topBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                adapter.removeAll();
//                adapter.notifyDataSetChanged();
                gallery.setCurrentIndex(gallery.getCurrentIndex() - 1);
            }
        });
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.addItem(android.R.color.darker_gray);
                adapter.notifyDataSetChanged();
            }
        });
        bottomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                adapter.removeAll();
//                adapter.addItem(android.R.color.holo_blue_dark);
//                adapter.notifyDataSetChanged();
                gallery.setCurrentIndex(gallery.getCurrentIndex() + 1);
            }
        });

        return container;
    }

    @Override
    protected void doTest() {

    }

}
