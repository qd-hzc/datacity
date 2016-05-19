package com.city.resourcecategory.analysis.report.controller;

import com.city.common.controller.BaseController;
import com.city.common.pojo.Constant;
import com.city.common.util.SessionUtil;
import com.city.common.util.table.builder.GenRCStrUtil;
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
import com.city.support.regime.collection.pojo.ExcelCellPojo;
import com.city.support.regime.collection.pojo.ExcelRowPojo;
import com.city.support.regime.collection.util.AutoCleanRedundanceFiles;
import com.city.support.regime.collection.util.ExportUtils;
import com.city.support.sys.user.entity.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.parser.XmlTreeBuilder;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.*;

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

    /**
     * 导出为Excel  单表或多表
     *
     * @param response
     * @param request
     * @param research  报表
     * @param yearArray 选择的要导出的时间
     * @author CRX
     * @createDate 2016-5-16
     */
    @RequestMapping("/checkExportToExcel")
    @ResponseBody
    public void checkExportToExcel(HttpServletResponse response, HttpServletRequest request, String research, String yearArray) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<Map<String, String>>>() {
        }.getType();
        try {
            List<Map<String, String>> list = gson.fromJson(research, type);
            Integer researchId = Integer.valueOf(list.get(0).get("id"));

            String basePath = request.getSession().getServletContext().getRealPath("/");
            //打包插件路径
            String exportPath = basePath + "download";

            List<Map<String, String>> tableList = new ArrayList<>();
            String fileName = list.get(0).get("name");// 用于存放生成的文件名称
            TimePojo pojo = new TimePojo();
            List<Map<String, String>> timeList = gson.fromJson(yearArray, type);
            //为报告期时
            if (timeList != null && timeList.size() > 0) {
                for (int i = 0; i < timeList.size(); i++) {
                    Map<String, String> map = timeList.get(i);
                    String year = map.get("year");
                    String months = map.get("month");
                    String monthNames = map.get("name");
                    String[] monthList = null;
                    String[] monthNameList = null;
                    //时间频度为 半年、季度、月
                    if (!StringUtils.isEmpty(months.trim())) {
                        monthList = months.split(",");
                        monthNameList = monthNames.split(",");
                        for (int j = 0; j < monthList.length; j++) {
                            addTableAndTime(researchId, pojo, year, monthList[j], monthNameList[j], tableList);
                        }
                    } else {
                        //时间频度为 年
                        addTableAndTime(researchId, pojo, year, "12", "", tableList);
                    }
                }
            } else {
                //没有报告期数
                addTableAndTime(researchId, pojo, "", "", "", tableList);
            }
            List<String> fileNameList = new ArrayList();
            String showName = getExportToExcel(fileNameList, fileName, exportPath, tableList);
            ExportUtils.export(response, fileNameList, "multi", exportPath, showName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 为集合添加 要导出为Excel的Html及对应sheet页名称所包含的时间
     *
     * @param researchId 报表ID
     * @param pojo       要查询的时间
     * @param year       时间中的年
     * @param month      时间中的频度
     * @param monthName  频度名称
     * @param tableList  集合  存放要导出为Excel的Html及对应sheet页名称所包含的时间
     * @return
     * @author CRX
     * @createdate 2016-5-17
     */
    private List<Map<String, String>> addTableAndTime(Integer researchId, TimePojo pojo, String year, String month, String monthName, List<Map<String, String>> tableList) {
        Map<String, String> tableAndTime = new HashMap<String, String>();
        if ("" == year) {
            pojo = null;
            tableAndTime.put("yearName", "");
        } else {
            pojo.setPeriod(Integer.parseInt(month));
            pojo.setYear(Integer.parseInt(year));
            tableAndTime.put("yearName", year + "年");
        }
        String table = queryRptService.queryCustomRpt(researchId, pojo);
        tableAndTime.put("table", table);

        tableAndTime.put("monthName", monthName);
        tableList.add(tableAndTime);
        return tableList;
    }

    /**
     * 生成Excel
     *
     * @param fileNameList
     * @param fileName
     * @param path
     * @param tableList    报表HTML集合
     * @return
     * @author CRX
     * @createDate 2016-5-16
     */
    private String getExportToExcel(List<String> fileNameList, String fileName, String path, List<Map<String, String>> tableList) {
//        FileOutputStream fileOutputStream = null;
        try {
            // 判断是否存在路径
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
                AutoCleanRedundanceFiles.putRedundanceFiles(path);
            }
            String tableName = fileName;

            String file = path + "/" + tableName + ".xls";
            fileNameList.add(file);
            // 创建一个webbook，对应一个Excel文件
            HSSFWorkbook wb = new HSSFWorkbook();

            if (tableList != null) {
                for (int i = 0; i < tableList.size(); i++) {
                    Map<String, String> tableAndTime = tableList.get(i);
                    String yearName = tableAndTime.get("yearName");
                    String monthName = tableAndTime.get("monthName");
                    String tableHtml = tableAndTime.get("table");

                    // 在webbook中添加一个sheet,对应Excel文件中的sheet
                    HSSFSheet sheet = null;
                    //设置sheet页名称
                    sheet = wb.createSheet(fileName + "" + yearName + monthName);

                    // 设置工作表列宽
                    sheet.setDefaultColumnWidth(20);
                    // 设置工作表行高
                    sheet.setDefaultRowHeight((short) 500);
                    // 创建单元格样式
                    // 居中
                    HSSFCellStyle style = ExportUtils.getHssfCellStyle(wb, HSSFCellStyle.ALIGN_CENTER);
                    HSSFCellStyle style_string = ExportUtils.getHssfCellStyle(wb, HSSFCellStyle.ALIGN_CENTER);
                    HSSFDataFormat format = wb.createDataFormat();
                    style_string.setDataFormat(format.getFormat("@"));
                    // 右对齐
                    HSSFCellStyle style_right = ExportUtils.getHssfCellStyle(wb, HSSFCellStyle.ALIGN_RIGHT);
                    // 左对齐
                    HSSFCellStyle style_left = ExportUtils.getHssfCellStyle(wb, HSSFCellStyle.ALIGN_LEFT);
                    style_left.setDataFormat(format.getFormat("@"));
                    Document doc = Jsoup.parse(tableHtml, "", new Parser(new XmlTreeBuilder()));
                    Elements tables = doc.select("table");
                    int collength = 0;
                    if (tables.size() > 0) {
                        Element table = tables.first();
                        //在sheet中添加标题
                        Elements theads = table.select("thead");//表头
                        HSSFRow headRow = sheet.createRow(0);
                        HSSFCell headCell = headRow.createCell(0);
                        if (theads.size() > 0 && theads.hasText()) {
                            //获取标题所占列数
                            Integer col = Integer.valueOf(theads.first().select("td").attr("colspan"));
                            collength = col;
                            //合并单元格，参数依次为起始行，结束行，起始列，结束列
                            sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, col - 1));
                            String td = theads.first().select("td").text();
                            headCell.setCellValue(td);
                            headCell.setCellStyle(style_string);
                        }
                        Elements tbodys = table.select("tbody");//表格内容
                        List<Map<String, Object>> cellRangeAddressList = new ArrayList<Map<String, Object>>();
                        int beginRow = 2;
                        if (tbodys.size() > 0) {

                            Elements trs = tbodys.select("tr");
                            List<ExcelRowPojo> excelRowList = ExportUtils.getExcelRowList(trs, true);
                            ExportUtils.setExcelExtraCell(excelRowList);
                            //添加内容
                            for (ExcelRowPojo excelRow : excelRowList) {
                                HSSFRow row = sheet.createRow(beginRow);
                                List<ExcelCellPojo> excelCellList = excelRow.getExcelCellList();
                                List<ExcelCellPojo> excelExtraCellList = excelRow.getExcelExtraCellList();
                                for (ExcelCellPojo excelCell : excelCellList) {
                                    int beginColCell = excelCell.getBeginColCell();
                                    int endColCell = excelCell.getEndColCell();
                                    int beginRowCell = excelCell.getBeginRowCell();
                                    int endRowCell = excelCell.getEndRowCell();
                                    //HSSFCell cell = row.createCell(beginRowCell);
                                    int[] colCell = excelCell.getColCell(beginColCell, endColCell, excelExtraCellList);
                                    HSSFCell cell = row.createCell(colCell[0]);
                                    cell.setCellValue(excelCell.getCellValue());

                                    int nodeType = excelCell.getNodeType();
                                    if (ExcelCellPojo.real == nodeType) {
                                        Map<String, Object> map = new HashMap<>();
                                        map.put("address", new CellRangeAddress(beginRowCell, endRowCell, colCell[0], colCell[1]));
                                        map.put("nodeType", ExcelCellPojo.real);
                                        cellRangeAddressList.add(map);
                                    }
                                    if (excelCell.getCellType() != null) {
                                        cell.setCellStyle(style_string);
                                    } else {
                                        cell.setCellStyle(style_string);
                                    }

                                    if (endRowCell - beginRowCell > 0 || colCell[1] - colCell[0] > 0) {
                                        //合并单元格，参数依次为起始行，起始列，结束行，结束列
                                        sheet.addMergedRegion(new CellRangeAddress(beginRowCell, endRowCell, colCell[0], colCell[1]));
                                        // 记录所有要合并的单元格，最后添加样式，否则会被别的样式覆盖
                                        Map<String, Object> map = new HashMap<>();
                                        map.put("address", new CellRangeAddress(beginRowCell, endRowCell, colCell[0], colCell[1]));
                                        map.put("nodeType", excelCell.getNodeType());
                                        cellRangeAddressList.add(map);
                                    }
                                }
                                beginRow++;
                            }
                        }

                        Elements tfoots = table.select("tfoot");//页脚
                        HSSFRow footRow = sheet.createRow(beginRow);
                        HSSFCell footCell = footRow.createCell(0);
                        if (tfoots.size() > 0) {
                            Integer col = Integer.valueOf(tfoots.first().select("td").attr("colspan"));
                            //合并单元格，参数依次为起始行，结束行，起始列，结束列
                            sheet.addMergedRegion(new CellRangeAddress(beginRow, beginRow, 0, col - 1));
                            footCell.setCellValue(tfoots.first().select("td").text());
                            footCell.setCellStyle(style_left);
                            // 记录所有要合并的单元格，最后添加样式，否则会被别的样式覆盖
                            Map<String, Object> map = new HashMap<>();
                            map.put("address", new CellRangeAddress(beginRow, beginRow, 0, col - 1));
                            map.put("nodeType", 1);
                            cellRangeAddressList.add(map);
                        }
                        RegionUtil.setBorderTop(HSSFCellStyle.BORDER_THIN, new CellRangeAddress(beginRow, beginRow, 0, collength - 1), sheet, wb);
                        for (Map<String, Object> map : cellRangeAddressList) {
                            CellRangeAddress cellRangeAddress = (CellRangeAddress) map.get("address");
                            int nodeType = (int) map.get("nodeType");
                            ExportUtils.setRegionBorder(HSSFCellStyle.BORDER_THIN, cellRangeAddress, sheet, wb, nodeType);
                        }
                        RegionUtil.setBorderRight(HSSFCellStyle.BORDER_NONE, new CellRangeAddress(beginRow, beginRow, 0, collength - 1), sheet, wb);
                    }

                }
            }
            //将文件存到指定位置
            FileOutputStream fout = new FileOutputStream(file);
            wb.write(fout);
            fout.flush();
            fout.close();
            return tableName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}



