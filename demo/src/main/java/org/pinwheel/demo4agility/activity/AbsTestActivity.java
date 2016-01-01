package org.pinwheel.demo4agility.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
abstract class AbsTestActivity extends Activity {
    private static final String TAG = AbsTestActivity.class.getSimpleName();

    private StringBuffer logBuffer;
    private ScrollView logContainer;
    private TextView logTxt;

    protected Handler uiHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onInitInCreate();
        setContentView(initView());

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(200);
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            doSomethingAfterCreated();
                        }
                    });
                } catch (InterruptedException e) {
                }
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        if (isShowLog()) {
            showLog(!isShowLog());
        } else {
            super.onBackPressed();
        }
    }

    private View initView() {
        uiHandler = new Handler(Looper.getMainLooper());
        logBuffer = new StringBuffer();

        logContainer = new ScrollView(getBaseContext());
        logContainer.setOverScrollMode(View.OVER_SCROLL_NEVER);
        HorizontalScrollView hScrollView = new HorizontalScrollView(getBaseContext());
        hScrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        logContainer.addView(hScrollView, -1, -1);
        logContainer.setBackgroundColor(Color.parseColor("#88000000"));
        LinearLayout wrapper = new LinearLayout(getBaseContext());
        wrapper.setOrientation(LinearLayout.HORIZONTAL);

        logTxt = new TextView(getBaseContext());
        logTxt.setTextColor(Color.WHITE);
        logTxt.setShadowLayer(5f, 2f, 2f, Color.BLACK);
        wrapper.addView(logTxt, -1, -1);
        hScrollView.addView(wrapper, -1, -1);

        FrameLayout container = new FrameLayout(getBaseContext());
        View v = getContentView();
        if (v != null) {
            container.addView(v);
        }
        container.addView(logContainer, -1, -1);

        showLog(false);
        return container;
    }

    protected abstract void onInitInCreate();

    protected abstract View getContentView();

    protected abstract void doSomethingAfterCreated();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 99999, 0, "ScreenLog")
                .setIcon(android.R.drawable.ic_menu_view)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getItemId() == 99999) {
            showLog(!isShowLog());
        }
        return super.onMenuItemSelected(featureId, item);
    }

    protected boolean isShowLog() {
        return logContainer.getVisibility() == View.VISIBLE;
    }

    protected void showLog(boolean is) {
        if (is) {
            logContainer.setVisibility(View.VISIBLE);
        } else {
            logContainer.setVisibility(View.GONE);
        }
    }

    protected void logout(final Object log) {
        String logStr = (log == null ? "null" : log.toString());
        Log.d(TAG, logStr);
        logBuffer.append(logStr + "\n");
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                logTxt.setText(logBuffer.toString());
            }
        });
    }

    protected void logoutTime(String tag) {
        logout("--> " + tag + " ［" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "]");
    }

    protected void logoutCount(String tag, final Runnable task) {
        logoutTime(tag + " start");
        long first_time = System.currentTimeMillis();
        task.run();
        logout("--> " + tag + " end: " + (System.currentTimeMillis() - first_time) + " ms");
    }

    protected void logoutObject(Object obj) {
        if (obj == null) {
            logout("logoutObject(): obj = null");
            return;
        }
        logoutTime("Reflex Object");
        logout("┌ " + obj.getClass().getSimpleName() + " {");
        reflexValue("│\t", obj);
        logout("└ }");
    }

    private void reflexValue(String space, Object obj) {
        Class cls = obj.getClass();
        for (Field field : cls.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                String type = field.getType().getSimpleName();
                String name = field.getName();
                String modifier = Modifier.toString(field.getModifiers());

                if (type.equals("int") || type.equals("Integer")
                        || type.equalsIgnoreCase("boolean")
                        || type.equalsIgnoreCase("float")
                        || type.equalsIgnoreCase("string")
                        || type.equalsIgnoreCase("long")
                        || type.equalsIgnoreCase("double")
                        || type.equalsIgnoreCase("short")
                        || type.equalsIgnoreCase("char")) {
                    Object v = field.get(obj);
                    String value = v == null ? "null" : v.toString();
                    logout(space + "\t " + modifier + " " + type + " " + name + " = " + value);
                } else {
                    Object v = field.get(obj);
                    if (v == null) {
                        logout(space + "\t " + modifier + " " + type + " " + name + " = null");
                    } else {
                        logout(space + "┌ " + modifier + " " + type + " " + name + " {");
                        reflexValue(space + "│\t", v);
                        logout(space + "└ }");
                    }
                }
            } catch (IllegalAccessException e) {
            }
        }
    }

}
