package org.pinwheel.demo4agility.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.pinwheel.agility.adapter.SimpleArrayAdapter;
import org.pinwheel.agility.util.BaseUtils;
import org.pinwheel.agility.view.drag.DragListView;

public class LauncherActivity extends AbsTesterActivity implements ListView.OnItemClickListener {

    @Override
    protected void beforeInitView() {

    }

    @Override
    protected View getContentView() {
        ListView list = new DragListView(this);
        LauncherAdapter adapter = new LauncherAdapter(this);
        list.setAdapter(adapter);
        list.setOnItemClickListener(this);
        return list;
    }

    @Override
    protected void afterInitView() {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
            ActivityInfo activityInfo = (ActivityInfo) parent.getAdapter().getItem(position);
            Intent intent = new Intent(getApplicationContext(), Class.forName(activityInfo.name));
            startActivity(intent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static class LauncherAdapter extends SimpleArrayAdapter<ActivityInfo> {
        private LayoutInflater inflater;

        public LauncherAdapter(Context context) {
            inflater = LayoutInflater.from(context);

            try {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 1);
                ActivityInfo[] activities = packageInfo.activities;
                for (ActivityInfo activityInfo : activities) {
                    if (activityInfo.name.contains(LauncherActivity.class.getName())) {
                        continue;
                    }
                    addItem(activityInfo);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ActivityInfo activityInfo = getItem(position);
            if (convertView == null) {
                convertView = inflater.inflate(android.R.layout.simple_list_item_2, null);
            }
            TextView text1 = BaseUtils.getViewByHolder(convertView, android.R.id.text1);
            TextView text2 = BaseUtils.getViewByHolder(convertView, android.R.id.text2);
            CharSequence label = activityInfo.nonLocalizedLabel;
            text1.setText(activityInfo.name.substring(activityInfo.name.lastIndexOf(".") + 1));
            text2.setText(label);
            return convertView;
        }
    }

}