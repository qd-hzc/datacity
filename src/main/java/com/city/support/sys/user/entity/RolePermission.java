package com.city.support.sys.user.entity;

import javax.persistence.*;

/**
 * Created by zhoutao on 2016/1/20.
 */
@Entity
@Table(name = "SPT_SYS_ROLE_PERMISSION")
public class RolePermission {
    public enum Type{
        MODULE,     //菜单权限
        REPORT      //报表权限
    }

    private int id;
    //private Type type;
    private Role role;
    private Permission permission;

    @Id
    @GeneratedValue(generator = "RolePermissionGen")
    @SequenceGenerator(name = "RolePermissionGen", sequenceName = "SPT_SYS_ROLE_PERMISSION_SEQ")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /*@Enumerated(EnumType.ORDINAL)
    @Column(name="TYPE")
    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }*/

    @ManyToOne(targetEntity = Role.class)
    @JoinColumn(name = "ROLE_ID")
    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @ManyToOne(targetEntity = Permission.class)
    @JoinColumn(name = "PERMISSION_ID")
    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }
}
