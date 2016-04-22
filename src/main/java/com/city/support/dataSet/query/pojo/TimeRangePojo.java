package com.city.support.dataSet.query.pojo;

import com.city.common.pojo.Constant;

/**
 * Created by wxl on 2016/3/5.
 * 时间范围,保存
 */
public class TimeRangePojo extends TimePojo {
    private int type = Constant.TIMERANGE.XUANZE;//时间类型  Constant.TIMERANGE[LIANXU,XUANZE,BAOGAOQI,WU]中的一个
    private int periodsSpan = 1;//期数,类型为报告期时生效

    public TimeRangePojo() {
    }

    public TimeRangePojo(int type) {
        this.type = type;
    }

    public TimeRangePojo(int year, int period) {
        super(year, period);
    }

    public TimeRangePojo(int type, int year, int period) {
        super(type, year, period);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPeriodsSpan() {
        return periodsSpan;
    }

    public void setPeriodsSpan(int periodsSpan) {
        this.periodsSpan = periodsSpan;
    }
}
