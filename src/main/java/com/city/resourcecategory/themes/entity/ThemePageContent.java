package com.city.resourcecategory.themes.entity;

import javax.persistence.*;

/**
 * Created by wxl on 2016/3/10.
 * 主题页面内容
 */
@Entity
@Table(name = "RC_THEME_PAGE_CONTENT")
public class ThemePageContent {
    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "rcThemePageContent", sequenceName = "RC_THEME_PAGE_CONTENT_SEQ")
    @GeneratedValue(generator = "rcThemePageContent")
    private Integer id;
    @Column(name = "THEME_PAGE_ID")
    private Integer themePageId;//主题配置页面id
    @Column(name = "CONTAINER_ID")
    private String containerId;//要渲染的dom id
    @Column(name = "CONTAINER_NAME")
    private Integer containerName;//要渲染的dom 显示名
    @Column(name = "CONTENT_TYPE")
    private Integer contentType;//要加载到dom的内容类型
    @Column(name = "CONTENT_VALUE")
    private String contentValue;//要加载到dom的内容
    @Column(name = "EXTRA_PARAMS")
    private String extraParams;//加载内容的额外参数

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getThemePageId() {
        return themePageId;
    }

    public void setThemePageId(Integer themePageId) {
        this.themePageId = themePageId;
    }

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public Integer getContainerName() {
        return containerName;
    }

    public void setContainerName(Integer containerName) {
        this.containerName = containerName;
    }

    public Integer getContentType() {
        return contentType;
    }

    public void setContentType(Integer contentType) {
        this.contentType = contentType;
    }

    public String getContentValue() {
        return contentValue;
    }

    public void setContentValue(String contentValue) {
        this.contentValue = contentValue;
    }

    public String getExtraParams() {
        return extraParams;
    }

    public void setExtraParams(String extraParams) {
        this.extraParams = extraParams;
    }
}
