package org.pinwheel.agility.util;

import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * class name：SIMCardHelper<BR>
 * class description：读取Sim卡信息<BR>
 * PS： 必须在加入各种权限 <BR>
 *
 * @author dnwang
 */
public final class SIMCardHelper {

    private SIMCardHelper() {
    }

    public static String getNativePhoneNumber(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (!telephonyManager.hasIccCard()) {
            return "";
        } else {
            return telephonyManager.getLine1Number();
        }
    }

    public static String getProvidersName(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (!telephonyManager.hasIccCard()) {
            return "";
        }
        String ProvidersName = null;
        String IMSI = telephonyManager.getSubscriberId();
        // IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
        if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
            ProvidersName = "中国移动";
        } else if (IMSI.startsWith("46001")) {
            ProvidersName = "中国联通";
        } else if (IMSI.startsWith("46003")) {
            ProvidersName = "中国电信";
        }
        return ProvidersName;
    }

    public static String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (!telephonyManager.hasIccCard()) {
            return "";
        }
        return telephonyManager.getDeviceId();
    }

}