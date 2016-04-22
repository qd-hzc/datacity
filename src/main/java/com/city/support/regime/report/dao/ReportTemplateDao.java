package com.city.support.regime.report.dao;

import com.city.common.dao.BaseDao;
import com.city.common.pojo.Page;
import com.city.support.regime.collection.entity.ReportInfo;
import com.city.support.regime.report.entity.ReportTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wxl on 2016/1/14 0014.
 */
@Repository
public class ReportTemplateDao extends BaseDao<ReportTemplate> {

    /**
     * 根据条件查询
     *
     * @param name      表名,可为空
     * @param depIds    部门,可多选,可为空
     * @param periods   报送周期,可多选,可为空
     * @param rptType   报表模板类型,可为空
     * @param beginYear 开始年,可为空
     * @param endYear   结束年,可为空
     */
    public List<ReportTemplate> getRptTmpsByCondition(Page page, String name, String depIds, String periods, Integer rptType, Integer beginYear, Integer endYear) {
        StringBuilder sb = new StringBuilder("from ReportTemplate where 1=1");
        if (name != null && name.trim().length() > 0) {
            sb.append(" and name like '%").append(name).append("%'");
        }
        if (depIds != null) {
            sb.append(" and department.id in (").append(depIds).append(")");
        }
        if (periods != null && periods.trim().length() > 0) {
            sb.append(" and period in (").append(periods).append(")");
        }
        if (rptType != null) {
            sb.append(" and rptType=").append(rptType);
        }
        if (beginYear != null) {
            sb.append(" and beginYear>=").append(beginYear);
        }
        if (endYear != null && endYear != 0) {//endYear=0时表示一直有效
            sb.append(" and endYear<=").append(endYear);
            sb.append(" and beginYear<=").append(endYear);
        }
        sb.append(" order by id desc");
        return queryWithPageByHQL(sb.toString(), page);
    }

