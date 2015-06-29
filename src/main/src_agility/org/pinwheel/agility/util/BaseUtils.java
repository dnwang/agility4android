package org.pinwheel.agility.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public final class BaseUtils {
    private static final String TAG = BaseUtils.class.getSimpleName();

    private BaseUtils() {

    }

    /**
     * 流转字符串
     *
     * @param is
     * @return
     */
    public static String streamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    /**
     * 判断当前网络是否为wifi
     *
     * @param mContext
     * @return
     */
    public static boolean isWifi(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    /**
     * 版本是否匹配
     *
     * @return
     */
    public static Boolean equalsVersionName(Context context, String newVersion) {
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String currentVersion = info.versionName;
            if (currentVersion.equals(newVersion))
                return true;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 版本是否匹配
     *
     * @return
     */
    public static Boolean equalsVersionCode(Context context, int newVersionCode) {
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            int currentVersion = info.versionCode;
            if (currentVersion == newVersionCode) {
                return true;
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get version
     *
     * @param context
     * @return
     */
    public static String getVersion(Context context) {
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get version
     *
     * @param context
     * @return
     */
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

    /**
     * dial phone number
     *
     * @param context
     * @param number
     */
    public static void callNumber(Context context, String number) {
        if (TextUtils.isEmpty(number)) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
        context.startActivity(intent);
    }

    /**
     * Install APK file
     *
     * @param c
     * @param isPrivatePath private path
     * @param apk
     */
    public static void installApk(Context c, boolean isPrivatePath, File apk) {
        if (apk == null || !apk.exists()) {
            return;
        }
        if (isPrivatePath) {
            String cmd = "chmod 777 " + apk.getAbsolutePath();
            try {
                Runtime.getRuntime().exec(cmd);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apk.toString()), "application/vnd.android.package-archive");
        if (c instanceof Activity) {
            c.startActivity(i);
        } else {
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            c.startActivity(i);
        }
    }

    /**
     * is out of bounds
     *
     * @param activity
     * @param event
     * @return
     */
    public static boolean isOutOfBounds(Activity activity, MotionEvent event) {
        final int x = (int) event.getX();
        final int y = (int) event.getY();
        final int slop = ViewConfiguration.get(activity).getScaledWindowTouchSlop();
        final View decorView = activity.getWindow().getDecorView();
        return (x < -slop) || (y < -slop) || (x > (decorView.getWidth() + slop)) || (y > (decorView.getHeight() + slop));
    }

    /**
     * get apk file info
     *
     * @param c
     * @param apk
     * @return
     */
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

    public static int computeTimeLengthInMinute(String start, String end) {
        int startInt = getTimeInMinute(start);
        int endInt = getTimeInMinute(end);
        int length = endInt - startInt;
        final int DAY_IN_MINUTE = 24 * 60;
        if (endInt < startInt) {
            length = DAY_IN_MINUTE - (startInt - endInt);
        }
        return Math.max(0, length);
    }

    public static int getTimeInMinute(String ts) {
        String[] strings = ts.split(":");
        int time = 0;
        for (int i = 0; i < strings.length; i++) {
            int n = Integer.parseInt(strings[i]);
            time += n * Math.pow(60, strings.length - i - 1);
        }
        return time;
    }

    public static int getTimeInSecond(String time) {
        String[] strings = time.split(":");
        int[] ints = new int[strings.length];
        for (int i = 0; i < ints.length; i++) {
            ints[i] = Integer.parseInt(strings[i]);
        }
        return ints[0] * 60 * 60 + ints[1] * 60 + ints[2];
    }

    public static int computeTimeLag(String start, String end) {
        int timeLag = getTimeInSecond(end) - getTimeInSecond(start);
        if (timeLag < 0) {
            timeLag = getTimeInSecond("24:00:00") + timeLag;
        }
        return timeLag;
    }

    public static int getTimePercent(String startTime, String endTime) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);
        String curTime = hour + ":" + minutes + ":" + seconds;
        float programTimeLength = BaseUtils.computeTimeLag(startTime, endTime);
        float playedTimeLength = BaseUtils.computeTimeLag(startTime, curTime);
        int progress = (int) ((playedTimeLength / programTimeLength) * 100);
        return progress;
    }

    public static int getCurTimeInSecond() {
        return getTimeInSecond(getCurrentTime("HH:mm:ss"));
    }

    public static String getCurrentTime(String format) {
        return new SimpleDateFormat(format).format(Calendar.getInstance().getTime());
    }

    public static String millisecToYMD(long millis) {
//        if (milli != null && milli.length() > 13) {
//            milli = milli.substring(0, 13);
//        }
        Calendar calendar = new GregorianCalendar();
        Date date = calendar.getTime();
        calendar.setTimeInMillis(millis);
        int day = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH) + 1; // time, month 1~12
        int year = calendar.get(Calendar.YEAR);
        if (month < 10) {
            return year + ":0" + month;
        }
        return year + ":" + month + ":" + day;
    }

    /**
     * @param contentLength
     * @return
     */
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

    /**
     * 判断一个字符是否是汉字
     */
    public static boolean isChinese(char a) {
        int v = (int) a;
        return (v >= 0x4E00 && v <= 0x9FFF);
    }

    /**
     * long值转换成ip地址 方法表述
     */
    public static String long2Ip(long ipInt) {
        StringBuilder sb = new StringBuilder();
        sb.append(ipInt & 0xFF).append(".");
        sb.append((ipInt >> 8) & 0xFF).append(".");
        sb.append((ipInt >> 16) & 0xFF).append(".");
        sb.append((ipInt >> 24) & 0xFF);
        return sb.toString();
    }

    /**
     * 根据子网掩码长度计算子网掩码
     */
    public static String prefixLengthToMaskString(int length) {
        if (length == 0) {
            return "0.0.0.0";
        } else if (length < 0 || length > 32) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        int mask = 0xffffffff << 32 - length;
        builder.append((mask >> 24) & 0xff).append(".");
        builder.append((mask >> 16) & 0xff).append(".");
        builder.append((mask >> 8) & 0xff).append(".");
        builder.append(mask & 0xff);
        return builder.toString();
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

    /**
     * 判断某一个应用程序是不是系统的应用程序，
     * 如果是返回true，否则返回false。
     */
    public static boolean isSystemApp(ApplicationInfo info) {
        //有些系统应用是可以更新的，如果用户自己下载了一个系统的应用来更新了原来的，它还是系统应用，这个就是判断这种情况的
        if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
            return false;
        } else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {//判断是不是系统应用
            return false;
        }
        return true;
    }

    public static boolean isAppRunning(Context context, String pkgName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        boolean isAppRunning = false;
        for (ActivityManager.RunningTaskInfo info : list) {
            if (info.topActivity.getPackageName().equals(pkgName) || info.baseActivity.getPackageName().equals(pkgName)) {
                isAppRunning = true;
                break;
            }
        }
        return isAppRunning;
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

    private static long lastClickTime;

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 800) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    public static int getJsonInt(JSONObject json, String key, int defaultInt) {
        int num;
        try {
            num = json.getInt(key);
        } catch (JSONException e) {
            num = defaultInt;
        }
        return num;
    }

    public static String getJsonString(JSONObject json, String key, String defaultString) {
        String str;
        try {
            str = json.getString(key);
        } catch (JSONException e) {
            str = defaultString;
        }
        return str;
    }

    public static int string2Int(String string, int defaultInt) {
        int num;
        try {
            num = Integer.parseInt(string);
        } catch (Exception e) {
            num = defaultInt;
        }
        return num;
    }

    public static <T> T getMapValue(Map<? extends Object, T> args, Object key, T defaultValue) {
        if (args.containsKey(key)) {
            return args.get(key);
        } else {
            return defaultValue;
        }
    }

    /**
     * @author denan.wang
     * @date 2014/8/26
     * @description 获取系统时区
     */
    public static String getTimeZone() {
        TimeZone tz = TimeZone.getDefault();
        String value = tz.getDisplayName(false, TimeZone.SHORT);
        value = value.substring(value.lastIndexOf("+") + 1);
        value = value.replace("0", "");
        return value;
    }

    /**
     * @author denan.wang
     * @date 2014/9/20
     * @description 毫秒 转为 HH:mm:ss
     */
    public static String generateTime(long time) {
        int totalSeconds = (int) (time / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
//        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds) : String.format("%02d:%02d", minutes, seconds);
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    /**
     * @author denan.wang
     * @date 2014/9/23
     * @description 深拷贝
     */
    public static <T> T deepClone(T obj) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream oo = new ObjectOutputStream(bo);
            oo.writeObject(obj);
            ByteArrayInputStream bi = new ByteArrayInputStream(bo.toByteArray());
            ObjectInputStream oi = new ObjectInputStream(bi);
            return (T) oi.readObject();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @author denan.wang
     * @date 2014/9/26
     * @description 反射获取id
     */
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
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        try {
            return info.getMacAddress();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * @author denan.wang
     * @date 2015/2/5
     * @description 判断网络是否连接
     */
    public boolean isNetworkConnected(Context context) {
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