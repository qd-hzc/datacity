package com.city.support.sys.user.dao;

import com.city.common.dao.BaseDao;
import com.city.support.sys.user.entity.Department;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wys on 2015/12/28.
 */
@Repository
public class DepartmentDao extends BaseDao<Department> {

    public List<Department> queryDepByPid(Integer pid) {
        String hql = "from Department t where t.pDep = ?";
        /*Query q = getSession().createQuery(hql);
        System.out.println(q);
        q.setInteger(0, pid);
        return q.list();*/
        Object[] args = {pid};
        return this.queryWithParamsByHQL(hql, args);
    }

    public List<Department> queryAllDepSort() {
        String hql = "from Department t order by t.sort";
        return this.queryByHQL(hql);
    }

    public Department getDepById(Integer id){
        return super.queryById(id);
    }

    public List<Department> getDepByIds(String ids) {
        String hql = "from Department t where id in ("+ids+") order by t.sort";
        return this.queryByHQL(hql);
    }
    public List<Department> getDepByName(String depName) {
        StringBuilder sb = new StringBuilder("from Department where 1=1");
        if (depName != null && depName.trim().length() > 0) {
            sb.append(" and depName like '%").append(depName).append("%'");
        }
        sb.append(" order by sort");
        return queryByHQL(sb.toString());
    }
}
