package com.city.common.util;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created by wxl on 2016/2/29.
 * list相关的操作
 */
public class ListUtil {

    /**
     * 将空格去掉,生成每个项的toString
     *
     * @param list      要生成字符串的集合
     * @param separator 分隔符
     */
    public static String getArrStr(Collection list, String separator) {
        StringBuilder sb = new StringBuilder();
        if (notEmpty(list)) {
            if (separator == null) {
                separator = ",";
            }
            Iterator it = list.iterator();
            while (it.hasNext()) {
                Object next = it.next();
                if (next != null) {
                    String str = next.toString();
                    sb.append(str.trim()).append(separator);
                }
            }
            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
        }
        return sb.toString();
    }

    /**
     * 将空格去掉,生成每个项的toString
     *
     * @param list 要生成字符串的集合
     */
    public static String getArrStr(Collection list) {
        return getArrStr(list, ",");
    }

    /**
     * 判断集合非空
     */
    public static boolean notEmpty(Collection list) {
        return list == null || list.size() == 0 ? false : true;
    }
}
