package com.city.support.regime.collection.controller;

import com.city.common.controller.BaseController;
import com.city.common.pojo.Constant;
import com.city.common.util.SessionUtil;
import com.city.support.regime.collection.entity.ReportInfo;
import com.city.support.regime.collection.pojo.ExcelCellPojo;
import com.city.support.regime.collection.pojo.ExcelRowPojo;
import com.city.support.regime.collection.pojo.RptHtmlPojo;
import com.city.support.regime.collection.service.ReportDataService;
import com.city.support.regime.collection.service.ReportExcelService;
import com.city.support.regime.collection.service.ReportInfoService;
import com.city.support.regime.collection.util.AutoCleanRedundanceFiles;
import com.city.support.regime.collection.util.ExportUtils;
import com.city.support.regime.collection.util.ImportUtils;
import com.city.support.regime.report.entity.ReportTemplate;
import com.city.support.regime.report.entity.ReportTemplateStyle;
import com.city.support.regime.report.service.ReportManageService;
import com.city.support.sys.user.entity.User;
import com.city.support.sys.user.pojo.CurrentUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.parser.XmlTreeBuilder;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by wgx on 2016/2/18.
 */
@Controller
@RequestMapping("/support/regime/collection/excel")
public class ReportExcelController extends BaseController {
    @Autowired
    private ReportManageService reportManageService;
    @Autowired
    private ReportInfoService reportInfoService;
    @Autowired
    private ReportDataService reportDataService;
    @Autowired
    private ReportExcelService reportExcelService;

    /**
     * 批量导出excel
     *
     * @param reportList 需导出报表信息
     * @param isOne      是否导出为一个文件
     * @return
     */
    @RequestMapping("/batchExportToExcel")
    @ResponseBody
    public void batchExportToExcel(HttpServletResponse response, HttpServletRequest request, String reportList, Boolean isOne, Boolean hasData) {
        try {
            reportExcelService.batchExportToExcel(response, request, reportList, isOne, hasData);
            //result = genSuccessMsg(null, "导出成功", 200);
        } catch (Exception e) {
            e.printStackTrace();
            //result = genFaultMsg(null, "导出失败", 500);
        }
        //return result;
    }
    /**
     * 单表导入excel
     */

