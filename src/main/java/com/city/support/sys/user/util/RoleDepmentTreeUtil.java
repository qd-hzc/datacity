package com.city.support.sys.user.util;

import com.city.common.util.tree.GenTreeUtil;
import com.city.support.sys.user.entity.Department;
import com.city.support.sys.user.pojo.ReportPermissionType;

import java.util.HashMap;
import java.util.Map;


public class RoleDepmentTreeUtil extends GenTreeUtil<Department> {
    @Override
    public Map<String, Object> genTreeNode(Department entity) {
        Map<String,Object> result = new HashMap<>();
        result.put("id",entity.getId()+"");
        result.put("depId",entity.getId()+"");
        result.put("text",entity.getDepName()+" 报表");
        result.put("reportPermissionType", ReportPermissionType.DEPARTMENT_OTHERS);
        result.put("checked",false);
        result.put("flagPid",entity.getpDep()+"");
        result.put("expanded",true);
        return result;
    }

    @Override
    protected Object getEntityPid(Department entity) {
        return entity.getpDep();
    }
/*    @Override
    protected boolean isLeaf(Map<String,Object> node){
        return false;
    }*/
}
