package com.city.support.manage.item.dao;

import com.city.common.dao.BaseDao;
import com.city.support.manage.item.entity.ItemGroupInfo;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Administrator on 2015/12/29 0029.
 */
@Repository
public class ItemGroupInfoDao extends BaseDao<ItemGroupInfo> {

    /**
     * 根据分组获取信息
     */
    public List<ItemGroupInfo> getInfosByGroup(String itemName, Integer status, String groupIds) {
        StringBuilder sb = new StringBuilder("from ItemGroupInfo where groupId in (").append(groupIds).append(")");
        if (itemName != null && itemName.trim().length() > 0) {
            sb.append(" and itemName like '%").append(itemName).append("%'");
        }
        if (status != null) {
            sb.append(" and status=").append(status);
        }
        sb.append("order by groupId,sortIndex");
        return queryByHQL(sb.toString());
    }

    /**
     * 查找指标
     */
    public List<ItemGroupInfo> getInfosByGroupAndItems(String itemIds, Integer groupId) {
        StringBuilder sb = new StringBuilder("from ItemGroupInfo where groupId=").append(groupId).append(" and item.id in (").append(itemIds).append(")");
        return queryByHQL(sb.toString());
    }

    /**
     * 获取分组下的排序最大值
     *
     * @param groupId
     * @return
     */
    public List getMaxSort(Integer groupId) {
        String hql = "select max(sortIndex) from ItemGroupInfo";
        return queryByHQL(hql);
    }

    /**
     * 删除分组内容
     *
     * @param ids
     */
    public void removeItemGroupInfos(String ids) {
        String hql = "delete from ItemGroupInfo where id in (" + ids + ")";
        updateByHQL(hql);
    }

    /**
     * 根据分组id删除分组内容
     */
    public void removeItemGroupInfosByGroupIds(String groupIds) {
        String hql = "delete from ItemGroupInfo where groupId in (" + groupIds + ")";
        updateByHQL(hql);
    }

    /**
     * 根据指标id删除分组内容
     */
    public void removeItemGroupInfosByItemIds(String itemIds) {
        String hql = "delete from ItemGroupInfo where item.id in (" + itemIds + ")";
        updateByHQL(hql);
    }

    /**
     * 根据指标id改变状态
     */
    public void setStatus(String itemIds, Integer status) {
        String hql = "update ItemGroupInfo set status=" + status + " where item.id in (" + itemIds + ")";
        updateByHQL(hql);
    }

    /**
     * 清除指标中的口径
     */
    public void clearCaliber(String caliberIds) {
        String hql = "update ItemGroupInfo set caliberId=null where caliberId in (" + caliberIds + ")";
        updateByHQL(hql);
    }

    /**
     * 保存指标顺序
     */
    public void saveSorts(int id, int sortIndex) {
        String hql = "update ItemGroupInfo set sortIndex=? where id=?";
        updateWithParamsByHQL(hql, new Object[]{sortIndex, id});
    }


    /**
     * 清空默认部门
     */
    public void clearDepId(String depIds) {
        String hql = "update ItemGroupInfo set department.id=null where department.id in (" + depIds + ")";
        updateByHQL(hql);
    }
}
