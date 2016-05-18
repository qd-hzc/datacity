package com.city.app.staffValid.controller;

import com.city.app.staffValid.entity.AppPerson;
import com.city.app.staffValid.service.AppPersonService;
import com.city.common.controller.BaseController;
import com.city.common.util.EsiJsonParamUtil;
import com.city.common.util.ListUtil;
import com.city.support.sys.user.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by wxl on 2016/3/28.
 */
@Controller
@RequestMapping("/app/personValid")
public class AppStaffController extends BaseController {
    @Autowired
    private AppPersonService appPersonService;

    @RequestMapping("/manageJsp")
    public ModelAndView manageJsp() {
        ModelAndView mv = new ModelAndView("/app/personValid/manage");
        return mv;
    }

    /**
     * 根据条件查询
     *
     * @param name             名称
     * @param validCode        校验码
     * @param depId            部门
     * @param includeDownLevel 包含下级
     */
    @RequestMapping("/queryStaffs")
    @ResponseBody
    public List<AppPerson> queryStaffs(String name, String validCode, Integer depId, boolean includeDownLevel) {
        List<AppPerson> persons = appPersonService.queryStaffs(name, validCode, depId, includeDownLevel);
        if (ListUtil.notEmpty(persons)) {
            for (AppPerson AppPerson : persons) {
                Role role = AppPerson.getRole();
                if (role != null) {
                    role.setCreateUser(null);
                }
            }
        }
        return persons;
    }


    @RequestMapping("/queryStaffsWithoutRole")
    @ResponseBody
    public List<AppPerson> queryStaffsWithoutRole(String name) {
        List<AppPerson> appPersons = appPersonService.queryStaffs(name, null, null, false);
        if (ListUtil.notEmpty(appPersons)) {
            for (AppPerson appPerson : appPersons) {
                appPerson.setRole(null);
            }
        }
        return appPersons;
    }

    /**
     * 查询根据validcode
     *
     * @param validCode 校验码
     */
    @RequestMapping("queryStaffByValidCode")
    @ResponseBody
    public String queryStaffByValidCode(String validCode) {
        return appPersonService.queryStaffByValidCode(validCode);
    }

    /**
     * 修改人员
     */
    @RequestMapping("/saveStaff")
    @ResponseBody
    public Map<String, Object> saveStaff(AppPerson appPerson) {
        Map<String, Object> result;
        try {
            appPersonService.saveStaff(appPerson);
            result = genSuccessMsg(null, "保存成功", 200);
        } catch (Exception e) {
            e.printStackTrace();
            result = genFaultMsg(null, "保存失败,服务端保存异常!", 500);
        }
        return result;
    }

    /**
     * 批量删除
     */
    @RequestMapping("/deleteStaffs")
    @ResponseBody
    public Map<String, Object> deleteStaff(HttpServletRequest request) {
        Map<String, Object> result;
        EsiJsonParamUtil<AppPerson> util = new EsiJsonParamUtil<>();
        try {
            List<AppPerson> staffs = util.parseObjToList(request, AppPerson.class);
            appPersonService.deleteStaff(staffs);
            result = genSuccessMsg(null, "删除成功", 200);
        } catch (Exception e) {
            e.printStackTrace();
            result = genFaultMsg(null, "删除失败", 500);
        }
        return result;
    }

    /**
     * 获取人员树
     */
    @RequestMapping("/getStaffTree")
    @ResponseBody
    public List<Map<String, Object>> getStaffTree() {
        return appPersonService.getStaffTree();
    }
}
