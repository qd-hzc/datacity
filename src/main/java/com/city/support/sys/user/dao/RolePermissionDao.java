package com.city.support.sys.user.dao;

import com.city.common.dao.BaseDao;
import com.city.support.sys.user.entity.Role;
import com.city.support.sys.user.entity.RolePermission;
import org.springframework.stereotype.Repository;

/**
 * Created by zhoutao on 2016/2/2.
 */
@Repository
public class RolePermissionDao extends BaseDao<RolePermission> {

    public void add(RolePermission rp ){
        super.insert(rp, false);
    }

    public void deleteByRole(Role role){
        String hql = "delete from RolePermission p where p.role.id="+role.getId();

        super.updateByHQL(hql);
    }
}
