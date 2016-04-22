package com.city.support.manage.surobj.entity;

import javax.persistence.*;

/**
 * Created by wys on 2016/1/15.
 */
@Entity
@Table(name = "SPT_MGR_SUROBJGROUP")
public class SurveyObjGroup {
    public final static Integer TYPE_AREA = 1;
    public final static Integer TYPE_OTHER = 2;
    public final static Integer TYPE_COMP = 3;
    private Integer id;
    private String surveyObjGroupName;
    private Integer surveyObjGroupType;
    private String surveyObjGroupInfo;
    private Integer surveyObjGroupSort;

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

    @Column(name = "SUROBJGROUP_NAME")
    public String getSurveyObjGroupName() {
        return surveyObjGroupName;
    }

    public void setSurveyObjGroupName(String surveyObjGroupName) {
        this.surveyObjGroupName = surveyObjGroupName;
    }

    @Column(name = "SUROBJGROUP_TYPE")
    public Integer getSurveyObjGroupType() {
        return surveyObjGroupType;
    }

    public void setSurveyObjGroupType(Integer surveyObjGroupType) {
        this.surveyObjGroupType = surveyObjGroupType;
    }

    @Column(name = "SUROBJGROUP_INFO")
    public String getSurveyObjGroupInfo() {
        return surveyObjGroupInfo;
    }

    public void setSurveyObjGroupInfo(String surveyObjInfo) {
        this.surveyObjGroupInfo = surveyObjInfo;
    }

    @Column(name = "SUROBJGROUP_SORT")
    public Integer getSurveyObjGroupSort() {
        return surveyObjGroupSort;
    }

    public void setSurveyObjGroupSort(Integer surveyObjGroupSort) {
        this.surveyObjGroupSort = surveyObjGroupSort;
    }
}
