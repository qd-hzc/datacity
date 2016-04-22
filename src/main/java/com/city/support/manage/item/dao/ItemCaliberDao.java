package com.city.support.manage.item.dao;

import com.city.common.dao.BaseDao;
import com.city.support.manage.item.entity.ItemCaliber;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Administrator on 2015/12/28 0028.
 */
@Repository
public class ItemCaliberDao extends BaseDao<ItemCaliber> {

    /**
     * 根据指标获取口径
     * @param itemId
     * @return
     */
    public List<ItemCaliber> queryCalibersByItem(Integer itemId) {
        String hql="from ItemCaliber where itemId=? order by sortIndex";
        return queryWithParamsByHQL(hql,new Object[]{itemId});
    }

    /**
     * 获取最大排序
     * @param itemId
     * @return
     */
    public List getMaxSort(Integer itemId) {
        String hql="select max(sortIndex) from ItemCaliber where itemId="+itemId;
        return queryByHQL(hql);
    }

    /**
     * 保存顺序
     */
    public void saveSorts(Integer id,Integer sortIndex){
        String hql="update ItemCaliber set sortIndex=? where id=?";
        updateWithParamsByHQL(hql,new Object[]{sortIndex,id});
    }
}
