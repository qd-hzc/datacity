package com.city.support.dataSet.dao;

import com.city.common.dao.BaseDao;
import com.city.common.pojo.Constant;
import com.city.support.dataSet.entity.DataSetData;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wxl on 2016/2/23.
 */
@Repository
public class DataSetDataDao extends BaseDao<DataSetData> {

    /**
     * 根据数据集查询
     */
    public List<DataSetData> queryDataSetData(Integer dataSetId, Integer dataType, String dataName) {
        StringBuilder sb = new StringBuilder("from DataSetData where dataSetId=").append(dataSetId);
        if (dataType != null) {
            sb.append(" and dataType=").append(dataType);
        }
        if (dataName != null && dataName.trim().length() > 0) {
            sb.append(" and dataName like '%").append(dataName).append("%'");
        }
        return queryByHQL(sb.toString());
    }


    /**
     * 删除数据及数据
     */
    public void removeDataSetDatas(String ids) {
        updateByHQL(new StringBuilder("delete from DataSetData where id in (").append(ids).append(")").toString());
    }

    /**
     * 清空数据及数据
     */
    public void clearDataSetDatas(String dataSetIds) {
        updateByHQL(new StringBuilder("delete from DataSetData where dataSetId in (").append(dataSetIds).append(")").toString());
    }

    /**
     * 根据指标获取其他主宾蓝信息
     */
    public List queryBarInfos(int dataType, String itemIds) {
        if (dataType == Constant.MetadataType.ITEM) {//指标
            StringBuilder sb = new StringBuilder("from ReportTemplateBarInfo where dataType=").append(Constant.MetadataType.ITEM);
            if (itemIds != null && itemIds.trim().length() > 0) {
                sb.append(" and dataValue in (").append(itemIds).append(")");
            }
            return queryByHQL(sb.toString());
        }
        StringBuilder sb = new StringBuilder("from ReportTemplateBarInfo where dataType=").append(dataType)
                .append(" and tmpId in (").append("select tmpId from ReportTemplateBarInfo where dataType=")
                .append(Constant.MetadataType.ITEM);
        if (itemIds != null && itemIds.trim().length() > 0) {
            sb.append(" and dataValue in (").append(itemIds).append(")");
        }
        sb.append(") order by dataValue");
        return queryByHQL(sb.toString());
    }

    /**
     * 根据id集合获取指标口径
     */
    public List queryCalibers(String caliberIds) {
        return queryBySql(new StringBuilder("select id,name from SPT_MGR_ITEMCALIBER where id in (").append(caliberIds).append(")").toString());
    }

    /**
     * 根据id集合获取部门
     */
    public List queryDeps(String depIds) {
        return queryBySql(new StringBuilder("select id,dep_name name from SPT_SYS_DEPART where id in (").append(depIds).append(")").toString());
    }

    /**
     * 根据id集合获取报表
     */
    public List queryTmps(String tmpIds) {
        return queryBySql(new StringBuilder("select id,name from SPT_RGM_RPT_TMP where id in (").append(tmpIds).append(")").toString());
    }

    /**
     * 根据id集合获取地区
     */
    public List queryAreas(String areaIds) {
        return queryByHQL(new StringBuilder("select id,name from SptMgrAreaEntity where id in (").append(areaIds).append(")").toString());
    }
}
