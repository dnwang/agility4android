package org.pinwheel.agility.util.ex;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * <b>类说明:反射工具类</b>
 *
 * @author sulei Email:leisu@iflytek.com
 * @version 2015年6月2日 上午11:50:34
 */
public final class ReflectionUtils {

    private ReflectionUtils() {
        throw new AssertionError();
    }

    /**
     * 获取对象中的成员属性
     *
     * @param obj          需反射的对象
     * @param propertyName 属性名称
     * @return
     * @throws NoSuchFieldException
     */
    public static Object getProperty(Object obj, String propertyName)
            throws NoSuchFieldException {
        Object result = null;
        Class<? extends Object> objClass = obj.getClass();
        Field field = null;
        do {
            try {
                field = objClass.getDeclaredField(propertyName);
            } catch (NoSuchFieldException e) {
            }

            if (null != field) {
                field.setAccessible(true);
                try {
                    result = field.get(obj);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
                break;
            }
        } while ((objClass = objClass.getSuperclass()) != null);

        if (null == field) {
            throw new NoSuchFieldException();
        }
        return result;
    }

    /**
     * 设置对象中的成员属性值
     *
     * @param obj
     * @param propertyName
     * @param paramObject
     * @throws NoSuchFieldException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static void setProperty(Object obj, String propertyName,
                                   Object paramObject) throws NoSuchFieldException,
            IllegalArgumentException {
        Class<? extends Object> objClass = obj.getClass();
        Field field = null;
        do {

            try {
                field = objClass.getDeclaredField(propertyName);
            } catch (NoSuchFieldException e) {
            }
            if (null != field) {
                field.setAccessible(true);
                try {
                    field.set(obj, paramObject);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        } while ((objClass = objClass.getSuperclass()) != null);
        if (null == field) {
            throw new NoSuchFieldException();
        }
    }

    /**
     * 获得类的静态属性
     *
     * @param className    类名称
     * @param propertyName 属性名称
     * @return
     * @throws ClassNotFoundException
     * @throws NoSuchFieldException
     */
    public static Object getStaticProperty(String className, String propertyName)
            throws ClassNotFoundException, NoSuchFieldException {
        Class<?> objClass = Class.forName(className);
        return getStaticProperty(objClass, propertyName);
    }

    /**
     * 获取类的静态属性
     *
     * @param clazz
     * @param propertyName
     * @return
     * @throws SecurityException
     * @throws NoSuchFieldException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static Object getStaticProperty(Class<?> clazz, String propertyName)
            throws NoSuchFieldException {
        Object result = null;
        Class<?> objClass = clazz;
        Field field = null;
        do {
            try {
                field = objClass.getDeclaredField(propertyName);
            } catch (NoSuchFieldException e) {
            }
            if (null != field) {
                field.setAccessible(true);
                try {
                    result = field.get(objClass);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
                break;
            }
        } while ((objClass = objClass.getSuperclass()) != null);
        if (null == field) {
            throw new NoSuchFieldException();
        }
        return result;
    }

    /**
     * 设置类静态属性值
     *
     * @param className
     * @param propertyName
     * @param paramObject
     * @throws ClassNotFoundException
     * @throws NoSuchFieldException
     * @throws IllegalArgumentException
     */
    public static void setStaticProperty(String className, String propertyName,
                                         Object paramObject) throws ClassNotFoundException, NoSuchFieldException,
            IllegalArgumentException {
        Class<?> objClass = Class.forName(className);
        setStaticProperty(objClass, propertyName, paramObject);
    }

    /**
     * 设置类静态属性值
     *
     * @param clazz
     * @param propertyName
     * @param paramObject
     * @throws NoSuchFieldException
     * @throws IllegalArgumentException
     */
    public static void setStaticProperty(Class<?> clazz, String propertyName,
                                         Object paramObject) throws NoSuchFieldException,
            IllegalArgumentException {
        Class<?> objClass = clazz;
        Field field = null;
        do {

            try {
                field = objClass.getDeclaredField(propertyName);
            } catch (NoSuchFieldException e) {
            }
            if (null != field) {
                field.setAccessible(true);
                try {
                    field.set(null, paramObject);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        } while ((objClass = objClass.getSuperclass()) != null);
        if (null == field) {
            throw new NoSuchFieldException();
        }
    }

    /**
     * 执行对象的成员方法
     *
     * @param owner       需反射的对象
     * @param methodName  方法名
     * @param paramsClass 参数类型
     * @param params      参数
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static Object invokeMethod(Object owner, String methodName,
                                      Class<?>[] paramsClass, Object[] params)
            throws NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        Class<?> cls = owner.getClass();
        Method method = getMethod(cls, methodName, paramsClass);
        method.setAccessible(true);

        return method.invoke(owner, params);
    }

    /**
     * 执行类的静态方法
     *
     * @param className   需反射的对象
     * @param methodName  方法名
     * @param paramsClass 参数类型
     * @param params      参数
     * @return
     * @throws NoSuchMethodException
     * @throws ClassNotFoundException
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     */
    public static Object invokeStaticMethod(String className,
                                            String methodName, Class<?>[] paramsClass, Object[] params)
            throws NoSuchMethodException, ClassNotFoundException,
            IllegalArgumentException, InvocationTargetException {
        Class<?> cls = Class.forName(className);
        return invokeStaticMethod(cls, methodName, paramsClass, params);
    }

    /**
     * 执行类的静态方法
     *
     * @param clazz
     * @param methodName
     * @param paramsClass
     * @param params
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     */
    public static Object invokeStaticMethod(Class<?> clazz, String methodName,
                                            Class<?>[] paramsClass, Object[] params)
            throws NoSuchMethodException, IllegalArgumentException,
            InvocationTargetException {
        Method method = getMethod(clazz, methodName, paramsClass);

        try {
            method.setAccessible(true);
            return method.invoke(null, params);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 创建实例对象
     *
     * @param className   构造参数名
     * @param paramsClass 参数类型
     * @param params      参数
     * @return
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws Exception
     */
    public static Object newInstance(String className, Class<?>[] paramsClass,
                                     Object[] params) throws ClassNotFoundException,
            NoSuchMethodException, InstantiationException,
            IllegalAccessException, IllegalArgumentException,
            InvocationTargetException {
        Class<?> clss = Class.forName(className);
        Constructor<?> cons = clss.getConstructor(paramsClass);
        return cons.newInstance(params);
    }

    /**
     * 创建类实例对象
     *
     * @param clazz
     * @param paramsClass
     * @param params
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static Object newInstance(Class<?> clazz, Class<?>[] paramsClass,
                                     Object[] params) throws NoSuchMethodException,
            InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        Constructor<?> cons = clazz.getConstructor(paramsClass);
        return cons.newInstance(params);
    }

    /**
     * 验证方法在该对象中是否存在
     *
     * @param owner       需反射的对象
     * @param methodName  方法名
     * @param paramsClass 参数类型
     * @return
     */
    public static boolean hasMethod(Object owner, String methodName,
                                    Class<?>[] paramsClass) {
        Class<?> ownerClass = owner.getClass();
        Method method = null;
        try {
            method = getMethod(ownerClass, methodName, paramsClass);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null != method;
    }

    /**
     * 利用递归找一个类的指定方法，如果找不到，去父亲里面找直到最上层Object对象为止。
     *
     * @param clazz      目标类
     * @param methodName 方法名
     * @param classes    方法参数类型数组
     * @return 方法对象
     * @throws NoSuchMethodException
     */
    public static Method getMethod(Class<?> clazz, String methodName,
                                   final Class<?>[] classes) throws NoSuchMethodException {
        Method method = null;

        Class<?> objClass = clazz;
        do {
            try {
                method = objClass.getDeclaredMethod(methodName, classes);
            } catch (NoSuchMethodException e) {
            }

            if (null != method) {
                method.setAccessible(true);
                break;
            }
        } while ((objClass = objClass.getSuperclass()) != null);

        if (null == method) {
            throw new NoSuchMethodException();
        }
        return method;
    }

}
