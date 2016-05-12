package com.city.support.sys.user.util;

import com.city.common.util.tree.GenTreeUtil;
import com.city.support.sys.user.entity.Department;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wys on 2016/1/4.
 */
public class GenDepmentTreeUtil extends GenTreeUtil<Department> {
    @Override
    public Map<String, Object> genTreeNode(Department entity) {
        Map<String,Object> result = new HashMap<>();
        result.put("id",entity.getId());
        result.put("depName",entity.getDepName());
        result.put("depShortName",entity.getDepShortName());
        result.put("depLevel",entity.getDepLevel());
        result.put("pDep",entity.getpDep());
        result.put("sort",entity.getSort());
        result.put("text",entity.getText());
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
