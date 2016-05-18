package com.city.resourcecategory.analysis.common.dao;

import com.city.common.dao.BaseDao;
import com.city.common.pojo.Page;
import com.city.common.util.StringUtil;
import com.city.resourcecategory.analysis.common.entity.QueryResourceVO;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by HZC on 2016/5/16.
 */
@Repository
public class ResourceDao extends BaseDao<QueryResourceVO> {

    /**
     * 返回所有查询的资源
     * <pre>
     *     查询综合表，过滤综合表的用户权限，
     *     其他资源暂时不过滤权限，
     *     模糊搜索，匹配资源名称，分页
     * </pre>
     *
     * @param permissionIds 综合表权限：有可读可写权限的综合表id
     * @param text          搜索名称
     * @param page          分页
     * @return
     * @author hzc
     * @createDate 2016-5-16
     */
    public List<QueryResourceVO> getAllResource(String permissionIds, String text, Page page) {
        StringBuffer sql = new StringBuffer("SELECT id,name,type,period,comments,extraId,extraName,departmentId,departmentName,rn  FROM ( select t.id,t.name,t.type,t.period,t.comments,t.extraId,t.extraName,t.departmentId,t.departmentName,rownum as rn from( select id,name,type,period,comments,extraId,extraName,departmentId,departmentName from (");
        if (StringUtil.notEmpty(permissionIds)) {
            sql.append("SELECT srrt.ID, srrt.NAME, 2 AS TYPE, srrt.PERIOD, srrt.RPT_COMMENTS AS comments, srrt.group_id as extraId, srrg.name as extraName, srrt.DEP_ID AS departmentId, ssd.DEP_NAME as departmentName FROM ");
            sql.append(" SPT_RGM_RPT_TMP srrt left join SPT_SYS_DEPART ssd on srrt.DEP_ID = ssd.id LEFT JOIN spt_rgm_rpt_group srrg on srrg.id = srrt.group_id");
            sql.append(" where srrt.id in (").append(permissionIds).append(") and srrt.name like '%").append(text).append("%' UNION ALL ");
        }
        sql.append("SELECT RRCR.ID, RRCR.NAME, 3 AS TYPE, RRCR.PERIOD, RRCR.comments, RRCR.RESEARCH_GROUP_ID AS extraId, RRRG.\"NAME\" as extraName, -1 AS departmentId, '' as departmentName");
        sql.append(" FROM RC_REPORT_CUSTOM_RESEARCH rrcr LEFT JOIN RC_REPORT_RESEARCH_GROUP rrrg ON RRCR.RESEARCH_GROUP_ID = RRRG.\"ID\"");
        sql.append(" where rrcr.name like '%").append(text).append("%'");
        sql.append(" UNION ALL SELECT RAC.ID, RAC.title AS NAME, 4 AS TYPE, RAC.PERIOD_TYPE AS period, '' AS comments, RAC.GROUP_ID AS extraId, RACG.\"NAME\" as extraName, -1 AS departmentId, '' as departmentName");
        sql.append(" FROM RC_ANALYSIS_CHARTBASE rac LEFT JOIN RC_ANALYSIS_CHARTGROUP racg ON RAC.\"GROUP_ID\" = RACG.\"ID\"");
        sql.append(" where rac.title like '%").append(text).append("%'");
        sql.append(" UNION ALL SELECT RTC.ID, RTC.NAME, 5 AS TYPE, -1 AS period, '' AS comments, RTC.THEME_ID AS extraId, RTT.\"NAME\" as extraName, -1 AS departmentId, '' as departmentName");
        sql.append(" FROM RC_TEXT_CONTENT rtc LEFT JOIN RC_TEXT_THEME rtt ON RTC.THEME_ID = RTT.\"ID\"");
        sql.append(" where rtc.name like '%").append(text).append("%'");

        sql.append(" ) ORDER BY NAME ) t ) where rn >").append((page.getPage() - 1) * page.getLimit()).append(" and rn <=").append(page.getPage() * page.getLimit());

        return queryBySql(sql.toString(), QueryResourceVO.class);
    }

    /**
     * 返回所有查询的资源的数量
     * <pre>
     *     查询综合表，过滤综合表的用户权限，
     *     其他资源暂时不过滤权限，
     *     模糊搜索，匹配资源名称，分页
     * </pre>
     *
     * @param permissionIds 综合表权限：有可读可写权限的综合表id
     * @param text          搜索名称
     * @return
     * @author hzc
     * @createDate 2016-5-16
     */
    public int getAllResourceCount(String permissionIds, String text) {
        StringBuffer sql = new StringBuffer("select count(*) from (SELECT id FROM ( ");
        if (StringUtil.notEmpty(permissionIds)) {
            sql.append("SELECT srrt.ID,srrt.name FROM ");
            sql.append(" SPT_RGM_RPT_TMP srrt ");
            sql.append(" where srrt.id in (").append(permissionIds).append(") and srrt.name like '%")
                    .append(text).append("%' UNION ALL ");
        }
        sql.append("SELECT RRCR.ID,rrcr.name ");
        sql.append(" FROM RC_REPORT_CUSTOM_RESEARCH rrcr ");
        sql.append(" where rrcr.name like '%").append(text).append("%'");
        sql.append(" UNION ALL SELECT RAC.ID,rac.title ");
        sql.append(" FROM RC_ANALYSIS_CHARTBASE rac ");
        sql.append(" where rac.title like '%").append(text).append("%'");
        sql.append(" UNION ALL SELECT RTC.ID,rtc.name");
        sql.append(" FROM RC_TEXT_CONTENT rtc ");
        sql.append(" where rtc.name like '%").append(text).append("%'");

        sql.append(" ) ORDER BY NAME ) ");

        System.out.println(sql.toString());
        List list = queryBySql(sql.toString());
        if (null != list && list.size() > 0) {
            BigDecimal l = (BigDecimal) list.get(0);
            return l.intValue();
        }
        return 0;
    }
}
