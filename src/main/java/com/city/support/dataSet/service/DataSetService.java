package com.city.support.dataSet.service;

import com.city.common.pojo.Constant;
import com.city.common.pojo.Page;
import com.city.common.util.ListUtil;
import com.city.support.dataSet.dao.DataSetDao;
import com.city.support.dataSet.dao.DataSetDataDao;
import com.city.support.dataSet.entity.DataSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by wxl on 2016/2/22.
 */
@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
public class DataSetService {
    @Autowired
    private DataSetDao dataSetDao;
    @Autowired
    private DataSetDataDao dataSetDataDao;

    /**
     * 根据名字分页查询
     *
     * @param name 查询的名字
     * @param page 分页
     */
    public Page queryByName(String name, Page page) {
        if (name == null) {
            name = "";
        }
        List<DataSet> dataSets = dataSetDao.queryByName(name, page);
        //设置标识状态
        if (dataSets != null && dataSets.size() > 0) {
            for (DataSet dataSet : dataSets) {
                //设置基础集状态
                boolean flag1 = dataSetDataDao.queryDataSetData(dataSet.getId(), Constant.MetadataType.ITEM, null).size() > 0;
                boolean flag2 = dataSetDataDao.queryDataSetData(dataSet.getId(), Constant.MetadataType.TIME_FRAME, null).size() > 0;
                dataSet.setBaseFlag(flag1 && flag2);
                //TODO 设置扩展集状态
            }
        }
        long total = (long) dataSetDao.queryTotal(name).get(0);
        page.setTotal((int) total);
        page.setDatas(dataSets);
        return page;
    }

    /**
     * 保存数据集
     */
    public String saveDataSet(DataSet dataSet) {
        //查看名称是否重复
        List<DataSet> dataSets = dataSetDao.queryByName(dataSet.getName().trim());
        if (ListUtil.notEmpty(dataSets)) {
            return "保存失败,数据集名称不能重复!";
        } else {
            try {
                dataSetDao.saveOrUpdate(dataSet, false);
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                return "保存失败,服务端运行异常!";
            }
        }

    }

    /**
     * 删除数据集
     */
    public void removeDataSets(String ids) {
        //清空基础集
        dataSetDataDao.clearDataSetDatas(ids);
        //TODO 清空扩展集
        dataSetDao.removeDataSets(ids);
    }

    /**
     * 返回所有数据集
     *
     * @return
     * @author crx
     * @createDate 2016-3-22
     */
    public Object getAlldataSet() {
        return dataSetDao.selectAll();
    }
}
