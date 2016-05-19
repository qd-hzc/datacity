package com.city.app.staffValid.service;

import com.city.app.staffValid.dao.AppPersonDao;
import com.city.app.staffValid.entity.AppPerson;
import com.city.common.util.ListUtil;
import com.city.support.sys.user.service.DepartmentManagerService;
import com.google.gson.Gson;
import org.apache.shiro.codec.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.Charset;
import java.util.*;

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
    @Autowired
    private AppLoginStatusService appLoginStatusService;

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
     * 查询含有某验证码的人 手机端认证登录
     */
    public String queryStaffByValidCode(String validCode, String uuid) {
        Map<String, Object> result = new HashMap<>();
        //首先根据验证码查询能登陆还剩多长时间
        long l = appLoginStatusService.nextLoginTime(uuid);
        if (l > 0) {//不可登录!
            long t = 60 * 1000 * 60 - l;
            result.put("success", false);
            //分钟数
            long minutes = t / 1000 / 60;
            //秒数
            long seconds = (t / 1000) % 60;
            result.put("msg", "短时间内登陆过于频繁,距离下次登录时间还有:" + minutes + "分" + seconds + "秒!");
        } else {//登录
            byte[] decode = Base64.decode(Base64.decode(validCode.getBytes()));//解码
            AppPerson appPerson = appPersonDao.queryStaffByValidCode(new String(decode));
            if (appPerson == null) {//验证码错误
                //保存失败次数
                appLoginStatusService.saveFailLogin(uuid);
                result.put("success", false);
                result.put("msg", "登录失败,验证码错误!");
            } else {
                //登陆成功,清除登陆次数
                appLoginStatusService.clearFailLogin(uuid);
                result.put("success", true);
                Map<String, Object> obj = new HashMap<>();
                obj.put("id", appPerson.getId());
                obj.put("name", appPerson.getName());
                obj.put("duty", appPerson.getDuty());
                result.put("data", obj);
            }
        }
        return new String(Base64.encode(Base64.encode(new Gson().toJson(result).getBytes(Charset.forName("UTF-8")))));
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

    /**
     * 获取人员树
     */
    public List<Map<String, Object>> getStaffTree() {
        List<AppPerson> appPersons = appPersonDao.queryAll();
        List<Map<String, Object>> result = null;
        if (ListUtil.notEmpty(appPersons)) {
            //先按照职位存储
            Map<Integer, List<AppPerson>> dutyMap = new HashMap<>();
            List<AppPerson> dutyPersons = null;
            for (AppPerson person : appPersons) {
                Integer dutyId = person.getDutyId();
                dutyPersons = dutyMap.get(dutyId);
                if (dutyPersons == null) {
                    dutyPersons = new ArrayList<>();
                    dutyMap.put(dutyId, dutyPersons);
                }
                dutyPersons.add(person);
            }
            //组合成职位树
            result = new ArrayList<>();
            Set<Integer> keys = dutyMap.keySet();
            Map<String, Object> dutyObj = null;
            List<Map<String, Object>> dutyChildren = null;
            for (Integer key : keys) {
                dutyPersons = dutyMap.get(key);
                dutyObj = new HashMap<>();
                dutyObj.put("id", "duty-" + key);
                dutyObj.put("name", dutyPersons.get(0).getDuty());
                //拼装每个职务下的人员
                dutyChildren = new ArrayList<>();
                Map<String, Object> personObj = null;
                for (AppPerson person : dutyPersons) {
                    personObj = new HashMap<>();
                    personObj.put("id", person.getId());
                    personObj.put("name", person.getName());
                    personObj.put("leaf", true);
                    personObj.put("checked", false);
                    dutyChildren.add(personObj);
                }
                dutyObj.put("expanded", true);
                dutyObj.put("checked", false);
                dutyObj.put("children", dutyChildren);
                result.add(dutyObj);
            }
        }
        return result;
    }
}
