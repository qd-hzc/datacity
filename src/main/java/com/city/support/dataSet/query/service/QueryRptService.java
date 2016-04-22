package com.city.support.dataSet.query.service;

import com.city.common.pojo.Constant;
import com.city.common.util.ListUtil;
import com.city.common.util.StringUtil;
import com.city.common.util.table.builder.GenTableStrUtil;
import com.city.resourcecategory.analysis.report.dao.CustomResearchDao;
import com.city.resourcecategory.analysis.report.dao.CustomResearchStyleDao;
import com.city.resourcecategory.analysis.report.entity.CustomResearchEntity;
import com.city.resourcecategory.analysis.report.entity.CustomResearchStyleEntity;
import com.city.resourcecategory.themes.pojo.ResearchVO;
import com.city.support.dataSet.dao.DataSetDataDao;
import com.city.support.dataSet.entity.DataSet;
import com.city.support.dataSet.entity.DataSetData;
import com.city.support.dataSet.query.dao.QueryRptDao;
import com.city.support.dataSet.query.pojo.ResearchTimePojo;
import com.city.support.dataSet.query.pojo.RptDataPojo;
import com.city.support.dataSet.query.pojo.TimePojo;
import com.city.support.dataSet.query.pojo.TimeRangePojo;
import com.city.support.dataSet.query.util.FillDataUtil;
import com.city.support.dataSet.query.util.QueryConditionUtil;
import com.google.gson.Gson;
import org.apache.commons.collections.MapUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
//import java.util.function.Function;

/**
 * Created by wxl on 2016/3/3.
 */
@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
public class QueryRptService {
    @Autowired
    private CustomResearchDao customResearchDao;
    @Autowired
    private CustomResearchStyleDao customResearchStyleDao;
    @Autowired
    private DataSetDataDao dataSetDataDao;
    @Autowired
    private QueryRptDao queryRptDao;

    /**
     * 返回分析报表
     * <pre>
     *     若不需要填时间,请将timePojo设为null，则查询的分析报表为连续报告期或者选择报告期
     * </pre>
     *
     * @param tmpId    报表id
     * @param timePojo 要查询的最新时间
     */
    public String queryCustomRpt(Integer tmpId, TimePojo timePojo) {
        return generateReport(tmpId, timePojo, true, true);
    }

    /**
     * 返回分析报表
     * <pre>
     *     根据分析报表报告期，查询不同期报表数据的报表。
     *     报表是否带样式，与是否带数据，根据查询条件决定
     * </pre>
     *
     * @param vo 查询条件
     *           {
     *           //分析报表id
     *           id:12,
     *           //分析报表年份：2014,2015
     *           year:2015,
     *           //分析报表期度：1月，1季度，上半年
     *           period:1,
     *           //是否填充数据：0：否，1：是
     *           hasData:1,
     *           //是否带表格宽度和高度：0：否，1：是
     *           hasStyle:1
     *           }
     * @return 如果成功，返回报表，String类型
     * @author hzc
     * @createDate 2016-3-25
     */
    public String queryCustomRpt(ResearchVO vo) {

        TimePojo pojo = new TimePojo(vo.getYear(), vo.getPeriod());

        return generateReport(vo.getId(), pojo, vo.getHasData() == 1, vo.getHasStyle() == 1);
    }

    /**
     * 返回分析报表
     * <pre>
     *     如果为固定时间期度的分析报表,请将timePojo设为null
     * </pre>
     *
     * @param tmpId    分析报表id
     * @param timePojo 要查询的时间
     * @param hasData  是否填充数据
     * @param hasStyle 是否带样式
     * @return
     */
    public String generateReport(Integer tmpId, TimePojo timePojo, boolean hasData, boolean hasStyle) {

        String result = "暂无表样";
        ///自定义报表
        CustomResearchEntity tmp = customResearchDao.queryById(tmpId);
        //表样
        CustomResearchStyleEntity style = customResearchStyleDao.selectByResearchId(tmpId);
        if (style != null && null != style.getRptStyle()) {
            //表样信息
            result = handleTable(timePojo, hasData, hasStyle, tmp, style);
        }

        return result;
    }

