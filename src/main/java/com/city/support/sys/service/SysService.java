package com.city.support.sys.service;

import com.city.common.util.SessionUtil;
import com.city.support.sys.module.entity.Module;
import com.city.support.sys.user.entity.User;
import com.city.support.sys.user.pojo.CurrentUser;
import com.city.support.sys.user.pojo.ReportPermission;
import com.city.support.sys.user.service.ReportPermissionService;
import com.city.support.sys.user.service.UserManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by HZC on 2016/3/25.
 */
@Component
public class SysService {
    @Autowired
    private UserManagerService userManagerService;
    @Autowired
    private ReportPermissionService reportPermissionService;

    /**
     * 设置用户信息
     *
     * @param request
     * @param user
     * @mender hzc
     * @modifiedDate 2016-3-25
     */
    public void setUserInfo(HttpServletRequest request, User user) {

        user.setIp(getRemoteHost(request));
        SessionUtil.setUser(request.getSession(), user);
        //包装当前用户信息
        CurrentUser currentUser = new CurrentUser();
        currentUser.setUser(user);
        Map<Integer, ReportPermission> reportPermissionMap = reportPermissionService.findReportPermissionByUser(user);//获取用户报表报表权限
        currentUser.setReportPermissionMap(reportPermissionMap);
        List<Module> moduleList = userManagerService.findModulesByUser(user);//获取用户菜单权限
        currentUser.setModuleList(moduleList);
        request.getSession().setAttribute("currentUser", currentUser);
    }

    /**
     * 获取用户ip
     *
     * @param request
     * @return
     */
    public String getRemoteHost(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip.equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : ip;
    }
}
