package com.city.resourcecategory.analysis.text.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by wxl on 2016/3/14.
 * 分析主题
 */
@Entity
@Table(name = "RC_TEXT_THEME")
public class TextTheme implements Serializable {
    //根据分析日期排序
    public static final String SORT_BY_DATE = "analysisDate desc";
    //根据索引排序
    public static final String SORT_BY_INDEX = "sortIndex";

    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "rcTextTheme", sequenceName = "RC_TEXT_THEME_SEQ")
    @GeneratedValue(generator = "rcTextTheme")
    private Integer id;
    @Column(name = "NAME")
    private String name;//主题名
    @Column(name = "MODEL_ID")
    private Integer modelId;//说明
    @Column(name = "INFOS", length = 1000)
    private String infos;
    @Column(name = "SORT_INDEX")
    private Integer sortIndex;//排序
    @Column(name = "CONTENT_SORT_TYPE")
    private String contentSortType;//分析内容排序方式
    @Column
    private Integer creator;//创建人
    @Column(name = "CREATE_TIME")
    private Date createTime;//创建时间
    @Column
    private Integer updator;//更新人
    @Column(name = "UPDATE_TIME")
    private Date updateTime;//更新时间
    @Transient
    private Integer unChecked;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getModelId() {
        return modelId;
    }

    public void setModelId(Integer modelId) {
        this.modelId = modelId;
    }

    public String getInfos() {
        return infos;
    }

    public void setInfos(String infos) {
        this.infos = infos;
    }

    public Integer getSortIndex() {
        return sortIndex;
    }

    public void setSortIndex(Integer sortIndex) {
        this.sortIndex = sortIndex;
    }

    public String getContentSortType() {
        return contentSortType;
    }

    public void setContentSortType(String contentSortType) {
        this.contentSortType = contentSortType;
    }

    public Integer getCreator() {
        return creator;
    }

    public void setCreator(Integer creator) {
        this.creator = creator;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getUpdator() {
        return updator;
    }

    public void setUpdator(Integer updator) {
        this.updator = updator;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getUnChecked() {
        return unChecked;
    }

    public void setUnChecked(Integer unChecked) {
        this.unChecked = unChecked;
    }

}
