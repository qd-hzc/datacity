package com.city.app.staffValid.service;

import com.city.app.staffValid.dao.AppPersonDao;
import com.city.app.staffValid.entity.AppPerson;
import com.city.common.util.ListUtil;
import com.city.support.sys.user.service.DepartmentManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by wxl on 2016/3/28.
 */
@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
public class AppPersonService {
    @Autowired
    private AppPersonDao appPersonDao;
    @Autowired
    private DepartmentManagerService departmentManagerService;

    /**
     * 根据条件查询
     *
     * @param name             名称
     * @param validCode        校验码
     * @param depId            部门
     * @param includeDownLevel 包含下级
     */
    public List<AppPerson> queryStaffs(String name, String validCode, Integer depId, boolean includeDownLevel) {
        String depIds;
        if (depId == null) {
            depIds = null;
        } else {
            if (includeDownLevel) {
                depIds = ListUtil.getArrStr(departmentManagerService.findAllDownDeps(depId));
            } else {
                depIds = depId.toString();
            }
        }
        return appPersonDao.queryStaffs(name, validCode, depIds);
    }

    /**
     * 查询對應id的人員
     */
    public List<AppPerson> queryAllStaffs(String ids) {
        return appPersonDao.queryAllStaffs(ids);
    }

    /**
     * 查询含有某验证码的人
     */
    public AppPerson queryStaffByValidCode(String validCode) {
        AppPerson appPerson = appPersonDao.queryStaffByValidCode(validCode);
        if (appPerson != null) {
            appPerson.setRole(null);
        }
        return appPerson;
    }

    /**
     * 修改人员
     */
    public void saveStaff(AppPerson appPerson) {
        appPersonDao.saveStaff(appPerson);
    }

    /**
     * 删除人员
     */
    public void deleteStaff(List<AppPerson> staffs) {
        if (ListUtil.notEmpty(staffs)) {
            for (AppPerson staff : staffs) {
                appPersonDao.delete(staff, true);
            }
        }
    }
}
