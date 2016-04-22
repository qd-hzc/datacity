package com.city.support.sys.user.service;

import com.city.support.sys.user.dao.PermissionDao;
import com.city.support.sys.user.entity.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * Created by zhoutao on 2016/2/1.
 */
@Service
@Transactional(readOnly = false, rollbackFor = Exception.class)
public class PermissionService {
    @Autowired
    private PermissionDao permissionDao;

    public void save(Permission pm){
        permissionDao.add(pm);
    }

    /**
     * 根据权限查询信息获取权限
     * @param perm  查询信息
     * @return
     */
    public List<Permission> findByInfo(Permission perm){
        return permissionDao.getByInfo(perm);
    }

}
