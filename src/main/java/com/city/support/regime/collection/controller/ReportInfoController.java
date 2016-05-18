package com.city.support.regime.collection.controller;

import com.city.common.controller.BaseController;
import com.city.common.event.EsiEvent;
import com.city.common.event.watcher.SystemLogWatched;
import com.city.common.pojo.Constant;
import com.city.common.pojo.Page;
import com.city.common.util.EsiJsonParamUtil;
import com.city.common.util.EsiLogUtil;
import com.city.common.util.SessionUtil;
import com.city.support.regime.collection.entity.ReportInfo;
import com.city.support.sys.log.entity.SystemLog;
import com.city.support.regime.collection.pojo.RptHtmlPojo;
import com.city.support.regime.collection.service.ReportDataService;
import com.city.support.regime.collection.service.ReportInfoService;
import com.city.support.sys.log.service.SystemLogService;
import com.city.support.regime.collection.util.ImportUtils;
import com.city.support.regime.report.entity.*;
import com.city.support.regime.report.service.ReportManageService;
import com.city.support.sys.user.entity.User;
import com.city.support.sys.user.pojo.CurrentUser;
import com.city.support.sys.user.pojo.ReportPermission;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.parser.XmlTreeBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Created by wgx on 2016/1/28.
 */
@Controller
@RequestMapping("/support/regime/collection")
public class ReportInfoController extends BaseController {
    @Autowired
    private ReportInfoService reportInfoService;
    @Autowired
    private ReportDataService reportDataService;
    @Autowired
    private ReportManageService reportManageService;
    private String rptTmp;

    private SystemLogWatched systemLogWatched;

