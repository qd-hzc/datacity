package com.city.resourcecategory.themes.service;

import com.city.common.pojo.Constant;
import com.city.resourcecategory.analysis.chart.service.BasicChartConfigService;
import com.city.resourcecategory.analysis.common.entity.TimeRangeEntity;
import com.city.resourcecategory.analysis.common.service.TimeRangeService;
import com.city.resourcecategory.analysis.report.entity.CustomResearchEntity;
import com.city.resourcecategory.analysis.report.service.CustomResearchService;
import com.city.resourcecategory.analysis.text.entity.TextContent;
import com.city.resourcecategory.analysis.text.entity.TextTheme;
import com.city.resourcecategory.analysis.text.service.TextContentService;
import com.city.resourcecategory.analysis.text.service.TextThemeService;
import com.city.resourcecategory.themes.dao.ThemePageDao;
import com.city.resourcecategory.themes.entity.ThemePage;
import com.city.resourcecategory.themes.entity.ThemePageContent;
import com.city.resourcecategory.themes.pojo.DataSetTimeFrame;
import com.city.resourcecategory.themes.pojo.ReportVO;
import com.city.resourcecategory.themes.pojo.ResearchVO;
import com.city.support.dataSet.query.pojo.ResearchTimePojo;
import com.city.support.dataSet.query.pojo.RptDataPojo;
import com.city.support.dataSet.query.pojo.TimePojo;
import com.city.support.dataSet.query.pojo.TimeRangePojo;
import com.city.support.dataSet.query.service.QueryRptService;
import com.city.support.manage.timeFrame.entity.TimeFrame;
import com.city.support.manage.timeFrame.service.TimeFrameService;
import com.city.support.regime.collection.entity.ReportData;
import com.city.support.regime.collection.entity.ReportInfo;
import com.city.support.regime.collection.service.ReportInfoService;
import com.city.support.sys.user.entity.Role;
import com.city.support.sys.user.pojo.CurrentUser;
import com.city.support.sys.user.pojo.ReportPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.*;

/**
 * 主题对接前台类
 * Created by HZC on 2016/3/18.
 */
@Service
@Transactional
public class CommonService {

    @Autowired
    private ManageThemesService themesService;

    @Autowired
    private ReportInfoService reportInfoService;

    @Autowired
    private CustomResearchService researchService;

    @Autowired
    private TimeRangeService rangeService;

    @Autowired
    private QueryRptService queryRptService;

    @Autowired
    private ThemePageDao pageDao;

    @Autowired
    BasicChartConfigService basicChartConfigService;

    @Autowired
    private TimeFrameService timeFrameService;

    @Autowired
    private TextThemeService textThemeService;

    @Autowired
    private TextContentService textContentService;

    /**
     * 返回首页
     *
     * @return
     * @author hzc
     * @createDate 2016-3-18
     */
    public ThemePage getIndex() {
        return pageDao.selectIndex();
    }

    /**
     * 返回themepage
     *
     * @param id
     * @return
     * @author hzc
     * @createDate 2016-3-18
     */
    public ThemePage getThemePage(Integer id) {
        return themesService.getThemePageById(id);
    }

    /**
     * 返回数据
     *
     * @param content
     * @return
     * @author hzc
     * @createDate 2016-3-18
     */
    public Object returnData(ThemePageContent content, CurrentUser user) throws Exception {
        Object result = "无数据";
        Integer contentType = content.getContentType();
        String contentValue = content.getContentValue();
        switch (contentType) {
            case Constant.THEME_CONTENT_TYPE.RPT_SYNTHESIZE://综合表
                result = getReport(user, contentValue);
                break;
            case Constant.THEME_CONTENT_TYPE.RPT_CUSTOM://自定义表
                result = getResearch(contentValue);
                break;
            case Constant.THEME_CONTENT_TYPE.CHART://图表
                result = getChart(user, contentValue);
                break;
            case Constant.THEME_CONTENT_TYPE.MAP://地图
                result = getChart(user, contentValue);
                break;
            case Constant.THEME_CONTENT_TYPE.TEXT_DESC://文字分析
                result = getTextContent(user, contentValue);
                break;
            case Constant.THEME_CONTENT_TYPE.FILE://文件
                break;
            case Constant.THEME_CONTENT_TYPE.MENU://目录
                result = getMenus(user, contentValue);
                break;
            case Constant.THEME_CONTENT_TYPE.DATA_SET://数据集
                result = getDataSetData(contentValue);
                break;
            case Constant.THEME_CONTENT_TYPE.TEXT_THEME://分析主题
                result = getTextContentList(user, contentValue);
                break;
        }
        return result;
    }

