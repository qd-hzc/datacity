package com.city.support.sys.log.controller;

import com.city.common.controller.BaseController;
import com.city.common.pojo.Page;
import com.city.support.sys.log.entity.SystemLog;
import com.city.support.sys.log.service.SystemLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by CRX on 2016/4/18.
 */
@Controller
@RequestMapping("/support/sys/log")
public class SystemLogController extends BaseController {
    @Autowired
    private SystemLogService systemLogService;

    /**
     * 进入日志列表页面
     *
     * @return
     */
    @RequestMapping("/toSystemLog")
    public String toSystemLog() {
        return "support/sys/log/systemLog";
    }

    /**
     * 获取所有系统日志
     *
     * @return
     */
    @RequestMapping("/getSystemLogByOrder")
    @ResponseBody
    public Object getSystemLogByOrder(Page page) {
        List<SystemLog> systemLogList = systemLogService.findByOrder(page);
        page.setDatas(systemLogList);
        if (systemLogList != null) {
            page.setTotal(systemLogService.getLogCount());
        }
        return page;
    }
}
