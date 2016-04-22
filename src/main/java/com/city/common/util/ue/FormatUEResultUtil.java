package com.city.common.util.ue;

import com.city.common.util.ue.pojo.ListURL;
import com.city.common.util.ue.pojo.URL;
import com.google.gson.Gson;

import java.util.List;

/**
 * Created by wxl on 2016/3/16.
 */
public class FormatUEResultUtil {
    private static Gson gson = new Gson();

    /**
     * 解析对象
     */
    public static String formatObj(String objStr, String fromSub, String preAdding) {
        if (objStr != null && objStr.trim().length() > 0) {
            ListURL list = gson.fromJson(objStr, ListURL.class);
            if (list != null) {
                List<URL> urls = list.getList();
                if (urls != null && urls.size() > 0) {
                    for (URL url : urls) {
                        url.setUrl(formatUrl(url.getUrl(), fromSub, preAdding));
                    }
                    return gson.toJson(list);
                }
            }
            return objStr;
        }
        return objStr;
    }

    /**
     * 解析单个路径
     */
    private static String formatUrl(String url, String fromSub, String preAdding) {
        int index = url.indexOf(fromSub);
        if (index >= 0) {
            return preAdding + url.substring(index);
        }
        return url;

    }
}
