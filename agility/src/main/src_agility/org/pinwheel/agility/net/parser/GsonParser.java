package org.pinwheel.agility.net.parser;

import com.google.gson.Gson;
import com.litesuits.android.log.Log;

import org.pinwheel.agility.net.RequestManager;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

/**
 * 版权所有 (C), 2014 <br>
 * <br>
 * All rights reserved
 *
 * @author dnwang
 * @date 2014/10/30 23:26
 * @description
 */
public final class GsonParser<T> implements IResponseParser<T> {
    private static final String TAG = GsonParser.class.getSimpleName();

    private T obj;

    private Gson mGson;
    private Class<T> cls;
    private Type type;

    public GsonParser(Class<T> cls) {
        this.mGson = new Gson();
        this.cls = cls;
    }

    public GsonParser(Type type) {
        this.mGson = new Gson();
        this.type = type;
    }

    @Override
    public final void parse(InputStream inStream) throws Exception {
        if (cls != null) {
            this.onParse(mGson.fromJson(new InputStreamReader(inStream), cls));
        } else if (type != null) {
            this.onParse((T) mGson.fromJson(new InputStreamReader(inStream), type));
        }
    }

    @Override
    public final void parse(String dataString) throws Exception {
        if (cls != null) {
            this.onParse(mGson.fromJson(dataString, cls));
        } else if (type != null) {
            this.onParse((T) mGson.fromJson(dataString, type));
        }

        if (RequestManager.debug) {
            Log.d(TAG, dataString);
        }
    }

    @Override
    public final void parse(byte[] dataBytes) throws Exception {
        String result = new String(dataBytes, "UTF-8");
        if (cls != null) {
            this.onParse(mGson.fromJson(result, cls));
        } else if (type != null) {
            this.onParse((T) mGson.fromJson(result, type));
        }

        if (RequestManager.debug) {
            Log.d(TAG, result);
        }
    }

    protected void onParse(T t) throws Exception {
        this.obj = t;
    }

    @Override
    public T getResult() {
        return obj;
    }
}
