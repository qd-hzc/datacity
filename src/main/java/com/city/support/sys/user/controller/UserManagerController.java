package com.city.support.sys.user.controller;

import com.city.common.controller.BaseController;
import com.city.common.pojo.Page;
import com.city.support.regime.report.entity.ReportTemplate;
import com.city.support.regime.report.service.ReportManageService;
import com.city.support.sys.module.entity.Module;
import com.city.support.sys.module.service.ModuleManagerService;
import com.city.support.sys.user.entity.*;
import com.city.support.sys.user.pojo.PermissionType;
import com.city.support.sys.user.pojo.ReportPermissionType;
import com.city.support.sys.user.service.*;
import com.city.support.sys.user.util.MD5Util;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Type;
import java.util.*;

/**
 * 用户管理Controller
 */
@Controller
public class UserManagerController extends BaseController {
    @Autowired
    private UserManagerService userManagerService;
    @Autowired
    private DepartmentManagerService departmentManagerService;
    @Autowired
    private RoleManagerService roleManagerService;
    @Autowired
    private ReportManageService reportManageService;
    @Autowired
    private RptPermissionService rptPermissionService;
    @Autowired
    private UserRptPermissionService userRptPermissionService;
    @Autowired
    private ModuleManagerService moduleManagerService;

    @RequestMapping("/support/sys/user/userManage/jumpMainJsp")
    public ModelAndView jumpMainJsp(){
        ModelAndView mv = new ModelAndView("support/sys/user/userManage/main");
        return mv;
    }

    @RequestMapping("/support/sys/user/userManage/saveUser")
    @ResponseBody
    public Map<String, Object> saveUser(User user) {
        Map<String, Object> result = new HashMap<String, Object>();

        Integer departmentId = user.getDepartment().getId();
        Department dep = departmentManagerService.findDepById(departmentId);
        user.setDepartment(dep);
        if(user.getId() == null)
            user.setLoginPwd(MD5Util.MD5("123456"));//初始密码为123456
        else{
            if(!("".equals(user.getLoginPwd().trim()))) {//密码不为空时修改密码
                user.setLoginPwd(MD5Util.MD5(user.getLoginPwd().trim()));//修改密码
            }

            User tmpUser = userManagerService.findUserById(user.getId());
            tmpUser.setUserName(user.getUserName());
            tmpUser.setLoginName(user.getLoginName());
            tmpUser.setLoginPwd(user.getLoginPwd());
            tmpUser.setDepartment(user.getDepartment());
            tmpUser.setDuty(user.getDuty());
            tmpUser.setEmail(user.getEmail());
            tmpUser.setMobilePhone(user.getMobilePhone());
            tmpUser.setSex(user.isSex());
            tmpUser.setState(user.isState());
            tmpUser.setUserInfo(user.getUserInfo());

            user = tmpUser;

        }


        userManagerService.saveOrUpdateUser(user);

        result.put("success", true);
        result.put("userId",user.getId());
        return result;
    }

    /**
     * 删除用户 软删除 状态置为不可用
     * @param userIds
     * @return
     */
    @RequestMapping("/support/sys/user/userManage/deleteUser")
    @ResponseBody
    public Map<String, Object> deleteUser(String userIds) {
        Map<String, Object> result = new HashMap<String, Object>();

        Gson gson = new Gson();
        Type type = new TypeToken<List<String>>(){}.getType();
        List<String> idList = gson.fromJson(userIds, type);

        for(String id : idList){
            User user = userManagerService.findUserById(Integer.valueOf(id));
            user.setState(User.STATE_DISABLE);

            userManagerService.saveOrUpdateUser(user);
        }

        result.put("success", true);
        return result;
    }

    @RequestMapping("/support/sys/user/userManage/findUsersPageByNameOrLoginName")
    @ResponseBody
    public Page findUsersPageByNameOrLoginName(String name, String loginName, Page page){
        if(name == null)
            name = "";
        if(loginName == null)
            loginName = "";

        List<User> usersList = userManagerService.findUserByNameOrLoginName(name,loginName);
        page.setTotal(usersList.size());
        List<User> usersPageList = userManagerService.findUserPageByNameOrLoginName(page, name, loginName);
        //测试用
        for(User user : usersPageList){
            //user.setRoles(null);
            for(Role role : user.getRoles()){
                role.setCreateUser(null);
            }
        }

        page.setDatas(usersPageList);

        return page;
    }

