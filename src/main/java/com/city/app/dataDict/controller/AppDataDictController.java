package com.city.app.dataDict.controller;

import com.city.app.dataDict.entity.AppDataDict;
import com.city.app.dataDict.service.AppDataDictMenuService;
import com.city.app.dataDict.service.AppDataDictService;
import com.city.common.controller.BaseController;
import com.city.common.util.EsiJsonParamUtil;
import com.city.support.sys.user.pojo.CurrentUser;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wxl on 2016/3/23.
 */
@Controller
@RequestMapping("/app/dataDict")
public class AppDataDictController extends BaseController {
    @Autowired
    private AppDataDictMenuService appDataDictMenuService;
    @Autowired
    private AppDataDictService appDataDictService;

    private Gson gson = new Gson();

    /**
     * 查询数据字典
     *
     * @param name             名
     * @param menuId           目录
     * @param includeDownLevel 索要查询的分组是否包含下级
     */
    @RequestMapping("/queryDicts")
    @ResponseBody
    public List<AppDataDict> queryDicts(HttpServletRequest request, String name, Integer status, Integer menuId, boolean includeDownLevel) {
        List<Integer> menuIds;
        if (includeDownLevel) {
            String roleIds = appDataDictMenuService.getRoles(CurrentUser.getCurrentUser(request).getUser());
            menuIds = appDataDictService.queryDownMenus(menuId, roleIds);
        } else {
            menuIds = new ArrayList<>();
            menuIds.add(menuId);
        }
        return appDataDictService.queryDicts(name, menuIds, status);
    }

    /**
     * 根据分组名划分
     *
     * @param menuId 目录id
     */
    @RequestMapping("/queryDictsForGroup")
    @ResponseBody
    public Map<String, List<AppDataDict>> queryDictsForGroup(Integer menuId) {
        return appDataDictService.queryDictsForGroup(menuId);
    }

    @RequestMapping("/saveDict")
    @ResponseBody
    public Map<String, Object> saveDict(AppDataDict dict) {
        Map<String, Object> result;
        try {
            appDataDictService.saveDict(dict);
            result = genSuccessMsg(gson.toJson(dict), "保存成功", 200);
        } catch (Exception e) {
            e.printStackTrace();
            result = genFaultMsg("保存失败,服务端响应异常!");
        }
        return result;
    }

    @RequestMapping("/saveDicts")
    @ResponseBody
    public Map<String, Object> saveDicts(HttpServletRequest request) {
        Map<String, Object> result;
        EsiJsonParamUtil<AppDataDict> util = new EsiJsonParamUtil<>();
        try {
            List<AppDataDict> dicts = util.parseObjToList(request, AppDataDict.class);
            appDataDictService.saveDict(dicts);
            result = genSuccessMsg("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            result = genFaultMsg("保存失败,服务端响应异常!");
        }
        return result;
    }

    /**
     * 删除
     */
    @RequestMapping("/deleteDicts")
    @ResponseBody
    public Map<String, Object> deleteDicts(HttpServletRequest request) {
        Map<String, Object> result;
        EsiJsonParamUtil<AppDataDict> util = new EsiJsonParamUtil<>();
        try {
            List<AppDataDict> dicts = util.parseObjToList(request, AppDataDict.class);
            appDataDictService.deleteDicts(dicts);
            result = genSuccessMsg("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            result = genFaultMsg("操作失败,服务端响应异常!");
        }
        return result;
    }

}
