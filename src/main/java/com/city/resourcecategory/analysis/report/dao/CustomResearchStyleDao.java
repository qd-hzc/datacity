package com.city.resourcecategory.analysis.report.dao;

import com.city.common.dao.BaseDao;
import com.city.resourcecategory.analysis.report.entity.CustomResearchStyleEntity;
import org.springframework.stereotype.Repository;

/**
 * Created by HZC on 2016/2/26.
 */
@Repository
public class CustomResearchStyleDao extends BaseDao<CustomResearchStyleEntity> {

    /**
     * 返回自定义查询表样信息
     *
     * @param id
     * @return
     * @author hzc
     * @createDate 2016-2-26
     */
    public CustomResearchStyleEntity selectByResearchId(int id) {
        return unqueryByHQL("from CustomResearchStyleEntity where custom_research_id = " + id);
    }

    /**
     * 根据自定义查询id删除表样
     *
     * @param id
     * @author hzc
     * @createDate 2016-3-7
     */
    public void deleteByResearchId(Integer id) {
        updateByHQL("delete from CustomResearchStyleEntity where customResearchId=" + id);
    }
}
