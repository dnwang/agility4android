package org.pinwheel.agility.compat;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Copyright (C), 2016 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 * @version 7/27/16,20:52
 * @see
 */
public final class GrantPermissionsHelper {

    private Map<Integer, String[]> permissionsMap;
    private Map<Integer, Set<Runnable>> runnerMap;

    public GrantPermissionsHelper() {
        permissionsMap = new ConcurrentHashMap<>();
        runnerMap = new ConcurrentHashMap<>();
    }

    public void requestPermissions(Activity activity, Runnable runnable, String... permissions) {
        if (null == activity || permissions.length == 0) {
            if (null != runnable) {
                runnable.run();
            }
            return;
        }
        if (Build.VERSION.SDK_INT >= 23) {//Build.VERSION_CODES.M
            List<String> needGrantList = new ArrayList<>(permissions.length);
            for (String permission : permissions) {
                int result = ContextCompat.checkSelfPermission(activity.getApplicationContext(), permission);
                if (result != PackageManager.PERMISSION_GRANTED) {
                    needGrantList.add(permission);
                }
            }
            if (!needGrantList.isEmpty()) {
                final int key = Arrays.hashCode(permissions);
                if (containsKey(key)) {
                    appendRunner(key, runnable);
                } else {
                    bindRunner(key, permissions, runnable);
                    ActivityCompat.requestPermissions(activity, needGrantList.toArray(new String[needGrantList.size()]), key);
                }
            } else {
                if (null != runnable) {
                    runnable.run();
                }
            }
        } else {
            if (null != runnable) {
                runnable.run();
            }
        }
    }

    /**
     * Please call this method on {@link android.app.Activity.onRequestPermissionsResult}
     */
    public void onRequestPermissionsResult(int key, String[] permissions, int[] grantResults) {
        if (null == permissions || null == grantResults || !containsKey(key)) {
            return;
        }
        Set<Runnable> runnerList = runnerMap.get(key);
        if (null != runnerList && !runnerList.isEmpty()) {
            final String[] rawPermissionsRequest = permissionsMap.get(key);
            if (rawPermissionsRequest.length == permissions.length) {
                int sizeOfGrantPermissions = 0;
                final int size = Math.min(permissions.length, grantResults.length);
                for (int i = 0; i < size; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        sizeOfGrantPermissions++;
                    }
                }
                if (sizeOfGrantPermissions == permissions.length) {
                    for (Runnable runner : runnerList) {
                        runner.run();
                    }
                }
            }
        }
        removeKey(key);
    }

    private void bindRunner(int key, String[] permissions, Runnable runnable) {
        permissionsMap.put(key, permissions);
        Set<Runnable> runnerList = new CopyOnWriteArraySet<>();
        runnerList.add(runnable);
        runnerMap.put(key, runnerList);
    }

    private void appendRunner(int key, Runnable runnable) {
        runnerMap.get(key).add(runnable);
    }

    private boolean containsKey(int key) {
        return permissionsMap.containsKey(key) && runnerMap.containsKey(key);
    }

    private void removeKey(int key) {
        permissionsMap.remove(key);
        runnerMap.remove(key);
    }

}
