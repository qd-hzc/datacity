package com.city.support.sys.user.controller;

import com.city.common.controller.BaseController;
import com.city.common.util.EsiJsonParamUtil;
import com.city.common.util.EsiLogUtil;
import com.city.support.sys.user.entity.Department;
import com.city.support.sys.user.service.DepartmentManagerService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wys on 2015/12/31.
 */

@RequestMapping("/support/sys/dep")
@Controller
public class DepManagerController extends BaseController {
    @Autowired
    private DepartmentManagerService departmentManagerService;

    @RequestMapping("/configPage")
    public ModelAndView getDepmentConfig() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("/support/sys/dep/configPage");
        return mv;
    }

    @RequestMapping("/queryAllDep")
    @ResponseBody
    public Map queryAllDep(HttpServletRequest request) {
        Map<String, Object> result = null;
        Map<String, Object> root = null;
        try {
            root = departmentManagerService.getDepTree();
            root.put("id", 0);
            root.put("depName", "组织机构");
            List list = new ArrayList();
            return root;
        } catch (Exception e) {
            e.printStackTrace();
            EsiLogUtil.error(getLog(), e.getMessage());
            result = this.genFaultMsg(null, "获取部门失败", null);
        }
        return root;
    }

    @RequestMapping("/addDep")
    @ResponseBody
    public Map<String, Object> addDepment(Department department) {

        Map<String, Object> result = null;
        try {
            Gson g = new Gson();
            EsiLogUtil.debug(getLog(), g.toJson(department));
            departmentManagerService.addDepartment(department);
            result = genSuccessMsg(department, "添加部门成功", null);

        } catch (Exception e) {
            e.printStackTrace();
            EsiLogUtil.error(getLog(), e.getMessage());
            result = genFaultMsg(null, "添加部门失败", null);
        }
        return result;
    }

    @RequestMapping("/updateDep")
    @ResponseBody
    public Map<String, Object> updateDepment(HttpServletRequest request) {
        Map<String, Object> result = null;
        List<Department> departments = null;
        try {
            EsiJsonParamUtil<Department> eJPUtil = new EsiJsonParamUtil<>();
            departments = eJPUtil.parseObjToList(request, Department.class);
            for (Department department : departments) {
                System.out.println(department);
                departmentManagerService.updateDeparment(department);
            }
            result = genSuccessMsg(null, "修改部门成功", null);
        } catch (Exception e) {
            e.printStackTrace();
            EsiLogUtil.error(getLog(), e.getMessage());
            result = genFaultMsg(null, "修改失败", null);
        }
        return result;
    }

    @RequestMapping("/removeDep")
    @ResponseBody
    public Map<String, Object> removeDepment(HttpServletRequest request) {
        Map<String, Object> result = null;
        List<Department> departments = null;
        try {
            EsiJsonParamUtil<Department> eJPUtil = new EsiJsonParamUtil<>();
            departments = eJPUtil.parseObjToList(request, Department.class);
            for (Department department : departments) {
                departmentManagerService.removeDeparment(department);
            }
            result = genSuccessMsg(null, "删除部门成功", null);
        } catch (Exception e) {
            e.printStackTrace();
            EsiLogUtil.error(getLog(), e.getMessage());
            result = genFaultMsg(null, "删除失败", null);
        }
        return result;
    }
}