    /**
     * 返回图表、地图
     *
     * @param user
     * @param contentValue
     * @return
     */
    private Object getChart(CurrentUser user, String contentValue) {
        int chartId = Integer.parseInt(contentValue);
        if (1 == 1) {// 权限验证
            Map<String, Object> result = basicChartConfigService.getChartById(chartId);
            if (result != null) {
                return result;
            }
        }
        return "无数据";
    }

    /**
     * 返回分析主题
     *
     * @param user
     * @param contentValue
     * @return
     */
    private Object getTextTheme(CurrentUser user, String contentValue) {
        int themeId = Integer.parseInt(contentValue);
        if (1 == 1) {// 权限验证
            TextTheme result = textThemeService.queryTextThemeById(themeId);
            if (result != null) {
                return result;
            }
        }
        return "无数据";
    }

    /**
     * 返回分析内容列表
     *
     * @param user
     * @param contentValue
     * @return
     */
    private Object getTextContentList(CurrentUser user, String contentValue) {
        int themeId = Integer.parseInt(contentValue);
        if (1 == 1) {// 权限验证
            Map<String, Object> result = new HashMap<>();
            List<TextContent> textContentList = textContentService.queryAllTextContentByThemeId(null, themeId, TextTheme.SORT_BY_DATE, null, Constant.TEXT_CONTENT_STATUS.CHECKED);
            List<TextContent> textContentListByUser = textContentService.queryAllTextContentByThemeId(user.getUser(), themeId, TextTheme.SORT_BY_DATE, null, null);
            if (textContentList != null) {
                List<Map<String, Object>> allTextMapList = new ArrayList<>();
                List<Map<String, Object>> userTextMapList = new ArrayList<>();
                for(TextContent textContent: textContentList){
                    Map<String, Object> map = new HashMap<>();
                    map.put("id",textContent.getId());
                    map.put("creatorName", textContent.getCreatorName());
                    map.put("analysisDate",textContent.getAnalysisDate());
                    map.put("name", textContent.getName());
                    map.put("subtitle",textContent.getSubTitle());
                    map.put("isSel",false);
                    allTextMapList.add(map);
                }
                for(TextContent textContent: textContentListByUser){
                    Map<String, Object> map = new HashMap<>();
                    map.put("id",textContent.getId());
                    map.put("creatorName", textContent.getCreatorName());
                    map.put("analysisDate",textContent.getAnalysisDate());
                    map.put("name", textContent.getName());
                    map.put("subtitle",textContent.getSubTitle());
                    map.put("isSel",false);
                    userTextMapList.add(map);
                }
                result.put("all", allTextMapList);
                result.put("user", userTextMapList);
                return result;
            }
        }
        return "无数据";
    }

    /**
     * 返回分析内容列表
     *
     * @param user
     * @param contentValue
     * @return
     */
    private Object getTextContent(CurrentUser user, String contentValue) {
        int contentId = Integer.parseInt(contentValue);
        if (1 == 1) {// 权限验证

            TextContent result = textContentService.queryById(contentId);
            if (result != null) {
                return result;
            }
        }
        return "无数据";
    }

