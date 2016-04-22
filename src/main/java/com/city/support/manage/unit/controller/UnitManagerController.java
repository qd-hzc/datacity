package com.city.support.manage.unit.controller;

import com.city.common.controller.BaseController;
import com.city.common.pojo.Page;
import com.city.support.manage.unit.entity.DataType;
import com.city.support.manage.unit.entity.Unit;
import com.city.support.manage.unit.entity.UnitType;
import com.city.support.manage.unit.service.UnitManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhoutao on 2015/12/30.
 * 单位管理controller
 */
@Controller
public class UnitManagerController extends BaseController {
    @Autowired
    private UnitManagerService ums;

    /**
     * 跳转到计量单位管理页面
     * @return  管理页面
     */
    @RequestMapping("/support/unit/unitManager/jumpUnitManage")
    public String jumpUnitManage(){
        return "support/manage/unit/unitManage";
    }

/*-----------计量单位类型管理 start ----------*/
    /**
     * 获取所有的计量单位类型信息 Tree
     * @return list
     */
    @RequestMapping("/support/unit/unitManager/findAllUnitTypeTree")
    @ResponseBody
    public List<Map<String, Object>> findAllUnitTypeTree(){
        return ums.findAllUnitTypeTree();
    }

    /**
     * 添加计量单位类型信息
     * @param ut    类型信息
     * @return
     */
    @RequestMapping("/support/unit/unitManager/addUnitType")
    @ResponseBody
    public Map<String, Object> addUnitType(UnitType ut){
        ums.saveUnitType(ut);
        return this.genSuccessMsg(ut, "保存单位类型成功!", 0);
    }

    /**
     * 修改计量单位类型信息
     * @param ut    类型信息
     * @return
     */
    @RequestMapping("/support/unit/unitManager/updateUnitType")
    @ResponseBody
    public Map<String, Object> updateUnitType(UnitType ut){
        ums.updateUnitType(ut);
        return this.genSuccessMsg(ut, "修改单位类型成功!", 0);
    }

    /**
     * 删除计量单位类型信息
     * @param ut    类型信息
     * @return
     */
    @RequestMapping("/support/unit/unitManager/deleteUnitType")
    @ResponseBody
    public Map<String, Object> deleteUnitType(UnitType ut){
        ums.deleteUnitType(ut);
        return this.genSuccessMsg(ut, "删除单位类型成功!", 0);
    }
/*-----------计量单位类型管理 end ----------*/

/*-----------计量单位信息管理 start ----------*/
    /**
     * 获取某单位类型下单位信息
     * @return
     */
    @RequestMapping("/support/unit/unitManager/findUnitByUnitType")
    @ResponseBody
    public Page findUnitByUnitType(UnitType ut, Page page){
        page.setDatas(ums.findUnitPageByType(ut, page));
        page.setTotal(ums.findUnitByType(ut).size());

        return page;
    }

    /**
     * 获取某单位类型下基准单位信息
     * @return  list
     */
    @RequestMapping("/support/unit/unitManager/findStandardUnitsByUnitType")
    @ResponseBody
    public List<Unit> findStandardUnitsByUnitType(UnitType ut){
        return ums.findStandardUnitByType(ut);
    }

    /**
     * 添加单位信息
     * @param unit  单位信息
     * @return
     */
    @RequestMapping("/support/unit/unitManager/addUnit")
    @ResponseBody
    public Map<String, Object> addUnit(Unit unit){
        if(unit.getStandardUnit() !=null && unit.getStandardUnit().getId() != null) {
            Unit standardUnit = ums.findUnitById(unit.getStandardUnit());//获取基准计量单位信息
            unit.setStandardUnit(standardUnit);
        }else {
            unit.setStandardUnit(null);
        }

        if(unit.getDataType() !=null && unit.getDataType().getId() != null) {
            DataType dt = ums.findDataTypeById(unit.getDataType());//获取数据类型信息
            unit.setDataType(dt);
        }else{
            unit.setDataType(null);
        }

        if(unit.getUnitType() !=null && unit.getUnitType().getId() != null) {
            UnitType ut = ums.findUnitTypeById(unit.getUnitType());//获取单位类型信息
            unit.setUnitType(ut);
        }else{
            unit.setUnitType(null);
        }
        ums.saveUnit(unit);
        return this.genSuccessMsg(null, "保存单位信息成功!", 0);
    }

    /**
     * 修改单位信息
     * @param unit  单位信息
     * @return
     */
    @RequestMapping("/support/unit/unitManager/updateUnit")
    @ResponseBody
    public Map<String, Object> updateUnit(Unit unit){
        if(unit.getStandardUnit() !=null && unit.getStandardUnit().getId() != null) {
            Unit standardUnit = ums.findUnitById(unit.getStandardUnit());//获取基准计量单位信息
            unit.setStandardUnit(standardUnit);
        }else {
            unit.setStandardUnit(null);
        }

        if(unit.getDataType() !=null && unit.getDataType().getId() != null) {
            DataType dt = ums.findDataTypeById(unit.getDataType());//获取数据类型信息
            unit.setDataType(dt);
        }else{
            unit.setDataType(null);
        }

        if(unit.getUnitType() !=null && unit.getUnitType().getId() != null) {
            UnitType ut = ums.findUnitTypeById(unit.getUnitType());//获取单位类型信息
            unit.setUnitType(ut);
        }else{
            unit.setUnitType(null);
        }
        ums.updateUnit(unit);
        return this.genSuccessMsg(null, "修改单位信息成功!", 0);
    }

    /**
     * 删除单位信息
     * @param unitIds
     * @return
     */
    @RequestMapping("/support/unit/unitManager/deleteUnit")
    @ResponseBody
    public Map<String, Object> deleteUnit(String unitIds){
        ums.batchDelte(unitIds);
        return this.genSuccessMsg(null, "删除单位信息成功!", 0);
    }

    /**
     * 根据名称获取单位信息
     * @param name  查询名称
     * @return
     */
    @RequestMapping("/support/unit/unitManager/findUnitByName")
    @ResponseBody
    public List<Unit> findUnitByName(String name){
        if(name == null)
            name = "";

        return ums.findUnitByName(name);
    }
/*-----------计量单位信息管理 end ----------*/

/*-----------数据类型管理 start ----------*/
    /**
     * 添加数据类型
     * @param dt    数据类型信息
     * @return
     */
    @RequestMapping("/support/unit/unitManager/addDataType")
    @ResponseBody
    public Map<String, Object> addDataType(DataType dt){
        ums.saveDataType(dt);
        return this.genSuccessMsg(dt, "保存数据类型成功!", 0);
    }

    /**
     * 获取所有数据类型
     * @return
     */
    @RequestMapping("/support/unit/unitManager/findAllDataTypes")
    @ResponseBody
    public List<DataType> findAllDataTypes(){
        return ums.findAllDataType();
    }

    /**
     * 根据名称获取数据类型
     * @param name  查询名称
     * @return
     */
    @RequestMapping("/support/unit/unitManager/findDataTypeByName")
    @ResponseBody
    public List<DataType> findDataTypeByName(String name){
        if(name == null)
            name = "";

        return ums.findDataTypeByName(name);
    }

/*-----------数据类型管理 end ----------*/











}
