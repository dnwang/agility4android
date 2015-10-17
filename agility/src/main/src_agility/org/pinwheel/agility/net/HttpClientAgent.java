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

        final void dispatchOnError(final Exception e) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    onDeliverError(e);
                }
            });
        }

        final void dispatchOnSuccess(final Object result) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (result == null) {
                        onDeliverSuccess(null);
                    } else {
                        try {
                            onDeliverSuccess((T) result);
                        } catch (ClassCastException e) {
                            onDeliverError(e);
                        }
                    }
                }
            });
        }
    }

    /**
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

}
