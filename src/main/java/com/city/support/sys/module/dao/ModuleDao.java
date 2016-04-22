package com.city.support.sys.module.dao;

import com.city.common.dao.BaseDao;
import com.city.support.sys.module.entity.Module;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wys on 2015/12/30.
 */
@Repository
public class ModuleDao extends BaseDao<Module> {

    public List<Module> queryAllEnableModule() {
        String hql = "From Module t where t.moduleState=" + Module.ENABLE.toString();
        return queryByHQL(hql);
    }

    public List<Module> queryAllModuleBySort() {
        String hql = "from Module t order by t.moduleSort";
        return this.queryByHQL(hql);
    }

    public  Module getModuleById(Integer id){
        return super.queryById(id);
    }

    public List<Module> getModulesByPid(Integer pid){
        String hql = " from Module m where m.modulePid ="+pid;

        return super.queryByHQL(hql);
    }

    public List<Module> getModulesByRoleId(Integer roleId){
        String hql = "from Module m where id in(select r.module.id from RoleModule r where r.role.id ="+roleId+" ) order by m.id";

        return super.queryByHQL(hql);
    }
}
