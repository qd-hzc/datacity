package com.city.resourcecategory.analysis.chart.controller;

import com.city.common.controller.BaseController;
import com.city.common.pojo.Constant;
import com.city.common.util.EsiJsonParamUtil;
import com.city.common.util.EsiLogUtil;
import com.city.common.util.SessionUtil;
import com.city.resourcecategory.analysis.chart.entity.AnalysisChartBase;
import com.city.resourcecategory.analysis.chart.entity.AnalysisChartInfo;
import com.city.resourcecategory.analysis.chart.entity.AnalysisChartStructure;
import com.city.resourcecategory.analysis.chart.service.BasicChartConfigService;
import com.city.resourcecategory.analysis.chart.service.BasicChartStructureService;
import com.city.resourcecategory.analysis.common.entity.TimeRangeEntity;
import com.city.resourcecategory.analysis.common.service.TimeRangeService;
import com.city.support.manage.item.entity.ItemCaliber;
import com.city.support.regime.report.entity.ReportTemplate;
import com.city.support.sys.user.entity.Department;
import com.city.support.sys.user.entity.User;
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
 * Created by wys on 2016/2/29.
 */
@Controller
@RequestMapping("/support/resourcecategory/analysis/chart")
public class BasicChartDesignController extends BaseController {

    @Autowired
    private BasicChartStructureService basicChartStructureService;
    @Autowired
    private BasicChartConfigService basicChartConfigService;
    @Autowired
    private TimeRangeService timeRangeService;

    @RequestMapping("/chartDesign")
    public ModelAndView chartDesign(Integer chartId) {
        Gson gson = new Gson();
        ModelAndView mv = new ModelAndView();
        mv.setViewName("/resourceCategory/analysis/chart/background/chartDesign");
        AnalysisChartBase analysisChartBase = basicChartConfigService.queryAnalysisChartBaseById(chartId);
        //报送频率
        List<Map<String, Object>> fres = Constant.FrequencyType.getAllForArray(analysisChartBase.getPeriodType());
        mv.addObject("fres", gson.toJson(fres));
        mv.addObject("periodType", analysisChartBase.getPeriodType());
        mv.addObject("chartId", chartId);
        //元数据类型
        List<Map<String, Object>> metadataTypes = Constant.MetadataType.getAllTypeForArray();
        mv.addObject("metadataTypes", gson.toJson(metadataTypes));
        return mv;
    }
    @RequestMapping("/test")
    public ModelAndView chartTest(Integer chartId) {
        Gson gson = new Gson();
        ModelAndView mv = new ModelAndView();
        mv.setViewName("/resourceCategory/analysis/chart/test");
        mv.addObject("chartId", chartId);
        return mv;
    }

    @RequestMapping("/test1")
    public ModelAndView chartTest1(Integer chartId) {
        Gson gson = new Gson();
        ModelAndView mv = new ModelAndView();
        mv.setViewName("/resourceCategory/analysis/chart/test1");
        mv.addObject("chartId", chartId);
        return mv;
    }
    @RequestMapping("/test2")
    public ModelAndView chartTest2(Integer chartId) {
        Gson gson = new Gson();
        ModelAndView mv = new ModelAndView();
        mv.setViewName("/resourceCategory/analysis/chart/test2");
        mv.addObject("chartId", chartId);
        return mv;
    }

    @RequestMapping("/test4")
    public ModelAndView chartTest4(Integer chartId) {
        Gson gson = new Gson();
        ModelAndView mv = new ModelAndView();
        mv.setViewName("/resourceCategory/analysis/chart/test4");
        mv.addObject("chartId", chartId);
        return mv;
    }
    @RequestMapping("/queryChartStructure")
    @ResponseBody
    public Map<String, Object> queryChartStructure(Integer chartId, Integer structureType) {
        Map<String, Object> result = null;
        List<AnalysisChartStructure> datas = null;
        try {
            datas = basicChartStructureService.queryChartStructure(chartId, structureType);
            result = genSuccessMsg(datas, "查询成功", null);
        } catch (Exception e) {
            e.printStackTrace();
            EsiLogUtil.error(getLog(), e.getMessage());
            result = genFaultMsg("查询失败");
        }
        return result;
    }

    @RequestMapping("/updateChartStructure")
    @ResponseBody
    public Map<String, Object> updateChartStructure(HttpServletRequest request) {
        Map<String, Object> result = null;
        List<AnalysisChartStructure> datas = null;
        try {
            EsiJsonParamUtil<AnalysisChartStructure> jp = new EsiJsonParamUtil<>();
            datas = jp.parseObjToList(request, AnalysisChartStructure.class);
            datas = basicChartStructureService.updateChartStructure(datas);
            result = genSuccessMsg(datas, "操作成功", null);
        } catch (Exception e) {
            e.printStackTrace();
            EsiLogUtil.error(getLog(), e.getMessage());
            result = genFaultMsg("操作失败");
        }
        return result;
    }

