package com.city.support.sys.user.service;

import com.city.support.sys.user.dao.UserRptPermissionDao;
import com.city.support.sys.user.entity.UserRptPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by zhoutao on 2016/3/8.
 */
@Service
@Transactional(readOnly = false, rollbackFor = {Exception.class}, propagation = Propagation.REQUIRED)
public class UserRptPermissionService {
    @Autowired
    private UserRptPermissionDao userRptPermissionDao;

    public void save(UserRptPermission urp){
        userRptPermissionDao.add(urp);
    }

    /**
     * 删除用户相关报表权限
     * @param userId 用户id
     */
    public void removeRptPermissionByUserId(Integer userId){
        userRptPermissionDao.deleteByUserId(userId);
    }
}