    /**
     * 处理表样
     *
     * @param timePojo 时间pojo
     * @param hasData  是否填充数据
     * @param hasStyle 是否带表样，宽度，高度
     * @param tmp      分析报表模板
     * @param style    表样
     * @return 返回处理完成的表样
     * @mender hzc
     * @modifiedDate 2016-3-28
     */
    private String handleTable(TimePojo timePojo, boolean hasData, boolean hasStyle, CustomResearchEntity tmp, CustomResearchStyleEntity style) {

        Element table = Jsoup.parse(style.getRptStyle()).select("table").get(0);

        //如果为最新报告期，则填时间
        if (timePojo != null) {
            timePojo.setFrequency(tmp.getPeriod());
            FillDataUtil.fillTime(table, timePojo);
        }

        if (!hasStyle) {
            clearTableStyle(table);
        }

        if (hasData) {
            fillTableData(style, tmp, table);
        }
        return table.toString();
    }

    /**
     * 清除表格宽度和高度
     *
     * @param table
     * @author hzc
     * @createDate 2016-3-28
     */
    private void clearTableStyle(Element table) {

        table.removeAttr("width");
        table.removeAttr("height");

        Elements tds = table.getElementsByTag("td");
        for (Element td : tds) {
            td.removeAttr("width");
            td.removeAttr("height");
        }

        Elements trs = table.getElementsByTag("tr");
        for (Element tr : trs) {
            tr.removeAttr("width");
            tr.removeAttr("height");
        }

    }

    /**
     * 报表填充统计数据
     *
     * @param style
     * @param tmp
     * @param table
     */
    private void fillTableData(CustomResearchStyleEntity style, CustomResearchEntity tmp, Element table) {

        //获取查询条件
        List<DataSetData> conditions = getConditionsByStyle(style);

        //查询数据集数据
        DataSet dataSet = tmp.getDataSet();
        List<RptDataPojo> datas = null;

        if (dataSet != null) {//使用数据集查询
            List<Integer> items = getItemsByConditions(conditions);
            datas = queryRptDatas(dataSet.getId(), items, null);
        } else {//根据自身查询条件查询
            datas = queryRptDatas(conditions, null, null);
        }

        //填数据
        FillDataUtil.fillTableData(table, datas);

    }

    /**
     * 查询报表时间
     *
     * @param dataSetId 数据集
     * @param items     指标筛选,数据集中包含在此处的指标才做查询,为空时查询数据集所有指标
     */
    public List<TimePojo> queryRptTime(Integer dataSetId, List<Integer> items) {
        List<DataSetData> conditions = dataSetDataDao.queryDataSetData(dataSetId, null, null);
        return queryRptTime(conditions, items);
    }

    /**
     * 查询报表时间
     *
     * @param conditions 查询条件
     * @param items      指标筛选,查询条件中包含在此处的指标才做查询,为空时查询查询条件中所有指标
     */
    public List<TimePojo> queryRptTime(List<DataSetData> conditions, List<Integer> items) {
        List<TimePojo> times = null;
        //解析后的数据集数据
        Map<Integer, List<DataSetData>> conditionMap = QueryConditionUtil.formatCondition(conditions, items);
        //生成sql,获取结果
        if (QueryConditionUtil.checkConditionValid(conditionMap)) {
            String sql = QueryConditionUtil.createRptTimeSql(conditionMap);
            //查询出的数据
            List<Object[]> list = queryRptDao.queryRptTime(sql);
            if (list.size() > 0) {
                times = new ArrayList<>();
                for (Object[] arr : list) {
                    times.add(new TimePojo(((BigDecimal) arr[0]).intValue(), ((BigDecimal) arr[1]).intValue()));
                }
            }
        }
        return times;
    }

