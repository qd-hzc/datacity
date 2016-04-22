package com.city.support.sys.user.dao;

import com.city.common.dao.BaseDao;
import com.city.common.pojo.Page;
import com.city.support.sys.user.entity.Department;
import com.city.support.sys.user.entity.Role;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RoleDao extends BaseDao<Role> {

    //添加角色
    public void add(Role role){
        super.saveOrUpdate(role, false);
    }

    public Role getById(Integer id){
        return super.queryById(id);
    }

    //根据名称查询角色信息
    public List<Role> getByName(String roleName){
        String hql = "from Role r where r.name like '%"+roleName+"%' and r.state = "+Role.State.ENABLE.ordinal()+" order by r.id desc";

        return super.queryByHQL(hql);
    }

    //根据名称查询角色信息 分页
    public List<Role> getByPage(Page page, String roleName){
        String hql = "from Role r where r.name like '%"+roleName+"%' and r.state = "+Role.State.ENABLE.ordinal()+" order by r.id desc";

        return super.queryWithPageByHQL(hql,page);
    }
}
