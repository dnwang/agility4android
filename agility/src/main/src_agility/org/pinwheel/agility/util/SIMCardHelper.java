package org.pinwheel.agility.util;

import android.content.Context;
import android.telephony.TelephonyManager;

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

    public static String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (!telephonyManager.hasIccCard()) {
            return "";
        }
        return telephonyManager.getDeviceId();
    }

}