    @RequestMapping("/delChartStructure")
    @ResponseBody
    public Map<String, Object> delChartStructure(HttpServletRequest request, Boolean drag) {
        Map<String, Object> result = null;
        List<AnalysisChartStructure> datas = null;

        try {
            EsiJsonParamUtil<AnalysisChartStructure> jp = new EsiJsonParamUtil<>();
            datas = jp.parseObjToList(request, AnalysisChartStructure.class);
            basicChartStructureService.delChartStructure(datas, drag);
            result = genSuccessMsg("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            EsiLogUtil.error(getLog(), e.getMessage());
            result = genFaultMsg("操作失败");
        }
        return result;
    }

    @RequestMapping("/queryChartInfo")
    @ResponseBody
    public Map<String, Object> queryChartInfo(Integer chartId, Integer infoType) {
        Map<String, Object> result = null;
        List<AnalysisChartInfo> datas = null;
        try {
            datas = basicChartStructureService.queryChartInfo(chartId, infoType);
            result = genSuccessMsg(datas, "查询成功", null);
        } catch (Exception e) {
            e.printStackTrace();
            EsiLogUtil.error(getLog(), e.getMessage());
            result = genFaultMsg("查询失败");
        }
        return result;
    }

    @RequestMapping("/updateChartInfo")
    @ResponseBody
    public Map<String, Object> updateChartInfo(HttpServletRequest request) {
        Map<String, Object> result = null;
        List<AnalysisChartInfo> datas = null;
        try {
            EsiJsonParamUtil<AnalysisChartInfo> jp = new EsiJsonParamUtil<>();
            datas = jp.parseObjToList(request, AnalysisChartInfo.class);
            datas = basicChartStructureService.updateChartInfo(datas);
            result = genSuccessMsg(datas, "操作成功", null);
        } catch (Exception e) {
            e.printStackTrace();
            EsiLogUtil.error(getLog(), e.getMessage());
            result = genFaultMsg("操作失败");
        }
        return result;
    }

    @RequestMapping("/getItemCaliber")
    @ResponseBody
    public List<ItemCaliber> getItemCaliber(Integer itemId) {
        if (itemId == null) {
            return null;
        }
        return basicChartStructureService.getItemCaliberByItemId(itemId);
    }

    @RequestMapping("/getItemDep")
    @ResponseBody
    public List<Department> getItemDep(Integer itemId) {
        if (itemId == null) {
            return null;
        }
        return basicChartStructureService.getItemDepByItemId(itemId);
    }

    @RequestMapping("/getItemReportTemplate")
    @ResponseBody
    public List<ReportTemplate> getItemReportTemplate(Integer itemId) {
        if (itemId == null) {
            return null;
        }
        return basicChartStructureService.getItemReportInfoByItemId(itemId);
    }

    @RequestMapping("/delChartInfo")
    @ResponseBody
    public Map<String, Object> delChartInfo(HttpServletRequest request) {
        Map<String, Object> result = null;
        List<AnalysisChartInfo> datas = null;
        try {
            EsiJsonParamUtil<AnalysisChartInfo> jp = new EsiJsonParamUtil<>();
            datas = jp.parseObjToList(request, AnalysisChartInfo.class);
            basicChartStructureService.delChartInfo(datas);
            result = genSuccessMsg("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            EsiLogUtil.error(getLog(), e.getMessage());
            result = genFaultMsg("操作失败");
        }
        return result;
    }

    @RequestMapping("/updateChartStructureTimeData")
    @ResponseBody
    public Map<String, Object> updateChartStructureTimeData(HttpServletRequest request, Integer foreignType, Integer foreignId) {
        Map<String, Object> result = null;
        List<TimeRangeEntity> datas = null;
        try {
            EsiJsonParamUtil<TimeRangeEntity> jp = new EsiJsonParamUtil<>();
            datas = jp.parseObjToList(request, TimeRangeEntity.class);
            String str = new Gson().toJson(datas);
            User user = (User) SessionUtil.getUser(request.getSession());
            timeRangeService.saveTimeRange(str, foreignType, foreignId, user);
            result = genSuccessMsg(null, "操作成功", null);
        } catch (Exception e) {
            e.printStackTrace();
            EsiLogUtil.error(getLog(), e.getMessage());
            result = genFaultMsg("操作失败");
        }
        return result;
    }
}
