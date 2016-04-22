package com.city.support.sys.user.dao;

import com.city.common.dao.BaseDao;
import com.city.support.sys.user.entity.RptPermission;
import com.city.support.sys.user.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by zhoutao on 2016/3/8.
 */
@Repository
public class RptPermissionDao extends BaseDao<RptPermission> {

    public void add(RptPermission rp){
        super.insert(rp,false);
    }

    /**
     * 根据信息查询报表权限
     * @param rptPermission
     * @return
     */
    public List<RptPermission> findByInfo(RptPermission rptPermission){
        String hql = "from RptPermission r where r.rptId="+rptPermission.getRptId()+" and r.permissionType="+rptPermission.getPermissionType().ordinal();

        return super.queryByHQL(hql);
    }

}
