package org.pinwheel.agility.net;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public final class HttpClientAgentHelper {

    private HttpClientAgentHelper() {

    }

    public static boolean isImportOkHttp() {
        try {
            Class cls = Class.forName("com.squareup.okhttp.OkHttpClient");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean isImportVolley() {
        try {
            Class cls = Class.forName("com.android.volley.RequestQueue");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

}
