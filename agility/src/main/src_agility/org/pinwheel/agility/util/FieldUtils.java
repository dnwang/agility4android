package org.pinwheel.agility.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
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

    /**
     * Copyright (C), 2015 <br>
     * <br>
     * All rights reserved <br>
     * <br>
     *
     * @author dnwang
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface Ignore {
    }

    private FieldUtils() {

    }

    public static Map<String, String> obj2Map(Object obj) {
        Map<String, String> values = new HashMap<>();
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

    public static Class getGenericClass(Object obj) {
        if (obj == null) {
            return null;
        }
        Type genType = obj.getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        return (Class) params[0];
    }

}
