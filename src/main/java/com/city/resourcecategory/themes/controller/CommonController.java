package com.city.resourcecategory.themes.controller;

import com.city.common.controller.BaseController;
import com.city.resourcecategory.themes.entity.ThemePage;
import com.city.resourcecategory.themes.entity.ThemePageContent;
import com.city.resourcecategory.themes.pojo.ReportVO;
import com.city.resourcecategory.themes.pojo.ResearchVO;
import com.city.resourcecategory.themes.service.CommonService;
import com.city.resourcecategory.themes.service.ManageThemesService;
import com.city.support.regime.collection.service.ReportInfoService;
import com.city.support.sys.user.entity.Role;
import com.city.support.sys.user.entity.User;
import com.city.support.sys.user.pojo.CurrentUser;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * 主题对接前台类
 * Created by HZC on 2016/3/18.
 */
@Controller
@RequestMapping("/resourcecategory/themes/commonController")
public class CommonController extends BaseController {

    @Autowired
    private CommonService commonService;

    @Autowired
    private ManageThemesService themesService;

    @Autowired
    private ReportInfoService reportInfoService;

    private static final String COMMONPATH = "/resourceCategory/themes/";

    /**
     * 返回首页
     * <pre>
     *     返回前台主页
     * </pre>
     *
     * @return 返回页面
     * @author hzc
     * @createDate 2016-3-18
     */
    @RequestMapping("/returnIndex")
    public ModelAndView returnIndex(HttpServletRequest request) {
        User user = CurrentUser.getCurrentUser(request).getUser();
        ModelAndView mv = new ModelAndView();
        ThemePage page = commonService.getIndex();
        mv.setViewName(COMMONPATH + page.getModulePath());
        Gson gson = new Gson();
        mv.addObject("page", gson.toJson(page));
        mv.addObject("user", user);
        return mv;
    }

    /**
     * 返回页面
     * <pre>
     *     根据主题配置id，返回对应页面
     *     并且验证用户是否有权限访问该页面
     *             uuid:页面唯一标示
     * </pre>
     *
     * @param themePageId 主题配置id
     * @return
     * @author hzc
     * @createDate 2016-3-18
     */
    @RequestMapping("/returnPage")
    public ModelAndView returnPage(Integer themePageId, HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();

        ThemePage page = commonService.getThemePage(themePageId);

        if (null == page || page.getModulePath() == null) {

            mv.setViewName("/common/noConfig");
        } else {

            CurrentUser user = CurrentUser.getCurrentUser(request);
//        验证权限
            boolean hasRole = false;

            if (user.getUser().isAdmin()) {
                hasRole = true;
            } else {

                Set<Role> roles = user.getUser().getRoles();
                for (Role role : roles) {
                    if (null == page.getRole() || page.getRole().indexOf(role.getId().toString()) > -1) {
                        hasRole = true;
                    }
                }
            }

//        有权限
            if (hasRole) {

                Gson gson = new Gson();
                mv.addObject("page", gson.toJson(page));

                mv.setViewName(COMMONPATH + page.getModulePath());
                mv.addObject("uuid", UUID.randomUUID());
            } else {

//            无权限
                mv.setViewName("/common/noAuthority");
            }
        }

        return mv;
    }

    /**
     * 返回数据
     * <pre>
     *     根据查询数据类型，和数据id，返回数据
     *     1：综合表：验证权限，不过滤状态，填充数据，表格有样式
     *     2：分析报表：无权限验证，返回综合表，报表时间范围，报表表样，
     *        如果报表为最新报告期（时间范围为报告期数），则返回所有报告期，可为空
     *        不过滤状态，填充数据，表格有样式
     *     3：图表：
     *     4：地图：
     *     5：文字分析：
     *     6：文件：
     *     7：目录：
     *     9：数据集：根据数据集id，返回所有数据集数据
     * </pre>
     *
     * @param content 查询参数：{contentValue:12,contentType:8}
     * @return 返回对应数据
     * @author hzc
     * @createDate 2016-3-18
     */
    @RequestMapping("/returnData")
    @ResponseBody
    public Object returnData(ThemePageContent content, HttpServletRequest request) throws Exception {
        CurrentUser user = CurrentUser.getCurrentUser(request);
        return genSuccessMsg(commonService.returnData(content, user), "成功", null);
    }

