package com.city.support.sys.user.service;

import com.city.support.sys.user.dao.RptPermissionDao;
import com.city.support.sys.user.entity.RptPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by zhoutao on 2016/3/8.
 */
@Service
@Transactional(readOnly = false, rollbackFor = Exception.class)
public class RptPermissionService {
    @Autowired
    private RptPermissionDao rptPermissionDao;
    /**
     * 根据条件查询获取报表权限信息
     * @param rpt
     * @return
     */
    public List<RptPermission> findRptPermissionByInfo(RptPermission rpt){
        return rptPermissionDao.findByInfo(rpt);
    }

    public void save(RptPermission rpt){
        rptPermissionDao.add(rpt);
    }
}

