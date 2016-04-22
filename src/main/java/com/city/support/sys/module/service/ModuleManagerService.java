package com.city.support.sys.module.service;

import com.city.common.util.ConvertUtil;
import com.city.support.sys.module.dao.ModuleDao;
import com.city.support.sys.user.dao.RoleModuleDao;
import com.city.support.sys.module.entity.Module;
import com.city.support.sys.user.entity.RoleModule;
import com.city.support.sys.module.pojo.ModuleNode;
import com.city.support.sys.module.util.GenModuleCheckTreeUtil;
import com.city.support.sys.module.util.GenModuleTreeUtil;
import com.city.support.sys.module.util.GenModuleTreeWatcher;
import com.city.support.sys.user.entity.Role;
import com.city.support.sys.user.entity.User;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by wys on 2015/12/30.
 */
@Service
@Transactional(readOnly = false, rollbackFor = Exception.class)
public class ModuleManagerService {
    @Autowired
    private ModuleDao moduleDao;
    @Autowired
    private RoleModuleDao roleModelDao;

    /**
     * 根据模块id获取模块
     *
     * @param moduleId
     * @return
     */
    public Module getModuleById(Integer moduleId) {
        return moduleDao.queryById(moduleId);
    }

    /**
     * @param user   传入用户
     * @param module 传入根节点，如果要返回全部传入null
     * @return 返回树结构，没有用户传入null；
     */
    public Map<String, Object> getModuleTree(User user, Module module) {
        //判断用户是否启用
        if (user != null && User.STATE_ENABLE==user.isState()) {
            //如果是超管返回所有模块，如果不是超管根据配置读取模块
            List<Module> moduleList = null;
            if (User.ADMIN==user.isAdmin()) {
                moduleList = moduleDao.queryAllEnableModule();
            } else {
                //根据用户拥有角色查询出用户可查看模块
                Set<Module> moduleSet = new HashSet<Module>();
                List<RoleModule> tmpRoleModuleList = null;
                moduleList = new ArrayList<>();

                for (Role role : user.getRoles()) {
                        tmpRoleModuleList = roleModelDao.getRoleModuleByRole(role);
                        for (RoleModule tmpRoleModule : tmpRoleModuleList) {
                            moduleSet.add(tmpRoleModule.getModule());
                        }
                }
                for (Module tmpModule : moduleSet) {
                    moduleList.add(tmpModule);
                }
            }

            //根据可查看模块List组织成树结构
            Map<String, Object> root = null;
            //生成模块树工具
            GenModuleTreeUtil genModuleTreeUtil = new GenModuleTreeUtil();
            //添加生成节点监听进行节点排序
            genModuleTreeUtil.addWatcher(new GenModuleTreeWatcher(GenModuleTreeUtil.GENNODE));
            if (module == null) {
                //如果module为空，返回整个结构
                root = new HashMap<>();
                root.put("id", Module.SUPPORTID);
            } else {
                //如果有父节点，返回该节点下的结构
                root = genModuleTreeUtil.genTreeNode(module);
            }

            genModuleTreeUtil.genTree(root, moduleList);
            Gson g = new Gson();
            return root;
        }
        //没有用户传回null
        return null;
    }

    /**
     * 生成模块树
     *
     * @param node  节点
     * @param datas 模块List
     */
    private void genModuleTree(ModuleNode node, List<Module> datas) {
        if (datas != null) {
            ModuleNode tmpNode = null;
            for (Module tmpModule : datas) {
                if (tmpModule.getModulePid().equals(node.getId())) {
                    tmpNode = new ModuleNode(tmpModule.getId(), true, tmpModule, tmpModule.getModuleName(), tmpModule.getModuleType());
                    node.getChildren().add(tmpNode);
                    genModuleTree(tmpNode, datas);
                }
            }

            if (node.getChildren().size() > 0) {
                node.setLeaf(false);
                //按Sort进行排序
                Collections.sort(node.getChildren(), new Comparator<ModuleNode>() {
                    @Override
                    public int compare(ModuleNode o1, ModuleNode o2) {
                        Integer o1Sort = o1.getModule().getModuleSort();
                        Integer o2Sort = o2.getModule().getModuleSort();
                        if (o1Sort != null && o2Sort != null) {
                            return o1Sort - o2Sort;
                        } else if (o1Sort == null) {
                            return 0;
                        }
                        return Integer.MAX_VALUE;
                    }
                });
            }
        }
    }

    /**
     * 生成模块树
     * @return
     */
    public Map<String, Object> getAllModuleTreeBySort() {
        List<Module> modules = moduleDao.queryAllModuleBySort();
        Map<String, Object> root = new HashMap<>();
        GenModuleTreeUtil genModuleTreeUtil = new GenModuleTreeUtil();
        root.put("id", 0);
        genModuleTreeUtil.genTree(root, modules);
        return root;
    }

    /**
     * 生成带复选框的模块树
     * add by zt
     * @return
     */
    public Map<String, Object> getAllModuleCheckTreeBySort() {
        List<Module> modules = moduleDao.queryAllModuleBySort();
        Map<String, Object> root = new HashMap<>();
        GenModuleCheckTreeUtil checkTreeUtil = new GenModuleCheckTreeUtil();
        root.put("id", "0");
        checkTreeUtil.genTree(root, modules);
        return root;
    }

    public List<Module> getAllModuleBySort() {
        return moduleDao.queryAllModuleBySort();
    }
    /**
     * 添加模块
     * @param module 模块
     */
    public void addModule(Module module) {
        moduleDao.insert(module, true);
    }

    /**
     * 修改模块
     * @param updatemodule 模块
     */
    public void updateModule(Module updatemodule) {
        Module module = moduleDao.queryById(updatemodule.getId());
        ConvertUtil<Module> convertUtil = new ConvertUtil<>();
        convertUtil.replication(updatemodule, module, Module.class.getName());
        moduleDao.update(module, true);
    }

    /**
     * 删除模块
     * @param module 模块
     */
    public void removeModule(Module module) {
        moduleDao.delete(module, true);
    }

    /**
     * 根据id获取模块信息
     * @param id
     * @return
     */
    public  Module findModuleById(Integer id){{
        return moduleDao.getModuleById(id);
    }}

    /**
     * 获取子模块
     * @param pid
     * @return
     */
    public List<Module> findModulesByPid(Integer pid){
        return moduleDao.getModulesByPid(pid);
    }

    /**
     * 获取角色关联的菜单信息
     * @param roleId
     * @return
     */
    public List<Module> findModulesByRoleId(Integer roleId){
        return moduleDao.getModulesByRoleId(roleId);
    }
}
