package com.city.resourcecategory.analysis.text.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by wxl on 2016/3/14.
 * 分析模版
 */
@Entity
@Table(name = "RC_TEXT_MODEL")
public class TextModel implements Serializable {
    public static final int ENABLED = 1;//启用
    public static final int DISABLED = 1;//弃用
    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "rcTextModel", sequenceName = "RC_TEXT_MODEL_SEQ")
    @GeneratedValue(generator = "rcTextModel")
    private Integer id;
    @Column(name = "NAME", unique = true)
    private String name;//模板名
    @Column(name = "CONTENT", columnDefinition = "CLOB")
    private String content;//模板内容
    @Column(name = "STATUS")
    private Integer status;//模板状态
    @Column
    private Integer creator;//创建人
    @Column(name = "CREATE_TIME")
    private Date createTime;//创建时间
    @Column
    private Integer updator;//更新人
    @Column(name = "UPDATE_TIME")
    private Date updateTime;//更新时间

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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
}
