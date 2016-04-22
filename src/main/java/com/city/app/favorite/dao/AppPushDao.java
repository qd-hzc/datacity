package com.city.app.favorite.dao;

import com.city.app.favorite.entity.AppPush;
import com.city.common.dao.BaseDao;
import com.city.common.util.StringUtil;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Administrator on 2016/4/18.
 */
@Repository
public class AppPushDao extends BaseDao<AppPush> {
    /**
     * 查询
     * @param receivers
     * @param name
     * @return
     */
    public List<AppPush> queryByReceivers(String receivers, String name) {
        StringBuilder hql = new StringBuilder("from AppPush where 1=1");
        if (StringUtil.trimNotEmpty(receivers)) {
            hql.append(" and receiver in (" + receivers + ")");
        }
        if (StringUtil.trimNotEmpty(name)) {
            hql.append(" and name like '%").append(name).append("%'");
        }
        hql.append(" order by time desc");
        return this.queryByHQL(hql.toString());
    }

    public void delete(String ids) {
        StringBuilder sb = new StringBuilder("delete from AppPush where id in (").append(ids).append(")");
        updateByHQL(sb.toString());
    }

    /**
     * 单个保存
     * @param appPush
     */
    public void saveAppPush(AppPush appPush) {
        if (appPush.getId() != null) {
            update(appPush, false);
        } else {
            insert(appPush, false);
        }
    }

    /**
     *批量保存
     * @param appPushs
     */
    public void saveAppPushes(List<AppPush> appPushs) {
        for (AppPush appPush : appPushs) {
            insert(appPush, true);
        }
    }

    /**
     * 查询  未读推送的数量
     */
    public List<AppPush> queryPushByFlag(Integer receiver, Integer flag) {
        StringBuilder sb = new StringBuilder("from AppPush where receiver=").append(receiver);
        if (flag != null) {
            sb.append(" and flag=").append(flag);
        }
        sb.append(" order by time desc");
        return queryByHQL(sb.toString());
    }

    /**
     * 设为已读
     */
    public void setReaded(Integer receiver, Integer menuId) {
        String hql = "update AppPush set flag= 1 where receiver=? and menuId=?";
        updateWithParamsByHQL(hql, new Object[]{receiver, menuId});
    }

}
