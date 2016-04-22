package com.city.support.dataSet.query.pojo;

import com.city.common.pojo.Constant;

import java.util.Comparator;

/**
 * Created by wxl on 2016/3/4.
 * 保存年,月,频度
 */
public class TimePojo implements Comparable<TimePojo> {
    private Integer frequency = Constant.PeriodType.MONTH;//频度, Constant.PeriodType[YEAR,HALF,QUARTER,MONTH] 4个值里面的一个,默认为月报
    private int year;//年
    private int period;//期度

    public TimePojo() {
    }

    public TimePojo(int year, int period) {
        this.year = year;
        this.period = period;
    }

    public TimePojo(Integer frequency, int year, int period) {
        if (frequency != null) {
            this.frequency = frequency;
        }
        this.year = year;
        this.period = period;
    }

    //向前推移n个报告期后的时间
    public TimePojo(TimePojo timePojo, int periodsBefore) {
        //获取时间中的数据
        int fre = timePojo.frequency;
        int year = timePojo.year;
        int period = timePojo.period;
        //报告期跨度
        int periodSpan = Constant.PeriodType.getPeriodSpan(fre);
        while (periodsBefore-- > 0) {
            period -= periodSpan;//减少期数
            if (period <= 0) {
                period = 12;
                year--;
            }
        }
        this.frequency = fre;
        this.year = year;
        this.period = period;
    }

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TimePojo timePojo = (TimePojo) o;

        if (year != timePojo.year) return false;
        if (period != timePojo.period) return false;
        return frequency.equals(timePojo.frequency);

    }

    @Override
    public int hashCode() {
        int result = frequency.hashCode();
        result = 31 * result + year;
        result = 31 * result + period;
        return result;
    }

    @Override
    public int compareTo(TimePojo o) {
        int year = this.year - o.year;
        if (year == 0) {
            return this.period - o.period;
        }
        return year;
    }
}
