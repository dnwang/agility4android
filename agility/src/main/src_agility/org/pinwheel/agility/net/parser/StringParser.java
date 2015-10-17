package org.pinwheel.agility.net.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public class StringParser implements IDataParser<String> {
    private final static String TAG = StringParser.class.getSimpleName();

    private String result;

    private OnParseAdapter listener;

    @Override
    public void parse(InputStream inStream) throws Exception {
        result = streamToString(inStream);
        if (listener != null) {
            listener.dispatchOnComplete();
        }
    }

    @Override
    public void parse(byte[] dataBytes) throws Exception {
        if (listener != null) {
            listener.dispatchOnProgress(0, 0);
        }

        result = new String(dataBytes, "UTF-8");

        if (listener != null) {
            listener.dispatchOnProgress(0, result.getBytes().length);
        }

        if (listener != null) {
            listener.dispatchOnComplete();
        }
    }

    @Override
    public void parse(String dataString) throws Exception {
        if (listener != null) {
            listener.dispatchOnProgress(0, 0);
        }

        result = dataString;

        if (listener != null) {
            listener.dispatchOnProgress(0, result == null ? -1 : result.getBytes().length);
        }

        if (listener != null) {
            listener.dispatchOnComplete();
        }
    }

    @Override
    public String getResult() {
        return result;
    }

    @Override
    public void release() {
        result = null;
        listener = null;
    }

    @Override
    public void setOnParseAdapter(OnParseAdapter listener) {
        this.listener = listener;
    }

    protected final String streamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            long lastTime = 0;
            long currentTime = 0;
            long progress = 0;

            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");

                // progress callback
                if (listener != null) {
                    progress += line.length();
                    currentTime = System.currentTimeMillis();
                    if (currentTime - lastTime > 1000) {
                        listener.dispatchOnProgress(progress, -1);// -1 unknown
                        lastTime = currentTime;
                    }
                }
                // end
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

}
