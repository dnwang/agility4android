package org.pinwheel.agility.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public final class BaseUtils {
    private static final String TAG = BaseUtils.class.getSimpleName();

    private BaseUtils() {

    }

    @Deprecated
    public static boolean isWifi(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    public static String getVersionName(Context context) {
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int getVersionCode(Context context) {
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static PackageInfo getAPKInfo(Context c, File apk) {
        if (!apk.exists())
            return null;
        String archiveFilePath = apk.getAbsolutePath();
        PackageManager pm = c.getPackageManager();
        //PackageManager.GET_ACTIVITIES = 1
        PackageInfo info = pm.getPackageArchiveInfo(archiveFilePath, 1);
        return info;
    }

    public static Date dateFormat(String arcTime, String format) {
        SimpleDateFormat formatSrc = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = formatSrc.parse(arcTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String dateFormatToString(Date date, String format) {
        SimpleDateFormat formatSrc = new SimpleDateFormat(format);
        return formatSrc.format(date);
    }

    public static String longSizeToStr(long contentLength) {
        float length = contentLength;
        String strLen;
        if (length >= 1024 * 1024 * 1024) {
            length /= 1024 * 1024 * 1024;
            String s = String.valueOf(length);
            strLen = s.substring(0, s.length() > 4 ? 4 : s.length()) + "GB";
            while (strLen.length() < 6) {
                strLen = strLen.replace("GB", "0GB");
            }
        } else if (length >= 1024 * 1024) {
            length /= 1024 * 1024;
            String s = String.valueOf(length);
            strLen = s.substring(0, s.length() > 4 ? 4 : s.length()) + "MB";
            while (strLen.length() < 6) {
                strLen = strLen.replace("MB", "0MB");
            }
        } else if (length >= 1024) {
            length /= 1024;
            String s = String.valueOf(length);
            strLen = s.substring(0, s.length() > 4 ? 4 : s.length()) + "KB";
            while (strLen.length() < 6) {
                strLen = strLen.replace("KB", "0KB");
            }
        } else {
            strLen = (int) (length) + "B";
        }
        for (int i = 0; i < strLen.length(); i++) {
            if (strLen.charAt(i) == '.' && i == strLen.length() - 3) {
                strLen = strLen.substring(0, i) + strLen.substring(i + 1);
                break;
            }
        }
        return strLen;
    }

    public static boolean isChinese(char a) {
        int v = (int) a;
        return (v >= 0x4E00 && v <= 0x9FFF);
    }

    public static String getAvailMemory(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        System.out.println("availMem;" + mi.availMem);
        return Formatter.formatFileSize(context, mi.availMem);
    }

    public static String getTotalMemory(Context context) {
        String str1 = "/proc/meminfo";
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;
        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
//            for (String num : arrayOfString) {
//                Log.i(str2, num + "\t");
//            }
            initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;
            localBufferedReader.close();
        } catch (IOException e) {
        }
        return Formatter.formatFileSize(context, initial_memory);
    }

    public static boolean externalMemoryAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static String getAvailableInternalMemorySize(Context context) {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return Formatter.formatFileSize(context, availableBlocks * blockSize);
    }

    public static String getTotalInternalMemorySize(Context context) {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return Formatter.formatFileSize(context, totalBlocks * blockSize);
    }

    public static String getAvailableExternalMemorySize(Context context) {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return Formatter.formatFileSize(context, availableBlocks * blockSize);
        } else {
            return "ERROR";
        }
    }

    public static String getTotalExternalMemorySize(Context context) {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return Formatter.formatFileSize(context, totalBlocks * blockSize);
        } else {
            return "ERROR";
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends View> T getViewByHolder(View view, int id) {
        SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
        if (viewHolder == null) {
            viewHolder = new SparseArray<View>();
            view.setTag(viewHolder);
        }
        View childView = viewHolder.get(id);
        if (childView == null) {
            childView = view.findViewById(id);
            viewHolder.put(id, childView);
        }
        return (T) childView;
    }

    public static int string2Int(String string, int defaultValue) {
        int result = defaultValue;
        try {
            result = Integer.parseInt(string);
        } catch (Exception e) {
        }
        return result;
    }

    public static <T> T getMapValue(Map<? extends Object, T> args, Object key, T defaultValue) {
        if (args.containsKey(key)) {
            return args.get(key);
        } else {
            return defaultValue;
        }
    }

    public static int getResourceID(String pkg_name, String cls_name, String id_name) {
        int id = 0;
        try {
            Class<?> cls = Class.forName(pkg_name + ".R$" + cls_name);
            id = cls.getField(id_name).getInt(cls);
        } catch (Exception e) {
            Log.e(TAG, "getResourceID() --> Can't find id : " + pkg_name + ".R." + cls_name + "." + id_name);
        }
        return id;
    }

    public static String getMacAddress(Context context) {
        try {
            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            String mac = info.getMacAddress();
            return mac == null ? "" : mac;
        } catch (Exception e) {
            return "";
        }
    }

    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

}