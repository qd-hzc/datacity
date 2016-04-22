package com.city.support.dataSet.controller;

import com.city.common.controller.BaseController;
import com.city.common.pojo.Page;
import com.city.support.dataSet.entity.DataSet;
import com.city.support.dataSet.service.DataSetService;
import com.city.support.regime.collection.entity.ReportData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * Created by wxl on 2016/2/22.
 * 数据集管理
 */
@Controller
@RequestMapping("/support/dataSet")
public class DataSetController extends BaseController {
    @Autowired
    private DataSetService dataSetService;

    /**
     * 进入数据集管理界面
     */
    @RequestMapping("/manageJsp")
    public String dataSetManageJsp() {
        return "support/dataSet/manage";
    }

    /**
     * 根据名字分页查询
     *
     * @param name 查询的名字
     * @param page 分页
     */
    @RequestMapping("/queryDataSet")
    @ResponseBody
    public Page queryDataSet(String name, Page page) {
        return dataSetService.queryByName(name, page);
    }

    /**
     * 保存数据集
     */
    @RequestMapping("/saveDataSet")
    @ResponseBody
    public Map<String, Object> saveDataSet(DataSet dataSet) {
        Map<String, Object> result = null;
        try {
            dataSetService.saveDataSet(dataSet);
            result = genSuccessMsg(null, "保存成功", 200);
        } catch (Exception e) {
            e.printStackTrace();
            result = genFaultMsg(null, "保存失败", 500);
        }
        return result;
    }

    /**
     * 删除数据集
     */
    @RequestMapping("/removeDataSets")
    @ResponseBody
    public Map<String, Object> removeDataSets(String ids) {
        Map<String, Object> result = null;
        try {
            dataSetService.removeDataSets(ids);
            result = genSuccessMsg(null, "删除成功", 200);
        } catch (Exception e) {
            e.printStackTrace();
            result = genFaultMsg(null, "删除失败", 500);
        }
        return result;
    }

    /**
     * 返回所有数据集
     * @return
     * @author crx
     * @createDate 2016-3-22
     */
    @RequestMapping("/getAllDataSet")
    @ResponseBody
    public Object getAllDataSet(){
        return  dataSetService.getAlldataSet();
    }
}
