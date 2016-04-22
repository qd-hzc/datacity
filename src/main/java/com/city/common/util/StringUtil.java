package com.city.common.util;

/**
 * Created by wxl on 2016/3/21.
 * 字符串相关工具类
 */
public class StringUtil {
    /**
     * 判断字符串非空
     */
    public static boolean notEmpty(String str) {
        return str == null || str.equals("") ? false : true;
    }

    /**
     * 判断字符串去空格后非空
     */
    public static boolean trimNotEmpty(String str) {
        return str == null || str.trim().equals("") ? false : true;
    }
}
