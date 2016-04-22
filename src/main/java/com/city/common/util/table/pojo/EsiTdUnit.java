package com.city.common.util.table.pojo;

import java.io.Serializable;

/**
 * Created by wxl on 2016/1/28 0028.
 * 单元格单位
 */
public class EsiTdUnit implements Serializable {
    //单位
    private Integer unitId;
    //单位名
    private String unitName;
    //数据格式
    private String dataFormat;

    public Integer getUnitId() {
        return unitId;
    }

    public void setUnitId(Integer unitId) {
        this.unitId = unitId;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getDataFormat() {
        return dataFormat;
    }

    public void setDataFormat(String dataFormat) {
        this.dataFormat = dataFormat;
    }
}
