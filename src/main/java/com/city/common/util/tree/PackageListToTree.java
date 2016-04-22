package com.city.common.util.tree;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.MapUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/12/31 0031.
 * 将list打包成map
 * 默认情况下,主键为id,父节点为parentId,名称为name  若不同请重写
 */
public abstract class PackageListToTree<T> {
    /**
     * 将List<T>封装成树
     *
     * @param entities    实体列表
     * @param isNeedColor 是否需要将查出来的实例加色
     */
    public List<Map<String, Object>> packageListToTree(List<T> entities, boolean isNeedColor) {
        return packageListToTree(entities, 0, isNeedColor);
    }

    /**
     * 将List<T>封装成树
     *
     * @param entities    实体列表
     * @param rootId      需要加载到的根节点
     * @param isNeedColor 是否需要将查出来的实例加色
     */
    public List<Map<String, Object>> packageListToTree(List<T> entities, Integer rootId, boolean isNeedColor) {
        if (entities != null && entities.size() > 0) {
            // 全局map
            Map<Integer, Map<String, Object>> globalMap = new HashMap<>();
            //根节点
            Map<String, Object> rootMap = new HashMap<>();
            globalMap.put(rootId, rootMap);
            //开始封装
            for (T entity : entities) {
                int key = getEntityKey(entity);
                Map<String, Object> entityMap = globalMap.get(key);
                //
                if (entityMap == null) {
                    entityMap = getEntityMap(entity);
                    //存入全局
                    globalMap.put(key, entityMap);
                    queryForGlobalMap(globalMap, entityMap);
                }
                if (isNeedColor) {
                    entityMap.put("text", appendFontColor(getEntityName(entity)));
                }
            }
            return (List<Map<String, Object>>) globalMap.get(rootId).get("children");
        }
        return null;
    }

    /**
     * 从全局map中查找
     */
    private void queryForGlobalMap(Map<Integer, Map<String, Object>> globalMap, Map<String, Object> currentMap) {
        Integer parentId = (Integer) currentMap.get("parentId");
        while (true) {
            if (parentId == null) {
                return;
            }
            Map<String, Object> parentMap = globalMap.get(parentId);
            if (parentMap != null) {
                //直接放入
                List<Map<String, Object>> children = (List<Map<String, Object>>) parentMap.get("children");
                if (children == null) {
                    children = new ArrayList<>();
                    parentMap.put("children", children);
                }
                parentMap.put("leaf", false);
                children.add(currentMap);
                //存入全局
                globalMap.put((Integer) currentMap.get("id"), currentMap);
                return;
            }
            if (parentMap == null) {
                T dep = getEntityById(parentId);
                if (dep == null) {
                    return;
                }
                parentMap = getEntityMap(dep);
                //存入全局
                globalMap.put(parentId, parentMap);
                //放入
                List<Map<String, Object>> children = (List<Map<String, Object>>) parentMap.get("children");
                if (children == null) {
                    children = new ArrayList<>();
                    parentMap.put("children", children);
                }
                parentMap.put("leaf", false);
                children.add(currentMap);
                //变换条件
                currentMap = parentMap;
                parentId = (Integer) currentMap.get("parentId");
            }
        }
    }

    /**
     * 根据主键获取实体类
     */
    protected abstract T getEntityById(Integer entityKey);

    /**
     * 获取主键
     */
    protected Integer getEntityKey(T t) {
        try {
            Map map = BeanUtils.describe(t);
            return MapUtils.getInteger(map, "id");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取父id,若属性值不为parentId,需要重写
     */
    protected Integer getParentId(T t) {
        try {
            Map map = BeanUtils.describe(t);
            return MapUtils.getInteger(map, "parentId");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取名称,默认为name属性
     */
    protected String getEntityName(T t) {
        try {
            Map map = BeanUtils.describe(t);
            return MapUtils.getString(map, "name");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将实体封装
     * 若需要其他字段,可在子类中重写该方法
     */
    public Map<String, Object> getEntityMap(T t) {
        Map map = new HashMap();
        map.put("id", getEntityKey(t));
        map.put("parentId", getParentId(t));
        map.put("text", getEntityName(t));
        map.put("leaf", true);
        return map;
    }


    /**
     * 给文本添加颜色
     */
    private String appendFontColor(String name) {
        return "<font color=\"red\">" + name + "</font>";
    }
}
