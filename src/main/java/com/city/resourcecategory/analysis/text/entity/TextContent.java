package com.city.resourcecategory.analysis.text.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by wxl on 2016/3/14.
 * 分析内容
 */
@Entity
@Table(name = "RC_TEXT_CONTENT")
public class TextContent implements Serializable {
    @Id
    @Column
    @SequenceGenerator(name = "rcTextContent", sequenceName = "RC_TEXT_CONTENT_SEQ")
    @GeneratedValue(generator = "rcTextContent")
    private Integer id;
    @Column(nullable = false)
    private String name;
    @OneToOne
    @JoinColumn(name = "THEME_ID")
    private TextTheme theme;
    @Column
    private Integer status;//状态: 待审,发布,驳回
    @Column
    private Integer type;//普通分析
    @Column(columnDefinition = "CLOB")
    private String content;//分析内容
    @Column(name = "INFOS", length = 1000)
    private String infos;
    @Column(name = "ANALYSIS_DATE")
    private Date analysisDate;//分析日期
    @Column(name = "SORT_INDEX")
    private Integer sortIndex;
    @Column
    private Integer creator;//创建人
    @Column(name = "CREATE_TIME")
    private Date createTime;//创建时间
    @Column
    private Integer updator;//更新人
    @Column(name = "UPDATE_TIME")
    private Date updateTime;//更新时间
    @Transient
    private String labelIds;

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

    public TextTheme getTheme() {
        return theme;
    }

    public void setTheme(TextTheme theme) {
        this.theme = theme;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getInfos() {
        return infos;
    }

    public void setInfos(String infos) {
        this.infos = infos;
    }

    public Date getAnalysisDate() {
        return analysisDate;
    }

    public void setAnalysisDate(Date analysisDate) {
        this.analysisDate = analysisDate;
    }

    public Integer getSortIndex() {
        return sortIndex;
    }

    public void setSortIndex(Integer sortIndex) {
        this.sortIndex = sortIndex;
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

    public String getLabelIds() {
        return labelIds;
    }

    public void setLabelIds(String labelIds) {
        this.labelIds = labelIds;
    }
}
