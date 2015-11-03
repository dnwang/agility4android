package org.pinwheel.agility.util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Json数据解析工具类，支持的Json格式有
 * [{"":"","":""},{"":"","":""},{"":"","":""}]、 {"":"","":""}]  单层嵌套
 * {"":"","":{}}  只支持多层的Json对象嵌套    多层的数组嵌套，混合嵌套暂不不支持
 */
public final class JsonUtils {
    /**
     * json数据解析     [{"":"","":""},{"":"","":""},{"":"","":""}]  单层嵌套模式
     * 暂不支持[,,,,](数组中嵌套的不是Json对象而是普通对象)
     *
     * @param clazz
     * @param json
     * @return
     * @throws Exception
     */
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

    /**
     * json数据解析    [{"":"","":""},{"":"","":""},{"":"","":""}]
     *
     * @param clazz
     * @param array
     * @return
     * @throws Exception
     */
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

    /**
     * json数据解析  {}  简单模式
     *
     * @param clazz
     * @param json
     * @return
     * @throws Exception
     */
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

    /**
     * json数据解析  {}  简单模式
     *
     * @param clazz
     * @param jsonobj
     * @return
     * @throws Exception
     */
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
