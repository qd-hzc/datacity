package com.city.support.manage.area.util;

import com.city.common.util.tree.GenTreeUtil;
import com.city.support.manage.area.entity.SptMgrAreaEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wys on 2016/1/20.
 */
public class GenAreaUtil extends GenTreeUtil<SptMgrAreaEntity> {
    private boolean isCheckTree = false;
    @Override
    public Map<String, Object> genTreeNode(SptMgrAreaEntity entity) {
        Map<String, Object> result = new HashMap<>();
        result.put("id", entity.getId());
        result.put("parentId", entity.getParentId());
        result.put("code", entity.getCode());
        result.put("name", entity.getName());
        result.put("nameEn", entity.getNameEn());
        result.put("comments", entity.getComments());
        result.put("status", entity.getStatus());
        result.put("sort", entity.getSort());
        if(isCheckTree)
            result.put("checked",false);
        return result;
    }

    @Override
    protected Object getEntityPid(SptMgrAreaEntity entity) {
        return entity.getParentId();
    }

    public boolean isCheckTree() {
        return isCheckTree;
    }

    public void setIsCheckTree(boolean isCheckTree) {
        this.isCheckTree = isCheckTree;
    }
}
