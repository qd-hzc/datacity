package com.city.support.manage.item.dao;

import com.city.common.dao.BaseDao;
import com.city.support.manage.item.entity.ItemGroup;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Administrator on 2015/12/29 0029.
 */
@Repository
public class ItemGroupDao extends BaseDao<ItemGroup> {

    /**
     * 查询下级
     */
    public List<ItemGroup> getDownGroups(Integer groupId){
        String hql="from ItemGroup where parentId=? order by sortIndex";
        return queryWithParamsByHQL(hql,new Object[]{groupId});
    }

    /**
     * 获取分组
     * @param name
     * @param status
     * @return
     */
    public List<ItemGroup> queryGroups(String name, Integer status) {
        StringBuilder sb=new StringBuilder("from ItemGroup where 1=1");
        if(name!=null&&name.trim().length()>0){
            sb.append(" and name like '%").append(name).append("%'");
        }
        if(status!=null){
            sb.append(" and status =").append(status);
        }
        sb.append(" order by sortIndex");
        return queryByHQL(sb.toString());
    }

    /**
     * 获取最大排序
     * @return
     */
    public List getMaxSort(Integer parentId) {
        String hql="select max(sortIndex) from ItemGroup where parentId="+parentId;
        return queryByHQL(hql);
    }

    /**
     * 删除分组
     * @param ids
     */
    public void removeGroups(String ids) {
        String hql="delete from ItemGroup where id in ("+ids+")";
        updateByHQL(hql);
    }
}
