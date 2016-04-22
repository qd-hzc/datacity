package com.city.support.regime.collection.pojo;

import com.city.support.regime.collection.entity.ReportDataId;

import java.util.Map;

/**
 * Created by wgx on 2016/3/1.
 * 获取hmtl报表信息用来存到数据库
 */
public class RptHtmlPojo {
    private Integer rptId;
    private Map<String, String> map;
    private ReportDataId reportDataId;
    private String time;
    private String value;
    private Integer tmpId;
    private Integer unitId;
    private String unit;
    private String dataFormat;
    private Integer dataType;
    private Integer collectionType;
    private String explain;

    public RptHtmlPojo() {
    }

    public RptHtmlPojo(Integer rptId,Integer tmpId, Integer dataType, Integer collectionType,String time, String explain) {
        this.rptId = rptId;
        this.dataType = dataType;
        this.collectionType = collectionType;
        this.explain = explain;
        this.time = time;
        this.tmpId = tmpId;
    }

    public Integer getRptId() {
        return rptId;
    }

    public void setRptId(Integer rptId) {
        this.rptId = rptId;
    }

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    public ReportDataId getReportDataId() {
        return reportDataId;
    }

    public void setReportDataId(ReportDataId reportDataId) {
        this.reportDataId = reportDataId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getTmpId() {
        return tmpId;
    }

    public void setTmpId(Integer tmpId) {
        this.tmpId = tmpId;
    }

    public Integer getUnitId() {
        return unitId;
    }

    public void setUnitId(Integer unitId) {
        this.unitId = unitId;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getDataFormat() {
        return dataFormat;
    }

    public void setDataFormat(String dataFormat) {
        this.dataFormat = dataFormat;
    }

    public Integer getDataType() {
        return dataType;
    }

    public void setDataType(Integer dataType) {
        this.dataType = dataType;
    }

    public Integer getCollectionType() {
        return collectionType;
    }

    public void setCollectionType(Integer collectionType) {
        this.collectionType = collectionType;
    }
}