    /**
     * 获取用户关联角色 分页
     * @param userId
     * @param page
     * @return
     */
    @RequestMapping("/support/sys/user/userManage/findRolesByUser")
    @ResponseBody
    public Page findRolesByUser(Integer userId, Page page){
        User user = userManagerService.findUserById(userId);
        Set<Role> roleSet = user.getRoles();//获取用户已关联角色
        List<Role> roleList = new ArrayList<Role>();
        for(Role role : roleSet){
            role.setCreateUser(null);
        }
        roleList.addAll(roleSet);

        int start = page.getStart();
        int end = start+page.getLimit();
        if(end > roleList.size())
            end = roleList.size();
        page.setDatas(roleList.subList(start, end));
        page.setTotal(roleList.size());

        return page;
    }

    /**
     * 获取用户关联菜单 分页
     * @param userId
     * @param page
     * @return
     */
    @RequestMapping("/support/sys/user/userManage/findFunsByUser")
    @ResponseBody
    public Page findFunsByUser(Integer userId, Page page){
        User user = userManagerService.findUserById(userId);

        List<Module> moduleList = userManagerService.findModulesByUser(user);//获取用户菜单权限


        int start = page.getStart();
        int end = start+page.getLimit();
        if(end > moduleList.size())
            end = moduleList.size();
        page.setDatas(moduleList.subList(start, end));
        page.setTotal(moduleList.size());

        return page;
    }



    /**
     * 更新角色的菜单权限集合
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value="/support/sys/user/userManage/updateUserRoleList")
    @ResponseBody
    public Map<String, Object> updateUserRoleList(HttpServletRequest request, HttpServletResponse response){
        Map<String, Object> result = new HashMap<String, Object>();

        String userId = request.getParameter("userId");
        userManagerService.saveOrUpdateUserRole(request, userId);//更新用户菜单权限
        result.put("success",true);
        return result;
    }



    /**
     * 根据名称获取报表
     * @param name
     * @return
     */
    @RequestMapping(value="/support/sys/user/userManage/findRptTmpsByName")
    @ResponseBody
    public List<ReportTemplate> findRptTmpsByNamePage( String name){
        List<ReportTemplate> tempList = reportManageService.getRptTmpsByName(name);

        return tempList;
    }

    /**
     * 更新用户报表权限信息
     * @param userId
     * @param dataList
     * @return
     */
    @RequestMapping(value="/support/sys/user/userManage/updateUserRptPermission")
    @ResponseBody
    public Map<String, Object> updateUserRptPermission(Integer userId, String dataList){
        Map<String, Object> result = new HashMap<String, Object>();
        userManagerService.saveOrUpdateUserRptPermission(userId, dataList);//更新用户报表权限

        result.put("success",true);
        return result;
    }



    /**
     * 查找用户默认的报表权限
     * @param request
     * @return
     */
    @RequestMapping(value="/support/sys/user/userManage/findSelectedRptPermissionPage")
    @ResponseBody
    public Page findSelectedRptPermissionPage(HttpServletRequest request, Page page){
        try {
                String userId = request.getParameter("userId");
                if(userId != null){
                    User user = userManagerService.findUserById(Integer.valueOf(userId));
                    List list =  userManagerService.getSelectRptPermssionMap(user); //获取用户所选权限map

                    int start = page.getStart();
                    int end = start+page.getLimit();
                    if(end > list.size())
                        end = list.size();
                    page.setDatas(list.subList(start, end));
                    page.setTotal(list.size());
                }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return page;
    }





    /**
     * 查找用户所拥有的所有的报表权限
     * @param request
     * @return
     */
    @RequestMapping(value="/support/sys/user/userManage/findUserRptPermissionPage")
    @ResponseBody
    public Page findUserRptPermissionPage(HttpServletRequest request, Page page){
        try {
            String userId = request.getParameter("userId");
            if(userId != null){
                User user = userManagerService.findUserById(Integer.valueOf(userId));
                List list =  userManagerService.getUserRptPermssionMapList(user); //获取用户拥有报表权限

                int start = page.getStart();
                int end = start+page.getLimit();
                if(end > list.size())
                    end = list.size();
                page.setDatas(list.subList(start, end));
                page.setTotal(list.size());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return page;
    }


}
