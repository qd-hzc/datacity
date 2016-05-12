package com.city.resourcecategory.themes.entity;

import org.hibernate.annotations.IndexColumn;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by wxl on 2016/3/10.
 * 主题配置页面,加权限时,用一张中间表将其与角色关联起来即可
 */
@Entity
@Table(name = "RC_THEME_PAGE")
public class ThemePage implements Serializable {
    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "rcThemePage", sequenceName = "RC_THEME_PAGE_SEQ")
    @GeneratedValue(generator = "rcThemePage")
    private Integer id;
    @Column(name = "NAME", nullable = false)
    private String name;
    @Column(name = "PARENT_ID", nullable = false)
    private Integer parentId;
    @Column(name = "THEME_CONFIG_PATH")
    private String themeConfigPath;//主题配置路径,将配置加载在内存中,可根据配置路径索引到
    @Column(name = "MODULE_PATH")
    private String modulePath;//模块路径
    @Column(name = "STATUS", nullable = false)
    private Integer status;//状态
    @OneToMany
    @JoinColumn(name = "THEME_PAGE_ID")
    private Set<ThemePageContent> contents;//内容
    @Column(name = "SORT_INDEX")
    @OrderBy
    private Integer sortIndex;
    //    是否为叶子节点
    @Column(name = "LEAF")
    private boolean leaf;

    @Column(name = "role")
    private String role;

    //    子
    @Transient
    private List<ThemePage> child = new ArrayList<>();

    //    是否为root节点
    @Transient
    private boolean root = true;

    //    是否为title
    @Transient
    private boolean title = false;

    @Transient
    private String text;

    public String getText() {
        return name;
    }

    public boolean isTitle() {
        return title;
    }

    public void setTitle(boolean title) {
        this.title = title;
    }

    public boolean isRoot() {
        return root;
    }

    public void setRoot(boolean root) {
        this.root = root;
    }

    public List<ThemePage> getChild() {
        return child;
    }

    public void setChild(List<ThemePage> child) {
        this.child = child;
    }

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
        this.text = name;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getThemeConfigPath() {
        return themeConfigPath;
    }

    public void setThemeConfigPath(String themeConfigPath) {
        this.themeConfigPath = themeConfigPath;
    }

    public String getModulePath() {
        return modulePath;
    }

    public void setModulePath(String modulePath) {
        this.modulePath = modulePath;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Set<ThemePageContent> getContents() {
        return contents;
    }

    public void setContents(Set<ThemePageContent> contents) {
        this.contents = contents;
    }

    public Integer getSortIndex() {
        return sortIndex;
    }

    public void setSortIndex(Integer sortIndex) {
        this.sortIndex = sortIndex;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
