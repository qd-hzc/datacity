package com.city.support.sys.user.dao;

import com.city.common.dao.BaseDao;
import com.city.support.sys.user.entity.User;
import com.city.support.sys.user.entity.UserRptPermission;
import org.springframework.stereotype.Repository;

/**
 * Created by zhoutao on 2016/3/8.
 */
@Repository
public class UserRptPermissionDao extends BaseDao<UserRptPermission> {

    public void add(UserRptPermission urp){
        super.insert(urp, false);
    }

    //删除用户相关报表权限
    public void deleteByUserId(Integer userId){
        String hql = "delete from UserRptPermission u where u.user.id="+userId;

        super.updateByHQL(hql);
    }
}
