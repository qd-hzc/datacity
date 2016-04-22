package com.city.resourcecategory.analysis.report.controller;

import com.city.common.controller.BaseController;
import com.city.common.pojo.Constant;
import com.city.common.util.SessionUtil;
import com.city.common.util.table.builder.GenRCStrUtil;
import com.city.common.util.table.builder.GenTableStrUtil;
import com.city.common.util.table.pojo.EsiTable;
import com.city.resourcecategory.analysis.common.entity.TimeRangeEntity;
import com.city.resourcecategory.analysis.common.service.TimeRangeService;
import com.city.resourcecategory.analysis.report.entity.CustomResearchEntity;
import com.city.resourcecategory.analysis.report.service.CustomResearchService;
import com.city.resourcecategory.analysis.report.service.DesignCustomResearchService;
import com.city.support.dataSet.query.pojo.ResearchTimePojo;
import com.city.support.dataSet.query.pojo.TimePojo;
import com.city.support.dataSet.query.service.QueryRptService;
import com.city.support.dataSet.query.util.QueryConditionUtil;
import com.city.support.manage.metadata.service.MetadataInfoService;
import com.city.support.sys.user.entity.User;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 设计自定义查询
 * Created by HZC on 2016/2/25.
 */
@Controller
@RequestMapping("/resourcecategory/analysis/report/designCustomResearch")
public class DesignCustomResearchController extends BaseController {

    @Autowired
    private DesignCustomResearchService designResearchService;

    @Autowired
    private CustomResearchService researchService;

    @Autowired
    private TimeRangeService rangeService;

    @Autowired
    private MetadataInfoService metadataInfoService;

    @Autowired
    private QueryRptService queryRptService;

    /**
     * 打开设计自定义查询页面
     *
     * @param request
     * @return
     * @author hzc
     * @createDate 2016-2-25
     */
    @RequestMapping("/showDesignResearch")
    public ModelAndView showDesignResearch(HttpServletRequest request) {
        String customResearchId = request.getParameter("_cr");
        ModelAndView mv = new ModelAndView("resourceCategory/analysis/report/designResearch");
        Gson gson = new Gson();
        int id = Integer.parseInt(customResearchId);
        mv.addObject("research", gson.toJson(researchService.getCustomResearchById(id)));
        //表样类型
        mv.addObject("style", gson.toJson(researchService.getCustomResearchStyleByResearchId(id)));
        //源数据类型
        List<Map<String, Object>> metaDataTypes = Constant.MetadataType.getAllForArray();
        mv.addObject("metaDataTypes", gson.toJson(metaDataTypes));
//        时间范围
        List<TimeRangeEntity> rangeEntities = rangeService.queryTimeRange(Constant.TIMERANGE.TYPE_REPORT, id);
        mv.addObject("timeRanges", gson.toJson(rangeEntities));
        return mv;
    }

