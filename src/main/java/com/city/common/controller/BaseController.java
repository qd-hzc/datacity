package com.city.common.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ApplicationObjectSupport;

public class BaseController extends ApplicationObjectSupport {
    /**
     * controller 层日志
     */
    private Logger log;

    public BaseController() {
        // 获取controller 的log对象
        log = LoggerFactory.getLogger(this.getClass());
    }

    /**
     * 返回成功消息
     *
     * @param datas 返回数据
     * @param msg   返回的消息
     * @param code  消息代码
     * @return
     */
    public Map<String, Object> genSuccessMsg(Object datas, String msg, Integer code) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("success", true);
        if (datas != null)
            result.put("datas", datas);
        if (code != null)
            result.put("code", code);
        else
            result.put("code", 200);
        result.put("msg", msg);
        return result;
    }

    public Map<String, Object> genSuccessMsg(String msg) {
        return genSuccessMsg(null, msg, 200);
    }

    /**
     * 返回失败消息
     *
     * @param datas 返回数据
     * @param msg   返回的消息
     * @param code  消息代码
     * @return
     */
    public Map<String, Object> genFaultMsg(Object datas, String msg, Integer code) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("success", false);
        if (datas != null)
            result.put("datas", datas);
        if (code != null)
            result.put("code", code);
        else
            result.put("code", 500);
        result.put("msg", msg);
        return result;
    }

    public Map<String, Object> genFaultMsg(String msg) {
        return genFaultMsg(null, msg, 500);
    }

    public Logger getLog() {
        return log;
    }

    public void setLog(Logger log) {
        this.log = log;
    }

}
