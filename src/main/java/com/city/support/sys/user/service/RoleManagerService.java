package com.city.support.sys.user.service;

import com.city.common.pojo.Constant;
import com.city.common.pojo.Page;
import com.city.support.sys.module.entity.Module;
import com.city.support.sys.module.service.ModuleManagerService;
import com.city.support.sys.user.dao.DepartmentDao;
import com.city.support.sys.user.dao.RoleDao;
import com.city.support.sys.user.dao.RoleModuleDao;
import com.city.support.sys.user.dao.RolePermissionDao;
import com.city.support.sys.user.entity.*;
import com.city.support.sys.user.pojo.PermissionType;
import com.city.support.sys.user.pojo.ReportPermissionType;
import com.city.support.sys.user.util.GenDepmentTreeUtil;
import com.city.support.sys.user.util.RoleDepmentTreeUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Created by wys on 2015/12/29.
 */
@Service
@Transactional(readOnly = false, rollbackFor = Exception.class)
public class RoleManagerService {
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private DepartmentDao departmentDao;
    @Autowired
    private RoleModuleDao roleModuleDao;
    @Autowired
    private RolePermissionDao rolePermissionDao;
    @Autowired
    private DepartmentManagerService departmentManagerService;
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private ModuleManagerService moduleManagerService;

    /**
     * 保存角色信息
     * @param role
     */
    public void saveRole(Role role){
        roleDao.add(role);
    }

    /**
     * 修改角色信息
     * @param role
     */
    public void updateRole(Role role){
        roleDao.saveOrUpdate(role, false);
    }

    /**
     * 获取所有角色信息
     * @return
     */
    public List<Role> findAll(){
           return roleDao.queryAll();
    }

    /**
     * 根据名称查询角色
     * @param roleName
     * @return
     */
    public List<Role> findByName(String roleName){
        return roleDao.getByName(roleName);
    }

    /**
     * 根据id获取角色信息
     * @param id
     * @return
     */
    public Role findRoleById(Integer id){
        return roleDao.getById(id);
    }

    /**
     * 获取角色分页信息
     * @param page
     * @return
     */
    public List<Role> findByPage(Page page, String roleName){
        return roleDao.getByPage(page, roleName);
    }

    /**
     * 保存角色菜单权限信息
     * @param rm
     */
    public void saveRoleModule(RoleModule rm){
        roleModuleDao.add(rm);
    }

    /**
     * 保存角色菜单权限信息
     * @param list
     */
    public void saveRoleModules(List<RoleModule> list){
        for(RoleModule rm : list){
            this.saveRoleModule(rm);
        }
    }

    /**
     * 删除角色下的菜单权限
     * @param role
     */
    public void removeRoleModulesByRole(Role role){
        roleModuleDao.deleteByRole(role);
    }

    /**
     * 保存菜单报表权限信息
     * @param rp
     */
    public void saveRolePermission(RolePermission rp){
        rolePermissionDao.add(rp);
    }

    /**
     * 删除角色相关联的报表权限
     * @param role
     */
    public void removeRolePermissionByRole(Role role){
        rolePermissionDao.deleteByRole(role);
    }

