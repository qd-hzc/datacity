package com.city.resourcecategory.themes.controller;

import com.city.common.controller.BaseController;
import com.city.common.util.EsiJsonParamUtil;
import com.city.resourcecategory.themes.entity.ThemePage;
import com.city.resourcecategory.themes.entity.ThemePageContent;
import com.city.resourcecategory.themes.pojo.EsiTheme;
import com.city.resourcecategory.themes.service.ManageThemesQueryService;
import com.city.resourcecategory.themes.service.ManageThemesService;
import com.city.resourcecategory.themes.util.FormatThemeUtil;
import com.city.support.sys.user.entity.Role;
import com.city.support.sys.user.service.RoleManagerService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Created by cheruixue on 2016/3/10.
 */
@Controller
@RequestMapping("/resourcecategory/themes/manageThemesController")
public class ManageThemesController extends BaseController {

    @Autowired
    private ManageThemesService themesService;

    @Autowired
    private ManageThemesQueryService themesQueryService;

    @Autowired
    private RoleManagerService roleManagerService;

    /**
     * 跳转到模板管理
     *
     * @return
     * @author CRX
     * @createDate 2016-3-10
     */
    @RequestMapping("/toManageTheme")
    public ModelAndView toManageTheme() {
        ModelAndView mv = new ModelAndView("resourceCategory/themes/manage/manageTheme");
        return mv;
    }

    /**
     * 返回模版管理树
     *
     * @param request
     * @return
     * @author crx
     * @createDate 2016-3-10
     */
    @RequestMapping("/getManageThemeTree")
    @ResponseBody
    public Object getManageThemeTree(HttpServletRequest request) {
        String parentIdS = request.getParameter("node");
        int parentId = Integer.parseInt(parentIdS);
        List<ThemePage> list = themesService.getThemePagesByParentId(parentId);
        return list;
    }

    /**
     * 保存或者更新模板菜单
     *
     * @param page
     * @return
     * @author CRX
     * @createDate 2016-3-10
     */
    @RequestMapping("/saveOrUpdateManageTheme")
    @ResponseBody
    public Object saveOrUpdateManageTheme(ThemePage page) {
        String name = page.getName().trim();
        if (StringUtils.isEmpty(name)) {
            return genFaultMsg("请求失败", "名称不能为空", null);
        }
        page.setName(name);
        Integer parentId = page.getParentId();
//        判断是否名称重复
        List<ThemePage> list = themesService.getThemePagesByNameAndPId(name, parentId);
        if (list.size()==0){
            ThemePage themePage = themesService.saveOrUpdateManageTheme(page);
            return genSuccessMsg(themePage, "请求成功", null);

        }else {
            return  genFaultMsg("请求失败", "名称重复", null);
        }
    }

    /**
     * 删除模板菜单
     *
     * @param id
     * @return
     * @author CRX
     * @createDate 2016-3-10
     */
    @RequestMapping("/deleteThemePage")
    @ResponseBody
    public Object deleteThemePage(Integer id) {
        themesService.deleteThemePages(id);
        return genSuccessMsg("请求成功", "删除成功", null);
    }

    /**
     * 保存排序
     *
     * @param request
     * @return
     * @author hzc
     * @createDate 2016-4-29
     */
    @RequestMapping("/sortThemeIndex")
    @ResponseBody
    public Object sortThemeIndex(HttpServletRequest request) {
        Map<String, Object> result = null;
        EsiJsonParamUtil<ThemePage> util = new EsiJsonParamUtil<>();
        try {
            List<ThemePage> pages = util.parseObjToList(request, ThemePage.class);
            themesService.saveThemeSort(pages);
            result = genSuccessMsg("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            result = genFaultMsg("保存失败,服务端运行异常!");
        }
        return result;
    }

    /**
     * 返回所有的配置的主题
     *
     * @return
     * @author crx
     * @createDate 2016-3-11
     */
    @RequestMapping("/getThemes")
    @ResponseBody
    public Object getThemes() {
        List<String> configs = FormatThemeUtil.getThemeConfigs();
        LinkedList<EsiTheme> themes = new LinkedList<>();
        for (String config : configs) {
            EsiTheme theme = FormatThemeUtil.getThemeByConfig(config);
            themes.add(theme);
        }
        return genSuccessMsg(themes, "请求成功", null);
    }

    /**
     * 返回所有配置菜单
     * <pre>
     *     根据名称模糊查询
     * </pre>
     *
     * @param name
     * @return
     * @author crx
     * @createDate 2016-3-14
     */
    @RequestMapping("/getThemesByName")
    @ResponseBody
    public Object getThemesByName(String name) {
        List<ThemePage> list = themesQueryService.getThemesTreeByName(name);
        return themesQueryService.packageListToTree(list, !StringUtils.isEmpty(name));
    }

    /**
     * 保存模板菜单配置信息
     *
     * @param page
     * @return
     * @author crx
     * @createDate 2016-3-14
     */
    @RequestMapping("/saveThemeConfig")
    @ResponseBody
    public Object saveThemeConfig(ThemePage page, HttpServletRequest request) {
        String[] content = request.getParameterValues("content[]");
        Gson gson = new Gson();
        Type type = new TypeToken<HashSet<ThemePageContent>>() {
        }.getType();
        Set<ThemePageContent> list = gson.fromJson(Arrays.toString(content), type);
        page.setContents(list);
        ThemePage themePage = themesService.saveOrUpdate(page);
        return genSuccessMsg(themePage, "请求成功", null);
    }

    /**
     * 返回角色
     *
     * @return
     * @author crx
     * @createDate 2016-3-15
     */
    @RequestMapping("/getAllRoles")
    @ResponseBody
    public Object getAllRoles() {
        List<Role> all = roleManagerService.findAll();
        LinkedList<Map> maps = new LinkedList<>();
        for (int i = 0; i < all.size(); i++) {
            Role role = all.get(i);
            HashMap<String, Object> map = new HashMap<>();
            map.put("id", role.getId());
            map.put("name", role.getName());
            map.put("checked", false);
            map.put("leaf", true);
            maps.add(map);
        }
        return maps;
    }

    /**
     * 获取主题模板配置信息
     *
     * @param id
     * @return
     * @author crx
     * @createDate 2016-3-15
     */
    @RequestMapping("/getManageThemeConfig")
    @ResponseBody
    public Object getManageThemeConfig(Integer id) {
        ThemePage page = themesService.getThemePageById(id);
        return genSuccessMsg(page, "请求成功", null);
    }
}