    /**
     * 生成表样
     *
     * @mender hzc
     * @modifiedDate 2016-3-3
     */
    @RequestMapping("/genTableStr")
    @ResponseBody
    public Map<String, Object> genTableStr(String data, HttpServletRequest request) {
        Gson gson = new Gson();
        EsiTable table = gson.fromJson(data, EsiTable.class);
        Map<String, Object> result = null;
        try {
            String tableStr = new GenRCStrUtil(table, request).genTable().toString();
            result = genSuccessMsg(tableStr, "操作成功", 200);
        } catch (Exception e) {
            result = genFaultMsg(null, "操作失败", 500);
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 保存主宾信息
     *
     * @param data    主宾蓝信息
     * @param styleId 表样id
     * @return
     * @author hzc
     * @createDate 2016-3-3
     */
    @RequestMapping("/saveBarInfo")
    @ResponseBody
    public Object saveBarInfo(String data, Integer styleId, String realDate, HttpServletRequest request) {
        Gson gson = new Gson();
        EsiTable table = gson.fromJson(data, EsiTable.class);
        EsiTable realTable = gson.fromJson(realDate, EsiTable.class);
        Map<String, Object> result = null;
        try {
            User user = (User) SessionUtil.getUser(request.getSession());
            String s = designResearchService.saveBarInfos(table, realTable, styleId, request, user);
            result = genSuccessMsg(s, "操作成功", null);
        } catch (Exception e) {
            e.printStackTrace();
            result = genFaultMsg(null, "操作失败", null);
        }
        return result;
    }

    /**
     * 返回主宾栏树信息
     *
     * @param styleId   表样id
     * @param isMainBar 是否是主栏,1:是主栏,0:是宾栏
     * @mender hzc
     * @modifiedDate 2-16-3-3
     */
    @RequestMapping("/getReportBarTree")
    @ResponseBody
    public Object getReportBarTree(Integer styleId, Integer isMainBar) {
        return designResearchService.getBarInfoTrees(styleId, isMainBar, 0);
    }

    /**
     * 返回年份
     *
     * @return
     * @author hzc
     * @createDate 2016-3-4
     */
    @RequestMapping("/getYearStore")
    @ResponseBody
    public Object getYearStore() {
        return metadataInfoService.findByType(49, null);
    }

    /**
     * 返回分析报表有效报告期数
     * <pre>
     *     根据分析报表的数据集（如果有数据集则为指定数据集，
     *     如果没有则根据表样主宾栏信息获取到的虚拟数据集），
     *     返回所有有效的报告期.
     * </pre>
     *
     * @param researchId 分析报表id
     * @return 成功返回 {datas:[{year:2015,//年 period:2//月，季，半年，年 }],msg:"请求成功",
     * code:200,success:true}}
     * @author hzc
     * @createDate 2016-3-29
     */
    @RequestMapping("/getResearchPeriods")
    @ResponseBody
    public Object getResearchPeriods(Integer researchId) {

        CustomResearchEntity research = researchService.getCustomResearchById(researchId);

        //        分析报表有效报告期时间：年，月
        List<TimePojo> periods = queryRptService.getResearchTime(research);
        List<ResearchTimePojo> pojos = queryRptService.genResearchTime(periods);

        return genSuccessMsg(pojos, "请求成功", 200);
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
    public Object saveReportContent(String content, Integer styleId) {
        try {
            researchService.saveReportStyle(content, styleId);
            return genSuccessMsg("保存成功", "请求成功", null);
        } catch (Exception e) {
            e.printStackTrace();
            return genFaultMsg("保存失败", "请求失败", null);
        }
    }

    /**
     * 返回自定义查询预览页面
     *
     * @return
     * @author hzc
     * @createDate 2016-3-7
     */
    @RequestMapping("/showPreviewResearch")
    public ModelAndView showPreviewResearch(Integer researchId) {
        Gson gson = new Gson();
        ModelAndView mv = new ModelAndView("resourceCategory/analysis/report/previewResearch");
        CustomResearchEntity research = researchService.getCustomResearchById(researchId);
        mv.addObject("research", gson.toJson(research));
        List<TimeRangeEntity> timeRangeList = rangeService.queryTimeRange(Constant.TIMERANGE.TYPE_REPORT, researchId);
        mv.addObject("timeRange", gson.toJson(timeRangeList.get(0)));

        TimePojo timePojo = new TimePojo();
        //        分析报表有效报告期时间：年，月
        List<TimePojo> periods = queryRptService.getResearchTime(research);
        periods = QueryConditionUtil.filtTimes(periods, research.getPeriod());
        List<ResearchTimePojo> pojos = queryRptService.genResearchTime(periods);
        mv.addObject("periods", gson.toJson(pojos));
        if (null != periods && periods.size() > 0) {

            if (timeRangeList.get(0).getType() == Constant.TIMERANGE.BAOGAOQI) {
                if (null != pojos && pojos.size() > 0) {
                    ResearchTimePojo pojo = pojos.get(0);
                    timePojo = new TimePojo(pojo.getFrequency(), pojo.getYear(), pojo.getPeriods().get(0));
                } else {
                    Date now = new Date();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd/hh/mm/ss");
                    String dateStr = format.format(now);
                    String[] date = dateStr.split("/");
                    int year = Integer.parseInt(date[0]);
                    int period = Integer.parseInt(date[1]);
                    timePojo.setYear(year);
                    switch (research.getPeriod()) {
                        case Constant.PeriodType.YEAR:
                            break;
                        default:
                            timePojo.setPeriod(period);
                    }
                }
            } else {
                timePojo = null;
            }
            String table = queryRptService.queryCustomRpt(researchId, timePojo);
            mv.addObject("table", gson.toJson(table));
        } else {
            mv.addObject("table", "无数据");
        }
        return mv;
    }

    /**
     * 返回自定义查询报告期数查询报表数据
     *
     * @param year       年份：2016、2015
     * @param period     期度：1月，2季度，上半年
     * @param researchId 自定义查询id
     * @return
     * @author hzc
     * @createDate 2016-3-8
     */
    @RequestMapping("/getPeriodCustomResearch")
    @ResponseBody
    public Object getPeriodCustomResearch(Integer year, Integer period, Integer researchId) {
        TimePojo pojo = new TimePojo();
        if (null != period) {
            pojo.setPeriod(period);
        } else {
            pojo.setPeriod(12);
        }
        pojo.setYear(year);
        String table = queryRptService.queryCustomRpt(researchId, pojo);
        return genSuccessMsg(table, "请求成功", null);
    }

    /**
     * 返回所有自定义查询表
     *
     * @return
     * @author crx
     * @createDate 2016-3-14
     */
    @RequestMapping("/getAllCustomResearch")
    @ResponseBody
    public Object getAllCustomResearch() {
        return researchService.getAllCustomResearch();
    }
}
