package com.city.resourcecategory.themes.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wxl on 2016/3/10.
 * 主题
 */
public class EsiTheme implements Serializable {
    private String themeName;
    private String themeConfigPath;//主题配置文件路径
    private List<EsiThemeModule> modules;//模块

    public String getThemeName() {
        return themeName;
    }

    public void setThemeName(String themeName) {
        this.themeName = themeName;
    }

    public String getThemeConfigPath() {
        return themeConfigPath;
    }

    public void setThemeConfigPath(String themeConfigPath) {
        this.themeConfigPath = themeConfigPath;
    }

    public List<EsiThemeModule> getModules() {
        return modules;
    }

    public void setModules(List<EsiThemeModule> modules) {
        this.modules = modules;
    }
}
