package com.city.resourcecategory.themes.pojo;

/**
 * 分析报表查询条件
 * Created by HZC on 2016/3/25.
 */
public class ResearchVO {
    //        自定义表id
    private Integer id;
    //        自定义表年份：如：2016,2015
    private Integer year;
    //        自定义表期度：如：1月，1季度，上半年
    private Integer period;
    //        是否填充数据：0：不填充数据，1：填充数据
    private Integer hasData = 1;
    //        是否带表格宽度和高度：0：不带，1：带样式
    private Integer hasStyle = 1;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    public Integer getHasData() {
        return hasData;
    }

    public void setHasData(Integer hasData) {
        this.hasData = hasData;
    }

    public Integer getHasStyle() {
        return hasStyle;
    }

    public void setHasStyle(Integer hasStyle) {
        this.hasStyle = hasStyle;
    }

    public ResearchVO() {
    }

    public ResearchVO(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "ResearchVO{" +
                "id=" + id +
                ", year=" + year +
                ", period=" + period +
                ", hasData=" + hasData +
                ", hasStyle=" + hasStyle +
                '}';
    }
}
