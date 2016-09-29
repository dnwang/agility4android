package org.pinwheel.agility.net.parser;

import android.os.Handler;
import android.os.Looper;

import org.pinwheel.agility.util.callback.Action2;

import java.io.InputStream;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public abstract class DataParserAdapter<T> implements IDataParser<T> {

    private OnParseListener listener;

    @Override
    public void parse(byte[] dataBytes) throws Exception {

    }

    @Override
    public void parse(InputStream inStream) throws Exception {

    }

    @Override
    public T getResult() {
        return null;
    }

    public void release() {
        listener = null;
    }

    public void setOnParseListener(OnParseListener listener) {
        this.listener = listener;
    }

    public void setOnParseListener(Action2<Long, Long> action) {
        setOnParseListener(new ActionWrapperParseListener(action));
    }

    private Handler uiHandler = new Handler(Looper.getMainLooper());

    protected final void dispatchProgress(final long progress, final long total) {
        if (listener == null) {
            return;
        }
        if (Looper.myLooper() == Looper.getMainLooper()) {
            listener.onProgress(progress, total);
        } else {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onProgress(progress, total);
                }
            });
        }
    }

    protected final void dispatchComplete() {
        if (listener == null) {
            return;
        }
        if (Looper.myLooper() == Looper.getMainLooper()) {
            listener.onComplete();
        } else {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onComplete();
                }
            });
        }
    }

    /**
     * Parser callback
     */
    public interface OnParseListener {

        void onProgress(long progress, long total);

        void onComplete();

    }

    public static class ActionWrapperParseListener implements OnParseListener {

        protected Action2<Long, Long> action;

        public ActionWrapperParseListener(Action2<Long, Long> action) {
            this.action = action;
        }

        @Override
        public void onProgress(long progress, long total) {
            if (null != action) {
                action.call(progress, total);
            }
        }

        @Override
        public void onComplete() {

        }
    }

}
