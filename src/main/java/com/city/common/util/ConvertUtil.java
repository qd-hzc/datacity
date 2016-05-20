package com.city.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 转换工具类
 * Created by HZC on 2016/1/5.
 */
public class ConvertUtil<T> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * 转换pojo的字段值到bank中
     * <pre>
     *     传入两个对象，如果pojo中的字段不为空，则将pojo中的对应字段值赋予bank
     * </pre>
     *
     * @param pojo 赋值的源
     * @param bank 待赋值的对象
     * @param name 全类名：com.city.support.manage.area.entity.SptMgrAreaEntity
     * @author hzc
     * @createDate 2016-1-5
     */
    public void replication(T pojo, T bank, String name) {
        try {
            Class<?> aClass = Class.forName(name);
            Method[] methods = aClass.getDeclaredMethods();
            for (Method method : methods) {
                String nameGet = method.getName();
                if (nameGet.indexOf("get") > -1) {
                    Object pge = getter(pojo, nameGet);
                    if (pge != null) {
                        for (Method methodSet : methods) {
                            String nameSet = methodSet.getName();
                            if (nameSet.indexOf("set") > -1 && nameGet.substring(1).equals(nameSet.substring(1))) {
                                Class<?>[] parameterTypes = methodSet.getParameterTypes();
                                Class<?> parameterType = parameterTypes[0];
                                setter(bank, nameSet, pge, parameterType);
                            }
                        }
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            EsiLogUtil.error(log, e.getMessage());
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            EsiLogUtil.error(log, e.getMessage());
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            EsiLogUtil.error(log, e.getMessage());
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            EsiLogUtil.error(log, e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 将对象的非空属性替换为自己的属性
     *
     * @param pojo   属性值赋予的对象
     * @param source 提供属性值的对象
     * @param aClass 类
     */
    public void apply(T pojo, T source, Class aClass) {
        try {
            Method[] methods = aClass.getDeclaredMethods();
            for (Method method : methods) {
                String nameGet = method.getName();
                if (nameGet.indexOf("get") == 0) {//get方法
                    Object pge = getter(source, nameGet);//pojo的get方法
                    if (pge != null) {
                        for (Method methodSet : methods) {
                            String nameSet = methodSet.getName();
                            if (nameSet.indexOf("set") == 0 && nameGet.substring(1).equals(nameSet.substring(1))) {
                                Class<?>[] parameterTypes = methodSet.getParameterTypes();
                                Class<?> parameterType = parameterTypes[0];
                                setter(pojo, nameSet, pge, parameterType);
                            }
                        }
                    }
                } else if (nameGet.indexOf("is") == 0) {//is方法
                    Object pge = getter(source, nameGet);//pojo的get方法
                    if (pge != null) {
                        for (Method methodSet : methods) {
                            String nameSet = methodSet.getName();
                            if (nameSet.indexOf("set") == 0 && nameGet.substring(2).equals(nameSet.substring(3))) {
                                Class<?>[] parameterTypes = methodSet.getParameterTypes();
                                Class<?> parameterType = parameterTypes[0];
                                setter(pojo, nameSet, pge, parameterType);
                            }
                        }
                    }

                }
            }
        } catch (NoSuchMethodException e) {
            EsiLogUtil.error(log, e.getMessage());
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            EsiLogUtil.error(log, e.getMessage());
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            EsiLogUtil.error(log, e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * getter方法
     *
     * @param t
     * @param methodStr
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private Object getter(T t, String methodStr) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = t.getClass().getMethod(methodStr);
        Object invoke = method.invoke(t);
        return invoke;
    }

    /**
     * setter方法
     *
     * @param t
     * @param methodStr
     * @param value
     * @param type
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private void setter(T t, String methodStr, Object value, Class<?> type) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = t.getClass().getMethod(methodStr, type);
        method.invoke(t, value);
    }
}
