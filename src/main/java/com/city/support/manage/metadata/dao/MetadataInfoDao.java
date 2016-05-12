package com.city.support.manage.metadata.dao;

import com.city.common.dao.BaseDao;
import com.city.common.pojo.Page;
import com.city.support.manage.metadata.entity.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wgx on 2016/1/13.
 */
@Repository
public class MetadataInfoDao extends BaseDao<MetadataInfo> {

    /**
     * 获取所有的系统元数据
     *
     * @return
     */
    public List<MetadataInfo> getAll() {
        return super.queryAll();
    }

    /**
     * 根据id获取系统元数据信息
     *
     * @param id id
     * @return MetadataInfo 系统元数据信息
     */
    public MetadataInfo getById(Integer id) {
        MetadataInfo mi = super.queryById(id);
        return mi;
    }

    /**
     * 根据类型获取系统元数据信息
     *
     * @param type 元数据类型
     * @return MetadataInfo 系统元数据信息
     */
    public List<MetadataInfo> getByType(Integer type) {
        String hql = "from MetadataInfo where type='" + type + "'order by sort";
        return queryByHQL(hql);
    }

    /**
     * 根据类型和分页条件获取系统元数据信息
     *
     * @param type
     * @param page
     * @return
     */
    public List<MetadataInfo> getPageByType(Integer type, Page page) {
        String hql = "from MetadataInfo where type='" + type + "' order by sort";
        return queryWithPageByHQL(hql, page);
    }

    /**
     * 增加系统元数据
     *
     * @param metadataInfo
     */
    public void insert(MetadataInfo metadataInfo) {
        super.insert(metadataInfo, true);
    }

    /**
     * 修改系统元数据
     *
     * @param metadataInfo
     */
    public void update(MetadataInfo metadataInfo) {
        super.update(metadataInfo, true);
    }

    /**
     * 删除系统元数据
     *
     * @param metadataInfo
     */
    public void delete(MetadataInfo metadataInfo) {
        super.delete(metadataInfo, true);
    }

    /**
     * 根据type批量删除
     *
     * @param types 元数据类型id字符串
     */
    public void batchDeleteByType(String types) {
        if (types != null) {
            String sql = "delete from SPT_MGR_METADATA_INFO where type in (" + types + ")";
            super.getSession().createSQLQuery(sql).executeUpdate();
        }
    }


    /**
     * 使用sql批量删除
     *
     * @param ids id字符串 格式为 1,2,3
     */
    public void batchDelete(String ids) {
        if (!StringUtils.isEmpty(ids)) {
            String sql = "delete from SPT_MGR_METADATA_INFO where id in (" + ids + ")";
            super.getSession().createSQLQuery(sql).executeUpdate();
        }
    }

    /**
     * 根据名称获取系统元数据
     *
     * @param name 查询名称
     * @return
     */
    public List<MetadataInfo> getByName(String name) {
        String hql = "from MetadataInfo mi where mi.name like '%" + name + "%'";
        return super.queryByHQL(hql);
    }

    public List<MetadataInfo> getByTypeAndName(Integer type, String name) {
        String hql = "from MetadataInfo mi where mi.name ='" + name + "' and mi.type ='" + type + "'";
        return super.queryByHQL(hql);
    }
}
