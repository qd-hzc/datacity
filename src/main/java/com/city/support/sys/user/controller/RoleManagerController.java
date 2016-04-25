package com.city.support.sys.user.controller;

import com.city.common.controller.BaseController;
import com.city.common.pojo.Constant;
import com.city.common.pojo.Page;
import com.city.common.pojo.SystemConfigPojo;
import com.city.support.sys.module.entity.Module;
import com.city.support.sys.module.service.ModuleManagerService;
import com.city.support.sys.user.entity.*;
import com.city.support.sys.user.pojo.CurrentUser;
import com.city.support.sys.user.pojo.PermissionType;
import com.city.support.sys.user.pojo.ReportPermissionType;
import com.city.support.sys.user.service.DepartmentManagerService;
import com.city.support.sys.user.service.PermissionService;
import com.city.support.sys.user.service.RoleManagerService;
import com.city.support.sys.user.service.UserManagerService;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhoutao on 2016/1/21.
 * 角色管理Controller
 */
@Controller
public class RoleManagerController extends BaseController {

    @Autowired
    private RoleManagerService roleManagerService;
    @Autowired
    private UserManagerService userManagerService;
    @Autowired
    private DepartmentManagerService departmentManagerService;
    @Autowired
    private ModuleManagerService moduleManagerService;
    @Autowired
    private PermissionService permissionService;

    @RequestMapping("/support/sys/user/roleManager/jumpMainJsp")
    public ModelAndView jumpMainJsp(){
        ModelAndView mv = new ModelAndView("support/sys/user/roleManage/main");
        return mv;
    }

    @RequestMapping("/support/sys/user/roleManager/addRole")
    @ResponseBody
    public Map<String, Object> addRole(Role role, HttpServletRequest request){
        Map<String, Object> result = new HashMap<String, Object>();

        role.setCreateUser(CurrentUser.getCurrentUser(request).getUser());
        roleManagerService.saveRole(role);
        result.put("success",true);
        result.put("roleId",role.getId());
        return result;
    }

    @RequestMapping("/support/sys/user/roleManager/updateRole")
    @ResponseBody
    public Map<String, Object> updateRole(Role role, HttpServletRequest request){
        Map<String, Object> result = new HashMap<String, Object>();
        Role tmpRole = roleManagerService.findRoleById(role.getId());
        tmpRole.setName(role.getName());
        tmpRole.setComments(role.getComments());
        role.setId(null);
        //role.setCreateUser(CurrentUser.getCurrentUser(request).getUser());
        roleManagerService.saveRole(tmpRole);
        result.put("success",true);
        result.put("roleId",tmpRole.getId());
        return result;
    }

    /**
     * 删除角色信息  软删除 状态置为不可用
     * @param roleIds
     * @return
     */
    @RequestMapping("/support/sys/user/roleManager/deleteRole")
    @ResponseBody
    public Map<String, Object> deleteRole(String roleIds){
        Map<String, Object> result = new HashMap<String, Object>();
        Gson gson = new Gson();
        Type type = new TypeToken<List<String>>(){}.getType();
        List<String> idList = gson.fromJson(roleIds, type);

        for(String roleId : idList){
            Role role = roleManagerService.findRoleById(Integer.valueOf(roleId));
            role.setState(Role.State.DISABLE);//状态置为不可用

            roleManagerService.updateRole(role);
        }

        result.put("success",true);
        return result;
    }

    /**
     * 获取角色信息 分页
     * @param page
     * @return
     */
    @RequestMapping("/support/sys/user/roleManager/findRoleByNamePage")
    @ResponseBody
    public Page findRoleByNamePage(Page page, String roleName){
        if(roleName == null)
            roleName = "";

        List<Role> all = roleManagerService.findByName(roleName);
        page.setTotal(all.size());

        List<Role> roles = roleManagerService.findByPage(page, roleName);
        //测试用
        for(Role role : roles){
            role.setCreateUser(null);
        }
        page.setDatas(roles);
        return page;
    }

