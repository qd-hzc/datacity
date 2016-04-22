package com.city.resourcecategory.analysis.chart.dao;

import com.city.common.dao.BaseDao;
import com.city.resourcecategory.analysis.chart.entity.AnalysisChartStructure;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wys on 2016/2/29.
 */
@Repository
public class AnalysisChartStructureDao extends BaseDao<AnalysisChartStructure> {
    public List<AnalysisChartStructure> queryChartStructure(Integer chartId, Integer structureType) {
        String hql = "From AnalysisChartStructure t where t.chartId = ? and structureType = ? order by t.structureSort";
        Object[] params = {chartId, structureType};
        return queryWithParamsByHQL(hql, params);
    }

    public void deleteById(Integer id) {
        String hql = "delete AnalysisChartStructure t where t.id = ?";
        Query q = getSession().createQuery(hql);
        q.setInteger(0,id);
        q.executeUpdate();
    }

    public AnalysisChartStructure queryByChartInfoId(Integer id) {
        String hql = "From AnalysisChartStructure t where t.analysisChartInfo.id = ? order by t.structureSort";
        Object[] param = {id};
        List<AnalysisChartStructure> result = queryWithParamsByHQL(hql, param);
        if (result.size() > 0)
            return result.get(0);
        else
            return null;
    }
}
