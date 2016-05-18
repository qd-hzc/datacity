package com.city.resourcecategory.analysis.chart.dao;

import com.city.common.dao.BaseDao;
import com.city.common.pojo.Page;
import com.city.common.util.StringUtil;
import com.city.resourcecategory.analysis.chart.entity.AnalysisChartBase;
import com.city.resourcecategory.analysis.common.entity.QueryResourceVO;
import org.hibernate.SQLQuery;
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

    public List<AnalysisChartBase> getByNameAndId(String title, Integer groupId, Integer id) {
        String hql = "from AnalysisChartBase t where t.title ='" + title + "' and t.groupId ='" + groupId + "' and t.id <>'" + id + "'";
        return super.queryByHQL(hql);
    }

    public List<AnalysisChartBase> getByAllNameAndGroupId(String title, Integer groupId) {
        String hql = "from AnalysisChartBase t where t.title ='" + title + "' and t.groupId ='" + groupId + "'";
        return super.queryByHQL(hql);
    }


    /**
     * 返回图表分析数量
     * <pre>
     *     根据图表名称，模糊查询图表分析，返回匹配数量
     * </pre>
     *
     * @param text
     * @return
     * @author hzc
     * @createDate 2016-5-13
     */
    public int selectForSearchCount(String text) {
        String hql = "select count(id) from AnalysisChartBase t where 1=1 ";
        if (StringUtil.notEmpty(text)) {
            hql = hql + " and t.title like '%" + text + "%'";
        }
        List list = queryByHQL(hql);
        if (null != list && list.size() > 0) {
            long l = (long) list.get(0);
            return (int) l;
        }
        return 0;
    }

    /**
     * 返回图表分析
     * <pre>
     *     根据图表名称，模糊查询图表分析，分页
     * </pre>
     *
     * @param text
     * @param page
     * @return
     * @author hzc
     * @createDate 2016-5-13
     */
    public List selectForSearch(String text, Page page) {
        StringBuffer hql = new StringBuffer("SELECT RAC.ID, RAC.title AS NAME, 4 AS TYPE, RAC.PERIOD_TYPE AS period, '' AS comments, RAC.GROUP_ID AS extraId, RACG.\"NAME\" as extraName, -1 AS departmentId, '' as departmentName ");
        hql.append(",rownum as rn FROM RC_ANALYSIS_CHARTBASE rac LEFT JOIN RC_ANALYSIS_CHARTGROUP racg ")
                .append(" ON RAC.\"GROUP_ID\" = RACG.\"ID\" where 1=1 ");
        if (StringUtil.notEmpty(text)) {
            hql.append(" and rac.title like '%").append(text).append("%'");
        }
        hql.append(" order by rac.id ");
        System.out.println(hql.toString());
        SQLQuery q = getSession().createSQLQuery(hql.toString());
        q.addEntity(QueryResourceVO.class);
        setPageParamsForQuery(q, page);
        return (List<QueryResourceVO>) q.list();
    }
}
