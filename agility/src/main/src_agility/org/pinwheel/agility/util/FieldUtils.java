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
    public @interface Ignore {
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Mark {
    }

    private FieldUtils() {
        throw new AssertionError();
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
                values.put(field.getName(), (v == null ? "" : String.valueOf(v)));
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

    public static Map<String, String> getPropertiesWithoutIgnore(Object obj) {
        if (null == obj) {
            return null;
        }
        return getPropertiesWithoutIgnore((Class) obj.getClass(), obj);
    }

    public static <T> Map<String, String> getPropertiesWithoutIgnore(Class<T> cls, T obj) {
        if (null == obj || null == cls) {
            return null;
        }
        Map<String, String> superParams = getPropertiesWithoutIgnore(cls.getSuperclass(), obj);
        Map<String, String> params = new HashMap<>();
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(FieldUtils.Ignore.class)) {
                try {
                    field.setAccessible(true);
                    Object v = field.get(obj);
                    params.put(field.getName(), v == null ? "" : String.valueOf(v));
                } catch (IllegalAccessException ignore) {
                }
            }
        }
        if (null != superParams) {
            superParams.putAll(params);
            return superParams;
        } else {
            return params;
        }
    }

    public static Map<String, String> getPropertiesWithMark(Object obj) {
        if (null == obj) {
            return null;
        }
        return getPropertiesWithMark((Class) obj.getClass(), obj);
    }

    public static <T> Map<String, String> getPropertiesWithMark(Class<T> cls, T obj) {
        if (null == obj || null == cls) {
            return null;
        }
        Map<String, String> superParams = getPropertiesWithMark(cls.getSuperclass(), obj);
        Map<String, String> params = new HashMap<>();
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(FieldUtils.Mark.class)) {
                try {
                    field.setAccessible(true);
                    Object v = field.get(obj);
                    params.put(field.getName(), v == null ? "" : String.valueOf(v));
                } catch (IllegalAccessException ignore) {
                }
            }
        }
        if (null != superParams) {
            superParams.putAll(params);
            return superParams;
        } else {
            return params;
        }
    }

}
