package com.city.support.regime.collection.service;

import com.city.common.event.EsiEvent;
import com.city.common.event.listener.EsiListenerAdapter;
import com.city.common.event.watcher.EsiEventWatched;
import com.city.common.event.watcher.RptTmpWatched;
import com.city.common.event.watcher.SystemLogWatched;
import com.city.common.pojo.Constant;
import com.city.common.pojo.Page;
import com.city.common.util.SessionUtil;
import com.city.resourcecategory.themes.pojo.ReportVO;
import com.city.support.regime.collection.dao.ReportDataDao;
import com.city.support.regime.collection.dao.ReportInfoDao;
import com.city.support.regime.collection.entity.ReportData;
import com.city.support.regime.collection.entity.ReportDataId;
import com.city.support.regime.collection.entity.ReportInfo;
import com.city.support.regime.collection.pojo.RptHtmlPojo;
import com.city.support.regime.collection.util.ImportUtils;
import com.city.support.regime.collection.util.SystemLogUtils;
import com.city.support.regime.report.dao.ReportTemplateDao;
import com.city.support.regime.report.entity.ReportTemplate;
import com.city.support.regime.report.entity.ReportTemplateStyle;
import com.city.support.regime.report.service.ReportGroupService;
import com.city.support.regime.report.service.ReportManageService;
import com.city.support.sys.log.entity.SystemLog;
import com.city.support.sys.log.service.SystemLogService;
import com.city.support.sys.user.dao.DepartmentDao;
import com.city.support.sys.user.entity.Department;
import com.city.support.sys.user.entity.User;
import com.city.support.sys.user.pojo.CurrentUser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.parser.XmlTreeBuilder;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by wgx on 2016/1/28.
 */
@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
public class ReportInfoService {

    @Autowired
    private ReportManageService reportManageService;
    @Autowired
    private ReportInfoDao reportInfoDao;
    @Autowired
    private DepartmentDao departmentDao;
    @Autowired
    private ReportDataDao reportDataDao;
    @Autowired
    private ReportTemplateDao reportTemplateDao;
    @Autowired
    private ReportDataService reportDataService;
    @Autowired
    private ReportGroupService reportGroupService;
    @Autowired
    private SystemLogService systemLogService;
    private EsiEventWatched esiEventWatched;
    private EsiEventWatched esiEventWatched1;

    private SystemLogWatched systemLogWatched;

    @Autowired
    public ReportInfoService(RptTmpWatched rptTmpWatched, SystemLogWatched systemLogWatched) {
        esiEventWatched = rptTmpWatched;
        esiEventWatched1 = systemLogWatched;
        esiEventWatched.addListener(new EsiListenerAdapter() {
            @Override
            public boolean handlerEvent(EsiEvent eEvent) {
                Map<String, Object> args = eEvent.getArgs();
                User user = null;
                switch (eEvent.getEventName()) {
                    case RptTmpWatched.BEFOREDELETETMP://报表模板删除前
                        String tmpIds = (String) args.get(RptTmpWatched.PARAMS_TMPIDS);
                        user = (User) args.get(RptTmpWatched.PARAMS_USER);
                        removeRptInfos(user, tmpIds);
                        break;
                    case RptTmpWatched.BEFOREUPDATETMP://报表模板修改前
                        ReportTemplate tmp = (ReportTemplate) args.get(RptTmpWatched.PARAMS_TMP);
                        user = (User) args.get(RptTmpWatched.PARAMS_USER);
                        updatePeriodAndDalay(user, tmp);
                        break;
                }
                return true;
            }
        }, null);
    }

