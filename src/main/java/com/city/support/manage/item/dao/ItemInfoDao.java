package com.city.support.manage.item.dao;

import com.city.common.dao.BaseDao;
import com.city.support.manage.item.entity.ItemInfo;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Administrator on 2016/1/5 0005.
 */
@Repository
public class ItemInfoDao extends BaseDao<ItemInfo> {

    /**
     * 根据指标获取
     * @param itemId
     * @return
     */
    public List<ItemInfo> getItemInfosByItem(Integer itemId){
        String hql="from ItemInfo where itemId=?";
        return queryWithParamsByHQL(hql,new Object[]{itemId});
    }

    /**
     * 获取最大顺序
     * @param itemId
     * @return
     */
    public List getMaxSort(Integer itemId) {
        String hql="select max(sortIndex) from ItemInfo where itemId="+itemId;
        return queryByHQL(hql);
    }

    /**
     * 根据指标id删除分组内容
     */
    public void removeItemInfosByItemIds(String itemIds){
        String hql="delete from ItemInfo where itemId in ("+ itemIds +")";
        updateByHQL(hql);
    }

    /**
     * 删除指标信息
     */
    public void removeItemInfos(String ids){
        String hql="delete from ItemInfo where id in ("+ ids +")";
        updateByHQL(hql);
    }
}
