package com.city.support.manage.surobj.entity;

import com.city.support.manage.area.entity.SptMgrAreaEntity;

import javax.persistence.*;

/**
 * Created by wys on 2016/1/18.
 */
@Entity
@Table(name = "SPT_MGR_EXTSUROBJ")
public class ExtendSurveyObj {
    private Integer id;
    private String surObjName;
    private String surObjCode;
    private SptMgrAreaEntity surArea;
    private String surObjInfo;

    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "surObj", sequenceName = "SPT_MGR_SUROBJ_SEQ")
    @GeneratedValue(generator = "surObj")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "EXTSUROBJ_AREA_ID")
    public SptMgrAreaEntity getSurArea() {
        return surArea;
    }

    public void setSurArea(SptMgrAreaEntity surArea) {
        this.surArea = surArea;
    }

    @Column(name = "EXTSUROBJ_CODE")
    public String getSurObjCode() {
        return surObjCode;
    }

    public void setSurObjCode(String surObjCode) {
        this.surObjCode = surObjCode;
    }

    @Column(name = "EXTSUROBJ_NAME")
    public String getSurObjName() {
        return surObjName;
    }

    public void setSurObjName(String surObjName) {
        this.surObjName = surObjName;
    }

    @Column(name = "EXTSUROBJ_INFO")
    public String getSurObjInfo() {
        return surObjInfo;
    }

    public void setSurObjInfo(String surObjInfo) {
        this.surObjInfo = surObjInfo;
    }
}
