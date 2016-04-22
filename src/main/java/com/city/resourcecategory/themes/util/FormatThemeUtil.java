package com.city.resourcecategory.themes.util;

import com.city.common.util.file.FileContentUtil;
import com.city.resourcecategory.themes.pojo.EsiTheme;

import java.io.File;
import java.util.List;

/**
 * Created by wxl on 2016/3/10.
 * 解析主题文件
 */
public class FormatThemeUtil {
    private static String prePath = FormatThemeUtil.class.getResource("/").getPath();//  /F:/ideaFiles/city_base/src/main/webapp/WEB-INF/classes/

    static {
        prePath = prePath.substring(0, prePath.length() - 8) + "pages/resourceCategory/themes/";
    }

    /**
     * 获取所有主题配置文件路径(读取locations.config文件)
     */
    public static List<String> getThemeConfigs() {
        String absPath = prePath + "locations.config";
        File locationFile = new File(absPath);
        List<String> configs = null;
        if (locationFile.exists()) {//存在
            configs = FileContentUtil.getConfigJsonStr(absPath, List.class);
        }
        return configs;
    }

    /**
     * 根据主题配置路径读取配置文件,并转成实体
     *
     * @param themeConfig 配置路径
     */
    public static EsiTheme getThemeByConfig(String themeConfig) {
        String absPath = prePath + themeConfig;
        File themeFile = new File(absPath);
        EsiTheme esiTheme = null;
        if (themeFile.exists()) {
            esiTheme = FileContentUtil.getConfigJsonStr(absPath, EsiTheme.class);
            esiTheme.setThemeConfigPath(themeConfig);
        }
        return esiTheme;
    }
}
