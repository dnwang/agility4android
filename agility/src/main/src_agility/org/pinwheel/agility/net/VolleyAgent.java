package org.pinwheel.agility.net;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;

import org.pinwheel.agility.net.parser.IDataParser;

import java.util.Map;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public class VolleyAgent implements HttpClientAgent {
    private static final String TAG = VolleyAgent.class.getSimpleName();

    private RequestQueue queue;

    public VolleyAgent(Context context) {
        queue = Volley.newRequestQueue(context);
    }

    protected final int convertMethod(String methodStr) {
        if ("GET".equalsIgnoreCase(methodStr)) {
            return com.android.volley.Request.Method.GET;
        } else if ("POST".equalsIgnoreCase(methodStr)) {
            return com.android.volley.Request.Method.POST;
        } else if ("DELETE".equalsIgnoreCase(methodStr)) {
            return com.android.volley.Request.Method.DELETE;
        } else if ("PUT".equalsIgnoreCase(methodStr)) {
            return com.android.volley.Request.Method.PUT;
        } else {
            return com.android.volley.Request.Method.GET;
        }
    }

    protected com.android.volley.Request convert(Request request) {
        return new RequestWrapper(convertMethod(request.getMethod()), request);
    }

    @Override
    public void enqueue(Request request) {
        if (queue == null || request == null) {
            if (debug) {
                Log.e(TAG, "queue or request must not null !");
            }
            return;
        }

        OnRequestAdapter listener = request.getRequestListener();
        if (listener != null && listener.onRequest(request)) {
            // no need handle continue
            return;
        }

        com.android.volley.Request volleyRequest = convert(request);
        // cancel tag
        Object tag = request.getTag();
        if (tag != null) {
            if (request.isKeepSingle()) {
                cancel(tag);
            }
        }
        queue.add(volleyRequest);
    }

    @Override
    public void parallelExecute(Request... requests) {
        if (debug) {
            Log.e(TAG, HttpConnectionAgent.class.getSimpleName() + " not support cancel.");
        }
    }

    @Override
    public void cancel(final Object... tags) {
        if (queue == null || tags == null || tags.length == 0) {
            return;
        }
        queue.cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(com.android.volley.Request request) {
                for (Object tag : tags) {
                    if (tag.equals(request.getTag())) {
                        return true;
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void release() {
        queue.cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(com.android.volley.Request request) {
                // cancel all request
                return true;
            }
        });
        queue = null;
    }

    protected static class RequestWrapper<T> extends com.android.volley.Request<T> {
        private Request request;

        public RequestWrapper(int method, Request request) {
            super(method, request.getUrlByMethod(), null);
            setRetryPolicy(new DefaultRetryPolicy(request.getTimeout(), request.getNumOfRetries(), 1.0f));
            Object tag = request.getTag();
            if (tag != null) {
                setTag(tag);
            }
            this.request = request;
        }

        @Override
        protected Response<T> parseNetworkResponse(NetworkResponse response) {
            OnRequestAdapter listener = request.getRequestListener();
            if (listener != null && listener.onResponse(response)) {
                // no need handle continue
                return Response.success(null, HttpHeaderParser.parseCacheHeaders(response));
            }
            try {
                request.getResponseParser().parse(response.data);
                IDataParser<T> parser = request.getResponseParser();
                if (parser == null) {
                    return Response.success(null, HttpHeaderParser.parseCacheHeaders(response));
                } else {
                    parser.parse(response.data);
                    return Response.success(parser.getResult(), HttpHeaderParser.parseCacheHeaders(response));
                }
            } catch (Exception e) {
                if (debug) {
                    Log.e(TAG, e.getMessage());
                }
                VolleyError error = new VolleyError(e.getMessage());
                error.setStackTrace(e.getStackTrace());
                return Response.error(error);
            }
        }

        @Override
        protected void deliverResponse(T response) {
            OnRequestAdapter listener = request.getRequestListener();
            if (listener != null) {
                listener.dispatchOnSuccess(response);
            }
        }

        @Override
        public void deliverError(VolleyError error) {
            super.deliverError(error);
            OnRequestAdapter listener = request.getRequestListener();
            if (listener != null) {
                listener.dispatchOnError(error);
            }
        }

        @Override
        public byte[] getBody() throws AuthFailureError {
            if (request.getBody() != null) {
                return request.getBody();
            } else {
                return super.getBody();
            }
        }

        @Override
        protected Map<String, String> getParams() throws AuthFailureError {
            Map<String, String> params = request.getParams();
            if (params != null && !params.isEmpty()) {
                Map<String, String> superParams = super.getParams();
                if (superParams != null) {
                    superParams.putAll(params);
                    return superParams;
                } else {
                    return params;
                }
            } else {
                return super.getParams();
            }
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> headers = request.getHeaders();
            if (headers != null && !headers.isEmpty()) {
                Map<String, String> superHeaders = super.getHeaders();
                if (superHeaders != null) {
                    superHeaders.putAll(headers);
                    return superHeaders;
                } else {
                    return headers;
                }
            } else {
                return super.getHeaders();
            }
        }

    }

}
