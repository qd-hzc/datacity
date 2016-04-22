package com.city.support.regime.collection.entity;

import javax.persistence.*;

/**
 * Created by wgx on 2016/2/15.
 */
@Entity
@Table(name="SPT_RGM_RPT_DATA")
public class ReportData {
    /**
     * 联合主键
     */
    private ReportDataId reportDataId;
    /**
     * 指标体系
     */
    private Integer itemgroup;
    /**
     * 统计对象分组
     */
    private Integer surobjgroup;
    /**
     * 时间
     */
    private String time;
    /**
     * 值
     */
    private String itemValue;
    /**
     * 单元格(加工表)
     */
    private String cell;
    /**
     * 模板id
     */
    private Integer rptTmpId;
    /**
     * 单位id
     */
    private Integer unitId;
    /**
     * 单位
     */
    private String unit;
    /**
     * 数据类型(数字、文本等)
     */
    private Integer dataType;
    /**
     * 数据格式()
     */
    private String dataFormat;
    /**
     * 数据状态(可用、不可用)
     */
    private Integer dataStatus;
    /**
     * 采集类型(计算、填报、抽取、导入)
     */
    private Integer collectionType;
    /**
     * 说明
     */
    private String explain;
    /**
     * 冗余字段
     */
    private String r1;
    private String r2;
    private String r3;

    @EmbeddedId
    @AttributeOverrides(
            {
                    @AttributeOverride(name = "timeframe", column =@Column(name = "TIMEFRAME") ),
                    @AttributeOverride(name = "item", column =@Column(name = "ITEM") ),
                    @AttributeOverride(name = "itemdict", column =@Column(name = "ITEMDICT") ),
                    @AttributeOverride(name = "itemcaliber", column =@Column(name = "ITEMCALIBER") ),
                    @AttributeOverride(name = "surobj", column =@Column(name = "SUROBJ") ),
                    @AttributeOverride(name = "surobjType", column =@Column(name = "SUROBJTYPE") ),
                    @AttributeOverride(name = "depId", column =@Column(name = "DEPID") ),
                    @AttributeOverride(name = "areaId", column =@Column(name = "AREAID") ),
                    @AttributeOverride(name = "year", column =@Column(name = "YEAR") ),
                    @AttributeOverride(name = "month", column =@Column(name = "MONTH") ),
                    @AttributeOverride(name = "rptId", column =@Column(name = "RPTID") )
            })
    public ReportDataId getReportDataId() {
        return reportDataId;
    }

    public void setReportDataId(ReportDataId reportDataId) {
        this.reportDataId = reportDataId;
    }
    @Column(name = "ITEMGROUP")
    public Integer getItemgroup() {
        return itemgroup;
    }

    public void setItemgroup(Integer itemgroup) {
        this.itemgroup = itemgroup;
    }
    @Column(name = "SUROBJGROUP")
    public Integer getSurobjgroup() {
        return surobjgroup;
    }

    public void setSurobjgroup(Integer surobjgroup) {
        this.surobjgroup = surobjgroup;
    }
    @Column(name = "TIME")
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }

    @Column(name = "ITEMVALUE")
    public String getItemValue() {
        return itemValue;
    }

    public void setItemValue(String itemValue) {
        this.itemValue = itemValue;
    }
    @Column(name = "CELL")
    public String getCell() {
        return cell;
    }

    public void setCell(String cell) {
        this.cell = cell;
    }
    @Column(name = "UNIT")
    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
    @Column(name = "DATATYPE")
    public Integer getDataType() {
        return dataType;
    }

    public void setDataType(Integer dataType) {
        this.dataType = dataType;
    }
    @Column(name = "DATAFORMAT")
    public String getDataFormat() {
        return dataFormat;
    }

    public void setDataFormat(String dataFormat) {
        this.dataFormat = dataFormat;
    }
    @Column(name = "DATASTATUS")
    public Integer getDataStatus() {
        return dataStatus;
    }

    public void setDataStatus(Integer dataStatus) {
        this.dataStatus = dataStatus;
    }
    @Column(name = "COLLECTIONTYPE")
    public Integer getCollectionType() {
        return collectionType;
    }

    public void setCollectionType(Integer collectionType) {
        this.collectionType = collectionType;
    }
    @Column(name = "EXPLAIN",length =1000)
    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    @Column(name = "UNIT_ID")
    public Integer getUnitId() {
        return unitId;
    }
    public void setUnitId(Integer unitId) {
        this.unitId = unitId;
    }
    @Column(name = "RPT_TMP_ID")
    public Integer getRptTmpId() {
        return rptTmpId;
    }

    public void setRptTmpId(Integer rptTmpId) {
        this.rptTmpId = rptTmpId;
    }
    @Column(name = "R1")
    public String getR1() {
        return r1;
    }

    public void setR1(String r1) {
        this.r1 = r1;
    }
    @Column(name = "R2")
    public String getR2() {
        return r2;
    }

    public void setR2(String r2) {
        this.r2 = r2;
    }
    @Column(name = "R3")
    public String getR3() {
        return r3;
    }

    public void setR3(String r3) {
        this.r3 = r3;
    }
}
