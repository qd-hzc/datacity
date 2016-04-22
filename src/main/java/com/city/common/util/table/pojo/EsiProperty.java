package com.city.common.util.table.pojo;

import java.io.Serializable;

/**
 * Created by wxl on 2016/1/26 0026.
 */
public class EsiProperty implements Serializable {
    //数据类型
    private Integer dataType;
    //数据名
    private String dataName;
    //数据值
    private Integer dataValue;
    //扩充字段
    //元数据为指标时,代表指标口径
    //源数据为调查对象时,代表调查对象类型
    //源数据为时间时,代表年或期度
    private String dataInfo1;
    //源数据为指标时,代表默认部门
    //源数据为时间时,代表
    private String dataInfo2;
    //是否为继承的属性
    private boolean extend;

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

    public boolean isExtend() {
        return extend;
    }

    public void setExtend(boolean extend) {
        this.extend = extend;
    }
}
