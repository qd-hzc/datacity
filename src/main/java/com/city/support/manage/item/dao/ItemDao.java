package com.city.support.manage.item.dao;

import com.city.common.dao.BaseDao;
import com.city.common.pojo.Page;
import com.city.support.manage.item.entity.Item;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Administrator on 2015/12/28 0028.
 */
@Repository
public class ItemDao extends BaseDao<Item> {

    /**
     * 查询指标
     */
    public List<Item> getItemsForPage(String name, Integer status, Page page) {
        StringBuilder sb = new StringBuilder("from Item where 1=1");
        if (name != null) {
            sb.append(" and name like '%").append(name.trim()).append("%'");
        }
        if (status != null) {
            sb.append(" and status=").append(status);
        }
        sb.append(" order by sortIndex DESC");
        return queryWithPageByHQL(sb.toString(), page);
    }

    /**
     * 根据ids获取
     */
    public List<Item> getItemsByIds(String ids) {
        String hql = "from Item where id in (" + ids + ")";
        return queryByHQL(hql);
    }

    /**
     * 查询最大sortIndex
     */
    public List getMaxSort() {
        String hql = "select max(sortIndex) from Item";
        return queryByHQL(hql);
    }

    /**
     * 废弃
     */
    public void setStatus(String ids, Integer status) {
        String hql = "update Item set status=" + status + " where id in (" + ids + ")";
        updateByHQL(hql);
    }

    /**
     * 清除指标中的口径
     */
    public void clearCaliber(String caliberIds) {
        String hql = "update Item set caliberId=null where caliberId in (" + caliberIds + ")";
        updateByHQL(hql);
    }

    /**
     * 清除指标中的部门
     */
    public void clearDepId(String depIds) {
        String hql = "update Item set department.id=null where department.id in (" + depIds + ")";
        updateByHQL(hql);
    }
}
