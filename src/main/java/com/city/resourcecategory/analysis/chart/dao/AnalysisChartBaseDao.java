package com.city.resourcecategory.analysis.chart.dao;

import com.city.common.dao.BaseDao;
import com.city.resourcecategory.analysis.chart.entity.AnalysisChartBase;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wys on 2016/2/23.
 */
@Repository
public class AnalysisChartBaseDao extends BaseDao<AnalysisChartBase> {
    public List<AnalysisChartBase> queryAnalysisChartBaseByGroupId(Integer groupId) {
        String hql = "From AnalysisChartBase t where t.groupId = ? order by t.chartSort";
        Object[] param = {groupId};
        return queryWithParamsByHQL(hql, param);
    }

    public List<AnalysisChartBase> queryAllChart() {
        StringBuilder sb = new StringBuilder("From AnalysisChartBase t order by t.chartSort");
        return queryByHQL(sb.toString());
    }

}
