package com.city.support.sys.log.dao;

import com.city.common.dao.BaseDao;
import com.city.common.pojo.Page;
import com.city.support.manage.timeFrame.entity.TimeFrame;
import com.city.support.sys.log.entity.SystemLog;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wgx on 2016/2/23.
 */
@Repository
public class SystemLogDao extends BaseDao<SystemLog> {
    /**
     * 查找所有系统日志
     *
     * @return
     */


    //按照排序获取所有系统日志
    public List<SystemLog> getByOrder() {
        String hql = "from SystemLog order by operateDate  desc";
        return queryByHQL(hql);
    }

    public List<SystemLog> getPageByOrder(Page page) {
        String hql = "from SystemLog order by operateDate desc";
        return queryWithPageByHQL(hql, page);
    }

    /**
     * 返回日志数量
     *
     * @return
     */
    public Integer selectLogCount() {
        return Integer.parseInt(String.valueOf(unqueryByHQL("select count(id) from SystemLog")));
    }
}

