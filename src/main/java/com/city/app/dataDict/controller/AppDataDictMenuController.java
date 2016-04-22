package com.city.app.dataDict.controller;

import com.city.app.dataDict.entity.AppDataDictMenu;
import com.city.app.dataDict.service.AppDataDictMenuService;
import com.city.common.controller.BaseController;
import com.city.common.util.EsiJsonParamUtil;
import com.city.support.dataSet.query.pojo.TimePojo;
import com.city.support.sys.user.entity.Role;
import com.city.support.sys.user.entity.User;
import com.city.support.sys.user.pojo.CurrentUser;
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
 * Created by wxl on 2016/3/21.
 */
@Controller
@RequestMapping("/app/dataDict")
public class AppDataDictMenuController extends BaseController {
    @Autowired
    private AppDataDictMenuService appDataDictMenuService;

    private Gson gson = new Gson();

    @RequestMapping("/manageJsp")
    public ModelAndView manageJsp() {
        ModelAndView mv = new ModelAndView("/app/dataDict/manage");
        return mv;
    }

    /**
     * 查询数据字典目录树
     */
    @RequestMapping("/queryDictMenus")
    @ResponseBody
    public List<AppDataDictMenu> queryDictMenus(HttpServletRequest request) {
        User user = CurrentUser.getCurrentUser(request).getUser();
        return appDataDictMenuService.queryDictMenus(user, request.getContextPath());
    }

    /**
     * 查询数据字典目录树,根据名字和节点查询
     */
    @RequestMapping("/queryDictMenuTree")
    @ResponseBody
    public Map<String, Object> queryDictMenuTree(HttpServletRequest request, Integer nodeId, String name) {
        User user = CurrentUser.getCurrentUser(request).getUser();
        return appDataDictMenuService.queryDictMenus(user, nodeId, name, request.getContextPath());
    }

    /**
     * 复制目录
     *
     * @param fromId    拖拽的节点
     * @param toId      拖拽到的节点
     * @param sortIndex 顺序
     */
    @RequestMapping("/copyDictMenus")
    @ResponseBody
    public Map<String, Object> copyDictMenus(Integer fromId, Integer toId, Integer sortIndex) {
        Map<String, Object> result = null;
        try {
            appDataDictMenuService.copyDictMenus(fromId, toId, sortIndex);
            result = genSuccessMsg("复制成功!");
        } catch (Exception e) {
            e.printStackTrace();
            result = genFaultMsg("复制失败,服务端异常!");
        }
        return result;
    }

    /**
     * 保存目录:多个,用于排序
     *
     * @param request
     */
    @RequestMapping("/saveDictMenus")
    @ResponseBody
    public Map<String, Object> saveDictMenus(HttpServletRequest request) {
        Map<String, Object> result = null;
        EsiJsonParamUtil<AppDataDictMenu> util = new EsiJsonParamUtil<>();
        try {
            List<AppDataDictMenu> menus = util.parseObjToList(request, AppDataDictMenu.class);
            appDataDictMenuService.saveDictMenu(menus);
            result = genSuccessMsg("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            result = genFaultMsg("保存失败,服务端运行异常!");
        }
        return result;
    }

    /**
     * 保存目录:单个,用于保存和修改
     *
     * @param menu
     */
    @RequestMapping("/saveDictMenu")
    @ResponseBody
    public Map<String, Object> saveDictMenu(AppDataDictMenu menu, HttpServletRequest request) {
        Map<String, Object> result = null;
        try {
            appDataDictMenuService.saveDictMenu(menu);
            menu.setIconByPath(request.getContextPath());
            result = genSuccessMsg(gson.toJson(menu), "保存成功", 200);
        } catch (Exception e) {
            e.printStackTrace();
            result = genFaultMsg("保存失败,服务端运行异常!");
        }
        return result;
    }

    /**
     * 删除目录
     */
    @RequestMapping("/deleteDictMenus")
    @ResponseBody
    public Map<String, Object> deleteDictMenus(HttpServletRequest request) {
        Map<String, Object> result = null;
        EsiJsonParamUtil<AppDataDictMenu> util = new EsiJsonParamUtil<>();
        try {
            List<AppDataDictMenu> menus = util.parseObjToList(request, AppDataDictMenu.class);
            appDataDictMenuService.deleteDictMenus(menus);
            result = genSuccessMsg("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            result = genFaultMsg("删除失败,服务端运行异常!");
        }
        return result;
    }

    /**
     * 获取角色
     * 若为管理员用户,则获取所有角色
     * 否则,获取所关联的角色
     */
    @RequestMapping("/getRoles")
    @ResponseBody
    public List<Role> getRoles(HttpServletRequest request, String name) {
        User user = CurrentUser.getCurrentUser(request).getUser();//当前用户
        return appDataDictMenuService.getRoles(user, name);
    }

    /**
     * 返回所有时间
     *
     * @param menuId 父节点
     */
    @RequestMapping("/getRptTimes")
    @ResponseBody
    public List<TimePojo> getRptTimes(Integer menuId) {
        return appDataDictMenuService.queryRptTime(menuId);
    }

    /**
     * 根据目录获取数据
     *
     * @param menuId 父节点
     * @param time   时间
     */
    @RequestMapping("/getRptDatas")
    @ResponseBody
    public Map<Integer, Map<String, Object>> getRptDatas(Integer menuId, TimePojo time) {
        return appDataDictMenuService.queryRptData(menuId, time);
    }

}
