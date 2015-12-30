package org.pinwheel.agility.util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public final class JsonUtils {

    public static <T> List<T> parserJsonToList(Class<T> clazz, String json) throws Exception {
        Field[] names = clazz.getFields();
        JSONArray array = new JSONArray(json);
        List<T> list = new ArrayList<T>();
        int length = array.length();
        for (int i = 0; i < length; i++) {
            T object = clazz.newInstance();
            JSONObject jsonobj = array.getJSONObject(i);
            for (int j = 0; j < names.length; j++) {
                names[j].set(object, jsonobj.get(names[j].getName()));
            }
            list.add(object);
        }
        return list;
    }

    public static <T> List<T> parserJsonToList(Class<T> clazz, JSONArray array) throws Exception {
        Field[] names = clazz.getFields();
        List<T> list = new ArrayList<T>();
        int length = array.length();
        for (int i = 0; i < length; i++) {
            T object = clazz.newInstance();
            JSONObject jsonobj = array.getJSONObject(i);
            for (int j = 0; j < names.length; j++) {
                names[j].set(object, jsonobj.get(names[j].getName()));
            }
            list.add(object);
        }
        return list;
    }

    public static <T> T parserJsonToObject(Class<T> clazz, String json) throws Exception {
        Field[] names = clazz.getFields();
        JSONObject jsonobj = new JSONObject(json);
        T object = clazz.newInstance();
        for (int i = 0; i < names.length; i++) {
            Object hah = jsonobj.get(names[i].getName());
            if (hah instanceof JSONObject) {
                hah = parserJsonToObject(names[i].getType(), (JSONObject) hah);
            }
            names[i].set(object, hah);
        }
        return object;
    }

    public static <T> T parserJsonToObject(Class<T> clazz, JSONObject jsonobj) throws Exception {
        Field[] names = clazz.getFields();
        T object = clazz.newInstance();
        for (int i = 0; i < names.length; i++) {
            Object hah = jsonobj.get(names[i].getName());
            if (hah instanceof JSONObject) {
                hah = parserJsonToObject(names[i].getType(), (JSONObject) hah);
            }
            names[i].set(object, hah);
        }
        return object;
    }
}
