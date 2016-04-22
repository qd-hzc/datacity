package com.city.resourcecategory.themes.pojo;

/**
 * 数据集中对应时间框架的数据
 * Created by HZC on 2016/4/5.
 */
public class DataSetTimeFrame {
    //    时间框架id
    private Integer timeFrameId;
    //    时间框架名称
    private String name;
    //    对应数据集中的数据
    private String value;

    public DataSetTimeFrame() {
    }

    public DataSetTimeFrame(Integer timeFrameId, String name, String value) {
        this.timeFrameId = timeFrameId;
        this.name = name;
        this.value = value;
    }

    public Integer getTimeFrameId() {
        return timeFrameId;
    }

    public void setTimeFrameId(Integer timeFrameId) {
        this.timeFrameId = timeFrameId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
