package com.city.common.event.watcher;

import org.springframework.stereotype.Component;

/**
 * Created by wgx on 2016/1/15.
 */
@Component
public class MetadataWatched extends EsiEventWatched {
    public final static String ADDTYPE = "addType";
    public final static String BEFOREADDTYPE = "beforeAddType";
    public final static String PARAM_TYPE = "type";

    public final static String ADDINFO = "addInfo";
    public final static String BEFOREADDINFO = "beforeAddInfo";
    public final static String PARAM_INFO = "info";
    //删除前
    public final static String BEFOREDELETE="beforeDelete";
    public final static String DELETE="delete";
    public final static String PARAM_TYPEIDS="typeIds";
}
