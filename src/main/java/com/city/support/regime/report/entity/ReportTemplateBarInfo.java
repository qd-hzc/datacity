package com.city.support.regime.report.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by wxl on 2016/1/13 0013.
 * 主宾栏信息
 */
@Entity
@Table(name = "SPT_RGM_RPT_TMPBARINFO")
public class ReportTemplateBarInfo implements Serializable {
    private Integer id;
    /**
     * 元数据名
     */
    private String dataName;
    /**
     * 元数据类型
     */
    private Integer dataType;
    /**
     * 元数据id
     */
    private Integer dataValue;
    /**
     * 元数据扩展字段1
     * 如果为指标类型，则该字段为指标口径
     * 如果为统计对象类型,该字段表示统计对象类型
     */
    private String dataInfo1;
    /**
     * 元数据扩展字段2
     * 如果为指标类型，则该字段为部门
     * 如果为统计对象类型,该字段表示地区
     */
    private String dataInfo2;
    /**
     * 是否为主栏：1：主栏；0：宾栏
     */
    private Integer isMainBar;
    /**
     * 父节点
     */
    private Integer parentId;
    /**
     * 排序
     */
    private Integer sortIndex;
    /**
     * 关联 ReportTemplateStyle的外键,不使用映射
     */
    private Integer styleId;

    /**
     * 存储模板的冗余字段
     */
    private Integer tmpId;

    /**
     * 是否为属性，1：是；0：否
     */

    private Integer isProperty;
    /**
     * 是否为实节点
     */
    private Integer isRealNode;

    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "reportTemplateBarInfoGen", sequenceName = "SPT_RGM_RPT_TMPBARINFO_SEQ")
    @GeneratedValue(generator = "reportTemplateBarInfoGen")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "DATA_NAME", nullable = false)
    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    @Column(name = "DATA_TYPE", nullable = false)
    public Integer getDataType() {
        return dataType;
    }

    public void setDataType(Integer dataType) {
        this.dataType = dataType;
    }

    @Column(name = "DATA_VALUE")
    public Integer getDataValue() {
        return dataValue;
    }

    public void setDataValue(Integer dataValue) {
        this.dataValue = dataValue;
    }

    @Column(name = "DATA_INFO1")
    public String getDataInfo1() {
        return dataInfo1;
    }

    public void setDataInfo1(String dataInfo1) {
        this.dataInfo1 = dataInfo1;
    }

    @Column(name = "DATA_INFO2")
    public String getDataInfo2() {
        return dataInfo2;
    }

    public void setDataInfo2(String dataInfo2) {
        this.dataInfo2 = dataInfo2;
    }

    @Column(name = "IS_MAIN_BAR", nullable = false)
    public Integer getIsMainBar() {
        return isMainBar;
    }

    public void setIsMainBar(Integer isMainBar) {
        this.isMainBar = isMainBar;
    }

    @Column(name = "PARENT_ID", nullable = false)
    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    @Column(name = "SORT_INDEX")
    public Integer getSortIndex() {
        return sortIndex;
    }

    public void setSortIndex(Integer sortIndex) {
        this.sortIndex = sortIndex;
    }

    @Column(name = "STYLE_ID", nullable = false)
    public Integer getStyleId() {
        return styleId;
    }

    public void setStyleId(Integer styleId) {
        this.styleId = styleId;
    }

    @Column(name = "TMP_ID", nullable = false)
    public Integer getTmpId() {
        return tmpId;
    }

    public void setTmpId(Integer tmpId) {
        this.tmpId = tmpId;
    }

    @Column(name = "IS_PROPERTY", nullable = false)
    public Integer getIsProperty() {
        return isProperty;
    }

    public void setIsProperty(Integer isProperty) {
        this.isProperty = isProperty;
    }

    @Column(name = "IS_REAL_NODE", columnDefinition = "number default 0")
    public Integer getIsRealNode() {
        return isRealNode;
    }

    public void setIsRealNode(Integer isRealNode) {
        this.isRealNode = isRealNode;
    }
}