    /**
     * 返回综合表
     * <pre>
     *     根据综合表id，查询综合表最新报告期的报表和所有报告期
     *     报表验证权限，返回审核通过的最新一期报告期的报表,
     *     返回的报表，带表格样式，报表填充数据
     * </pre>
     *
     * @param user         当前用户
     * @param contentValue 综合表id
     * @return 有数据：map类型：{table:"<table></table>",
     * reportInfos:List<ReportInfo>}，无数据：无数据
     */
    public Object getReport(CurrentUser user, String contentValue) throws Exception {

        int rptTmpId = Integer.parseInt(contentValue);

        if (checkPermission(rptTmpId, user)) {

            List<ReportInfo> reportInfos = reportInfoService.getInfosByTmpIdAndStatus(rptTmpId, "4");

            if (reportInfos.size() > 0) {
//                    最新报告期id
                Integer rptInfoId = reportInfos.get(0).getId();
                return getReportMap(reportInfoService.getRptInfoHtml(rptInfoId), reportInfos);
            }
        }
        return "无数据";
    }

    /**
     * 返回综合表，和所有报告期
     * <pre>
     *     根据综合表id，查询最新一期报告期的报表，同时返回所有报告期
     *     查询条件，综合表id，该报告期报表状态，表样是否填充数据，
     *     表样是否带表格宽度和高度
     *     带用户权限验证
     * </pre>
     *
     * @param vo {@code 查询参数
     *           //综合表某个报告期id
     *           id:12,
     *           //状态：0：全部；1:待填报；2：草稿；3：待审；4：已审；5：已驳回
     *           //使用逗号分隔
     *           status:2,3,4,
     *           //是否填充数据：0：否；1：是
     *           hasData:1,
     *           //是否带表格宽度和高度：0：否；1：是
     *           hasStyle:1
     *           }
     * @return {@code 成功
     * data:{
     * //对应查询报告期的报表
     * table:"<table></table>",
     * //所有该综合表的所有报告期
     * reportInfos:List<ReportInfo>
     * }
     * }
     * @author hzc
     * @createDate 2016-3-29
     */
    public Object getReport(ReportVO vo, CurrentUser user) throws Exception {

        int rptTmpId = vo.getId();

        if (checkPermission(rptTmpId, user)) {

            Object infos = getReportByIdAndStatus(vo);
            if (infos != null) return infos;
        }
        return "无数据";
    }

    /**
     * 返回综合表，和所有报告期
     * <pre>
     *     根据综合表id，查询最新一期报告期的报表，同时返回所有报告期
     *     查询条件，综合表id，该报告期报表状态，表样是否填充数据，
     *     表样是否带表格宽度和高度
     * </pre>
     *
     * @param vo {@code 查询参数
     *           //综合表某个报告期id
     *           id:12,
     *           //状态：0：全部；1:待填报；2：草稿；3：待审；4：已审；5：已驳回
     *           //使用逗号分隔
     *           status:2,3,4,
     *           //是否填充数据：0：否；1：是
     *           hasData:1,
     *           //是否带表格宽度和高度：0：否；1：是
     *           hasStyle:1
     *           }
     * @return {@code 成功
     * data:{
     * //对应查询报告期的报表
     * table:"<table></table>",
     * //所有该综合表的所有报告期
     * reportInfos:List<ReportInfo>
     * }
     * }
     * @author hzc
     * @createDate 2016-3-29
     */
    public Map getReportByIdAndStatus(ReportVO vo) throws Exception {
        List<ReportInfo> infos = reportInfoService.getInfosByTmpIdAndStatus(vo.getId(), vo.getStatus());

//          当前综合表有相关状态的报告期
        if (null != infos && infos.size() > 0) {

//              有效报告期中的最新一期报告期
            ReportInfo info = infos.get(0);
            vo.setId(info.getId());

            return getReportMap(reportInfoService.getRptInfoHtml(vo), infos);
        }
        return null;
    }

