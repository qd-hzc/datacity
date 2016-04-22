package com.city.common.event.watcher;

import org.springframework.stereotype.Component;

/**
 * Created by wys on 2016/1/21.
 */
@Component
public class SurObjWatched extends EsiEventWatched {
    public final static String B4DELSURGROUP = "beforSurObjGroupDel";
    public final static String PARAM_SURGROUP = "surObjGroup";
}
