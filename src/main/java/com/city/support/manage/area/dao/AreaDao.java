package com.city.support.manage.area.dao;

import com.city.common.dao.BaseDao;
import com.city.support.manage.area.entity.SptMgrAreaEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 地区
 * 地区相关数据库操作
 * Created by HZC on 2015/12/30.
 */
@Repository
public class AreaDao extends BaseDao<SptMgrAreaEntity> {
    /**
     * 查询地区树（状态为启用）
     *
     * @param areaName
     * @return
     */
    public List<SptMgrAreaEntity> queryAreaByName(String areaName) {
        String hql = "from SptMgrAreaEntity t where t.status=1 and t.name like ?";
        Object[] param = {"%" + areaName + "%"};
        return this.queryWithParamsByHQL(hql, param);
    }

    /**
     * 批量删除
     *
     * @param entities
     * @author hzc
     * @createDate 2016-2-16
     */
    public void deleteByIds(List<SptMgrAreaEntity> entities) {
        StringBuffer sql = new StringBuffer();
        sql.append("DELETE from SptMgrAreaEntity where id in (");
        for (SptMgrAreaEntity entity : entities) {
            sql.append(entity.getId());
            sql.append(",");
        }
        sql.append("-1 )");
        updateByHQL(sql.toString());
    }

    /**
     * 返回地区集合
     * <pre>
     *     根据父id，查询地区
     * </pre>
     *
     * @param areaId
     * @return
     */
    public List<SptMgrAreaEntity> getAreasByParentId(Integer areaId) {
        String sql = "from SptMgrAreaEntity where parent_id = " + areaId + " order by sort";
        return queryByHQL(sql);
    }

    /**
     * 获取所有地区
     *
     * @return
     */
    public List<SptMgrAreaEntity> getAllAreas() {
        return queryByHQL("from SptMgrAreaEntity order by id");
    }

    /**
     * 返回最大序号
     *
     * @param parentId
     * @return
     */
    public List getMaxSort(Integer parentId) {
        return queryByHQL("select max(sort) from SptMgrAreaEntity where parentId = " + parentId);
    }
}
