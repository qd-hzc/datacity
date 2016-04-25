package com.city.resourcecategory.analysis.text.dao;

import com.city.common.dao.BaseDao;
import com.city.common.pojo.Page;
import com.city.common.util.ListUtil;
import com.city.common.util.StringUtil;
import com.city.resourcecategory.analysis.text.entity.TextContent;
import com.city.resourcecategory.analysis.text.pojo.TimeSpan;
import com.city.support.dataSet.query.pojo.TimePojo;
import com.city.support.dataSet.query.pojo.TimeRangePojo;
import com.city.support.sys.user.entity.User;
import org.hibernate.Query;
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

    public List<TextContent> queryByThemeId(User user,Integer themeId, String contentSortType, String name, Integer status) {
        StringBuilder sb = new StringBuilder("from TextContent where 1=1");
        if (name != null && !"".equals(name)) {
            sb.append(" and( name like '%").append(name).append("%' or infos like '%").append(name).append("%')");
        }
        if (user != null && user.getId() != 0) {
            sb.append(" and creator=").append(user.getId());
        }
        if (status != null && status != 0) {
            sb.append(" and status=").append(status);
        }
        if (themeId != null) {
            sb.append(" and theme.id=").append(themeId);
        }
        sb.append(" order by ");
        if (contentSortType != null && !"".equals(contentSortType)) {
            sb.append(contentSortType);
        } else {
            sb.append("sortIndex");
        }
        return queryByHQL(sb.toString());
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
}
