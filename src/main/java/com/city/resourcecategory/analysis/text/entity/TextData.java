package com.city.resourcecategory.analysis.text.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by wxl on 2016/3/14.
 * 分析主题和分析内容所关联的数据
 */
@Entity
@Table(name = "RC_TEXT_DATA")
public class TextData implements Serializable {
    @Id
    @Column
    @SequenceGenerator(name = "rcTextData", sequenceName = "RC_TEXT_DATA_SEQ")
    @GeneratedValue(generator = "rcTextData")
    private Integer id;
    @Column(name = "DATA_NAME")
    private String dataName;
    @Column(name = "DATA_TYPE")
    private Integer dataType;
    @Column(name = "DATA_VALUE")
    private Integer dataValue;
    @Column(name = "FOREIGN_TYPE")
    private Integer foreignType;//外键类型
    @Column(name = "FOREIGN_ID")
    private Integer foreignId;//外键值

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

    public Integer getForeignType() {
        return foreignType;
    }

    public void setForeignType(Integer foreignType) {
        this.foreignType = foreignType;
    }

    public Integer getForeignId() {
        return foreignId;
    }

    public void setForeignId(Integer foreignId) {
        this.foreignId = foreignId;
    }
}
