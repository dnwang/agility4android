package org.pinwheel.agility.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved
 *
 * @author dnwang
 */
public final class FieldUtils {

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

    public static HashMap<String, Object> obj2Map(Object obj) {
        HashMap<String, Object> values = new HashMap<>();
        Class cls = obj.getClass();
        for (Field field : cls.getDeclaredFields()) {
            if (!field.isAnnotationPresent(Ignore.class)) {
                try {
                    field.setAccessible(true);
                    values.put(field.getName(), field.get(obj));
                } catch (IllegalAccessException ignore) {
                }
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

    public static HashMap<String, Object> getPropertiesWithoutIgnore(Object obj) {
        if (null == obj) {
            return null;
        }
        return getPropertiesWithoutIgnore((Class) obj.getClass(), obj);
    }

    public static <T> HashMap<String, Object> getPropertiesWithoutIgnore(Class<T> cls, T obj) {
        if (null == obj || null == cls) {
            return null;
        }
        HashMap<String, Object> superParams = getPropertiesWithoutIgnore(cls.getSuperclass(), obj);
        HashMap<String, Object> params = new HashMap<>();
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(Ignore.class)) {
                try {
                    field.setAccessible(true);
                    params.put(field.getName(), field.get(obj));
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

    public static HashMap<String, Object> getPropertiesWithMark(Object obj) {
        if (null == obj) {
            return null;
        }
        return getPropertiesWithMark((Class) obj.getClass(), obj);
    }

    public static <T> HashMap<String, Object> getPropertiesWithMark(Class<T> cls, T obj) {
        if (null == obj || null == cls) {
            return null;
        }
        HashMap<String, Object> superParams = getPropertiesWithMark(cls.getSuperclass(), obj);
        HashMap<String, Object> params = new HashMap<>();
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Mark.class)) {
                try {
                    field.setAccessible(true);
                    params.put(field.getName(), field.get(obj));
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