    /**
     * 返回综合表，和所有报告期
     * <pre>
     *     根据综合表某个报告期id，查询综合表对应报告期报表，同时返回本期报表，和所有报告期
     *     带用户权限验证
     *     过滤报告期状态
     * </pre>
     *
     * @param vo   查询参数
     *             //综合表某个报告期id
     *             id:12,
     *             //状态：0：全部；1:待填报；2：草稿；3：待审；4：已审；5：已驳回
     *             //使用逗号分隔
     *             status:2,3,4,
     *             //是否填充数据：0：否；1：是
     *             hasData:1,
     *             //是否带表格宽度和高度：0：否；1：是
     *             hasStyle:1
     * @param user 当前用户
     * @return 有数据：
     * map类型：
     * {table:"<table></table>",reportInfos:List<ReportInfo>}，
     * 无数据：无数据
     * @author hzc
     * @createDate 2016-3-28
     */
    public Object getReportByInfo(ReportVO vo, CurrentUser user) throws Exception {

        Integer rptInfoId = vo.getId();

        ReportInfo info = reportInfoService.getReportInfosByRptInfoId(rptInfoId);

        //        过滤查询条件，报表状态
        if (vo.getStatus().indexOf(String.valueOf(info.getRptStatus())) < 0) {
            return "无数据";
        }

        Integer rptTmpId = info.getTmpId();

        boolean permission = checkPermission(rptTmpId, user);

        if (permission) {

            List<ReportInfo> reportInfos = reportInfoService.getInfosByTmpIdAndStatus(rptTmpId, vo.getStatus());

            return getReportMap(reportInfoService.getRptInfoHtml(vo), reportInfos);
        }

        return "无数据";
    }

    /**
     * 返回综合表某个报告期报表和所有报告期
     *
     * @param table       报表
     * @param reportInfos 所有报告期
     * @return map类型：{table:"<table></table>",reportInfos:List<ReportInfo>}
     * @author hzc
     * @createDate 2016-3-28
     */
    private Map getReportMap(String table, List<ReportInfo> reportInfos) {

        HashMap<String, Object> map = new HashMap<>();

        map.put("table", table);
        map.put("reportInfos", reportInfos);

        return map;
    }

