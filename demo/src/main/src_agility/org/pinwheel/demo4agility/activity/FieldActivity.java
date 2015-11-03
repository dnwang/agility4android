package org.pinwheel.demo4agility.activity;

import android.view.View;

import org.pinwheel.agility.field.FieldUtils;
import org.pinwheel.agility.field.Ignore;
import org.pinwheel.agility.net.Request;
import org.pinwheel.agility.net.VolleyRequestHelper;
import org.pinwheel.demo4agility.field.DemoEntity;
import org.pinwheel.demo4agility.field.InjectStruct;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Map;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved
 *
 * @author dnwang
 */
public class FieldActivity extends AbsTestActivity {

    @Override
    protected void onInitInCreate() {

    }

    @Override
    protected View getContentView() {
        return null;
    }

    @Override
    protected void doTest() {
        InjectStruct injectStruct = new InjectStruct();
        // 注入
        logout("-------------- Inject -----------------");
        injectValue(injectStruct);
        logout(injectStruct.toString());

//        Object reflex_obj = new ReflexStruct();
        Object reflex_obj = injectStruct;

        // 反射
        logout("-------------- Reflex -----------------");
        logout("┌ " + reflex_obj.getClass().getSimpleName() + " {");
        reflexValue("│\t", reflex_obj);
        logout("└ }");


        // add method test 201506029
        Map<String, String> values = FieldUtils.obj2Map(new DemoEntity());
        logout(values);
        VolleyRequestHelper.init(this);
        VolleyRequestHelper.debug = true;
        Request api = new Request.Builder().url("http://www.baidu.com").addParams(values).create();
        VolleyRequestHelper.doGet(api, null);
    }

    private void reflexValue(String space, Object obj) {
        Class cls = obj.getClass();

        for (Field field : cls.getDeclaredFields()) {
            field.setAccessible(true); // 设置之后才能获取private的属性值，否则get只能获取public
            Annotation[] declareAnnotations = field.getDeclaredAnnotations();
            String annotations = "";
            boolean isIgnore = false;
            for (Annotation annotation : declareAnnotations) {
                if (annotation instanceof Ignore) {
                    isIgnore = true;
                }
                annotations += annotation.annotationType().getSimpleName() + "; ";
            }
            if (isIgnore) {
                logout(space + "\t " + field.getName() + " (" + annotations + ")");
            } else {
                try {
                    String type = field.getType().getSimpleName(); // 类型
                    String name = field.getName(); // 名称
                    String modifier = Modifier.toString(field.getModifiers()); // 修饰副

                    if (type.equals("int") || type.equals("Integer")
                            || type.equalsIgnoreCase("boolean")
                            || type.equalsIgnoreCase("float")
                            || type.equalsIgnoreCase("string")
                            || type.equalsIgnoreCase("long")
                            || type.equalsIgnoreCase("double")
                            || type.equalsIgnoreCase("short")
                            || type.equalsIgnoreCase("char")) {
                        Object v = field.get(obj); // 属性值
                        String value = v == null ? "null" : v.toString();
                        logout(space + "\t " + modifier + " " + type + " " + name + " = " + value);
                    } else {
                        Object v = field.get(obj);
                        if (v == null) {
                            logout(space + "\t " + modifier + " " + type + " " + name + " = null");
                        } else {
                            logout(space + "┌ " + modifier + " " + type + " " + name + " {");
                            reflexValue(space + "│\t", v);
                            logout(space + "└ }");
                        }
                    }
                } catch (IllegalAccessException e) {
                }
            }
        }
    }

    private void injectValue(Object obj) {
        Class cls = obj.getClass();

        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                String typeName = field.getType().getSimpleName();
                // set(Object), setXXX()
                if (typeName.equalsIgnoreCase("string")) {
                    field.set(obj, "im inject");
                } else if (typeName.equalsIgnoreCase("int") || typeName.equalsIgnoreCase("integer")) {
                    field.setInt(obj, 11);
                } else if (typeName.equalsIgnoreCase("long")) {
                    field.setLong(obj, 22l);
                } else if (typeName.equalsIgnoreCase("double")) {
                    field.setDouble(obj, 3.3333333);
                } else if (typeName.equalsIgnoreCase("float")) {
                    field.setFloat(obj, 4.4f);
                } else if (typeName.equalsIgnoreCase("boolean")) {
                    field.setBoolean(obj, true);
                } else if (typeName.equalsIgnoreCase("short")) {
                    field.setShort(obj, (short) 10);
                } else if (typeName.equalsIgnoreCase("char") || typeName.equalsIgnoreCase("character")) {
                    field.setChar(obj, 'w');
                } else {
                    Object sub_obj = createObject(field.getType());

                    field.set(obj, sub_obj);
                    injectValue(sub_obj);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private Object createObject(Class cls) {
        String typeName = cls.getSimpleName();

        // 基础类型 不能newInstance(), 这里使用 包装类型返回
        if (typeName.equalsIgnoreCase("int")) {
            return 0;
        } else if (typeName.equalsIgnoreCase("long")) {
            return 0l;
        } else if (typeName.equalsIgnoreCase("double")) {
            return 0.0;
        } else if (typeName.equalsIgnoreCase("float")) {
            return 0.0f;
        } else if (typeName.equalsIgnoreCase("boolean")) {
            return false;
        } else if (typeName.equalsIgnoreCase("short")) {
            return (short) 0;
        } else if (typeName.equalsIgnoreCase("char")) {
            return '\0';
        } else {
            Object obj = null;

            Constructor constructor = cls.getConstructors()[0]; // 使用第一个构造
            Class[] types = constructor.getParameterTypes();
            Object[] params = new Object[types.length];
            for (int i = 0; i < params.length; i++) {
                params[i] = createObject(types[i]);  // 生成默认的构造参数
            }

            try {
                obj = constructor.newInstance(params);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            return obj;
        }
    }

}
