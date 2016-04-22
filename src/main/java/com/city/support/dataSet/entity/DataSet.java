package com.city.support.dataSet.entity;

import javax.persistence.*;

/**
 * Created by wxl on 2016/2/22.
 * 数据集实体
 */
@Entity
@Table(name = "SPT_DATASET")
public class DataSet {
    private Integer id;
    private String name;
    private String comments;//备注说明
    //基础集标识
    private boolean baseFlag;
    //扩展集标识
    private boolean expandFlag;

    public DataSet() {
    }

    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "dataSetGen", sequenceName = "SPT_DATASET_SEQ")
    @GeneratedValue(generator = "dataSetGen")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "NAME", unique = true, nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "COMMENTS")
    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @Transient
    public boolean isBaseFlag() {
        return baseFlag;
    }

    public void setBaseFlag(boolean baseFlag) {
        this.baseFlag = baseFlag;
    }

    @Transient
    public boolean isExpandFlag() {
        return expandFlag;
    }

    public void setExpandFlag(boolean expandFlag) {
        this.expandFlag = expandFlag;
    }
}
