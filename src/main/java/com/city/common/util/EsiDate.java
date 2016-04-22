package com.city.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间类
 * Created by HZC on 2016/3/28.
 */
public class EsiDate {

    private String[] date;

    /**
     * 返回时间年：2016
     *
     * @return
     */
    public String getYear() {
        return date[0];
    }

    /**
     * 返回时间月：01
     *
     * @return
     */
    public String getMonth() {
        return date[1];
    }

    /**
     * 返回时间日：04
     *
     * @return
     */
    public String getDay() {
        return date[2];
    }

    /**
     * 返回时间小时：12
     *
     * @return
     */
    public String getHour() {
        return date[3];
    }

    /**
     * 返回时间分钟：34
     *
     * @return
     */
    public String getMinute() {
        return date[4];
    }

    /**
     * 返回时间秒：34
     *
     * @return
     */
    public String getSecond() {
        return date[5];
    }

    /**
     * 空构造，初始化当前时间
     */
    public EsiDate() {
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd/hh/mm/ss");
        String dateStr = format.format(now);
        date = dateStr.split("/");
    }

    /**
     * 初始化传入时间
     *
     * @param time
     */
    public EsiDate(Date time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd/hh/mm/ss");
        String dateStr = format.format(time);
        date = dateStr.split("/");
    }

    /**
     * 获取当前时间的time
     *
     * @return
     */
    public static long getTime() {
        return new Date().getTime();
    }
}
