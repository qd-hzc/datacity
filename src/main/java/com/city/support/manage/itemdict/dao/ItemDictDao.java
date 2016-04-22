package com.city.support.manage.itemdict.dao;

import com.city.common.dao.BaseDao;
import com.city.support.manage.itemdict.entity.SptMgrItemDictEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 指标分组目录类
 * Created by HZC on 2015/12/31.
 */
@Repository
public class ItemDictDao extends BaseDao<SptMgrItemDictEntity> {

    /**
     * 返回一个指标分组目录
     * <pre>
     *     根据id查询指标分组目录
     * </pre>
     *
     * @param itemDictId
     * @return
     * @author hzc
     * @createDate 2015-12-31
     */
    public SptMgrItemDictEntity getItemDictById(Integer itemDictId) {
        String sql = "from SptMgrItemDictEntity where status = " + SptMgrItemDictEntity.ENABLE + " and id = " + itemDictId;
        return unqueryByHQL(sql);
    }

    /**
     * 返回指标分组目录集合
     * <pre>
     *     根据指标分组目录id获取该目录下级所有目录
     * </pre>
     *
     * @param itemDictId
     * @return
     * @author hzc
     * @createDate 2015-12-31
     */
    public List<SptMgrItemDictEntity> getItemDictsByParentId(Integer itemDictId) {
        String sql = "from SptMgrItemDictEntity where parent_id = " + itemDictId + " and status = " + SptMgrItemDictEntity.ENABLE;
        return queryByHQL(sql);
    }

    /**
     * 返回所有有效的指标分组目录
     *
     * @return
     * @author hzc
     * @createDate 2016-1-15
     */
    public List<SptMgrItemDictEntity> findAll() {
        return queryByHQL("from SptMgrItemDictEntity where status = " + SptMgrItemDictEntity.ENABLE);
    }

    /**
     * 批量删除
     *
     * @param entities
     * @author hzc
     * @createDate 2016-2-16
     */
    public void deleteByIds(List<SptMgrItemDictEntity> entities) {
        StringBuffer sql = new StringBuffer();
        sql.append("DELETE from SptMgrItemDictEntity  where id in (");
        for (SptMgrItemDictEntity entity : entities) {
            sql.append(entity.getId());
            sql.append(",");
        }
        sql.append("-1 )");
        updateByHQL(sql.toString());
    }

    /**
     * 返回指标分组目录
     * <pre>
     *     根据父id，查询指标分组目录
     * </pre>
     *
     * @param itemDictId
     * @return
     */
    public List<SptMgrItemDictEntity> getSptMgrItemDictEntities(Integer itemDictId) {
        String sql = "from SptMgrItemDictEntity where parent_id = " + itemDictId + "order by sort asc ,createDate asc";
        return queryByHQL(sql);
    }

    /**
     * 获取最大顺序
     *
     * @param parentId
     * @return
     */
    public List getMaxSort(Integer parentId) {
        return queryByHQL("select max(sort) from SptMgrItemDictEntity where parentId = " + parentId);
    }

    /**
     * 根据名字查询指标目录分组
     *
     * @param name
     * @return
     */
    public List<SptMgrItemDictEntity> getItemDictByName(String name) {
        return queryByHQL("from SptMgrItemDictEntity where name = '" + name + "'");
    }
}
