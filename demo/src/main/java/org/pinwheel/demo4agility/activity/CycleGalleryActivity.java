package org.pinwheel.demo4agility.activity;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.pinwheel.agility.adapter.SimpleArrayAdapter;
import org.pinwheel.agility.view.SweetCircularView;

import java.util.Arrays;


public class CycleGalleryActivity extends AbsTestActivity {
    private static final String TAG = CycleGalleryActivity.class.getSimpleName();

    private SweetCircularView gallery;

    private SimpleArrayAdapter adapter = new SimpleArrayAdapter<Integer>() {
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
//            if (convertView == null) {
//                convertView = new Button(CycleGalleryActivity.this);
//            }
//
//            Button btn = (Button) convertView;
//            btn.setGravity(Gravity.CENTER);
//            btn.setTextSize(32);
//            btn.setText("" + position);
//            btn.setBackgroundColor(getResources().getColor(getItem(position)));
//            convertView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    logout(position);
//                }
//            });
//            logout("getView() position:" + position);
//            return convertView;
            return createView(position);// test for ScrollView
        }

        private View createView(final int position) {
            HorizontalScrollView scrollView = new HorizontalScrollView(CycleGalleryActivity.this);
            LinearLayout linearLayout = new LinearLayout(CycleGalleryActivity.this);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            TextView textView = new TextView(CycleGalleryActivity.this);
            textView.setBackgroundColor(getResources().getColor(getItem(position)));
            textView.setText("" + position);
            textView.setTextSize(32);
            textView.setGravity(Gravity.CENTER);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    logout(position);
                }
            });
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(1200, 600);
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

    @Override
    protected View getContentView() {
        FrameLayout container = new FrameLayout(this);

        gallery = new SweetCircularView(this);
        gallery.setAdapter(adapter);
//        gallery.setOrientation(LinearLayout.HORIZONTAL);
        gallery.setOrientation(LinearLayout.VERTICAL);
        gallery.setSensibility(0.35f);
        FrameLayout.LayoutParams galleryParams = new FrameLayout.LayoutParams(-1, -1);
        galleryParams.setMargins(50, 50, 50, 50);
        container.addView(gallery, galleryParams);

        gallery.setOnItemSwitchListener(new SweetCircularView.OnItemSwitchListener() {
            @Override
            public void onItemSelected(int newDataIndex, int oldDataIndex, int newItemIndex, int oldItemIndex) {
                Log.d(TAG, "onItemSelected(" + newDataIndex + ", " + oldDataIndex + ", " + newItemIndex + ", " + oldItemIndex + ")");
            }

            @Override
            public void onItemScrolled(int dataIndex, int itemIndex, float offset) {
                Log.d(TAG, "onItemScrolled(" + dataIndex + ", " + itemIndex + ", " + offset + ")");
            }
        });

        FrameLayout.LayoutParams left = new FrameLayout.LayoutParams(-2, -2, Gravity.LEFT | Gravity.CENTER_VERTICAL);
        Button leftBtn = new Button(this);
        leftBtn.setText("<");
        container.addView(leftBtn, left);

        FrameLayout.LayoutParams right = new FrameLayout.LayoutParams(-2, -2, Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        Button rightBtn = new Button(this);
        rightBtn.setText(">");
        container.addView(rightBtn, right);

        LinearLayout funcContainer = new LinearLayout(this);
        funcContainer.setOrientation(LinearLayout.HORIZONTAL);
        Button func1 = new Button(this);
        func1.setText("add");
        Button func2 = new Button(this);
        func2.setText("remove");
        Button func3 = new Button(this);
        func3.setText("replace");
        Button func4 = new Button(this);
        func4.setText("reSize");
        funcContainer.addView(func1);
        funcContainer.addView(func2);
        funcContainer.addView(func3);
        funcContainer.addView(func4);
        container.addView(funcContainer);

        func1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.addItem(android.R.color.holo_red_dark);
                adapter.addItem(android.R.color.holo_orange_dark);
                adapter.addItem(android.R.color.holo_blue_dark);
                adapter.addItem(android.R.color.holo_green_dark);
                adapter.notifyDataSetChanged();
            }
        });
        func2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.removeAll();
                adapter.notifyDataSetChanged();
            }
        });
        func3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.removeAll();
                adapter.addItem(android.R.color.darker_gray);
                adapter.addItem(android.R.color.holo_purple);
                adapter.notifyDataSetChanged();
            }
        });
        func4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gallery.setRecycleItemSize(5);
                adapter.notifyDataSetChanged();
            }
        });
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                gallery.setCurrentIndex(gallery.getCurrentIndex() - 1);
                gallery.moveNext();
            }
        });
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                gallery.setCurrentIndex(gallery.getCurrentIndex() + 1);
                gallery.movePrevious();
            }
        });

        return container;
    }

    @Override
    protected void doTest() {

    }

}
