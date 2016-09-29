package org.pinwheel.agility.net;

import android.os.Handler;
import android.os.Looper;

import org.pinwheel.agility.util.callback.Action3;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public abstract class HttpClientAgent {

    public static boolean isImportOkHttp2() {
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

    private Handler uiHandler = new Handler(Looper.getMainLooper());

    protected final void dispatchError(final RequestAdapter adapter, final Exception e) {
        if (adapter == null) {
            return;
        }
        if (Looper.myLooper() == Looper.getMainLooper()) {
            adapter.onDeliverError(e);
        } else {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    adapter.onDeliverError(e);
                }
            });
        }
    }

    protected final void dispatchSuccess(final RequestAdapter adapter, final Object result) {
        if (adapter == null) {
            return;
        }
        if (Looper.myLooper() == Looper.getMainLooper()) {
            adapter.onDeliverSuccess(result);
        } else {
            uiHandler.post(new Runnable() {
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
    public static abstract class RequestAdapter<T> {

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
    public static abstract class HandleTagRequestAdapter<T> extends RequestAdapter<T> {

        private Object tag;

        public HandleTagRequestAdapter(Object tag) {
            this.tag = tag;
        }

        public final Object getTag() {
            return tag;
        }

    }

    public static class ActionWrapperRequestAdapter<T> extends RequestAdapter<T> {

        private Action3<Boolean, T, Exception> action;

        ActionWrapperRequestAdapter(Action3<Boolean, T, Exception> action) {
            this.action = action;
        }

        @Override
        public void onDeliverSuccess(T obj) {
            if (null != action) {
                action.call(true, obj, null);
            }
        }

        @Override
        public void onDeliverError(Exception e) {
            if (null != action) {
                action.call(false, null, e);
            }
        }
    }

}