    /**
     * 返回综合表
     * <pre>
     *     根据综合表的个报告期id，查询综合表
     *     不过滤报告期状态，返回表，填充数据，带表格样式
     * </pre>
     *
     * @param id 综合表报告期id
     * @return
     * @author hzc
     * @createDate 2016-3-21
     */
    @RequestMapping("/returnReportByReportInfo")
    @ResponseBody
    public Object returnReportByReportInfo(Integer id) throws Exception {
        return genSuccessMsg(reportInfoService.getRptInfoHtml(id), "操作成功", null);
    }

    /**
     * 返回综合表
     * <pre>
     *     根据报表id，返回综合表最新一期填报完成的报表，同时返回所有报表报告期,
     *     验证报表用户权限，返回的报表带数据和表格样式
     * </pre>
     *
     * @param request
     * @param id      综合表id
     * @return {@code
     * 有数据：map类型：{table:"<table></table>",
     * reportInfos:List<ReportInfo>}，无数据：无数据
     * }
     * @author hzc
     * @createDate 2016-3-23
     */
    @RequestMapping("/returnReports")
    @ResponseBody
    public Object returnReportS(HttpServletRequest request, String id) throws Exception {
        return genSuccessMsg(commonService.getReport(CurrentUser.getCurrentUser(request), id), "请求成功", null);
    }

    /**
     * 返回自定义查询
     * <pre>
     *     根据分析报表id查询，返回报表，报表时间范围，报表表样，
     *     如果报表为最新报告期（时间范围为报告期数），则返回最新有效时间报告期
     *     带数据，带表格高度和宽度
     * </pre>
     *
     * @param id 分析报表id
     * @return 返回结果
     * {@code
     * {datas:{"research":CustomResearchEntity,"timeRange":TimeRangeEntity,
     * "table":"<table></table>","periods":[{//频度 frequency:1,//年 year:2015,
     * //月，季，半年 period:2 }]},
     * code:200,success:true,msg:"请求成功"}
     * }
     */
    @RequestMapping("/returnResearchs")
    @ResponseBody
    public Object returnResearchs(String id) {
        return genSuccessMsg(commonService.getResearch(id), "请求成功", null);
    }

    /**
     * 返回详情页
     * <pre>
     *     返回url对应的页面，同时返回页面传过来的数据
     *     传递参数：url：返回的页面
     *             data：传递到url页面的数据
     *             uuid:页面唯一标示
     * </pre>
     *
     * @param url 地址url
     * @return
     * @author CRX
     * @createDate 2016-3-22
     */
    @RequestMapping("/returnDetails")
    public ModelAndView returnDetails(HttpServletRequest request, String url) {

        ModelAndView mv = new ModelAndView(COMMONPATH + url);

        String data = request.getParameter("data");
        mv.addObject("data", data);

        mv.addObject("uuid", UUID.randomUUID());

        return mv;
    }


    /**
     * 返回某期分析报表
     * <pre>
     *     根据分析报表报告期，查询不同期报表数据的报表。
     *     报表是否带样式，与是否带数据，根据查询条件决定
     * </pre>
     *
     * @param vo 查询条件
     *           {@code
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
     * @return 返回结果
     * 成功：{@code
     * {datas:"<table></table>,code:200,success:true,msg:"请求成功"}
     * }
     * 失败：程序错误
     * @author hzc
     * @createDate 2016-3-25
     */
    @RequestMapping("/getPeriodCustomResearch")
    @ResponseBody
    public Object getPeriodCustomResearch(ResearchVO vo) {
        return genSuccessMsg(commonService.getPeriodCustomResearch(vo), "请求成功", null);
    }

