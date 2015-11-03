package org.pinwheel.agility.net;

import android.text.TextUtils;

import org.pinwheel.agility.net.parser.IDataParser;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public final class Request implements Serializable {

    private String baseUrl;
    private String method;
    private byte[] body;
    private Map<String, String> params;
    private Map<String, String> headers;
    private int numOfRetries, timeOut;
    private boolean isKeepSingle;
    private Object tag;
    private IDataParser responseParser;
    private HttpClientAgent.OnRequestAdapter requestListener;

//    private Request(String method, String url, int timeOut) {
//        this.baseUrl = TextUtils.isEmpty(url) ? "http://" : url;
//        this.method = TextUtils.isEmpty(method) ? "GET" : method;
//        this.timeOut = timeOut;
//        body = null;
//        params = new HashMap<String, String>(0);
//        headers = new HashMap<String, String>(0);
//        numOfRetries = 0;
//        isKeepSingle = false;
//        tag = baseUrl;
//    }

    private Request(Builder builder) {
        baseUrl = builder.url;
        method = builder.method;
        headers = builder.headers;
        body = builder.body;
        params = builder.params;
        timeOut = builder.timeOut;
        numOfRetries = builder.numOfRetries;
        isKeepSingle = builder.isKeepSingle;
        tag = builder.tag == null ? baseUrl : builder.tag;// use baseUrl as default tag
    }

    /**
     * Get http url by given method
     *
     * @return full url
     */
    public String getUrlByMethod() {
        if ("GET".equalsIgnoreCase(method)) {
            if (params == null || params.isEmpty()) {
                return baseUrl;
            }
            StringBuilder url = new StringBuilder(baseUrl);
            if (!baseUrl.contains("?")) {
                url.append("?");
            }
            Set<Map.Entry<String, String>> set = params.entrySet();
            for (Map.Entry<String, String> entry : set) {
                url.append("&").append(entry.getKey()).append("=").append(entry.getValue());
            }
            return url.toString().replace("?&", "?");
        } else {
            return baseUrl;
        }
    }

    public String getMethod() {
        return method;
    }

    public byte[] getBody() {
        return body;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public int getTimeout() {
        return timeOut;
    }

    public int getNumOfRetries() {
        return numOfRetries;
    }

    public boolean isKeepSingle() {
        return isKeepSingle;
    }

    public Object getTag() {
        return tag;
    }

    public IDataParser getResponseParser() {
        return responseParser;
    }

    public HttpClientAgent.OnRequestAdapter getRequestListener() {
        return requestListener;
    }

    public void setOnRequestListener(HttpClientAgent.OnRequestAdapter listener) {
        this.requestListener = listener;
    }

    public void setResponseParser(IDataParser parser) {
        this.responseParser = parser;
    }

    public <T> void setResponseParser(IDataParser<T> parser, HttpClientAgent.OnRequestAdapter<T> listener) {
        setResponseParser(parser);
        setOnRequestListener(listener);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (tag == null || o == null || getClass() != o.getClass()) {
            return false;
        }
        Request request = (Request) o;
        return tag.equals(request.tag);
    }

    @Override
    public int hashCode() {
        return tag == null ? 0 : tag.hashCode();
    }

    /**
     * Request builder
     */
    public static class Builder {

        private String url;
        private String method;
        private byte[] body;
        private Map<String, String> params;
        private Map<String, String> headers;

        private int numOfRetries, timeOut;
        private boolean isKeepSingle;
        private Object tag;

        public Builder(Request request) {
            this();
            if (request != null) {
                url = request.baseUrl;
                method = request.method;
                body = request.body;
                params.putAll(request.params);
                headers.putAll(request.headers);
                numOfRetries = request.numOfRetries;
                timeOut = request.timeOut;
                isKeepSingle = request.isKeepSingle;
                tag = request.tag;
            }
        }

        public Builder() {
            url = "http://";
            method = "GET";
            body = null;
            params = new HashMap<String, String>(0);
            headers = new HashMap<String, String>(0);
            numOfRetries = 0;
            timeOut = 10 * 60;
            isKeepSingle = false;
            tag = null;
        }

        public Builder url(String url) {
            this.url = TextUtils.isEmpty(url) ? "http://" : url;
            return this;
        }

        public Builder method(String method) {
            this.method = TextUtils.isEmpty(method) ? "GET" : method;
            return this;
        }

        public Builder addHeader(String key, Object value) {
            if (TextUtils.isEmpty(key)) {
                return this;
            }
            headers.put(key, String.valueOf(value));
            return this;
        }

        public Builder addHeaders(Map<String, String> values) {
            if (values == null || values.isEmpty()) {
                return this;
            }
            headers.putAll(values);
            return this;
        }

        public Builder body(byte[] bytes) {
            this.body = bytes;
            return this;
        }

        public Builder addParam(String key, Object value) {
            if (TextUtils.isEmpty(key)) {
                return this;
            }
            params.put(key, String.valueOf(value));
            return this;
        }

        public Builder addParams(Map<String, String> values) {
            if (values == null || values.isEmpty()) {
                return this;
            }
            params.putAll(values);
            return this;
        }

        public Builder timeOut(int timeOut, int numOfRetries) {
            this.timeOut = Math.max(0, timeOut);
            this.numOfRetries = Math.max(0, numOfRetries);
            return this;
        }

        public Builder tag(Object tag) {
            this.tag = tag;
            return this;
        }

        public Builder keepSingle(boolean isKeySingle) {
            this.isKeepSingle = isKeySingle;
            return this;
        }

        public Request create() {
            return new Request(this);
        }
    }

}
