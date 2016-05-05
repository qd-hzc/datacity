package com.city.support.regime.report.service;

import com.city.common.event.EsiEvent;
import com.city.common.event.listener.EsiListenerAdapter;
import com.city.common.event.watcher.EsiEventWatched;
import com.city.common.event.watcher.RptTmpWatched;
import com.city.common.event.watcher.SystemLogWatched;
import com.city.common.pojo.Constant;
import com.city.common.pojo.Page;
import com.city.common.util.ConvertUtil;
import com.city.common.util.table.builder.GenTableStrUtil;
import com.city.support.regime.collection.entity.ReportInfo;
import com.city.support.regime.collection.util.SystemLogUtils;
import com.city.support.regime.report.dao.ReportTemplateBarInfoDao;
import com.city.support.regime.report.dao.ReportTemplateDao;
import com.city.support.regime.report.dao.ReportTemplateStyleDao;
import com.city.support.regime.report.entity.ReportTemplate;
import com.city.support.regime.report.entity.ReportTemplateBarInfo;
import com.city.support.regime.report.entity.ReportTemplateStyle;
import com.city.support.sys.log.entity.SystemLog;
import com.city.support.sys.user.dao.DepartmentDao;
import com.city.support.sys.user.entity.Department;
import com.city.support.sys.user.entity.User;
import com.city.support.sys.user.pojo.CurrentUser;
import com.city.support.sys.user.pojo.ReportPermission;
import com.google.gson.Gson;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wxl on 2016/1/14 0014.
 * 报表和表样的管理
 */
@Service
@Transactional(readOnly = false, noRollbackFor = Exception.class, propagation = Propagation.REQUIRED)
public class ReportManageService {
    @Autowired
    private ReportTemplateDao reportTemplateDao;
    @Autowired
    private DepartmentDao departmentDao;
    @Autowired
    private ReportTemplateStyleDao reportTemplateStyleDao;
    @Autowired
    private ReportTemplateBarInfoDao reportTemplateBarInfoDao;

    @Autowired
    private ReportGroupService groupService;

    private EsiEventWatched esiEventWatched;

    //    用户日志监听
    private EsiEventWatched eventWatchedLog;

    @Autowired
    public ReportManageService(RptTmpWatched rptTmpWatched, SystemLogWatched logWatched) {
        //添加报表模板删除前的事件
        rptTmpWatched.addListener(new EsiListenerAdapter() {
            @Override
            public boolean handlerEvent(EsiEvent e) {
                Map<String, Object> args = e.getArgs();
                switch (e.getEventName()) {
                    case RptTmpWatched.BEFOREDELETETMP://报表模板删除前
                        String tmpIds = (String) args.get(RptTmpWatched.PARAMS_TMPIDS);
                        User user = (User) args.get(RptTmpWatched.PARAMS_USER);
                        try {
                            removeRptTmpStylesByTmps(user, tmpIds);
                        } catch (IllegalAccessException e1) {
                        }
                        break;
                    case RptTmpWatched.BEFOREDELETETMPSTYLE://删除报表表样前,删除所有相关主宾蓝信息
                        String styleIds = (String) args.get(RptTmpWatched.PARAMS_STYLEIDS);
                        reportTemplateBarInfoDao.clearStyleBarInfos(styleIds);
                        break;
                }
                return true;
            }
        }, null);
        this.esiEventWatched = rptTmpWatched;
        this.eventWatchedLog = logWatched;
    }

    /**
     * 根据条件查询报表模板
     *
     * @param name             表名,可为空
     * @param depId            部门
     * @param includeDownLevel 包含下级
     * @param periods          报送周期,可多选,可为空
     * @param rptType          报表模板类型,可为空
     * @param beginYear        开始年,可为空
     * @param endYear          结束年,可为空
     */
    public Page getRptTmpsByCondition(Page page, String name, Integer depId, boolean includeDownLevel, String periods, Integer rptType, Integer beginYear, Integer endYear) {
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
        List<ReportTemplate> tmps = reportTemplateDao.getRptTmpsByCondition(page, name, depIds, periods, rptType, beginYear, endYear);
        page.setDatas(tmps);
        page.setTotal(reportTemplateDao.getTmpCountByCondition(name, depIds, periods, rptType, beginYear, endYear));
        return page;
    }

