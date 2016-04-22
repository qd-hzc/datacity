package com.city.support.regime.report.dao;

import com.city.common.dao.BaseDao;
import com.city.support.regime.report.entity.ReportGroup;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 综合表分组
 * Created by HZC on 2016/4/15.
 */
@Repository
public class ReportGroupDao extends BaseDao<ReportGroup> {

    /**
     * 返回综合表分组
     * <pre>
     *     根据综合表名字和状态，查询所有符合条件的综合表分组
     * </pre>
     *
     * @param name   综合表名称
     * @param status 综合表状态
     * @return
     * @author hzc
     * @createDate 2016-4-15
     */
    public List<ReportGroup> selectGroups(String name, Integer status) {
        StringBuilder sb = new StringBuilder("from ReportGroup where 1=1");
        if (name != null && name.trim().length() > 0) {
            sb.append(" and name like '%").append(name).append("%'");
        }
        if (status != null) {
            sb.append(" and status =").append(status);
        }
        sb.append(" order by parentId,sort");
        return queryByHQL(sb.toString());
    }

    /**
     * 返回综合表分组
     * <pre>
     *     根据父id，查询综合表分组
     * </pre>
     *
     * @param groupId 父id
     * @return
     * @author hzc
     * @createDate 2016-4-15
     */
    public List<ReportGroup> selectGroupsByParentId(Integer groupId) {
        String hql = "from ReportGroup where parentId=? order by sort";
        return queryWithParamsByHQL(hql, new Object[]{groupId});
    }

    /**
     * 获取最大顺序
     *
     * @param parentId
     * @return
     */
    public List getMaxSort(Integer parentId) {
        String hql = "select max(sort) from ReportGroup where parentId=" + parentId;
        return queryByHQL(hql);
    }

    /**
     * 删除分组
     * <pre>
     *      根据id集合，删除所有综合表分组
     * </pre>
     *
     * @param ids 分组id集合
     * @author hzc
     * @createDate 2016-4-15
     */
    public void removeGroups(String ids) {
        updateByHQL("delete from ReportGroup where id in (" + ids + ")");
    }

    /**
     * 返回综合表分组
     * <pre>
     *     根据分组id集合，返回所有综合表分组
     * </pre>
     *
     * @param ids
     * @return
     * @author hzc
     * @createDate 2016-4-18
     */
    public List<ReportGroup> selectGroupsByIds(String ids) {
        return queryByHQL("from ReportGroup where id in (" + ids + ")");
    }
}
