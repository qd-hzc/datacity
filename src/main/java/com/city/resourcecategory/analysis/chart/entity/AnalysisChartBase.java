package com.city.resourcecategory.analysis.chart.entity;

import javax.persistence.*;

/**
 * Created by wys on 2016/2/23.
 */
@Entity
@Table(name = "RC_ANALYSIS_CHARTBASE")
public class AnalysisChartBase {
    private Integer id;
    private String title;
    private String subTitle;
    private Integer chartType;
    private Integer periodType;
    private String timeline;
    private String plug;
    private String chartStyle;
    private Integer groupId;
    private Integer chartSort;
    @Transient
    private String name;

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

    @Column(name = "TITLE")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(name = "SUBTITLE")
    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    @Column(name = "CHART_TYPE")
    public Integer getChartType() {
        return chartType;
    }

    public void setChartType(Integer chartType) {
        this.chartType = chartType;
    }

    @Column(name = "PERIOD_TYPE")
    public Integer getPeriodType() {
        return periodType;
    }

    public void setPeriodType(Integer periodType) {
        this.periodType = periodType;
    }

    @Column(name = "TIMELINE")
    public String getTimeline() {
        return timeline;
    }

    public void setTimeline(String timeline) {
        this.timeline = timeline;
    }

    @Column(name = "PLUG")
    public String getPlug() {
        return plug;
    }

    public void setPlug(String plug) {
        this.plug = plug;
    }

    @Column(name = "CHART_STYLE")
    public String getChartStyle() {
        return chartStyle;
    }

    public void setChartStyle(String chartStyle) {
        this.chartStyle = chartStyle;
    }

    @Column(name = "GROUP_ID")
    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }
    @Column(name = "CHART_SORT")
    public Integer getChartSort() {
        return chartSort;
    }

    public void setChartSort(Integer chartSort) {
        this.chartSort = chartSort;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
