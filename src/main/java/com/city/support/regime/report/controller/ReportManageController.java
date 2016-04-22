package com.city.support.regime.report.controller;

import com.city.common.controller.BaseController;
import com.city.common.pojo.Constant;
import com.city.common.pojo.Page;
import com.city.common.util.SessionUtil;
import com.city.support.regime.report.entity.ReportTemplate;
import com.city.support.regime.report.entity.ReportTemplateStyle;
import com.city.support.regime.report.service.DesignReportService;
import com.city.support.regime.report.service.ReportManageService;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by wxl on 2016/1/14 0014.
 */
@Controller
@RequestMapping("/support/regime/report")
public class ReportManageController extends BaseController {
    @Autowired
    private ReportManageService reportManageService;

    @Autowired
    private DesignReportService designReportService;

    /**
     * 进入报表模板管理界面
     */
    @RequestMapping("/rptTmpManagePage")
    public ModelAndView rptTmpManagePage(HttpServletRequest request) {
        Gson gson = new Gson();
        ModelAndView mv = new ModelAndView("support/regime/report/rptManage");
        //当前用户
        CurrentUser currentUser = CurrentUser.getCurrentUser(request);
        mv.addObject("depId", currentUser.getUser().getDepartment().getId());
        //报表类型
        List<Map<String, Object>> rptTypes = Constant.RptType.getAllForArray();
        mv.addObject("rptTypes", gson.toJson(rptTypes));
        //表样类型
        List<Map<String, Object>> styleTypes = Constant.RptStyleType.getAllForArray();
        mv.addObject("styleTypes", gson.toJson(styleTypes));
        //统计对象类型
        List<Map<String, Object>> researchObjTypes = Constant.ResearchObjType.getAllForArray();
        mv.addObject("researchObjTypes", gson.toJson(researchObjTypes));
        //报送周期
        List<Map<String, Object>> periods = Constant.PeriodType.getAllForArray();
        mv.addObject("periods", gson.toJson(periods));
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
     * 根据条件查询报表模板
     *
     * @param name             表名,可为空
     * @param depId            部门
     * @param includeDownLevel 选中的部门是否包含下级
     * @param periods          报送周期,可多选,可为空
     * @param rptType          报表模板类型,可为空
     * @param beginYear        开始年,可为空
     * @param endYear          结束年,可为空
     */
    @RequestMapping("/getRptTmpsByCondition")
    @ResponseBody
    public Page getRptTmpsByCondition(Page page, String name, Integer depId, boolean includeDownLevel, String periods, Integer rptType, Integer beginYear, Integer endYear) {
        return reportManageService.getRptTmpsByCondition(page, name, depId, includeDownLevel, periods, rptType, beginYear, endYear);
    }

    /**
     * 根据条件查询报表模板
     *
     * @param name             表名,可为空
     * @param groupId          分组id
     * @param includeDownLevel 是否包含下级
     * @param status           状态
     */
    @RequestMapping("/getRptTmpsByGroup")
    @ResponseBody
    public Page getRptTmpsByGroup(Page page, Integer groupId, String name, boolean includeDownLevel, Integer status) {
        return reportManageService.getRptTmpsByCondition(page, groupId, name, includeDownLevel, status);
    }

    /**
     * 保存报表模板
     */
    @RequestMapping("/saveRptTmp")
    @ResponseBody
    public Map<String, Object> saveRptTmp(ReportTemplate rptTmp, Integer depId, HttpServletRequest request) {
        Map<String, Object> result;
        try {
            reportManageService.saveRptTmp(rptTmp, depId, CurrentUser.getCurrentUser(request));
            result = genSuccessMsg(null, "保存成功", 200);
        } catch (Exception e) {
            e.printStackTrace();
            result = genFaultMsg(null, "服务器端异常", 500);
        }
        return result;
    }

    /**
     * 删除报表模版
     */
    @RequestMapping("/removeRptTmps")
    @ResponseBody
    public Map<String, Object> removeRptTmps(String ids, HttpServletRequest request) {
        Map<String, Object> result;
        try {
            // 获取当前用户
            User user = SessionUtil.getUser(request.getSession());
            reportManageService.removeRptTmps(ids, user);
            result = genSuccessMsg(null, "删除成功", 200);
        } catch (Exception e) {
            e.printStackTrace();
            result = genFaultMsg(null, "删除失败", 500);
        }
        return result;
    }

    /**
     * 复制报表模板
     */
    @RequestMapping("/copyRptTmps")
    @ResponseBody
    public Map<String, Object> copyRptTmps(String tmpIds, HttpServletRequest request) {
        Map<String, Object> result;
        try {
            reportManageService.copyRptTmps(tmpIds, CurrentUser.getCurrentUser(request));
            result = genSuccessMsg(null, "操作成功", 200);
        } catch (Exception e) {
            e.printStackTrace();
            result = genFaultMsg(null, "操作失败", 500);
        }
        return result;
    }

    /**
     * 根据模板id获取表样
     *
     * @param tmpId 报表模板id
     */
    @RequestMapping("/getRptStyleByTmp")
    @ResponseBody
    public List<ReportTemplateStyle> getRptStyleByTmp(Integer tmpId) {
        return reportManageService.getRptStyleByTmp(tmpId.toString());
    }

    /**
     * 保存表样
     */
    @RequestMapping("/saveRptStyle")
    @ResponseBody
    public Map<String, Object> saveRptStyle(HttpServletRequest request, ReportTemplateStyle style, Integer tmpId) {
        Map<String, Object> result;
        try {
            reportManageService.saveRptStyle(CurrentUser.getCurrentUser(request).getUser(), style, tmpId);
            result = genSuccessMsg(null, "保存成功", 200);
        } catch (Exception e) {
            e.printStackTrace();
            result = genFaultMsg(null, "保存失败", 500);
        }
        return result;
    }

    /**
     * 删除表样
     */
    @RequestMapping("/removeRptTmpStyles")
    @ResponseBody
    public Map<String, Object> removeRptTmpStyles(HttpServletRequest request, String ids) {
        Map<String, Object> result;
        try {
            reportManageService.removeRptTmpStyles(CurrentUser.getCurrentUser(request).getUser(), ids);
            result = genSuccessMsg(null, "删除成功", 200);
        } catch (Exception e) {
            e.printStackTrace();
            result = genFaultMsg(null, "删除失败", 500);
        }
        return result;
    }

    /**
     * 复制表样
     */
    @RequestMapping("/copyRptTmpStyles")
    @ResponseBody
    public Map<String, Object> copyRptTmpStyles(HttpServletRequest request, String styleIds) {
        Map<String, Object> result;
        try {
            reportManageService.copyRptTmpStyles(CurrentUser.getCurrentUser(request).getUser(), styleIds);
            result = genSuccessMsg(null, "操作成功", 200);
        } catch (Exception e) {
            e.printStackTrace();
            result = genFaultMsg(null, "操作失败", 500);
        }
        return result;
    }

    /**
     * 返回所有可读综合表
     *
     * @param request
     * @return
     * @author crx
     * @createDate 2016-3-14
     */
    @RequestMapping("/getAllReport")
    @ResponseBody
    public Object getAllReport(HttpServletRequest request) {
        Map<Integer, ReportPermission> permission = CurrentUser.getReportPermission(request);
        LinkedList<Integer> ids = new LinkedList<>();
        Set<Integer> set = permission.keySet();
        for (Integer key : set) {
            ReportPermission reportPermission = permission.get(key);
            if (reportPermission.isRead()) {
                ids.add(reportPermission.getReportTmpId());
            }
        }
        List<ReportTemplate> list = reportManageService.getAllReportByIds(ids);
        return list;
    }

    /**
     * 根据表样id查询是否有主宾栏
     */
    @RequestMapping("/isHasBar")
    @ResponseBody
    public boolean isHasBar(Integer styleId) {
        return designReportService.isHasBar(styleId);
    }


}
