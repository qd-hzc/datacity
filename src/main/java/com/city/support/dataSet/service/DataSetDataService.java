package com.city.support.dataSet.service;

import com.city.common.pojo.Constant;
import com.city.common.util.ListUtil;
import com.city.support.dataSet.dao.DataSetDataDao;
import com.city.support.dataSet.entity.DataSetData;
import com.city.support.dataSet.pojo.DataInfoType;
import com.city.support.dataSet.pojo.DataSetAreaInfoPojo;
import com.city.support.dataSet.pojo.DataSetInfoPojo;
import com.city.support.manage.item.entity.ItemCaliber;
import com.city.support.regime.report.entity.ReportTemplateBarInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by wxl on 2016/2/23.
 */
@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
public class DataSetDataService {
    @Autowired
    private DataSetDataDao dataSetDataDao;

    /**
     * 根据数据集查询
     */
    public List<DataSetData> queryDataSetItem(Integer dataSetId, Integer dataType, String dataName) {
        return dataSetDataDao.queryDataSetData(dataSetId, dataType, dataName);
    }

    /**
     * 根据指标获取其他主宾蓝信息
     * 用来查询 指标,时间框架,地区,分组目录
     */
    public List<DataSetInfoPojo> queryBarInfos(int dataType, String itemIds) {
        List<ReportTemplateBarInfo> bars = dataSetDataDao.queryBarInfos(dataType, itemIds);
        List<DataSetInfoPojo> infos = new ArrayList<>();
        if (bars != null && bars.size() > 0) {
            List<Integer> infoIdList = new ArrayList<>();
            for (ReportTemplateBarInfo bar : bars) {
                int dataValue = bar.getDataValue();
                if (!infoIdList.contains(dataValue)) {
                    if (dataType == Constant.MetadataType.RESEARCH_OBJ) {//调查对象类型
                        infos.add(new DataSetAreaInfoPojo(dataValue, bar.getDataName(), Integer.parseInt(bar.getDataInfo1()), Integer.parseInt(bar.getDataInfo2())));
                    } else {
                        infos.add(new DataSetInfoPojo(dataValue, bar.getDataName()));
                    }
                    infoIdList.add(dataValue);
                }
            }
        }
        return infos;
    }

    /**
     * 根据指标获取口径,部门和所属报表
     */
    public List getInfoList(int infoType, int itemId) {
        List<ReportTemplateBarInfo> bars = dataSetDataDao.queryBarInfos(Constant.MetadataType.ITEM, itemId + "");
        List<ItemCaliber> infos = new ArrayList<>();
        if (bars != null && bars.size() > 0) {
            Set<Integer> caliberIds = new HashSet<>();
            for (ReportTemplateBarInfo bar : bars) {
                String dataInfo1 = getInfoId(infoType, bar);
                if (dataInfo1 != null && dataInfo1.trim().length() > 0) {
                    caliberIds.add(Integer.parseInt(dataInfo1));
                }
            }
            //查询数据库,获取口径数据
            String cs = ListUtil.getArrStr(caliberIds);
            return getInfoList(infoType, cs);
        }
        return infos;
    }

    /**
     * 根据类型获取id
     */
    private String getInfoId(int infoType, ReportTemplateBarInfo bar) {
        switch (infoType) {
            case DataInfoType.CALIBER:
                return bar.getDataInfo1();
            case DataInfoType.DEP:
                return bar.getDataInfo2();
            case DataInfoType.RPT:
                return bar.getTmpId().toString();
            default:
                return null;
        }
    }

    /**
     * 根据类型获取结果
     */
    public List getInfoList(int infoType, String infoIds) {
        switch (infoType) {
            case DataInfoType.CALIBER:
                return dataSetDataDao.queryCalibers(infoIds);
            case DataInfoType.DEP:
                return dataSetDataDao.queryDeps(infoIds);
            case DataInfoType.RPT:
                return dataSetDataDao.queryTmps(infoIds);
            case DataInfoType.AREA:
                return dataSetDataDao.queryAreas(infoIds);
            default:
                return null;
        }
    }

    /**
     * 添加数据集数据
     *
     * @param dataSetDatas 数据集数据
     */
    public void addDataSetDatas(String dataSetDatas) {
        List<DataSetData> datas = new Gson().fromJson(dataSetDatas, new TypeToken<List<DataSetData>>() {
        }.getType());
        if (datas != null && datas.size() > 0) {
            Map<Integer, List<DataSetData>> qMap = new HashMap<>();
            List<DataSetData> dataList = null;
            for (DataSetData data : datas) {
                int dataSetId = data.getDataSetId();
                dataList = qMap.get(dataSetId);
                if (dataList == null) {
                    dataList = dataSetDataDao.queryDataSetData(dataSetId, null, null);
                    qMap.put(dataSetId, dataList);
                }
                if (!dataList.contains(data)) {
                    dataSetDataDao.insert(data, false);
                }
            }
        }
    }

    /**
     * 编辑数据集数据,
     */
    public String editDataSetData(DataSetData dataSetData) {
        //该数据集必不为空
        List<DataSetData> dataSetDatas = dataSetDataDao.queryDataSetData(dataSetData.getDataSetId(), null, null);
        DataSetData data = dataSetDataDao.queryById(dataSetData.getId());
        //移除掉当前id对应的数据
        dataSetDatas.remove(data);
        //判断是否包含要保存的数据
        if (dataSetDatas.contains(dataSetData)) {
            return "保存失败,失败原因:<b style=\"color:red\">该数据集中已存在要保存的数据!</b>";
        }
        dataSetDataDao.getSession().merge(dataSetData);
        return null;
    }

    /**
     * 删除数据集数据
     *
     * @param ids
     */
    public void removeDataSetDatas(String ids) {
        dataSetDataDao.removeDataSetDatas(ids);
    }

    /**
     * 清空数据及数据
     */
    public void clearDataSetDatas(Integer dataSetId) {
        dataSetDataDao.clearDataSetDatas(dataSetId.toString());
    }
}
