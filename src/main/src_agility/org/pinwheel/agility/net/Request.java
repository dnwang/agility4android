package org.pinwheel.agility.net;

import android.net.Uri;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class Request {

    private String mBaseUrl;
    private Map<String, String> mParams;
    private Map<String, String> mHeaders;

    private int numOfRetries, timeOut;
    private boolean isKeepSingle;
    private String mTag; //用于Volley标记请求,取消时根据此Tag

    public Request(String base_url) {
        mBaseUrl = TextUtils.isEmpty(base_url) ? "http://" : base_url;
        mParams = new HashMap<String, String>(0);
        mHeaders = new HashMap<String, String>(0);
        mTag = mBaseUrl;
        numOfRetries = 0;
        timeOut = 24 * 3600 * 1000;
        isKeepSingle = false;
    }

    public Map<String, String> getParams() {
        return mParams;
    }

    public Map<String, String> getHeaders() {
        return mHeaders;
    }

    public String getUrlByMethod(int method) {
        switch (method) {
            case com.android.volley.Request.Method.GET:
                return getUrlWithParams();
            case com.android.volley.Request.Method.POST:
                return mBaseUrl;
            case com.android.volley.Request.Method.PUT:
                return mBaseUrl;
            case com.android.volley.Request.Method.DELETE:
                return mBaseUrl;
            default:
                return mBaseUrl;
        }
    }

    private String getUrlWithParams() {
        if (mParams == null || mParams.isEmpty()) {
            return mBaseUrl;
        }
        StringBuilder url = new StringBuilder(mBaseUrl);
        if (!mBaseUrl.contains("?")) {
            url.append("?");
        }
        Set<Map.Entry<String, String>> set = mParams.entrySet();
        for (Map.Entry<String, String> entry : set) {
            url.append("&").append(entry.getKey()).append("=").append(entry.getValue());
        }
        return url.toString().replace("?&", "?");
    }

    public Request addParam(String key, Object value) {
        if (TextUtils.isEmpty(key) || value == null) {
            return this;
        }
        mParams.put(key, value.toString());
        return this;
    }

    public Request addEncodeParam(String key, Object value) {
        if (TextUtils.isEmpty(key) || value == null) {
            return this;
        }
        mParams.put(key, Uri.encode(value.toString()));
        return this;
    }

    public Request addHeader(String key, Object value) {
        if (TextUtils.isEmpty(key) || value == null) {
            return this;
        }
        mHeaders.put(key, value.toString());
        return this;
    }

    public Request setTimeOut(int time_out, int num_of_retries) {
        if (time_out > 0) {
            timeOut = time_out;
        }
        if (num_of_retries > 0) {
            numOfRetries = num_of_retries;
        }
        return this;
    }

    public int getTimeout() {
        return timeOut;
    }

    public int getNumOfRetries() {
        return numOfRetries;
    }

    public Request setKeepSingle(boolean is) {
        this.isKeepSingle = is;
        return this;
    }

    public boolean isKeepSingle() {
        return isKeepSingle;
    }

    public String getTag() {
        return mTag;
    }

    public Request setTag(String tag) {
        if (TextUtils.isEmpty(tag)) {
            return this;
        }
        this.mTag = tag;
        return this;
    }

    public void cancelSelf() {
        RequestManager.cancel(getTag());
    }

}
