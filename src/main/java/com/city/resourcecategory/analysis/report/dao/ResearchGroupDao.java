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
}
