package com.city.support.sys.user.service;

import com.city.common.util.tree.PackageListToTree;
import com.city.support.sys.user.dao.DepartmentDao;
import com.city.support.sys.user.entity.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by wxl on 2016/1/7 0007.
 */
@Service
public class DepQueryService extends PackageListToTree<Department> {
    @Autowired
    private DepartmentDao departmentDao;

    @Override
    protected Department getEntityById(Integer entityKey) {
        return departmentDao.queryById(entityKey);
    }

    @Override
    protected Integer getEntityKey(Department department) {
        return department.getId();
    }

    @Override
    protected Integer getParentId(Department department) {
        return department.getpDep();
    }

    @Override
    protected String getEntityName(Department department) {
        return department.getDepName();
    }

    @Override
    public Map<String, Object> getEntityMap(Department department) {
        Map<String,Object> map= super.getEntityMap(department);
        map.put("depName",department.getDepName());
        return map;
    }

    public List<Department> queryDepTreeByName(String depName) {
        return departmentDao.getDepByName(depName);
    }
}
