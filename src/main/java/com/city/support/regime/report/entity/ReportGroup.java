package com.city.support.regime.report.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * 综合表分组
 * Created by cobra on 2016/4/15.
 */
@Entity
@Table(name = "SPT_RGM_RPT_GROUP")
public class ReportGroup {

    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "reportGorupGen", sequenceName = "SPT_RGM_RPT_GROUP_SEQ")
    @GeneratedValue(generator = "reportGorupGen")
    private Integer id;
    @Column(name = "PARENT_ID")
    private Integer parentId;

    //    名称
    @Column(name = "NAME")
    private String name;
    //    排序
    @Column(name = "SORT")
    private Integer sort;
    //    节点
    @Column(name = "LEAF")
    private boolean leaf;
    //    说明
    @Column(name = "COMMENTS")
    private String comments;
    //    创建日期
    @Column(name = "CREATE_DATE")
    private Date createDate;
    //    创建者
    @Column(name = "CREATOR_ID")
    private Integer creatorId;
    //    状态:0：无效，1：有效
    @Column(name = "STATUS")
    private int status;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Integer getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Integer creatorId) {
        this.creatorId = creatorId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