    /**
     * 根据条件查询所有报表
     */
    public Page getReportInfosByCondition(Page page, HttpServletRequest request, Integer groupId, boolean includeGroupChildren, LinkedList<Integer> rptTmpIds, String name, Integer depId, boolean includeDownLevel, Integer rptStatus, String periods, Integer rptType, Integer beginYear, Integer endYear) {

        //获取所要查询的部门id
        String depIds = null;
        if (depId != null) {
            if (includeDownLevel) {
                List<Integer> depDownIds = getDepDownIds(depId);
                String depStrs = depDownIds.toString();
                depIds = depStrs.substring(1, depStrs.length() - 1);
            } else {
                depIds = depId.toString();
            }
        }
        String groupIds = null;
        List<ReportTemplate> reportTemplateList = null;
        if (groupId != null) {
            if (includeGroupChildren) {
                List<Integer> groupDownIds = reportGroupService.getDownGroups(groupId);
                String groupStrs = groupDownIds.toString();
                groupIds = groupStrs.substring(1, groupStrs.length() - 1);
            } else {
                groupIds = groupId.toString();
            }
            reportTemplateList = reportTemplateDao.queryByGroupIds(groupIds);
        }
        List<ReportInfo> reportInfoList = reportInfoDao.getReportInfosByCondition(page, reportTemplateList, groupId, rptTmpIds, name, depIds, rptStatus, periods, rptType, beginYear, endYear);
        page.setDatas(reportInfoList);
        page.setTotal(reportInfoDao.getReportInfoCountByCondition(reportTemplateList, groupId, rptTmpIds, name, depIds, rptStatus, periods, rptType, beginYear, endYear));
        return page;
    }

    private void sendListener(User user, Integer operateType, ReportInfo reportInfo, String method) {
        SystemLog sysLog = null;
        sysLog = SystemLogUtils.createReportInfoLog(user, operateType, reportInfo, method);
        //添加并触发事件
        EsiEvent esiEvent = new EsiEvent();
        esiEvent.setEventName(SystemLogWatched.SYS_LOG);
        esiEvent.getArgs().put(SystemLogWatched.SYS_LOG, sysLog);
        esiEventWatched1.notifyAllListener(esiEvent);
    }

    private void sendListener(User user, Integer operateType, ReportInfo reportInfo, String method, List<ReportData> reportDataList) {
        List<SystemLog> systemLogList = null;
        systemLogList = SystemLogUtils.createReportDataLog(user, operateType, reportInfo, method, reportDataList);
        //添加并触发事件
        EsiEvent esiEvent = new EsiEvent();
        esiEvent.setEventName(SystemLogWatched.SYS_LOG_LIST);
        esiEvent.getArgs().put(SystemLogWatched.SYS_LOG_LIST, systemLogList);
        esiEventWatched1.notifyAllListener(esiEvent);
    }

    private void sendListener(User user, Integer operateType, List<ReportInfo> reportInfoList, String method) {
        List<SystemLog> sysLogList = SystemLogUtils.createReportInfoLog(user, operateType, reportInfoList, method);
        //添加并触发事件
        EsiEvent esiEvent = new EsiEvent();
        esiEvent.setEventName(SystemLogWatched.SYS_LOG_LIST);
        esiEvent.getArgs().put(SystemLogWatched.SYS_LOG_LIST, sysLogList);
        esiEventWatched1.notifyAllListener(esiEvent);
    }

    /**
     * 根据模板id查询
     */
    public List<ReportInfo> getReportInfosByRptTmpId(Integer rptTmpId) {
        return reportInfoDao.getReportInfosByRptTmpId(rptTmpId);
    }

    /**
     * 根据报表id查询
     */
    public ReportInfo getReportInfosByRptInfoId(Integer rptInfoId) {
        return reportInfoDao.queryById(rptInfoId);
    }

    /**
     * 根据多个报表id查询
     */
    public List<ReportInfo> getReportInfosByRptInfoIds(String ids) {
        return reportInfoDao.queryByIds(ids);
    }

    public List<ReportInfo> getReportInfosByRptInfoAndTmpIds(String rptTmpIds, String rptInfoIds) {
        return reportInfoDao.queryByRptInfoAndTmpIds(rptTmpIds, rptInfoIds);
    }

