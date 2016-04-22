package com.city.support.sys.user.service;

import com.city.common.pojo.Page;
import com.city.support.regime.report.entity.ReportTemplate;
import com.city.support.regime.report.service.ReportManageService;
import com.city.support.sys.module.entity.Module;
import com.city.support.sys.module.service.ModuleManagerService;
import com.city.support.sys.user.dao.*;
import com.city.support.sys.user.entity.*;
import com.city.support.sys.user.pojo.PermissionType;
import com.city.support.sys.user.pojo.ReportPermissionType;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Created by wys on 2015/12/28.
 */
@Service
@Transactional(readOnly = false, rollbackFor = {Exception.class}, propagation = Propagation.REQUIRED)
public class UserManagerService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private ReportManageService reportManageService;
    @Autowired
    private RptPermissionService rptPermissionService;
    @Autowired
    private UserRptPermissionService userRptPermissionService;
    @Autowired
    private RoleManagerService roleManagerService;
    @Autowired
    private ModuleManagerService moduleManagerService;

    public Map<String, Object> vailUser(String loginName, String loginPwd) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("success", false);
        if (loginName == null || loginName.equals("")) {
            result.put("Msg", "登录名不能为空");
            return result;
        }

        if (loginPwd == null || loginPwd.equals("")) {
            result.put("Msg", "密码不能为空");
            return result;
        }
        User user = null;
        user = userDao.vailUser(loginName, loginPwd);
        if (user != null) {
            result.put("success", true);
            result.put("data", user);
        } else {
            result.put("Msg", "登录失败");
        }
        return result;

    }

    /**
     * 添加用户
     * @param user 添加的用户实体
     */
    @Transactional(readOnly = false, rollbackFor = {Exception.class}, propagation = Propagation.REQUIRED)
    public void saveOrUpdateUser(User user) {
        userDao.saveOrUpdate(user, false);
    }

    /**
     * 根据用户名或登录名查询用户信息
     * @param name
     * @param loginName
     * @return
     */
    public List<User> findUserByNameOrLoginName(String name,String loginName){
        return userDao.getUserByNameOrLoginName(name, loginName);
    }

    /**
     * 根据用户名或登录名查询用户信息 分页
     * @param page
     * @param name  用户名
     * @param loginName 登录用户名
     * @return
     */
    public List<User> findUserPageByNameOrLoginName(Page page,String name,String loginName){
        return userDao.getUserPageByNameOrLoginName(page, name, loginName);
    }
    /**
     * 根据id查询用户
     * @param id id
     * @return
     */
    public User findUserById(Integer id){
        return userDao.queryById(id);
    }

    /**
     * 删除用户相关角色
     * @param userId 用户id
     */
    public void removeRoleByUserId(Integer userId){
        userDao.deleteRoleByUserId(userId);
    }

    public void saveOrUpdateUserRole(HttpServletRequest request, String userId) {
        User user = findUserById(Integer.valueOf(userId));
        user.getRoles().clear();//先删除已关联的角色
        //userManagerService.removeRoleByUserId(Integer.valueOf(userId));//先删除已关联的角色

        String dataList = request.getParameter("dataList");
        Gson gson = new Gson();
        Type type = new TypeToken<List<Map>>(){}.getType();
        List<Map<String,String>> mapList = gson.fromJson(dataList, type);
        for(Map<String,String> map : mapList){
            Role role = roleManagerService.findRoleById(Integer.valueOf(map.get("id")));
            user.getRoles().add(role);
        }
        saveOrUpdateUser(user);
    }

    /**
     * 获取用户的菜单权限
     * @param user
     * @return
     */
    public List<Module> findModulesByUser(User user) {
        List<Module> moduleList = new ArrayList<Module>();
        if(!user.isAdmin()){
            Set<Role> roleSet = user.getRoles();//获取用户已关联角色
            Set<Module> moduleSet = new HashSet<Module>();//跟用户角色关联的菜单
            for(Role role : roleSet){
                moduleSet.addAll(role.getModules());
            }

            moduleList.addAll(moduleSet);
        }else{//管理员特殊处理 拥有全部菜单权限
            moduleList = moduleManagerService.getAllModuleBySort();
        }
        return moduleList;
    }

    /**
     * 保存或者更新用户报表权限信息
     * @param userId
     * @param dataList
     */
    public void saveOrUpdateUserRptPermission(Integer userId, String dataList) {
        User user = findUserById(userId);//查询用户信息
        // 先删除原来的报表权限集合
        userRptPermissionService.removeRptPermissionByUserId(userId);
        // 然后插入新的数据
        if (dataList != null && !"".equals(dataList.trim())) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<Map<String, String>>>() {
            }.getType();
            List<Map<String, String>> mapList = gson.fromJson(dataList, type);
            for (int i = 0; i < mapList.size(); i++) {
                Map<String, String> map = mapList.get(i);
                String rptId = map.get("id");
                if (rptId != null && rptId.trim().length() > 0) {
                    String isApproval = map.get("isApproval");
                    String isRead = map.get("isRead");
                    String isWrite = map.get("isWrite");
                    if(isApproval != null && ("1".equals(isApproval.trim())|| "true".equals(isApproval.trim()))){
                        saveUserRptPerm(user, rptId, PermissionType.APPROVAL);
                    }

                    if(isRead != null && ("1".equals(isRead.trim())|| "true".equals(isRead.trim()))){
                        saveUserRptPerm(user, rptId, PermissionType.READ);
                    }

                    if(isWrite != null && ("1".equals(isWrite.trim())|| "true".equals(isWrite.trim()))){
                        saveUserRptPerm(user, rptId, PermissionType.WRITE);
                    }

                }

            }
        }
    }

    /**
     * 保存用户关联报表权限
     * @param user
     * @param rptId
     * @param pt
     */
    private void saveUserRptPerm(User user, String rptId, PermissionType pt) {
        RptPermission rpt = new RptPermission();
        rpt.setRptId(Integer.parseInt(rptId));
        rpt.setPermissionType(pt);

        List<RptPermission> tmpList = rptPermissionService.findRptPermissionByInfo(rpt);//查询相关报表权限信息
        if(tmpList.size() > 0){
            rpt = tmpList.get(0);
        }else{
            rptPermissionService.save(rpt);//保存报表权限信息
        }

        UserRptPermission urp = new UserRptPermission();
        urp.setUser(user);
        urp.setRptPermission(rpt);
        userRptPermissionService.save(urp);//保存用户关联的报表权限
    }

    /**
     * 获取用户所选权限map
     * @param user
     * @return
     */
    public List getSelectRptPermssionMap( User user) {
        List list = new ArrayList();

        Set<RptPermission> perms = user.getRptPermissions();
        if (perms != null) {
            boolean flag = false;
            //转换格式 以部门id为key 以本部门报表权限List做value
            Map<Integer, List<RptPermission>> permMap = new HashMap<Integer, List<RptPermission>>();
            for (RptPermission perm : perms) {
                List<RptPermission> tmpList = permMap.get(perm.getRptId());
                if (tmpList == null) {
                    tmpList = new ArrayList<RptPermission>();
                    permMap.put(perm.getRptId(), tmpList);
                }
                tmpList.add(perm);
            }

            for (Map.Entry<Integer,List<RptPermission>> entry : permMap.entrySet()) {
                Map map = new HashMap();
                Integer rptId = entry.getKey();
                map.put("id", rptId + "");
                ReportTemplate rt = reportManageService.getRptTmpById(rptId);
                map.put("name", rt.getName());
                map.put("depId", rt.getDepartment().getId());
                processRptPermission(entry, map);

                list.add(map);
            }
        }

        return list;
    }

    /**
     * 获取用户所选权限
     * @param user  用户
     * @return
     */
    public List getUserRptPermssionMapList( User user) {
        List list = new ArrayList();

        //获取用户报表权限Set
        Set<RptPermission> perms = getUserRptPermissions(user);

        //转换格式 以部门id为key 以本部门报表权限List做value
        Map<Integer, List<RptPermission>> permMap = new HashMap<Integer, List<RptPermission>>();
        for (RptPermission perm : perms) {
            List<RptPermission> tmpList = permMap.get(perm.getRptId());
            if (tmpList == null) {
                tmpList = new ArrayList<RptPermission>();
                permMap.put(perm.getRptId(), tmpList);
            }
            tmpList.add(perm);
        }

        for (Map.Entry<Integer,List<RptPermission>> entry : permMap.entrySet()) {
            Map map = new HashMap();
            Integer rptId = entry.getKey();
            map.put("id", rptId + "");
            ReportTemplate rt = reportManageService.getRptTmpById(rptId);
            map.put("name", rt.getName());
            map.put("depId", rt.getDepartment().getId());
            processRptPermission(entry, map);

            list.add(map);
        }

        return list;
    }

    //处理报表权限 可读 可写 可添
    public void processRptPermission(Map.Entry<Integer, List<RptPermission>> entry, Map map) {
        for(RptPermission p : entry.getValue()){
            if(p.getPermissionType() == PermissionType.READ)
                map.put("isRead", true);
            if(p.getPermissionType() == PermissionType.WRITE)
                map.put("isWrite", true);
            if(p.getPermissionType() == PermissionType.APPROVAL)
                map.put("isApproval", true);
        }
    }

    /**
     * 获取用户所拥有报表权限Set
     * @param user
     * @return
     */
    public Set<RptPermission> getUserRptPermissions(User user) {
        Set<RptPermission> rptPermSet = new HashSet<>();
        if(!user.isAdmin()) {
            Set<RptPermission> perms = user.getRptPermissions();

            if (perms != null)
                rptPermSet.addAll(perms);

            //处理角色对应的报表权限
            Set<Role> roleSet = user.getRoles();
            for (Role role : roleSet) {
                List<Permission> permissionList = role.getPermissions();
                for (Permission perm : permissionList) {
                    List<ReportTemplate> temps = null;
                    Integer depId = null;
                    if (perm.getReportPermissionType() == ReportPermissionType.ALL) {
                        temps = reportManageService.getAllRptTmp();
                    } else if (perm.getReportPermissionType() == ReportPermissionType.DEPARTMENT) {
                        temps = reportManageService.findRptTmpsByDep(user.getDepartment().getId());
                    } else {
                        temps = reportManageService.getRptTmpsExpectDep(user.getDepartment().getId());
                    }

                    fillRptPermSet(rptPermSet, perm.getPermissionType(), temps);
                }
            }
        }else{//管理员特殊处理 拥有所有权限
            List<ReportTemplate> temps = reportManageService.getAllRptTmp();

            fillRptPermSet(rptPermSet, PermissionType.READ, temps);
            fillRptPermSet(rptPermSet, PermissionType.WRITE, temps);
            fillRptPermSet(rptPermSet, PermissionType.APPROVAL, temps);
        }
        return rptPermSet;
    }

    private void fillRptPermSet(Set<RptPermission> rptPermSet, PermissionType pt, List<ReportTemplate> temps) {
        for(ReportTemplate tem : temps){
            RptPermission rpt = new RptPermission();
            rpt.setRptId(tem.getId());
            rpt.setPermissionType(pt);

            List<RptPermission> rptPermissionList = rptPermissionService.findRptPermissionByInfo(rpt);
            if(rptPermissionList!=null && rptPermissionList.size()>0)
                rpt = rptPermissionList.get(0);
            else
                rptPermissionService.save(rpt);

            rptPermSet.add(rpt);
        }
    }


}
