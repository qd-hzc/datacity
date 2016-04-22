package com.city.support.dataSet.query.dao;

import com.city.common.dao.BaseDao;
import com.city.support.dataSet.query.pojo.TimePojo;
import com.city.support.regime.collection.entity.ReportData;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wxl on 2016/3/5.
 */
@Repository
public class QueryRptDao extends BaseDao<ReportData> {

    /**
     * 根据sql从数据表中查询
     */
    public List<ReportData> queryRptData(String sql) {
        return queryBySql(sql, ReportData.class);
    }

    /**
     * 根据sql从数据表中查询
     */
    public List queryRptTime(String sql) {
        return queryBySql(sql);
    }
}