    /**
     * 查找选择的菜单
     * @param request
     * @return
     */
    @RequestMapping(value="/support/sys/user/roleManager/findSelectedFunTree")
    @ResponseBody
    public List findSelectedFunTree(HttpServletRequest request){
        String isNeedToDataBase = request.getParameter("isNeedToDataBase");
        List list  = new ArrayList();
        List mapList = roleManagerService.findSeletedFunTreeList(request, isNeedToDataBase, list);
        if (mapList != null) return mapList;
        return list;
    }




    /**
     * 更新角色的菜单权限集合
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value="/support/sys/user/roleManager/updateRoleFunList")
    @ResponseBody
    public Map<String, Object> updateRoleFunList(HttpServletRequest request, HttpServletResponse response){
        Map<String, Object> result = new HashMap<String, Object>();

        String roleId = request.getParameter("roleId");
        String moduleIds =  request.getParameter("moduleIds");

        Role role = roleManagerService.saveOrUpdateRoleFun(roleId, moduleIds);

        result.put("success",true);
        result.put("roleId",role.getId());
        return result;
    }


    @RequestMapping(value="/support/sys/user/roleManager/findSelectedFunPage")
    @ResponseBody
    public Page findSelectedFunPage(HttpServletRequest request, Page page){
        String roleId = request.getParameter("roleId");

        List<Module> moduleList = moduleManagerService.findModulesByRoleId(Integer.valueOf(roleId));//角色关联的已授权菜单
        page.setTotal(moduleList.size());
        int start = page.getStart();
        int end = page.getStart()+page.getLimit();
        if(end > moduleList.size())
            end = moduleList.size();
        page.setDatas(moduleList.subList(start, end));//已授权菜单展示分页

        return page;
    }

    /**
     * 更新角色的报表权限集合
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value="/support/sys/user/roleManager/updateRolePermissionList")
    @ResponseBody
    public Map<String, Object> updateRolePermissionList(HttpServletRequest request, HttpServletResponse response){
        Map<String, Object> result = new HashMap<String, Object>();

        String roleId = request.getParameter("roleId");
        String perms =  request.getParameter("perms");

        Role role = roleManagerService.saveOrUpdateRolePermission(roleId, perms);//更新角色权限信息

        result.put("success",true);
        result.put("roleId",role.getId());
        return result;
    }




    /**
     * 获取所有报表权限tree
     * @param role
     * @return
     */
    @RequestMapping("/support/sys/user/roleManager/findAllPermissionsTree")
    @ResponseBody
    public List<Map<String, Object>> findAllPermissionsTree(Role role){

        //获取报表权限tree
        List<Map<String, Object>> result = roleManagerService.getPermissionTreeMaps();

        return result;
    }


    /**
     * 查找选择的报表
     * @param request
     * @return
     */
    @RequestMapping(value="/support/sys/user/roleManager/findSelectedRptTree")
    @ResponseBody
    public List findSelectedRptTree(HttpServletRequest request){
        String isNeedToDataBase = request.getParameter("isNeedToDataBase");
        List list  = new ArrayList();
        List mapList = roleManagerService.findSeletedRptTreeList(request, isNeedToDataBase, list);//获取已选择的报表

        if (mapList != null) return mapList;
        return list;
    }





    /**
     * 查找选择的报表
     * @param request
     * @return
     */
    @RequestMapping(value="/support/sys/user/roleManager/findSelectedRptPage")
    @ResponseBody
    public Page findSelectedRptPage(HttpServletRequest request, Page page){
        try {
            String roleId = request.getParameter("roleId");
            if(roleId != null){
                Role role = roleManagerService.findRoleById(Integer.valueOf(roleId));//角色信息
                List permissionList = roleManagerService.getSelectRptPermssionList(role); //获取已受报表权限集合

                page.setTotal(permissionList.size());
                int start = page.getStart();
                int end = page.getStart()+page.getLimit();
                if(end > permissionList.size())
                    end = permissionList.size();
                page.setDatas(permissionList.subList(start, end));//已授权菜单展示分页
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return page;
    }


}
