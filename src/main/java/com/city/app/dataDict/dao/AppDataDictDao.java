package com.city.app.dataDict.dao;

import com.city.app.dataDict.entity.AppDataDict;
import com.city.common.dao.BaseDao;
import com.city.common.pojo.AppConstant;
import com.city.common.util.StringUtil;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wxl on 2016/3/21.
 */
@Repository
public class AppDataDictDao extends BaseDao<AppDataDict> {

    /**
     * 查询数据字典
     *
     * @param menuIds 目录
     * @param name    名字
     * @param status  状态
     */
    public List<AppDataDict> queryDict(String menuIds, String name, Integer status) {
        StringBuilder sb = new StringBuilder("from AppDataDict where 1=1");
        if (StringUtil.trimNotEmpty(menuIds)) {
            sb.append(" and menuId in (").append(menuIds).append(")");
        }
        if (StringUtil.trimNotEmpty(name)) {
            sb.append(" and dataName like '%").append(name.trim()).append("%'");
        }
        if (status != null) {
            sb.append(" and status=").append(status);
        }
        sb.append(" order by sortIndex,id desc");
        return queryByHQL(sb.toString());
    }

    /**
     * 根据父节点目录查询数据集
     */
    public List queryDownDataSets(Integer menuId) {
        //从数据中查询
        StringBuilder sb = new StringBuilder("select dataValue from AppDataDict where dataType=").append(AppConstant.DATA_DICT_TYPE.DATA_SET)
                .append(" and menuId in (").append("select id from AppDataDictMenu where parentId=").append(menuId).append(")");
        return queryByHQL(sb.toString());
    }

    /**
     * 根据目录和类型获取
     *
     * @param menuId
     * @param dataType
     * @return
     */
    public List<AppDataDict> queryByMenuAndType(Integer menuId, Integer dataType) {
        StringBuilder sb = new StringBuilder("from AppDataDict where 1=1");
        if (dataType != null) {
            sb.append(" and dataType=").append(dataType);
        }
        if (menuId != null) {
            sb.append(" and menuId=").append(menuId);
        }
        return queryByHQL(sb.toString());
    }

    /**
     * 根据menuId删除
     *
     * @param menuId
     */
    public void deleteDictsByMenu(Integer menuId) {
        StringBuilder sb = new StringBuilder("delete from AppDataDict where menuId=").append(menuId);
        updateByHQL(sb.toString());
    }

    /**
     *根据目录获取已发布数据
     * @param menuId
     * @return
     */
    public List<AppDataDict> queryByMenu(Integer menuId){
        String hql = "from AppDataDict where status = 1 and menuId = "+menuId;

        return queryByHQL(hql);
    }
}