    /**
     * 根据条件查询数目
     *
     * @param name
     * @param depIds
     * @param periods
     * @param rptType
     * @param beginYear
     * @param endYear
     * @return
     */
    public int getTmpCountByCondition(String name, String depIds, String periods, Integer rptType, Integer beginYear, Integer endYear) {
        StringBuilder sb = new StringBuilder("select count(*) from ReportTemplate where 1=1");
        if (name != null && name.trim().length() > 0) {
            sb.append(" and name like '%").append(name).append("%'");
        }
        if (depIds != null) {
            sb.append(" and department.id in (").append(depIds).append(")");
        }
        if (periods != null && periods.trim().length() > 0) {
            sb.append(" and period in (").append(periods).append(")");
        }
        if (rptType != null) {
            sb.append(" and rptType=").append(rptType);
        }
        if (beginYear != null) {
            sb.append(" and beginYear>=").append(beginYear);
        }
        if (endYear != null && endYear != 0) {//endYear=0时表示一直有效
            sb.append(" and endYear<=").append(endYear);
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
     * 删除报表模板
     */
    public void removeRptTmps(String ids) {
        String hql = "delete from ReportTemplate where id in (" + ids + ")";
        updateByHQL(hql);
    }

    /**
     * 根据模板id查询报表模板
     *
     * @param rptTmpIds
     * @return
     */
    public List<ReportTemplate> queryByIds(String rptTmpIds) {
        StringBuilder sb = new StringBuilder("from ReportTemplate where 1=1");
        if (rptTmpIds != null && rptTmpIds.trim().length() > 0) {
            sb.append(" and id in ( ").append(rptTmpIds).append(" )");
        }
        return queryByHQL(sb.toString());
    }

    /**
     * 根据分组id查询报表模板
     *
     * @param rptGroupIds
     * @return
     */
    public List<ReportTemplate> queryByGroupIds(String rptGroupIds) {
        StringBuilder sb = new StringBuilder("from ReportTemplate where 1=1");
        if (rptGroupIds != null && rptGroupIds.trim().length() > 0) {
            sb.append(" and groupId in ( ").append(rptGroupIds).append(" )");
        }
        return queryByHQL(sb.toString());
    }

    /**
     * 根据指标id查询指标所属部门id
     *
     * @param itemId
     * @return
     */
    public List queryDepId(Integer itemId) {
        StringBuilder sb = new StringBuilder();
        sb.append("select distinct department.id from ReportTemplate where 1=1");
        if (itemId != null) {
            sb.append(" and id in (select distinct r.tmpId from ReportTemplateBarInfo r where r.dataValue = ").append(itemId).append(")");
        }
        return queryByHQL(sb.toString());
    }

    /**
     * 根据指标id查询报表
     *
     * @param itemId
     * @return
     */
    public List<ReportTemplate> queryByItemId(Integer itemId) {
        StringBuilder sb = new StringBuilder("from ReportTemplate where 1=1");
        if (itemId != null) {
            sb.append(" and id in (select distinct r.tmpId from ReportTemplateBarInfo r where r.dataValue = ").append(itemId).append(")");
        }
        return queryByHQL(sb.toString());
    }

    /**
     * 根据条件查询
     *
     * @param name 表名,可为空
     */
    public List<ReportTemplate> getRptTmpsByName(String name) {
        StringBuilder sb = new StringBuilder("from ReportTemplate where 1=1");
        if (name != null && name.trim().length() > 0) {
            sb.append(" and name like '%").append(name).append("%'");
        }
        sb.append(" order by id desc");
        return queryByHQL(sb.toString());
    }

    /**
     * 查询部门下报表
     *
     * @param depId
     * @return
     */
    public List<ReportTemplate> getRptTmpsByDep(Integer depId) {
        String hql = "from ReportTemplate r where r.department.id =" + depId;

        return queryByHQL(hql);
    }

    /**
     * 除此部门外所有部门下的模板表
     *
     * @param depId
     * @return
     */
    public List<ReportTemplate> getRptTmpsExpectDep(Integer depId) {
        String hql = "from ReportTemplate r where r.department.id !=" + depId;

        return queryByHQL(hql);
    }

    /**
     * 返回所有报表模板
     * <pre>
     *     根据ids，查询所有报表模板
     * </pre>
     *
     * @param id
     * @return
     * @author crx
     * @createDate 2016-3-14
     */
    public List<ReportTemplate> selectAllByIds(String id) {
        return queryByHQL("from ReportTemplate where id in (" + id + ")");
    }

    /**
     * 返回报表
     * <pre>
     *     根据条件查询报表
     * </pre>
     *
     * @param page
     * @param groups 分组id集合
     * @param name   查询名字
     * @param status 状态
     * @return
     * @author hzc
     * @createDate 2016-4-15
     */
    public List<ReportTemplate> getRptTmpsByCondition(Page page, String groups, String name, Integer status) {

        StringBuilder sb = new StringBuilder("from ReportTemplate where groupId in (").append(groups).append(")");
        if (name != null && name.trim().length() > 0) {
            sb.append(" and name like '%").append(name).append("%'");
        }
        if (status != null) {
            sb.append(" and status=").append(status);
        }
        sb.append(" order by id desc");

        return queryWithPageByHQL(sb.toString(), page);
    }

    /**
     * 返回报表数量
     * <pre>
     *     根据条件查询报表，返回报表数量
     * </pre>
     *
     * @param name   查询名字
     * @param groups 分组id
     * @param status 状态
     * @return
     * @author hzc
     * @createDate 2016-4-15
     */
    public Integer getTmpCountByCondition(String name, String groups, Integer status) {
        StringBuilder sb = new StringBuilder("select count(*) from ReportTemplate where 1=1 and groupId in (").append(groups).append(")");
        if (name != null && name.trim().length() > 0) {
            sb.append(" and name like '%").append(name).append("%'");
        }
        if (status != null) {
            sb.append(" and status=").append(status);
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
     * 更新报表的分组id
     *
     * @param reportIds
     * @param groupId
     * @return
     * @author hzc
     * @createDate 2016-4-18
     */
    public void updateReportsGroupId(String reportIds, Integer groupId) {
        updateByHQL("update ReportTemplate set groupId = " + groupId + " where id in (" + reportIds + ")");
    }
}