    @RequestMapping("/importFromExcel")
    @ResponseBody
    public Map<String, Object> importFromExcel(HttpServletResponse response, HttpServletRequest request) {
        Map<String, Object> result = null;
        try {
            // 获取当前用户
            User user = SessionUtil.getUser(request.getSession());
            MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
            MultipartFile multipartFile = multipartHttpServletRequest.getFile("excel");
            Workbook wb = null;
            boolean isXlsx = false;
            try {
                InputStream inputStream = multipartFile.getInputStream();
                POIFSFileSystem fs = new POIFSFileSystem(inputStream);
                wb = new HSSFWorkbook(fs);
            } catch (Exception ex) {
                InputStream inputStream = multipartFile.getInputStream();
                wb = new XSSFWorkbook(inputStream);
                isXlsx = true;
            }
            // poi读取excel 进行解析
            //HSSFWorkbook wb = new HSSFWorkbook(fs);
            int sn = wb.getNumberOfSheets();
            if (sn > 0) {
                int importAll = 0;//导入报表数
                int passOrWait = 0;//待审核和已审核数量
                int noPerm = 0;//没有权限的数量
                for (int i = 0; i < sn; i++) {
                    Sheet sheet = null;
                    if (isXlsx) {
                        sheet = (XSSFSheet) wb.getSheetAt(i);
                    } else {
                        sheet = (HSSFSheet) wb.getSheetAt(i);
                    }
                    String sheetName = sheet.getSheetName();
                    // System.out.println(sheetName);
                    String[] sheets = sheetName.split(Constant.UNDERLINE);
                    if (sheets.length != 3 && sheets.length != 2) {
                        continue;
                    } else {
                        String name = sheets.length == 3 ? sheets[0] : "";//名称
                        String sheetTime = sheets.length == 3 ? sheets[1] : sheets[0];//时间
                        Integer rptTmpId = sheets.length == 3 ? Integer.parseInt(sheets[2]) : Integer.parseInt(sheets[1]);//模板id
                        ReportTemplate reportTemplate = reportManageService.getRptTmpById(rptTmpId);
                        //判断当前用户是否有填报某报表的权限
                        boolean isWrite = CurrentUser.hasWritePermission(request, rptTmpId);
                        if (reportTemplate != null && isWrite) {
                            if (name.equals(reportTemplate.getName()) || sheets.length == 2) {
                                Integer index = sheetTime.indexOf(Constant.FrequencyType.YEAR_STRING);
                                Integer year = null;
                                Integer month = null;
                                if (index == -1) {
                                    continue;
                                } else {
                                    year = Integer.parseInt(sheetTime.substring(0, index));
                                    month = Constant.FrequencyType.getFrequencyType(reportTemplate.getPeriod(), sheetTime.substring(index + 1));
                                }
                                ReportInfo reportInfo = reportInfoService.getReportInfosByTimeAndTmpId(year, month, rptTmpId);
                                if (reportInfo != null) {
                                    // 报表处于待审核状态和已审核状态不能导入数据
                                    if (reportInfo.getRptStatus() != Constant.RPT_STATUS.WAITING_PASS && reportInfo.getRptStatus() != Constant.RPT_STATUS.PASS) {
                                        Integer rptStyleId = reportInfo.getRptStyleId();
                                        //ReportTemplateStyle reportTemplateStyle = reportManageService.getRptStyleById(rptStyleId);
                                        ReportTemplateStyle reportTemplateStyle = reportManageService.getRptStyleByRptInfo(reportInfo);
                                        String styleHtml = (reportTemplateStyle != null) ? reportTemplateStyle.getRptStyle() : null;
                                        if (styleHtml != null) {

                                            Document doc = Jsoup.parse(styleHtml, "", new Parser(new XmlTreeBuilder()));
                                            Elements tables = doc.select("table");
                                            if (tables.size() > 0) {
                                                Element table = tables.first();
                                                Elements tbodys = table.select("tbody");//表格内容
                                                if (tbodys.size() > 0) {
                                                    Elements trs = tbodys.select("tr");
                                                    if (isXlsx) {
                                                        ExportUtils.importExcelToHtml(trs, (XSSFSheet) sheet);
                                                    } else {
                                                        ExportUtils.importExcelToHtml(trs, (HSSFSheet) sheet);
                                                    }
                                                }

                                            }
                                            List<RptHtmlPojo> rptHtmlList = ImportUtils.getRptHtml(reportInfo.getId(), Constant.DATA_TYPE.NUMBER, Constant.COLLECTION_TYPE.LEADIN, reportInfo.getTime(), reportInfo.getYear(), reportInfo.getMonth(), doc, reportInfo.getDptId());


                                            reportDataService.saveOrSubmitRptDataList(user, rptHtmlList, reportInfo.getRptStatus(), reportInfo);
                                            if (reportInfo.getRptStatus() != Constant.RPT_STATUS.REJECT) {
                                                //修改报表状态
                                                reportInfoService.updateStatus(user, reportInfo.getId(), Constant.RPT_STATUS.DRAFT, request);
                                            }
                                            /*for (RptHtmlPojo rptHtml : rptHtmlList) {
                                                reportDataService.saveOrSubmitRptData(user,rptHtml,reportInfo.getRptStatus(),reportInfo);
                                                if(reportInfo.getRptStatus()!=Constant.RPT_STATUS.REJECT){
                                                    //修改报表状态
                                                    reportInfoService.updateStatus(user,rptHtml.getRptId(), Constant.RPT_STATUS.DRAFT,request);
                                                }
                                            }*/
                                            importAll++;
                                        }
                                    } else {
                                        passOrWait++;
                                    }
                                }
                            } else {
                                result = genFaultMsg(null, "Excel格式错误", 500);
                                return result;
                            }
                        } else if (reportTemplate != null && !isWrite) {
                            noPerm++;
                        }
                    }
                }
                if (noPerm == sn) {
                    result = genFaultMsg(null, "导入失败，您没有修改报表权限", 500);
                } else if (importAll == sn) {
                    result = genSuccessMsg(null, "导入成功", 200);
                } else if (passOrWait == sn) {
                    result = genFaultMsg(null, "导入失败，报表处于已审核或待审核状态", 500);
                } else if (importAll == sn - passOrWait) {
                    result = genFaultMsg(null, "导入失败，部分报表处于已审核或待审核状态", 500);
                } else if (importAll == 0) {
                    result = genFaultMsg(null, "导入失败，请检查Excel格式是否错误", 500);
                } else if (noPerm > 0) {
                    result = genFaultMsg(null, "部分数据导入失败，请检查是否有修改权限", 500);
                } else {
                    result = genFaultMsg(null, "部分数据导入失败，请检查Excel格式是否错误", 500);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = genFaultMsg(null, "导入失败", 500);
        }
        return result;

    }

    /**
     * 单sheet导入，按照传入的时间来处理第一个sheet页
     *
     * @param response
     * @param request
     * @param rptInfoId
     * @return
     */
    @RequestMapping("/importFromSheet")
    @ResponseBody
    public Map<String, Object> importFromSheet(HttpServletResponse response, HttpServletRequest request, Integer rptInfoId) {
        Map<String, Object> result = null;
        try {
            // 获取当前用户
            User user = SessionUtil.getUser(request.getSession());
            MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
            MultipartFile multipartFile = multipartHttpServletRequest.getFile("excel");
            //InputStream inputStream = multipartFile.getInputStream();
            //POIFSFileSystem fs = new POIFSFileSystem(inputStream);
            Workbook wb = null;
            boolean isXlsx = false;
            try {// .xls 格式
                InputStream inputStream = multipartFile.getInputStream();
                POIFSFileSystem fs = new POIFSFileSystem(inputStream);
                wb = new HSSFWorkbook(fs);
            } catch (Exception ex) {// .xlsx 格式
                InputStream inputStream = multipartFile.getInputStream();
                wb = new XSSFWorkbook(inputStream);
                isXlsx = true;
            }
            // poi读取excel 进行解析
            //HSSFWorkbook wb = new HSSFWorkbook(fs);
            int sn = wb.getNumberOfSheets();
            if (sn > 0) {
                int importAll = 0;//导入报表数
                int passOrWait = 0;//待审核和已审核数量
                // 添加第一个sheet页内容
                Sheet sheet = null;
                if (isXlsx) {
                    sheet = (XSSFSheet) wb.getSheetAt(0);
                } else {
                    sheet = (HSSFSheet) wb.getSheetAt(0);
                }
                String sheetName = sheet.getSheetName();
                // System.out.println(sheetName);
                String[] sheets = sheetName.split(Constant.UNDERLINE);
                if (sheets.length != 3 && sheets.length != 2) {
                    result = genFaultMsg(null, "Sheet页格式错误", 500);
                    return result;
                } else {
                    String name = sheets.length == 3 ? sheets[0] : "";//名称
                    //String sheetTime = sheets[1];//时间
                    Integer rptTmpId = sheets.length == 3 ? Integer.parseInt(sheets[2]) : Integer.parseInt(sheets[1]);//模板id
                    ReportTemplate reportTemplate = reportManageService.getRptTmpById(rptTmpId);
                    //判断当前用户是否有填报某报表的权限
                    boolean isWrite = CurrentUser.hasWritePermission(request, rptTmpId);
                    if (reportTemplate != null && isWrite) {
                        if (name.equals(reportTemplate.getName()) || sheets.length == 2) {
                            if (rptInfoId == null) {
                                result = genFaultMsg(null, "导入失败", 500);
                                return result;
                            }
                            ReportInfo reportInfo = reportInfoService.getReportInfosByRptInfoId(rptInfoId);

                            if (reportInfo != null) {
                                // 报表处于待审核状态和已审核状态不能导入数据
                                if (reportInfo.getRptStatus() != Constant.RPT_STATUS.WAITING_PASS && reportInfo.getRptStatus() != Constant.RPT_STATUS.PASS) {
                                    Integer rptStyleId = reportInfo.getRptStyleId();
                                    //ReportTemplateStyle reportTemplateStyle = reportManageService.getRptStyleById(rptStyleId);
                                    ReportTemplateStyle reportTemplateStyle = reportManageService.getRptStyleByRptInfo(reportInfo);
                                    String styleHtml = (reportTemplateStyle != null) ? reportTemplateStyle.getRptStyle() : null;
                                    if (styleHtml != null) {

                                        Document doc = Jsoup.parse(styleHtml, "", new Parser(new XmlTreeBuilder()));
                                        Elements tables = doc.select("table");
                                        if (tables.size() > 0) {
                                            Element table = tables.first();
                                            Elements tbodys = table.select("tbody");//表格内容
                                            if (tbodys.size() > 0) {
                                                Elements trs = tbodys.select("tr");
                                                if (isXlsx) {
                                                    ExportUtils.importExcelToHtml(trs, (XSSFSheet) sheet);
                                                } else {
                                                    ExportUtils.importExcelToHtml(trs, (HSSFSheet) sheet);
                                                }
                                            }

                                        }
                                        List<RptHtmlPojo> rptHtmlList = ImportUtils.getRptHtml(reportInfo.getId(), Constant.DATA_TYPE.NUMBER, Constant.COLLECTION_TYPE.LEADIN, reportInfo.getTime(), reportInfo.getYear(), reportInfo.getMonth(), doc, reportInfo.getDptId());

                                        reportDataService.saveOrSubmitRptDataList(user, rptHtmlList, reportInfo.getRptStatus(), reportInfo);
                                        if (reportInfo.getRptStatus() != Constant.RPT_STATUS.REJECT) {
                                            //修改报表状态
                                            reportInfoService.updateStatus(user, reportInfo.getId(), Constant.RPT_STATUS.DRAFT, request);
                                        }
                                            /*for (RptHtmlPojo rptHtml : rptHtmlList) {
                                                reportDataService.saveOrSubmitRptData(user,rptHtml,reportInfo.getRptStatus(),reportInfo);
                                                if(reportInfo.getRptStatus()!=Constant.RPT_STATUS.REJECT){
                                                    //修改报表状态
                                                    reportInfoService.updateStatus(user,rptHtml.getRptId(), Constant.RPT_STATUS.DRAFT,request);
                                                }
                                            }*/
                                        importAll++;
                                    } else {
                                        result = genFaultMsg(null, "报表暂无表样！", 500);
                                        return result;
                                    }
                                } else {
                                    passOrWait++;
                                    result = genFaultMsg(null, "导入失败，报表处于已审核或待审核状态", 500);
                                    return result;
                                }
                            }
                        } else {
                            result = genFaultMsg(null, "Excel格式错误", 500);
                            return result;
                        }
                    } else if (reportTemplate != null && !isWrite) {
                        return genFaultMsg(null, "导入失败,请检查是否有修改权限", 500);
                    }
                }
                result = genSuccessMsg(null, "导入成功", 200);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = genFaultMsg(null, "导入失败", 500);
        }
        return result;

    }


}
