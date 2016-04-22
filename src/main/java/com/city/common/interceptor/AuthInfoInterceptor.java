package com.city.common.interceptor;

import com.city.common.pojo.Constant;
import com.city.common.util.EsiLogUtil;
import com.city.common.util.SessionUtil;
import com.city.support.sys.controller.SysIndexController;
import com.city.support.sys.module.entity.Module;
import com.city.support.sys.service.SysService;
import com.city.support.sys.user.entity.User;
import com.city.support.sys.user.pojo.CurrentUser;
import com.city.support.sys.user.pojo.ReportPermission;
import com.city.support.sys.user.service.ReportPermissionService;
import com.city.support.sys.user.service.UserManagerService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 请求拦截器,查看请求和参数
 *
 * @author wys
 */
public class AuthInfoInterceptor extends HandlerInterceptorAdapter {
    Logger log = EsiLogUtil.getLogInstance(this.getClass());

    @Autowired
    private SysService sysService;
    @Autowired
    private UserManagerService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        //获取当前请求路径
        String url = request.getRequestURI();
        if (isAccessURL(url)) {
            return super.preHandle(request, response, handler);
        }

        //获取当前用户
        User user = SessionUtil.getUser(request.getSession());

        //如果已有用户说明已登录
        if (user == null) {

            /**
             * 判断手机端用户
             * 手机端用户请求添加header属性 x-app:用户id
             * @mender hzc
             * @modifiedDate 2016-3-25
             */
            String xApp = request.getHeader("x-app");
            if (!StringUtils.isEmpty(xApp)) {

                User appUser = userService.findUserById(Integer.parseInt(xApp));

                if (null == appUser) {
                    response.getWriter().write("{success:false,code:401,msg:\"请求超权\"}");
                    return false;
                }

                sysService.setUserInfo(request, appUser);

            } else {

                response.addHeader("toIndex", "yes");
                request.getRequestDispatcher("/login.jsp").forward(request, response);
                return false;
            }
        }
        return super.preHandle(request, response, handler);
    }

    public boolean isAccessURL(String url) {

        //TODO 待添加可无权限通过URL 后期添加配置文件
        if (url != null && (url.indexOf("login") > 0)) {
            return true;
        }
        return false;
    }

}
