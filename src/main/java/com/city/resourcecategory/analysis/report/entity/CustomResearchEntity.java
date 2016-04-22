package com.city.resourcecategory.analysis.report.entity;

import com.city.support.dataSet.entity.DataSet;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * 自定义查询模板
 * Created by HZC on 2016/2/19.
 */
@Entity
@Table(name = "RC_REPORT_CUSTOM_RESEARCH")
public class CustomResearchEntity {
    //    id
    @Id
    @Column(name = "ID", unique = true)
    @SequenceGenerator(name = "RC_REPORT_CUSTOM_RESEARCH_GEN", sequenceName = "RC_REPORT_CUSTOM_RESEARCH_SEQ")
    @GeneratedValue(generator = "RC_REPORT_CUSTOM_RESEARCH_GEN")
    private Integer id;
    //    自定义查询分组id
    @Column(name = "RESEARCH_GROUP_ID", nullable = false)
    private Integer researchGroupId;
    //    名称
    @Column(name = "NAME", nullable = false)
    private String name;
    //    数据源
    @OneToOne
    @JoinColumn(name = "RESOURCE_ID")
    private DataSet dataSet;
    //    说明
    @Column(name = "COMMENTS")
    private String comments;
    //    类型：1：自定义查询
    @Column(name = "TYPE", nullable = false)
    private Integer type;
    //    频度：年，半年，季，月
    @Column(name = "PERIOD")
    private Integer period;
    //    状态
    @Column(name = "STATUS", nullable = false)
    private Integer status;

    @Column(name = "CREATE_DATE", nullable = false, updatable = false)
    private Date createDate;
    @Column(name = "CREATOR_ID", nullable = false, updatable = false)
    private Integer creatorId;
    @Column(name = "UPDATE_DATE")
    private Date updateDate;
    @Column(name = "UPDATER_ID")
    private Integer updaterId;

    /**
     * Getter for property 'id'.
     *
     * @return Value for property 'id'.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Setter for property 'id'.
     *
     * @param id Value to set for property 'id'.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Getter for property 'researchGroupId'.
     *
     * @return Value for property 'researchGroupId'.
     */
    public Integer getResearchGroupId() {
        return researchGroupId;
    }

    /**
     * Setter for property 'researchGroupId'.
     *
     * @param researchGroupId Value to set for property 'researchGroupId'.
     */
    public void setResearchGroupId(Integer researchGroupId) {
        this.researchGroupId = researchGroupId;
    }

    /**
     * Getter for property 'name'.
     *
     * @return Value for property 'name'.
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for property 'name'.
     *
     * @param name Value to set for property 'name'.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for property 'comments'.
     *
     * @return Value for property 'comments'.
     */
    public String getComments() {
        return comments;
    }

    /**
     * Setter for property 'comments'.
     *
     * @param comments Value to set for property 'comments'.
     */
    public void setComments(String comments) {
        this.comments = comments;
    }

    /**
     * Getter for property 'type'.
     *
     * @return Value for property 'type'.
     */
    public Integer getType() {
        return type;
    }

    /**
     * Setter for property 'type'.
     *
     * @param type Value to set for property 'type'.
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * Getter for property 'period'.
     *
     * @return Value for property 'period'.
     */
    public Integer getPeriod() {
        return period;
    }

    /**
     * Setter for property 'period'.
     *
     * @param period Value to set for property 'period'.
     */
    public void setPeriod(Integer period) {
        this.period = period;
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

    /**
     * Getter for property 'dataSet'.
     *
     * @return Value for property 'dataSet'.
     */
    public DataSet getDataSet() {
        return dataSet;
    }

    /**
     * Setter for property 'dataSet'.
     *
     * @param dataSet Value to set for property 'dataSet'.
     */
    public void setDataSet(DataSet dataSet) {
        this.dataSet = dataSet;
    }
}