package com.city.common.util.ue;

import com.city.common.util.file.FileContentUtil;

import java.io.File;

/**
 * Created by wxl on 2016/3/16.
 */
public class LoadUEConfig {

    private static String prePath = LoadUEConfig.class.getResource("/").getPath();//  /F:/ideaFiles/city_base/src/main/webapp/WEB-INF/classes/

    static {
        prePath = prePath.substring(0, prePath.length() - 16) + "handler/ueditor/config.json";
    }

    /**
     * 加载ue配置
     * @param projectPath
     */
    public static void loadUEConfig(String projectPath) {
        File file = new File(prePath);
        if (file.exists()) {
            String configStr = FileContentUtil.getConfigJsonStr(prePath);
            if (configStr.contains("|projectPath|")) {
                configStr = configStr.replaceAll("\\|projectPath\\|", projectPath);
                FileContentUtil.writeConfigStr(prePath, configStr, false);
            }
        }

    }
}
