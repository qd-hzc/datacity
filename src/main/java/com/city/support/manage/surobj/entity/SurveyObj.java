package com.city.support.manage.surobj.entity;

import javax.persistence.*;

/**
 * Created by wys on 2016/1/15.
 */
@Entity
@Table(name = "SPT_MGR_SUROBJ")
public class SurveyObj {
    private Integer id;
    private String surveyObjName;
    private Integer surveyObjId;
    private String surveyObjCode;
    private Integer surveyObjType;
    private Integer surveyObjAreaId;
    private String surveyObjAreaName;
    private Integer surveyObjSort;
    private Integer surveyObjGroupId;

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

    @Column(name = "SUROBJ_AREAID")
    public Integer getSurveyObjAreaId() {
        return surveyObjAreaId;
    }

    public void setSurveyObjAreaId(Integer surveyObjAreaId) {
        this.surveyObjAreaId = surveyObjAreaId;
    }

    @Column(name = "SUROBJ_OBJID")
    public Integer getSurveyObjId() {
        return surveyObjId;
    }

    public void setSurveyObjId(Integer surveyObjId) {
        this.surveyObjId = surveyObjId;
    }

    @Column(name = "SUROBJ_OBJNAME")
    public String getSurveyObjName() {
        return surveyObjName;
    }

    public void setSurveyObjName(String surveyObjName) {
        this.surveyObjName = surveyObjName;
    }

    @Column(name = "SUROBJ_TYPE")
    public Integer getSurveyObjType() {
        return surveyObjType;
    }

    public void setSurveyObjType(Integer surveyObjType) {
        this.surveyObjType = surveyObjType;
    }

    @Column(name = "SUROBJ_SORT")
    public Integer getSurveyObjSort() {
        return surveyObjSort;
    }

    public void setSurveyObjSort(Integer surveyObjSort) {
        this.surveyObjSort = surveyObjSort;
    }
    @Column(name = "SUROBJ_GROUPID")
    public Integer getSurveyObjGroupId() {
        return surveyObjGroupId;
    }

    public void setSurveyObjGroupId(Integer surveyObjGroupId) {
        this.surveyObjGroupId = surveyObjGroupId;
    }
    @Column(name = "SUROBJ_AREANAME")
    public String getSurveyObjAreaName() {
        return surveyObjAreaName;
    }

    public void setSurveyObjAreaName(String surveyObjAreaName) {
        this.surveyObjAreaName = surveyObjAreaName;
    }
    @Column(name = "SUROBJ_CODE")
    public String getSurveyObjCode() {
        return surveyObjCode;
    }

    public void setSurveyObjCode(String surveyObjCode) {
        this.surveyObjCode = surveyObjCode;
    }
}
