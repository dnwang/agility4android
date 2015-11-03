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
    public void parse(String dataString) throws Exception {

    }

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

    protected final void dispatchOnProgress(final long progress, final long total) {
        if (listener != null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    listener.onProgress(progress, total);
                }
            });
        }
    }

    protected final void dispatchOnComplete() {
        if (listener != null) {
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

        public void onProgress(long progress, long total);

        public void onComplete();

    }

}