    /**
     * 获取已关联菜单tree
     * @param request
     * @param isNeedToDataBase
     * @param list
     * @return
     */
    public List findSeletedFunTreeList(HttpServletRequest request, String isNeedToDataBase, List list) {
        try {
            if (isNeedToDataBase == null) {//查库
                String roleId = request.getParameter("roleId");
                if(roleId != null){
                    Role role = findRoleById(Integer.valueOf(roleId));//角色信息
                    List<Module> modules = role.getModules();
                    if (modules != null) {
                        for (int i = 0; i < modules.size(); i++) {
                            Module m = modules.get(i);
                            Map map = new HashMap();
                            map.put("id", m.getId()+"");
                            map.put("text", m.getModuleName());
                            map.put("moduleName", m.getModuleName());
                            map.put("modulePid", m.getModulePid()+"");
                            map.put("checked", false);
                            List childList = moduleManagerService.findModulesByPid(m.getId());
                            map.put("leaf",!(childList != null && childList.size()>0));
                            map.put("expanded", true);
                        /*Map map = BeanUtils.describe(acFunction);
                        map.put("id", acFunction.getFunId());
                        map.put("text", acFunction.getFunName());
                        map.put("leaf", queryMap.get(acFunction.getFunId().toString().trim()) == null);
                        map.put("expanded", true);*/
                            list.add(i, map);
                        }
                        return sequence("0",list,false);
                    }
                }
            }else {
                String dataList = request.getParameter("dataList");
                Gson gson = new Gson();
                Type type = new TypeToken<List<Map>>(){}.getType();
                List<Map> mapList = gson.fromJson(dataList, type);
                return sequence("0",mapList,false);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据当前id查找所有子菜单
     * @param currentId
     * @param mapList
     * @return
     */
    private List<Map> sequence(String currentId, List<Map> mapList, boolean loaded){
        List<Map> childList = new ArrayList<Map>();
        if(currentId != null){
            for (int i = 0; i < mapList.size(); i++) {
                Map map = mapList.get(i);
                String parentId = (String)map.get("modulePid");
                if (currentId.equals(parentId)) {
                    map.put("loaded",loaded);
                    childList.add(map);//不需要排序，后台传过来时顺序是正确的
                    List<Map> childList2 = sequence((String) map.get("id"), mapList, loaded);//递归
                    map.put("children", childList2);
                    // map.put("expanded", true);
                    map.put("checked", false);
                }
            }
        }

        return childList;
    }

    /**
     * 保存或更新角色菜单权限
     * @param roleId
     * @param moduleIds
     * @return
     */
    public Role saveOrUpdateRoleFun(String roleId, String moduleIds) {
        Role role = findRoleById(Integer.valueOf(roleId));//角色信息
        removeRoleModulesByRole(role);//先删除角色下的菜单权限信息

        if (moduleIds != null && !"".equals(moduleIds.trim())) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<String>>() {
            }.getType();
            List<String> moduleIdList = gson.fromJson(moduleIds, type);

            Set<String> moduleIdSet = new HashSet<>();//去重
            for(String moduleId : moduleIdList){
                moduleIdSet.add(moduleId);
            }

            for(String moduleId : moduleIdSet){
                Module module = moduleManagerService.findModuleById(Integer.valueOf(moduleId));
                RoleModule rm = new RoleModule();
                rm.setRole(role);
                rm.setModule(module);

                saveRoleModule(rm);
            }
        }
        return role;
    }

    /**
     * 保存或更新角色权限信息
     * @param roleId
     * @param perms
     * @return
     */
    public Role saveOrUpdateRolePermission(String roleId, String perms) {
        Role role = findRoleById(Integer.valueOf(roleId));//角色信息
        removeRolePermissionByRole(role);//先删除角色下的报表权限信息

        if (perms != null && !"".equals(perms.trim())) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<String>>() {
            }.getType();
            List<String> permIdList = gson.fromJson(perms, type);
            for(String permId : permIdList){
                if(permId == null)
                    continue;

                String[] tmpArr = permId.split("\\|");
                if(tmpArr.length >2){
                    Integer depId = Integer.valueOf(tmpArr[0]);//部门id
                    ReportPermissionType rpt = ReportPermissionType.valueOf(tmpArr[1]);//报表权限的类型
                    for(int i = 2; i<tmpArr.length; i++){
                        String rptPerm = tmpArr[i];
                        PermissionType pt = PermissionType.valueOf(rptPerm);
                        Permission p = new Permission();
                        p.setDep(depId);
                        p.setPermissionType(pt);
                        p.setReportPermissionType(rpt);
                        List<Permission> list = permissionService.findByInfo(p);//去数据库查询有没有相应的报表权限
                        if(list.size() > 0)
                            p = list.get(0);
                        else{
                            permissionService.save(p);
                        }

                        RolePermission rp = new RolePermission();
                        rp.setRole(role);
                        rp.setPermission(p);

                        saveRolePermission(rp);//保存角色报表权限信息
                    }
                }

            }
        }
        return role;
    }

    /**
     * 拼装可选报表权限tree
     * @return
     */
    public List<Map<String, Object>> getPermissionTreeMaps() {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

        Map<String, Object> AllMap = new HashMap<String, Object>();
        AllMap.put("depId","-4");
        AllMap.put("text","所有报表");
        AllMap.put("reportPermissionType", ReportPermissionType.ALL);
        AllMap.put("checked",false);
        AllMap.put("leaf",true);
        AllMap.put("flagPid","-1");
        AllMap.put("expanded",true);
        result.add(AllMap);

        Map<String, Object> departmentMap = new HashMap<String, Object>();
        departmentMap.put("depId","-2");
        departmentMap.put("text","本部门 报表");
        departmentMap.put("reportPermissionType", ReportPermissionType.DEPARTMENT);
        departmentMap.put("checked",false);
        departmentMap.put("leaf",true);
        departmentMap.put("flagPid","-1");
        departmentMap.put("expanded",true);
        result.add(departmentMap);

        Map<String, Object> otherstMap = new HashMap<String, Object>();
        otherstMap.put("depId","0");
        otherstMap.put("text","外部门 报表");
        otherstMap.put("reportPermissionType", ReportPermissionType.DEPARTMENT_OTHERS);
        otherstMap.put("checked",false);
        otherstMap.put("leaf",false);
        otherstMap.put("flagPid","-1");
        otherstMap.put("expanded",true);

        /*List<Department> depList = departmentManagerService.findDepByPid(Constant.systemConfigPojo.getDepParentId());
        List<Map<String, Object>> depTmpList = new ArrayList<Map<String, Object>>();
        for(Department dep : depList){
            Map<String, Object> depMap = new HashMap<String, Object>();
            depMap.put("depId",dep.getId()+"");
            depMap.put("text",dep.getDepName()+" 报表");
            depMap.put("reportPermissionType", ReportPermissionType.DEPARTMENT_OTHERS);
            depMap.put("checked",false);
            depMap.put("leaf",true);
            depMap.put("flagPid","0");
            depMap.put("expanded",true);
            depTmpList.add(depMap);
        }*/
        List<Department> departments = departmentDao.queryAllDepSort();
        Map<String, Object> root = new HashMap<>();
        RoleDepmentTreeUtil roleDepmentTreeUtil = new RoleDepmentTreeUtil();
        root.put("id", 0);
        roleDepmentTreeUtil.genTree(root, departments);
        otherstMap.put("children", root.get("children"));

        result.add(otherstMap);
        return result;
    }

    /**
     * 获取已选择的报表tree
     * @param request
     * @param isNeedToDataBase
     * @param list
     * @return
     */
    public List findSeletedRptTreeList(HttpServletRequest request, String isNeedToDataBase, List list) {
        try {
            if (isNeedToDataBase == null) {//查库
                String roleId = request.getParameter("roleId");
                if(roleId != null){
                    Role role = findRoleById(Integer.valueOf(roleId));//角色信息
                    if (getSelectRptPermssionTreeMap(list, role)) //获取已受报表权限tree
                        return sequenceRpt("-1", list);
                }
            }else {
                String dataList = request.getParameter("dataList");
                Gson gson = new Gson();
                Type type = new TypeToken<List<Map>>(){}.getType();
                List<Map> mapList = gson.fromJson(dataList, type);
                List<Map> tmp = getUniqMaps(mapList, "depId");//去重

                return sequenceRpt("-1", tmp);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //去重
    private List<Map> getUniqMaps(List<Map> mapList, String idName) {
        List<Map> tmp = new ArrayList<>();
        boolean flag = false;
        for(Map map : mapList){
            flag = false;
            for(Map m : tmp){
                if(((String)map.get(idName)).equals(((String)m.get(idName)))) {
                    if(map.get("isRead")!=null && ((boolean)map.get("isRead"))==true|| m.get("isRead")!=null && ((boolean)m.get("isRead"))==true){
                        m.put("isRead",true);
                    }
                    if(map.get("isWrite")!=null && ((boolean)map.get("isWrite"))==true || m.get("isWrite")!=null && ((boolean)m.get("isWrite"))==true){
                        m.put("isWrite",true);
                    }
                    if(map.get("isApproval")!=null && ((boolean)map.get("isApproval"))==true || m.get("isApproval")!=null && ((boolean)m.get("isApproval"))==true){
                        m.put("isApproval",true);
                    }

                    flag = true;
                    break;
                }
            }

            if(!flag)
                tmp.add(map);
        }
        return tmp;
    }


    /**
     * 获取角色已授权报表权限tree
     * @param list
     * @param role
     * @return
     */
    private boolean getSelectRptPermssionTreeMap(List list, Role role) {
        List<Permission> perms = role.getPermissions();
        if (perms != null) {
            boolean flag = false;
            //转换格式 以部门id为key 以本部门报表权限List做value
            Map<Integer,List<Permission>> permMap = new HashMap<Integer,List<Permission>>();
            for(Permission perm : perms){
                List<Permission> tmpList = permMap.get(perm.getDep());
                if(tmpList == null){
                    tmpList = new ArrayList<Permission>();
                    permMap.put(perm.getDep(), tmpList);
                }
                tmpList.add(perm);
            }

            for (Map.Entry<Integer,List<Permission>> entry : permMap.entrySet()) {
                Map map = new HashMap();
                Integer depId = entry.getKey();
                map.put("depId", depId+"");
                switch (depId){
                    case -4:
                        map.put("text","所有报表");
                        map.put("reportPermissionType", ReportPermissionType.ALL);
                        map.put("checked",false);
                        map.put("leaf",true);
                        map.put("flagPid","-1");
                        map.put("expanded",true);

                        processPermission(entry, map);
                        break;
                    case -2 :
                        map.put("text","本部门 报表");
                        map.put("reportPermissionType", ReportPermissionType.DEPARTMENT);
                        map.put("checked",false);
                        map.put("leaf",true);
                        map.put("flagPid","-1");
                        map.put("expanded",true);

                        processPermission(entry, map);
                        break;
                    default :
                        flag = true;//是否有外部门报表权限标志
                        Department d = departmentManagerService.findDepById(depId);
                        map.put("text", d.getDepName() + " 报表");
                        map.put("reportPermissionType", ReportPermissionType.DEPARTMENT_OTHERS);
                        map.put("checked",false);
                        //map.put("leaf",true);
                        Integer parentDepId = d.getpDep();
                        findParentDep(list, permMap, parentDepId);//获取所有父目录
                        map.put("flagPid",d.getpDep()+"");

                        map.put("expanded",true);

                        processPermission(entry, map);
                        break;
                }

                list.add( map);
            }
            if(flag){//对外部门添加跟节点
                Map map = new HashMap();
                map.put("depId","0");
                map.put("text","外部门 报表");
                map.put("reportPermissionType", ReportPermissionType.DEPARTMENT_OTHERS);
                map.put("checked",false);
                map.put("leaf",false);
                map.put("flagPid","-1");
                map.put("expanded",true);

                list.add( map);
            }

            return true;
        }
        return false;
    }

    private void findParentDep(List list, Map<Integer, List<Permission>> permMap, Integer parentDepId) {
        if(parentDepId!=0 && permMap.get(parentDepId) == null){
            Department parent = departmentManagerService.findDepById(parentDepId);
            Map parentMap = new HashMap();
            parentMap.put("text", parent.getDepName() + " 报表");
            parentMap.put("reportPermissionType", ReportPermissionType.DEPARTMENT_OTHERS);
            parentMap.put("checked",false);
            parentMap.put("flagPid",parent.getpDep()+"");
            parentMap.put("depId",parentDepId+"");
            parentMap.put("expanded",true);
            list.add(parentMap);
            findParentDep(list, permMap, parent.getpDep());
        }
    }

    /**
     * 获取角色已授权报表权限List
     * @param role
     * @return list
     */
    public List getSelectRptPermssionList( Role role) {
        List list = new ArrayList();

        List<Permission> perms = role.getPermissions();
        if (perms != null) {
            boolean flag = false;
            //转换格式 以部门id为key 以本部门报表权限List做value
            Map<Integer,List<Permission>> permMap = new HashMap<Integer,List<Permission>>();
            for(Permission perm : perms){
                List<Permission> tmpList = permMap.get(perm.getDep());
                if(tmpList == null){
                    tmpList = new ArrayList<Permission>();
                    permMap.put(perm.getDep(), tmpList);
                }
                tmpList.add(perm);
            }

            for (Map.Entry<Integer,List<Permission>> entry : permMap.entrySet()) {
                Map map = new HashMap();
                Integer depId = entry.getKey();
                map.put("depId", depId+"");
                switch (depId){
                    case -4:
                        map.put("rptName","所有报表");
                        map.put("reportPermissionType", ReportPermissionType.ALL);
                        map.put("flagPid","-1");

                        processPermission(entry, map);
                        break;
                    case -2 :
                        map.put("rptName","本部门 报表");
                        map.put("reportPermissionType", ReportPermissionType.DEPARTMENT);
                        map.put("flagPid","-1");

                        processPermission(entry, map);
                        break;
                    default :
                        flag = true;//是否有外部门报表权限标志
                        map.put("rptName", departmentManagerService.findDepById(depId).getDepName() + " 报表");
                        map.put("reportPermissionType", ReportPermissionType.DEPARTMENT_OTHERS);
                        map.put("flagPid","0");

                        processPermission(entry, map);
                        break;
                }

                list.add( map);
            }

        }
        return list;
    }

    //处理报表权限 可读 可写 可添
    private void processPermission(Map.Entry<Integer, List<Permission>> entry, Map map) {
        for(Permission p : entry.getValue()){
            if(p.getPermissionType() == PermissionType.READ)
                map.put("isRead", true);
            if(p.getPermissionType() == PermissionType.WRITE)
                map.put("isWrite", true);
            if(p.getPermissionType() == PermissionType.APPROVAL)
                map.put("isApproval", true);
        }
    }

    /**
     * 根据当前id查找所有子报表
     * @param currentId
     * @param mapList
     * @return
     */
    private List<Map> sequenceRpt(String currentId, List<Map> mapList){
        List<Map> childList = new ArrayList<Map>();
        if(currentId != null){
            for (int i = 0; i < mapList.size(); i++) {
                Map map = mapList.get(i);
                String parentId = (String)map.get("flagPid");
                if (currentId.equals(parentId)) {
                    map.put("loaded",false);
                    childList.add(map);//不需要排序，后台传过来时顺序是正确的
                    String id = (String) map.get("depId");
                    List<Map> childList2 = sequenceRpt((String) map.get("depId"), mapList);//递归
                    map.put("children", childList2);
                    if(childList2.size() == 0)
                        map.put("leaf", true);
                    // map.put("expanded", true);
                    map.put("checked", false);
                }
            }
        }

        return childList;
    }

}
