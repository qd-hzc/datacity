package com.city.resourcecategory.analysis.chart.dao;

import com.city.common.dao.BaseDao;
import com.city.resourcecategory.analysis.chart.entity.AnalysisChartGroup;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wys on 2016/2/23.
 */
@Repository
public class AnalysisChartGroupDao extends BaseDao<AnalysisChartGroup> {

    public List<AnalysisChartGroup> queryAnalysisChartGroupByPid(Integer pid) {
        String hql = "From AnalysisChartGroup t where t.pId = ?";
        Object[] param = {pid};
        return queryWithParamsByHQL(hql, param);
    }

    public List<AnalysisChartGroup> getByAllNameAndpId(String name, Integer parentId) {
        String hql = "from AnalysisChartGroup t where t.name ='" + name + "' and t.pId ='" + parentId + "'";
        return super.queryByHQL(hql);
    }

    public List<AnalysisChartGroup> getByNameAndId(String name, Integer parentId, Integer id) {
        String hql = "from AnalysisChartGroup t where t.name ='" + name + "' and t.pId ='" + parentId + "' and t.id <>'" + id + "'";
        return super.queryByHQL(hql);
    }
}
