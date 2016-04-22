package com.city.support.sys.user.pojo;

import com.city.support.sys.module.entity.Module;
import com.city.support.sys.user.entity.User;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by zhoutao on 2016/3/9.
 * 当前登录用户
 * 包含用户信息和报表权限信息
 */
public class CurrentUser {
    private User user;
    private Map<Integer, ReportPermission> reportPermissionMap;
    private List<Module> moduleList;


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Map<Integer, ReportPermission> getReportPermissionMap() {
        return reportPermissionMap;
    }

    public void setReportPermissionMap(Map<Integer, ReportPermission> reportPermissionMap) {
        this.reportPermissionMap = reportPermissionMap;
    }

    public List<Module> getModuleList() {
        return moduleList;
    }

    public void setModuleList(List<Module> moduleList) {
        this.moduleList = moduleList;
    }

    /**
     * 获取当前登录用户的包装类
     *
     * @param request
     * @return
     */
    public static CurrentUser getCurrentUser(HttpServletRequest request) {
        return (CurrentUser) request.getSession().getAttribute("currentUser");
    }

    /**
     * 获取当前用户的报表权限
     * 以报表模板ID为key,以ReportPermission报表权限包装类为值 的MAP集合
     *
     * @param request
     * @return Map<Integer, ReportPermission>
     */
    public static Map<Integer, ReportPermission> getReportPermission(HttpServletRequest request) {
        return getCurrentUser(request).getReportPermissionMap();
    }

    /**
     * 获取当前用户的报表权限
     * 以报表模板ID为key,以ReportPermission报表权限包装类为值 的MAP集合
     *
     * @param user
     * @return
     */
    public static Map<Integer, ReportPermission> getReportPermission(CurrentUser user) {
        return user.getReportPermissionMap();
    }

    /**
     * 获取当用户的菜单权限
     *
     * @param request
     * @return List<Module>
     */
    public static List<Module> getModules(HttpServletRequest request) {
        return getCurrentUser(request).getModuleList();
    }

    //获取某报表报表权限
    private static ReportPermission findReportPermissionByReport(HttpServletRequest request, Integer reportId) {
        ReportPermission rp = null;
        Map<Integer, ReportPermission> map = getReportPermission(request);
        if (map != null) {
            rp = map.get(reportId);
        }

        return rp;
    }

    /**
     * 判断当前用户是否有读取某报表的权限
     *
     * @param request
     * @param rmpId   报表模板ID
     * @return true false
     */
    public static boolean hasReadPermission(HttpServletRequest request, Integer rmpId) {
        boolean result = false;
        ReportPermission rp = findReportPermissionByReport(request, rmpId);
        if (rp != null)
            result = rp.isRead();

        return result;
    }

    /**
     * 判断当前用户是否有填报某报表的权限
     *
     * @param request
     * @param rmpId   报表模板ID
     * @return true false
     */
    public static boolean hasWritePermission(HttpServletRequest request, Integer rmpId) {
        boolean result = false;
        ReportPermission rp = findReportPermissionByReport(request, rmpId);
        if (rp != null)
            result = rp.isWrite();

        return result;
    }

    /**
     * 判断当前用户是否有审核某报表的权限
     *
     * @param request
     * @param rmpId   报表模板ID
     * @return true false
     */
    public static boolean hasApprovalPermission(HttpServletRequest request, Integer rmpId) {
        boolean result = false;
        ReportPermission rp = findReportPermissionByReport(request, rmpId);
        if (rp != null)
            result = rp.isAPPROVAL();

        return result;
    }
}
