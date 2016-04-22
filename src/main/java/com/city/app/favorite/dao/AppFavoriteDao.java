package com.city.app.favorite.dao;

import com.city.app.dataDict.entity.AppDataDictMenu;
import com.city.app.favorite.entity.AppFavorite;
import com.city.common.dao.BaseDao;
import com.city.common.util.ListUtil;
import com.city.common.util.StringUtil;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wxl on 2016/4/8.
 */
@Repository
public class AppFavoriteDao extends BaseDao<AppFavorite> {

    /**
     * 查询
     *
     * @param menuId    目录id
     * @param year      内容:年
     * @param period    内容:月
     * @param sender    推送(订阅)人
     * @param receiver  接收人
     * @param orderType 类型,订阅或推送
     */
    public List<AppFavorite> queryFavorites(Integer menuId, Integer year, Integer period, Integer sender, Integer receiver, Integer orderType) {
        StringBuilder sb = new StringBuilder("from AppFavorite where 1=1");
        if (menuId != null) {
            sb.append(" and menuId=").append(menuId);
        }
        if (year != null) {
            sb.append(" and year=").append(year);
        }
        if (period != null) {
            sb.append(" and period=").append(period);
        }
        if (sender != null) {
            sb.append(" and sender=").append(sender);
        }
        if (receiver != null) {
            sb.append(" and receiver=").append(receiver);
        }
        if (orderType != null) {
            sb.append(" and orderType=").append(orderType);
        }
        sb.append(" order by orderDate desc");
        return queryByHQL(sb.toString());
    }

    /**
     * 取消订阅或推动
     */
    public void cancelOrder(Integer menuId, Integer year, Integer period, Integer sender, Integer receiver, Integer orderType) {
        StringBuilder sb = new StringBuilder("delete from AppFavorite where 1=1");
        if (menuId != null) {
            sb.append(" and menuId=").append(menuId);
        }
        if (year != null) {
            sb.append(" and year=").append(year);
        }
        if (period != null) {
            sb.append(" and period=").append(period);
        }
        if (sender != null) {
            sb.append(" and sender=").append(sender);
        }
        if (receiver != null) {
            sb.append(" and receiver=").append(receiver);
        }
        if (orderType != null) {
            sb.append(" and orderType=").append(orderType);
        }
        updateByHQL(sb.toString());
    }

    /**
     * 删除
     */
    public void delete(String ids) {
        StringBuilder sb = new StringBuilder("delete from AppFavorite where id in (").append(ids).append(")");
        updateByHQL(sb.toString());
    }

    /**
     * 查询目录
     */
    public List<AppDataDictMenu> queryMenus(String menuIds) {
        List<AppDataDictMenu> menus = new ArrayList<>();
        if (StringUtil.trimNotEmpty(menuIds)) {
            //本级
            StringBuilder sb1 = new StringBuilder("from AppDataDictMenu where id in (").append(menuIds).append(")");
            List<AppDataDictMenu> list1 = getSession().createQuery(sb1.toString()).list();
            //父级
            StringBuilder sb2 = new StringBuilder("from AppDataDictMenu where id in (").append("select parentId ").append(sb1).append(")");
            List<AppDataDictMenu> list2 = getSession().createQuery(sb2.toString()).list();
            //组装
            if (ListUtil.notEmpty(list1)) {
                menus.addAll(list1);
            }
            if (ListUtil.notEmpty(list2)) {
                menus.addAll(list2);
            }
        }
        return menus;
    }

    /**
     * 查询有内容的目录
     */
    public List<AppDataDictMenu> queryExistContentMenus(String menuIds, String name, Integer status) {
        StringBuilder sb = new StringBuilder("from AppDataDictMenu t where id in (").append(menuIds).append(")");
        if (StringUtil.trimNotEmpty(name)) {
            sb.append("and name like '%").append(name.trim()).append("%'");
        }
        if (status != null) {
            sb.append(" and t.status=").append(status);
        }
        sb.append(" and exists (select d.menuId from AppDataDict d where d.menuId = t.id)");
        return getSession().createQuery(sb.toString()).list();
    }
}
