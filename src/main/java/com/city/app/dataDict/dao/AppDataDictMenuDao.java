package com.city.app.dataDict.dao;

import com.city.app.dataDict.entity.AppDataDictMenu;
import com.city.common.dao.BaseDao;
import com.city.common.util.StringUtil;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wxl on 2016/3/21.
 */
@Repository
public class AppDataDictMenuDao extends BaseDao<AppDataDictMenu> {
    /**
     * 查询数据字典目录
     *
     * @param parentId 父id
     * @param roleIds  角色id
     * @param name     关键字搜索
     * @param status   状态
     */
    public List<AppDataDictMenu> queryDictMenu(Integer parentId, String roleIds, String name, Integer status) {
        StringBuilder sb = new StringBuilder("from AppDataDictMenu where 1=1");
        if (parentId != null) {
            sb.append(" and parentId=").append(parentId);
        }
        if (roleIds != null && roleIds.length() > 0) {//角色
            sb.append(" and roleId in (").append(roleIds).append(")");
        }
        if (StringUtil.trimNotEmpty(name)) {//名称
            sb.append(" and name like '%").append(name.trim()).append("%'");
        }
        if (status != null) {
            sb.append(" and status=").append(status);
        }
        sb.append(" order by sortIndex");
        return queryByHQL(sb.toString());
    }

    /**
     * 根据名称查询
     *
     * @param name
     */
    public List<AppDataDictMenu> queryByFullName(Integer parentId, String name) {
        StringBuilder sb = new StringBuilder("from AppDataDictMenu where parentId=").append(parentId).append(" and name= '").append(name).append("'");
        return queryByHQL(sb.toString());
    }

    /**
     * 查询
     */
    public List<AppDataDictMenu> queryDictMenu(String roleIds, Integer type) {
        StringBuilder sb = new StringBuilder("from AppDataDictMenu where 1=1");
        if (roleIds != null && roleIds.length() > 0) {//角色
            sb.append(" and roleId in (").append(roleIds).append(")");
        }
        if (type != null) {//类型
            sb.append(" and type=").append(type);
        }
        sb.append(" order by sortIndex");
        return queryByHQL(sb.toString());
    }


    /**
     * 查询父节点下最大排序
     */
    public List queryMaxSort(Integer parentId) {
        StringBuilder sb = new StringBuilder("select max(sortIndex) from AppDataDictMenu where parentId=").append(parentId);
        return queryByHQL(sb.toString());
    }
}
