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
public interface IDataParser<T> {

    public static boolean debug = false;

    /**
     * @param inStream
     * @throws Exception
     */
    public void parse(InputStream inStream) throws Exception;

    /**
     * @param dataBytes
     * @throws Exception
     */
    public void parse(byte[] dataBytes) throws Exception;

    /**
     * @param dataString
     * @throws Exception
     */
    public void parse(String dataString) throws Exception;

    /**
     * Get parser result
     *
     * @return T
     */
    public T getResult();

    public void release();

    public void setOnParseAdapter(OnParseAdapter listener);


    /**
     * Parse progress adaper
     */
    public abstract class OnParseAdapter {

        public abstract void onProgress(long progress, long total);

        public void onComplete() {
        }

        final void dispatchOnProgress(final long progress, final long total) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    onProgress(progress, total);
                }
            });
        }

        final void dispatchOnComplete() {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    onComplete();
                }
            });
        }

    }

}
