package com.city.resourcecategory.analysis.chart.entity;

import javax.persistence.*;

/**
 * Created by wys on 2016/2/29.
 */
@Entity
@Table(name = "RC_ANALYSIS_CHARTINFO")
public class AnalysisChartInfo {
    private Integer id;
    private String name;
    private Integer chartId;
    private Integer infoType;
    private Integer chartType;
    private String group;
    private Integer isShow;
    private Integer axis;
    private String info;
    private String style;
    private Integer structureId;
    private Integer infoSort;
    private Integer metaType;//冗余字段

    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "analysisChart", sequenceName = "RC_ANALYSIS_CHART_SEQ")
    @GeneratedValue(generator = "analysisChart")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "CHART_ID")
    public Integer getChartId() {
        return chartId;
    }

    public void setChartId(Integer chartId) {
        this.chartId = chartId;
    }

    @Column(name = "INFOTYPE")
    public Integer getInfoType() {
        return infoType;
    }

    public void setInfoType(Integer infoType) {
        this.infoType = infoType;
    }

    @Column(name = "CHARTTYPE")
    public Integer getChartType() {
        return chartType;
    }

    public void setChartType(Integer chartType) {
        this.chartType = chartType;
    }

    @Column(name = "INFOGROUP")
    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @Column(name = "ISSHOW")
    public Integer getIsShow() {
        return isShow;
    }

    public void setIsShow(Integer isShow) {
        this.isShow = isShow;
    }

    @Column(name = "AXIS")
    public Integer getAxis() {
        return axis;
    }

    public void setAxis(Integer axis) {
        this.axis = axis;
    }

    @Column(name = "CHART_INFO")
    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Column(name = "STYLE")
    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    @Column(name = "STRUCTURE_ID")
    public Integer getStructureId() {
        return structureId;
    }

    public void setStructureId(Integer structureId) {
        this.structureId = structureId;
    }

    @Column(name = "INFO_SORT")
    public Integer getInfoSort() {
        return infoSort;
    }

    public void setInfoSort(Integer infoSort) {
        this.infoSort = infoSort;
    }

    @Column(name = "METATYPE")
    public Integer getMetaType() {
        return metaType;
    }

    public void setMetaType(Integer metaType) {
        this.metaType = metaType;
    }
}
