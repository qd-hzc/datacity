package com.city.resourcecategory.analysis.chart.controller;

import com.city.common.controller.BaseController;
import com.city.common.pojo.Constant;
import com.city.common.util.EsiJsonParamUtil;
import com.city.common.util.EsiLogUtil;
import com.city.resourcecategory.analysis.chart.entity.AnalysisChartBase;
import com.city.resourcecategory.analysis.chart.entity.AnalysisChartGroup;
import com.city.resourcecategory.analysis.chart.entity.AnalysisChartInfo;
import com.city.resourcecategory.analysis.chart.entity.AnalysisChartStructure;
import com.city.resourcecategory.analysis.chart.service.BasicChartConfigService;
import com.city.resourcecategory.analysis.chart.service.BasicChartStructureService;
import com.city.resourcecategory.analysis.common.entity.TimeRangeEntity;
import com.city.resourcecategory.analysis.common.service.TimeRangeService;
import com.city.support.dataSet.entity.DataSetData;
import com.city.support.dataSet.query.pojo.RptDataPojo;
import com.city.support.dataSet.query.pojo.TimePojo;
import com.city.support.dataSet.query.pojo.TimeRangePojo;
import com.city.support.dataSet.query.service.QueryRptService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wys on 2016/2/22.
 */
@Controller
@RequestMapping("/support/resourcecategory/analysis/chart")
public class BasicChartConfigController extends BaseController {
    @Autowired
    private BasicChartConfigService basicChartConfigService;
    @Autowired
    private BasicChartStructureService basicChartStructureService;
    @Autowired
    QueryRptService queryRptService;
    @Autowired
    private TimeRangeService rangeService;

