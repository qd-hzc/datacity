package com.city.support.sys.user.entity;

import org.hibernate.annotations.*;

import javax.persistence.*;

import org.hibernate.annotations.Parameter;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by wys on 2015/12/28.
 */
@Entity
@Table(name = "SPT_SYS_DEPART")
public class Department {
    /**
     * 部门id
     */
    private Integer id;
    /**
     * 部门名称
     */
    private String depName;
    /**
     * 部门简称
     */
    private String depShortName;
    /**
     * 部门级别
     */
    private Integer depLevel;
    /**
     * 部门电话
     */
    private String depPhone;
    /**
     * 部门传真
     */
    private String depFax;
    /**
     * 部门邮件
     */
    private String depEmail;
    /**
     * 上级部门ID
     */
    private Integer pDep;
    /**
     * 部门排序
     */
    private Integer sort;

    @Id
    @GeneratedValue(generator = "na")
    @GenericGenerator(name = "na", strategy = "native", parameters = {@Parameter(name = "sequence", value = "DEP_SEQ")})
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "DEP_EMAIL")
    public String getDepEmail() {
        return depEmail;
    }

    public void setDepEmail(String depEmail) {
        this.depEmail = depEmail;
    }

    @Column(name = "DEP_FAX")
    public String getDepFax() {
        return depFax;
    }

    public void setDepFax(String depFax) {
        this.depFax = depFax;
    }

    @Column(name = "DEP_LEVEL")
    public Integer getDepLevel() {
        return depLevel;
    }

    public void setDepLevel(Integer depLevel) {
        this.depLevel = depLevel;
    }

    @Column(name = "DEP_NAME")
    public String getDepName() {
        return depName;
    }

    public void setDepName(String depName) {
        this.depName = depName;
    }

    @Column(name = "DEP_PHONE")
    public String getDepPhone() {
        return depPhone;
    }

    public void setDepPhone(String depPhone) {
        this.depPhone = depPhone;
    }

    @Column(name = "DEP_SHORTNAME")
    public String getDepShortName() {
        return depShortName;
    }

    public void setDepShortName(String depShortName) {
        this.depShortName = depShortName;
    }

    @Column(name = "PDEP_ID")
    public Integer getpDep() {
        return pDep;
    }

    public void setpDep(Integer pDep) {
        this.pDep = pDep;
    }

    @Column(name = "DEP_SORT")
    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}
