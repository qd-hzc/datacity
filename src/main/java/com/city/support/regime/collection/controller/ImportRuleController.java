package com.city.support.regime.collection.controller;

import com.city.common.controller.BaseController;
import com.city.common.util.EsiJsonParamUtil;
import com.city.common.util.EsiLogUtil;
import com.city.support.regime.collection.entity.ExcelMap;
import com.city.support.regime.collection.entity.ImportRule;
import com.city.support.regime.collection.service.ImportRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by wys on 2016/2/2.
 */
@Controller
@RequestMapping("/support/regime/collection")
public class ImportRuleController extends BaseController {
    @Autowired
    private ImportRuleService importRuleService;

    @RequestMapping("/importRuleConfig")
    public ModelAndView configImportRule() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("/support/regime/collection/importRuleConfig");
        return mv;
    }

    @RequestMapping("/queryImportRule")
    @ResponseBody
    public Map<String, Object> queryImportRule() {
        Map<String, Object> result = null;
        List<ImportRule> datas = null;
        try {
            datas = importRuleService.queryAllImportRule();
            result = genSuccessMsg(datas, "获取成功", null);
        } catch (Exception e) {
            e.printStackTrace();
            EsiLogUtil.error(getLog(), e.getMessage());
            result = genFaultMsg(null, "获取失败", null);
        }
        return result;
    }

    @RequestMapping("/addImportRule")
    @ResponseBody
    public Map<String, Object> addImportRule(HttpServletRequest request) {
        Map<String, Object> result = null;
        EsiJsonParamUtil<ImportRule> esiJsonParamUtil = new EsiJsonParamUtil<>();
        ImportRule importRule = null;
        try {
            importRule = esiJsonParamUtil.parseObj(request, ImportRule.class);
            importRuleService.addImportRule(importRule);
            result = genSuccessMsg(importRule, "添加成功", null);
        } catch (Exception e) {
            e.printStackTrace();
            EsiLogUtil.error(getLog(), e.getMessage());
            result = genFaultMsg(null, "添加失败", null);
        }
        return result;
    }

    @RequestMapping("updateImportRule")
    @ResponseBody
    public Map<String, Object> updateImportRule(HttpServletRequest request) {
        Map<String, Object> result = null;
        EsiJsonParamUtil<ImportRule> esiJsonParamUtil = new EsiJsonParamUtil<>();
        ImportRule importRule = null;
        try {
            importRule = esiJsonParamUtil.parseObj(request, ImportRule.class);
            importRuleService.updateImportRule(importRule);
            result = genSuccessMsg(null, "修改成功", null);
        } catch (Exception e) {
            e.printStackTrace();
            EsiLogUtil.error(getLog(), e.getMessage());
            result = genFaultMsg(null, "修改失败", null);
        }
        return result;
    }

    @RequestMapping("delImportRule")
    @ResponseBody
    public Map<String, Object> delImportRule(HttpServletRequest request) {
        Map<String, Object> result = null;
        EsiJsonParamUtil<ImportRule> esiJsonParamUtil = new EsiJsonParamUtil<>();
        List<ImportRule> importRuleList = null;
        try {
            importRuleList = esiJsonParamUtil.parseObjToList(request, ImportRule.class);
            importRuleService.delImportRule(importRuleList);
            result = genSuccessMsg(null, "删除成功", null);
        } catch (Exception e) {
            e.printStackTrace();
            EsiLogUtil.error(getLog(), e.getMessage());
            result = genFaultMsg(null, "删除失败", null);
        }
        return result;
    }

    @RequestMapping("/queryExcelMap")
    @ResponseBody
    public Map<String, Object> queryExcelmap(Integer importRuleId) {
        Map<String, Object> result = null;
        List<ImportRule> datas = null;
        try {
            datas = importRuleService.queryExcelMap(importRuleId);
            result = genSuccessMsg(datas, "获取成功", null);
        } catch (Exception e) {
            e.printStackTrace();
            EsiLogUtil.error(getLog(), e.getMessage());
            result = genFaultMsg(null, "获取失败", null);
        }
        return result;
    }

    @RequestMapping("/addExcelmap")
    @ResponseBody
    public Map<String, Object> addExcelmap(HttpServletRequest request) {
        Map<String, Object> result = null;
        EsiJsonParamUtil<ExcelMap> esiJsonParamUtil = new EsiJsonParamUtil<>();
        ExcelMap excelMap = null;
        Integer importRuleId = null;
        String importRuleIdStr = null;
        try {
            excelMap = esiJsonParamUtil.parseObj(request, ExcelMap.class);
            importRuleIdStr = esiJsonParamUtil.getParam(request, "importRuleId");
            if (importRuleIdStr != null && !"".equals(importRuleIdStr)) {
                importRuleId = Integer.parseInt(importRuleIdStr);
                importRuleService.addExcelMap(importRuleId, excelMap);
                if (excelMap.getId() != null)
                    result = genSuccessMsg(excelMap, "添加成功", null);
                else
                    result = genFaultMsg(null, "重复的报表", null);
            } else {
                result = genFaultMsg(null, "添加失败", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            EsiLogUtil.error(getLog(), e.getMessage());
            result = genFaultMsg(null, "添加失败", null);
        }
        return result;
    }

    @RequestMapping("updateExcelMap")
    @ResponseBody
    public Map<String, Object> updateExcelMap(HttpServletRequest request) {
        Map<String, Object> result = null;
        EsiJsonParamUtil<ExcelMap> esiJsonParamUtil = new EsiJsonParamUtil<>();
        ExcelMap excelMap = null;
        try {
            excelMap = esiJsonParamUtil.parseObj(request, ExcelMap.class);
            importRuleService.updateExcelMap(excelMap);
            result = genSuccessMsg(null, "修改成功", null);
        } catch (Exception e) {
            e.printStackTrace();
            EsiLogUtil.error(getLog(), e.getMessage());
            result = genFaultMsg(null, "修改失败", null);
        }
        return result;
    }

    @RequestMapping("delExcelMap")
    @ResponseBody
    public Map<String, Object> delExcelMap(HttpServletRequest request) {
        Map<String, Object> result = null;
        EsiJsonParamUtil<ExcelMap> esiJsonParamUtil = new EsiJsonParamUtil<>();
        List<ExcelMap> excelMapList = null;
        Integer importRuleId = null;
        String importRuleIdStr = null;
        try {
            excelMapList = esiJsonParamUtil.parseObjToList(request, ExcelMap.class);
            importRuleIdStr = esiJsonParamUtil.getParam(request, "importRuleId");
            if (importRuleIdStr != null && !"".equals(importRuleIdStr)) {
                importRuleId = Integer.parseInt(importRuleIdStr);
                importRuleService.delExcelMap(importRuleId, excelMapList);
                result = genSuccessMsg(null, "删除成功", null);
            } else {
                result = genFaultMsg(null, "删除失败", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            EsiLogUtil.error(getLog(), e.getMessage());
            result = genFaultMsg(null, "删除失败", null);
        }
        return result;
    }
}
