package com.city.support.dataSet.controller;

import com.city.common.controller.BaseController;
import com.city.support.dataSet.entity.DataSetData;
import com.city.support.dataSet.pojo.DataSetInfoPojo;
import com.city.support.dataSet.service.DataSetDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * Created by wxl on 2016/2/23.
 */
@Controller
@RequestMapping("/support/dataSet")
public class DataSetDataController extends BaseController {
    @Autowired
    private DataSetDataService dataSetDataService;

    /**
     * 根据数据集查询
     */
    @RequestMapping("/queryDataSetData")
    @ResponseBody
    public List<DataSetData> queryDataSetDatas(Integer dataSetId, Integer dataType, String dataName) {
        return dataSetDataService.queryDataSetItem(dataSetId, dataType, dataName);
    }

    /**
     * 添加数据及数据
     *
     * @param dataSetDatas 数据集数据集合的json
     */
    @RequestMapping("/addDataSetDatas")
    @ResponseBody
    public Map<String, Object> addDataSetDatas(String dataSetDatas) {
        Map<String, Object> result;
        try {
            dataSetDataService.addDataSetDatas(dataSetDatas);
            result = genSuccessMsg("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            result = genFaultMsg("保存失败");
        }
        return result;
    }

    /**
     * 编辑数据集数据,
     */
    @RequestMapping("/editDataSetData")
    @ResponseBody
    public Map<String, Object> editDataSetData(DataSetData dataSetData) {
        Map<String, Object> result;
        try {
            String msg = dataSetDataService.editDataSetData(dataSetData);
            if (msg != null) {
                result = genFaultMsg(msg);
            } else {
                result = genSuccessMsg("保存成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = genFaultMsg("保存失败,失败原因:<b style=\"color:red\">数据库操作过程出错!<b>");
        }
        return result;
    }

    /**
     * 删除数据集数据
     *
     * @param ids ids
     */
    @RequestMapping("/removeDataSetDatas")
    @ResponseBody
    public Map<String, Object> removeDataSetDatas(String ids) {
        Map<String, Object> result;
        try {
            dataSetDataService.removeDataSetDatas(ids);
            result = genSuccessMsg("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            result = genFaultMsg("删除失败");
        }
        return result;
    }

    /**
     * 清空数据及数据
     */
    @RequestMapping("/clearDataSetDatas")
    @ResponseBody
    public Map<String, Object> clearDataSetDatas(Integer dataSetId) {
        Map<String, Object> result;
        try {
            dataSetDataService.clearDataSetDatas(dataSetId);
            result = genSuccessMsg("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            result = genFaultMsg("操作失败");
        }
        return result;
    }

    /**
     * 根据指标获取其他主宾蓝信息,指标为空时查询全部
     * 用来查询 指标,时间框架,地区,分组目录
     */
    @RequestMapping("/queryBarInfos")
    @ResponseBody
    public List<DataSetInfoPojo> queryBarInfos(int dataType, String itemIds) {
        return dataSetDataService.queryBarInfos(dataType, itemIds);
    }

    /**
     * 根据指标获取 口径,部门,所属表
     */
    @RequestMapping("/getInfoList")
    @ResponseBody
    public List getInfoList(int infoType, int itemId) {
        return dataSetDataService.getInfoList(infoType, itemId);
    }

    /**
     * 根据名称和类型获取map
     */
    @RequestMapping("/queryInfoName")
    @ResponseBody
    public List queryInfoName(Integer dataType, String dataIds) {
        return dataSetDataService.getInfoList(dataType, dataIds);
    }

}
