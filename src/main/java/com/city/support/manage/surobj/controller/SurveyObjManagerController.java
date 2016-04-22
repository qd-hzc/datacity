package com.city.support.manage.surobj.controller;

import com.city.common.controller.BaseController;
import com.city.common.util.EsiJsonParamUtil;
import com.city.common.util.EsiLogUtil;
import com.city.support.manage.surobj.entity.ExtendSurveyObj;
import com.city.support.manage.surobj.entity.SurveyObj;
import com.city.support.manage.surobj.entity.SurveyObjGroup;
import com.city.support.manage.surobj.service.SurveyObjManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by wys on 2016/1/15.
 */
@Controller
@RequestMapping("/support/manage/surobj")
public class SurveyObjManagerController extends BaseController {
    @Autowired
    private SurveyObjManagerService surveyObjManagerService;

    @RequestMapping("/surobjConfig")
    public ModelAndView configPage() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("support/manage/surobj/surobjConfig");
        return mv;
    }

    @RequestMapping("/queryExtSurObj")
    @ResponseBody
    public Map<String, Object> queryExtSurObj() {
        Map<String, Object> result = null;
        List<ExtendSurveyObj> datas = surveyObjManagerService.queryExtSurveyObj();
        result = genSuccessMsg(datas, "查询成功", null);
        return result;
    }

    /**
     * @param request
     * @return
     */
    @RequestMapping("/addExtSurObj")
    @ResponseBody
    public Map<String, Object> addExtSurObj(HttpServletRequest request) {
        EsiJsonParamUtil<ExtendSurveyObj> jsonUtil = new EsiJsonParamUtil<ExtendSurveyObj>();
        Map<String, Object> result = null;
        try {
            ExtendSurveyObj data = jsonUtil.parseObj(request, ExtendSurveyObj.class);
            String areaIdStr = jsonUtil.getParam(request, "surAreaId");
            if (areaIdStr != null) {
                if (!"".equals(areaIdStr)) {
                    Integer areaId = Integer.parseInt(areaIdStr);
                    surveyObjManagerService.addExtSurveyObj(data, areaId);
                    result = genSuccessMsg(data, "添加成功", null);
                } else {
                    result = genFaultMsg(null, "添加其他统计对象失败", null);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            EsiLogUtil.error(getLog(), e.getMessage());
            result = genFaultMsg(null, "添加其他统计对象失败", null);
        }
        return result;
    }

    /**
     * @param request
     * @return
     */
    @RequestMapping("/updateExtSurObj")
    @ResponseBody
    public Map<String, Object> updateExtSurObj(HttpServletRequest request) {
        EsiJsonParamUtil<ExtendSurveyObj> jsonUtil = new EsiJsonParamUtil<ExtendSurveyObj>();
        Map<String, Object> result = null;
        String surAreaIdStr = null;
        List<ExtendSurveyObj> extendSurveyObjs = null;
        try {
            extendSurveyObjs = jsonUtil.parseObjToList(request, ExtendSurveyObj.class);
            surAreaIdStr = jsonUtil.getParam(request,"surAreaId");
            for (ExtendSurveyObj extendSurveyObj : extendSurveyObjs) {
                surveyObjManagerService.updateExtSurveyObj(extendSurveyObj,surAreaIdStr);
            }
            result = genSuccessMsg(null, "修改成功", null);
        } catch (Exception e) {
            e.printStackTrace();
            EsiLogUtil.error(getLog(), e.getMessage());
            result = genFaultMsg(null, "修改其他统计对象失败", null);
        }
        return result;
    }

    /**
     * @param request
     * @return
     */
    @RequestMapping("/removeExtSurObj")
    @ResponseBody
    public Map<String, Object> removeExtSurObj(HttpServletRequest request) {
        EsiJsonParamUtil<ExtendSurveyObj> jsonUtil = new EsiJsonParamUtil<ExtendSurveyObj>();
        Map<String, Object> result = null;
        List<ExtendSurveyObj> extendSurveyObjs = null;
        try {
            extendSurveyObjs = jsonUtil.parseObjToList(request, ExtendSurveyObj.class);
            for (ExtendSurveyObj extendSurveyObj : extendSurveyObjs) {
                surveyObjManagerService.removeExtSurveyObj(extendSurveyObj);
            }
            result = genSuccessMsg(null, "删除成功", null);
        } catch (Exception e) {
            e.printStackTrace();
            EsiLogUtil.error(getLog(), e.getMessage());
            result = genFaultMsg(null, "删除其他统计对象失败", null);
        }
        return result;
    }

    @RequestMapping("/querySurObjGroup")
    @ResponseBody
    public Map<String, Object> querySurObjGroup(String groupName) {
        Map<String, Object> result = null;
        List<SurveyObjGroup> surveyObjGroupList = null;
        try {
            if (groupName == null || "".equals(groupName)) {
                surveyObjGroupList = surveyObjManagerService.querySurGroupByName("");
            } else {
                surveyObjGroupList = surveyObjManagerService.querySurGroupByName(groupName);
            }
            result = genSuccessMsg(surveyObjGroupList, "查询成功", null);
        } catch (Exception e) {
            e.printStackTrace();
            EsiLogUtil.error(getLog(), e.getMessage());
            result = genFaultMsg(null, "查询统计对象分组失败", null);
        }
        return result;
    }

    @RequestMapping("/addSurObjGroup")
    @ResponseBody
    public Map<String, Object> addSurObjGroup(HttpServletRequest request) {
        Map<String, Object> result = null;
        SurveyObjGroup surObjGroup = null;
        try {
            EsiJsonParamUtil<SurveyObjGroup> jsonUtil = new EsiJsonParamUtil<>();
            surObjGroup = jsonUtil.parseObj(request, SurveyObjGroup.class);
            surveyObjManagerService.addSurObjGroup(surObjGroup);
            result = genSuccessMsg(surObjGroup, "添加成功", null);
        } catch (Exception e) {
            e.printStackTrace();
            EsiLogUtil.error(getLog(), e.getMessage());
            result = genFaultMsg(null, "添加统计对象分组失败", null);
        }
        return result;
    }

    @RequestMapping("/updateSurObjGroup")
    @ResponseBody
    public Map<String, Object> updateSurObjGroup(HttpServletRequest request) {
        Map<String, Object> result = null;
        List<SurveyObjGroup> surObjGroupList = null;
        try {
            EsiJsonParamUtil<SurveyObjGroup> jsonUtil = new EsiJsonParamUtil<>();
            surObjGroupList = jsonUtil.parseObjToList(request, SurveyObjGroup.class);
            surveyObjManagerService.updateSurObjGroup(surObjGroupList);
            result = genSuccessMsg(null, "修改成功", null);
        } catch (Exception e) {
            e.printStackTrace();
            EsiLogUtil.error(getLog(), e.getMessage());
            result = genFaultMsg(null, "修改统计对象分组失败", null);
        }
        return result;
    }

    @RequestMapping("/delSurObjGroup")
    @ResponseBody
    public Map<String, Object> delSurObjGroup(HttpServletRequest request) {
        Map<String, Object> result = null;
        List<SurveyObjGroup> surObjGroupList = null;
        try {
            EsiJsonParamUtil<SurveyObjGroup> jsonUtil = new EsiJsonParamUtil<>();
            surObjGroupList = jsonUtil.parseObjToList(request, SurveyObjGroup.class);
            surveyObjManagerService.delSurObjGroup(surObjGroupList);
            result = genSuccessMsg(null, "删除成功", null);
        } catch (Exception e) {
            e.printStackTrace();
            EsiLogUtil.error(getLog(), e.getMessage());
            result = genFaultMsg(null, "删除统计对象分组失败", null);
        }
        return result;
    }

    @RequestMapping("/addSurObj")
    @ResponseBody
    public Map<String, Object> addSurObj(HttpServletRequest request) {
        Map<String, Object> result = null;
        List<SurveyObj> surObjList = null;
        try {
            EsiJsonParamUtil<SurveyObj> jsonUtil = new EsiJsonParamUtil<>();
            surObjList = jsonUtil.parseObjToList(request, SurveyObj.class);
            surveyObjManagerService.addSurObj(surObjList);
            result = genSuccessMsg(surObjList, "添加成功", null);
        } catch (Exception e) {
            e.printStackTrace();
            EsiLogUtil.error(getLog(), e.getMessage());
            result = genFaultMsg(null, "添加统计对象失败", null);
        }
        return result;
    }

    @RequestMapping("/updateSurObj")
    @ResponseBody
    public Map<String, Object> updateSurObj(HttpServletRequest request) {
        Map<String, Object> result = null;
        List<SurveyObj> surObjList = null;
        try {
            EsiJsonParamUtil<SurveyObj> jsonUtil = new EsiJsonParamUtil<>();
            surObjList = jsonUtil.parseObjToList(request, SurveyObj.class);
            surveyObjManagerService.updateSurObj(surObjList);
            result = genSuccessMsg(/*surObjList*/null, "修改成功", null);
        } catch (Exception e) {
            e.printStackTrace();
            EsiLogUtil.error(getLog(), e.getMessage());
            result = genFaultMsg(null, "修改统计对象失败", null);
        }
        return result;
    }


    @RequestMapping("/delSurObj")
    @ResponseBody
    public Map<String, Object> delSurObj(HttpServletRequest request) {
        Map<String, Object> result = null;
        List<SurveyObj> surObjList = null;
        try {
            EsiJsonParamUtil<SurveyObj> jsonUtil = new EsiJsonParamUtil<>();
            surObjList = jsonUtil.parseObjToList(request, SurveyObj.class);
            surveyObjManagerService.delSurObj(surObjList);
            result = genSuccessMsg(null, "删除成功", null);
        } catch (Exception e) {
            e.printStackTrace();
            EsiLogUtil.error(getLog(), e.getMessage());
            result = genFaultMsg(null, "删除统计对象失败", null);
        }
        return result;
    }

    @RequestMapping("/querySurObjByGroupId")
    @ResponseBody
    public Map<String, Object> querySurObjByGroupId(Integer groupId) {
        Map<String, Object> result = null;
        List<SurveyObj> surObjList = null;
        try {
            surObjList = surveyObjManagerService.querySurObjByGroupId(groupId);
            result = genSuccessMsg(surObjList, "查询成功", null);
        } catch (Exception e) {
            e.printStackTrace();
            EsiLogUtil.error(getLog(), e.getMessage());
            result = genFaultMsg(null, "查询失败", null);
        }
        return result;
    }

}
