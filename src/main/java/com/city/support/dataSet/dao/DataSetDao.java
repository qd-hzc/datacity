package com.city.support.dataSet.dao;

import com.city.common.dao.BaseDao;
import com.city.common.pojo.Page;
import com.city.support.dataSet.entity.DataSet;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wxl on 2016/2/22.
 */
@Repository
public class DataSetDao extends BaseDao<DataSet> {

    /**
     * 根据名字分页查询
     *
     * @param name 查询的名字
     * @param page 分页
     */
    public List<DataSet> queryByName(String name, Page page) {
        return queryWithPageByHQL(new StringBuilder("from DataSet where name like '%").append(name).append("%' or comments like '%")
                .append(name).append("%' order by id desc").toString(), page);
    }

    /**
     * 根据名字全部匹配
     *
     * @param name
     * @return
     */
    public List<DataSet> queryByName(String name) {
        return queryByHQL("from DataSet where name=" + name);
    }

    /**
     * 查询总数
     */
    public List queryTotal(String name) {
        return queryByHQL(new StringBuilder("select count(*) from DataSet where name like '%").append(name).append("%'").toString());
    }

    /**
     * 删除数据集
     */
    public void removeDataSets(String ids) {
        updateByHQL(new StringBuilder("delete from DataSet where id in (").append(ids).append(")").toString());
    }

    /**
     * 查找所有数据集
     *
     * @return
     * @author crx
     * @createDate 2016-3-22
     */
    public Object selectAll() {
        return queryByHQL("from DataSet");
    }
}
