package com.city.app.staffValid.entity;

import com.city.support.sys.user.entity.Department;
import com.city.support.sys.user.entity.Role;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by wxl on 2016/3/28.
 * 手机端人员情况
 */
@Entity
@Table(name = "APP_PERSON")
public class AppPerson implements Serializable{
    @Id
    @Column
    @SequenceGenerator(name = "appPersonGen", sequenceName = "APP_PERSON_SEQ")
    @GeneratedValue(generator = "appPersonGen")
    private Integer id;
    //姓名
    @Column
    private String name;
    //手机
    @Column
    private String phone;
    //邮箱
    @Column
    private String email;
    //部门
    @OneToOne
    @JoinColumn(name = "DEP_ID")
    private Department department;
    //职务
    @Column
    private String duty;
    //说明
    @Column
    private String comments;
    @Column(name = "VALID_CODE")
    private String validCode;
    @OneToOne
    @JoinColumn(name = "ROLE_ID")
    private Role role;

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public String getDuty() {
        return duty;
    }

    public void setDuty(String duty) {
        this.duty = duty;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getValidCode() {
        return validCode;
    }

    public void setValidCode(String validCode) {
        this.validCode = validCode;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
