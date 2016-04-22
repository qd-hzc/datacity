package com.city.resourcecategory.analysis.report.dao;

import com.city.common.dao.BaseDao;
import com.city.resourcecategory.analysis.report.entity.CustomResearchEntity;
import com.city.resourcecategory.analysis.report.entity.CustomResearchStyleEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 自定义查询Dao类
 * Created by HZC on 2016/2/22.
 */
@Repository
public class CustomResearchDao extends BaseDao<CustomResearchEntity> {

    /**
     * 返回自定义查询
     * <pre>
     *     根据groupId，查询自定义查询
     * </pre>
     *
     * @param groupId
     * @return
     * @author hzc
     * @createDate 2016-2-24
     */
    public List<CustomResearchEntity> selectResearchsByGroupId(int groupId) {
        String hql = "from CustomResearchEntity where researchGroupId = " + groupId + " order by createDate";
        return queryByHQL(hql);
    }

    /**
     * 删除自定义查询
     *
     * @param id
     * @author hzc
     * @createDate 2016-2-25
     */
    public void deleteById(Integer id) {
        updateByHQL("delete from CustomResearchEntity where id = " + id);
    }

    /**
     * 返回所有自定义查询表
     *
     * @return
     * @author crx
     * @createDate 2016-3-14
     */
    public List<CustomResearchEntity> selectAll() {
        return queryByHQL("from CustomResearchEntity where type = 1 and status = 1");
    }
}
