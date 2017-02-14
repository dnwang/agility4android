package org.pinwheel.sample.activity;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.pinwheel.agility.adapter.SimplePagerAdapter;
import org.pinwheel.agility.util.UIUtils;
import org.pinwheel.agility.view.drag.BaseDragIndicator;
import org.pinwheel.agility.view.drag.DragRefreshWrapper;
import org.pinwheel.agility.view.drag.Draggable;
import org.pinwheel.sample.R;
import org.pinwheel.sample.test.CustomProgress;

import java.util.ArrayList;

public class DragViewActivity extends AbsTesterActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

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
    protected void beforeInitView() {

    }

    @Override
    protected View getContentView() {
        View contentView = LayoutInflater.from(this).inflate(R.layout.activity_draggable, null);

        final Button button = (Button) contentView.findViewById(R.id.button);
        button.setOnClickListener(v -> Toast.makeText(v.getContext(), ((TextView) v).getText(), Toast.LENGTH_SHORT).show());

//        final AbsListView dragView = (AbsListView) contentView.findViewById(R.id.drag_view);
//        initHeader((ListView) dragView);
//        initAdapter(dragView);

        final DragRefreshWrapper dragRefreshWrapper = (DragRefreshWrapper) contentView.findViewById(R.id.drag_wrapper);
        dragRefreshWrapper.getHeaderIndicator().setBackgroundColor(Color.RED);
        dragRefreshWrapper.setHeaderIndicator(new CustomIndicator(this));
        dragRefreshWrapper.setFooterVisibility(false);

        dragRefreshWrapper.setOnRefreshListener(new DragRefreshWrapper.OnRefreshListener() {
            @Override
            public void onTopRefresh() {
                delay(3000, () -> {
                    dragRefreshWrapper.onRefreshComplete();
//                        adapter.insert("Im refresh", 0);
                    button.setText("" + (Integer.parseInt(button.getText().toString()) + 1));
                });
            }

            @Override
            public void onBottomLoad() {
                delay(1000, () -> {
                    dragRefreshWrapper.onLoadComplete();
//                        adapter.add("Im added");
                    button.setText("" + (Integer.parseInt(button.getText().toString()) - 1));
                });
            }
        });

        return contentView;
    }

    @Override
    protected void afterInitView() {
        ((DragRefreshWrapper) findViewById(R.id.drag_wrapper)).doRefresh();
    }

    private void initHeader(ListView list) {
        final ViewPager headerPager = new ViewPager(this);
        ArrayList<View> pagers = new ArrayList<>(4);
        for (int i = 0; i < 4; i++) {
            TextView page = new TextView(this);
            page.setId(i);
            page.setBackgroundColor(colors[(int) (Math.random() * colors.length)]);
            page.setGravity(Gravity.CENTER);
            page.setText("" + i);
            page.setTextColor(Color.BLACK);
            pagers.add(page);
            page.setOnClickListener(v -> Toast.makeText(v.getContext(), "Page: " + v.getId(), Toast.LENGTH_SHORT).show());
        }
        PagerAdapter adapter = new SimplePagerAdapter(pagers);
        headerPager.setAdapter(adapter);
        list.addHeaderView(headerPager);

        mainHandler.postDelayed(() -> {
            ViewGroup.LayoutParams params = headerPager.getLayoutParams();
            params.height = 300;
            params.width = -1;
            headerPager.setLayoutParams(params);
        }, 1000);
    }

    private void initAdapter(AbsListView absListView) {
        final int size = 30;
        ArrayList<String> data = new ArrayList<>(size);
        for (int index = 0; index < size; index++) {
            data.add(index + ", " + index + ", " + index);
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, data);
        absListView.setOnItemClickListener(this);
        absListView.setOnItemLongClickListener(this);
        absListView.setAdapter(adapter);
    }

    private void delay(long delay, Runnable runnable) {
        mainHandler.postDelayed(runnable, delay);
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

    /**
     * Custom progress
     */
    private class CustomIndicator extends BaseDragIndicator {

        private CustomProgress progress;

        public CustomIndicator(Context context) {
            super(context);
            this.init();
        }

        public CustomIndicator(Context context, AttributeSet attrs) {
            super(context, attrs);
            this.init();
        }

        public CustomIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            this.init();
        }

        private void init() {
            progress = new CustomProgress(getContext());
            LayoutParams params = new LayoutParams(-2, -2);
            params.gravity = Gravity.CENTER | Gravity.LEFT;
            final int margin = UIUtils.dip2px(getContext(), 8);
            params.setMargins(margin, margin, margin, margin);
            addView(progress, params);

            getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    moveTo(0);
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            });
        }

        @Override
        public void setVisibility(int visibility) {
            super.setVisibility(visibility);
            progress.setVisibility(visibility);
        }

        @Override
        public void onMove(float distance, float offset) {
            final int position = getDraggable().getPosition();
            final int state = getDraggable().getState();
            if (position != Draggable.EDGE_TOP || state == Draggable.STATE_INERTIAL) {
                return;
            }
            final int height = getMeasuredHeight();
            final float percent = Math.min(Math.abs(distance), height) / height;
            moveTo(percent);
        }

        @Override
        public void onHold() {
            super.onHold();
            progress.spin();
        }

        @Override
        public void reset() {
            super.reset();
        }

        private void moveTo(float percent) {
            setTranslationY(-getMeasuredHeight() * (1 - percent));
        }
    }

}