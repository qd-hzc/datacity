package com.city.resourcecategory.analysis.common.dao;

import com.city.common.dao.BaseDao;
import com.city.resourcecategory.analysis.common.entity.TimeRangeEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wxl on 2016/3/1.
 */
@Repository
public class TimeRangeDao extends BaseDao<TimeRangeEntity> {

    /**
     * 根据外键查询
     *
     * @param foreignType 外键类型
     * @param foreignId   外键id
     */
    public List<TimeRangeEntity> queryTimeRange(Integer foreignType, Integer foreignId) {
        return queryWithParamsByHQL("from TimeRangeEntity where foreignType=? and foreignId=?", new Object[]{foreignType, foreignId});
    }

    /**
     * 根据外键清空
     *
     * @param foreignType 外键类型
     * @param foreignId   外键id
     */
    public void clearTimeRange(Integer foreignType, Integer foreignId) {
        updateByHQL("delete from TimeRangeEntity where foreignType=" + foreignType + " and foreignId=" + foreignId);
    }
}
