package org.pinwheel.demo4agility.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.pinwheel.agility.adapter.SimpleArrayAdapter;
import org.pinwheel.agility.compat.GrantPermissionsHelper;
import org.pinwheel.agility.util.callback.Action1;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.UUID;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
abstract class AbsTesterActivity extends Activity {
    private static final String TAG = AbsTesterActivity.class.getSimpleName();

    private static final DateFormat FORMAT = new SimpleDateFormat("[MM-dd HH:mm:ss]", Locale.PRC);

    protected int LOGGER_BUF_SIZE = -1;
    protected final Handler mainHandler = new Handler(Looper.getMainLooper());
    protected ViewHolder holder;

    private View loggerView;
    private LoggerAdapter adapter = new LoggerAdapter();
    private GrantPermissionsHelper grantPermissionsHelper = new GrantPermissionsHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        beforeInitView();
        holder = new ViewHolder(initView());
        setContentView(holder.contentView);
        showLogger(false);
        afterInitView();

        loggerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                AbsTesterActivity.this.onGlobalLayout();
                loggerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    private View initView() {
        ListView listView = new ListView(this);
        listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        listView.setDivider(null);
        listView.setAdapter(adapter);
        ImageView emptyTips = new ImageView(this);
        emptyTips.setOnClickListener(v -> {
        });
        emptyTips.setBackgroundResource(0);
        emptyTips.setScaleType(ImageView.ScaleType.CENTER);
        emptyTips.setImageResource(android.R.drawable.ic_dialog_info);
        listView.setEmptyView(emptyTips);
        FrameLayout wrapper = new FrameLayout(this);
        wrapper.setBackgroundColor(Color.parseColor("#AA000000"));
        wrapper.addView(emptyTips, -1, -1);
        wrapper.addView(listView, -1, -1);
        loggerView = wrapper;

        FrameLayout contentView = new FrameLayout(this);
        View v = getContentView();
        if (v != null) {
            contentView.addView(v, -1, -1);
        }
        contentView.addView(loggerView, -1, -1);
        return contentView;
    }

    protected abstract View getContentView();

    protected abstract void beforeInitView();

    protected abstract void afterInitView();

    protected void onGlobalLayout() {
        // TODO: 4/30/16
    }

    protected final View inflate(int layout) {
        return View.inflate(this, layout, null);
    }

    protected final void requestPermissions(Action1<Boolean> callback, String... permissions) {
        grantPermissionsHelper.requestPermissions(this, callback, permissions);
    }

    protected final void postDelayed(Runnable runnable, long delay) {
        mainHandler.postDelayed(runnable, delay);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        grantPermissionsHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
        if (isLoggerShown()) {
            showLogger(false);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 99999, 0, "Logger").setIcon(android.R.drawable.ic_menu_view).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getItemId() == 99999) {
            showLogger(!isLoggerShown());
        }
        return super.onMenuItemSelected(featureId, item);
    }

    protected final boolean isLoggerShown() {
        return loggerView.getVisibility() == View.VISIBLE;
    }

    protected final void showLogger(boolean is) {
        loggerView.setVisibility(is ? View.VISIBLE : View.GONE);
        if (is) {
            adapter.notifyDataSetChanged();
        }
    }

    private void putLogEntity(LogEntity entity) {
        if (LOGGER_BUF_SIZE > 0) {
            int overSize = Math.max(0, adapter.getCount() - LOGGER_BUF_SIZE);
            for (int i = 0; i < overSize; i++) {
                adapter.remove(i);
            }
        }
        adapter.addItem(entity);
    }

    protected final void logout(Object obj, Object... tags) {
        final Object[] finalTags = (tags == null || tags.length == 0) ? new String[]{TAG} : tags;
        Log.d(Arrays.toString(finalTags), String.valueOf(obj));// to logcat
        putLogEntity(new LogEntity(obj, finalTags));// to logger view
        if (isLoggerShown()) {
            if (Looper.myLooper() != Looper.getMainLooper()) {
                mainHandler.post(() -> adapter.notifyDataSetChanged());
            } else {
                adapter.notifyDataSetChanged();
            }
        }
    }

    protected final void countTime(final Runnable task, Object... tags) {
        new Thread(() -> {
            long beginTime = System.currentTimeMillis();
            task.run();
            long endTime = System.currentTimeMillis();
            logout(String.format("%s%s->%d(ms)", FORMAT.format(beginTime), FORMAT.format(endTime), (endTime - beginTime)), tags);
        }).start();
    }

    /**
     * Logger view data adapter
     */
    private static final class LoggerAdapter extends SimpleArrayAdapter<LogEntity> {
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LogEntity entity = getItem(position);
            if (convertView == null) {
                convertView = createView(parent.getContext());
            }
            Holder holder = (Holder) convertView.getTag();
            holder.title.setText(String.format("%s: %s", entity.getTime(), entity.getTags()));
            holder.txt.setText(String.valueOf(entity.getLogObj()));
            return convertView;
        }

        private View createView(Context context) {
            LinearLayout contentView = new LinearLayout(context);
            contentView.setOrientation(LinearLayout.VERTICAL);
            Holder holder = new Holder();
            contentView.setTag(holder);
            holder.title = new TextView(context);
            holder.title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
            holder.txt = new TextView(context);
            holder.txt.setTextColor(Color.CYAN);
            contentView.addView(holder.title);
            contentView.addView(holder.txt);
            return contentView;
        }

        private static final class Holder {
            TextView title;
            TextView txt;
        }
    }

    /**
     * Log struct
     */
    private static final class LogEntity {

        private String id;
        private long time;
        private Object[] tags;
        private Object obj;

        public LogEntity(Object obj, Object... tags) {
            this.id = UUID.randomUUID().toString();
            this.time = System.currentTimeMillis();
            this.tags = tags;
            this.obj = obj;
        }

        private String tmpTimeString = null;
        private String tmpTagString = null;
        private String tmpObjString = null;

        public String getTime() {
            if (tmpTimeString == null) {
                tmpTimeString = FORMAT.format(time);
            }
            return tmpTimeString;
        }

        public String getTags() {
            if (tmpTagString == null) {
                tmpTagString = Arrays.toString(tags);
            }
            return tmpTagString;
        }

        public String getLogObj() {
            if (tmpObjString == null) {
                if (obj == null) {
                    tmpObjString = "null";
                } else if (obj instanceof String) {
                    tmpObjString = String.valueOf(obj);
                } else {
                    tmpObjString = new Gson().toJson(obj);
                }
            }
            return tmpObjString;
        }

        @Override
        public String toString() {
            return getTime() + getTags() + getLogObj();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            LogEntity logEntity = (LogEntity) o;
            return id != null ? id.equals(logEntity.id) : logEntity.id == null;
        }

        @Override
        public int hashCode() {
            return id != null ? id.hashCode() : 0;
        }
    }

    /**
     * View references helper
     */
    public static final class ViewHolder {
        private SparseArray<View> holder;
        private View contentView;

        public ViewHolder(View root) {
            this.contentView = root;
        }

        public <T extends View> T getView(int id) {
            if (holder == null) {
                holder = new SparseArray<>();
            }
            View view = holder.get(id);
            if (view == null && contentView != null) {
                view = contentView.findViewById(id);
                holder.put(id, view);
            }
            return (T) view;
        }

        public TextView getTextView(int id) {
            return getView(id);
        }

        public Button getButton(int id) {
            return getView(id);
        }

        public ImageView getImageView(int id) {
            return getView(id);
        }

        public ViewGroup getViewGroup(int id) {
            return getView(id);
        }

        public ListView getListView(int id) {
            return getView(id);
        }
    }

}
