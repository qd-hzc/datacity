package com.city.resourcecategory.analysis.common.controller;

import com.city.common.controller.BaseController;
import com.city.common.util.EsiLogUtil;
import com.city.common.util.SessionUtil;
import com.city.resourcecategory.analysis.common.service.AnalysisService;
import com.city.resourcecategory.analysis.common.service.TimeRangeService;
import com.city.resourcecategory.analysis.report.entity.CustomResearchEntity;
import com.city.resourcecategory.analysis.report.service.CustomResearchService;
import com.city.support.regime.report.service.DesignReportService;
import com.city.support.sys.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by HZC on 2016/2/29.
 */
@Controller
@RequestMapping("/resourcecategory/analysis/common/analysis")
public class AnalysisController extends BaseController {
    @Autowired
    private DesignReportService designReportService;
    @Autowired
    private AnalysisService analysisService;
    @Autowired
    private TimeRangeService rangeService;
    @Autowired
    private CustomResearchService researchService;

    /**
     * 返回分组树信息
     *
     * @return
     * @author hzc
     * @createDate 2016-2-29
     */
    @RequestMapping("/getGroupInfoTrees")
    @ResponseBody
    public Object getGroupInfoTrees() {
        return analysisService.getGroupInfoTrees();
    }

    @RequestMapping("/getGroupInfoTreeForChart")
    @ResponseBody
    public Object getGroupInfoTreeForChart() {
        return analysisService.getGroupInfoTreeForChart();
    }

    @RequestMapping("/getItemGroupTree")
    @ResponseBody
    public Map<String, Object> getItemGroupTree() {
        Map<String, Object> result = null;
        try {
            List datas = analysisService.getItemGroupTree();
            result = genSuccessMsg(datas, "查询成功", null);
        } catch (Exception e) {
            e.printStackTrace();
            EsiLogUtil.error(getLog(), e.getMessage());
            result = genFaultMsg("查询失败");
        }
        return result;
    }

    /**
     * 保存时间范围
     *
     * @param request
     * @return
     * @author hzc
     * @createDate 2016-3-2
     */
    @RequestMapping("/saveTimeRangeAndFrequency")
    @ResponseBody
    public Object saveTimeRangeAndFrequency(HttpServletRequest request) {
        String timeRange = request.getParameter("timeRange");
        Integer foreignType = Integer.parseInt(request.getParameter("foreignType"));
        Integer foreignId = Integer.parseInt(request.getParameter("foreignId"));
        Integer period = Integer.parseInt(request.getParameter("period"));
        User user = (User) SessionUtil.getUser(request.getSession());
        rangeService.saveTimeRange(timeRange, foreignType, foreignId,user);
        CustomResearchEntity entity = new CustomResearchEntity();
        entity.setId(foreignId);
        entity.setPeriod(period);
        researchService.updateEntity(entity);
        return genSuccessMsg("请求成功", "保存成功", null);
    }
}
