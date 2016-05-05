package com.city.support.sys.module.util;

import com.city.common.util.tree.GenTreeUtil;
import com.city.support.sys.module.entity.Module;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wys on 2016/1/4.
 */
public class GenModuleTreeUtil extends GenTreeUtil<Module> {

    @Override
    protected Object getEntityPid(Module entity) {
        return entity.getModulePid();
    }

    @Override
    public Map<String, Object> genTreeNode(Module entity) {
        Map<String, Object> result = new HashMap<>();
        result.put("id", entity.getId() + "");
        result.put("moduleName", entity.getModuleName());
        result.put("text", entity.getModuleName());
        result.put("moduleType", entity.getModuleType());
        result.put("sort", entity.getModuleSort());
        result.put("moduleShortName", entity.getModuleShortName());
        result.put("moduleSort", entity.getModuleSort());

        result.put("moduleIndex", entity.getModuleIndex());
        result.put("moduleConfig", entity.getModuleConfig());
        result.put("moduleParams", entity.getModuleParams());
        result.put("moduleDesc", entity.getModuleDesc());

        result.put("moduleIcon", entity.getModuleIcon());
        result.put("modulePic", entity.getModulePic());

        result.put("moduleComment", entity.getModuleComment());

        result.put("modulePid", entity.getModulePid() + "");
        result.put("moduleState", entity.getModuleState());

        result.put("module", entity);
        return result;
    }
}
