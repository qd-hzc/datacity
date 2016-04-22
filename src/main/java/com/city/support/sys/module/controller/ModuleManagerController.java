package com.city.support.sys.module.controller;

import com.city.common.controller.BaseController;
import com.city.common.util.EsiJsonParamUtil;
import com.city.common.util.EsiLogUtil;
import com.city.support.sys.module.entity.Module;
import com.city.support.sys.module.service.ModuleManagerService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
/**
 * Created by wys on 2015/12/30.
 */
@RequestMapping("/support/sys/module")
@Controller
public class ModuleManagerController extends BaseController {

    @Autowired
    ModuleManagerService moduleManagerService;

    /**
     * 进入模块管理页面
     * @return
     */
    @RequestMapping("/moduleConfig")
    public ModelAndView getModuleConfig(){
        ModelAndView mv = new ModelAndView();
        mv.addObject("/support/sys/module/moduleConfig");
        return mv;
    }

    /**
     * 获取所有模块
     * @param httpServletRequest
     * @return
     */
    @RequestMapping("/queryAllModels")
    @ResponseBody
    public List<Module> queryAllModules(HttpServletRequest httpServletRequest){
            return moduleManagerService.getAllModuleBySort();
    }

    /**
     * 获取所有模块的树
     * @param httpServletRequest
     * @return
     */
    @RequestMapping("/queryAllModuleTree")
    @ResponseBody
    public Map queryAllModule(HttpServletRequest httpServletRequest){
        Map<String, Object> root = null;
        try {
            root = moduleManagerService.getAllModuleTreeBySort();
            root.put("id", Module.ROOT);
            root.put("muduleName","模块管理");
        } catch (Exception e) {
            e.printStackTrace();
            //this.genFaultMsg(null,"获取模块失败",null);
        }
        return root;
    }

    /**
     * 获取带复选框的所有模块的树
     * add by zt
     * @param httpServletRequest
     * @return
     */
    @RequestMapping("/queryAllCheckModule")
    @ResponseBody
    public Map queryAllCheckModule(HttpServletRequest httpServletRequest){
        Map<String, Object> root = null;
        try {
            root = moduleManagerService.getAllModuleCheckTreeBySort();
            root.put("id", "0");
            root.put("muduleName","模块管理");
        } catch (Exception e) {
            e.printStackTrace();
            //this.genFaultMsg(null,"获取模块失败",null);
        }
        return root;
    }

    /**
     * 添加模块
     * @param Module
     * @return
     */
    @RequestMapping("/addModule")
    @ResponseBody
    public Map<String, Object> addModule(Module Module) {
        Map<String, Object> result = null;
        try {
            Gson g = new Gson();
            EsiLogUtil.debug(getLog(), g.toJson(Module));
            moduleManagerService.addModule(Module);
            result = genSuccessMsg(Module, "添加模块成功", null);

        } catch (Exception e) {
            e.printStackTrace();
            EsiLogUtil.error(getLog(), e.getMessage());
            result = genFaultMsg(null, "添加模块失败", null);
        }
        return result;
    }

    /**
     * 修改模块
     * @param request
     * @return
     */
    @RequestMapping("/updateModule")
    @ResponseBody
    public Map<String, Object> updateModule(HttpServletRequest request) {
        Map<String, Object> result = null;
        List<Module> Modules = null;
        try {
            EsiJsonParamUtil<Module> eJPUtil = new EsiJsonParamUtil<>();
            Modules = eJPUtil.parseObjToList(request, Module.class);
            for (Module Module : Modules) {
                moduleManagerService.updateModule(Module);
            }
            result = genSuccessMsg(null, "修改模块成功", null);
        } catch (Exception e) {
            e.printStackTrace();
            EsiLogUtil.error(getLog(), e.getMessage());
            result = genFaultMsg(null, "修改失败", null);
        }
        return result;
    }

    /**
     * 删除模块
     * @param request
     * @return
     */
    @RequestMapping("/removeModule")
    @ResponseBody
    public Map<String, Object> removeModule(HttpServletRequest request) {
        Map<String, Object> result = null;
        List<Module> Modules = null;
        try {
            EsiJsonParamUtil<Module> eJPUtil = new EsiJsonParamUtil<>();
            Modules = eJPUtil.parseObjToList(request, Module.class);
            for (Module Module : Modules) {
                moduleManagerService.removeModule(Module);
            }
            result = genSuccessMsg(null, "删除模块成功", null);
        } catch (Exception e) {
            e.printStackTrace();
            EsiLogUtil.error(getLog(), e.getMessage());
            result = genFaultMsg(null, "删除失败", null);
        }
        return result;
    }


}
