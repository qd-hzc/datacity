package com.city.support.sys.update;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wys on 2016/5/3.
 */
public class EsiTaskData {
    private Map<String, Object> datas = new HashMap<>();

    public void addData(String key, Object data) {
        datas.put(key, data);
    }

    public void removeData(String key) {
        datas.remove(key);
    }
}
