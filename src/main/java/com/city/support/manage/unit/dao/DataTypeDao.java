package com.city.support.manage.unit.dao;

import com.city.common.dao.BaseDao;
import com.city.support.manage.unit.entity.DataType;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by zhoutao on 2015/12/30.
 * 数据类型操作DAO
 */
@Repository
public class DataTypeDao extends BaseDao<DataType> {

    //获取所有的数据类型
    public List<DataType> getAll(){
        return super.queryAll();
    }

    //增加数据类型
    public void insert(DataType dt){
        super.insert(dt, true);
    }

    //根据id获取数据类型
    public DataType getById(Integer id){
        return super.queryById(id);
    }

    //根据名称获取数据类型
    public List<DataType> getByName(String name){
        String hql = "from DataType dt where dt.name like '%"+name+"%'";
        return super.queryByHQL(hql);
    }
}