    /**
     * 根据条件查询报表模板
     *
     * @param name             表名,可为空
     * @param groupId          分组id
     * @param includeDownLevel 包含下级
     * @param status           状态
     */
    public Page getRptTmpsByCondition(Page page, Integer groupId, String name, boolean includeDownLevel, Integer status) {

        if (null == groupId) {
            groupId = 0;
        }
        String groups;
        if (includeDownLevel) {
            List<Integer> downGroups = groupService.getDownGroups(groupId);
            String groupStrs = downGroups.toString();
            groups = groupStrs.substring(1, groupStrs.length() - 1);
        } else {
            groups = groupId.toString();
        }

        List<ReportTemplate> tmps = reportTemplateDao.getRptTmpsByCondition(page, groups, name, status);
        page.setDatas(tmps);
        page.setTotal(reportTemplateDao.getTmpCountByCondition(name, groups, status));
        return page;
    }

    /**
     * 查询所有报表模板
     */
    public List<ReportTemplate> getAllRptTmp() {
        return reportTemplateDao.queryAll();
    }

    /**
     * 根据模板id查询报表模板
     *
     * @param rptTmpId
     */
    public ReportTemplate getRptTmpById(Integer rptTmpId) {
        return reportTemplateDao.queryById(rptTmpId);
    }

    /**
     * 根据模板id查询报表模板
     *
     * @param rptTmpIds
     */
    public List<ReportTemplate> getRptTmpById(String rptTmpIds) {
        return reportTemplateDao.queryByIds(rptTmpIds);
    }

    /**
     * 根据分组id查询报表模板
     *
     * @param rptGroupIds
     */
    public List<ReportTemplate> getRptTmpByGroupId(String rptGroupIds) {
        return reportTemplateDao.queryByGroupIds(rptGroupIds);
    }

    /**
     * 保存报表
     */
    public void saveRptTmp(ReportTemplate rptTmp, Integer depId, CurrentUser currentUser) throws IllegalAccessException {
        //设置部门
        Department dep = new Department();
        dep.setId(depId);
        rptTmp.setDepartment(dep);

        Gson gson = new Gson();

        //更新
        if (rptTmp.getId() != null) {
            //添加并触发事件
            EsiEvent e = new EsiEvent();
            e.setEventName(RptTmpWatched.BEFOREUPDATETMP);
            e.getArgs().put(RptTmpWatched.PARAMS_TMPID, rptTmp.getId());
            e.getArgs().put(RptTmpWatched.PARAMS_TMPPERIOD, rptTmp.getPeriod());
            e.getArgs().put(RptTmpWatched.PARAMS_TMPDALAY, rptTmp.getSubmitDaysDelay());
            e.getArgs().put(RptTmpWatched.PARAMS_USER, currentUser.getUser());
            esiEventWatched.notifyAllListener(e);

//            记录用户操作日志
            EsiEvent logEvent = new EsiEvent();
            logEvent.setEventName(SystemLogWatched.SYS_LOG);
            logEvent.getArgs().put(SystemLogWatched.SYS_LOG,
                    SystemLogUtils.createLog(currentUser.getUser(), SystemLog.UPDATE, rptTmp, SystemLog.REPORT_TMP, "saveRptTmp"));
            eventWatchedLog.notifyAllListener(logEvent);

            reportTemplateDao.update(rptTmp, false);
        } else {//插入
            reportTemplateDao.insert(rptTmp, false);

//            用户操作日志
            EsiEvent reportLogEvent = new EsiEvent();
            reportLogEvent.setEventName(SystemLogWatched.SYS_LOG);
            reportLogEvent.getArgs().put(SystemLogWatched.SYS_LOG,
                    SystemLogUtils.createLog(currentUser.getUser(), SystemLog.INSERT, rptTmp, SystemLog.REPORT_TMP, "saveRptTmp"));
            eventWatchedLog.notifyAllListener(reportLogEvent);

            if (rptTmp.getId() != null && currentUser.getUser().isAdmin())//将新创建的模板添加的当前登录的管理员用户 add by zt
                addNewRptTmpToAdmin(currentUser, rptTmp.getId());

            //创建默认表样
            ReportTemplateStyle style = new ReportTemplateStyle();
            style.setName("默认表样");
            style.setBeginYear(rptTmp.getBeginYear());
            style.setEndYear(0);//一直有效
            style.setBeginPeriod(rptTmp.getBeginPeriod());
            style.setEndPeriod(rptTmp.getEndPeriod());
            style.setReportTemplate(rptTmp);
            style.setStyleType(Constant.RptStyleType.FIXED);
            reportTemplateStyleDao.insert(style, false);

//            用户日志
            EsiEvent styleLogEvent = new EsiEvent();
            styleLogEvent.setEventName(SystemLogWatched.SYS_LOG);
            styleLogEvent.getArgs().put(SystemLogWatched.SYS_LOG,
                    SystemLogUtils.createLog(currentUser.getUser(), SystemLog.INSERT, style, SystemLog.TMP_STYLE, "saveRptTmp"));
            eventWatchedLog.notifyAllListener(styleLogEvent);
        }

    }

