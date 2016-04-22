package com.city.support.sys.user.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by wys on 2015/12/28.
 */
@Entity
@Table(name = "SPT_SYS_USER")
public class User {
    public final static boolean STATE_ENABLE = true;
    public final static boolean STATE_DISABLE = false;
    public final static boolean ADMIN = true;
    /**
     * 用户id
     */
    private Integer id;
    /**
     * 用户名称
     */
    private String userName;
    /**
     * 用户登录名称
     */
    private String loginName;
    /**
     * 用户密码
     */
    private String loginPwd;
    /**
     * 用户手机
     */
    private String mobilePhone;
    /**
     * 用户邮件
     */
    private String email;
    /**
     * 用户性别
     */
    private boolean sex;
    /**
     * 用户状态
     */
    private boolean state;
    /**
     * 用户备注
     */
    private String userInfo;
    /**
     * 用户部门
     */
    private Department department;

    /**
     * 职务
     */
    private String duty;
    /**
     * 用户首页面
     */
    private String indexPage;
    /**
     * 是否超级管理员 0 不是 1是
     */
    private boolean admin = false;
    /**
     * 用户角色
     */
    private Set<Role> roles = new HashSet<Role>();

    private Set<RptPermission> rptPermissions = new HashSet<RptPermission>();

    /**
     * 用户登录ip
     */
    private String ip;

    @Id
    @GeneratedValue(generator = "na")
    @GenericGenerator(name = "na", strategy = "native", parameters = {@Parameter(name = "sequence", value = "USER_SEQ")})
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "USER_NAME")
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    @Column(name = "USER_LOGINAME")
    public String getLoginName() {
        return loginName;
    }


    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    @Column(name = "USER_PWD")
    public String getLoginPwd() {
        return loginPwd;
    }

    public void setLoginPwd(String loginPwd) {
        this.loginPwd = loginPwd;
    }


    @Column(name = "USER_EMAIL")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    @Column(name = "USER_MPHONE")
    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    @Column(name = "USER_SEX")
    public boolean isSex() {
        return sex;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

    @Column(name = "USER_STATE")
    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    @Column(name = "USER_ADMIN")
    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    @Column(name = "USER_INFO", length = 500)
    public String getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(String userInfo) {
        this.userInfo = userInfo;
    }

    @Column(name = "USER_INDEX")
    public String getIndexPage() {
        return indexPage;
    }

    public void setIndexPage(String indexPage) {
        this.indexPage = indexPage;
    }

    @ManyToOne
    @JoinColumn(name = "DEP_ID")
    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    @Column(name = "USER_DUTY")
    public String getDuty() {
        return duty;
    }

    public void setDuty(String duty) {
        this.duty = duty;
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "SPT_SYS_USER_ROLE",
            joinColumns = @JoinColumn(name = "USER_ID"),
            inverseJoinColumns = @JoinColumn(name = "ROLE_ID")
    )
    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "SPT_SYS_USER_RPT_PERMISSION",
            joinColumns = @JoinColumn(name = "USER_ID"),
            inverseJoinColumns = @JoinColumn(name = "PERM_ID")
    )
    public Set<RptPermission> getRptPermissions() {
        return rptPermissions;
    }

    public void setRptPermissions(Set<RptPermission> rptPermissions) {
        this.rptPermissions = rptPermissions;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
