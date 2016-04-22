package com.city.support.manage.metadata.dao;

import com.city.common.dao.BaseDao;
import com.city.support.manage.metadata.entity.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wgx on 2016/1/13.
 */
@Repository
public class MetadataTypeDao extends BaseDao<MetadataType> {

    /**
     * 获取所有的系统元数据类型
     *
     * @return
     */
    public List<MetadataType> getAll() {
        return super.queryAll();
    }

    /**
     * 增加系统元数据类型
     *
     * @param metadataType
     */
    public void insert(MetadataType metadataType) {
        super.insert(metadataType, true);
    }

    /**
     * 修改系统元数据类型
     *
     * @param metadataType
     */
    public void update(MetadataType metadataType) {
        super.update(metadataType, true);
    }

    /**
     * 删除系统元数据类型
     *
     * @param metadataType
     */
    public void delete(MetadataType metadataType) {
        super.update(metadataType, true);
    }
    /**
     * 使用sql批量删除
     *
     * @param ids id字符串 格式为 1,2,3
     */
    public void batchDelete(String ids) {
        if (!StringUtils.isEmpty(ids)) {
            String sql = "delete from SPT_MGR_METADATA_TYPE where id in (" + ids + ")";
            super.getSession().createSQLQuery(sql).executeUpdate();
        }
    }

    /**
     * 根据id查询元数据类型
     * @param id  id
     * @return
     */
    public MetadataType getById(Integer id){
        return super.queryById(id);
    }
    /**
     * 根据名称查询元数据类型
     *
     * @param name 查询名称
     * @return
     */
    public List<MetadataType> getByName(String name) {
        String hql = "from MetadataType mt where mt.name like '%" + name + "%'";
        return super.queryByHQL(hql);
    }
}
