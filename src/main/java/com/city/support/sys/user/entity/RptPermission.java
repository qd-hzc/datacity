package com.city.support.sys.user.entity;

import com.city.support.sys.user.pojo.PermissionType;

import javax.persistence.*;

/**
 * Created by zhoutao on 2016/3/8.
 */
@Entity
@Table(name = "SPT_SYS_RPT_PERMISSION")
public class RptPermission {
    private Integer id;
    private Integer rptId;                                 //报表模板id
    private PermissionType permissionType;               //权限类型


    @Id
    @SequenceGenerator(name = "rptPermissionGenerator", sequenceName = "SPT_SYS_RPT_PERMISSION_SEQ")
    @GeneratedValue(generator = "rptPermissionGenerator")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "RPT_ID")
    public Integer getRptId() {
        return rptId;
    }

    public void setRptId(Integer rptId) {
        this.rptId = rptId;
    }

    @Enumerated(EnumType.ORDINAL)
    @Column(name="PERMISSION_TYPE")
    public PermissionType getPermissionType() {
        return permissionType;
    }

    public void setPermissionType(PermissionType permissionType) {
        this.permissionType = permissionType;
    }

    @Override
    public int hashCode() {
        return rptId*10+permissionType.ordinal();
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof RptPermission))
            return false;

        RptPermission rpt = (RptPermission)obj;
        return (rptId == rpt.getRptId()) && (permissionType.ordinal() == rpt.getPermissionType().ordinal());
    }
}
