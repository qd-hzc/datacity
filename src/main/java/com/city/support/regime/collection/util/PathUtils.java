package com.city.support.regime.collection.util;

/**
 * 文件路径工具类
 *
 * @author chenyongqiang
 *
 */
public class PathUtils {
    private static String filePath = "";

    public static String getResourcePath() {
        if ("".equals(filePath)) {
            String result = PathUtils.class.getResource("PathUtils.class")
                    .toString();
            int index = result.indexOf("WEB-INF");
            if (index == -1) {
                index = result.indexOf("bin");
            }
            result = result.substring(0, index);
            if (result.startsWith("jar")) {
                int k = result.indexOf("jar");
                result = result.substring(k + 10);// 当class文件在jar文件中时，返回”jar:file:/F:/…”样的路径
            } else if (result.startsWith("file")) {
                int k = result.indexOf("file");
                result = result.substring(k + 6);// 当class文件在jar文件中时，返回”file:/F:/…”样的路径
            } else if (result.startsWith("zip:")) {
                int k = result.indexOf("zip");
                result = result.substring(k + 5);// 当class文件在jar文件中时，返回”/zip:/F:/…”样的路径
            }
            result = result.replace("%20", " ");
            filePath = "/" + result;
        }
        return filePath;
    }
    public static void main(String[] args) {
        System.out.println(PathUtils.getResourcePath());
    }
}
