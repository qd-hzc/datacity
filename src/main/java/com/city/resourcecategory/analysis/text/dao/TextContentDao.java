package com.city.resourcecategory.analysis.text.dao;

import com.city.common.dao.BaseDao;
import com.city.common.pojo.Page;
import com.city.common.util.ListUtil;
import com.city.common.util.StringUtil;
import com.city.resourcecategory.analysis.common.entity.QueryResourceVO;
import com.city.resourcecategory.analysis.text.entity.TextContent;
import com.city.resourcecategory.analysis.text.pojo.TimeSpan;
import com.city.support.dataSet.query.pojo.TimePojo;
import com.city.support.dataSet.query.pojo.TimeRangePojo;
import com.city.support.sys.user.entity.User;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wxl on 2016/3/15.
 */
@Repository
public class TextContentDao extends BaseDao<TextContent> {

    public void deleteById(Integer id) {
        String hql = "delete TextContent t where t.id = ?";
        Query q = getSession().createQuery(hql);
        q.setInteger(0, id);
        q.executeUpdate();
    }

    public void deleteByThemeId(Integer id) {
        String hql = "delete TextContent t where t.theme.id = ?";
        Query q = getSession().createQuery(hql);
        q.setInteger(0, id);
        q.executeUpdate();
    }

    public List<TextContent> queryByThemeId(User user, Integer themeId, String contentSortType, String name, Integer status) {
        StringBuilder sb = new StringBuilder("select new com.city.resourcecategory.analysis.text.entity.TextContent(tc.id,tc.name,tc.subTitle,tc.theme,tc.status,tc.type,tc.infos,tc.analysisDate,tc.sortIndex,tc.creator,tc.creatorName,tc.createTime,tc.updator,tc.updatorName,tc.updateTime) from TextContent tc where 1=1");
        if (name != null && !"".equals(name)) {
            sb.append(" and( tc.name like '%").append(name).append("%' or tc.infos like '%").append(name).append("%')");
        }
        if (user != null && user.getId() != 0) {
            sb.append(" and tc.creator=").append(user.getId());
        }
        if (status != null && status != 0) {
            sb.append(" and tc.status=").append(status);
        }
        if (themeId != null) {
            sb.append(" and tc.theme.id=").append(themeId);
        }
        sb.append(" order by ");
        if (contentSortType != null && !"".equals(contentSortType)) {
            sb.append("tc.").append(contentSortType).append(" desc");
        } else {
            sb.append("tc.sortIndex");
        }

        List<TextContent> list = queryByHQL(sb.toString());
        return list;
    }

    /**
     * 根据报告期数查询,
     *
     * @param themeId 分析主题
     * @param time    必须为报告期数类型,且不能为空
     * @param status  审核状态
     */
    public List<TextContent> queryByTime(Integer themeId, TimeRangePojo time, Integer status) {
        StringBuilder sb = new StringBuilder("from TextContent where status=").append(status);
        if (themeId != null) {
            sb.append(" and theme.id=").append(themeId);
        }
        TimeSpan timeSpan = null;
        if (time.getYear() != 0) {
            //设置时间
            timeSpan = new TimeSpan(time.getYear(), time.getPeriod());
            //继续拼sql
            sb.append(" and analysisDate < ?");
        }
        //根据时间排序排序
        sb.append(" order by analysisDate desc");
        //获取条数
        Page page = new Page();
        page.setStart(0);
        page.setLimit(time.getPeriodsSpan());
        //查询
        Query query = getSession().createQuery(sb.toString());
        if (timeSpan != null) {
            query.setDate(0, timeSpan.getEnd());
        }
        setPageParamsForQuery(query, page);
        return query.list();
    }