    /**
     * 查询报表数据!!!!
     *
     * @param dataSetId 数据集
     * @param times     时间范围,为空时不对时间做筛选
     * @param items     指标筛选,数据集中包含在此处的指标才做查询,为空时查询数据集所有指标
     */
    public List<RptDataPojo> queryRptDatas(Integer dataSetId, List<Integer> items, List<TimePojo> times) {
        List<DataSetData> conditions = dataSetDataDao.queryDataSetData(dataSetId, null, null);
        return queryRptDatas(conditions, items, times);
    }

    /**
     * 查询报表数据!!!!
     *
     * @param conditions 查询条件
     * @param times      时间范围,为空时不对时间做筛选
     * @param items      指标筛选,查询条件中包含在此处的指标才做查询,为空时查询查询条件中所有指标
     */
    public List<RptDataPojo> queryRptDatas(List<DataSetData> conditions, List<Integer> items, List<TimePojo> times) {
        //解析后的数据集数据
        Map<Integer, List<DataSetData>> conditionMap = QueryConditionUtil.formatCondition(conditions, items);
        //生成sql
        if (QueryConditionUtil.checkConditionValid(conditionMap)) {
            //首先转化时间状态
            String sql = QueryConditionUtil.createRptDataSql(conditionMap, convertTime(times, conditions, items));
            return QueryConditionUtil.convertRptDatas(queryRptDao.queryRptData(sql));
        }
        return null;
    }

