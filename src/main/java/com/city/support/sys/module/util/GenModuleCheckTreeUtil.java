package com.city.support.sys.module.util;

import com.city.common.util.tree.GenTreeUtil;
import com.city.support.sys.module.entity.Module;

import java.util.HashMap;
import java.util.Map;

public class GenModuleCheckTreeUtil extends GenTreeUtil<Module> {

    @Override
    protected Object getEntityPid(Module entity) {
        return entity.getModulePid();
    }

    @Override
    public Map<String, Object> genTreeNode(Module entity) {
        Map<String,Object> result = new HashMap<>();
        result.put("id",entity.getId()+"");
        result.put("moduleName",entity.getModuleName());
        result.put("modulePid",entity.getModulePid()+"");
        result.put("checked", false);//add by zt  添加复选框
        result.put("expanded", true);
        return result;
    }
}
