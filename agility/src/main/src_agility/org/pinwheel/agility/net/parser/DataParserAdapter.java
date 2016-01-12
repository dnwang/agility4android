package org.pinwheel.agility.net.parser;

import android.os.Handler;
import android.os.Looper;

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

    protected final void dispatchProgress(final long progress, final long total) {
        if (listener == null) {
            return;
        }
        if (Looper.myLooper() == Looper.getMainLooper()) {
            listener.onProgress(progress, total);
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
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
            new Handler(Looper.getMainLooper()).post(new Runnable() {
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

}
