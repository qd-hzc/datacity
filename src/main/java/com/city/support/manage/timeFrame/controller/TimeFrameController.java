package com.city.support.manage.timeFrame.controller;

import com.city.common.controller.BaseController;
import com.city.common.pojo.Constant;
import com.city.common.pojo.Page;
import com.city.common.util.EsiJsonParamUtil;
import com.city.common.util.EsiLogUtil;
import com.city.support.manage.timeFrame.entity.TimeFrame;
import com.city.support.manage.timeFrame.service.TimeFrameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by zhoutao on 2015/12/30.
 * 时间框架管理controller
 */
@Controller
@RequestMapping("/support/manage/timeFrame")
public class TimeFrameController extends BaseController {
    @Autowired
    private TimeFrameService timeFrameService;

    /**
     * 进入时间框架管理页面
     *
     * @return
     */
    @RequestMapping("/timeFrameConfig")
    public String itemFrameConfig() {
        return "support/manage/timeFrame/timeFrameConfig";
    }

    /**
     * 获取所有时间框架
     *
     * @param page
     * @return
     */
    @RequestMapping("/getAllTimeFrames")
    @ResponseBody
    public Page getAllTimeFrames(Page page) {
        List<TimeFrame> timeFrameList = timeFrameService.findAll();
        page.setDatas(timeFrameList);
        if (timeFrameList != null) {
            page.setTotal(timeFrameList.size());
        }

        return page;
    }

    /**
     * 按照排序获取所有时间框架
     *
     * @param page
     * @return
     */
    @RequestMapping("/getAllTimeFramesByOrder")
    @ResponseBody
    public Page getAllTimeFramesByOrder(Page page) {
        List<TimeFrame> timeFrameList = timeFrameService.findByOrder(page);
        page.setDatas(timeFrameList);
        if (timeFrameList != null) {
            page.setTotal(timeFrameService.findByOrder(null).size());
        }
        return page;
    }

    /**
     * 保存时间框架
     *
     * @param timeFrame
     * @return
     */
    @RequestMapping("/saveTimeFrame")
    @ResponseBody
    public Map<String, Object> saveTimeFrame(TimeFrame timeFrame) {
        Map<String, Object> result = null;
        try {
            Integer reqResult = timeFrameService.save(timeFrame);
            if (reqResult == Constant.RequestResult.SUCCESS) {
                result = genSuccessMsg(timeFrame, "保存成功", null);
            } else if (reqResult == Constant.RequestResult.EXIST) {
                result = genFaultMsg(null, "时间框架已经存在", null);
            } else {
                result = genFaultMsg(null, "保存失败", null);
            }

        } catch (Exception e) {
            e.printStackTrace();
            EsiLogUtil.error(getLog(), e.getMessage());
            result = genFaultMsg(null, "保存失败", null);
        }
        return result;
    }

/*    *//**
     * 保存时间框架
     * @param request
     * @return
     *//*
    @RequestMapping("/saveTimeFrame")
    @ResponseBody
    public Map<String, Object> saveTimeFrame(HttpServletRequest request) {
        Map<String, Object> result = null;
        List<TimeFrame> timeFrameList = null;
        EsiJsonParamUtil<TimeFrame> paramUtil = new EsiJsonParamUtil<>();
        try {
            timeFrameList = paramUtil.parseObjToList(request, TimeFrame.class);
            if (timeFrameList != null) {
                timeFrameService.batchSave(timeFrameList);
                result = genSuccessMsg(timeFrameList, "保存成功", null);
            } else {
                result = genFaultMsg(null, "保存失败", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            EsiLogUtil.error(getLog(), e.getMessage());
            result = genFaultMsg(null, "保存失败", null);
        }
        return result;
    }*/

    /**
     * 更新时间框架
     *
     * @param request
     * @return
     */
    @RequestMapping("/updateTimeFrame")
    @ResponseBody
    public Map<String, Object> updateTimeFrame(HttpServletRequest request) {
        Map<String, Object> result = null;
        List<TimeFrame> timeFrameList = null;
        EsiJsonParamUtil<TimeFrame> paramUtil = new EsiJsonParamUtil<>();
        try {
            timeFrameList = paramUtil.parseObjToList(request, TimeFrame.class);
            if (timeFrameList != null) {
                timeFrameService.update(timeFrameList);
                result = genSuccessMsg(null, "更新成功", null);
            } else {
                result = genFaultMsg(null, "更新失败", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            EsiLogUtil.error(getLog(), e.getMessage());
            result = genFaultMsg(null, "更新失败", null);
        }
        return result;
    }

    /**
     * 批量删除时间框架
     *
     * @param request
     * @return
     */
    @RequestMapping("/batchDeleteTimeFrames")
    @ResponseBody
    public Map<String, Object> batchDeleteTimeFrames(HttpServletRequest request) {
        Map<String, Object> result = null;
        List<TimeFrame> timeFrames = null;
        EsiJsonParamUtil<TimeFrame> paramUtil = new EsiJsonParamUtil<>();
        try {
            timeFrames = paramUtil.parseObjToList(request, TimeFrame.class);
            if (timeFrames != null) {
                timeFrameService.batchDelete(timeFrames);
                result = genSuccessMsg(timeFrames, "删除成功", null);
            } else {
                result = genFaultMsg(null, "删除失败", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            EsiLogUtil.error(getLog(), e.getMessage());
            result = genFaultMsg(null, "删除失败", null);
        }
        return result;
    }


    /**
     * 根据名称查询时间框架
     *
     * @param name 查询名称
     * @return
     */
    @RequestMapping("/findTimeFrameByName")
    @ResponseBody
    public List<TimeFrame> findTimeFrameByName(String name) {
        if (name == null)
            name = "";

        return timeFrameService.findByName(name);
    }

}
