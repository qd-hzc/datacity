package com.city.resourcecategory.analysis.report.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * 自定义查询模板的表样
 * Created by HZC on 2016/2/26.
 */
@Entity
@Table(name = "RC_REPORT_CR_STYLE")
public class CustomResearchStyleEntity {
    //    id
    @Id
    @Column(name = "ID", unique = true)
    @GeneratedValue(generator = "generator")
    @GenericGenerator(name = "generator", strategy = "increment")
    private Integer id;
    @Column(name = "CUSTOM_RESEARCH_ID", unique = true)
    private Integer customResearchId;
    //    表样信息
    @Column(name = "RPT_STYLE", columnDefinition = "clob")
    private String rptStyle;
    //    表样设计信息
    @Column(name = "DESIGN_STYLE", columnDefinition = "clob")
    private String designStyle;
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
     * Getter for property 'rptStyle'.
     *
     * @return Value for property 'rptStyle'.
     */
    public String getRptStyle() {
        return rptStyle;
    }

    /**
     * Setter for property 'rptStyle'.
     *
     * @param rptStyle Value to set for property 'rptStyle'.
     */
    public void setRptStyle(String rptStyle) {
        this.rptStyle = rptStyle;
    }

    /**
     * Getter for property 'designStyle'.
     *
     * @return Value for property 'designStyle'.
     */
    public String getDesignStyle() {
        return designStyle;
    }

    /**
     * Setter for property 'designStyle'.
     *
     * @param designStyle Value to set for property 'designStyle'.
     */
    public void setDesignStyle(String designStyle) {
        this.designStyle = designStyle;
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