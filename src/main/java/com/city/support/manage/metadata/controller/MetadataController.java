package com.city.support.manage.metadata.controller;

import com.city.common.controller.BaseController;
import com.city.common.pojo.Constant;
import com.city.common.pojo.Page;
import com.city.common.util.EsiJsonParamUtil;
import com.city.support.manage.metadata.entity.*;
import com.city.support.manage.metadata.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by wgx on 2016/1/14.
 */
@Controller
@RequestMapping("/support/manage/metadata")
public class MetadataController extends BaseController {
    @Autowired
    MetadataInfoService metadataInfoService;
    @Autowired
    MetadataTypeService metadataTypeService;

    /**
     * 进入系统元数据管理页面
     *
     * @return
     */
    @RequestMapping("/metadataConfig")
    public String metadataConfig() {
        return "support/manage/metadata/metadataConfig";
    }

    /**
     * 获取所有系统元数据类型
     *
     * @return
     */
    @RequestMapping("/getAllMetadataTypes")
    @ResponseBody
    public Page getAllMetadataTypes(Page page) {
        List<MetadataType> metadataTypeList = metadataTypeService.findAll();
        page.setDatas(metadataTypeList);
        return page;
    }

    /**
     * 保存系统元数据类型
     *
     * @param metadataType
     * @return
     */
    @RequestMapping("/saveMetadataType")
    @ResponseBody
    public Map<String, Object> saveMetadataType(MetadataType metadataType) {
        Map<String, Object> result;
        try {
            List<MetadataType> metadataTypeList = metadataTypeService.findMetadataTypeByName(metadataType.getName());
            if(metadataTypeList.size()==0) {
                metadataTypeService.save(metadataType);
                result = genSuccessMsg(metadataType, "保存成功", 200);
            }else{
                result = genFaultMsg(null, "名称已经存在！", 500);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = genFaultMsg(null, "保存失败", 500);
        }
        return result;
    }

    /**
     * 修改系统元数据类型
     *
     * @param request
     * @return
     */
    @RequestMapping("/updateMetadataType")
    @ResponseBody
    public Map<String, Object> updateMetadataType1(HttpServletRequest request) {
        Map<String, Object> result;
        List<MetadataType> metadataTypeList = null;
        EsiJsonParamUtil<MetadataType> paramUtil = new EsiJsonParamUtil<>();
        try {
            metadataTypeList = paramUtil.parseObjToList(request,MetadataType.class);
            if(metadataTypeList!=null){
                int reqResult = metadataTypeService.update(metadataTypeList);
                if (reqResult == Constant.RequestResult.SUCCESS) {
                    result = genSuccessMsg(null, "更新成功", 200);
                } else if (reqResult == Constant.RequestResult.EXIST) {
                    result = genFaultMsg(null, "名称已经存在！", null);
                } else {
                    result = genFaultMsg(null, "更新失败", null);
                }
            }else{
                result = genFaultMsg(null, "更新失败", 500);
            }

        } catch (Exception e) {
            e.printStackTrace();
            result = genFaultMsg(null, "更新失败", 500);
        }
        return result;
    }
    /**
     * 批量删除系统元数据类型
     *
     * @param request
     * @return
     */
    @RequestMapping("/batchDeleteMetadataTypes")
    @ResponseBody
    public Map<String, Object> batchDeleteMetadataTypes1(HttpServletRequest request) {
        Map<String, Object> result;
        List<MetadataType> metadataTypeList = null;
        EsiJsonParamUtil<MetadataType> paramUtil = new EsiJsonParamUtil<>();
        try {
            metadataTypeList = paramUtil.parseObjToList(request,MetadataType.class);
            if(metadataTypeList!=null){
                metadataTypeService.batchDelete(metadataTypeList);
                result = genSuccessMsg(null, "删除成功", 200);
            }else{
                result = genFaultMsg(null, "删除失败", 500);
            }

        } catch (Exception e) {
            e.printStackTrace();
            result = genFaultMsg(null, "删除失败", 500);
        }
        return result;
    }
    /**
     * 进入相应类型的系统元数据管理页面
     *
     * @return
     */
    @RequestMapping("/metadataInfoConfig")
    public ModelAndView metadataInfoConfig(Integer id) {
        ModelAndView mv_b = new ModelAndView("support/manage/metadata/infoPage/blankPage");
        if (id == null) {
            return mv_b;
        }
        MetadataType metadataType = metadataTypeService.findById(id);
        if (metadataType == null || metadataType.getInfoPage() == null) {
            return mv_b;
        }
        ModelAndView mv = new ModelAndView("support/manage/metadata/infoPage/" + metadataType.getInfoPage());
        mv.addObject("type", metadataType.getId());
        mv.addObject("name", metadataType.getName());
        return mv;
    }

    /**
     * 根据元数据类型获取所有系统元数据
     *
     * @return
     */
    @RequestMapping("/getAllMetadataInfosByType")
    @ResponseBody
    public Page getAllMetadataInfosByType(Integer type, Page page) {
        List<MetadataInfo> metadataInfoList = metadataInfoService.findByType(type, page);
        page.setDatas(metadataInfoList);
        if (metadataInfoList != null) {
            page.setTotal(metadataInfoService.findByType(type, null).size());
        }
        return page;
    }

    /**
     * 根据元数据类型获取所有系统元数据
     *
     * @param sortType    排序方式,如果为1,则正序排列,为-1则倒叙排列,默认正序
     * @param beginItem   是否显示”一直有效“
     * @return
     */
    @RequestMapping("/getAllYears")
    @ResponseBody
    public List<Map<String, Object>> getAllYears(Integer sortType,Integer beginItem) {
        return metadataInfoService.getAllYears(sortType,beginItem);
    }

    /**
     * 查询所有职务
     *
     * @return
     */
    @RequestMapping("/getAllDuties")
    @ResponseBody
    public List<MetadataInfo> getAllDuties() {
        return metadataInfoService.findByType(Constant.systemConfigPojo.getDutyType(), null);
    }

    /**
     * 保存系统元数据类型
     *
     * @param metadataInfo
     * @return
     */
    @RequestMapping("/saveMetadataInfo")
    @ResponseBody
    public Map<String, Object> saveMetadataInfo(MetadataInfo metadataInfo) {
        Map<String, Object> result;
        try {
            if(metadataInfo.getType()!=null) {
                List<MetadataInfo> metadataInfoList = metadataInfoService.getByTypeAndName(metadataInfo.getType(), metadataInfo.getName());
                if (metadataInfoList.size() == 0) {
                    metadataInfoService.save(metadataInfo);
                    result = genSuccessMsg(metadataInfo, "保存成功", 200);
                } else {
                    result = genFaultMsg(null, "名称已存在！", 500);
                }
            }else{
                result = genFaultMsg(null, "保存失败", 500);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = genFaultMsg(null, "保存失败", 500);
        }
        return result;
    }

    /**
     * 修改系统元数据类型
     *
     * @param request
     * @return
     */
    @RequestMapping("/updateMetadataInfo")
    @ResponseBody
    public Map<String, Object> updateMetadataInfo1(HttpServletRequest request) {
        Map<String, Object> result;
        List<MetadataInfo> metadataInfoList = null;
        EsiJsonParamUtil<MetadataInfo> paramUtil = new EsiJsonParamUtil<>();
        try {
            metadataInfoList = paramUtil.parseObjToList(request,MetadataInfo.class);

            if(metadataInfoList!=null){
                int reqResult = metadataInfoService.update(metadataInfoList);
                if (reqResult == Constant.RequestResult.SUCCESS) {
                    result = genSuccessMsg(null, "更新成功", 200);
                } else if (reqResult == Constant.RequestResult.EXIST) {
                    result = genFaultMsg(null, "名称已经存在！", null);
                } else {
                    result = genFaultMsg(null, "保存失败", null);
                }
            }else{
                result = genSuccessMsg(null, "更新失败", 500);
            }


        } catch (Exception e) {
            e.printStackTrace();
            result = genFaultMsg(null, "更新失败", 500);
        }
        return result;
    }

    /**
     * 批量删除系统元数据信息
     *
     * @param request 系统元数据信息list集合
     * @return
     */
    @RequestMapping("/batchDeleteMetadataInfos")
    @ResponseBody
    public Map<String, Object> batchDeleteMetadataInfos1(HttpServletRequest request) {
        Map<String, Object> result;
        List<MetadataInfo> metadataInfoList = null;
        EsiJsonParamUtil<MetadataInfo> paramUtil = new EsiJsonParamUtil<>();
        try {
            metadataInfoList = paramUtil.parseObjToList(request,MetadataInfo.class);
            if(metadataInfoList!=null){
                metadataInfoService.batchDelete(metadataInfoList);
                result = genSuccessMsg(null, "删除成功", 200);
            }else{
                result = genSuccessMsg(null, "删除失败", 500);
            }


        } catch (Exception e) {
            e.printStackTrace();
            result = genFaultMsg(null, "删除失败", 500);
        }
        return result;
    }
    /**
     * 返回地区等级
     *
     * @return
     * @author hzc
     * @createDate 2016-2-16
     */
    @RequestMapping("/getAreaLevel")
    @ResponseBody
    public Object getAreaLevel() {
        return metadataInfoService.findByType(21, null);
    }
}
