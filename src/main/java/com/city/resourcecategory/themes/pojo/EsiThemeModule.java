package com.city.resourcecategory.themes.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wxl on 2016/3/10.
 * 模块
 */
public class EsiThemeModule implements Serializable {
    private String moduleName;//模块名
    private Integer pathType;//模块路径类型,可为 相对文件位置,项目路径,网页路径3种
    private String modulePath;//模块路径
    private List<EsiThemeModuleContent> contents;//内容

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public Integer getPathType() {
        return pathType;
    }

    public void setPathType(Integer pathType) {
        this.pathType = pathType;
    }

    public String getModulePath() {
        return modulePath;
    }

    public void setModulePath(String modulePath) {
        this.modulePath = modulePath;
    }

    public List<EsiThemeModuleContent> getContents() {
        return contents;
    }

    public void setContents(List<EsiThemeModuleContent> contents) {
        this.contents = contents;
    }
}
