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
public interface HttpClientAgent {

    public static boolean debug = false;

    public void enqueue(Request request);

    public void parallelExecute(Request... requests);

    public void cancel(Object... tags);

    public void release();

    /**
     * Request callback
     *
     * @param <T>
     */
    abstract class OnRequestAdapter<T> {

        public boolean onRequest(Request request) {
            return false;
        }

        public boolean onResponse(Object args) {
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
    abstract class OnRequestHandleTagAdapter<T> extends OnRequestAdapter<T> {

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
    abstract class OnRequestWrapper<T> extends OnRequestAdapter<T> {

        private OnRequestAdapter adapter;

        public OnRequestWrapper(OnRequestAdapter adapter) {
            this.adapter = adapter;
        }

        @Override
        public boolean onRequest(Request request) {
            if (adapter != null) {
                return adapter.onRequest(request);
            }
            return super.onRequest(request);
        }

        @Override
        public boolean onResponse(Object args) {
            if (adapter != null) {
                return adapter.onResponse(args);
            }
            return super.onResponse(args);
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

        public abstract void onDeliverComplete();
    }

}