    /**
     * 返回分析报表
     * <pre>
     *     根据分析报表id查询，返回报表，报表时间范围，报表表样，
     *     如果报表为最新报告期（时间范围为报告期数），则返回当前分析报表有效报告期的最新时间期
     *
     *
     * </pre>
     *
     * @param vo 查询条件：
     *           {@code
     *           //分析报表id
     *           id:12,
     *           //是否填充数据：0：否，1：是
     *           hasData:1,
     *           //是否带表格宽度和高度：0：否，1：是
     *           hasStyle:1
     *           }
     * @return 返回结果：
     * {@code
     * {datas:{"research":CustomResearchEntity,"timeRange":TimeRangeEntity,
     * "table":"<table></table>","periods":[{//频度 frequency:1,//年 year:2015,
     * // 分析报表期度：年：12，半年：6、12，季：3、6、9、12，月：1、2、3、4、5、6、7、8、9、10、11、12
     * period:List<Integer> }]},
     * code:200,success:true,msg:"请求成功"}
     * }
     * @author hzc
     * @createDate 2016-3-28
     */
    @RequestMapping("/getCustomResearch")
    @ResponseBody
    public Object getCustomResearch(ResearchVO vo) {
        return genSuccessMsg(commonService.getCustomResearch(vo), "请求成功", null);
    }

    /**
     * 返回综合表，和所有报告期
     * <pre>
     *     根据综合表报告期id，查询综合表对应报告期报表，
     *     同时返回本期报表，和所有符合状态的报告期
     *     带用户权限验证
     *     过滤状态
     * </pre>
     *
     * @param vo 查询参数
     *           {@code
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
     * @return 成功
     * {@code
     * {datas:data,msg:"请求成功",success:true,code:200}
     * data:{
     * //对应查询报告期的报表
     * table:"<table></table>",
     * //所有该综合表的所有报告期
     * reportInfos:List<ReportInfo>
     * }
     * }
     * @author hzc
     * @createDate 2016-3-28
     */
    @RequestMapping("/getReportByInfo")
    @ResponseBody
    public Object getReportByInfo(ReportVO vo, HttpServletRequest request) throws Exception {

        CurrentUser user = CurrentUser.getCurrentUser(request);

        return genSuccessMsg(commonService.getReportByInfo(vo, user), "请求成功", null);
    }

    /**
     * 返回综合表，和所有报告期
     * <pre>
     *     根据综合表id，查询最新一期报告期的报表，同时返回所有符合查询状态的报告期
     *     查询条件，综合表id，该报告期报表状态，表样是否填充数据，表样是否带表格宽度和高度
     *     带用户权限验证
     *     过滤状态
     * </pre>
     *
     * @param vo 查询参数
     *           //综合表id
     *           id:12,
     *           //状态：0：全部；1:待填报；2：草稿；3：待审；4：已审；5：已驳回
     *           //使用逗号分隔
     *           status:2,3,4,
     *           //是否填充数据：0：否；1：是
     *           hasData:1,
     *           //是否带表格宽度和高度：0：否；1：是
     *           hasStyle:1
     * @return 成功
     * {datas:data,msg:"请求成功",success:true,code:200}
     * data:{
     * //对应查询报告期的报表
     * table:"<table></table>",
     * //所有该综合表的所有报告期
     * reportInfos:List<ReportInfo>
     * }
     * @author hzc
     * @createDate 2016-3-29
     */
    @RequestMapping("/getReport")
    @ResponseBody
    public Object getReport(ReportVO vo, HttpServletRequest request) throws Exception {

        CurrentUser user = CurrentUser.getCurrentUser(request);

        Object report = commonService.getReport(vo, user);

        return genSuccessMsg(report, "请求成功", null);
    }

