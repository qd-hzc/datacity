package com.city.resourcecategory.analysis.report.dao;

import com.city.common.dao.BaseDao;
import com.city.common.pojo.Page;
import com.city.common.util.StringUtil;
import com.city.resourcecategory.analysis.common.entity.QueryResourceVO;
import com.city.resourcecategory.analysis.report.entity.CustomResearchEntity;
import org.hibernate.SQLQuery;
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

    /**
     * 返回报表
     *
     * @param page
     * @param groups 分组id集合
     * @param name   查询名字
     * @return
     * @author hzc
     * @createDate 2016-5-5
     */
    public List<CustomResearchEntity> getRptTmpsByCondition(Page page, String groups, String name) {
        StringBuilder sb = new StringBuilder("from CustomResearchEntity where researchGroupId in (").append(groups).append(")");
        if (name != null && name.trim().length() > 0) {
            sb.append(" and name like '%").append(name).append("%'");
        }
        sb.append(" order by id desc");

        return queryWithPageByHQL(sb.toString(), page);
    }

    /**
     * 返回分析报表数量
     *
     * @param name
     * @param groups 分组id集合
     * @return
     * @author hzc
     * @createDate 2016-5-5
     */
    public Integer getTmpCountByCondition(String name, String groups) {
        StringBuilder sb = new StringBuilder("select count(*) from CustomResearchEntity where 1=1 and researchGroupId in (").append(groups).append(")");
        if (name != null && name.trim().length() > 0) {
            sb.append(" and name like '%").append(name).append("%'");
        }

        sb.append(" order by id desc");

        List tmps = queryByHQL(sb.toString());
        if (tmps != null && tmps.size() > 0) {
            long count = (long) tmps.get(0);
            return (int) count;
        }
        return 0;
    }


    /**
     * 返回分析报表模板
     * <pre>
     *     根据分析报表名字，查询分析报表
     * </pre>
     *
     * @param name 分析报表名字
     * @return
     * @author hzc
     * @createDate 2016-5-6
     */
    public List<CustomResearchEntity> selectResearchByName(String name) {
        return queryByHQL("from CustomResearchEntity where name ='" + name + "'");
    }

    /**
     * 返回分析报表数量
     * <pre>
     *     根据分析报表名称查询，返回匹配数量
     * </pre>
     *
     * @param text
     * @return
     * @author hzc
     * @createDate 2016-5-13
     */
    public int selectForSearchCount(String text) {
        String hql = "select count(id) from CustomResearchEntity where 1=1 ";
        if (StringUtil.notEmpty(text)) {
            hql = hql + " and name like '%" + text + "%'";
        }
        List list = queryByHQL(hql);
        if (null != list && list.size() > 0) {
            return (int) (long) list.get(0);
        }
        return 0;
    }

    /**
     * 返回分析报表
     * <pre>
     *     根据分析报表名称，模糊查询
     * </pre>
     *
     * @param text
     * @param page
     * @return
     * @author hzc
     * @createDate 2016-5-13
     */
    public List selectForSearch(String text, Page page) {
        StringBuffer hql = new StringBuffer("SELECT RRCR.ID, RRCR.NAME, 3 AS TYPE, RRCR.PERIOD, RRCR.comments,");
        hql.append(" RRCR.RESEARCH_GROUP_ID AS extraId, RRRG.\"NAME\" as extraName, -1 AS departmentId,").
                append(" '' as departmentName,rownum as rn FROM RC_REPORT_CUSTOM_RESEARCH rrcr ").
                append(" LEFT JOIN RC_REPORT_RESEARCH_GROUP rrrg ON RRCR.RESEARCH_GROUP_ID = RRRG.\"ID\" ").
                append(" where 1=1 ");
        if (StringUtil.notEmpty(text)) {
            hql.append(" and rrcr.name like '%").append(text).append("%' ");
        }
        hql.append("order by rrcr.id");
        SQLQuery q = getSession().createSQLQuery(hql.toString());
        q.addEntity(QueryResourceVO.class);
        setPageParamsForQuery(q, page);
        return (List<QueryResourceVO>) q.list();

    }
}
