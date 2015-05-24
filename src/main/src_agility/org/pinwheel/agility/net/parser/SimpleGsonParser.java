package org.pinwheel.agility.net.parser;

import com.google.gson.Gson;

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
@Deprecated
final class SimpleGsonParser<T> implements IResponseParser<T> {

    private T obj;

    private Gson mGson;
    private Class<T> cls;
    private Type type;

    public SimpleGsonParser(Class<T> cls) {
        this.mGson = new Gson();
        this.cls = cls;
    }

    public SimpleGsonParser(Type type) {
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
    }

    @Override
    public final void parse(byte[] dataBytes) throws Exception {
        if (cls != null) {
            this.onParse(mGson.fromJson(new String(dataBytes, "UTF-8"), cls));
        } else if (type != null) {
            this.onParse((T) mGson.fromJson(new String(dataBytes, "UTF-8"), type));
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
