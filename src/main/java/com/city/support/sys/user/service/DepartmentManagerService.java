package com.city.support.sys.user.service;

import com.city.common.event.EsiEvent;
import com.city.common.event.watcher.DepWatched;
import com.city.common.event.watcher.EsiEventWatched;
import com.city.common.util.*;
import com.city.support.sys.user.dao.DepartmentDao;
import com.city.support.sys.user.dao.RoleDao;
import com.city.support.sys.user.entity.Department;
import com.city.support.sys.user.util.GenDepmentTreeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wys on 2015/12/29.
 */
@Service
@Transactional(readOnly = false, rollbackFor = Exception.class)
public class DepartmentManagerService {
    @Autowired
    private DepartmentDao departmentDao;
    @Autowired
    private RoleDao roleDao;

    private EsiEventWatched esiEventWatched;

    @Autowired
    public DepartmentManagerService(DepWatched depWatched) {
        this.esiEventWatched = depWatched;
    }

    public Map<String, Object> getDepTree() {
        List<Department> departments = departmentDao.queryAllDepSort();
        Map<String, Object> root = new HashMap<>();
        GenDepmentTreeUtil genDepmentTreeUtil = new GenDepmentTreeUtil();
        root.put("id", 0);
        genDepmentTreeUtil.genTree(root, departments);
        return root;
    }

    public void removeDeparment(Department department) {
        EsiEvent beforeDel = new EsiEvent();
        beforeDel.setEventName(DepWatched.BEFOREDELETE);
        beforeDel.getArgs().put(DepWatched.PARAM_DEPIDS, department.getId().toString());
        esiEventWatched.notifyAllListener(beforeDel);

        departmentDao.delete(department, true);
        EsiEvent delEvent = new EsiEvent();
        delEvent.setEventName(DepWatched.DELETE);
        delEvent.getArgs().put(DepWatched.PARAM_DEPIDS, department.getId().toString());
        esiEventWatched.notifyAllListener(delEvent);
    }


    public void addDepartment(Department department) {
        EsiEvent beforeAdd = new EsiEvent();
        beforeAdd.setEventName(DepWatched.BEFOREADDDEP);
        beforeAdd.getArgs().put(DepWatched.PARAM_DEP, department);
        esiEventWatched.notifyAllListener(beforeAdd);
        //建立部门
        departmentDao.saveOrUpdate(department, true);
        EsiEvent addEvent = new EsiEvent();
        addEvent.setEventName(DepWatched.ADDDEP);
        addEvent.getArgs().put(DepWatched.PARAM_DEP, department);
        esiEventWatched.notifyAllListener(addEvent);
    }

    public void updateDeparment(Department department) {
        Department dep = departmentDao.queryById(department.getId());
        ConvertUtil<Department> convertUtil = new ConvertUtil<>();
        convertUtil.replication(department, dep, Department.class.getName());
        departmentDao.update(dep, true);
    }

    /**
     * 获取自定部门的子部门
     *
     * @param pid
     * @return
     */
    public List<Department> findDepByPid(Integer pid) {
        return departmentDao.queryDepByPid(pid);
    }

    /**
     * 获取所有下级
     *
     * @param depId 索要添加的下级
     */
    public List<Integer> findAllDownDeps(Integer depId) {
        List<Integer> depIds = new ArrayList<>();
        depIds.add(depId);
        //查询下级
        List<Department> deps = departmentDao.queryDepByPid(depId);
        if (ListUtil.notEmpty(deps)) {
            for (Department dep : deps) {
                depIds.addAll(findAllDownDeps(dep.getId()));
            }
        }
        return depIds;
    }

    /**
     * 根据id获取部门信息
     *
     * @param id
     * @return
     */
    public Department findDepById(Integer id) {
        return departmentDao.getDepById(id);
    }

}
