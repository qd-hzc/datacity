package com.city.support.sys.user.dao;

import com.city.common.dao.BaseDao;
import com.city.support.sys.user.entity.Person;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wys on 2015/12/28.
 */
@Repository
public class PersonDao extends BaseDao<Person> {
    public List<Person> queryPersonByDep(Integer depId){
        String hql = "from Person t where t.department.id = ?";
        Object[] params = {depId};
        return this.queryWithParamsByHQL(hql,params);
    }

    public void delPersonByDep(Integer depId){
        String hql = "delete Person t where t.department.id = ?";
        Object[] params = {depId};
        Query q = getSession().createQuery(hql);
        setParamsForQuery(q,params);
        q.executeUpdate();
    }
}