    /**
     * 返回综合表和符合条件的综合表报告期集合
     * <pre>
     *     根据综合表id，year，m，查询综合表对应的报告期报表
     *     返回综合表表样，过滤用户权限
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
     *           //频度：1：年报，2：半年报，3：季报，4：月报，可为空
     *           frequency:2
     *           }
     *           }
     * @return 返回结果
     * {@code
     * 成功：{datas:data,msg:"请求成功",success:true,code:200}
     * data:{
     * //对应查询报告期的报表
     * table:"<table></table>",
     * //所有该综合表的所有报告期
     * reportInfos:List<ReportInfo>
     * }
     * 失败：程序错误
     * @author hzc
     * @createDate 2016-3-30
     */
    @RequestMapping("/getReportByTime")
    @ResponseBody
    public Object getReportByTime(HttpServletRequest req, ReportVO vo) throws Exception {

        CurrentUser user = CurrentUser.getCurrentUser(req);

        return genSuccessMsg(commonService.getReportByTime(vo, user), "请求成功", 200);
    }


    /**
     * 返回分析报表
     * <pre>
     *     根据分析报表id，查询分析报表,
     *     如果为最新报告期，则返回有效报告期最新一期
     *     无其他查询条件，只返回表样（填充数据，带表格样式）
     * </pre>
     *
     * @return
     * @author hzc
     * @createDate 2016-4-1
     */
    @RequestMapping("/getResearch")
    @ResponseBody
    public Object getResearch(Integer id) {

        ResearchVO vo = new ResearchVO();
        vo.setId(id);
        vo.setHasData(1);
        vo.setHasStyle(1);

        Map map = commonService.getCustomResearch(vo);

        return genSuccessMsg(map.get("table"), "请求成功", 200);
    }

    /**
     * 返回综合表
     * <pre>
     *     根据综合表id，year，m，查询综合表对应的报告期报表
     *     带数据，带表格样式
     *     返回综合表表样
     * </pre>
     *
     * @param vo 查询条件
     *           {@code
     *           {
     *           //综合表id
     *           id:12,
     *           //报告期年，可为空
     *           year:2016,
     *           //报告期期度：年，半年，季，月，可为空
     *           m:3
     *           }
     *           }
     * @return 返回结果
     * {@code
     * 成功：{datas:"<table></table>",msg:"请求成功",success:true,code:200}
     * 失败：程序错误
     * }
     * @author hzc
     * @createDate 2016-3-30
     */

    @RequestMapping("/getOnlyReportByTime")
    @ResponseBody
    public Object getOnlyReportByTime(ReportVO vo) throws Exception {

        vo.setHasData(1);
        vo.setHasStyle(1);
        vo.setStatus("4");

        Map map = commonService.getReportByTime(vo);
        return genSuccessMsg(null != map ? map.get("table") : "无数据", "请求成功", 200);
    }

    /**
     * 返回综合表
     * <pre>
     *     根据综合表id，查询最新一期报告期的报表
     *     查询条件，综合表id，
     *     带数据，带表格样式
     * </pre>
     *
     * @param id {@code 查询参数
     *           //综合表某个报告期id
     *           id:12
     *           }
     * @return {@code {datas:"<table></table>",msg:"请求成功",success:true,code:200}
     * }
     * @author hzc
     * @createDate 2016-3-29
     */
    @RequestMapping("/getOnlyReportById")
    @ResponseBody
    public Object getOnlyReportById(Integer id) throws Exception {

        ReportVO vo = new ReportVO();
        vo.setId(id);
        vo.setHasData(1);
        vo.setHasStyle(1);
        vo.setStatus("4");

        Map map = commonService.getReportByIdAndStatus(vo);
        return genSuccessMsg(null != map ? map.get("table") : "无数据", "请求成功", 200);
    }

