package com.city.common.util.tree;

import com.city.common.util.ListUtil;
import org.apache.commons.beanutils.BeanUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wxl on 2016/3/24.
 * 不从数据库查询,自己提供一个list作为库,来封装树
 */
public class PackageLocalListToTree<T> extends PackageListToTree<T> {

    private List<T> allEntities;

    /**
     * 提供所有实体
     *
     * @param allEntities
     */
    public PackageLocalListToTree(List<T> allEntities) {
        this.allEntities = allEntities;
    }

    @Override
    protected T getEntityById(Integer entityKey) {
        if (ListUtil.notEmpty(allEntities)) {
            for (T t : allEntities) {
                if (getEntityKey(t).equals(entityKey)) {
                    return t;
                }
            }
        }
        return null;
    }

    @Override
    public Map<String, Object> getEntityMap(T t) {
        Map map = new HashMap();
        try {
            map = BeanUtils.describe(t);
            map.put("id", getEntityKey(t));
            map.put("parentId", getParentId(t));
            map.put("text", getEntityName(t));
            map.put("leaf", true);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public List<T> getAllEntities() {
        return allEntities;
    }

    public void setAllEntities(List<T> allEntities) {
        this.allEntities = allEntities;
    }
}
