package com.city.resourcecategory.analysis.report.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * 自定义查询分组
 * Created by HZC on 2016/2/19.
 */
@Entity
@Table(name = "RC_REPORT_RESEARCH_GROUP")
public class ResearchGroupEntity {

    //    主键
    @Id
    @Column(name = "ID", unique = true)
    @GeneratedValue(generator = "generator")
    @GenericGenerator(name = "generator", strategy = "increment")
    private Integer id;
    //    父id
    @Column(name = "PARENT_ID", nullable = false)
    private Integer parentId;
    //    是否为叶子节点
    @Column(name = "LEAF", nullable = false)
    private boolean leaf;
    //    名称
    @Column(name = "NAME", nullable = false)
    private String name;
    @Column(name = "SORT", nullable = false)
    private Integer sort;
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
     * Getter for property 'parentId'.
     *
     * @return Value for property 'parentId'.
     */
    public Integer getParentId() {
        return parentId;
    }

    /**
     * Setter for property 'parentId'.
     *
     * @param parentId Value to set for property 'parentId'.
     */
    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    /**
     * Getter for property 'leaf'.
     *
     * @return Value for property 'leaf'.
     */
    public boolean isLeaf() {
        return leaf;
    }

    /**
     * Setter for property 'leaf'.
     *
     * @param leaf Value to set for property 'leaf'.
     */
    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    /**
     * Getter for property 'sort'.
     *
     * @return Value for property 'sort'.
     */
    public Integer getSort() {
        return sort;
    }

    /**
     * Setter for property 'sort'.
     *
     * @param sort Value to set for property 'sort'.
     */
    public void setSort(Integer sort) {
        this.sort = sort;
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
