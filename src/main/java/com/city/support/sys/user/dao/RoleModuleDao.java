package com.city.support.sys.user.dao;


import com.city.common.dao.BaseDao;
import com.city.support.sys.module.entity.Module;
import com.city.support.sys.user.entity.RoleModule;
import com.city.support.sys.user.entity.Role;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wys on 2015/12/30.
 */
@Repository
public class RoleModuleDao extends BaseDao<RoleModule> {

    /**
     * 根据角色查询RoleModule
     *
     * @param role
     * @return
     */
    public List<RoleModule> getRoleModuleByRole(Role role) {
        List<RoleModule> result = null;
        String hql = "From RoleModule t where t.role = ? and t.module.moduleState =" + Module.ENABLE.toString();
        Object[] params = {role};
        result = this.queryWithParamsByHQL(hql, params);
        return result;
    }

    public void add(RoleModule rm){
        super.saveOrUpdate(rm, false);
    }

    /**
     * 删除角色下的菜单权限
     * @param role
     */
    public void deleteByRole(Role role){
        String hql = "delete from RoleModule t where t.role.id="+role.getId();

        super.updateByHQL(hql);
    }

}
