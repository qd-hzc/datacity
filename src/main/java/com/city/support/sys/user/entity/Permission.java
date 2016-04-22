package com.city.support.sys.user.entity;

import com.city.support.sys.user.pojo.PermissionType;
import com.city.support.sys.user.pojo.ReportPermissionType;

import javax.persistence.*;

/**
 * 权限表
 * Created by zhoutao on 2016/1/20.
 */
@Entity
@Table(name = "SPT_SYS_PERMISSION")
public class Permission {
    private Integer id;                                    //主键
    private Integer dep;                                   //部门 存储部门id
    private ReportPermissionType reportPermissionType;  //报表权限类型
    private PermissionType permissionType;               //权限类型

    @Id
    @SequenceGenerator(name = "permissionGenerator", sequenceName = "SPT_SYS_PERMISSION_SEQ")
    @GeneratedValue(generator = "permissionGenerator")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "DEP")
    public Integer getDep() {
        return dep;
    }

    public void setDep(Integer dep) {
        this.dep = dep;
    }

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "REPORT_PERMISSION_TYPE")
    public ReportPermissionType getReportPermissionType() {
        return reportPermissionType;
    }

    public void setReportPermissionType(ReportPermissionType reportPermissionType) {
        this.reportPermissionType = reportPermissionType;
    }

    @Enumerated(EnumType.ORDINAL)
    @Column(name="PERMISSION_TYPE")
    public PermissionType getPermissionType() {
        return permissionType;
    }

    public void setPermissionType(PermissionType permissionType) {
        this.permissionType = permissionType;
    }
}
