package org.pinwheel.agility.net.parser;

import android.util.Log;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public class GsonParser<T> extends DataParserAdapter<T> {
    private static final String TAG = GsonParser.class.getSimpleName();

    private static boolean debug = false;

    private T result;

    private Gson gson;
    private Class<T> cls;
    private Type type;

    public GsonParser(Class<T> cls) {
        this.gson = new Gson();
        this.cls = cls;
    }

    public GsonParser(Type type) {
        this.gson = new Gson();
        this.type = type;
    }

    @Override
    public final void parse(InputStream inStream) throws Exception {
        dispatchOnProgress(0, -1);

        if (cls != null) {
            this.onParse(gson.fromJson(new InputStreamReader(inStream), cls));
        } else if (type != null) {
            this.onParse((T) gson.fromJson(new InputStreamReader(inStream), type));
        }

        dispatchOnComplete();
    }

    @Override
    public final void parse(String dataString) throws Exception {
        dispatchOnProgress(0, dataString == null ? -1 : dataString.getBytes().length);

        if (debug) {
            Log.d(TAG, dataString);
        }

        if (cls != null) {
            this.onParse(gson.fromJson(dataString, cls));
        } else if (type != null) {
            this.onParse((T) gson.fromJson(dataString, type));
        }

        long length = dataString == null ? -1 : dataString.getBytes().length;
        dispatchOnProgress(length, length);

        dispatchOnComplete();
    }

    @Override
    public final void parse(byte[] dataBytes) throws Exception {
        dispatchOnProgress(0, dataBytes == null ? -1 : dataBytes.length);

        String result = new String(dataBytes, "UTF-8");
        if (debug) {
            Log.d(TAG, result);
        }
        if (cls != null) {
            this.onParse(gson.fromJson(result, cls));
        } else if (type != null) {
            this.onParse((T) gson.fromJson(result, type));
        }

        dispatchOnProgress(dataBytes.length, dataBytes.length);

        dispatchOnComplete();
    }

    protected void onParse(T t) throws Exception {
        this.result = t;
    }

    @Override
    public T getResult() {
        return result;
    }

    @Override
    public void release() {
        super.release();
        gson = null;
        cls = null;
        type = null;
        result = null;
    }

}
