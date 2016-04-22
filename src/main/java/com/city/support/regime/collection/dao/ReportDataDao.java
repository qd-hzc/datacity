package com.city.support.regime.collection.dao;

import com.city.common.dao.BaseDao;
import com.city.common.pojo.Constant;
import com.city.support.regime.collection.entity.ReportData;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wgx on 2016/2/15.
 */
@Repository
public class ReportDataDao extends BaseDao<ReportData> {
    /**
     * 根据报表id查询
     * @param rptId
     * @return
     */
    public List<ReportData> queryByRptId(Integer rptId) {
        StringBuilder sb = new StringBuilder("from ReportData where 1=1");
        if (rptId != null) {
            sb.append(" and RPTID =").append(rptId);
        }
        return queryByHQL(sb.toString());
    }
    /**
     * 根据模板id查询
     * @param tmpIds
     * @return
     */
    public List<ReportData> queryByTmpId(String tmpIds) {
        StringBuilder sb = new StringBuilder("from ReportData where 1=1");
        if (tmpIds != null) {
            sb.append(" and rptTmpId in (").append(tmpIds).append(")");
        }
        return queryByHQL(sb.toString());
    }
    public void removeRptDatasByTmpIds(String tmpIds) {
        String hql = "delete from ReportData t where t.rptTmpId in (" + tmpIds + ")";
        updateByHQL(hql);
    }
    public void removeRptDatasByInfoId(Integer id,String rptTmpIds) {
        String hql = "delete from ReportData t where t.reportDataId.rptId = " + id +" and t.dataStatus = "+ Constant.DATA_STATUS.UNUSABLE+" and t.rptTmpId in (" + rptTmpIds + ")";
        updateByHQL(hql);
    }

    public void removeRptDatasByRptIds(String rptIds) {
        String hql = "delete from ReportData t where t.reportDataId.rptId in (" + rptIds + ")";
        updateByHQL(hql);
    }
}
