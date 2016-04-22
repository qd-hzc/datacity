package com.city.support.dataSet.query.pojo;

import java.util.List;

/**
 * 分析报表有效报告期时间类
 * Created by HZC on 2016/3/31.
 */
public class ResearchTimePojo {

    //    分析报表频度
    private Integer frequency;
    //    分析报表报告期年：2016、2015
    private Integer year;
    //    分析报表期度：年：12，半年：6、12，季：3、6、9、12，月：1、2、3、4、5、6、7、8、9、10、11、12
    private List<Integer> periods;

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public List<Integer> getPeriods() {
        return periods;
    }

    public void setPeriods(List<Integer> periods) {
        this.periods = periods;
    }
}