    /**
     * 根据时间范围查询,不包括报告期数
     *
     * @param themeId 分析主题
     * @param times   时间范围
     * @param status  审核状态
     */
    public List<TextContent> queryByTime(Integer themeId, List<TimePojo> times, String contentSortType, Integer status) {
        StringBuilder sb = new StringBuilder("from TextContent where status=").append(status);
        if (themeId != null) {
            sb.append(" and theme.id=").append(themeId);
        }
        List<TimeSpan> timeSpans = new ArrayList<>();
        if (ListUtil.notEmpty(times)) {//时间非空
            sb.append(" and (1!=1");
            for (TimePojo time : times) {
                TimeSpan timeSpan = new TimeSpan(time.getYear(), time.getPeriod());
                timeSpans.add(timeSpan);
                //设置时间
                sb.append(" or (analysisDate between ? and ?)");
            }
            sb.append(")");
        }
        //排序
        if (!StringUtil.trimNotEmpty(contentSortType)) {
            contentSortType = "sortIndex";
        }
        sb.append(" order by ").append(contentSortType);
        Query query = getSession().createQuery(sb.toString());
        if (ListUtil.notEmpty(timeSpans)) {
            int i = 0;
            for (TimeSpan timeSpan : timeSpans) {
                query.setDate(i++, timeSpan.getBegin());
                query.setDate(i++, timeSpan.getEnd());
            }
        }
        return query.list();
    }

    /**
     * 审核内容
     *
     * @param ids    要审核的内容
     * @param status 驳回还是通过审核
     */
    public void checkTextContent(String ids, Integer status) {
        StringBuilder sb = new StringBuilder("update TextContent set status=").append(status).append(" where id in (").append(ids).append(")");
        updateByHQL(sb.toString());
    }

    /**
     * 查询分析内容数目
     *
     * @param themeId
     * @param status
     * @return
     */
    public List queryCountByThemeId(Integer themeId, String status) {
        StringBuilder sb = new StringBuilder("select count(*) from TextContent where");
        sb.append(" theme.id=").append(themeId);
        if (status != null && !"".equals(status)) {
            sb.append(" and status in(").append(status).append(")");
        }
        return queryByHQL(sb.toString());
    }

    /**
     * 根据主键集合查询
     */
    public List<TextContent> queryByIds(String ids) {
        StringBuilder sb = new StringBuilder("from TextContent where 1=1 and id in (").append(ids).append(") order by sortIndex");
        return queryByHQL(sb.toString());
    }

    public List<TextContent> queryByName(String name, Integer themeId) {
        String hql = "from TextContent t where t.name ='" + name + "' and t.theme.id = '" + themeId + "'";
        return super.queryByHQL(hql);
    }

    /**/
    public List<TextContent> queryByNameAndId(String name, Integer themeId, Integer id) {
        String hql = "from TextContent t where t.name ='" + name + "' and t.theme.id = '" + themeId + "' and t.id <>" + id;
        return super.queryByHQL(hql);
    }

    /**
     * 搜索文字分析，返回匹配数量
     * <pre>
     *     根据文字分析名称，模糊搜索
     * </pre>
     *
     * @param text
     * @return
     * @author hzc
     * @createDate 2016-5-13
     */
    public int selectForSearchCount(String text) {
        String sql = "select count(id) from TextContent t where  1=1 ";
        if (StringUtil.notEmpty(text)) {
            sql = sql + " and t.name like '%" + text + "%'";
        }
        List tmps = queryByHQL(sql);
        if (tmps != null && tmps.size() > 0) {
            long count = (long) tmps.get(0);
            return (int) count;
        }
        return 0;
    }


    /**
     * 搜索文字分析，返回匹配的文字分析
     * <pre>
     *     模糊查询，匹配名称，分页
     * </pre>
     *
     * @param text
     * @param page
     * @return
     * @author hzc
     * @createDate 2016-5-13
     */
    public List selectForSearch(String text, Page page) {
        StringBuffer hql = new StringBuffer("SELECT RTC.ID, RTC.NAME, 5 AS TYPE, -1 AS period, '' AS comments, RTC.THEME_ID AS extraId, RTT.\"NAME\" as extraName, -1 AS departmentId, '' as departmentName,rownum as rn ");
        hql.append(" FROM RC_TEXT_CONTENT rtc LEFT JOIN RC_TEXT_THEME rtt ON RTC.THEME_ID = RTT.\"ID\" where 1=1 ");
        if (StringUtil.notEmpty(text)) {
            hql.append(" and rtc.name like '%").append(text).append("%'");
        }
        hql.append(" order by rtc.id");
        SQLQuery q = getSession().createSQLQuery(hql.toString());
        q.addEntity(QueryResourceVO.class);
        setPageParamsForQuery(q, page);
        return (List<QueryResourceVO>) q.list();
    }
}
