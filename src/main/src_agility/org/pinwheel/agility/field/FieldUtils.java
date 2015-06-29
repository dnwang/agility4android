package org.pinwheel.agility.field;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved
 *
 * @author dnwang
 */
public class FieldUtils {

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

}
