package com.city.support.manage.unit.dao;

import com.city.common.dao.BaseDao;
import com.city.common.pojo.Page;
import com.city.support.manage.unit.entity.Unit;
import com.city.support.manage.unit.entity.UnitType;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by zhoutao on 2015/12/30.
 * 计量单位操作DAO
 */
@Repository
public class UnitDao extends BaseDao<Unit> {

    //增加
    public void insert(Unit unit){
        super.insert(unit, true);
    }

    //修改
    public void update(Unit unit){
        super.update(unit, true);
    }

    //删除
    public void delete(Unit unit){
        super.delete(unit, true);
    }

    /**
     * 根据单位类型批量删除单位信息
     * @param ut
     */
    public void deleteByUnitType(UnitType ut){
        String hql = "delete from Unit u where u.unitType.id = "+ut.getId();
        super.updateByHQL(hql);
    }

    /**
     * 批量删除
     * @param ids   id字符串 1,2,3
     */
    public void batchDelte(String ids){
        String hql = "delete from Unit u where u.id in ("+ids+")";
        super.updateByHQL(hql);
    }
    //获取所有计量单位
    public List<Unit> getAll(){
        return super.queryAll();
    }

    /**
     * 获取数据类型下计量单位
     * @param ut    数据类型
     * @return  list 集合
     */
    public List<Unit> getByType(UnitType ut){
        String hql = "from Unit u where u.unitType.id="+ut.getId();
        return super.queryByHQL(hql);
    }

    /**
     *分页获取数据类型下计量单位
     * @param ut
     * @return
     */
    public List<Unit> getPageByType(UnitType ut, Page page){
        String hql = "from Unit u where u.unitType.id="+ut.getId();
        return super.queryWithPageByHQL(hql, page);
    }

    /**
     * 获取数据类型下基准计量单位
     * @param ut    数据类型
     * @return  list 集合
     */
    public List<Unit> getStandardByType(UnitType ut){
        String hql = "from Unit u where u.unitType.id="+ut.getId()+" and u.standard=1";
        return super.queryByHQL(hql);
    }

    /**
     * 根据id获取单位
     * @param id    主键
     * @return  unit 单位信息
     */
    public Unit getById(Integer id){
        return super.queryById(id);
    }


    /**
     * 根据名称获取单位信息
     * @param name
     * @return
     */
    public List<Unit> getByName(String name){
        String hql = " from Unit u where u.name like '%"+name+"%'";
        return super.queryByHQL(hql);
    }
}
