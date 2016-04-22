package com.city.support.regime.report.controller;

import com.city.common.controller.BaseController;
import com.city.common.util.EsiJsonParamUtil;
import com.city.support.regime.report.entity.ReportGroup;
import com.city.support.regime.report.service.ReportGroupService;
import com.city.support.regime.report.service.ReportManageService;
import com.city.support.sys.user.entity.User;
import com.city.support.sys.user.pojo.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 综合表分组
 * Created by HZC on 2016/4/15.
 */
@Controller
@RequestMapping("/support/regime/report/reportGroup")
public class ReportGroupController extends BaseController {

    @Autowired
    private ReportGroupService groupService;

    @Autowired
    private ReportManageService manageService;

    /**
     * 返回综合表分组
     * <pre>
     *     根据综合表名字和状态，查询所有符合条件的综合表分组
     *     把分组结果封装成树结构
     * </pre>
     *
     * @param name   综合表名称
     * @param status 综合表状态
     * @return
     * @author hzc
     * @createDate 2016-4-15
     */
    @RequestMapping("/getReportGroups")
    @ResponseBody
    public List<Map<String, Object>> getReportGroups(String name, Integer status) {
        return groupService.getReportGroups(name, status);
    }

    /**
     * 保存综合表分组
     *
     * @param request
     * @param reportGroup
     * @return
     * @author hzc
     * @createDate 2016-4-15
     */
    @RequestMapping("/saveReportGroup")
    @ResponseBody
    public Map<String, Object> saveReportGroup(HttpServletRequest request, ReportGroup reportGroup) {
        Map<String, Object> result;
        try {
            if (null == reportGroup.getId()) {
                User user = CurrentUser.getCurrentUser(request).getUser();
                reportGroup.setCreatorId(user.getId());
                reportGroup.setCreateDate(new Date());
            }
            groupService.saveGroup(CurrentUser.getCurrentUser(request), reportGroup);
            result = genSuccessMsg(reportGroup, "保存成功", 200);
        } catch (Exception e) {
            e.printStackTrace();
            result = genFaultMsg(null, "分组名已存在或服務端存在異常", 500);
        }
        return result;
    }

    /**
     * 删除综合表分组
     *
     * @param id
     * @return
     * @author hzc
     * @createDate 2016-4-15
     */
    @RequestMapping("/removeGroups")
    @ResponseBody
    public Map<String, Object> removeGroups(HttpServletRequest request, Integer id) {
        Map<String, Object> result;
        try {
            groupService.removeGroups(CurrentUser.getCurrentUser(request), id);
            result = genSuccessMsg(null, "删除成功", 200);
        } catch (Exception e) {
            e.printStackTrace();
            result = genFaultMsg(null, "删除失败", 500);
        }
        return result;
    }

    /**
     * 综合表分组排序
     *
     * @param request
     * @return
     */
    @RequestMapping("/saveGroupSorts")
    @ResponseBody
    public Map<String, Object> saveGroupSorts(HttpServletRequest request) {
        Map<String, Object> result = null;
        EsiJsonParamUtil<ReportGroup> util = new EsiJsonParamUtil<>();
        try {
            List<ReportGroup> groups = util.parseObjToList(request, ReportGroup.class);
            groupService.saveGroupSorts(groups);
            result = genSuccessMsg("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            result = genFaultMsg("保存失败,服务端运行异常!");
        }
        return result;
    }

    /**
     * 保存报表的分组id
     *
     * @param reportIds
     * @param groupId
     * @return
     * @author hzc
     * @createDate 2016-4-18
     */
    @RequestMapping("/saveReportForGroup")
    @ResponseBody
    public Object saveReportForGroup(HttpServletRequest request, String reportIds, Integer groupId) throws IllegalAccessException {
        manageService.saveReportForGroup(CurrentUser.getCurrentUser(request), reportIds, groupId);
        return genSuccessMsg("请求成功", "操作成功", 200);
    }
}
