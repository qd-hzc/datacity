package com.city.support.sys.user.dao;

import com.city.common.dao.BaseDao;
import com.city.support.sys.user.entity.Permission;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by zhoutao on 2016/2/1.
 */
@Repository
public class PermissionDao extends BaseDao<Permission> {

    public void add(Permission pm){
        super.saveOrUpdate(pm, false);
    }

    /**
     * 根据权限查询信息获取权限
     * @param perm  查询信息
     * @return
     */
    public List<Permission> getByInfo(Permission perm){
        String hql = "from Permission p  where p.reportPermissionType="+perm.getReportPermissionType().ordinal()+" and p.permissionType="+perm.getPermissionType().ordinal()+" and p.dep="+perm.getDep();

        return super.queryByHQL(hql);
    }

}
