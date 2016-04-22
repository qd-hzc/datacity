package com.city.app.dataDict.entity;

import com.city.app.dataDict.icon.entity.AppDataDictMenuIcon;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by wxl on 2016/3/21.
 * 数据字典 目录
 */
@Entity
@Table(name = "APP_DATA_DICT_MENU")
public class AppDataDictMenu implements Serializable {
    @Id
    @Column
    @SequenceGenerator(name = "appDataDictMenuGen", sequenceName = "APP_DATA_DICT_MENU_SEQ")
    @GeneratedValue(generator = "appDataDictMenuGen")
    private Integer id;
    @Column
    private String name;
    @Column(name = "PARENT_ID")
    private Integer parentId;
    @Column(name = "ROLE_ID")
    private Integer roleId;
    @Column
    private Integer status;
    @Column(name = "SORT_INDEX")
    private Integer sortIndex;
    @OneToOne
    @JoinColumn(name = "ICON_ID")
    private AppDataDictMenuIcon menuIcon;
    @OneToOne
    @JoinColumn(name = "BG_ID")
    private AppDataDictMenuIcon menuBg;
    @Transient
    private String icon;
    @Transient
    private boolean leaf = true;

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

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getSortIndex() {
        return sortIndex;
    }

    public void setSortIndex(Integer sortIndex) {
        this.sortIndex = sortIndex;
    }

    public AppDataDictMenuIcon getMenuIcon() {
        return menuIcon;
    }

    public void setMenuIcon(AppDataDictMenuIcon menuIcon) {
        this.menuIcon = menuIcon;
    }

    public AppDataDictMenuIcon getMenuBg() {
        return menuBg;
    }

    public void setMenuBg(AppDataDictMenuIcon menuBg) {
        this.menuBg = menuBg;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    public void setIconByPath(String projectPath) {
        if (menuIcon != null) {
            setIcon(projectPath + AppDataDictMenuIcon.iconPreUrl + menuIcon.getPath());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AppDataDictMenu that = (AppDataDictMenu) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (parentId != null ? !parentId.equals(that.parentId) : that.parentId != null) return false;
        if (roleId != null ? !roleId.equals(that.roleId) : that.roleId != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;
        return sortIndex != null ? sortIndex.equals(that.sortIndex) : that.sortIndex == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (parentId != null ? parentId.hashCode() : 0);
        result = 31 * result + (roleId != null ? roleId.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (sortIndex != null ? sortIndex.hashCode() : 0);
        return result;
    }
}