    /**
     * 报表查询页面
     */
    @RequestMapping("/reportInfoConfig")
    public ModelAndView reportInfoConfig() {
        Gson gson = new Gson();
        ModelAndView mv = new ModelAndView("support/regime/collection/reportInfoConfig");
        //报表类型
        List<Map<String, Object>> rptTypes = Constant.RptType.getAllForArray();
        mv.addObject("rptTypes", gson.toJson(rptTypes));
        //报送周期
        List<Map<String, Object>> periods = Constant.PeriodType.getAllForArray();
        mv.addObject("periods", gson.toJson(periods));
        //填报状态
        List<Map<String, Object>> rptStatus = Constant.RPT_STATUS.getAllForArray();
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
    public Page queryReportInfos(Page page, Integer groupId, boolean includeGroupChildren, String name, Integer depId, boolean includeDownLevel, Integer rptStatus, String periods, Integer type, Integer beginYear, Integer endYear, HttpServletRequest request) {
        // 获取有查看权限的报表
        Map<Integer, ReportPermission> permission = CurrentUser.getReportPermission(request);
        LinkedList<Integer> rptTmpIds = new LinkedList<>();
        Set<Integer> set = permission.keySet();
        for (Integer key : set) {
            ReportPermission reportPermission = permission.get(key);
            if (reportPermission.isRead()) {
                rptTmpIds.add(reportPermission.getReportTmpId());
            }
        }
        return reportInfoService.getReportInfosByCondition(page, request, groupId, includeGroupChildren, rptTmpIds, name, depId, includeDownLevel, rptStatus, periods, type, beginYear, endYear);
    }

    /**
     * 生成往期报表
     */
    @RequestMapping("/createAllReportInfos")
    @ResponseBody
    public Map<String, Object> createAllReport(Integer rptTmpId, String yearArray, HttpServletRequest request) {
        Map<String, Object> result = null;
        Gson gson = new Gson();
        Type type = new TypeToken<List<Map<String, String>>>() {
        }.getType();
        try {
            // 获取当前用户
            User user = SessionUtil.getUser(request.getSession());
            List<Map<String, String>> list = gson.fromJson(yearArray, type);
            // 根据模板id获取模板
            ReportTemplate reportTemplate = reportManageService.getRptTmpById(rptTmpId);
            List<ReportTemplate> reportTemplateList = new ArrayList<ReportTemplate>();
            reportTemplateList.add(reportTemplate);
            boolean isRptInfoExist = false;
            for (int i = 0; i < list.size(); i++) {
                Map<String, String> map = list.get(i);
                String year = map.get("year");
                String months = map.get("month");
                String[] monthList = months.split(",");
                for (int j = 0; j < monthList.length; j++) {
                    int count = reportInfoService.createAllReportInfos(user, reportTemplate, Integer.valueOf(year), Integer.valueOf(monthList[j]));
                    if (count == ReportInfo.SUCCESS) {
                        result = genSuccessMsg(null, "生成报表成功", 200);
                    } else if (count == ReportInfo.EXIST) {
                        isRptInfoExist = true;
                    }
                }
            }
            if (result == null) {
                if (isRptInfoExist) {
                    result = genFaultMsg(null, "已经生成过报表", 200);
                } else {
                    result = genFaultMsg(null, "生成报表失败", 500);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = genFaultMsg(null, "生成报表失败", 500);
        }
        return result;
    }

    /**
     * 查询当前模板已经生成的报表
     */
    @RequestMapping("/isReportInfosExist")
    @ResponseBody
    public Map<String, Object> isReportInfosExist(Integer rptTmpId) {
        Map<String, Object> result = new HashMap<>();
        List<ReportInfo> reportInfoList = reportInfoService.getReportInfosByRptTmpId(rptTmpId);
        List<Map<String, Object>> timeExistList = new ArrayList<>();
        for (ReportInfo reportInfo : reportInfoList) {
            Map<String, Object> timeExistMap = new HashMap<>();
            timeExistMap.put("year", reportInfo.getYear());
            timeExistMap.put("month", reportInfo.getMonth());
            timeExistList.add(timeExistMap);
        }
        result.put("timeExistList", timeExistList);
        Calendar calendar = Calendar.getInstance();
        //获取当前年月；
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        result.put("nowyear", year);
        result.put("nowmonth", month);
        return result;
    }

    /**
     * 数据填报
     */
    @RequestMapping("/dataCollection")
    public ModelAndView dataCollection(Integer rptInfoId, Boolean isReview,Boolean isReadOnly, HttpServletRequest request) {
        ModelAndView mv = new ModelAndView("support/regime/collection/dataCollection");
        // 获取当前用户
        User user = SessionUtil.getUser(request.getSession());
        //判断当前用户是否有填报某报表的权限
        Boolean isWrite = false;
        Boolean isApproval = false;
        Gson gson = new Gson();
        if (rptInfoId != null) {
            ReportInfo reportInfo = reportInfoService.getReportInfosByRptInfoId(rptInfoId);
            //判断当前用户是否有填报某报表的权限
            isWrite = CurrentUser.hasWritePermission(request, reportInfo.getTmpId());
            isApproval = CurrentUser.hasApprovalPermission(request, reportInfo.getTmpId());
            mv.addObject("year", reportInfo.getYear());
            mv.addObject("month", reportInfo.getMonth());
            mv.addObject("isWrite", isWrite);
            mv.addObject("isApproval", isApproval);
            mv.addObject("rptInfoId", rptInfoId);
            mv.addObject("rptStatus", reportInfo.getRptStatus());
            List<Map<String, Object>> rptInfos = reportInfoService.getRptInfos(reportInfo.getTmpId(), isReview);
            mv.addObject("rptInfos", gson.toJson(rptInfos));
            mv.addObject("rptTmpId", reportInfo.getTmpId());
            mv.addObject("rptTmpName", reportInfo.getName());
            List<Map<String, Object>> years = getAllYears(rptInfos);
            Integer period = reportInfo.getPeriod();
            mv.addObject("years", gson.toJson(years));
            mv.addObject("period", period);
            if (isReview == null||!isReview) {
                //填报状态
                List<Map<String, Object>> rptStatusList = Constant.RPT_STATUS.getAllForArray();
                mv.addObject("rptStatusList", gson.toJson(rptStatusList));
                mv.addObject("isReview", false);

            } else {
                //审核状态
                List<Map<String, Object>> rptStatusList = Constant.RPT_STATUS.getReviewForArray();
                mv.addObject("rptStatusList", gson.toJson(rptStatusList));
                mv.addObject("isReview", isReview);
            }
            if(isReadOnly ==null){
                mv.addObject("isReadOnly", false);
            }else{
                mv.addObject("isReadOnly", isReadOnly);
            }
        }

        return mv;
    }

    private List<Map<String, Object>> getAllYears(List<Map<String, Object>> rptInfos) {
        int minYear = Integer.MAX_VALUE;
        int maxYear = Integer.MIN_VALUE;
        if(rptInfos!=null){
            for(Map<String, Object> map:rptInfos){
                int year = (int)map.get("year");
                if(year>maxYear){
                    maxYear = year;
                }
                if(year<minYear){
                    minYear = year;
                }
            }
        }
        List<Map<String, Object>> years = new ArrayList<>();
        for(int i=0;i<=maxYear-minYear;i++){
            Map<String, Object> map = new HashMap<>();
            map.put("text",maxYear-i+"年");
            map.put("value",maxYear-i);
            years.add(map);
        }
        return years;
    }

    /**
     * 获取表样
     *
     * @param rptInfoId
     * @param request
     * @return
     */
    @RequestMapping("/getRptInfoHtml")
    @ResponseBody
    public Map<String, Object> getRptInfoHtml(Integer rptInfoId, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        //判断当前用户的报表的权限
        Boolean isRead = false;
        Boolean isWrite = false;
        Boolean isApproval = false;
        try {
            if (rptInfoId != null) {
                ReportInfo reportInfo = reportInfoService.getReportInfosByRptInfoId(rptInfoId);
                //判断当前用户的报表的权限
                isRead = CurrentUser.hasReadPermission(request, reportInfo.getTmpId());
                String table = reportInfoService.getRptInfoHtml(rptInfoId);
                if (StringUtils.isEmpty(table) && isRead) {
                    result.put("table", "暂无表样");
                } else if (isRead) {
                    result.put("table", table);
                } else {
                    result.put("table", "您没有查看该报表权限！");
                }
            }else{
                result.put("table", "");
            }

        } catch (Exception e) {
            e.printStackTrace();
            result.put("table", "获取表样失败");
        }
        return result;
    }


    /**
     * 保存与提交数据
     *
     * @param tableHtml      带数据的表样
     * @param reportId       报表id
     * @param rptStatus      报表状态
     * @param dataType       数据类型
     * @param collectionType 采集类型
     * @param request
     * @return
     */
    @RequestMapping("saveOrSubmitRptData")
    @ResponseBody
    public Map<String, Object> saveOrSubmitRptData(String tableHtml, Integer reportId, Integer rptStatus, Integer dataType, Integer collectionType, HttpServletRequest request) {
        Map<String, Object> result = null;
        String success = "";
        String failure = "";
        Integer operateType = 0;
        //判断当前用户的报表的权限
        Boolean isRead = false;
        Boolean isWrite = false;
        Boolean isApproval = false;
        if (rptStatus == Constant.RPT_STATUS.DRAFT || rptStatus == Constant.RPT_STATUS.REJECT) {
            success = "保存成功";
            failure = "保存失败";
            operateType = Constant.OPERATE_TYPE.DRAFT;
        }
        if (rptStatus == Constant.RPT_STATUS.WAITING_PASS) {
            success = "提交成功";
            failure = "提交失败";
            operateType = Constant.OPERATE_TYPE.WAITING_PASS;
        }
        try {
            // 获取当前用户
            User user = SessionUtil.getUser(request.getSession());
            ReportInfo reportInfo = reportInfoService.getReportInfosByRptInfoId(reportId);
            //判断当前用户的报表的权限
            isWrite = CurrentUser.hasWritePermission(request, reportInfo.getTmpId());
            Integer status = reportInfo.getRptStatus();
            String time = reportInfo.getTime();
            Integer year = reportInfo.getYear();
            Integer month = reportInfo.getMonth();
            Integer depId = reportInfo.getDptId();
            //将HTML解析成一个Document
            if (!isWrite) {
                return genFaultMsg(null, "没有修改报表的权限", 500);
            }
            if (tableHtml != null) {
                Document doc = Jsoup.parse(tableHtml, "", new Parser(new XmlTreeBuilder()));
                List<RptHtmlPojo> rptHtmlList = ImportUtils.getRptHtml(reportId, dataType, collectionType, time, year, month, doc, depId);
                reportDataService.saveOrSubmitRptDataList(user, rptHtmlList, status, reportInfo);
/*                for (RptHtmlPojo rptHtml : rptHtmlList) {
                    reportDataService.saveOrSubmitRptData(user,rptHtml, status,reportInfo);
                }*/
                result = genSuccessMsg(null, success, 200);
                if (reportInfo.getRptStatus() != Constant.RPT_STATUS.REJECT && reportInfo.getRptStatus() != Constant.RPT_STATUS.PASS && reportInfo.getRptStatus() != Constant.RPT_STATUS.WAITING_PASS) {
                    //修改报表状态
                    reportInfoService.updateStatus(user, reportId, rptStatus, request);
                } else if (reportInfo.getRptStatus() == Constant.RPT_STATUS.REJECT && rptStatus == Constant.RPT_STATUS.WAITING_PASS) {
                    //修改报表状态
                    reportInfoService.updateStatus(user, reportId, rptStatus, request);
                }
                /*updateReportLog(request, reportId, "", operateType);*/
            } else {
                result = genFaultMsg(null, failure, 500);
               /* updateReportLog(request, reportId, failure, operateType);*/
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = genFaultMsg(null, failure, 500);
        }
        return result;
    }

    /**
     * 获取报表时间
     *
     * @param year   当前年份
     * @param month  当前月份
     * @param period 报送周期
     * @return
     */
    public static String getTime(int year, int month, Integer period) {
        switch (period) {
            case Constant.PeriodType.YEAR:
                return year + Constant.PeriodType.YEAR_CH + Constant.FrequencyType.YEAR + Constant.PeriodType.MONTH_CH;
            case Constant.PeriodType.HALF:
                if (month <= Constant.FrequencyType.HALF_UP) {
                    return year + Constant.PeriodType.YEAR_CH + Constant.FrequencyType.HALF_UP + Constant.PeriodType.MONTH_CH;
                } else {
                    return year + Constant.PeriodType.YEAR_CH + Constant.FrequencyType.HALF_DOWN + Constant.PeriodType.MONTH_CH;
                }
            case Constant.PeriodType.QUARTER:
                if (month <= Constant.FrequencyType.QUARTER_1) {
                    return year + Constant.PeriodType.YEAR_CH + Constant.FrequencyType.QUARTER_1 + Constant.PeriodType.MONTH_CH;
                } else if (month > Constant.FrequencyType.QUARTER_1 && month <= Constant.FrequencyType.QUARTER_2) {
                    return year + Constant.PeriodType.YEAR_CH + Constant.FrequencyType.QUARTER_2 + Constant.PeriodType.MONTH_CH;
                } else if (month > Constant.FrequencyType.QUARTER_2 && month <= Constant.FrequencyType.QUARTER_3) {
                    return year + Constant.PeriodType.YEAR_CH + Constant.FrequencyType.QUARTER_3 + Constant.PeriodType.MONTH_CH;
                } else {
                    return year + Constant.PeriodType.YEAR_CH + Constant.FrequencyType.QUARTER_4 + Constant.PeriodType.MONTH_CH;
                }
            case Constant.PeriodType.MONTH:
                return year + Constant.PeriodType.YEAR_CH + month + Constant.PeriodType.MONTH_CH;
            default:
                return "";
        }
    }

    /**
     * 获取报表月份
     *
     * @param month  当前月份
     * @param period 报送周期
     * @return
     */
    private Integer getMonth(int month, Integer period) {
        switch (period) {
            case Constant.PeriodType.YEAR:
                return Constant.FrequencyType.YEAR;
            case Constant.PeriodType.HALF:
                if (month <= Constant.FrequencyType.HALF_UP) {
                    return Constant.FrequencyType.HALF_UP;
                } else {
                    return Constant.FrequencyType.HALF_DOWN;
                }
            case Constant.PeriodType.QUARTER:
                if (month <= Constant.FrequencyType.QUARTER_1) {
                    return Constant.FrequencyType.QUARTER_1;
                } else if (month > Constant.FrequencyType.QUARTER_1 && month <= Constant.FrequencyType.QUARTER_2) {
                    return Constant.FrequencyType.QUARTER_2;
                } else if (month > Constant.FrequencyType.QUARTER_2 && month <= Constant.FrequencyType.QUARTER_3) {
                    return Constant.FrequencyType.QUARTER_3;
                } else {
                    return Constant.FrequencyType.QUARTER_4;
                }
            case Constant.PeriodType.MONTH:
                return month;
            default:
                return Constant.DEFAULT_ITEM_MENU;
        }
    }

    /**
     * 判断报表模板和表样的有效期
     *
     * @param year
     * @param month
     * @param beginYear
     * @param endYear
     * @param beginPeriod
     * @param endPeriod
     * @return
     */
    private boolean getRptPeriod(int year, int month, int beginYear, int endYear, int beginPeriod, int endPeriod) {
        if (beginYear > year) {
            return false;
        }
        if (endYear != 0) {
            if (endYear < year) {
                return false;
            }
            if (beginPeriod > month) {
                return false;
            }
            if (endPeriod < month) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param request
     * @param rptId       报表id
     * @param info        操作信息
     * @param operateType 操作类型
     */
/*    private void updateReportLog(HttpServletRequest request, Integer rptId, String info, Integer operateType) {
        //获取当前用户
        User user = SessionUtil.getUser(request.getSession());
        SystemLog systemLog = new SystemLog();
        systemLog.setUserId(user.getId());
        systemLog.setUserName(user.getUserName());
        systemLog.setOperateType(operateType);
        systemLog.setInfo(info);
        Date date = new Date();
        systemLog.setOperateDate(date);
        systemLog.setSourceId(rptId);
        systemLogService.save(systemLog);
    }*/

    /**
     * 批量记录日志
     * request
     * reportInfoList       报表
     * info                  操作信息
     * operateType          操作类型
     */
/*    private void updateReportLog(HttpServletRequest request, List<ReportInfo> reportInfoList, String info, Integer operateType) {
        //添加并触发事件
        EsiEvent esiEvent = new EsiEvent();
        esiEvent.setEventName(SystemLogWatched.SYS_LOG);
        systemLogWatched.notifyAllListener(esiEvent);
        *//*for(ReportInfo reportInfo: reportInfoList){
            updateReportLog(request, reportInfo.getId(), info, operateType);
        }*//*
    }*/
    @RequestMapping("/delReportInfos")
    @ResponseBody
    public Map<String, Object> delAnalysisChartBase(HttpServletRequest request) {
        Map<String, Object> result = null;
        List<ReportInfo> datas = null;
        // 获取当前用户
        User user = SessionUtil.getUser(request.getSession());
        // 获取有修改权限的报表
        Map<Integer, ReportPermission> permission = CurrentUser.getReportPermission(request);
        LinkedList<Integer> rptTmpIdList = new LinkedList<>();
        Set<Integer> set = permission.keySet();
        for (Integer key : set) {
            ReportPermission reportPermission = permission.get(key);
            if (reportPermission.isWrite()) {
                rptTmpIdList.add(reportPermission.getReportTmpId());
            }
        }
        String rptTmpIds = "-1";
        for (Integer rptTmpId : rptTmpIdList) {
            rptTmpIds += ",";
            rptTmpIds += rptTmpId.toString();
        }
        try {
            EsiJsonParamUtil<ReportInfo> paramUtil = new EsiJsonParamUtil<>();
            datas = paramUtil.parseObjToList(request, ReportInfo.class);
            reportInfoService.remove(user, datas, rptTmpIds);
            //updateReportLog(request, datas, "", Constant.OPERATE_TYPE.DELETE);
            result = genSuccessMsg(null, "删除成功", null);
        } catch (Exception e) {
            e.printStackTrace();
            EsiLogUtil.error(getLog(), e.getMessage());
            result = genFaultMsg(null, "删除失败", null);
        }
        return result;
    }
}
