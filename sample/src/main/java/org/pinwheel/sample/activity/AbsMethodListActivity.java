package org.pinwheel.sample.activity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.pinwheel.agility.view.drag.DragListView;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
abstract class AbsMethodListActivity extends AbsTesterActivity {

    @Override
    protected final View getContentView() {
        ArrayList<Map<String, String>> flagMethodList = new ArrayList<>();
        Method[] declaredMethods = AbsMethodListActivity.this.getClass().getDeclaredMethods();
        for (Method method : declaredMethods) {
            TestMethod testMethod = method.getAnnotation(TestMethod.class);
            if (testMethod != null) {
                Map<String, String> args = new HashMap<>();
                args.put("title", testMethod.title());
                args.put("name", method.getName());
                flagMethodList.add(args);
            }
        }
        ListView list = new DragListView(this);
        list.setAdapter(new SimpleAdapter(
                getBaseContext(), flagMethodList, android.R.layout.simple_list_item_2,
                new String[]{"title", "name"},
                new int[]{android.R.id.text1, android.R.id.text2}));
        list.setOnItemClickListener((parent, view, position, id) -> {
            Map<String, String> flagMethod = (Map<String, String>) parent.getAdapter().getItem(position);
            try {
                Method method = AbsMethodListActivity.this.getClass().getDeclaredMethod(flagMethod.get("name"));
                method.setAccessible(true);
                method.invoke(AbsMethodListActivity.this);
                AbsMethodListActivity.this.onItemClick(parent, view, position, id);
            } catch (Exception e) {
                logout(e.getClass().getSimpleName() + ": " + e.getMessage());
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
        String title() default "";
    }

}
