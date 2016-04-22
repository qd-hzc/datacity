package com.city.support.sys.user.service;

import com.city.support.sys.user.entity.RptPermission;
import com.city.support.sys.user.entity.User;
import com.city.support.sys.user.pojo.PermissionType;
import com.city.support.sys.user.pojo.ReportPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by zhoutao on 2016/3/9.
 * 报表权限信息DTO 服务类
 */
@Service
public class ReportPermissionService {
    @Autowired
    private UserManagerService userManagerService;

    /**
     * 获取用户报表权限
     * @param user
     * @return
     */
    public Map<Integer, ReportPermission> findReportPermissionByUser(User user){
        //以报表模板id为key
        Map<Integer, ReportPermission> result = new HashMap<Integer, ReportPermission>();

        Set<RptPermission> rptPermissionSet = userManagerService.getUserRptPermissions(user);//获取用户报表权限

        for (RptPermission perm : rptPermissionSet) {
            ReportPermission rp = result.get(perm.getRptId());
            if (rp == null) {
                rp = new ReportPermission();
                rp.setReportTmpId(perm.getRptId());
                result.put(perm.getRptId(), rp);
            }

            processPermissionType(perm.getPermissionType(), rp);//处理权限类型
        }

        return result;
    }

    //处理权限类型
    private void processPermissionType(PermissionType pt, ReportPermission rp){
        if(pt == PermissionType.READ)
            rp.setRead(true);
        if(pt == PermissionType.WRITE)
            rp.setWrite(true);
        if(pt == PermissionType.APPROVAL)
            rp.setAPPROVAL(true);
    }
}
