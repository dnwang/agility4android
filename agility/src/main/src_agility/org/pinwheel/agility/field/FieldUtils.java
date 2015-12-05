package org.pinwheel.agility.field;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved
 *
 * @author dnwang
 */
public final class FieldUtils {

    private FieldUtils() {

    }

    /**
     * 获取对象的成员变量
     *
     * @param obj
     * @return
     */
    public static Map<String, String> obj2Map(Object obj) {
        Map<String, String> values = new HashMap<String, String>();
        Class cls = obj.getClass();
        for (Field field : cls.getDeclaredFields()) {
            if (field.isAnnotationPresent(Ignore.class)) {
                continue;
            }
            try {
                field.setAccessible(true);
                Object v = field.get(obj);
                values.put(field.getName(), (v == null ? "" : v.toString()));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return values;
    }

    /**
     * 获取对象的泛型类
     *
     * @param obj
     * @return
     */
    public static Class getGenericClass(Object obj) {
        if (obj == null) {
            return null;
        }
        Type genType = obj.getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        return (Class) params[0];
    }

}
