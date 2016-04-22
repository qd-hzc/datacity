package com.city.common.event.watcher;

import com.city.common.event.watcher.EsiEventWatched;
import org.springframework.stereotype.Component;

/**
 * Created by wys on 2016/1/8.
 */
@Component
public class DepWatched extends EsiEventWatched {
    public final static String ADDDEP = "addDep";
    public final static String BEFOREADDDEP = "beforeAddDep";
    public final static String PARAM_DEP = "dep";

    //删除前
    public final static String BEFOREDELETE="beforeDelete";
    public final static String DELETE="delete";
    public final static String PARAM_DEPIDS="depIds";
}
