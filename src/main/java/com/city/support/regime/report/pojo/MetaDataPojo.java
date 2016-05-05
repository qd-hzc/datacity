package com.city.support.regime.report.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wxl on 2016/1/19 0019.
 * 元数据传输实体
 */
public class MetaDataPojo implements Serializable {
    //数据类型
    private Integer dataType;
    //数据名
    private String dataName;
    private String text;
    //数据值
    private Integer dataValue;
    //扩充字段
    //元数据为指标时,代表指标口径
    //源数据为调查对象时,代表调查对象类型
    private String dataInfo1;
    //源数据为指标时,代表默认部门
    private String dataInfo2;
    //子节点
    private List<MetaDataPojo> children;
    //是否为叶子节点
    private boolean leaf;
    //图标
    private String iconCls;

    public Integer getDataType() {
        return dataType;
    }

    public void setDataType(Integer dataType) {
        this.dataType = dataType;
    }

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
        this.text = dataName;
    }

    public String getText() {
        return dataName;
    }

    public Integer getDataValue() {
        return dataValue;
    }

    public void setDataValue(Integer dataValue) {
        this.dataValue = dataValue;
    }

    public String getDataInfo1() {
        return dataInfo1;
    }

    public void setDataInfo1(String dataInfo1) {
        this.dataInfo1 = dataInfo1;
    }

    public String getDataInfo2() {
        return dataInfo2;
    }

    public void setDataInfo2(String dataInfo2) {
        this.dataInfo2 = dataInfo2;
    }

    public List<MetaDataPojo> getChildren() {
        return children;
    }

    public void setChildren(List<MetaDataPojo> children) {
        this.children = children;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    public String getIconCls() {
        return iconCls;
    }

    public void setIconCls(String iconCls) {
        this.iconCls = iconCls;
    }
}
