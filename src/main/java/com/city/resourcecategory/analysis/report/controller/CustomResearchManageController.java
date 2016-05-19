package com.city.resourcecategory.analysis.report.controller;

import com.city.common.controller.BaseController;
import com.city.common.pojo.Page;
import com.city.common.util.EsiJsonParamUtil;
import com.city.common.util.SessionUtil;
import com.city.resourcecategory.analysis.report.entity.CustomResearchEntity;
import com.city.resourcecategory.analysis.report.entity.ResearchGroupEntity;
import com.city.resourcecategory.analysis.report.service.CustomResearchManageService;
import com.city.resourcecategory.analysis.report.service.CustomResearchService;
import com.city.support.dataSet.entity.DataSet;
import com.city.support.manage.pojo.DragAndDropVO;
import com.city.support.sys.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 自定义查询分组管理controller类
 * Created by HZC on 2016/2/22.
 */
@Controller
@RequestMapping("/resourcecategory/analysis/report/customResearchManage")
public class CustomResearchManageController extends BaseController {
    @Autowired
    private CustomResearchManageService manageService;

    @Autowired
    private CustomResearchService researchService;

    /**
     * 返回自定义查询管理主页
     *
     * @return
     */
    @RequestMapping("/toIndex")
    public String toIndex() {
        return "resourceCategory/analysis/report/groupManage";
    }

    /**
     * 返回自定义查询分组
     *
     * @return
     * @author hzc
     * @createDate 2016-2-22
     */
    @RequestMapping("/getResearchGroupTree")
    @ResponseBody
    public Object getResearchGroupTree(String name, Integer status) {
        return manageService.getResearchGroupTree(name, status);
    }

    /**
     * 删除自定义查询分组
     *
     * @param req
     * @return
     * @author hzc
     * @createDate 2016-2-24
     */
    @RequestMapping("/deleteResearchGroup")
    @ResponseBody
    public Object deleteResearchGroup(HttpServletRequest req) {
        String ids = req.getParameter("id");
        if (StringUtils.isEmpty(ids)) {
            return genFaultMsg("请求失败", "系统繁忙，请稍候再试", null);
        }

        try {
            manageService.deleteResearchGroups(Integer.parseInt(ids));
        } catch (Exception e) {
            return genFaultMsg("请求失败", "数据已绑定，请先解除绑定", null);
        }
        return genSuccessMsg("请求成功", "操作成功", null);
    }

    /**
     * 返回自定义查询报表
     * <pre>
     *     分页查询自定义插叙
     * </pre>
     *
     * @return
     * @author hzc
     * @createDate 2016-2-22
     */
    @RequestMapping("/getCustomResearchs")
    @ResponseBody
    public Page getCustomResearchs(Page page, Integer id, String name, boolean includeDownLevel) {
        return manageService.getCustomResearchsByGroups(page, id, name, includeDownLevel);
    }

    /**
     * 保存自定义查询分组
     *
     * @param entity
     * @return
     */
    @RequestMapping("/saveOrUpdateGroup")
    @ResponseBody
    public Object saveOrUpdateGroup(ResearchGroupEntity entity, HttpServletRequest req) {
        String trim = entity.getName().trim();
        if (StringUtils.isEmpty(trim)) {
            return genFaultMsg("请求错误", "请填写完整名称", null);
        }
        List<ResearchGroupEntity> group = manageService.getResearchGroupByName(trim);
        if (null != group && group.size() > 0) {
//            是否重复
            boolean isRepeat = true;
            if (null != entity.getId()) {
                for (ResearchGroupEntity rg : group) {
                    if (entity.getId() == rg.getId()) {
                        isRepeat = false;
                    }
                }
            }
            if (isRepeat) {
                return genFaultMsg("请求错误", "分组名称重复，不可用", null);
            }
        }
        Date date = new Date();
        User user = SessionUtil.getUser(req.getSession());
        Integer id = entity.getId();
        if (id != null && id > 0) {
//            更新
            entity.setUpdaterId(user.getId());
            entity.setUpdateDate(date);
        } else {
//            新建
            entity.setCreateDate(date);
            entity.setCreatorId(user.getId());
        }
        entity = manageService.saveOrUpdateGroup(entity);
        return genSuccessMsg(entity, "保存成功", null);
    }

    /**
     * 保存或者更新自定义查询
     *
     * @return
     * @author hzc
     * @createDate 2016-2-24
     */
    @RequestMapping("/saveCustomResearch")
    @ResponseBody
    public Object saveCustomResearch(CustomResearchEntity entity, HttpServletRequest request) {
        String name = entity.getName().trim();
        if (StringUtils.isEmpty(name)) {
            return genFaultMsg("请求失败", "请填写完整名称", null);
        }
        List<CustomResearchEntity> list = researchService.getCustomResearchByName(name);
        if (null != list && list.size() > 0) {
            boolean isRepeat = true;
            if (null != entity.getId()) {
                for (CustomResearchEntity rg : list) {
                    if (entity.getId() == rg.getId()) {
                        isRepeat = false;
                    }
                }
            }
            if (isRepeat) {
                return genFaultMsg("请求失败", "模板名称重复，不可用", null);
            }
        }

        String resourceId = request.getParameter("resourceId");
        if (!StringUtils.isEmpty(resourceId)) {
            DataSet dataSet = new DataSet();
            dataSet.setId(Integer.parseInt(resourceId));
            entity.setDataSet(dataSet);
        }
        Integer id = entity.getId();
        Date date = new Date();
        User user = SessionUtil.getUser(request.getSession());
        if (null != id && id > 0) {
//            更新
            entity.setUpdateDate(date);
            entity.setUpdaterId(user.getId());
        } else {
            entity.setCreateDate(date);
            entity.setCreatorId(user.getId());
        }
        CustomResearchEntity researchEntity = researchService.saveCustomResearch(entity);
        return genSuccessMsg(researchEntity, "保存成功", null);
    }

    /**
     * 删除自定义查询
     *
     * @return
     * @author hzc
     * @createDate 2016-2-25
     */
    @RequestMapping("/deleteCustomResearch")
    @ResponseBody
    public Object deleteCustomResearch(Integer id) {
        if (null == id) {
            return false;
        }
        researchService.deleteResearchById(id);
        return true;
    }

    /**
     * 保存报表分组排序
     *
     * @param request
     * @return
     * @author hzc
     * @createDate 2016-4-29
     */
    @RequestMapping("/sortGroupIndex")
    @ResponseBody
    public Object sortGroupIndex(HttpServletRequest request) {
        Map<String, Object> result = null;
        EsiJsonParamUtil<ResearchGroupEntity> util = new EsiJsonParamUtil<>();
        try {
            List<ResearchGroupEntity> groups = util.parseObjToList(request, ResearchGroupEntity.class);
            manageService.saveGroupSorts(groups);
            result = genSuccessMsg("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            result = genFaultMsg("保存失败,服务端运行异常!");
        }
        return result;
    }
}
