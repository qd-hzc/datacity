package com.city.support.sys.controller;

import com.city.common.util.EsiLogUtil;
import com.city.common.util.ListUtil;
import com.city.common.util.SessionUtil;
import com.city.support.sys.module.entity.Module;
import com.city.support.sys.module.service.ModuleManagerService;
import com.city.support.sys.service.SysService;
import com.city.support.sys.user.entity.Role;
import com.city.support.sys.user.entity.User;
import com.city.support.sys.user.pojo.CurrentUser;
import com.city.support.sys.user.service.ReportPermissionService;
import com.city.support.sys.user.service.UserManagerService;
import com.city.support.sys.user.util.MD5Util;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.city.common.controller.BaseController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * Created by wys on 2015/12/30.
 */
@Controller
@RequestMapping("/support/sys")
public class SysIndexController extends BaseController {
    public static Integer LOGINFAIL = 400;//登录失败返回400
    public static Integer LOGINSUCCESS = 200;//登录成功返回200

    @Autowired
    private UserManagerService userManagerService;
    @Autowired
    private ModuleManagerService moduleManagerService;
    @Autowired
    private ReportPermissionService reportPermissionService;
    @Autowired
    private SysService sysService;

    @RequestMapping("/index")
    public ModelAndView index(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();
        User user = SessionUtil.getUser(request.getSession());
        try {
            Module module = moduleManagerService.getModuleById(Module.SUPPORTID);
            Map<String, Object> root = moduleManagerService.getModuleTree(user, module);
            Gson g = new Gson();
            String rootJson = "null";
            if (root != null)
                rootJson = g.toJson(root);
            mv.setViewName(module.getModuleIndex());
            mv.addObject("MODULEID", Module.SUPPORTID);
            mv.addObject("rootJson", rootJson);
        } catch (Exception e) {
            e.printStackTrace();
            EsiLogUtil.error(getLog(), e.getMessage());
        }
        return mv;
    }

    /**
     * 跳转到标头页面
     */
    @RequestMapping("/top")
    public ModelAndView top(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView("support/sys/top");
        User user = CurrentUser.getCurrentUser(request).getUser();
        Set<Role> roles = user.getRoles();
        if (ListUtil.notEmpty(roles)) {
            for (Role role : roles) {
                role.setCreateUser(null);
            }
        }
        String userStr = null;
        try {
            userStr = new Gson().toJson(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mv.addObject("user", userStr);
        return mv;
    }

    @RequestMapping("/login")
    @ResponseBody
    public Map<String, Object> login(String loginName, String loginPwd, HttpServletRequest request) {
        Map<String, Object> userInfo = null;
        Map<String, Object> result = null;
        //验证用户
        userInfo = userManagerService.vailUser(loginName, MD5Util.MD5(loginPwd));
        //登录返回信息
        if (userInfo != null && (Boolean) userInfo.get("success")) {
            User user = (User) userInfo.get("data");

            sysService.setUserInfo(request, user);

            result = this.genSuccessMsg(user.getIndexPage(), "登录成功", LOGINSUCCESS);
            return result;
        } else {
            result = this.genFaultMsg("", "登录失败", LOGINFAIL);
            return result;
        }
    }

    /**
     * 登出
     */
    @RequestMapping("/logout")
    public String logout(HttpServletRequest request) {
        request.getSession().invalidate();//清除session
        return "redirect:/";
    }

}
