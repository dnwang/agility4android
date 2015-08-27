package org.pinwheel.demo4agility.activity;

import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import org.pinwheel.agility.adapter.SimpleArrayAdapter;
import org.pinwheel.agility.view.SweetCycleGallery;

import java.util.Arrays;


public class CycleGalleryActivity extends AbsTestActivity {

    private SweetCycleGallery gallery;

    private SimpleArrayAdapter adapter = new SimpleArrayAdapter<Integer>() {
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = createView(position);
//            if (convertView == null) {
//                convertView = new Button(CycleGalleryActivity.this);
//            }

//            Button btn = (Button) convertView;
//            btn.setGravity(Gravity.LEFT | Gravity.TOP);
//            btn.setTextSize(32);
//            btn.setText("" + position);
//            btn.setBackgroundColor(getResources().getColor(getItem(position)));
//            convertView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(CycleGalleryActivity.this, "" + position, Toast.LENGTH_SHORT).show();
//                }
//            });
            Log.e("-----", "currentIndex:" + gallery.getCurrentItem());
            return convertView;
        }

        private View createView(final int position) {
            HorizontalScrollView scrollView = new HorizontalScrollView(CycleGalleryActivity.this);
            LinearLayout linearLayout = new LinearLayout(CycleGalleryActivity.this);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            TextView textView = new TextView(CycleGalleryActivity.this);
            textView.setBackgroundColor(getResources().getColor(getItem(position)));
            textView.setText("sdfasfasdfafafaf\n\n\n\nadfadfafdafasfasf\n\n\n\nsdfsfsfsdf\n\n\n\nsdfsfsf\n\n\n\nsdfsfsf\n\n\n\nsdfsfsfsfds");
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(CycleGalleryActivity.this, "" + position, Toast.LENGTH_SHORT).show();
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
//        adapter.addAll(Arrays.asList(
//                android.R.color.holo_red_dark,
//                android.R.color.holo_orange_dark,
//                android.R.color.holo_blue_dark,
//                android.R.color.holo_green_dark,
//                android.R.color.darker_gray
//        ));
    }

    @Override
    protected View getContentView() {
//        new Handler(getMainLooper()).postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                gallery.setAdapter(adapter);
//            }
//        }, 3000l);
        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.addAll(Arrays.asList(
                        android.R.color.holo_red_dark,
                        android.R.color.holo_orange_dark,
                        android.R.color.holo_blue_dark,
                        android.R.color.holo_green_dark,
                        android.R.color.darker_gray
                ));
                adapter.notifyDataSetChanged();
            }
        }, 3000l);

        FrameLayout container = new FrameLayout(this);

        gallery = new SweetCycleGallery(this);
        gallery.setAdapter(adapter);
        gallery.setOrientation(SweetCycleGallery.VERTICAL);
        gallery.setAutoScrollInertial(0.5f);
        FrameLayout.LayoutParams galleryParams = new FrameLayout.LayoutParams(-1, -1);
        gallery.setPadding(50, 50, 50, 50);
        gallery.setBackgroundColor(getResources().getColor(android.R.color.white));
        galleryParams.setMargins(50, 50, 50, 50);
        container.addView(gallery, galleryParams);

        FrameLayout.LayoutParams left = new FrameLayout.LayoutParams(-2, -2, Gravity.LEFT | Gravity.CENTER_VERTICAL);
        Button leftBtn = new Button(this);
        leftBtn.setText("<");
        container.addView(leftBtn, left);

        FrameLayout.LayoutParams right = new FrameLayout.LayoutParams(-2, -2, Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        Button rightBtn = new Button(this);
        rightBtn.setText(">");
        container.addView(rightBtn, right);

        FrameLayout.LayoutParams top = new FrameLayout.LayoutParams(-2, -2, Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        Button topBtn = new Button(this);
        topBtn.setText("^");
        container.addView(topBtn, top);

        FrameLayout.LayoutParams bottom = new FrameLayout.LayoutParams(-2, -2, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        Button bottomBtn = new Button(this);
        bottomBtn.setText("v");
        container.addView(bottomBtn, bottom);

        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gallery.moveLeft();
            }
        });
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gallery.moveRight();
            }
        });
        topBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gallery.moveTop();
            }
        });
        bottomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gallery.moveBottom();
            }
        });

        return container;
    }

    @Override
    protected void doTest() {

    }

}
