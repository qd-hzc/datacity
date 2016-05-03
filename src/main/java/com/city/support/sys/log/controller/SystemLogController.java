package com.city.support.sys.log.controller;

import com.city.common.controller.BaseController;
import com.city.common.pojo.Page;
import com.city.support.sys.log.entity.SystemLog;
import com.city.support.sys.log.service.SystemLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Date;
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
    public List<SystemLog> getSystemLogByOrder(String name, String startDate, String endDate) {
//        String name = request.getParameter("name");
//        String startDate = request.getParameter("startDate");
//        String endDate = request.getParameter("endDate");
        Date sDate = null;
        if (!StringUtils.isEmpty(startDate.trim())) {
            sDate = new Date(startDate);
        }
        Date eDate = null;
        if (!StringUtils.isEmpty(endDate.trim())) {
            eDate = new Date(endDate);
        }
        List<SystemLog> systemLogList = systemLogService.findByOrder(name, sDate, eDate);

        return systemLogList;
    }

    /**
     * 按照搜索内容获取系统日志
     *
     * @param page
     * @param request
     * @return
     */
    @RequestMapping("/getSystemLogByCondition")
    @ResponseBody
    public Object getSystemLogByCondition(Page page, HttpServletRequest request) {
        String name = request.getParameter("name");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        List<SystemLog> systemLogList = null;
        Date sDate = null;
        if (!StringUtils.isEmpty(startDate.trim())) {
            sDate = new Date(startDate);
        }
        Date eDate = null;
        if (!StringUtils.isEmpty(endDate.trim())) {
            eDate = new Date(endDate);
        }

        systemLogList = systemLogService.findSystemLogByCondition(page, name, sDate, eDate);
        page.setDatas(systemLogList);
        page.setTotal(systemLogService.getLogCount(name, sDate, eDate));
        return page;
    }

    /**
     * 导出为Excel
     *
     * @param response
     * @param text      表格列名称
     * @param name      搜索条件 用户名或文字信息
     * @param startDate 搜索开始日期
     * @param endDate   搜索结束日期
     */
    @RequestMapping("/expertExcel")
    @ResponseBody
    public void expertExcel(HttpServletResponse response, String text, String name, String startDate, String endDate) {
        List<SystemLog> list = getSystemLogByOrder(name, startDate, endDate);
        systemLogService.getExcel(response, text, list);
    }

}
