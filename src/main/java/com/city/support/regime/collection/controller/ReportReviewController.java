package com.city.support.regime.collection.controller;

import com.city.common.controller.BaseController;
import com.city.common.event.EsiEvent;
import com.city.common.event.watcher.RptTmpWatched;
import com.city.common.event.watcher.SystemLogWatched;
import com.city.common.pojo.Constant;
import com.city.common.pojo.Page;
import com.city.common.util.SessionUtil;
import com.city.support.regime.collection.service.ReportInfoService;
import com.city.support.regime.collection.service.ReportReviewService;
import com.city.support.sys.log.entity.SystemLog;
import com.city.support.sys.log.service.SystemLogService;
import com.city.support.sys.user.entity.User;
import com.city.support.sys.user.pojo.CurrentUser;
import com.city.support.sys.user.pojo.ReportPermission;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by wgx on 2016/2/23.
 */
@Controller
@RequestMapping("/support/regime/review")
public class ReportReviewController extends BaseController{
    @Autowired
    private ReportInfoService reportInfoService;

    @Autowired
    private ReportReviewService reportReviewService;

    /**
     * 报表查询页面
     */
    @RequestMapping("reportReviewConfig")
    public ModelAndView reportReviewConfig() {
        Gson gson = new Gson();
        ModelAndView mv = new ModelAndView("support/regime/collection/reportReviewConfig");
        //报表类型
        List<Map<String, Object>> rptTypes = Constant.RptType.getAllForArray();
        mv.addObject("rptTypes", gson.toJson(rptTypes));
        //报送周期
        List<Map<String, Object>> periods = Constant.PeriodType.getAllForArray();
        mv.addObject("periods", gson.toJson(periods));
        //填报状态
        List<Map<String, Object>> rptStatus = Constant.RPT_STATUS.getReviewForArray();
        mv.addObject("rptStatus", gson.toJson(rptStatus));
        //报送状态
        List<Map<String, Object>> submitStatus = Constant.SUBMIT_STATUS.getAllForArray();
        mv.addObject("submitStatus", gson.toJson(submitStatus));
        //报送频率
        List<Map<String, Object>> yearFres = Constant.FrequencyType.getAllForArray(Constant.PeriodType.YEAR);
        mv.addObject("yearFres", gson.toJson(yearFres));
        List<Map<String, Object>> halfFres = Constant.FrequencyType.getAllForArray(Constant.PeriodType.HALF);
        mv.addObject("halfFres", gson.toJson(halfFres));
        List<Map<String, Object>> quarterFres = Constant.FrequencyType.getAllForArray(Constant.PeriodType.QUARTER);
        mv.addObject("quarterFres", gson.toJson(quarterFres));
        List<Map<String, Object>> monthFres = Constant.FrequencyType.getAllForArray(Constant.PeriodType.MONTH);
        mv.addObject("monthFres", gson.toJson(monthFres));
        return mv;
    }
    /**
     * 根据条件查询所有报表
     */
    @RequestMapping("/queryReportInfos")
    @ResponseBody
    public Page queryReportInfos(Page page,Integer groupId,boolean includeGroupChildren, String name, Integer depId, boolean includeDownLevel, Integer rptStatus, String periods, Integer type, Integer beginYear, Integer endYear, HttpServletRequest request) {
        // 获取有审核权限的报表
        Map<Integer, ReportPermission> permission = CurrentUser.getReportPermission(request);
        LinkedList<Integer> rptTmpIds = new LinkedList<>();
        Set<Integer> set = permission.keySet();
        for (Integer key : set) {
            ReportPermission reportPermission = permission.get(key);
            if (reportPermission.isAPPROVAL()) {
                rptTmpIds.add(reportPermission.getReportTmpId());
            }
        }
        return reportInfoService.getReportInfosByCondition(page,request,groupId,includeGroupChildren, rptTmpIds, name, depId, includeDownLevel, rptStatus, periods, type, beginYear, endYear);
    }

    /**
     * 通过或驳回
     * @param ids              报表id
     * @param rptStatus       报表状态
     * @return
     */
    @RequestMapping(value = "/rptReview")
    @ResponseBody
    public Map<String, Object> rptPass(HttpServletRequest request,String ids,Integer rptStatus,String info) {
        Map<String, Object> result = new HashMap<>();
        String success = "";
        String failure = "";
        Integer operateType = 0;

        if (rptStatus == Constant.RPT_STATUS.PASS) {
            success = "审核成功";
            failure = "审核失败";
            operateType = Constant.OPERATE_TYPE.PASS;
        }
        if (rptStatus == Constant.RPT_STATUS.REJECT) {
            success = "驳回成功";
            failure = "驳回失败";
            operateType = Constant.OPERATE_TYPE.REJECT;
        }
        if (rptStatus == Constant.RPT_STATUS.WAITING_PASS) {
            success = "提交成功";
            failure = "提交失败";
            operateType = Constant.OPERATE_TYPE.WAITING_PASS;
        }
        EsiEvent esiEvent = new EsiEvent();
        long startMili=System.currentTimeMillis();// 当前时间对应的毫秒数
        System.out.println("开始 "+startMili);


        try {
            // 获取当前用户
            User user = SessionUtil.getUser(request.getSession());
            reportInfoService.batchUpdateRptInfoStatus(user,ids, rptStatus, request);
            String[] rptIds = ids.split(",");
            /*for(String rptId:rptIds){
                updateReportLog(request,  Integer.valueOf(rptId), info, operateType);
            }*/

            result = genSuccessMsg(null, success, 200);
        } catch (Exception e) {
            e.printStackTrace();
            result = genFaultMsg(null, failure, 500);
            String[] rptIds = ids.split(",");
            /*for(String rptId:rptIds){
                updateReportLog(request,  Integer.valueOf(rptId), info, operateType);
            }*/
        }
        long endMili=System.currentTimeMillis();
        System.out.println("结束 s"+endMili);
        System.out.println("总耗时为："+(endMili-startMili)+"毫秒");
        return result;
    }

    /**
     *  @param request
     * @param reportId
     * @param info         驳回原因
     * @param operateType          操作类型
     */
/*    private void updateReportLog(HttpServletRequest request, Integer reportId, String info, Integer operateType) {
        //获取当前用户
        User user = SessionUtil.getUser(request.getSession());
        SystemLog systemLog = new SystemLog();
        systemLog.setUserId(user.getId());
        systemLog.setUserName(user.getUserName());
        systemLog.setOperateType(operateType);
        systemLog.setInfo(info);
        Date date = new Date();
        systemLog.setOperateDate(date);
        systemLog.setSourceId(reportId);
        systemLogService.save(systemLog);
    }*/

}
