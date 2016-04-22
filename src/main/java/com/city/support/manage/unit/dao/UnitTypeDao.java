package com.city.support.manage.unit.dao;

import com.city.common.dao.BaseDao;
import com.city.support.manage.unit.entity.UnitType;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by zhoutao on 2015/12/30.
 * 数据类型操作DAO
 */
@Repository
public class UnitTypeDao extends BaseDao<UnitType> {

    //获取所有类型
    public List<UnitType> getAll(){
        return super.queryAll();
    }

    /**
     * 根据id获取数据类型
     * @param id    id
     * @return  UnitType 单位类型信息
     */
    public UnitType getById(Integer id){
        return super.queryById(id);
    }

    //增加类型
    public void insert(UnitType ut){
        super.insert(ut, true);
    }

    //删除类型
    public void delete(UnitType ut){
        super.delete(ut, true);
    }

    //修改类型
    public void update(UnitType ut){
        super.update(ut, true);
    }
}
