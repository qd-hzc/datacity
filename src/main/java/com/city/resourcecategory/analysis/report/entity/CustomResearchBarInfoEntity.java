package com.city.resourcecategory.analysis.report.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * 自定义查询主宾栏信息
 * Created by HZC on 2016/2/19.
 */
@Entity
@Table(name = "RC_REPORT_CR_BAR_INFO")
public class CustomResearchBarInfoEntity {

    @Id
    @Column(name = "ID", unique = true)
    @GeneratedValue(generator = "generator")
    @GenericGenerator(name = "generator", strategy = "increment")
    private Integer id;
    //    父节点
    @Column(name = "PARENT_ID")
    private Integer parentId;
    //    关联CustomResearchEntity的外键,不使用映射
    @Column(name = "CUSTOM_RESEARCH_ID")
    private Integer customResearchId;
    //    元数据名
    @Column(name = "DATA_NAME")
    private String dataName;
    //    元数据类型
    @Column(name = "DATA_TYPE")
    private Integer dataType;
    //    元数据id
    @Column(name = "DATA_VALUE")
    private Integer dataValue;
    //    元数据扩展字段1
    //    如果为指标类型，则该字段为指标口径
    //    如果为统计对象类型,该字段表示统计对象类型
    @Column(name = "DATA_INFO1")
    private String dataInfo1;
    //    元数据扩展字段2
    //    如果为指标类型，则该字段为部门
    //    如果为统计对象类型,该字段表示地区
    @Column(name = "DATA_INFO2")
    private String dataInfo2;
    //    是否为主栏：1：主栏；0：宾栏
    @Column(name = "IS_MAIN_BAR")
    private Boolean isMainBar;
    //    排序
    @Column(name = "SORT_INDEX")
    private Integer sortIndex;
    //    是否为属性，1：是；0：否
    @Column(name = "IS_PROPERTY")
    private Boolean isProperty;
    //    是否为实节点
    @Column(name = "IS_REAL_NODE")
    private Boolean isRealNode;
    //    状态
    @Column(name = "STATUS")
    private Integer status;

    @Column(name = "CREATE_DATE")
    private Date createDate;
    @Column(name = "CREATE_ID")
    private Integer creatorId;
    @Column(name = "UPDATE_DATE")
    private Date updateDate;
    @Column(name = "UPDATE_ID")
    private Integer updaterId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public Integer getDataType() {
        return dataType;
    }

    public void setDataType(Integer dataType) {
        this.dataType = dataType;
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

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getSortIndex() {
        return sortIndex;
    }

    public void setSortIndex(Integer sortIndex) {
        this.sortIndex = sortIndex;
    }

    /**
     * Getter for property 'isMainBar'.
     *
     * @return Value for property 'isMainBar'.
     */
    public Boolean getIsMainBar() {
        return isMainBar;
    }

    /**
     * Setter for property 'isMainBar'.
     *
     * @param isMainBar Value to set for property 'isMainBar'.
     */
    public void setIsMainBar(Boolean isMainBar) {
        this.isMainBar = isMainBar;
    }

    /**
     * Getter for property 'isProperty'.
     *
     * @return Value for property 'isProperty'.
     */
    public Boolean getIsProperty() {
        return isProperty;
    }

    /**
     * Setter for property 'isProperty'.
     *
     * @param isProperty Value to set for property 'isProperty'.
     */
    public void setIsProperty(Boolean isProperty) {
        this.isProperty = isProperty;
    }

    /**
     * Getter for property 'isRealNode'.
     *
     * @return Value for property 'isRealNode'.
     */
    public Boolean getIsRealNode() {
        return isRealNode;
    }

    /**
     * Setter for property 'isRealNode'.
     *
     * @param isRealNode Value to set for property 'isRealNode'.
     */
    public void setIsRealNode(Boolean isRealNode) {
        this.isRealNode = isRealNode;
    }

    /**
     * Getter for property 'customResearchId'.
     *
     * @return Value for property 'customResearchId'.
     */
    public Integer getCustomResearchId() {
        return customResearchId;
    }

    /**
     * Setter for property 'customResearchId'.
     *
     * @param customResearchId Value to set for property 'customResearchId'.
     */
    public void setCustomResearchId(Integer customResearchId) {
        this.customResearchId = customResearchId;
    }

    /**
     * Getter for property 'status'.
     *
     * @return Value for property 'status'.
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * Setter for property 'status'.
     *
     * @param status Value to set for property 'status'.
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * Getter for property 'createDate'.
     *
     * @return Value for property 'createDate'.
     */
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * Setter for property 'createDate'.
     *
     * @param createDate Value to set for property 'createDate'.
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    /**
     * Getter for property 'creatorId'.
     *
     * @return Value for property 'creatorId'.
     */
    public Integer getCreatorId() {
        return creatorId;
    }

    /**
     * Setter for property 'creatorId'.
     *
     * @param creatorId Value to set for property 'creatorId'.
     */
    public void setCreatorId(Integer creatorId) {
        this.creatorId = creatorId;
    }

    /**
     * Getter for property 'updateDate'.
     *
     * @return Value for property 'updateDate'.
     */
    public Date getUpdateDate() {
        return updateDate;
    }

    /**
     * Setter for property 'updateDate'.
     *
     * @param updateDate Value to set for property 'updateDate'.
     */
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    /**
     * Getter for property 'updaterId'.
     *
     * @return Value for property 'updaterId'.
     */
    public Integer getUpdaterId() {
        return updaterId;
    }

    /**
     * Setter for property 'updaterId'.
     *
     * @param updaterId Value to set for property 'updaterId'.
     */
    public void setUpdaterId(Integer updaterId) {
        this.updaterId = updaterId;
    }
}