    /**
     * 转换时间状态,将时间范围转换为时间列表
     *
     * @param times      待转的时间范围
     * @param conditions 查询数据的条件,为期数类型且为最新报告期时用到,用来查询报告期
     * @param items      指标筛选,查询条件中包含在此处的指标才做查询,为空时查询查询条件中所有指标
     *                   ,为期数类型且为最新报告期时用到,用来查询报告期
     */
    public List<TimePojo> convertTime(List<TimePojo> times, List<DataSetData> conditions, List<Integer> items) {
        List<TimePojo> timePojos = null;
        if (times != null && times.size() > 0) {
            Collections.sort(times);
            timePojos = new ArrayList<>();
            TimePojo t0 = times.get(0);
            if (t0 instanceof TimeRangePojo) {
                TimeRangePojo t = (TimeRangePojo) t0;
                int type = t.getType();//类型
                if (type == Constant.TIMERANGE.LIANXU) {//连续
                    timePojos.add(t0);
                    if (times.size() >= 2) {
                        TimePojo t1 = times.get(1);
                        while (t1.compareTo(t0) > 0) {
                            timePojos.add(t1);
                            t1 = new TimePojo(t1, 1);
                            t1.setFrequency(t0.getFrequency());
                        }
                    }
                } else if (type == Constant.TIMERANGE.BAOGAOQI) {//报告期
                    int periodsSpan = t.getPeriodsSpan();//期数
                    boolean isSetted = false;
                    boolean isNeweast = t.getYear() == 0;//是否为最新报告期
                    if (isNeweast) {//最新报告期
                        List<TimePojo> ts = queryRptTime(conditions, items);
                        if (ts != null && ts.size() > 0) {
                            int frequency = t.getFrequency();//频度
                            for (TimePojo curT : ts) {
                                if (Constant.PeriodType.isMatchPeriod(frequency, curT.getPeriod())) {//期度是否匹配频度
                                    t.setYear(curT.getYear());
                                    t.setPeriod(curT.getPeriod());
                                    isSetted = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (isSetted || (!isNeweast)) {
                        timePojos.add(t);
                        if (periodsSpan > 1) {
                            for (; periodsSpan > 1; periodsSpan--) {
                                t0 = new TimePojo(t0, 1);
                                timePojos.add(t0);
                            }
                        }
                    }
                } else {//选择
                    return times;
                }
            } else {
                return times;
            }
            Collections.sort(timePojos);
        }
        return timePojos;
    }

    /**
     * 根据表样获取
     */
    private List<DataSetData> getConditionsByStyle(CustomResearchStyleEntity style) {
        List<DataSetData> result = null;
        String rptStyle = style.getRptStyle();
        if (StringUtil.trimNotEmpty(rptStyle)) {
            Element table = Jsoup.parse(rptStyle);
            //数据域
            Elements tds = table.getElementsByAttributeValue(GenTableStrUtil.TdAttrName.TYPE, Constant.TdEsiType.DATA);
            if (ListUtil.notEmpty(tds)) {
                result = new ArrayList<>();
                Gson gson = new Gson();
                Set<DataSetData> datas = new HashSet<>();
                DataSetData data = null;
                for (Element td : tds) {
                    if (td == null) {
                        continue;
                    }
                    Map dataMap = gson.fromJson(td.attr(GenTableStrUtil.TdAttrName.DATA_VALUE), HashMap.class);
                    if (dataMap == null || dataMap.size() == 0) {
                        continue;
                    }
                    data = new DataSetData();//指标
                    data.setDataType(Constant.MetadataType.ITEM);
                    data.setDataValue(MapUtils.getInteger(dataMap, GenTableStrUtil.ProAttrName.ITEM).toString());
                    data.setDataInfo1(MapUtils.getInteger(dataMap, GenTableStrUtil.ProAttrName.ITEM_CALIBER));
                    data.setDataInfo2(MapUtils.getInteger(dataMap, GenTableStrUtil.ProAttrName.DEP));
                    datas.add(data);
                    data = new DataSetData();//时间框架
                    data.setDataType(Constant.MetadataType.TIME_FRAME);
                    data.setDataValue(MapUtils.getInteger(dataMap, GenTableStrUtil.ProAttrName.TIME_FRAME).toString());
                    datas.add(data);
                    //统计对象
                    Integer surObj = MapUtils.getInteger(dataMap, GenTableStrUtil.ProAttrName.SUR_OBJ);
                    if (surObj != null) {
                        data = new DataSetData();//统计对象
                        data.setDataType(Constant.MetadataType.RESEARCH_OBJ);
                        data.setDataValue(surObj.toString());
                        data.setDataInfo1(MapUtils.getInteger(dataMap, GenTableStrUtil.ProAttrName.SUR_OBJ_TYPE));
                        data.setDataInfo2(MapUtils.getInteger(dataMap, GenTableStrUtil.ProAttrName.AREA));
                        datas.add(data);
                    }
                    //分组目录
                    String itemMenu = MapUtils.getString(dataMap, GenTableStrUtil.ProAttrName.ITEM_DICT);
                    if (StringUtil.trimNotEmpty(itemMenu)) {
                        data = new DataSetData();
                        data.setDataType(Constant.MetadataType.ITEM_MENU);
                        data.setDataValue(itemMenu);
                        datas.add(data);
                    }
                }
                result.addAll(datas);
            }
        }
        return result;
    }

    /**
     * 根据查询条件获取指标过滤
     *
     * @param conditions
     * @return
     */
    private List<Integer> getItemsByConditions(List<DataSetData> conditions) {
        List<Integer> items = null;
        if (conditions != null && conditions.size() > 0) {
            items = new ArrayList<>();
            Set<Integer> itemSet = new HashSet<>();
            for (DataSetData cond : conditions) {
                if (cond.getDataType() == Constant.MetadataType.ITEM) {
                    itemSet.add(Integer.parseInt(cond.getDataValue()));
                }
            }
            items.addAll(itemSet);
        }
        return items;
    }

    /**
     * 返回分析报表中的数据的有效报告期时间（年，期度（年，半年，季，月））
     * <pre>
     *     1、分析报表有数据集：查询数据集中的所有指标数据的时间，
     *     返回该数据集的报告期时间
     *     2、分析报表无数据集，则根据报表表样，解析数据表格中的所有指标信息，
     *     返回一个虚拟数据集的报告期时间
     *
     * </pre>
     *
     * @param research 分析报表
     * @return 返回结果:时间集合
     * <pre>
     *     TimePojo ：
     *     {@code
     *
     *          {
     *              //分析报表期度：1：年报，2：半年报
     *              //3：季报，4：月报
     *              frequency:2,
     *              //年
     *              year:2016,
     *              //期度：分析报表期度对应的期度值
     *              //1：年报：12
     *              //2：半年报：6,12
     *              //3：季报：3,6,9,12
     *              //4：月报：1,2,3,4,5,6,7,8,9,10,11,12
     *              period:1
     *          }
     *     }
     * </pre>
     */
    public List<TimePojo> getResearchTime(CustomResearchEntity research) {
        List<TimePojo> list = null;
        //        分析报表有数据集
        DataSet dataSet = research.getDataSet();

        if (null != dataSet && dataSet.getId() > 0) {

            list = queryRptTime(dataSet.getId(), null);

        } else {

//            无数据集
            CustomResearchStyleEntity style = customResearchStyleDao.selectByResearchId(research.getId());

            list = queryRptTime(getConditionsByStyle(style), null);
        }

        if (null != list && list.size() > 0) {
            Integer period = research.getPeriod();
            for (TimePojo pojo : list) {
                pojo.setFrequency(period);
            }
        }
        return list;
    }

    /**
     * 返回分析报表有效报告期
     * <pre>
     *     根据传入的分析报表的有效报告期，按照年合并期度，
     *     每年不同的报告期放到ResearchTimePojo的List periods 中
     * </pre>
     *
     * @param list 分析报表所有有效报告期
     * @return 时间期度，倒序排序
     * @author hzc
     * @createDate 2016-3-31
     */
    public List<ResearchTimePojo> genResearchTime(List<TimePojo> list) {

        List<ResearchTimePojo> result = new ArrayList<>();

        Collection<ResearchTimePojo> values = getResearchTimePojos(list);

//        组装结果集,结果排序
        if (!values.isEmpty()) {
            Iterator<ResearchTimePojo> iterator = values.iterator();
            while (iterator.hasNext()) {
                ResearchTimePojo next = iterator.next();
                Collections.sort(next.getPeriods(), new Comparator<Integer>() {
                    @Override
                    public int compare(Integer o1, Integer o2) {
                        return o2 - o1;
                    }
                });
                result.add(next);
            }
        }

//        按照年份倒序排序
        Collections.sort(result, new Comparator<ResearchTimePojo>() {
            @Override
            public int compare(ResearchTimePojo o1, ResearchTimePojo o2) {
                return o2.getYear() - o1.getYear();
            }
        });

        return result;
    }

    /**
     * 组装TimePojo为ResearchTimePojo
     *
     * @param list
     * @return
     * @author hzc
     * @createDate 2016-3-31
     */
    private Collection<ResearchTimePojo> getResearchTimePojos(List<TimePojo> list) {
        HashMap<Integer, ResearchTimePojo> map = new HashMap<>();
        if (null != list && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                TimePojo timePojo = list.get(i);
                int year = timePojo.getYear();
                ResearchTimePojo pojo = map.get(year);
                int period = timePojo.getPeriod();
                if (null != pojo) {
                    List<Integer> periods = pojo.getPeriods();
                    periods.add(period);
                    pojo.setPeriods(periods);
                } else {
                    pojo = new ResearchTimePojo();
                    pojo.setYear(year);
                    pojo.setFrequency(timePojo.getFrequency());
                    ArrayList<Integer> periods = new ArrayList<Integer>();
                    periods.add(period);
                    pojo.setPeriods(periods);
                    map.put(year, pojo);
                }
            }
        }
        return map.values();
    }

}
