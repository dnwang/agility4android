package org.pinwheel.agility.net;

import android.os.Handler;
import android.os.Looper;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public abstract class HttpClientAgent {

    public static boolean isImportOkHttp() {
        try {
            Class.forName("com.squareup.okhttp.OkHttpClient");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean isImportVolley() {
        try {
            Class.forName("com.android.volley.RequestQueue");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public abstract void enqueue(Request request);

    public abstract void parallelExecute(Request... requests);

    public abstract void cancel(Object... tags);

    public abstract void release();

    protected final void dispatchError(final OnRequestAdapter adapter, final Exception e) {
        if (adapter == null) {
            return;
        }
        if (Looper.myLooper() == Looper.getMainLooper()) {
            adapter.onDeliverError(e);
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    adapter.onDeliverError(e);
                }
            });
        }
    }

    protected final void dispatchSuccess(final OnRequestAdapter adapter, final Object result) {
        if (adapter == null) {
            return;
        }
        if (Looper.myLooper() == Looper.getMainLooper()) {
            adapter.onDeliverSuccess(result);
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    adapter.onDeliverSuccess(result);
                }
            });
        }
    }

    /**
     * Request callback
     *
     * @param <T>
     */
    public static abstract class OnRequestAdapter<T> {

        public boolean onRequestPrepare(Request request) {
            return false;
        }

        public boolean onRequestResponse(Object args) {
            return false;
        }

        public abstract void onDeliverSuccess(T obj);

        public abstract void onDeliverError(Exception e);

    }

    /**
     * Handle Tag
     *
     * @param <T>
     */
    public static abstract class OnRequestHandleTagAdapter<T> extends OnRequestAdapter<T> {

        private Object tag;

        public OnRequestHandleTagAdapter(Object tag) {
            this.tag = tag;
        }

        public final Object getTag() {
            return tag;
        }

    }

}
