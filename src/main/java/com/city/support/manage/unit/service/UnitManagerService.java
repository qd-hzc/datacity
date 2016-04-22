package com.city.support.manage.unit.service;

import com.city.common.pojo.Page;
import com.city.support.manage.unit.dao.DataTypeDao;
import com.city.support.manage.unit.dao.UnitDao;
import com.city.support.manage.unit.dao.UnitTypeDao;
import com.city.support.manage.unit.entity.DataType;
import com.city.support.manage.unit.entity.Unit;
import com.city.support.manage.unit.entity.UnitType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.datatype.DatatypeConfigurationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhoutao on 2015/12/30.
 * 计量单位管理服务Service
 * 包括:单位类型管理服务,数据类型管理服务,单位信息管理服务
 */
@Service
@Transactional
public class UnitManagerService {
    @Autowired
    private UnitDao ud;
    @Autowired
    private UnitTypeDao utd;
    @Autowired
    private DataTypeDao dtd;

/*-----单位信息维护------*/
    /**
     * 保存或修改单位信息
     *<div style="color: red"> 注意事项:</div>
     * unit.unitType.id 存储单位类型id 不可为空
     * unit.dataType.id 存储数据类型id 不可为空
     */
    public boolean saveOrUpdateUnit(Unit unit){

        if(unit.getUnitType().getId() == null || unit.getDataType().getId()==null)
            return false;

        UnitType ut = utd.getById(unit.getUnitType().getId());//单位类型
        DataType dt = dtd.getById(unit.getDataType().getId());//数据类型
        unit.setUnitType(ut);
        unit.setDataType(dt);

        ud.saveOrUpdate(unit, true);

        return true;
    }

    /**
     * 保存单位信息
     *<div style="color: red"> 注意事项:</div>
     * unit.unitType.id 存储单位类型id 不可为空
     * unit.dataType.id 存储数据类型id 不可为空
     */
    public boolean saveUnit(Unit unit){
        return this.saveOrUpdateUnit(unit);
    }

    /**
     * 修改单位信息
     *<div style="color: red"> 注意事项:</div>
     * unit.unitType.id 存储单位类型id 不可为空
     * unit.dataType.id 存储数据类型id 不可为空
     */
    public boolean updateUnit(Unit unit){
        return this.saveOrUpdateUnit(unit);
    }

    /**
     * 删除单位信息
     * @param unit  单位信息
     */
    public void deleteUnit(Unit unit){
        ud.delete(unit);
    }

    /**
     * 批量删除
     * @param ids   id字符串 1,2,3
     */
    public void batchDelte(String ids){
        ud.batchDelte(ids);
    }

    /**
     * 根据id获取单位信息
     * @param Unit  单位信息包含要查询的id
     * @return Unit 单位信息
     */
    public Unit findUnitById(Unit Unit){
        return ud.getById(Unit.getId());
    }

    /**
     * 获取数据类型下基准计量单位
     * @param ut    数据类型
     * @return  list 集合
     */
    public List<Unit> findStandardUnitByType(UnitType ut){
        return ud.getStandardByType(ut);
    }

    /**
     * 获取数据类型下计量单位
     * @param ut    数据类型
     * @return  list 集合
     */
    public List<Unit> findUnitByType(UnitType ut){
        return ud.getByType(ut);
    }


    /**
     * 分页获取数据类型下计量单位
     * @param ut    数据类型
     * @param page
     * @return
     */
    public List<Unit> findUnitPageByType(UnitType ut, Page page){
        return ud.getPageByType(ut, page);
    }

    /**
     * 根据名称获取单位信息
     * @param name  名称
     * @return
     */
    public List<Unit> findUnitByName(String name){
        return ud.getByName(name);
    }
/*----单位类型维护 start------*/

    /**
     * 获取所有的单位类型
     * @return  list
     */
    public List<UnitType> findAllUnitType(){
        return utd.getAll();
    }

    /**
     * 获取所有的单位类型 tree
     * @return  list
     */
    public List<Map<String, Object>> findAllUnitTypeTree(){
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

        List<UnitType> uts = this.findAllUnitType();
        for(UnitType ut : uts){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", ut.getId());
            map.put("name", ut.getName());
            map.put("comments", ut.getComments());
            map.put("text", ut.getName());
            map.put("leaf", true);

            result.add(map);
        }

        return result;
    }

    /**
     * 保存单位类型
     * @param ut    单位类型
     */
    public void saveUnitType(UnitType ut){
        utd.insert(ut);
    }

    /**
     * 修改单位类型
     * @param ut    单位类型
     */
    public void updateUnitType(UnitType ut){
        utd.update(ut);
    }

    /**
     * 删除单位类型
     * 注意: 会级联删除类型下单位信息
     * @param ut    单位类型
     */
    public void deleteUnitType(UnitType ut){
        ud.deleteByUnitType(ut); //删除单位类型下的单位信息
        utd.delete(ut);         //删除单位类型
    }

    /**
     * 根据id获取单位类型
     * @param ut    单位类型包含要查询的id
     * @return  unitType 单位类型
     */
    public UnitType findUnitTypeById(UnitType ut){
        return utd.getById(ut.getId());
    }
/*-----数据类型维护 start---------*/

    /**
     * 获取所有数据类型
     * @return  list    数据类型list
     */
    public List<DataType> findAllDataType(){
        return dtd.getAll();
    }

    /**
     * 保存数据类型
     * @param dt    数据类型
     */
    public void saveDataType(DataType dt){
        dtd.insert(dt);
    }

    /**
     * 根据id获取数据类型
     * @param dt    数据类型包含要查询的id
     * @return  dataType 数据类型
     */
    public DataType findDataTypeById(DataType dt){
        return dtd.getById(dt.getId());
    }

    /**
     * 根据名称获取数据类型
     * @param name  名称
     * @return
     */
    public List<DataType> findDataTypeByName(String name){
        return dtd.getByName(name);
    }

}
