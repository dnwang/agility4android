package org.pinwheel.agility.compat;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import org.pinwheel.agility.util.DigestUtils;
import org.pinwheel.agility.util.callback.Action1;

import java.util.ArrayList;
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

    private static final int REQ_CODE = 0xD7;

    private Map<String, Set<Action1<Boolean>>> callbackMap;

    public GrantPermissionsHelper() {
        callbackMap = new ConcurrentHashMap<>();
    }

    public void requestPermissions(Activity activity, Action1<Boolean> callback, String... permissions) {
        if (null == activity) {
            dispatchResult(callback, false);
            return;
        }
        if (null == permissions || permissions.length == 0) {
            dispatchResult(callback, true);
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//Build.VERSION_CODES.M
            final List<String> needGrantList = new ArrayList<>(permissions.length);
            for (String permission : permissions) {
                int result = ContextCompat.checkSelfPermission(activity.getApplicationContext(), permission);
                if (result != PackageManager.PERMISSION_GRANTED) {
                    needGrantList.add(permission);
                }
            }
            if (!needGrantList.isEmpty()) {
                permissions = needGrantList.toArray(new String[needGrantList.size()]);
                final String permissionsKey = getStringArrayMD5(permissions);
                if (containsKey(permissionsKey)) {
                    appendCallback(permissionsKey, callback);
                } else {
                    bindCallback(permissionsKey, callback);
                    ActivityCompat.requestPermissions(activity, permissions, REQ_CODE);
                }
            } else {
                dispatchResult(callback, true);
            }
        } else {
            dispatchResult(callback, true);
        }
    }

    /**
     * Please call this method on {@link android.app.Activity#onRequestPermissionsResult}
     */
    public void onRequestPermissionsResult(int key, String[] permissions, int[] grantResults) {
        if (REQ_CODE != key || null == permissions || null == grantResults) {
            return;
        }
        final String permissionsKey = getStringArrayMD5(permissions);
        Set<Action1<Boolean>> callbackList = callbackMap.get(permissionsKey);
        if (null != callbackList && !callbackList.isEmpty()) {
            int sizeOfGrantPermissions = 0;
            final int size = Math.min(permissions.length, grantResults.length);
            for (int i = 0; i < size; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    sizeOfGrantPermissions++;
                }
            }
            for (Action1<Boolean> callback : callbackList) {
                dispatchResult(callback, sizeOfGrantPermissions == permissions.length);
            }
        }
        removeKey(permissionsKey);
    }

    private void bindCallback(String key, Action1<Boolean> callback) {
        if (!TextUtils.isEmpty(key)) {
            Set<Action1<Boolean>> runnerList = new CopyOnWriteArraySet<>();
            runnerList.add(callback);
            callbackMap.put(key, runnerList);
        }
    }

    private void appendCallback(String key, Action1<Boolean> callback) {
        if (!TextUtils.isEmpty(key) && callbackMap.containsKey(key)) {
            callbackMap.get(key).add(callback);
        }
    }

    private boolean containsKey(String key) {
        return !TextUtils.isEmpty(key) && callbackMap.containsKey(key);
    }

    private void removeKey(String key) {
        callbackMap.remove(key);
    }

    private static String getStringArrayMD5(String[] array) {
        if (null == array || array.length == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (String s : array) {
            sb.append(s);
        }
        return DigestUtils.md5(sb.toString());
    }

    private void dispatchResult(Action1<Boolean> callback, boolean isSuccess) {
        if (null != callback) {
            callback.call(isSuccess);
        }
    }

}
