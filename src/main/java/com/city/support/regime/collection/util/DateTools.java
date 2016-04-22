package com.city.support.regime.collection.util;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.log4j.Logger;
/**
 * 时间转换工具
 * @author chenyq
 *
 */
public class DateTools {
    public static final String COMMON_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_PWD = "yyyyMMddHHmmss";
    private static Logger logger = Logger.getLogger(DateTools.class.getName());

    private static String msg = new String();

    /**
     * 根据给定格式得到当前日期时间
     * @param fmt 需要的日期格式
     * @return 符合格式要求的日期字符串 返回格式一般应为yyyy-MM-dd HH:mm:ss
     */
    public static String getDate(String fmt) {
        Date myDate = new Date(System.currentTimeMillis());
        SimpleDateFormat sDateformat = new SimpleDateFormat(fmt);
        sDateformat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        return sDateformat.format(myDate).toString();
    }

    /**
     * 将指定日期格式化
     * @param fmt
     * @param date
     * @return
     */
    public static String getDate(String fmt,Date date) {
        SimpleDateFormat sDateformat = new SimpleDateFormat(fmt);
        return sDateformat.format(date).toString();
    }


    public static String getCommonDateStr(Calendar calendar) {
        if (calendar == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(COMMON_DATE_FORMAT);
        return sdf.format(calendar.getTime());
    }
    public static void main(String[] args) {
        System.out.println(new Date("2012-12-12 : 00:00:00"));
    }
}
