package com.city.support.sys.user.controller;

import com.city.common.controller.BaseController;
import com.city.common.util.EsiJsonParamUtil;
import com.city.common.util.EsiLogUtil;
import com.city.support.sys.user.entity.Department;
import com.city.support.sys.user.entity.Person;
import com.city.support.sys.user.service.DepartmentPersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by wys on 2016/1/13.
 */
@Controller
@RequestMapping("/support/sys/person")
public class DepPersonManagerController extends BaseController {
    @Autowired
    private DepartmentPersonService departmentPersonService;

    @RequestMapping("/queryPerson")
    @ResponseBody
    public Map<String,Object> queryPerson(Integer depId) {
        Map<String, Object> result = null;
        List<Person> persons = null;
        try {
            if (depId != null) {
                persons = departmentPersonService.queryPersonByDep(depId);
                result = genSuccessMsg(persons, "获取人员成功", null);
            } else {
                result = genFaultMsg(null, "未找到部门", null);
            }

        } catch (Exception e) {
            e.printStackTrace();
            EsiLogUtil.error(getLog(), e.getMessage());
            result = genFaultMsg(null, "获取人员失败", null);
        }
        return result;
    }

    @RequestMapping("/addPerson")
    @ResponseBody
    public Map<String, Object> addPerson(HttpServletRequest request) {
        Map<String, Object> result = null;
        List<Person> persons = null;
        EsiJsonParamUtil<Person> paramUtil = new EsiJsonParamUtil<>();
        Integer depId = null;
        try {
            persons = paramUtil.parseObjToList(request, Person.class);
            depId = Integer.parseInt(paramUtil.getParam(request, "depId"));
            Department department = new Department();
            department.setId(depId);
            if (persons != null && depId != null) {
                departmentPersonService.addPerson(persons, department);
                result = genSuccessMsg(persons, "添加人员成功", null);
            } else {
                result = genFaultMsg(null, "添加人员失败", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            EsiLogUtil.error(getLog(), e.getMessage());
            result = genFaultMsg(null, "添加人员失败", null);
        }
        return result;
    }

    @RequestMapping("/updatePerson")
    @ResponseBody
    public Map<String, Object> updatePerson(HttpServletRequest request) {
        Map<String, Object> result = null;
        List<Person> persons = null;
        EsiJsonParamUtil<Person> paramUtil = new EsiJsonParamUtil<>();
        try {
            persons = paramUtil.parseObjToList(request, Person.class);
            if (persons != null) {
                departmentPersonService.updatePerson(persons);
                result = genSuccessMsg(persons, "修改人员成功", null);
            } else {
                result = genFaultMsg(null, "修改人员失败", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            EsiLogUtil.error(getLog(), e.getMessage());
            result = genFaultMsg(null, "修改人员失败", null);
        }
        return result;
    }

    @RequestMapping("/delPerson")
    @ResponseBody
    public Map<String, Object> delPerson(HttpServletRequest request) {
        Map<String, Object> result = null;
        List<Person> persons = null;
        EsiJsonParamUtil<Person> paramUtil = new EsiJsonParamUtil<>();
        try {
            persons = paramUtil.parseObjToList(request, Person.class);
            if (persons != null) {
                departmentPersonService.delPerson(persons);
                result = genSuccessMsg(persons, "删除人员成功", null);
            } else {
                result = genFaultMsg(null, "删除人员失败", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            EsiLogUtil.error(getLog(), e.getMessage());
            result = genFaultMsg(null, "删除人员失败", null);
        }
        return result;
    }

}
