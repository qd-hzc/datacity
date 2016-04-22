package com.city.support.sys.user.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Created by admin on 2015/12/28.
 */
@Entity
@Table(name = "SPT_SYS_PERSON")
public class Person {
    /**
     * 部门人员id
     */
    private Integer id;
    /**
     * 人员部门
     */
    private Department department;
    /**
     * 部门人员名称
     */
    private String name;
    /**
     * 部门人员职位
     */
    private Integer duty;
    /**
     * 部门人员联系方式
     */
    private String connect;
    /**
     * 部门人员邮箱
     */
    private String email;
    /**
     * 部门人员电话
     */
    private String tel;
    /**
     * 部门人员说明
     */
    private String personInfo;

    @Id
    @GeneratedValue(generator = "na")
    @GenericGenerator(name = "na", strategy = "native", parameters = {@org.hibernate.annotations.Parameter(name = "sequence", value = "PERSON_SEQ")})
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "PERSON_CONNECT")
    public String getConnect() {
        return connect;
    }

    public void setConnect(String connect) {
        this.connect = connect;
    }

    @ManyToOne
    @JoinColumn(name = "DEP_ID")
    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    @Column(name = "PERSON_DUTY")
    public Integer getDuty() {
        return duty;
    }

    public void setDuty(Integer duty) {
        this.duty = duty;
    }

    @Column(name = "PERSON_EMAIL")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "PERSON_NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "PERSON_INFO")
    public String getPersonInfo() {
        return personInfo;
    }

    public void setPersonInfo(String personInfo) {
        this.personInfo = personInfo;
    }

    @Column(name = "PERSON_TEL")
    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }
}
