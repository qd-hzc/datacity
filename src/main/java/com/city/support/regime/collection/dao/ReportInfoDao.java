package com.city.support.regime.collection.dao;

import com.city.common.dao.BaseDao;
import com.city.common.pojo.Constant;
import com.city.common.pojo.Page;
import com.city.resourcecategory.themes.pojo.ReportVO;
import com.city.support.regime.collection.entity.ReportInfo;
import com.city.support.regime.report.entity.ReportTemplate;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by wgx on 2016/1/28.
 */
@Repository
public class ReportInfoDao extends BaseDao<ReportInfo> {

    /**
     * 根据条件查询
     *
     * @param page
     * @param rptTmpIds 模板id
     * @return
     */
    public List<ReportInfo> getReportInfosByCondition(Page page, List<ReportTemplate> reportTemplateList, Integer groupId, LinkedList<Integer> rptTmpIds, String name, String depIds, Integer rptStatus, String periods, Integer type, Integer beginYear, Integer endYear) {
        StringBuilder sb = new StringBuilder("from ReportInfo where 1=1");
        sb.append(" and RPT_TMP_ID in (-1");
        for (Integer rptTmpId : rptTmpIds) {
            sb.append(",");
            sb.append(rptTmpId.toString());
        }
        sb.append(")");
        if (groupId != null) {
            sb.append(" and RPT_TMP_ID in (-1");
            if (reportTemplateList != null && reportTemplateList.size() > 0) {
                for (ReportTemplate reportTemplate : reportTemplateList) {
                    sb.append(",");
                    sb.append(reportTemplate.getId().toString());
                }
            }
            sb.append(")");
        }
        if (name != null && name.trim().length() > 0) {
            sb.append(" and name like '%").append(name).append("%'");
        }
        if (depIds != null) {
            sb.append(" and dptId in (").append(depIds).append(")");
        }
        if (periods != null && periods.trim().length() > 0) {
            sb.append(" and period in (").append(periods).append(")");
        }
        if (rptStatus != null && !rptStatus.equals(Constant.RPT_STATUS.ALL) && !rptStatus.equals(Constant.RPT_STATUS.REVIEW_ALL)) {
            sb.append(" and rptStatus=").append(rptStatus);
        } else if (rptStatus.equals(Constant.RPT_STATUS.REVIEW_ALL)) {
            sb.append(" and rptStatus in (").append(Constant.RPT_STATUS.PASS + "," + Constant.RPT_STATUS.WAITING_PASS).append(")");
        }
        if (type != null) {
            sb.append(" and type=").append(type);
        }
        if (beginYear != null) {
            sb.append(" and year>=").append(beginYear);
        }
        if (endYear != null && endYear != 0) {//endYear=0时表示一直有效
            sb.append(" and year<=").append(endYear);
        }
        sb.append(" order by RPT_TMP_ID desc,YEAR desc,Month desc");
        return queryWithPageByHQL(sb.toString(), page);
    }

    /**
     * 根据模板id查询
     *
     * @param rptTmpId 模板id
     * @return
     */
    public List<ReportInfo> getReportInfosByRptTmpId(Integer rptTmpId) {
        StringBuilder sb = new StringBuilder("from ReportInfo where 1=1");
        if (rptTmpId != null) {
            sb.append(" and RPT_TMP_ID =").append(rptTmpId);
        }
        sb.append(" order by YEAR desc,Month desc");
        return queryByHQL(sb.toString());
    }

    /**
     * 根据报表id查询
     *
     * @param ids
     */
    public List<ReportInfo> queryByIds(String ids) {
        StringBuilder sb = new StringBuilder("from ReportInfo where 1=1");
        if (ids != null && ids.trim().length() > 0) {
            sb.append(" and id in( ").append(ids).append(" )");
        }
        sb.append(" order by YEAR desc,Month desc");
        return queryByHQL(sb.toString());
    }

    /**
     * 根据报表id和模板id查询
     *
     * @param rptTmpIds
     * @param rptInfoIds
     * @return
     */
    public List<ReportInfo> queryByRptInfoAndTmpIds(String rptTmpIds, String rptInfoIds) {
        StringBuilder sb = new StringBuilder("from ReportInfo where 1=1");
        if (rptTmpIds != null && rptTmpIds.trim().length() > 0) {
            sb.append(" and RPT_TMP_ID in( ").append(rptTmpIds).append(" )");
        }
        if (rptInfoIds != null && rptInfoIds.trim().length() > 0) {
            sb.append(" and ID in( ").append(rptInfoIds).append(" )");
        }
        sb.append(" order by YEAR desc,Month desc");
        return queryByHQL(sb.toString());
    }

    public List<ReportInfo> queryByTimeAndTmpId(Integer year, Integer month, Integer rptTmpId) {
        StringBuilder sb = new StringBuilder("from ReportInfo where 1=1");
        if (rptTmpId != null) {
            sb.append(" and RPT_TMP_ID =").append(rptTmpId);
        }
        if (year != null) {
            sb.append(" and YEAR =").append(year);
        }
        if (month != null) {
            sb.append(" and MONTH =").append(month);
        }
        return queryByHQL(sb.toString());
    }