    /**
     * 返回数据集中最新一期数据的时间框架
     * <pre>
     *     根据数据集id，查询最新一期数据，返回数据中的时间框架
     * </pre>
     *
     * @param id 数据集id
     * @return 返回结果
     * {@code
     * 成功：{datas:List<DataSetTimeFrame>,msg:"请求成功",success:true,code:200}
     * 失败：程序错误
     * }
     * @author hzc
     * @createDate 2016-4-5
     */
    @RequestMapping("/getTimeFrames")
    @ResponseBody
    public Object getTimeFrames(Integer id) {
        return genSuccessMsg(commonService.getNewDataSetData(id), "请求成功", 200);
    }

    /**
     * 跳转到下级页面
     * <pre>
     *     根据主题配置，依据当前配置的页面的id，
     *     返回该页面的下级页面
     *     页面返回：
     *     {@code
     *          data：上级页面传给下级页面的数据
     *          uuid：页面唯一id
     *          page：返回的下级页面的配置内容
     *     }
     * </pre>
     *
     * @param request
     * @param id      页面配置id
     * @return
     * @author hzc
     * @createDate 2016-4-7
     */
    @RequestMapping("/loadSubPage")
    public ModelAndView loadSubPage(HttpServletRequest request, Integer id) {

        ModelAndView mv = new ModelAndView();

        List<ThemePage> pages = themesService.getThemePagesByParentIdAndStatus(id);

//        下级页面多于1个，不确定跳转哪一个，配置错误
        if ((null != pages && pages.size() > 1) || pages.size() == 0) {
            mv.setViewName("/common/noConfig");
            return mv;
        }

        ThemePage page = pages.get(0);

        if (null == page || page.getModulePath() == null) {
//            无配置
            mv.setViewName("/common/noConfig");
        } else {
//        验证权限
            boolean hasRole = false;

            CurrentUser user = CurrentUser.getCurrentUser(request);

            if (user.getUser().isAdmin()) {
                hasRole = true;
            } else {
                Set<Role> roles = user.getUser().getRoles();
                for (Role role : roles) {
                    if (null == page.getRole() || page.getRole().indexOf(role.getId().toString()) > -1) {
                        hasRole = true;
                    }
                }
            }

//        有权限
            if (hasRole) {

//                页面之间传值
                mv.addObject("data", request.getParameter("data"));

                Gson gson = new Gson();
                mv.addObject("page", gson.toJson(page));

                mv.setViewName(COMMONPATH + page.getModulePath());
                mv.addObject("uuid", UUID.randomUUID());

            } else {
//            无权限
                mv.setViewName("/common/noAuthority");
            }
        }

        return mv;
    }

    /**
     * 返回所有数据
     * <pre>
     *     根据查询数据类型，和数据id，返回数据
     *     返回结果为集合：
     *     data的结果类型为returnData方法返回结果
     *     {@code
     *          [
     *              {
     *                  contentType:1,
     *                  data:Object
     *              }
     *          ]
     *     }
     *     1：综合表：验证权限，不过滤状态，填充数据，表格有样式
     *     2：分析报表：无权限验证，返回综合表，报表时间范围，报表表样，
     *        如果报表为最新报告期（时间范围为报告期数），则返回所有报告期，可为空
     *        不过滤状态，填充数据，表格有样式
     *     3：图表：
     *     4：地图：
     *     5：文字分析：
     *     6：文件：
     *     7：目录：
     *     9：数据集：根据数据集id，返回所有数据集数据
     * </pre>
     *
     * @param contents 查询参数：[{contentValue:12,contentType:8}]
     * @return 返回对应数据
     * @author hzc
     * @createDate 2016-4-8
     */
    @RequestMapping("/returnAllData")
    @ResponseBody
    public Object returnAllData(@RequestBody List<ThemePageContent> contents, HttpServletRequest request) throws Exception {
        CurrentUser user = CurrentUser.getCurrentUser(request);
        return genSuccessMsg(commonService.returnAllData(contents, user), "成功", null);
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
    @RequestMapping("/getSyntheticalMenus")
    @ResponseBody
    public Object getSyntheticalMenus(String menus) {
        return genSuccessMsg(commonService.getSyntheticalMenus(menus), "调用成功", 200);
    }
}