    //将新创建的模板添加的当前登录的管理员用户 add by zt
    private void addNewRptTmpToAdmin(CurrentUser currentUser, Integer tmpId) {
        Map<Integer, ReportPermission> permissionMap = currentUser.getReportPermissionMap();
        ReportPermission rp = new ReportPermission();
        rp.setReportTmpId(tmpId);
        rp.setAPPROVAL(true);
        rp.setRead(true);
        rp.setWrite(true);
        permissionMap.put(tmpId, rp);

        currentUser.setReportPermissionMap(permissionMap);
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
     * 删除报表模板
     */
    public void removeRptTmps(String ids, User user) throws IllegalAccessException {
        //添加并触发事件
        EsiEvent e = new EsiEvent();
        e.setEventName(RptTmpWatched.BEFOREDELETETMP);
        e.getArgs().put(RptTmpWatched.PARAMS_TMPIDS, ids);
        e.getArgs().put(RptTmpWatched.PARAMS_USER, user);
        esiEventWatched.notifyAllListener(e);

        EsiEvent eventLog = new EsiEvent();
        List<ReportTemplate> list = getRptTmpById(ids);
        eventLog.setEventName(SystemLogWatched.SYS_LOG_LIST);
        eventLog.getArgs().put(SystemLogWatched.SYS_LOG_LIST,
                SystemLogUtils.createLogList(user, SystemLog.DELETE, list, SystemLog.REPORT_TMP, "removeRptTmps"));
        eventWatchedLog.notifyAllListener(eventLog);

        //删除模板
        reportTemplateDao.removeRptTmps(ids);
    }

    /**
     * 复制报表模板
     */
    public void copyRptTmps(String tmpIds, CurrentUser currentUser) throws IllegalAccessException {
        ConvertUtil<ReportTemplate> c = new ConvertUtil<>();
        String name = ReportTemplate.class.getName();
        String[] tmpIdList = tmpIds.split(",");
        ReportTemplate tmp = null;//从数据库查出来的实体
        ReportTemplate tmpC = null;//要复制的实体
        User user = currentUser.getUser();
        for (String tmpId : tmpIdList) {
            tmp = reportTemplateDao.queryById(Integer.parseInt(tmpId));
            tmpC = new ReportTemplate();
            c.replication(tmp, tmpC, name);
            tmpC.setName(tmp.getName() + "_副本");
            reportTemplateDao.insert(tmpC, true);

            if (tmpC.getId() != null && user.isAdmin())//将新创建的模板添加的当前登录的管理员用户 add by zt
                addNewRptTmpToAdmin(currentUser, tmpC.getId());
            //复制对应的表样
            copyRptTmpStyles(user, tmpId, tmpC.getId());
        }

//        用户操作日志
        EsiEvent esiEvent = new EsiEvent();
        List<ReportTemplate> list = getRptTmpById(tmpIds);
        esiEvent.setEventName(SystemLogWatched.SYS_LOG_LIST);
        esiEvent.getArgs().put(SystemLogWatched.SYS_LOG_LIST,
                SystemLogUtils.createLogList(user, SystemLog.COPY, list, SystemLog.REPORT_TMP, "copyRptTmps"));
        eventWatchedLog.notifyAllListener(esiEvent);

    }

    /**
     * 复制表样
     */
    public void copyRptTmpStyles(User user, String tmpOldId, Integer tmpNewId) throws IllegalAccessException {
        List<ReportTemplateStyle> oldStyles = reportTemplateStyleDao.getRptStyleByTmp(tmpOldId);

        ArrayList<ReportTemplateStyle> logList = new ArrayList<>();

        if (oldStyles != null && oldStyles.size() > 0) {
            ConvertUtil<ReportTemplateStyle> c = new ConvertUtil<>();
            String name = ReportTemplateStyle.class.getName();
            //模板
            ReportTemplate tmp = new ReportTemplate();
            tmp.setId(tmpNewId);
            for (ReportTemplateStyle style : oldStyles) {
                ReportTemplateStyle styleC = new ReportTemplateStyle();
                c.replication(style, styleC, name);
                styleC.setReportTemplate(tmp);
                //修改已生成的表样信息中所带有的模板id
                String designStyle = style.getDesignStyle();
                if (designStyle != null && designStyle.trim().length() > 0) {
                    Element table = Jsoup.parse(designStyle).select("table").get(0);
                    table.attr(GenTableStrUtil.TableAttrName.TMP_ID, tmpNewId.toString());
                    styleC.setDesignStyle(table.toString());
                }
                String rptStyle = style.getRptStyle();
                if (rptStyle != null && rptStyle.trim().length() > 0) {
                    Element table = Jsoup.parse(rptStyle).select("table").get(0);
                    table.attr(GenTableStrUtil.TableAttrName.TMP_ID, tmpNewId.toString());
                    styleC.setRptStyle(table.toString());
                }
                //插入
                reportTemplateStyleDao.insert(styleC, true);
                //复制主宾蓝信息
                copyBarInfos(style.getId(), 0, tmpNewId, styleC.getId(), 0);

                logList.add(styleC);
            }

//            用户操作日志
            EsiEvent esiEvent = new EsiEvent();
            esiEvent.setEventName(SystemLogWatched.SYS_LOG_LIST);
            esiEvent.getArgs().put(SystemLogWatched.SYS_LOG_LIST,
                    SystemLogUtils.createLogList(user, SystemLog.COPY, logList, SystemLog.TMP_STYLE, "copyRptTmpStyles"));
            eventWatchedLog.notifyAllListener(esiEvent);
        }
    }

    /**
     * 复制表样
     */
    public void copyRptTmpStyles(User user, String styleIds) throws IllegalAccessException {
        ConvertUtil<ReportTemplateStyle> c = new ConvertUtil<>();
        String name = ReportTemplateStyle.class.getName();
        String[] styleIdList = styleIds.split(",");
        ReportTemplateStyle style = null;//从数据库查出来的实体
        ReportTemplateStyle styleC = null;//要复制的实体
        for (String styleId : styleIdList) {
            style = reportTemplateStyleDao.queryById(Integer.parseInt(styleId));
            styleC = new ReportTemplateStyle();
            c.replication(style, styleC, name);
            styleC.setName(style.getName() + "_副本");
            reportTemplateStyleDao.insert(styleC, true);
            //复制主宾蓝信息
            copyBarInfos(Integer.parseInt(styleId), 0, null, styleC.getId(), 0);
        }

//        用户操作日志
        EsiEvent esiEvent = new EsiEvent();
        List<ReportTemplateStyle> list = reportTemplateStyleDao.selectStylesByIds(styleIds);
        esiEvent.setEventName(SystemLogWatched.SYS_LOG_LIST);
        esiEvent.getArgs().put(SystemLogWatched.SYS_LOG_LIST,
                SystemLogUtils.createLogList(user, SystemLog.COPY, list, SystemLog.TMP_STYLE, "copyRptTmpStyles"));
        eventWatchedLog.notifyAllListener(esiEvent);
    }

    /**
     * 复制表样
     *
     * @param fromStyleId  源表样的id
     * @param fromParentId 源表样主宾蓝的父id
     * @param toTmpId      复制目标的模板id,非空时才修改,为空时则使用默认值,也就是同一报表模板的表样的复制
     * @param toStyleId    复制目标的表样id
     * @param toParentId   复制目标的父id
     */
    private void copyBarInfos(int fromStyleId, int fromParentId, Integer toTmpId, int toStyleId, int toParentId) {
        List<ReportTemplateBarInfo> barInfos = reportTemplateBarInfoDao.getBarInfos(fromStyleId, fromParentId, null, null);
        if (barInfos != null && barInfos.size() > 0) {
            ConvertUtil<ReportTemplateBarInfo> c = new ConvertUtil<>();
            String name = ReportTemplateBarInfo.class.getName();
            ReportTemplateBarInfo cBar = null;
            for (ReportTemplateBarInfo barInfo : barInfos) {
                cBar = new ReportTemplateBarInfo();
                c.replication(barInfo, cBar, name);
                //表样id
                cBar.setStyleId(toStyleId);
                //父id
                cBar.setParentId(toParentId);
                //模板id
                if (toTmpId != null) {
                    cBar.setTmpId(toTmpId);
                }
                //插入
                reportTemplateBarInfoDao.insert(cBar, true);
                //复制下级
                copyBarInfos(fromStyleId, barInfo.getId(), toTmpId, toStyleId, cBar.getId());
            }
        }
    }


    /**
     * 根据模板id获取表样
     */
    public List<ReportTemplateStyle> getRptStyleByTmp(String tmpIds) {
        return reportTemplateStyleDao.getRptStyleByTmp(tmpIds);
    }

    /**
     * 根据报表信息获取在有效期的表样
     */
    public ReportTemplateStyle getRptStyleByRptInfo(ReportInfo reportInfo) {
        ReportTemplateStyle reportTemplateStyle = null;
        List<ReportTemplateStyle> reportTemplateStyleList = getRptStyleByTmp(reportInfo.getTmpId().toString());
        for (ReportTemplateStyle rptTmpStyle : reportTemplateStyleList) {
            //获取模板有效期
            int beginYear = rptTmpStyle.getBeginYear();
            int endYear = rptTmpStyle.getEndYear();
            int beginPeriod = rptTmpStyle.getBeginPeriod();
            int endPeriod = rptTmpStyle.getEndPeriod();
            boolean isStylePeriod = getRptPeriod(reportInfo.getYear(), reportInfo.getMonth(), beginYear, endYear, beginPeriod, endPeriod);
            if (isStylePeriod) {
                reportTemplateStyle = rptTmpStyle;
                return reportTemplateStyle;
            }

        }
        return reportTemplateStyle;
    }

    /**
     * 根据表样id查询报表表样
     *
     * @param rptStyleId
     */
    public ReportTemplateStyle getRptStyleById(Integer rptStyleId) {
        return reportTemplateStyleDao.queryById(rptStyleId);
    }

    /**
     * 保存表样
     */
    public void saveRptStyle(User user, ReportTemplateStyle style, Integer tmpId) throws IllegalAccessException {
        //模板
        ReportTemplate tmp = new ReportTemplate();
        tmp.setId(tmpId);
        style.setReportTemplate(tmp);
        //更新
        if (style.getId() != null) {//修改,先查出表样内容
            ReportTemplateStyle tmpStyle = reportTemplateStyleDao.queryById(style.getId());
            style.setRptStyle(tmpStyle.getRptStyle());
            style.setDesignStyle(tmpStyle.getDesignStyle());
            reportTemplateStyleDao.getSession().merge(style);

            EsiEvent esiEvent = new EsiEvent();
            esiEvent.setEventName(SystemLogWatched.SYS_LOG);
            esiEvent.getArgs().put(SystemLogWatched.SYS_LOG,
                    SystemLogUtils.createLog(user, SystemLog.UPDATE, style, SystemLog.TMP_STYLE, "saveRptStyle"));
            eventWatchedLog.notifyAllListener(esiEvent);

        } else {
            reportTemplateStyleDao.insert(style, false);

            EsiEvent esiEvent = new EsiEvent();
            esiEvent.setEventName(SystemLogWatched.SYS_LOG);
            esiEvent.getArgs().put(SystemLogWatched.SYS_LOG,
                    SystemLogUtils.createLog(user, SystemLog.INSERT, style, SystemLog.TMP_STYLE, "saveRptStyle"));
            eventWatchedLog.notifyAllListener(esiEvent);
        }
    }

    /**
     * 删除表样
     *
     * @param user
     * @param ids
     */
    public void removeRptTmpStyles(User user, String ids) throws IllegalAccessException {

        List<ReportTemplateStyle> list = reportTemplateStyleDao.selectStylesByIds(ids);
        EsiEvent esiEvent = new EsiEvent();
        esiEvent.setEventName(SystemLogWatched.SYS_LOG_LIST);
        esiEvent.getArgs().put(SystemLogWatched.SYS_LOG_LIST,
                SystemLogUtils.createLogList(user, SystemLog.DELETE, list, SystemLog.TMP_STYLE, "removeRptTmpStyles"));
        eventWatchedLog.notifyAllListener(esiEvent);

        //添加并触发事件
        EsiEvent e = new EsiEvent();
        e.setEventName(RptTmpWatched.BEFOREDELETETMPSTYLE);
        e.getArgs().put(RptTmpWatched.PARAMS_STYLEIDS, ids);
        esiEventWatched.notifyAllListener(e);
        //删除表样
        reportTemplateStyleDao.removeRptTmpStyles(ids);


    }

    /**
     * 删除表样
     */
    public void removeRptTmpStylesByTmps(User user, String tmpIds) throws IllegalAccessException {
        List<ReportTemplateStyle> styles = getRptStyleByTmp(tmpIds);
        if (styles != null && styles.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (ReportTemplateStyle style : styles) {
                sb.append(style.getId()).append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
            removeRptTmpStyles(user, sb.toString());
        }
    }

    /**
     * 根据条件查询报表模板
     *
     * @param name 表名,可为空
     */
    public List<ReportTemplate> getRptTmpsByName(String name) {
        List<ReportTemplate> tmps = reportTemplateDao.getRptTmpsByName(name);
        return tmps;
    }

    /**
     * 查询部门下报表
     *
     * @param depId
     * @return
     */
    public List<ReportTemplate> findRptTmpsByDep(Integer depId) {
        return reportTemplateDao.getRptTmpsByDep(depId);
    }

    /**
     * 除此部门外所有部门下的模板表
     *
     * @param depId
     * @return
     */
    public List<ReportTemplate> getRptTmpsExpectDep(Integer depId) {
        return reportTemplateDao.getRptTmpsExpectDep(depId);
    }

    /**
     * 返回所有报表模板
     * <pre>
     *     根据ids，查询所有报表模板
     * </pre>
     *
     * @param ids
     * @return
     * @author crx
     * @createDate 2016-3-14
     */
    public List<ReportTemplate> getAllReportByIds(List<Integer> ids) {
        StringBuffer id = new StringBuffer();
        for (int i = 0; i < ids.size(); i++) {
            id.append(ids.get(i).toString());
            id.append(",");
        }
        id.append("-11");
        return reportTemplateDao.selectAllByIds(id.toString());
    }

    /**
     * 保存报表的分组id
     *
     * @param currentUser
     * @param reportIds   报表id
     * @param groupId     综合表分组id
     * @return
     * @author hzc
     * @createDate 2016-4-18
     */
    public void saveReportForGroup(CurrentUser currentUser, String reportIds, Integer groupId) throws IllegalAccessException {

        reportTemplateDao.updateReportsGroupId(reportIds, groupId);

//        记录用户操作日志
        EsiEvent esiEvent = new EsiEvent();
        esiEvent.setEventName(SystemLogWatched.SYS_LOG_LIST);
        List<ReportTemplate> rptTmpById = getRptTmpById(reportIds);
        esiEvent.getArgs().put(SystemLogWatched.SYS_LOG_LIST,
                SystemLogUtils.createLogList(currentUser.getUser(), SystemLog.UPDATE, rptTmpById, SystemLog.REPORT_GROUP, "saveReportForGroup"));
        eventWatchedLog.notifyAllListener(esiEvent);
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
}
