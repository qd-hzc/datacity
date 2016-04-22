package com.city.resourcecategory.analysis.report.controller;

import com.city.common.controller.BaseController;
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
    public Object getResearchGroupTree(HttpServletRequest request) {
        String id = request.getParameter("node");
        if (StringUtils.isEmpty(id)) {
            id = "0";
        }
        return manageService.getResearchGroupTree(Integer.parseInt(id));
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
    public Object getCustomResearchs(HttpServletRequest request) {
        String id = request.getParameter("id");
        if (StringUtils.isEmpty(id)) {
            return null;
        }
        List<CustomResearchEntity> list = researchService.getCustomResearchsByGroupId(Integer.parseInt(id));
        return list;
    }

    /**
     * 自定义查询分组排序
     *
     * @return
     */
    @RequestMapping("/sortResearchGroup")
    @ResponseBody
    public Object sortResearchGroup(DragAndDropVO vo) {
        return manageService.saveDragAndDrop(vo);
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
        if (StringUtils.isEmpty(entity.getName())) {
            return genFaultMsg("请求错误", "系统繁忙，请稍候再试", null);
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
}
