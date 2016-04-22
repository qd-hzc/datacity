package com.city.support.sys.module.pojo;

import com.city.support.sys.module.entity.Module;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wys on 2015/12/30.
 * 模块树节点
 */
public class ModuleNode {
    private Integer id;
    private String moduleName;
    private Integer moduleType;
    private Module module;
    private Boolean leaf;
    private List<ModuleNode> children = new ArrayList<ModuleNode>();

    public ModuleNode() {
    }

    public ModuleNode(Integer id, Boolean leaf, Module module, String moduleName, Integer moduleType) {
        this.id = id;
        this.leaf = leaf;
        this.module = module;
        this.moduleName = moduleName;
        this.moduleType = moduleType;
    }

    public List<ModuleNode> getChildren() {
        return children;
    }

    public void setChildren(List<ModuleNode> children) {
        this.children = children;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getLeaf() {
        return leaf;
    }

    public void setLeaf(Boolean leaf) {
        this.leaf = leaf;
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public Integer getModuleType() {
        return moduleType;
    }

    public void setModuleType(Integer moduleType) {
        this.moduleType = moduleType;
    }
}