    /**
     * 根据时间和模板id查询
     *
     * @param year
     * @param month
     * @param rptTmpId
     */
    public ReportInfo getReportInfosByTimeAndTmpId(Integer year, Integer month, Integer rptTmpId) {
        return reportInfoDao.queryByTimeAndTmpId(year, month, rptTmpId).get(0);
    }

    /**
     * 生成当期报表
     */
    public int createReportInfos(User user, List<ReportTemplate> reportTemplateList) {
        int result = ReportInfo.EXIST;
        //获取当前年月；
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        try {
            result = createRptInfo(user, reportTemplateList, year, month, true);

        } catch (Exception e) {
            e.printStackTrace();
            return ReportInfo.FAIL;
        }
        return result;
    }

    /**
     * 生成上月报表
     */
    public int createLastMonthReportInfos(User user, List<ReportTemplate> reportTemplateList) {
        int result = ReportInfo.EXIST;
        //获取当前年月；
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        if (month == 0) {
            year -= 1;
            month = 12;
        }
        try {
            result = createRptInfo(user, reportTemplateList, year, month, true);

        } catch (Exception e) {
            e.printStackTrace();
            return ReportInfo.FAIL;
        }
        return result;
    }

    /**
     * 生成往期报表
     */
    public int createAllReportInfos(User user, ReportTemplate reportTemplate, int year, int month) {
        int result = ReportInfo.EXIST;
        try {
            List<ReportTemplate> reportTemplateList = new ArrayList<ReportTemplate>();
            reportTemplateList.add(reportTemplate);
            result = createRptInfo(user, reportTemplateList, year, month, false);

        } catch (Exception e) {
            e.printStackTrace();
            return ReportInfo.FAIL;
        }
        return result;
    }

