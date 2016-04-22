package com.city.common.event.watcher;

import org.springframework.stereotype.Component;

/**
 * Created by CRX on 2016/4/15.
 */
@Component
public class SystemLogWatched extends EsiEventWatched {
    //    系统日志
    public final static String SYS_LOG = "systemLog";
    //    系统日志集合
    public final static String SYS_LOG_LIST = "systemLogList";
}
