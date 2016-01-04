package org.pinwheel.demo4agility.activity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

import org.pinwheel.agility.view.drag.DragListView;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
abstract class AbsMethodListActivity extends AbsTestActivity {

    @Override
    protected final View getContentView() {
        ArrayList<String> requestMethods = new ArrayList<>();
        Method[] methods = AbsMethodListActivity.this.getClass().getMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(TestMethod.class)) {
                requestMethods.add(method.getName());
            }
        }
        ListView list = new DragListView(this);
        BaseAdapter adapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, requestMethods);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String methodName = String.valueOf(parent.getAdapter().getItem(position));
                try {
                    Method method = AbsMethodListActivity.this.getClass().getDeclaredMethod(methodName);
                    method.setAccessible(true);
                    method.invoke(AbsMethodListActivity.this);
                    AbsMethodListActivity.this.onItemClick(parent, view, position, id);
                } catch (Exception e) {
                    logout(e.getClass().getSimpleName() + ": " + e.getMessage());
                }
            }
        });
        return list;
    }

    protected void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO: 1/4/16
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    protected @interface TestMethod {
    }

}