    /**
     * 根据日期生成报表
     *
     * @param reportTemplateList 报表模板list
     * @param year               年
     * @param month              月
     * @return
     */
    private int createRptInfo(User user, List<ReportTemplate> reportTemplateList, int year, int month, boolean isAuto) {
        int result = ReportInfo.SUCCESS;
        boolean styleValid = false;
        try {
            for (ReportTemplate reportTemplate : reportTemplateList) {
                //根据模板有效期来判断是否生成报表
                int rptBeginYear = reportTemplate.getBeginYear();
                int rptEndYear = reportTemplate.getEndYear();
                int rptBeginPeriod = reportTemplate.getBeginPeriod();
                int rptEndPeriod = reportTemplate.getEndPeriod();
                // 根据模板的报送频率判断是否生成报表
                String frequency = reportTemplate.getFrequency();
                String[] frequencies = new String[]{};
                if (frequency.length() > 0) {
                    frequencies = frequency.split(",");
                }
                List<String> tempList = Arrays.asList(frequencies);
                if (!tempList.contains(String.valueOf(month))) {// 报送频率中不包含此月份
                    continue;
                }
                // 报送周期
                int period = reportTemplate.getPeriod();
                int rptmonth = month;
                if (isAuto) {
                    rptmonth = getMonth(month, period);
                    if (rptmonth > month) {//未到上报期
                        continue;
                    }
                }
                //
                boolean isRptPeriod = getRptPeriod(year, month, rptBeginYear, rptEndYear, rptBeginPeriod, rptEndPeriod);
                if (isRptPeriod) {
                    List<ReportTemplateStyle> reportTemplateStyleList = reportManageService.getRptStyleByTmp(reportTemplate.getId().toString());
                    for (ReportTemplateStyle reportTemplateStyle : reportTemplateStyleList) {
                        //获取报表时间
                        String nowDate = getTime(year, month, period);
                        int beginYear = reportTemplateStyle.getBeginYear();
                        int endYear = reportTemplateStyle.getEndYear();
                        int beginPeriod = reportTemplateStyle.getBeginPeriod();
                        int endPeriod = reportTemplateStyle.getEndPeriod();
                        boolean isStylePeriod = getRptPeriod(year, month, beginYear, endYear, beginPeriod, endPeriod);
                        if (isStylePeriod) {
                            styleValid = true;
                            int count = createReportInfo(user, reportTemplate, reportTemplateStyle, nowDate, year, rptmonth);
                            if ((count == ReportInfo.EXIST && isAuto) || count == ReportInfo.FAIL) {//生成失败
                                result++;
                            } else if (count == ReportInfo.EXIST && !isAuto) {//生成往期报表时只有一个模板id，直接返回
                                return ReportInfo.EXIST;
                            }
                            break;
                        }

                    }
                }
                if (!styleValid) {
                    result++;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            return ReportInfo.FAIL;
        }
        return result;
    }

    /**
     * 生成单个报表
     */
    public int createReportInfo(User user, ReportTemplate reportTemplate, ReportTemplateStyle reportTemplateStyle, String time, Integer year, Integer month) {

        try {
            int count = reportInfoDao.getRptInfoCount(time, reportTemplate.getId());
            if (count > 0) {
                return ReportInfo.EXIST;
            }
            ReportInfo reportInfo = new ReportInfo();
            reportInfo.setName(reportTemplate.getName());
            reportInfo.setTime(time);
            reportInfo.setYear(year);
            reportInfo.setMonth(month);
            reportInfo.setType(reportTemplate.getRptType());
            reportInfo.setPeriod(reportTemplate.getPeriod());
            reportInfo.setRptStatus(Constant.RPT_STATUS.WAITING_FILL);
            reportInfo.setSubmitStatus(Constant.SUBMIT_STATUS.NOT);
            reportInfo.setDptId(reportTemplate.getDepartment().getId());
            reportInfo.setRptStyleId(reportTemplateStyle.getId());
            reportInfo.setTmpId(reportTemplate.getId());
            reportInfo.setSubmitDaysDelay(reportTemplate.getSubmitDaysDelay());
            reportInfoDao.saveOrUpdate(reportInfo, true);
            // 添加触发事件
            sendListener(user, SystemLog.INSERT, reportInfo, Thread.currentThread().getStackTrace()[1].getMethodName());
            return ReportInfo.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ReportInfo.FAIL;
        }

    }

    /**
     * 根据模板id删除报表
     *
     * @param tmpIds
     */
    public void removeRptInfos(User user, String tmpIds) {
        List<ReportInfo> reportInfoList = getReportInfosByRptInfoAndTmpIds(tmpIds, null);
        // 添加触发事件
        sendListener(user, SystemLog.DELETE, reportInfoList, Thread.currentThread().getStackTrace()[1].getMethodName());
        for (ReportInfo reportInfo : reportInfoList) {
            List<ReportData> reportDataList = reportDataDao.queryByRptId(reportInfo.getId());
            sendListener(user, SystemLog.DELETE, reportInfo, Thread.currentThread().getStackTrace()[1].getMethodName(), reportDataList);
        }
        reportDataDao.removeRptDatasByTmpIds(tmpIds);
        reportInfoDao.removeRptInfo(tmpIds);
    }

    /**
     * 根据报表id删除报表及数据
     *
     * @param reportInfoList
     */
    public void remove(User user, List<ReportInfo> reportInfoList, String rptTmpIds) {
        StringBuilder sb = new StringBuilder();
        for (ReportInfo reportInfo : reportInfoList) {
            sb.append(reportInfo.getId()).append(",");
        }
        sb.append("-1");
        List<ReportInfo> newReportInfoList = getReportInfosByRptInfoIds(sb.toString());
        // 添加触发事件
        sendListener(user, SystemLog.DELETE, newReportInfoList, Thread.currentThread().getStackTrace()[1].getMethodName());
        for (ReportInfo reportInfo : newReportInfoList) {
            List<ReportData> reportDataList = reportDataDao.queryByRptId(reportInfo.getId());
            sendListener(user, SystemLog.DELETE, reportInfo, Thread.currentThread().getStackTrace()[1].getMethodName(), reportDataList);
            reportDataDao.removeRptDatasByInfoId(reportInfo.getId(), rptTmpIds);
            reportInfoDao.removeById(reportInfo.getId(), rptTmpIds);
        }

    }

    /**
     * 获取下级部门
     *
     * @param depId
     * @return
     */
    private List<Integer> getDepDownIds(Integer depId) {
        List<Integer> depIds = new ArrayList<>();
        depIds.add(depId);
        //下级
        List<Department> deps = departmentDao.queryDepByPid(depId);
        if (deps != null && deps.size() > 0) {
            for (Department dep : deps) {
                depIds.addAll(getDepDownIds(dep.getId()));
            }
        }
        return depIds;
    }

    /**
     * 获取报表时间
     *
     * @param year   当前年份
     * @param month  当前月份
     * @param period 报送周期
     * @return
     */
    private String getTime(int year, int month, Integer period) {
        switch (period) {
            case Constant.PeriodType.YEAR:
                return year + "年" + Constant.FrequencyType.YEAR + "月";
            case Constant.PeriodType.HALF:
                if (month <= 6) {
                    return year + "年" + Constant.FrequencyType.HALF_UP + "月";
                } else {
                    return year + "年" + Constant.FrequencyType.HALF_DOWN + "月";
                }
            case Constant.PeriodType.QUARTER:
                if (month <= 3) {
                    return year + "年" + Constant.FrequencyType.QUARTER_1 + "月";
                } else if (month > 3 && month <= 6) {
                    return year + "年" + Constant.FrequencyType.QUARTER_2 + "月";
                } else if (month > 6 && month <= 9) {
                    return year + "年" + Constant.FrequencyType.QUARTER_3 + "月";
                } else {
                    return year + "年" + Constant.FrequencyType.QUARTER_4 + "月";
                }
            case Constant.PeriodType.MONTH:
                return year + "年" + month + "月";
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
                if (month <= 6) {
                    return Constant.FrequencyType.HALF_UP;
                } else {
                    return Constant.FrequencyType.HALF_DOWN;
                }
            case Constant.PeriodType.QUARTER:
                if (month <= 3) {
                    return Constant.FrequencyType.QUARTER_1;
                } else if (month > 3 && month <= 6) {
                    return Constant.FrequencyType.QUARTER_2;
                } else if (month > 6 && month <= 9) {
                    return Constant.FrequencyType.QUARTER_3;
                } else {
                    return Constant.FrequencyType.QUARTER_4;
                }
            case Constant.PeriodType.MONTH:
                return month;
            default:
                return 0;
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
     * 修改报表状态
     *
     * @param rptTmp
     * @param user
     */
    public void updatePeriodAndDalay(User user, ReportTemplate rptTmp) {
        List<ReportInfo> reportInfoList = reportInfoDao.queryByTimeAndTmpId(null, null, rptTmp.getId());
        boolean isChange;
        for (ReportInfo reportInfo : reportInfoList) {
            isChange = false;
            if (!reportInfo.getDptId().equals(rptTmp.getDepartment().getId())) {
                reportInfo.setDptId(rptTmp.getDepartment().getId());
                reportDataDao.updateDepId(reportInfo.getId(), rptTmp.getDepartment().getId());
                // 添加触发事件
                List<ReportData> reportDataList = reportDataDao.queryByRptId(reportInfo.getId());
                sendListener(user, SystemLog.UPDATE, reportInfo, Thread.currentThread().getStackTrace()[1].getMethodName(), reportDataList);
                isChange = true;
            }
            if (!reportInfo.getPeriod().equals(rptTmp.getPeriod())) {
                reportInfo.setPeriod(rptTmp.getPeriod());
                isChange = true;
            }
            if (!reportInfo.getSubmitDaysDelay().equals(rptTmp.getSubmitDaysDelay())) {
                reportInfo.setSubmitDaysDelay(rptTmp.getSubmitDaysDelay());
                isChange = true;
            }
            if (isChange) {
                reportInfoDao.saveOrUpdate(reportInfo, true);
                // 添加触发事件
                sendListener(user, SystemLog.UPDATE, reportInfo, Thread.currentThread().getStackTrace()[1].getMethodName());
            }
        }
    }

    /**
     * 修改报表状态
     *
     * @param reportId
     * @param rptStatus
     */

    public void updateStatus(User user, Integer reportId, Integer rptStatus, HttpServletRequest request) {
        ReportInfo reportInfo = reportInfoDao.queryById(reportId);
        //判断当前用户的报表的权限
        boolean isWrite = CurrentUser.hasWritePermission(request, reportInfo.getTmpId());
        boolean isApproval = CurrentUser.hasApprovalPermission(request, reportInfo.getTmpId());
        if ((isApproval && (rptStatus.equals(Constant.RPT_STATUS.PASS) || rptStatus.equals(Constant.RPT_STATUS.REJECT))) || (isWrite && (rptStatus.equals(Constant.RPT_STATUS.DRAFT) || rptStatus.equals(Constant.RPT_STATUS.WAITING_PASS)))) {
            reportInfo.setRptStatus(rptStatus);
            updateDataStatus(user, rptStatus, reportInfo);
            reportInfoDao.saveOrUpdate(reportInfo, true);
            // 添加触发事件
            sendListener(user, rptStatus, reportInfo, Thread.currentThread().getStackTrace()[1].getMethodName());
        }
    }

    /**
     * 批量修改报表状态
     *
     * @param reportIds
     * @param rptStatus
     */
    public void batchUpdateRptInfoStatus(User user, String reportIds, Integer rptStatus, HttpServletRequest request) {
        List<ReportInfo> reportInfoList = reportInfoDao.queryByIds(reportIds);
        String rejectInfo = "";
        if (request.getParameter("info") != null) {
            rejectInfo = (String) request.getParameter("info");
        }
        for (ReportInfo reportInfo : reportInfoList) {
            //判断当前用户的报表的权限
            boolean isWrite = CurrentUser.hasWritePermission(request, reportInfo.getTmpId());
            boolean isApproval = CurrentUser.hasApprovalPermission(request, reportInfo.getTmpId());
            if ((isApproval && (rptStatus.equals(Constant.RPT_STATUS.PASS) || rptStatus.equals(Constant.RPT_STATUS.REJECT))) || (isWrite && (rptStatus.equals(Constant.RPT_STATUS.DRAFT) || rptStatus.equals(Constant.RPT_STATUS.WAITING_PASS)))) {
                reportInfo.setRptStatus(rptStatus);
                reportInfo.setRejectInfo(rejectInfo);
                updateDataStatus(user, rptStatus, reportInfo);
                reportInfoDao.saveOrUpdate(reportInfo, true);
                // 添加触发事件
                sendListener(user, rptStatus, reportInfo, Thread.currentThread().getStackTrace()[1].getMethodName());
            }
        }
    }

    private void updateDataStatus(User user, Integer rptStatus, ReportInfo reportInfo) {
        if (rptStatus.equals(Constant.RPT_STATUS.PASS)) {//审核通过后修改数据状态
            List<ReportData> reportDataList = reportDataDao.queryByRptId(reportInfo.getId());
            for (ReportData reportData : reportDataList) {
                reportData.setDataStatus(Constant.DATA_STATUS.USABLE);
                reportDataDao.saveOrUpdate(reportData, true);
            }
            // 添加触发事件
            sendListener(user, SystemLog.UPDATE, reportInfo, Thread.currentThread().getStackTrace()[1].getMethodName(), reportDataList);
        } else if (rptStatus.equals(Constant.RPT_STATUS.REJECT)) {//驳回后通过后修改数据状态
            List<ReportData> reportDataList = reportDataDao.queryByRptId(reportInfo.getId());
            for (ReportData reportData : reportDataList) {
                reportData.setDataStatus(Constant.DATA_STATUS.UNUSABLE);
                reportDataDao.saveOrUpdate(reportData, true);

            }
            // 添加触发事件
            sendListener(user, SystemLog.UPDATE, reportInfo, Thread.currentThread().getStackTrace()[1].getMethodName(), reportDataList);
        }
    }

    /**
     * 返回综合表的某个报告期的表样
     * <pre>
     *     无报表返回空
     *     填充数据，带表样，不过滤该报告期报表状态
     * </pre>
     *
     * @param rptInfoId 综合表的某个报告期
     * @return
     */
    public String getRptInfoHtml(Integer rptInfoId) throws Exception {
        ReportVO vo = new ReportVO();
        vo.setId(rptInfoId);
        vo.setHasData(1);
        vo.setHasStyle(1);
        return getRptInfoHtml(vo);
    }

    /**
     * 返回某个报告期报表
     * <pre>
     *     根据报告期id，查询该报告期报表，按照查询条件，返回是否填充数据，是否带表格宽度和高度的报告期报表
     * </pre>
     *
     * @param vo 查询条件
     *           //综合表某个报告期id
     *           id:12,
     *           //是否填充数据：0：否；1：是
     *           hasData:1,
     *           //是否带表格宽度和高度：0：否；1：是
     *           hasStyle:1
     * @return 返回表样，如果失败，返回空
     * @throws Exception
     */
    public String getRptInfoHtml(ReportVO vo) throws Exception {

        String result = null;

        //        获取综合表某期报告期
        ReportInfo reportInfo = getReportInfosByRptInfoId(vo.getId());
        //ReportTemplateStyle reportTemplateStyle = reportManageService.getRptStyleById(Integer.valueOf(reportInfo.getRptStyleId()));
        ReportTemplateStyle reportTemplateStyle = reportManageService.getRptStyleByRptInfo(reportInfo);

        if (null == reportTemplateStyle) {
            return result;
        }

        String styleHtml = reportTemplateStyle.getRptStyle();
//        处理报表
        if (null != styleHtml) {
            //将HTML解析成一个Document
            Document doc = Jsoup.parse(styleHtml, "", new Parser(new XmlTreeBuilder()));
            Elements tables = doc.select("table");

            if (tables.size() > 0) {
                Element table = tables.first();

                if (vo.getHasStyle() != 1) {
                    clearTableStyle(table);
                }

                if (vo.getHasData() == 1) {
//                   填充报表数据
                    fillTableData(reportInfo, table);
                }
                result = table.toString();
            }
        }
        return result;
    }

    /**
     * 清除表格宽度和高度
     *
     * @param table
     * @author hzc
     * @createDate 2016-3-28
     */
    private void clearTableStyle(Element table) {
        table.removeAttr("width");
        table.removeAttr("height");
        Elements tds = table.getElementsByTag("td");
        for (Element td : tds) {
            td.removeAttr("width");
            td.removeAttr("height");
        }
        Elements trs = table.getElementsByTag("tr");
        for (Element tr : trs) {
            tr.removeAttr("width");
            tr.removeAttr("height");
        }
    }

    /**
     * 报表填充数据
     *
     * @param reportInfo 报告期
     * @param table
     */
    private void fillTableData(ReportInfo reportInfo, Element table) {
        Integer rptStatus = reportInfo.getRptStatus();
        List<ReportData> reportInfoList = null;
        if (rptStatus >= Constant.RPT_STATUS.DRAFT) {//草稿有数据时从数据库读取数据
//                      先查询出所有数据，然后插入
//                      报表数据
            reportInfoList = reportDataService.getReportDataByRptId(reportInfo.getId());
        }

        Elements tds = table.select("td");
        for (Element td : tds) {
            if (td.attr("esi-type").equals(Constant.TdEsiType.DATA)) {
                Map<String, String> map = ImportUtils.getRptDataIdMap(td);
                ReportDataId reportDataId = ImportUtils.setRptDataId(map, reportInfo.getYear(), reportInfo.getMonth(), reportInfo.getId(), reportInfo.getDptId());
                ReportData reportData = null;
                if (reportInfoList != null) {
                    for (ReportData rd : reportInfoList) {
                        if (rd.getReportDataId().equals(reportDataId)) {
                            reportData = rd;
                            break;
                        }
                    }
                }
                if (reportData != null) {//数据库有数据插入数据
                    if (rptStatus == Constant.RPT_STATUS.WAITING_PASS || rptStatus == Constant.RPT_STATUS.PASS) {
                        if (reportData.getItemValue() != null && !"".equals(reportData.getItemValue())) {
                            td.html(reportData.getItemValue());
                        } else {
                            td.html("");
                        }
                    } else {
                        if (reportData.getItemValue() != null && !"".equals(reportData.getItemValue())) {
                            td.html("<input value='" + reportData.getItemValue() + "'/>");
                        } else {
                            td.html("<input value=''/>");
                        }
                    }


                } else {
                    td.html("<input value=''/>");
                }
            }
        }
    }

    /**
     * 返回综合表相关报告期
     * <pre>
     *     根据综合表id，和报告期状态，查询该综合表的所有符合要求的报告期
     *     所有报告期，按照时间倒序排序
     * </pre>
     *
     * @param rptTmpId 综合表id
     * @param status   报告期状态
     * @return
     * @author hzc
     * @createDate 2016-3-28
     */
    public List<ReportInfo> getInfosByTmpIdAndStatus(Integer rptTmpId, String status) {
        return reportInfoDao.selectInfosByTmpIdAndStatus(rptTmpId, status);
    }

    /**
     * 返回符合条件的综合表报告期集合
     * <pre>
     *     根据综合表id，报告期状态，报告期时间，查询综合表对应报告期
     * </pre>
     *
     * @param vo 查询条件：
     *           {@code
     *           //综合表id
     *           id:1,
     *           //状态：0：全部；1:待填报；2：草稿；3：待审；4：已审；5：已驳回
     *           //使用逗号分隔，可为空
     *           status:2,3,4,
     *           //报告期年，可为空
     *           year:2016,
     *           //报告期期度：年，半年，季，月，可为空
     *           m:3,
     *           //综合表频度：1：年报，2：半年报，3：季报，4：月报，可为空
     *           frequency:3
     *           }
     * @return 返回符合查询条件的所有综合表的报告期
     * @author hzc
     * @createDate 2016-3-30
     */
    public List<ReportInfo> getInfoByTime(ReportVO vo) {
        return reportInfoDao.selectInfoByTime(vo);
    }

    /**
     * 根据模板id和审核、填报状态获取该模板下的相应的报表信息
     * @param tmpId
     * @param isReview
     * @return
     */
    public List<Map<String, Object>> getRptInfos(Integer tmpId, Boolean isReview) {
        List<Map<String, Object>> result = new ArrayList<>();
        List<ReportInfo> reportInfoList = null;
        if (isReview == null||!isReview) {
            reportInfoList = getReportInfosByRptTmpId(tmpId);
        } else {
            reportInfoList = getInfosByTmpIdAndStatus(tmpId, Constant.RPT_STATUS.WAITING_PASS + "," + Constant.RPT_STATUS.PASS);
        }
        for (ReportInfo reportInfo : reportInfoList) {
            Map<String, Object> map = new HashMap<>();
            String name = reportInfo.getYear() + "年" + Constant.FrequencyType.getFrequencyName(reportInfo.getPeriod(), reportInfo.getMonth());
            Integer id = reportInfo.getId();
            Integer status = reportInfo.getRptStatus();
            Integer year = reportInfo.getYear();
            Integer month = reportInfo.getMonth();
            map.put("name",name);
            map.put("id",id);
            map.put("status",status);
            map.put("year",year);
            map.put("month",month);
            result.add(map);
        }

        return result;
    }
    public Integer getReportId(String name,Integer year ,Integer month){
        return reportInfoDao.getReportId(name,year,month);
    }

}