    /**
     * 查询数目
     *
     * @param rptTmpIds 模板id
     * @return
     */
    public int getReportInfoCountByCondition(List<ReportTemplate> reportTemplateList, Integer groupId, LinkedList<Integer> rptTmpIds, String name, String depIds, Integer rptStatus, String periods, Integer type, Integer beginYear, Integer endYear) {
        StringBuilder sb = new StringBuilder("select count(*) from ReportInfo where 1=1");
        sb.append(" and RPT_TMP_ID in (-1");
        for (Integer rptTmpId : rptTmpIds) {
            sb.append(",");
            sb.append(rptTmpId.toString());
        }
        sb.append(")");
        if (groupId != null) {
            sb.append(" and RPT_TMP_ID in (-1");
            if (reportTemplateList != null && reportTemplateList.size() > 0) {
                for (ReportTemplate reportTemplate : reportTemplateList) {
                    sb.append(",");
                    sb.append(reportTemplate.getId().toString());
                }
            }
            sb.append(")");
        }
        if (name != null && name.trim().length() > 0) {
            sb.append(" and name like '%").append(name).append("%'");
        }
        if (depIds != null) {
            sb.append(" and dptId in (").append(depIds).append(")");
        }
        if (periods != null && periods.trim().length() > 0) {
            sb.append(" and period in (").append(periods).append(")");
        }
        if (rptStatus != null && !rptStatus.equals(Constant.RPT_STATUS.ALL) && !rptStatus.equals(Constant.RPT_STATUS.REVIEW_ALL)) {
            sb.append(" and rptStatus=").append(rptStatus);
        } else if (rptStatus.equals(Constant.RPT_STATUS.REVIEW_ALL)) {
            sb.append(" and rptStatus in (").append(Constant.RPT_STATUS.PASS + "," + Constant.RPT_STATUS.WAITING_PASS).append(")");
        }
        if (type != null) {
            sb.append(" and type=").append(type);
        }
        if (beginYear != null) {
            sb.append(" and year>=").append(beginYear);
        }
        if (endYear != null && endYear != 0) {//endYear=0时表示一直有效
            sb.append(" and year<=").append(endYear);
        }
        List RptInfos = queryByHQL(sb.toString());
        if (RptInfos != null && RptInfos.size() > 0) {
            long count = (long) RptInfos.get(0);
            return (int) count;
        }
        return 0;
    }

    /**
     * @param time
     * @param rptTmpId
     * @return
     */
    public int getRptInfoCount(String time, Integer rptTmpId) {
        StringBuilder sb = new StringBuilder("select count(*) from ReportInfo where 1=1");
        if (time != null && time.trim().length() > 0) {
            sb.append(" and TIME ='").append(time).append("'");
        }
        if (rptTmpId != null) {
            sb.append(" and RPT_TMP_ID =").append(rptTmpId);
        }
        List RptInfos = queryByHQL(sb.toString());
        if (RptInfos != null && RptInfos.size() > 0) {
            long count = (long) RptInfos.get(0);
            return (int) count;
        }
        return 0;
    }

    public void removeRptInfo(String tmpIds) {
        String hql = "delete from ReportInfo t where t.tmpId in (" + tmpIds + ")";
        updateByHQL(hql);
    }

    public void removeById(Integer id, String rptTmpIds) {
        String hql = "delete from ReportInfo t where t.id = " + id + " and t.rptStatus not in (" + Constant.RPT_STATUS.PASS + "," + Constant.RPT_STATUS.WAITING_PASS + ") and t.tmpId in (" + rptTmpIds + ")";
        updateByHQL(hql);
    }

    /**
     * 返回综合表相关报告期
     * <pre>
     *     根据综合表id，和报告期状态，查询该综合表的所有符合要求的报告期
     * </pre>
     *
     * @param rptTmpId 综合表id
     * @param status   报告期状态
     * @return
     * @author hzc
     * @createDate 2016-3-28
     */
    public List<ReportInfo> selectInfosByTmpIdAndStatus(Integer rptTmpId, String status) {
        return queryByHQL("from ReportInfo where tmpId = " + rptTmpId + " and rptStatus in (" + status + ") order by YEAR desc,Month desc");
    }

    /**
     * 返回符合条件的综合表报告期集合
     * <pre>
     *     根据综合表id，报告期状态，报告期时间，查询综合表对应报告期
     * </pre>
     *
     * @param vo 查询条件：
     *           {@code
     *           //综合表id
     *           id:1,
     *           //状态：0：全部；1:待填报；2：草稿；3：待审；4：已审；5：已驳回
     *           //使用逗号分隔，可为空
     *           status:2,3,4,
     *           //报告期年，可为空
     *           year:2016,
     *           //报告期期度：年，半年，季，月，可为空
     *           m:3,
     *           //综合表频度：1：年报，2：半年报，3：季报，4：月报，可为空
     *           frequency:3
     *           }
     * @return 返回符合查询条件的所有综合表的报告期
     * @author hzc
     * @createDate 2016-3-30
     */
    public List<ReportInfo> selectInfoByTime(ReportVO vo) {
        StringBuffer sql = new StringBuffer("from ReportInfo where 1=1 ");

        sql.append(" and tmpId =").append(vo.getId());

        String status = vo.getStatus();
        if (!StringUtils.isEmpty(status)) {
            sql.append(" and rptStatus in (").append(status).append(")");
        }

        Integer year = vo.getYear();
        if (null != year && year > 0) {
            sql.append(" and year =").append(year);
        }

        Integer m = vo.getM();
        if (null != m && m > 0) {
            sql.append(" and month = ").append(m);
        }

        Integer frequency = vo.getFrequency();
        if (null != frequency && frequency > 0) {
            sql.append(" and period = ").append(frequency);
        }

        sql.append("  order by year, month desc");

        return queryByHQL(sql.toString());
    }

    /**
     * 根据名字和报告期查id
     *
     * @param name
     * @param year
     * @param month
     * @return
     */
    public Integer getReportId(String name, Integer year, Integer month) {
        String hql = "";
        if (name != null && year != null && month != null) {
            hql = "from ReportInfo where name='" + name + "'and year=" + year + " and month=" + month;

        }
        List<ReportInfo> result = this.queryByHQL(hql);
        if (result.size() > 0)
            return result.get(0).getId();
        else
            return 0;


    }

}
