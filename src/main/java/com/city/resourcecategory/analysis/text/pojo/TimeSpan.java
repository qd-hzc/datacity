package com.city.resourcecategory.analysis.text.pojo;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by wxl on 2016/3/29.
 * 时间范围,包括开始时间和结束时间,用来查询时间
 */
public class TimeSpan {
    private Date begin;
    private Date end;
    //时间
    private static Calendar calendar = Calendar.getInstance();

    /**
     * 初始化
     *
     * @param year  年
     * @param month 月
     */
    public TimeSpan(int year, int month) {
        //开始时间
        calendar.set(year, month - 1, 1, 0, 0, 0);
        begin = calendar.getTime();
        //结束时间
        calendar.set(Calendar.MONTH, month);
        end = calendar.getTime();
    }

    public Date getBegin() {
        return begin;
    }

    public void setBegin(Date begin) {
        this.begin = begin;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }
}
