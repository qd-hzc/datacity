package com.city.common.event.watcher;

import org.springframework.stereotype.Component;

/**
 * Created by wxl on 2016/1/18 0018.
 * 报表模板管理模块的监听
 */
@Component
public class RptTmpWatched extends EsiEventWatched {
    //删除报表模板前的事件
    public static final String BEFOREDELETETMP = "beforeDeleteTmp";
    //删除前所需的参数
    public static final String PARAMS_TMPIDS = "tmpIds";
    //删除表样前的事件
    public static final String BEFOREDELETETMPSTYLE = "beforeDeleteTmpStyle";
    //删除前所需的参数
    public static final String PARAMS_STYLEIDS = "styleIds";
    //修改报表模板前的事件
    public static final String BEFOREUPDATETMP = "beforeUpdateTmp";
    //修改前所需的参数
    public static final String PARAMS_TMP = "tmp";
    // 用户信息
    public static final String PARAMS_USER = "user";
}
