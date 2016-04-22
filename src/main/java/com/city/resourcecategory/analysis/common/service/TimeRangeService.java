package com.city.resourcecategory.analysis.common.service;

import com.city.common.pojo.Constant;
import com.city.resourcecategory.analysis.common.dao.TimeRangeDao;
import com.city.resourcecategory.analysis.common.entity.TimeRangeEntity;
import com.city.support.sys.user.entity.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import oracle.jdbc.driver.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by wxl on 2016/3/1.
 */
@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
public class TimeRangeService {
    @Autowired
    private TimeRangeDao timeRangeDao;

    /**
     * 查询时间范围
     *
     * @param foreignType 外键类型
     * @param foreignId   外键id
     */
    public List<TimeRangeEntity> queryTimeRange(Integer foreignType, Integer foreignId) {
        return timeRangeDao.queryTimeRange(foreignType, foreignId);
    }

    /**
     * 保存时间范围
     *
     * @param timeRange   [TimeRangeEntity]的json串
     * @param foreignType 外键类型
     * @param foreignId   外键id
     * @param user        当前用户
     */
    public void saveTimeRange(String timeRange, Integer foreignType, Integer foreignId, User user) {
        Date date = new Date();
        //获取实体
        List<TimeRangeEntity> ranges = new Gson().fromJson(timeRange, new TypeToken<ArrayList<TimeRangeEntity>>() {
        }.getType());
        if (ranges != null && ranges.size() > 0) {
            //清空
            timeRangeDao.clearTimeRange(foreignType, foreignId);
            //循环赋值并保存
            for (TimeRangeEntity range : ranges) {
                range.setForeignId(foreignId);
                range.setForeignType(foreignType);
                range.setCreateDate(date);
                range.setCreatorId(user.getId());
                range.setStatus(1);
                timeRangeDao.insert(range, true);
            }
        }
    }

    /**
     * 根据自定义查询id删除时间范围
     *
     * @param id
     * @author hzc
     * @createDate 2016-3-7
     */
    public void deleteByResearchId(Integer id) {
        timeRangeDao.clearTimeRange(Constant.TIMERANGE.TYPE_REPORT, id);
    }
}