    /**
     * 判断综合表权限
     * <pre>
     *     超级管理员跳过权限验证
     * </pre>
     *
     * @param rptTmpId 综合表id
     * @param user     当前用户
     * @return 有权限：true；无权限：false
     * @author hzc
     * @createDate 2016-3-28
     */
    private boolean checkPermission(Integer rptTmpId, CurrentUser user) {

        if (user.getUser().isAdmin()) return true;

        Map<Integer, ReportPermission> reportPermission = user.getReportPermissionMap();
        if (!reportPermission.isEmpty()) {

            for (Integer key : reportPermission.keySet()) {

                if (key.equals(rptTmpId)) {

                    ReportPermission permission = reportPermission.get(key);

                    if (permission.isRead()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * 返回分析报表
     * <pre>
     *     根据分析报表id查询，返回报表，报表时间范围，报表表样，
     *     如果报表为最新报告期（时间范围为报告期数），则返回最新有效时间报告期
     *     返回为map类型：
     *     {"research":CustomResearchEntity,"timeRange":TimeRangeEntity,
     *     "table":"<table></table>",
     *     "periods":[{//频度 frequency:1,//年 year:2015,
     *     // 分析报表期度：年：12，半年：6、12，季：3、6、9、12，月：1、2、3、4、5、6、7、8、9、10、11、12
     *          period:List<Integer>}]}
     *     带数据，带表格高度和宽度
     * </pre>
     *
     * @param contentValue 分析报表id
     * @return
     * @author hzc
     * @createDate 2016-3-28
     */
    public Map getResearch(String contentValue) {
        return getCustomResearch(new ResearchVO(Integer.parseInt(contentValue)));
    }

    /**
     * 返回分析报表
     * <pre>
     *     根据分析报表id查询，返回报表，报表时间范围，报表表样，
     *     如果报表为最新报告期（时间范围为报告期数），则返回所有报告期
     *     返回为map类型：
     *     {"research":CustomResearchEntity,"timeRange":TimeRangeEntity,
     *     "table":"<table></table>",
     *     "periods":[{//频度 frequency:1,//年 year:2015,
     *     //月，季，半年 period:2 }]}
     * </pre>
     *
     * @param vo 查询条件：{id:12,hasData:1,hasStyle:1}
     * @return 成功
     * <pre>
     *  {
     *   //自定义查询bean信息
     *   "research":{
     *       //id
     *       "id":48,
     *       //自定义查询分组id
     *       "researchGroupId":2,
     *      //名称
     *      "name":"aa",
     *      //数据源
     *       "dataSet":{
     *           //数据源id
     *           "id":21,
     *            //数据源名称
     *            "name":"ceshi",
     *            //数据源说明
     *            "comments":"test"
     *        },
     *       //自定义查询类型
     *       "type":1,
     *       //时间频度：年报，半年报，季报，月报
     *      "period":1
     *  },
     *   //时间范围：查询报表是时间范围是否为报告期数:
     *   //1:连续时间范围，2：选择时间范围，3：报告期数
     *   "timeRange":{
     *       //id
     *       "id": 2039,
     *       //自定义查询id或图表id
     *       "foreignId": 48,
     *       //关联类型：1：报表类型，2：图表类型
     *       "foreignType": 1,
     *       //时间范围类型：1:连续时间范围，2：选择时间范围，3：报告期数
     *       "type": 3,
     *       //时间类型：开始年：1，开始期度：2，结束年：3，
     *       //结束期度：4，年份：5，期度：6，报告期数：7
     *       "dataType": 1,
     *      //时间类型值
     *      "dataValue": 2010,
     *      //状态
     *      "status": 1
     *  },
     *  //自定义查询表
     *  "table":"<table></table>",
     *  "periods":[
     *      {
     *          //频度
     *          frequency:2,
     *          //年
     *          year:2015,
     *          // 分析报表期度：年：12，半年：6、12，季：3、6、9、12，月：1、2、3、4、5、6、7、8、9、10、11、12
     *          period:List<Integer>
     *      }
     *   ]
     * }
     * </pre>
     * @author hzc
     * @createDate 2016-3-28
     */
    public Map getCustomResearch(ResearchVO vo) {

        HashMap<String, Object> map = new HashMap<>();

//        自定义查询bean
        int researchId = vo.getId();
        CustomResearchEntity research = researchService.getCustomResearchById(researchId);
        map.put("research", research);

//        如果自定义查询为报告期数，则此timeRange有用，否则无用
        TimeRangeEntity rangeEntity = null;
        if (null != research) {
            List<TimeRangeEntity> timeRangeList = rangeService.queryTimeRange(Constant.TIMERANGE.TYPE_REPORT, researchId);
            rangeEntity = timeRangeList.get(0);
        }
        map.put("timeRange", rangeEntity);

//        分析报表有效报告期时间：年，月
        List<ResearchTimePojo> pojos = null;
        List<TimePojo> periods = null;
        if (null != research) {
            periods = queryRptService.getResearchTime(research);
            pojos = queryRptService.genResearchTime(periods);
        }
        map.put("periods", pojos);

        TimePojo timePojo;
        String table;
        if (null != periods && periods.size() > 0) {
            timePojo = periods.get(0);
            table = queryRptService.generateReport(researchId, timePojo, vo.getHasData() == 1, vo.getHasStyle() == 1);
        } else {
            table = "暂无数据";
        }
//        自定义查询表
        map.put("table", table);

        return map;
    }

    /**
     * 返回目录
     *
     * @param user
     * @param contentValue
     * @return
     */
    private Object getMenus(CurrentUser user, String contentValue) {

        List<ThemePage> pages = themesService.getThemesPageByIds(contentValue.replace(";", ","));
//                过滤权限
        if (user.getUser().isAdmin()) {
            return pages;
        }
        Set<Role> roles = user.getUser().getRoles();
        Iterator<Role> iterator = roles.iterator();
        StringBuffer roleStr = new StringBuffer();
        while (iterator.hasNext()) {
            Role role = iterator.next();
            roleStr.append(role.getId());
            roleStr.append(",");
        }
        for (int i = 0; i < pages.size(); i++) {
            ThemePage page = pages.get(i);
            String s = page.getRole();
            if (!StringUtils.isEmpty(s)) {
                String[] split = s.split(";");
                boolean rs = false;
                for (int j = 0; j < split.length; j++) {
                    String ss = split[j];
                    if (roleStr.indexOf(ss) > -1) {
                        rs = true;
                    }
                }
                if (!rs) {
                    pages.remove(page);
                    i--;
                }
            }
        }
        return pages;
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
     * @return 如果成功，返回报表
     * @author hzc
     * @createDate 2016-3-25
     */
    public String getPeriodCustomResearch(ResearchVO vo) {
        return queryRptService.queryCustomRpt(vo);
    }

    /**
     * 返回综合表和符合条件的综合表报告期集合
     * <pre>
     *     根据综合表id，year，m，查询综合表对应的报告期报表
     *     返回综合表表样，过滤用户权限
     *     查询条件：是否带表格高度和宽度，是否填充数据，状态
     * </pre>
     *
     * @param vo   查询条件
     *             {@code
     *             {
     *             //综合表id
     *             id:12,
     *             //状态：0：全部；1:待填报；2：草稿；3：待审；4：已审；5：已驳回
     *             //使用逗号分隔，可为空
     *             status:2,3,4,
     *             //是否填充数据：0：否；1：是
     *             hasData:1,
     *             //是否带表格宽度和高度：0：否；1：是
     *             hasStyle:1,
     *             //报告期年，可为空
     *             year:2016,
     *             //报告期期度：年，半年，季，月，可为空
     *             m:3,
     *             //综合表频度：1：年报，2：半年报，3：季报，4：月报，可为空
     *             frequency:3
     *             }
     *             }
     * @param user 当前用户
     * @return 返回结果
     * 成功：{
     * //对应查询报告期的报表
     * table:"<table></table>",
     * //所有该综合表的所有报告期
     * reportInfos:List<ReportInfo>
     * }
     * 失败：程序错误
     * @author hzc
     * @createDate 2016-3-30
     */
    public Object getReportByTime(ReportVO vo, CurrentUser user) throws Exception {

        Integer id = vo.getId();

        boolean permission = checkPermission(id, user);
        if (permission) {

            Object map = getReportByTime(vo);
            if (map != null) return map;

            return "无数据";
        }

        return "无权限";
    }

    /**
     * 返回综合表和符合条件的综合表报告期集合
     * <pre>
     *     根据综合表id，year，m，查询综合表对应的报告期报表
     *     返回综合表表样
     *     查询条件：是否带表格高度和宽度，是否填充数据，状态
     * </pre>
     *
     * @param vo 查询条件
     *           {@code
     *           {
     *           //综合表id
     *           id:12,
     *           //状态：0：全部；1:待填报；2：草稿；3：待审；4：已审；5：已驳回
     *           //使用逗号分隔，可为空
     *           status:2,3,4,
     *           //是否填充数据：0：否；1：是
     *           hasData:1,
     *           //是否带表格宽度和高度：0：否；1：是
     *           hasStyle:1,
     *           //报告期年，可为空
     *           year:2016,
     *           //报告期期度：年，半年，季，月，可为空
     *           m:3,
     *           //综合表频度：1：年报，2：半年报，3：季报，4：月报，可为空
     *           frequency:3
     *           }
     *           }
     * @return 返回结果
     * 成功：{
     * //对应查询报告期的报表
     * table:"<table></table>",
     * //所有该综合表的所有报告期
     * reportInfos:List<ReportInfo>
     * }
     * @author hzc
     * @createDate 2016-3-30
     */
    public Map getReportByTime(ReportVO vo) throws Exception {

        List<ReportInfo> infos = reportInfoService.getInfoByTime(vo);
        if (null != infos && infos.size() > 0) {

//                符合条件的最新一期报告期
            ReportInfo info = infos.get(0);
            vo.setId(info.getId());

            return getReportMap(reportInfoService.getRptInfoHtml(vo), infos);
        }
        return null;
    }

    /**
     * 返回分析报表
     * <pre>
     *     根据分析报表id，查询分析报表,
     *     如果为最新报告期，则返回有效报告期最新一期
     *     无其他查询条件，只返回表样（填充数据，不带表格样式）
     * </pre>
     *
     * @param id 分析报表id
     * @return
     * @author hzc
     * @createDate 2016-4-1
     */
    public String getResearchById(Integer id) {
        CustomResearchEntity research = researchService.getCustomResearchById(id);
        //        分析报表有效报告期时间：年，月
        List<TimePojo> periods = queryRptService.getResearchTime(research);
        List<ResearchTimePojo> pojos = queryRptService.genResearchTime(periods);

        TimePojo timePojo;
        if (null != pojos && pojos.size() > 0) {
            ResearchTimePojo pojo = pojos.get(0);
            timePojo = new TimePojo(pojo.getFrequency(), pojo.getYear(), pojo.getPeriods().get(0));
        } else {
            timePojo = null;
        }

        return queryRptService.generateReport(id, timePojo, true, false);
    }

    /**
     * 返回数据集数据
     * <pre>
     *   返回数据集中的所有数据，不过滤指标，不过滤时间
     * </pre>
     *
     * @param contentValue 数据集id
     * @return
     * @author hzc
     * @createDate 2016-4-5
     */
    private List<RptDataPojo> getDataSetData(String contentValue) {
        return queryRptService.queryRptDatas(Integer.parseInt(contentValue), null, null);
    }

    /**
     * 返回数据集数据中的时间框架，和该时间框架对应的数据
     * <pre>
     *     根据数据集id，查询数据集中的最新一期数据的时间框架
     *     无数据返回空（null）
     * </pre>
     *
     * @param dataSetId 数据集id
     * @return 返回数据集时间框架集合
     * @author hzc
     * @createDate 2016-4-5
     */
    public List<DataSetTimeFrame> getNewDataSetData(Integer dataSetId) {
        TimeRangePojo pojo = new TimeRangePojo(Constant.TIMERANGE.BAOGAOQI);
        ArrayList<TimePojo> list = new ArrayList<>();
        list.add(pojo);

//        获取数据集中的最新一期的数据
        List<RptDataPojo> datas = queryRptService.queryRptDatas(dataSetId, null, list);

        ArrayList<DataSetTimeFrame> chartDatas = new ArrayList<>();
        if (null != datas && datas.size() > 0) {
            for (int i = 0; i < datas.size(); i++) {
                RptDataPojo dataPojo = datas.get(i);
                List<ReportData> reportDatas = dataPojo.getDatas();

//                组装结果
                if (null != reportDatas && reportDatas.size() > 0) {
                    for (int j = 0; j < reportDatas.size(); j++) {
                        ReportData reportData = reportDatas.get(j);
                        Integer timeFrame = reportData.getReportDataId().getTimeFrame();
                        TimeFrame frame = timeFrameService.getEntityById(timeFrame);
                        DataSetTimeFrame chartData = new DataSetTimeFrame(frame.getId(), frame.getName(), reportData.getItemValue());
                        chartDatas.add(chartData);
                    }
                }
            }
        }
        return chartDatas;
    }

    /**
     * 返回所有数据
     * <pre>
     *     根据配置集合，返会所有配置详细信息
     * </pre>
     *
     * @param contents 所有配置主题
     * @param user     当前用户
     * @return
     * @author hzc
     * @createDate 2016-4-8
     */
    public Object returnAllData(List<ThemePageContent> contents, CurrentUser user) throws Exception {

        LinkedList<Object> list = new LinkedList<>();

        if (null != contents && contents.size() > 0) {
            for (int i = 0; i < contents.size(); i++) {

                ThemePageContent content = contents.get(i);

                HashMap<String, Object> map = new HashMap<>();
                map.put("content", content);

                Object o = returnData(content, user);
                map.put("data", o);

                list.add(map);
            }
        }
        return list;
    }

    /**
     * 返回重点关注菜单
     * <pre>
     *     返回重点关注菜单集合，集合是树结构，保存在 child 中
     * </pre>
     *
     * @param menus 菜单id的字符串，使用英文逗号（,）分隔
     * @return
     * @author hzc
     * @createDate 2016-4-22
     */
    public Object getSyntheticalMenus(String menus) {

        menus = menus.replaceAll(";", ",");

        List<ThemePage> list = themesService.getThemesPageByIds(menus);

        List<ThemePage> treeList = listToTreeList(list);

        ArrayList<Object> result = new ArrayList<>();

//        处理树非第一级的节点
        for (int i = 0; i < treeList.size(); i++) {

            ArrayList<ThemePage> newList = new ArrayList<>();

            ThemePage themePage = treeList.get(i);

            platTree(themePage.getChild(), 1, newList);

            themePage.setChild(newList);

            result.add(themePage);
        }

        return result;
    }

    /**
     * 拆平树
     * <pre>
     *     将树的级别，每2级变为一级
     * </pre>
     *
     * @param list    原树集合
     * @param cengji  初始层级：0：树的第一级和第二级开始变为一级，1：树的第二级和第一级开始变为一级
     * @param newList 新树集合
     * @author hzc
     * @createDate 2016-4-22
     */
    private void platTree(List<ThemePage> list, Integer cengji, List newList) {

        if (null != list && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {

                ThemePage themePage = list.get(i);
                List<ThemePage> child = themePage.getChild();

//                  层级为2的倍数时，拆平
                if (cengji % 2 == 0) {

                    themePage.setTitle(true);
                    themePage.setChild(new ArrayList<ThemePage>());

                    newList.add(themePage);
                    if (null != child && child.size() > 0) {

                        for (int j = 0; j < child.size(); j++) {
                            child.get(j).setTitle(false);
                        }

                        newList.addAll(child);

                        platTree(child, cengji + 1, new ArrayList());
                    }
                } else {

                    if (null != child && child.size() > 0) {

                        themePage.setChild(new ArrayList<ThemePage>());
                        platTree(child, cengji + 1, themePage.getChild());

                    }

                    newList.add(themePage);
                }
            }
        }
    }

    /**
     * 将list封装成treeList，返回新list
     *
     * @param list
     * @return 树结构的list
     * @author hzc
     * @createDate 2016-4-22
     */
    private List<ThemePage> listToTreeList(List<ThemePage> list) {

        List<ThemePage> result = new LinkedList<>();

        if (null != list && list.size() > 0) {

            for (int i = 0; i < list.size(); i++) {
                ThemePage themePage = list.get(i);
                for (int j = 0; j < list.size(); j++) {
                    ThemePage themePageJ = list.get(j);

                    if (String.valueOf(themePageJ.getParentId()).equals(String.valueOf(themePage.getId()))) {
//                        当前对象不是root节点
                        themePageJ.setRoot(false);
                        themePage.getChild().add(themePageJ);
                    }
                }

                result.add(themePage);
            }

//            删除非根节点的bean
            for (int i = 0; i < result.size(); i++) {
                ThemePage themePage = result.get(i);
                if (!themePage.isRoot()) {

                    result.remove(themePage);
                    i--;
                }
            }
        }

        return result;
    }
}
