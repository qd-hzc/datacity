package com.city.resourcecategory.analysis.report.dao;

import com.city.common.dao.BaseDao;
import com.city.resourcecategory.analysis.report.entity.ResearchGroupEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 自定义查询分组Dao类
 * Created by HZC on 2016/2/22.
 */
@Repository
public class ResearchGroupDao extends BaseDao<ResearchGroupEntity> {

    /**
     * 返回自定义查询分组
     * <pre>
     *     根据parentId，查询自定义查询分组
     * </pre>
     *
     * @param id
     * @return
     * @author hzc
     * @createDate 2016-2-24
     */
    public List<ResearchGroupEntity> selectResearchGroupByParentId(int id) {
        return queryByHQL("from ResearchGroupEntity where parent_id = " + id + " order by sort");
    }

    /**
     * 删除自定义查询分组
     *
     * @param list
     * @author hzc
     * @createDate 2016-2-24
     */
    public void deleteResearchGroups(List<ResearchGroupEntity> list) throws Exception {
        StringBuffer sql = new StringBuffer();
        sql.append("delete from ResearchGroupEntity where id in( ");
        for (int i = 0; i < list.size(); i++) {
            sql.append(list.get(i).getId());
            sql.append(",");
        }
        sql.append("-1 )");
        updateByHQL(sql.toString());
    }

    /**
     * 获取最大序号
     *
     * @param parentId
     * @return
     */
    public List getMaxSort(int parentId) {
        return queryByHQL("select max(sort) from ResearchGroupEntity where parentId = " + parentId);
    }

    /**
     * 获取分组
     * <pre>
     *     根据分组名称和状态查询，如果参数为空，则查询所有
     * </pre>
     *
     * @param name
     * @param status
     * @return
     * @author hzc
     * @createDate 2016-5-5
     */
    public List<ResearchGroupEntity> selectGroups(String name, Integer status) {
        StringBuilder sb = new StringBuilder("from ResearchGroupEntity where 1=1");
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
     * 返回分组
     * <pre>
     *     根据父id，查询分析报表分组
     * </pre>
     *
     * @param groupId
     * @return
     * @author hzc
     * @createDate 2016-5-5
     */
    public List<ResearchGroupEntity> selectGroupsByParentId(Integer groupId) {
        String hql = "from ResearchGroupEntity where parentId=? order by sort";
        return queryWithParamsByHQL(hql, new Object[]{groupId});
    }
}