    @RequestMapping("/basicChartConfigPage")
    public ModelAndView basicChartConfigPage() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("/resourceCategory/analysis/chart/background/basicChartConfigPage");
        return mv;
    }

    @RequestMapping("/queryAllAnalysisChartGroup")
    @ResponseBody
    public Map<String, Object> queryAllAnalysisChartGroup() {
        Map<String, Object> result = null;
        List<AnalysisChartGroup> datas = null;
        try {
            datas = basicChartConfigService.queryAllAnalysisChartGroup();
            result = genSuccessMsg(datas, "查询成功", null);
            Gson g = new Gson();
            System.out.println(g.toJson(result));
        } catch (Exception e) {
            e.printStackTrace();
            EsiLogUtil.error(getLog(), e.getMessage());
            result = genFaultMsg(null, "查询失败", null);
        }

        return result;
    }

    @RequestMapping("/updateAnalysisChartGroup")
    @ResponseBody
    public Map<String, Object> updateAnalysisChartGroup(HttpServletRequest request) {
        Map<String, Object> result = null;
        List<AnalysisChartGroup> datas = null;
        try {
            EsiJsonParamUtil<AnalysisChartGroup> paramUtil = new EsiJsonParamUtil<>();
            datas = paramUtil.parseObjToList(request, AnalysisChartGroup.class);
            datas = basicChartConfigService.updateAnalysisChartGroup(datas);
            result = genSuccessMsg(datas, "操作成功", null);
        } catch (Exception e) {
            e.printStackTrace();
            EsiLogUtil.error(getLog(), e.getMessage());
            result = genFaultMsg(null, "操作失败", null);
        }
        return result;
    }

    @RequestMapping("/delAnalysisChartGroup")
    @ResponseBody
    public Map<String, Object> delAnalysisChartGroup(HttpServletRequest request) {
        Map<String, Object> result = null;
        List<AnalysisChartGroup> datas = null;
        try {
            EsiJsonParamUtil<AnalysisChartGroup> paramUtil = new EsiJsonParamUtil<>();
            datas = paramUtil.parseObjToList(request, AnalysisChartGroup.class);
            basicChartConfigService.delAnalysisChartGroup(datas);
            result = genSuccessMsg(null, "操作成功", null);
        } catch (Exception e) {
            e.printStackTrace();
            EsiLogUtil.error(getLog(), e.getMessage());
            result = genFaultMsg(null, "操作失败", null);
        }
        return result;
    }

    @RequestMapping("/queryAnalysisChartBaseByGroupId")
    @ResponseBody
    public Map<String, Object> queryAnalysisChartBaseByGroupId(HttpServletRequest request) {
        Map<String, Object> result = null;
        List<AnalysisChartBase> datas = null;
        EsiJsonParamUtil<AnalysisChartBase> paramUtil = new EsiJsonParamUtil<>();
        String groupIdStr = null;
        Integer groupId = null;
        try {
            groupIdStr = paramUtil.getParam(request, "groupId");
            groupId = Integer.parseInt(groupIdStr);
            datas = basicChartConfigService.queryAnalysisChartBaseByGroupId(groupId);
            result = genSuccessMsg(datas, "查询成功", null);
        } catch (Exception e) {
            e.printStackTrace();
            EsiLogUtil.error(getLog(), e.getMessage());
            result = genFaultMsg(null, "查询失败", null);
        }
        return result;
    }

    /**
     *
     * @return
     */
    @RequestMapping("/queryAllChartExceptMap")
    @ResponseBody
    public List<AnalysisChartBase> queryAllChartExceptMap() {
        return basicChartConfigService.queryAllChartExceptMap();
    }

    /**
     *
     * @return
     */
    @RequestMapping("/queryAllMap")
    @ResponseBody
    public List<AnalysisChartBase> queryAllMap() {
        return basicChartConfigService.queryAllMap();
    }
    @RequestMapping("/updateAnalysisChartBase")
    @ResponseBody
    public Map<String, Object> updateAnalysisChartBase(HttpServletRequest request) {
        Map<String, Object> result = null;
        List<AnalysisChartBase> datas = null;
        try {
            EsiJsonParamUtil<AnalysisChartBase> paramUtil = new EsiJsonParamUtil<>();
            datas = paramUtil.parseObjToList(request, AnalysisChartBase.class);
            datas = basicChartConfigService.updateAnalysisChartBase(datas);
            result = genSuccessMsg(datas, "操作成功", null);
        } catch (Exception e) {
            e.printStackTrace();
            EsiLogUtil.error(getLog(), e.getMessage());
            result = genFaultMsg(null, "操作失败", null);
        }
        return result;
    }

    @RequestMapping("/delAnalysisChartBase")
    @ResponseBody
    public Map<String, Object> delAnalysisChartBase(HttpServletRequest request) {
        Map<String, Object> result = null;
        List<AnalysisChartBase> datas = null;
        try {
            EsiJsonParamUtil<AnalysisChartBase> paramUtil = new EsiJsonParamUtil<>();
            datas = paramUtil.parseObjToList(request, AnalysisChartBase.class);
            basicChartConfigService.delAnalysisChartBase(datas);
            result = genSuccessMsg(null, "操作成功", null);
        } catch (Exception e) {
            e.printStackTrace();
            EsiLogUtil.error(getLog(), e.getMessage());
            result = genFaultMsg(null, "操作失败", null);
        }
        return result;
    }

    /**
     * 根据图表id获取图表信息
     *
     * @param id
     * @return
     */
    @RequestMapping("/queryAnalysisChartInfoByChartId")
    @ResponseBody
    public Object queryAnalysisChartInfoByChartId(Integer id) {
        Map<String, Object> result = getChartById(id);
        return result;
    }

    private Map<String, Object> getChartById(Integer id) {
        Map<String, Object> result = new HashMap<>();
        AnalysisChartBase analysisChartBase = basicChartConfigService.queryAnalysisChartBaseById(id);
        result.put("chart", analysisChartBase);
        List<AnalysisChartInfo> analysisChartCategoryList = basicChartStructureService.queryChartInfo(id, Constant.STRUCTURE_TYPE.CATEGORY);
        List<HashMap> categoryList = new ArrayList<HashMap>();
        for (AnalysisChartInfo analysisChartCategory : analysisChartCategoryList) {
            HashMap map = new HashMap();
            map.put("info", analysisChartCategory);
            List<AnalysisChartStructure> chartStructureList = getChartStructureByChartInfo(analysisChartCategory);
            int isDynamic = 0;
            isDynamic = isDynamic(chartStructureList);
            map.put("isDynamic", isDynamic);
            map.put("condition", chartStructureList);
            categoryList.add(map);
        }
        result.put("category", categoryList);
        List<AnalysisChartInfo> analysisChartSeriesList = basicChartStructureService.queryChartInfo(id, Constant.STRUCTURE_TYPE.SERIES);
        List<HashMap> seriesList = new ArrayList<HashMap>();
        for (AnalysisChartInfo analysisChartSeries : analysisChartSeriesList) {
            HashMap map = new HashMap();
            map.put("info", analysisChartSeries);
            List<AnalysisChartStructure> chartStructureList = getChartStructureByChartInfo(analysisChartSeries);
            int isDynamic = 0;
            isDynamic = isDynamic(chartStructureList);
            map.put("isDynamic", isDynamic);
            map.put("condition", chartStructureList);
            seriesList.add(map);
        }
        result.put("series", seriesList);
        return result;
    }

    /**
     * 判断条件中是否含有动态指标和时间
     *
     * @param chartStructureList
     * @return
     */
    private int isDynamic(List<AnalysisChartStructure> chartStructureList) {
        for (AnalysisChartStructure analysisChartStructure : chartStructureList) {
            if (analysisChartStructure.getMetaType() == Constant.MetadataType.DYNAMIC_ITEM ||
                    analysisChartStructure.getMetaType() == Constant.MetadataType.DYNAMIC_ITEMGROUP ||
                    analysisChartStructure.getMetaType() == Constant.MetadataType.DYNAMIC_SUROBJ ||
                    analysisChartStructure.getMetaType() == Constant.MetadataType.DYNAMIC_TIMEFRAME||
                    analysisChartStructure.getMetaType() == Constant.MetadataType.DYNAMIC_TIME) {
                return 1;
            }
        }
        return 0;
    }

    /**
     * 用来获取分类轴数据的条件
     *
     * @param analysisChartCategory
     * @return
     */
    private List<AnalysisChartStructure> getChartStructureByChartInfo(AnalysisChartInfo analysisChartCategory) {
        List<AnalysisChartStructure> analysisChartStructureList = new ArrayList<AnalysisChartStructure>();
        Integer structureId = analysisChartCategory.getStructureId();
        setChartStructure(analysisChartStructureList, structureId);
        return analysisChartStructureList;
    }

    /**
     * 获取子节点所有父节点的信息
     *
     * @param analysisChartStructureList
     * @param structureId
     */
    private void setChartStructure(List<AnalysisChartStructure> analysisChartStructureList, Integer structureId) {
        if (structureId != null && structureId != 0) {
            AnalysisChartStructure analysisChartStructure = basicChartStructureService.queryChartStructureById(structureId);
            Integer parentId = analysisChartStructure.getParentId();
            analysisChartStructureList.add(analysisChartStructure);
            setChartStructure(analysisChartStructureList, parentId);
        }
    }

    /**
     * @param id   id
     * @param type 报表或图表
     * @return
     */
    @RequestMapping("/queryTimeByCondition")
    @ResponseBody
    public List<TimePojo> queryTimeByCondition(Integer id, Integer type, Integer periodType, HttpServletRequest request) {
        List<TimePojo> result = new ArrayList<>();
        try {
            List<TimePojo> timePojoList = queryTime(id, type, periodType);
            EsiJsonParamUtil<DataSetData> paramUtil = new EsiJsonParamUtil<>();
            List<DataSetData> conditions = null;
            conditions = paramUtil.parseObjToList(request, DataSetData.class);
            result = queryRptService.queryRptTime(conditions, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<TimePojo> queryTime(Integer id, Integer type, Integer periodType) {
        List<TimePojo> result = new ArrayList<>();
        List<TimeRangeEntity> timeRangeEntityList = rangeService.queryTimeRange(type, id);
        Integer timeType = timeRangeEntityList.get(0).getType();
        Integer timeValue = timeRangeEntityList.get(0).getDataValue();
        switch (timeType) {
            case 1:
                Map<String, Object> map = new HashMap<>();
                Integer beginYear = null;
                Integer endYear = null;
                Integer beginPeriod = null;
                Integer endPeriod = null;
                for (TimeRangeEntity timeRange : timeRangeEntityList) {
                    int dataType = timeRange.getDataType();
                    Integer dataValue = timeRange.getDataValue();
                    switch (dataType) {
                        case 1:
                            beginYear = dataValue;
                            break;
                        case 3:
                            endYear = dataValue;
                            break;
                        case 2:
                            beginPeriod = dataValue;
                            break;
                        case 4:
                            endPeriod = dataValue;
                            break;

                    }
                }
                if (beginYear != null && endYear != null && beginPeriod != null & endPeriod != null) {
                    TimeRangePojo beginTime = new TimeRangePojo(periodType, beginYear, beginPeriod);
                    TimeRangePojo endTime = new TimeRangePojo(periodType, endYear, endPeriod);
                    beginTime.setType(Constant.TIMERANGE.LIANXU);
                    endTime.setType(Constant.TIMERANGE.LIANXU);
                    result.add(beginTime);
                    result.add(endTime);
                }

                break;
            case 2:
                List<Integer> yearList = new ArrayList<Integer>();
                List<Integer> periodList = new ArrayList<Integer>();
                for (TimeRangeEntity timeRange : timeRangeEntityList) {
                    Integer dataType = timeRange.getDataType();
                    Integer dataValue = timeRange.getDataValue();
                    if (dataType == Constant.TIMERANGE.DATA_YEAR) {
                        yearList.add(dataValue);
                    }
                    if (dataType == Constant.TIMERANGE.DATA_PERIOD) {
                        periodList.add(dataValue);
                    }
                }
                for (Integer year : yearList) {
                    for (Integer period : periodList) {
                        TimePojo timePojo = new TimeRangePojo();
                        timePojo.setYear(year);
                        timePojo.setPeriod(period);
                        timePojo.setFrequency(periodType);
                        result.add(timePojo);
                    }
                }
                break;
            case 3:
                TimeRangePojo timeRangePojo = new TimeRangePojo();
                timeRangePojo.setPeriodsSpan(timeValue);
                timeRangePojo.setType(Constant.TIMERANGE.BAOGAOQI);
                result.add(timeRangePojo);
                break;
        }
        return result;

    }
    /**
     * @param chartId     id
     * @param chartType    报表或图表
     * @param request
     * @return
     */
    @RequestMapping("/queryDataByCondition")
    @ResponseBody
    public List<RptDataPojo> queryDataByCondition(Integer chartId, Integer chartType, Integer periodType,String dataSets, HttpServletRequest request) {
        List<RptDataPojo> result = new ArrayList<>();

        try {
            List<TimePojo> timePojoList =null;
            if(periodType != null) {
                timePojoList = queryTime(chartId, chartType, periodType);
            }
            Gson gson = new Gson();
            Type type = new TypeToken<List<DataSetData>>() {
            }.getType();
            List<DataSetData> conditions = null;
            conditions = gson.fromJson(dataSets, type);
            result = queryRptService.queryRptDatas(conditions, null, timePojoList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    /**
     * @param structureIds     id
     * @param chartType    报表或图表
     * @param request
     * @return
     */
    @RequestMapping("/queryDatasByCondition")
    @ResponseBody
    public List<List<RptDataPojo>> queryDatasByCondition(String structureIds, Integer chartType, Integer periodType,String dataSets, HttpServletRequest request) {
        List<List<RptDataPojo>> result = new ArrayList<>();

        try {
            Gson gson = new Gson();
            Type idType = new TypeToken<List<Integer>>() {
            }.getType();

            Type type = new TypeToken<List<List<DataSetData>>>() {
            }.getType();
            List<List<DataSetData>> conditionsList = null;
            conditionsList = gson.fromJson(dataSets, type);
            List<Integer> idList = null;
            idList = gson.fromJson(structureIds, idType);
            //EsiJsonParamUtil<DataSetData> paramUtil = new EsiJsonParamUtil<>();
            //conditions = paramUtil.parseObjToList(request, DataSetData.class);
            //for(List<DataSetData> conditions: conditionsList){
            for(int i=0;i<conditionsList.size();i++){
                List<DataSetData> conditions = conditionsList.get(i);
                List<TimePojo> timePojoList =null;
                if(periodType != null) {
                    if(idList.get(i)!=null){
                        timePojoList = queryTime(idList.get(i), chartType, periodType);
                    }
                }
                List<RptDataPojo> tmp = new ArrayList<>();
                tmp = queryRptService.queryRptDatas(conditions, null, timePojoList);
                result.add(tmp);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    @RequestMapping("/queryChartDatasByCondition")
    @ResponseBody
    public List<List<RptDataPojo>> queryChartDatasByCondition(String timeListStr,String dataSets) {
        List<List<RptDataPojo>> result = new ArrayList<>();

        try {
            Gson gson = new Gson();
            Type timeType = new TypeToken<List<List<TimeRangePojo>>>() {
            }.getType();

            Type dataType = new TypeToken<List<List<DataSetData>>>() {
            }.getType();
            List<List<DataSetData>> conditionsList = null;
            List<List<TimePojo>> timeList = null;
            conditionsList = gson.fromJson(dataSets, dataType);
            timeList = gson.fromJson(timeListStr, timeType);
            for(int i=0;i<conditionsList.size();i++){
                List<DataSetData> conditions = conditionsList.get(i);
                List<RptDataPojo> tmp = new ArrayList<>();
                tmp = queryRptService.queryRptDatas(conditions, null, timeList==null?null:timeList.get(i));
                result.add(tmp);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
