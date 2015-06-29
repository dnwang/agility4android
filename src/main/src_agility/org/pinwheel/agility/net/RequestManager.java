package org.pinwheel.agility.net;

import android.content.Context;
import android.util.Log;
import com.android.volley.*;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;
import org.pinwheel.agility.net.parser.IResponseParser;
import org.pinwheel.agility.net.parser.StringParser;

import java.util.Map;

public final class RequestManager {
    public static boolean debug = false;

    private static final String TAG = RequestManager.class.getSimpleName();
    private static RequestManager instance = null;

    private RequestQueue mQueue;

    public static synchronized RequestManager init(Context context) {
        if (instance == null) {
            instance = new RequestManager(context);
        }
        return instance;
    }

    public static synchronized void release() {
        if (instance != null) {
            instance.mQueue.cancelAll(new RequestQueue.RequestFilter() {
                @Override
                public boolean apply(com.android.volley.Request request) {
                    // cancel all request
                    return true;
                }
            });
            instance.mQueue = null;
            instance = null;
        }
    }

    public static void cancel(final String... tags) {
        if (instance == null || tags.length == 0) {
            return;
        }
        instance.mQueue.cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(com.android.volley.Request request) {
                for (String tag : tags) {
                    if (request.getTag().equals(tag)) {
                        return true;
                    }
                }
                return false;
            }
        });
    }

    public static void doGet(Request adapter, OnRequestListener<String> listener) {
        if (instance != null) {
            instance.doRequest(com.android.volley.Request.Method.GET, adapter, new StringParser(), listener);
        }
    }

    public static <T> void doGet(Request adapter, IResponseParser<T> parser, OnRequestListener<T> listener) {
        if (instance != null) {
            instance.doRequest(com.android.volley.Request.Method.GET, adapter, parser, listener);
        }
    }

    public static void doPost(Request adapter, OnRequestListener<String> listener) {
        if (instance != null) {
            instance.doRequest(com.android.volley.Request.Method.POST, adapter, new StringParser(), listener);
        }
    }

    public static <T> void doPost(Request adapter, IResponseParser<T> parser, OnRequestListener<T> listener) {
        if (instance != null) {
            instance.doRequest(com.android.volley.Request.Method.POST, adapter, parser, listener);
        }
    }

    public static void doPut(Request adapter, OnRequestListener<String> listener) {
        if (instance != null) {
            instance.doRequest(com.android.volley.Request.Method.PUT, adapter, new StringParser(), listener);
        }
    }

    public static <T> void doPut(Request adapter, IResponseParser<T> parser, OnRequestListener<T> listener) {
        if (instance != null) {
            instance.doRequest(com.android.volley.Request.Method.PUT, adapter, parser, listener);
        }
    }

    public static void doDelete(Request adapter, OnRequestListener<String> listener) {
        if (instance != null) {
            instance.doRequest(com.android.volley.Request.Method.DELETE, adapter, new StringParser(), listener);
        }
    }

    public static <T> void doDelete(Request adapter, IResponseParser<T> parser, OnRequestListener<T> listener) {
        if (instance != null) {
            instance.doRequest(com.android.volley.Request.Method.DELETE, adapter, parser, listener);
        }
    }


    private RequestManager(Context context) {
        this.mQueue = Volley.newRequestQueue(context);
    }

    private <T> void doRequest(int method, Request adapter, IResponseParser<T> parser, OnRequestListener<T> listener) {
        if (mQueue == null || adapter == null || parser == null) {
            return;
        }
        RequestWrapper<T> request = new RequestWrapper<T>(method, adapter, parser, listener);

        // 使用 adapter tag 标记一条 request
        final String tag = adapter.getTag();
        request.setTag(tag);
        // END

        // 设置请求次数，每次超时时长
        request.setRetryPolicy(new DefaultRetryPolicy(adapter.getTimeout(), adapter.getNumOfRetries(), 1.0f));
        // END

        // 清除队列中 相同url的请求
        if (adapter.isKeepSingle()) {
            mQueue.cancelAll(new RequestQueue.RequestFilter() {
                @Override
                public boolean apply(com.android.volley.Request request) {
                    return request.getTag().equals(tag);
                }
            });
        }
        // END

        mQueue.add(request);

        if (debug) {
            Log.d(TAG, "Request Tag:" + request.getTag().toString());
            Log.d(TAG, "Request Url:" + request.getUrl());
        }
    }

    private static class RequestWrapper<T> extends com.android.volley.Request<T> {

        private IResponseParser<T> mParser;
        private OnRequestListener<T> mListener;
        private Request mAdapter;

        public RequestWrapper(int method, Request adapter, IResponseParser<T> parser, OnRequestListener<T> listener) {
            super(method, adapter.getUrlByMethod(method), null);
            this.mAdapter = adapter;
            this.mParser = parser;
            this.mListener = listener;
        }

        @Override
        protected Response<T> parseNetworkResponse(NetworkResponse response) {
            try {
                if (mListener != null && mListener instanceof OnHandleResponseAdapter) {
                    ((OnHandleResponseAdapter) mListener).onHandleResponse(response);
                }
                mParser.parse(response.data);
                return Response.success(mParser.getResult(), HttpHeaderParser.parseCacheHeaders(response));
            } catch (Exception e) {
                if (RequestManager.debug) {
                    Log.e(TAG, e.getMessage());
                }
                VolleyError error = new VolleyError(e.getMessage());
                error.setStackTrace(e.getStackTrace());
                return Response.error(error);
            }
        }

        @Override
        protected void deliverResponse(T response) {
            if (mListener != null) {
                mListener.onSuccess(response);
            }
        }

        @Override
        public void deliverError(VolleyError error) {
            super.deliverError(error);
            if (mListener != null) {
                mListener.onError(error);
            }
        }

        @Override
        public byte[] getBody() throws AuthFailureError {
            if (mAdapter.getBody() != null) {
                return mAdapter.getBody();
            } else {
                return super.getBody();
            }
        }

        @Override
        protected Map<String, String> getParams() throws AuthFailureError {
            return mAdapter.getParams();
//            Map<String, String> params = mAdapter.getParams();
//            if (params != null && !params.isEmpty()) {
//                Map<String, String> superParams = super.getParams();
//                if (superParams != null) {
//                    superParams.putAll(params);
//                    return superParams;
//                } else {
//                    return params;
//                }
//            } else {
//                return super.getParams();
//            }
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            return mAdapter.getHeaders();
//            Map<String, String> headers = mAdapter.getHeaders();
//            if (headers != null && !headers.isEmpty()) {
//                Map<String, String> superHeaders = super.getHeaders();
//                if (superHeaders != null) {
//                    superHeaders.putAll(headers);
//                    return superHeaders;
//                } else {
//                    return headers;
//                }
//            } else {
//                return super.getHeaders();
//            }
        }

        @Override
        protected String getParamsEncoding() {
            return super.getParamsEncoding();
        }

    }

    public static interface OnRequestListener<T> {
        public void onError(Exception e);

        public void onSuccess(T obj);
    }

    public static abstract class OnHandleTagRequestAdapter<T> implements OnRequestListener<T> {
        private Object tag;

        public OnHandleTagRequestAdapter(Object tag) {
            this.tag = tag;
        }

        @Override
        @Deprecated
        public final void onError(Exception e) {
            this.onError(e, tag);
        }

        @Override
        @Deprecated
        public final void onSuccess(T obj) {
            this.onSuccess(obj, tag);
        }

        public abstract void onError(Exception e, Object tag);

        public abstract void onSuccess(T obj, Object tag);

    }

    public static abstract class OnHandleResponseAdapter<T> implements OnRequestListener<T> {
        @Override
        public void onError(Exception e) {
        }

        @Override
        public void onSuccess(T obj) {
        }

        public abstract void onHandleResponse(NetworkResponse response);

    }

}
