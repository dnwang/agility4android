package org.pinwheel.agility.util.ex;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;

import java.io.File;

/**
 * 软件工具类，获取软件的各种属性
 */
public class AppUtils {
    private Context context;

    public AppUtils(Context context) {
        this.context = context;
    }

    /**
     * 获取当前应用程序的版本号
     *
     * @return
     */
    public String getVersionName() {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            String version = packInfo.versionName;
            return version;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取当前正在运行的Activity
     *
     * @return <uses-permission android:name="android.permission.GET_TASKS"/>
     */
    public String getActivityName() {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        RunningTaskInfo info = manager.getRunningTasks(1).get(0);
        String shortClassName = info.topActivity.getShortClassName();
        System.out.println("shortClassName=" + shortClassName);
        return shortClassName;
    }

    /**
     * 安装指定文件路径的apk文件
     *
     * @param path
     */
    public void installApk(String path) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
        context.startActivity(intent); // 安装新版本
    }

    /**
     * 创建桌面快捷方式
     *
     * @param resId 应用图标
     *              <uses-permission android:name="com.android.launcher.permission.INSTALL_SHORTCUT" />
     */
    public void createShortcut(int resId, int name_resId) {
        Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, context.getString(name_resId));
        shortcut.putExtra("duplicate", false);
        ComponentName comp = new ComponentName(context.getPackageName(), "." + ((Activity) context).getLocalClassName());
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(Intent.ACTION_MAIN).setComponent(comp));
        ShortcutIconResource iconRes = ShortcutIconResource.fromContext(context, resId);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
        context.sendBroadcast(shortcut);
    }
}
