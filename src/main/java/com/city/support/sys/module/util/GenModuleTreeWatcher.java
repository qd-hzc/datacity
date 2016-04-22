package com.city.support.sys.module.util;

import com.city.common.util.EsiLogUtil;
import com.city.common.util.tree.GenTreeWatcher;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by wys on 2016/1/4.
 */
public class GenModuleTreeWatcher extends GenTreeWatcher {

    public GenModuleTreeWatcher(String watcherName) {
        super(watcherName);
    }

    @Override
    public void operate(Object[] args) {
        if (args != null && args.length > 2 && args[1] != null) {
            Collections.sort((List) args[1], new Comparator<Object>() {
                @Override
                public int compare(Object o1, Object o2) {
                    Map<String, Object> obj1 = (Map) o1;
                    Map<String, Object> obj2 = (Map) o2;
                    Integer sort1 = (Integer) obj1.get("sort");
                    Integer sort2 = (Integer) obj2.get("sort");
                    if (sort1 != null && sort2 != null) {
                        return sort1 - sort2;
                    } else if (sort1 == null) {
                        return 100;
                    }
                    return 0;
                }
            });
            //EsiLogUtil.debug(EsiLogUtil.getLogInstance(this.getClass()), args[1].toString());
        }
    }
}
