package com.city.support.regime.collection.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Created by wgx on 2016/2/16.
 */
@Embeddable
public class ReportDataId implements java.io.Serializable  {
    /**
     * 时间框架
     */
    private Integer timeFrame;
    /**
     * 指标
     */
    private Integer item;
    /**
     * 指标分组目录
     */
    private String itemDict;
    /**
     * 指标口径
     */
    private Integer itemCaliber;
    /**
     * 统计对象
     */
    private Integer surobj;
    /**
     * 统计对象类型
     */
    private Integer surobjType;
    /**
     * 来源部门
     */
    private Integer depId;
    /**
     * 地区
     */
    private Integer areaId;
    /**
     * 时间
     */
    private Integer year;
    private Integer month;
    /**
     * 报表id
     */
    private Integer rptId;

    public ReportDataId() {
    }

    /**
     *
     * @param timeFrame   时间框架
     * @param item         指标
     * @param itemCaliber 指标口径
     * @param surobj       统计对象
     * @param depId        来源部门
     * @param areaId       地区
     * @param year         年
     * @param month        月
     * @param rptId        报表id
     */

    public ReportDataId(Integer timeFrame, Integer item,String itemDict, Integer itemCaliber, Integer surobj,Integer surobjType, Integer depId, Integer areaId, Integer year,Integer month, Integer rptId) {
        this.timeFrame = timeFrame;
        this.item = item;
        this.itemDict = itemDict;
        this.itemCaliber = itemCaliber;
        this.surobj = surobj;
        this.surobjType = surobjType;
        this.depId = depId;
        this.areaId = areaId;
        this.year =year;
        this.month =month;
        this.rptId = rptId;
    }

    @Column(name = "TIMEFRAME")
    public Integer getTimeFrame() {
        return timeFrame;
    }

    public void setTimeFrame(Integer timeFrame) {
        this.timeFrame = timeFrame;
    }

    @Column(name = "ITEM")
    public Integer getItem() {
        return item;
    }

    public void setItem(Integer item) {
        this.item = item;
    }

    @Column(name = "ITEMDICT")
    public String getItemDict() {
        return itemDict;
    }

    public void setItemDict(String itemDict) {
        this.itemDict = itemDict;
    }

    @Column(name = "ITEMCALIBER")
    public Integer getItemCaliber() {
        return itemCaliber;
    }

    public void setItemCaliber(Integer itemCaliber) {
        this.itemCaliber = itemCaliber;
    }

    @Column(name = "SUROBJ")
    public Integer getSurobj() {
        return surobj;
    }

    public void setSurobj(Integer surobj) {
        this.surobj = surobj;
    }
    @Column(name = "SUROBJTYPE")
    public Integer getSurobjType() {
        return surobjType;
    }

    public void setSurobjType(Integer surobjType) {
        this.surobjType = surobjType;
    }

    @Column(name = "DEPID")
    public Integer getDepId() {
        return depId;
    }

    public void setDepId(Integer depId) {
        this.depId = depId;
    }
    @Column(name = "AREAID")
    public Integer getAreaId() {
        return areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }

    public Integer getYear() {
        return year;
    }
    @Column(name = "YEAR")
    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }
    @Column(name = "MONTH")
    public void setMonth(Integer month) {
        this.month = month;
    }

    @Column(name = "RPTID")
    public Integer getRptId() {
        return rptId;
    }

    public void setRptId(Integer rptId) {
        this.rptId = rptId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReportDataId that = (ReportDataId) o;

        if (timeFrame != null ? !timeFrame.equals(that.timeFrame) : that.timeFrame != null) return false;
        if (item != null ? !item.equals(that.item) : that.item != null) return false;
        if (itemDict != null ? !itemDict.equals(that.itemDict) : that.itemDict != null) return false;
        if (itemCaliber != null ? !itemCaliber.equals(that.itemCaliber) : that.itemCaliber != null) return false;
        if (surobj != null ? !surobj.equals(that.surobj) : that.surobj != null) return false;
        if (surobjType != null ? !surobjType.equals(that.surobjType) : that.surobjType != null) return false;
        if (depId != null ? !depId.equals(that.depId) : that.depId != null) return false;
        if (areaId != null ? !areaId.equals(that.areaId) : that.areaId != null) return false;
        if (year != null ? !year.equals(that.year) : that.year != null) return false;
        if (month != null ? !month.equals(that.month) : that.month != null) return false;
        return !(rptId != null ? !rptId.equals(that.rptId) : that.rptId != null);

    }

    @Override
    public int hashCode() {
        int result = timeFrame != null ? timeFrame.hashCode() : 0;
        result = 31 * result + (item != null ? item.hashCode() : 0);
        result = 31 * result + (itemDict != null ? itemDict.hashCode() : 0);
        result = 31 * result + (itemCaliber != null ? itemCaliber.hashCode() : 0);
        result = 31 * result + (surobj != null ? surobj.hashCode() : 0);
        result = 31 * result + (surobjType != null ? surobjType.hashCode() : 0);
        result = 31 * result + (depId != null ? depId.hashCode() : 0);
        result = 31 * result + (areaId != null ? areaId.hashCode() : 0);
        result = 31 * result + (year != null ? year.hashCode() : 0);
        result = 31 * result + (month != null ? month.hashCode() : 0);
        result = 31 * result + (rptId != null ? rptId.hashCode() : 0);
        return result;
    }
}
