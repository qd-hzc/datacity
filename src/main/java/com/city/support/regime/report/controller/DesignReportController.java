package com.city.support.regime.report.controller;

import com.city.common.controller.BaseController;
import com.city.common.event.EsiEvent;
import com.city.common.event.watcher.DepWatched;
import com.city.common.event.watcher.EsiEventWatched;
import com.city.common.event.watcher.SystemLogWatched;
import com.city.common.pojo.Constant;
import com.city.common.util.EsiLogUtil;
import com.city.common.util.table.builder.GenSynthesisStrUtil;
import com.city.common.util.table.builder.GenTableStrUtil;
import com.city.common.util.table.pojo.EsiTable;
import com.city.support.regime.report.entity.ReportTemplateStyle;
import com.city.support.regime.report.pojo.MetaDataPojo;
import com.city.support.regime.report.service.DesignReportService;
import com.city.support.sys.log.entity.SystemLog;
import com.city.support.sys.log.service.SystemLogService;
import com.city.support.sys.user.pojo.CurrentUser;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 设计表样
 * Created by HZC on 2016/1/13.
 */
@Controller
@RequestMapping("/support/regime/report/designReport")
public class DesignReportController extends BaseController {

    final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DesignReportService designReportService;

    @RequestMapping("/index")
    public String showIndex() {
        return "support/regime/report/index";
    }

    /**
     * 返回报表设计主页
     *
     * @return
     * @author hzc
     * @createDate 2016-1-13
     */
    @RequestMapping("/showReportDesign")
    public ModelAndView showReportDesign(HttpServletRequest request, Integer styleId) {
        Gson gson = new Gson();
        ModelAndView mv = new ModelAndView("support/regime/report/designReport");
        ReportTemplateStyle style = designReportService.getRptStyleById(styleId);
        mv.addObject("style", gson.toJson(style));
        //表样类型
        List<Map<String, Object>> styleTypes = Constant.RptStyleType.getAllForArray();
        mv.addObject("styleTypes", gson.toJson(styleTypes));
        //源数据类型
        List<Map<String, Object>> metaDataTypes = Constant.MetadataType.getAllForArray();
        mv.addObject("metaDataTypes", gson.toJson(metaDataTypes));
        return mv;
    }

    /**
     * 获取分组树信息（时间框架，指标分组目录，统计对象）
     *
     * @return
     */
    @RequestMapping("/getGroupInfoTrees")
    @ResponseBody
    public Object getGroupInfoTrees() {
        return designReportService.getGroupInfoTrees();
    }

    /**
     * 获取指标树
     */
    @RequestMapping("/getItemTree")
    @ResponseBody
    public List<MetaDataPojo> getItemTree() {
        return designReportService.getItemTree(0);
    }

    /**
     * 返回主宾栏树信息
     *
     * @param styleId   表样id
     * @param isMainBar 是否是主栏,1:是主栏,0:是宾栏
     */
    @RequestMapping("/getReportBarTree")
    @ResponseBody
    public List<Map<String, Object>> getReportBarTree(Integer styleId, Integer isMainBar) {
        return designReportService.getBarInfoTrees(styleId, isMainBar, 0);
    }

    /**
     * 获取附加信息
     *
     * @param dataType     数据类型
     * @param dataInfo     附加信息id
     * @param dataInfoType 1表示dataInfo1,2表示dataInfo2
     */
    @RequestMapping("/getDataInfo")
    @ResponseBody
    public Object getDataInfo(Integer dataType, Integer dataInfo, Integer dataInfoType) {
        switch (dataType) {
            case Constant.MetadataType.ITEM://指标
                if (dataInfoType == 1) {//口径
                    return designReportService.getCaliberById(dataInfo);
                }
                return designReportService.getDepById(dataInfo);
            default:
                return null;
        }
    }

    /**
     * 获取指标口径
     *
     * @param request
     * @return
     * @author hzc
     * @createDate 2016-1-19
     */
    @RequestMapping("/getItemCaliber")
    @ResponseBody
    public Object getItemCaliber(HttpServletRequest request) {
        String itemId = request.getParameter("itemId");
        if (StringUtils.isEmpty(itemId)) {
            return null;
        }
        return designReportService.getItemCaliberByItemId(Integer.parseInt(itemId));
    }

    /**
     * 生成表样
     */
    @RequestMapping("/genTableStr")
    @ResponseBody
    public Map<String, Object> genTableStr(String data, HttpServletRequest request) {
        Gson gson = new Gson();
        EsiTable table = gson.fromJson(data, EsiTable.class);
        Map<String, Object> result = null;
        try {
            GenTableStrUtil util = new GenSynthesisStrUtil(table, request);
            String tableStr = util.genTable().toString();
            result = genSuccessMsg(tableStr, "操作成功", 200);
        } catch (Exception e) {
            result = genSuccessMsg(null, "操作失败", 500);
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 保存主宾蓝信息
     *
     * @param data    主宾蓝信息
     * @param styleId 表样id
     * @return
     */
    @RequestMapping("/saveBarInfo")
    @ResponseBody
    public Map<String, Object> saveBarInfo(String data, Integer styleId, HttpServletRequest request) {
        Gson gson = new Gson();
        EsiTable table = gson.fromJson(data, EsiTable.class);
        Map<String, Object> result = null;
        try {
            String tableStr = designReportService.saveBarInfos(CurrentUser.getCurrentUser(request).getUser(), table, styleId, request);
            result = genSuccessMsg(tableStr, "操作成功", 200);
        } catch (Exception e) {
            result = genSuccessMsg(null, "操作失败", 500);
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 保存报表设计表样
     *
     * @param content
     * @return
     * @author hzc
     * @createDate 2016-2-2
     */
    @RequestMapping("/saveReportContent")
    @ResponseBody
    public Object saveReportContent(HttpServletRequest request, String content, Integer styleId) {
        try {
            designReportService.saveReportStyle(CurrentUser.getCurrentUser(request).getUser(), content, styleId);
            return genSuccessMsg("保存成功", "请求成功", null);
        } catch (Exception e) {
            EsiLogUtil.error(log, e.getMessage());
            return genFaultMsg("保存失败", "请求失败", null);
        }
    }
}
