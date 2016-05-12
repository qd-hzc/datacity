package com.city.resourcecategory.analysis.chart.entity;

import javax.persistence.*;
import java.util.List;

/**
 * Created by wys on 2016/2/29.
 */
@Entity
@Table(name = "RC_ANALYSIS_CHARTSTRUCTURE")
public class AnalysisChartStructure {
    private Integer id;
    private Integer chartId;
    private String metaName;
    private Integer metaId;
    private Integer metaType;
    private String metaExt;
    private Integer structureType;
    private Integer parentId;
    private Integer structureSort;
    private Integer realNode;
    //    private AnalysisChartInfo analysisChartInfo=new AnalysisChartInfo();
    private Boolean leaf;

    private String text;

    @Transient
    public String getText() {
        return metaName;
    }

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

    @Column(name = "CHART_ID")
    public Integer getChartId() {
        return chartId;
    }

    public void setChartId(Integer chartId) {
        this.chartId = chartId;
    }

    @Column(name = "CHARTNAME")
    public String getMetaName() {
        return metaName;
    }

    public void setMetaName(String metaName) {
        this.metaName = metaName;
        this.text = metaName;
    }

    @Column(name = "META_ID")
    public Integer getMetaId() {
        return metaId;
    }

    public void setMetaId(Integer metaId) {
        this.metaId = metaId;
    }

    @Column(name = "METATYPE")
    public Integer getMetaType() {
        return metaType;
    }

    public void setMetaType(Integer metaType) {
        this.metaType = metaType;
    }

    @Column(name = "METAEXT")
    public String getMetaExt() {
        return metaExt;
    }

    public void setMetaExt(String metaExt) {
        this.metaExt = metaExt;
    }

    @Column(name = "STRUCTURETYPE")
    public Integer getStructureType() {
        return structureType;
    }

    public void setStructureType(Integer structureType) {
        this.structureType = structureType;
    }

    @Column(name = "PARENT_ID")
    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

/*    @OneToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @OneToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name = "CHARTINFO_ID")
    public AnalysisChartInfo getAnalysisChartInfo() {
        return analysisChartInfo;
    }

    public void setAnalysisChartInfo(AnalysisChartInfo analysisChartInfo) {
        this.analysisChartInfo = analysisChartInfo;
    }*/

    @Column(name = "STURCTURE_SORT")
    public Integer getStructureSort() {
        return structureSort;
    }

    public void setStructureSort(Integer structureSort) {
        this.structureSort = structureSort;
    }

    @Column(name = "REALNODE")
    public Integer getRealNode() {
        return realNode;
    }

    public void setRealNode(Integer realNode) {

        this.realNode = realNode;
    }

    @Column(name = "LEAF")
    public Boolean getLeaf() {
        return leaf;
    }

    public void setLeaf(Boolean leaf) {
        this.leaf = leaf;
    }
}
