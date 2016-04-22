package com.city.resourcecategory.analysis.chart.dao;

import com.city.common.dao.BaseDao;
import com.city.common.pojo.Constant;
import com.city.resourcecategory.analysis.chart.entity.AnalysisChartInfo;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wys on 2016/2/29.
 */
@Repository
public class AnalysisChartInfoDao extends BaseDao<AnalysisChartInfo> {
    public List<AnalysisChartInfo> queryChartInfo(Integer chartId, Integer infoType) {
        String hql = "From AnalysisChartInfo t where t.chartId = ? and t.infoType = ? order by t.infoSort";
        Object[] params = {chartId, infoType};
        return queryWithParamsByHQL(hql, params);
    }

    public AnalysisChartInfo queryByIdStructureId(Integer chartId, Integer structureId) {
        List<AnalysisChartInfo> result = null;
        String hql = "From AnalysisChartInfo t where t.chartId = ? and t.structureId = ? order by t.infoSort";
        Object[] params = {chartId, structureId};
        result = queryWithParamsByHQL(hql, params);
        if (result.size() > 0)
            return result.get(0);
        else
            return null;
    }

    public void deleteByStructureId(Integer id) {
        String hql = "delete AnalysisChartInfo t where t.structureId = ?";
        Query q = getSession().createQuery(hql);
        q.setInteger(0,id);
        q.executeUpdate();
    }

    public Integer getMapCount(Integer chartId){
        StringBuilder sb = new StringBuilder("select count(*) from AnalysisChartInfo t where 1=1");
        if(chartId!= null){
            sb.append(" and t.chartId ='").append(chartId).append("'");
        }
        sb.append(" and t.chartType ='").append(Constant.ANALYSISCHART_INFO.CHART_MAP).append("'");
        List chartInfos = queryByHQL(sb.toString());
        if(chartInfos!=null&&chartInfos.size()>0){
            long count= (long) chartInfos.get(0);
            return (int) count;
        }
        return 0;
    }

}
