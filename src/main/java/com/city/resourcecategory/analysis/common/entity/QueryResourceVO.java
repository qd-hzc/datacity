package com.city.resourcecategory.analysis.common.entity;


import javax.persistence.*;

/**
 * 搜索资源，返回结果VO类
 * Created by HZC on 2016/5/12.
 */
@Entity
@Table(name = "RC_QUERY_RESOURCE")
public class QueryResourceVO {

    //    资源名称
    @Column(name = "name")
    private String name;
    //    资源类型：2、综合表，3、报表分析，4、图表分析，5、文字分析
    @Column(name = "type")
    private Integer type;
    //    资源id
    @Id
    private Integer id;
    //    描述
    @Column(name = "comments")
    private String comments;
    //    频度：年报，半年报，季报，月报
    @Column(name = "period")
    private Integer period;
    //    文字分析所属主题，图表分析、报表分析、综合表所属分组的id
    @Column(name = "EXTRAID")
    private Integer extraId;
    //    文字分析所属主题，图表分析、报表分析、综合表所属分组的名称
    @Column(name = "EXTRANAME")
    private String extraName;
    //    所属部门
    @Column(name = "DEPARTMENTID")
    private Integer departmentId;
    @Column(name = "DEPARTMENTNAME")
    private String departmentName;

    @Column(name = "rn")
    private Integer rn;

    public Integer getRn() {
        return rn;
    }

    public void setRn(Integer rn) {
        this.rn = rn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    public Integer getExtraId() {
        return extraId;
    }

    public void setExtraId(Integer extraId) {
        this.extraId = extraId;
    }

    public String getExtraName() {
        return extraName;
    }

    public void setExtraName(String extraName) {
        this.extraName = extraName;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public QueryResourceVO() {
    }

    public QueryResourceVO(QueryResourceVO vo) {
        this.name = vo.getName();
        this.type = vo.getType();
        this.id = vo.getId();
        this.comments = vo.getComments();
        this.period = vo.getPeriod();
        this.extraId = vo.getExtraId();
        this.extraName = vo.getExtraName();
        this.departmentId = vo.getDepartmentId();
        this.departmentName = vo.getDepartmentName();
        this.rn = vo.getRn();
    }
}
