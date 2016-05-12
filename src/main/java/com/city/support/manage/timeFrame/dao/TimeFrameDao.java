package com.city.support.manage.timeFrame.dao;

import com.city.common.dao.BaseDao;
import com.city.common.pojo.Page;
import com.city.support.manage.timeFrame.entity.TimeFrame;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by zhoutao on 2015/12/30.
 * 时间框架 操作DAO
 */
@Repository
public class TimeFrameDao extends BaseDao<TimeFrame> {

    //增加
    public void insert(TimeFrame tf) {
        super.insert(tf, true);
    }

    //删除
    public void delete(TimeFrame tf) {
        super.delete(tf, true);
    }

    /**
     * 使用sql批量删除
     *
     * @param ids id字符串 格式为 1,2,3
     */
    public void batchDelete(String ids) {
        if (!StringUtils.isEmpty(ids)) {
            String sql = "delete from SPT_MGR_TIME_FRAME where id in (" + ids + ")";
            super.getSession().createSQLQuery(sql).executeUpdate();
        }
    }

    //修改
    public void update(TimeFrame tf) {
        super.update(tf, true);
    }

    //获取所有时间框架
    public List<TimeFrame> getAll() {
        return super.queryAll();
    }

    //按照排序获取所有时间框架
    public List<TimeFrame> getByOrder() {
        String hql = "from TimeFrame order by orders";
        return queryByHQL(hql);
    }

    public List<TimeFrame> getPageByOrder(Page page) {
        String hql = "from TimeFrame order by orders";
        return queryWithPageByHQL(hql, page);
    }

    /**
     * 根据id获取时间框架信息
     *
     * @param id id
     * @return TimeFrame 时间框架信息
     */
    public TimeFrame getById(Integer id) {
        TimeFrame tf = super.queryById(id);
        return tf;
    }

    /**
     * 根据名称获取时间框架
     *
     * @param name
     * @return
     */
    public List<TimeFrame> getByName(String name) {
        String hql = "from TimeFrame tf where tf.name like '%" + name + "%'";
        return super.queryByHQL(hql);
    }

    /**
     * 根据名称获取时间框架
     *
     * @param name
     * @return
     */
    public List<TimeFrame> getByAllName(String name) {
        String hql = "from TimeFrame tf where tf.name ='" + name + "'";
        return super.queryByHQL(hql);
    }

    /**
     * 返回所有时间框架
     * <pre>
     *     根据时间框架id集合，返回所有时间框架
     * </pre>
     *
     * @param ids id集合
     * @return
     * @author hzc
     * @createDate 2016-4-5
     */
    public List<TimeFrame> selectByIds(String ids) {
        return queryByHQL("from TimeFrame where id in(" + ids + ")");
    }

    public List<TimeFrame> getByNameAndId(String name, Integer id) {
        String hql = "from TimeFrame tf where tf.name ='" + name + "' and tf.id <>" + id;
        return super.queryByHQL(hql);
    }
}
