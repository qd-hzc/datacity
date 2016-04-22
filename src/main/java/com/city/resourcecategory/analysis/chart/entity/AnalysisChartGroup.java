package com.city.resourcecategory.analysis.chart.entity;

import org.hibernate.annotations.IndexColumn;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wys on 2016/2/23.
 */
@Entity
@Table(name = "RC_ANALYSIS_CHARTGROUP")
public class AnalysisChartGroup {
    private Integer id;
    private String name;
    private Integer groupSort;
    private Integer pId;
    private List<AnalysisChartBase> analysisChartBases;
    private Boolean leaf;

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

    @Column(name = "GROUP_SORT")
    public Integer getGroupSort() {
        return groupSort;
    }

    public void setGroupSort(Integer groupSort) {
        this.groupSort = groupSort;
    }

    @Column(name = "PID")
    public Integer getpId() {
        return pId;
    }

    public void setpId(Integer pId) {
        this.pId = pId;
    }

    @OneToMany()
    @JoinColumn(name="GROUP_ID")
    @IndexColumn(name="CHART_SORT")
    public List<AnalysisChartBase> getAnalysisChartBases() {
        return analysisChartBases;
    }

    public void setAnalysisChartBases(List<AnalysisChartBase> analysisChartBases) {
        this.analysisChartBases = analysisChartBases;
    }

    public Boolean getLeaf() {
        return leaf;
    }

    public void setLeaf(Boolean leaf) {
        this.leaf = leaf;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AnalysisChartGroup that = (AnalysisChartGroup) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
