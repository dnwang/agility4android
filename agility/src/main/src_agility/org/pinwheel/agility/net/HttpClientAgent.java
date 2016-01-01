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

        protected final void dispatchOnError(final Exception e) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    onDeliverError(e);
                }
            });
        }

        protected final void dispatchOnSuccess(final Object result) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (result == null) {
                        onDeliverSuccess(null);
                    } else {
                        try {
                            T obj = (T) result;
                            onDeliverSuccess(obj);
                        } catch (ClassCastException e) {
                            onDeliverError(e);
                        }
                    }
                }
            });
        }
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

    /**
     * Request callback wrapper
     *
     * @param <T>
     */
    public static abstract class OnRequestWrapper<T> extends OnRequestAdapter<T> {

        private OnRequestAdapter adapter;

        public OnRequestWrapper(OnRequestAdapter adapter) {
            this.adapter = adapter;
        }

        @Override
        public boolean onRequestPrepare(Request request) {
            if (adapter != null) {
                return adapter.onRequestPrepare(request);
            }
            return super.onRequestPrepare(request);
        }

        @Override
        public boolean onRequestResponse(Object args) {
            if (adapter != null) {
                return adapter.onRequestResponse(args);
            }
            return super.onRequestResponse(args);
        }

        @Override
        public void onDeliverSuccess(T obj) {
            if (adapter != null) {
                adapter.onDeliverSuccess(obj);
            }
            onDeliverComplete();
        }

        @Override
        public void onDeliverError(Exception e) {
            if (adapter != null) {
                adapter.onDeliverError(e);
            }
            onDeliverComplete();
        }

        public void onDeliverComplete() {
            // TODO: 11/3/15
        }
    }

}
