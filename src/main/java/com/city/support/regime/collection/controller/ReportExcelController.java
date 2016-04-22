package com.city.support.regime.collection.controller;

import com.city.common.controller.BaseController;
import com.city.common.pojo.Constant;
import com.city.common.util.SessionUtil;
import com.city.support.regime.collection.entity.ReportInfo;
import com.city.support.regime.collection.pojo.ExcelCellPojo;
import com.city.support.regime.collection.pojo.ExcelRowPojo;
import com.city.support.regime.collection.pojo.RptHtmlPojo;
import com.city.support.regime.collection.service.ReportDataService;
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

    /**
     * 批量导出excel
     *
     * @param reportList 需导出报表信息
     * @param isOne      是否导出为一个文件
     * @return
     */
    @RequestMapping("/batchExportToExcel")
    @ResponseBody
    public void batchExportToExcel(HttpServletResponse response, HttpServletRequest request, String reportList, boolean isOne) {
        Map<String, Object> result = null;
        Gson gson = new Gson();
        Type type = new TypeToken<List<Map<String, String>>>() {
        }.getType();
        OutputStream out = null;
        try {
            List<Map<String, String>> list = gson.fromJson(reportList, type);
            String rptInfoIds = list.get(0).get("rptInfoIds");
            String rptTmpIds = list.get(0).get("rptTmpIds");
            if (isOne) {
                //下载路径
                String paths = request.getParameter("paths");
                String basePath = request.getSession().getServletContext().getRealPath("/");
                //下载名
                String name = request.getParameter("name");
                //打包插件路径
                String exportPath = basePath + "download";

                List<String> fileNameList = new ArrayList();// 用于存放生成的文件名称
                String showName = exportToExcel(fileNameList, exportPath, rptInfoIds, rptTmpIds);
                ExportUtils.export(response, fileNameList, "multi", exportPath, showName);
            } else {
                String[] rptTmpIdList = rptTmpIds.split(",");

                if (rptTmpIdList.length > 0) {
                    //下载路径
                    String paths = request.getParameter("paths");
                    String basePath = request.getSession().getServletContext().getRealPath("/");
                    //下载名
                    String name = request.getParameter("name");
                    //打包插件路径
                    String exportPath = basePath + "download";

                    List<String> fileNameList = new ArrayList();// 用于存放生成的文件名称
                    String showName = null;
                    for (String rptTmpId : rptTmpIdList) {
                        showName = exportToExcel(fileNameList, exportPath, rptInfoIds, rptTmpId);
                    }
                    if (rptTmpIdList.length == 1) {
                        ExportUtils.export(response, fileNameList, "multi", exportPath, showName);
                    } else {
                        ExportUtils.export(response, fileNameList, "multi", exportPath);
                    }
                }

            }


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
            InputStream inputStream = multipartFile.getInputStream();
            POIFSFileSystem fs = new POIFSFileSystem(inputStream);
            // poi读取excel 进行解析
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            int sn = wb.getNumberOfSheets();
            if (sn > 0) {
                int importAll = 0;//导入报表数
                int passOrWait =0;//待审核和已审核数量
                int noPerm =0;//没有权限的数量
                for (int i = 0; i < sn; i++) {
                    HSSFSheet sheet = wb.getSheetAt(i);
                    String sheetName = sheet.getSheetName();
                    // System.out.println(sheetName);
                    String[] sheets = sheetName.split(Constant.UNDERLINE);
                    if (sheets.length != 3&&sheets.length != 2) {
                        continue;
                    } else {
                        String name = sheets.length==3?sheets[0]:"";//名称
                        String sheetTime = sheets.length==3?sheets[1]:sheets[0];//时间
                        Integer rptTmpId = sheets.length==3?Integer.parseInt(sheets[2]):Integer.parseInt(sheets[1]);//模板id
                        ReportTemplate reportTemplate = reportManageService.getRptTmpById(rptTmpId);
                        //判断当前用户是否有填报某报表的权限
                        boolean isWrite = CurrentUser.hasWritePermission(request, rptTmpId);
                        if (reportTemplate != null&&isWrite) {
                            if (name.equals(reportTemplate.getName())||sheets.length == 2) {
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
                                    if (reportInfo.getRptStatus()!=Constant.RPT_STATUS.WAITING_PASS&&reportInfo.getRptStatus()!=Constant.RPT_STATUS.PASS) {
                                        Integer rptStyleId = reportInfo.getRptStyleId();
                                        //ReportTemplateStyle reportTemplateStyle = reportManageService.getRptStyleById(rptStyleId);
                                        ReportTemplateStyle reportTemplateStyle = reportManageService.getRptStyleByRptInfo(reportInfo);
                                        String styleHtml = (reportTemplateStyle!=null)?reportTemplateStyle.getRptStyle():null;
                                        if (styleHtml != null) {

                                            Document doc = Jsoup.parse(styleHtml, "", new Parser(new XmlTreeBuilder()));
                                            Elements tables = doc.select("table");
                                            if (tables.size() > 0) {
                                                Element table = tables.first();
                                                Elements tbodys = table.select("tbody");//表格内容
                                                if (tbodys.size() > 0) {
                                                    Elements trs = tbodys.select("tr");
                                                    importExcelToHtml(trs, sheet);
                                                }

                                            }
                                            List<RptHtmlPojo> rptHtmlList = ImportUtils.getRptHtml(reportInfo.getId(), Constant.DATA_TYPE.NUMBER, Constant.COLLECTION_TYPE.LEADIN, reportInfo.getTime(),reportInfo.getYear(),reportInfo.getMonth(), doc,reportInfo.getDptId());

                                            for (RptHtmlPojo rptHtml : rptHtmlList) {
                                                reportDataService.saveOrSubmitRptData(user,rptHtml,reportInfo.getRptStatus(),reportInfo);
                                                if(reportInfo.getRptStatus()!=Constant.RPT_STATUS.REJECT){
                                                    //修改报表状态
                                                    reportInfoService.updateStatus(user,rptHtml.getRptId(), Constant.RPT_STATUS.DRAFT,request);
                                                }
                                            }
                                            importAll++;
                                        }
                                    }else{
                                        passOrWait++;
                                    }
                                }
                            } else {
                                result = genSuccessMsg(null, "Excel格式错误", 500);
                                return result;
                            }
                        }else if(reportTemplate != null&&!isWrite){
                            noPerm++;
                        }
                    }
                }
                if(noPerm==sn){
                    result = genSuccessMsg(null, "导入失败，您没有修改报表权限", 500);
                }else if (importAll== sn) {
                    result = genSuccessMsg(null, "导入成功", 200);
                }else if(passOrWait == sn) {
                    result = genSuccessMsg(null, "导入失败，报表处于已审核或待审核状态", 500);
                }else if(importAll == sn - passOrWait) {
                    result = genSuccessMsg(null, "导入失败，部分报表处于已审核或待审核状态", 500);
                }else if (importAll == 0) {
                    result = genSuccessMsg(null, "导入失败，请检查Excel格式是否错误", 500);
                } else if(noPerm>0){
                    result = genSuccessMsg(null, "部分数据导入失败，请检查是否有修改权限", 500);
                }else {
                    result = genSuccessMsg(null, "部分数据导入失败，请检查Excel格式是否错误", 500);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = genSuccessMsg(null, "导入失败", 500);
        }
        return result;

    }

    /**
     * 单sheet导入，按照传入的时间来处理第一个sheet页
     * @param response
     * @param request
     * @param rptInfoId
     * @return
     */
    @RequestMapping("/importFromSheet")
    @ResponseBody
    public Map<String, Object> importFromSheet(HttpServletResponse response, HttpServletRequest request,Integer rptInfoId) {
        Map<String, Object> result = null;
        try {
            // 获取当前用户
            User user = SessionUtil.getUser(request.getSession());
            MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
            MultipartFile multipartFile = multipartHttpServletRequest.getFile("excel");
            InputStream inputStream = multipartFile.getInputStream();
            POIFSFileSystem fs = new POIFSFileSystem(inputStream);
            // poi读取excel 进行解析
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            int sn = wb.getNumberOfSheets();
            if (sn > 0) {
                int importAll = 0;//导入报表数
                int passOrWait =0;//待审核和已审核数量
                // 添加第一个sheet页内容
                    HSSFSheet sheet = wb.getSheetAt(0);
                    String sheetName = sheet.getSheetName();
                    // System.out.println(sheetName);
                    String[] sheets = sheetName.split(Constant.UNDERLINE);
                    if (sheets.length != 3&&sheets.length != 2) {
                        result = genSuccessMsg(null, "Sheet页格式错误", 500);
                        return result;
                    } else {
                        String name = sheets.length==3?sheets[0]:"";//名称
                        //String sheetTime = sheets[1];//时间
                        Integer rptTmpId = sheets.length==3?Integer.parseInt(sheets[2]):Integer.parseInt(sheets[1]);//模板id
                        ReportTemplate reportTemplate = reportManageService.getRptTmpById(rptTmpId);
                        //判断当前用户是否有填报某报表的权限
                        boolean isWrite = CurrentUser.hasWritePermission(request, rptTmpId);
                        if (reportTemplate != null&&isWrite) {
                            if (name.equals(reportTemplate.getName())||sheets.length == 2) {
                                if (rptInfoId == null) {
                                    result = genSuccessMsg(null, "导入失败", 500);
                                    return result;
                                }
                                ReportInfo reportInfo = reportInfoService.getReportInfosByRptInfoId(rptInfoId);

                                if (reportInfo != null) {
                                    // 报表处于待审核状态和已审核状态不能导入数据
                                    if (reportInfo.getRptStatus()!=Constant.RPT_STATUS.WAITING_PASS&&reportInfo.getRptStatus()!=Constant.RPT_STATUS.PASS) {
                                        Integer rptStyleId = reportInfo.getRptStyleId();
                                        //ReportTemplateStyle reportTemplateStyle = reportManageService.getRptStyleById(rptStyleId);
                                        ReportTemplateStyle reportTemplateStyle = reportManageService.getRptStyleByRptInfo(reportInfo);
                                        String styleHtml = (reportTemplateStyle!=null)?reportTemplateStyle.getRptStyle():null;
                                        if (styleHtml != null) {

                                            Document doc = Jsoup.parse(styleHtml, "", new Parser(new XmlTreeBuilder()));
                                            Elements tables = doc.select("table");
                                            if (tables.size() > 0) {
                                                Element table = tables.first();
                                                Elements tbodys = table.select("tbody");//表格内容
                                                if (tbodys.size() > 0) {
                                                    Elements trs = tbodys.select("tr");
                                                    importExcelToHtml(trs, sheet);
                                                }

                                            }
                                            List<RptHtmlPojo> rptHtmlList = ImportUtils.getRptHtml(reportInfo.getId(), Constant.DATA_TYPE.NUMBER, Constant.COLLECTION_TYPE.LEADIN, reportInfo.getTime(),reportInfo.getYear(),reportInfo.getMonth(), doc,reportInfo.getDptId());

                                            for (RptHtmlPojo rptHtml : rptHtmlList) {
                                                reportDataService.saveOrSubmitRptData(user,rptHtml,reportInfo.getRptStatus(),reportInfo);
                                                if(reportInfo.getRptStatus()!=Constant.RPT_STATUS.REJECT){
                                                    //修改报表状态
                                                    reportInfoService.updateStatus(user,rptHtml.getRptId(), Constant.RPT_STATUS.DRAFT,request);
                                                }
                                            }
                                            importAll++;
                                        }
                                    }else{
                                        passOrWait++;
                                    }
                                }
                            } else {
                                result = genSuccessMsg(null, "Excel格式错误", 500);
                                return result;
                            }
                        }else if (reportTemplate != null&&!isWrite) {
                            return genSuccessMsg(null, "导入失败,请检查是否有修改权限", 500);
                        }
                }
                result = genSuccessMsg(null, "导入成功", 200);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = genSuccessMsg(null, "导入失败", 500);
        }
        return result;

    }
    /**
     * 按照模板id导出excel
     *
     * @param rptInfoIds 报表id
     * @param rptTmpIds  模板id
     */
    public String exportToExcel(List<String> fileNameList, String path, String rptInfoIds, String rptTmpIds) {

        FileOutputStream fileOutputStream = null;
        try {
            // 判断是否存在路径
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
                AutoCleanRedundanceFiles.putRedundanceFiles(path);
            }
            String tableName = "";
            List<ReportTemplate> reportTemplateList = reportManageService.getRptTmpById(rptTmpIds);
            int listLength = reportTemplateList.size();
            if (listLength == 0) {
                return null;
            }
            tableName += reportTemplateList.get(0).getName();
            for (int i = 1; i < Math.min(2, listLength); i++) {
                tableName += "、" + reportTemplateList.get(i).getName();
            }
            if (listLength > 2) {
                tableName += "等";
            }
            String file = path + "/" + tableName + ".xls";
            fileNameList.add(file);
            //fileOutputStream = new FileOutputStream(file);
            List<ReportInfo> reportInfoList = reportInfoService.getReportInfosByRptInfoAndTmpIds(rptTmpIds, rptInfoIds);
            // 创建一个webbook，对应一个Excel文件
            HSSFWorkbook wb = new HSSFWorkbook();

            for (ReportInfo reportInfo : reportInfoList) {
                if (reportInfo != null) {
                    Integer rptStyleId = reportInfo.getRptStyleId();
                    //ReportTemplateStyle reportTemplateStyle = reportManageService.getRptStyleById(rptStyleId);
                    ReportTemplateStyle reportTemplateStyle = reportManageService.getRptStyleByRptInfo(reportInfo);
                    Integer rptTmpId = reportInfo.getTmpId();
                    String time = reportInfo.getTime();
                    Integer period = reportInfo.getPeriod();
                    Integer year = reportInfo.getYear();
                    Integer month = reportInfo.getMonth();
                    String sheetTime = year + Constant.FrequencyType.YEAR_STRING + Constant.FrequencyType.getFrequencyName(period, month);
                    String name = reportInfo.getName();
                    // 在webbook中添加一个sheet,对应Excel文件中的sheet
                    HSSFSheet sheet = null;
                    //if (listLength == 1) {
                    //    sheet = wb.createSheet(sheetTime);
                    //} else {
                    sheet = wb.createSheet(/*name + Constant.UNDERLINE +*/ sheetTime + Constant.UNDERLINE + rptTmpId);
                    // }

                    // 设置工作表列宽
                    sheet.setDefaultColumnWidth(20);
                    // 设置工作表行高
                    sheet.setDefaultRowHeight((short) 500);
                    // 创建单元格样式
                    // 居中
                    HSSFCellStyle style = getHssfCellStyle(wb, HSSFCellStyle.ALIGN_CENTER);
                    HSSFCellStyle style_string = getHssfCellStyle(wb, HSSFCellStyle.ALIGN_CENTER);
                    HSSFDataFormat format = wb.createDataFormat();
                    style_string.setDataFormat(format.getFormat("@"));
                    // 右对齐
                    HSSFCellStyle style_right = getHssfCellStyle(wb, HSSFCellStyle.ALIGN_RIGHT);
                    // 左对齐
                    HSSFCellStyle style_left = getHssfCellStyle(wb, HSSFCellStyle.ALIGN_LEFT);
                    style_left.setDataFormat(format.getFormat("@"));
                    if (reportTemplateStyle != null) {
                        String styleHtml = reportTemplateStyle.getRptStyle();
                        if (styleHtml != null) {
                            Document doc = Jsoup.parse(styleHtml, "", new Parser(new XmlTreeBuilder()));
                            Elements tables = doc.select("table");
                            int collength = 0;
                            if (tables.size() > 0) {
                                Element table = tables.first();
                                //在sheet中添加标题
                                Elements theads = table.select("thead");//表头
                                HSSFRow headRow = sheet.createRow(0);
                                HSSFCell headCell = headRow.createCell(0);
                                if (theads.size() > 0) {
                                    //获取标题所占列数
                                    Integer col = Integer.valueOf(theads.first().select("td").attr("colspan"));
                                    collength = col;
                                    //合并单元格，参数依次为起始行，结束行，起始列，结束列
                                    sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, col - 1));
                                    headCell.setCellValue(theads.first().select("td").text());
                                    headCell.setCellStyle(style_string);
                                }
                                Elements tbodys = table.select("tbody");//表格内容
                                List<Map<String, Object>> cellRangeAddressList = new ArrayList<Map<String, Object>>();
                                int beginRow = 2;
                                if (tbodys.size() > 0) {

                                    Elements trs = tbodys.select("tr");
                                    List<ExcelRowPojo> excelRowList = getExcelRowList(trs);
                                    setExcelExtraCell(excelRowList);
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
                                    setRegionBorder(HSSFCellStyle.BORDER_THIN, cellRangeAddress, sheet, wb, nodeType);
                                }
                                RegionUtil.setBorderRight(HSSFCellStyle.BORDER_NONE, new CellRangeAddress(beginRow, beginRow, 0, collength - 1), sheet, wb);
                            }


                        }
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

    /**
     * 设置单元格样式
     *
     * @param wb
     * @param align 水平样式
     * @return
     */
    private HSSFCellStyle getHssfCellStyle(HSSFWorkbook wb, short align) {
        HSSFCellStyle style = wb.createCellStyle();
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//垂直居中
        style.setAlignment(align);//水平居中
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);//下边框
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
        return style;
    }

    /**
     * 添加边框
     *
     * @param border
     * @param region
     * @param sheet
     * @param wb
     * @param nodeType 节点类型
     */
    private static void setRegionBorder(int border, CellRangeAddress region, Sheet sheet, Workbook wb, int nodeType) {
        RegionUtil.setBorderBottom(HSSFCellStyle.BORDER_NONE, region, sheet, wb);
        RegionUtil.setBorderLeft(border, region, sheet, wb);
        RegionUtil.setBorderRight(border, region, sheet, wb);
        if (ExcelCellPojo.real != nodeType) {
            RegionUtil.setBorderTop(border, region, sheet, wb);
        } else {
            RegionUtil.setBorderTop(HSSFCellStyle.BORDER_NONE, region, sheet, wb);
        }

    }

    /**
     * 在每一行添加单元格占多行的情况
     *
     * @param excelRowList
     */
    public static void setExcelExtraCell(List<ExcelRowPojo> excelRowList) {
        for (int i = 0; i < excelRowList.size(); i++) {
            ExcelRowPojo excelRow = excelRowList.get(i);
            List<ExcelCellPojo> excelCellPojoList = excelRow.getExcelCellList();
            for (ExcelCellPojo excelCell : excelCellPojoList) {
                int beginRowCell = excelCell.getBeginRowCell();
                int endRowCell = excelCell.getEndRowCell();
                // 处理此行单元格有占多行的情况
                if (endRowCell - beginRowCell > 0) {
                    for (int j = endRowCell - beginRowCell; j > 0; j--) {
                        excelRowList.get(i + j).getExcelExtraCellList().add(excelCell);
                    }
                }
            }
        }
    }

    /**
     * 按行存储单元格内容
     *
     * @param trs
     */
    public static List<ExcelRowPojo> getExcelRowList(Elements trs) {
        List<ExcelRowPojo> excelRowPojoList = new ArrayList<ExcelRowPojo>();
        //单元格起始行
        int beginRowCell = 2;
        for (Element tr : trs) {
            ExcelRowPojo excelRowPojo = new ExcelRowPojo();
            // 每行的单元格
            List<ExcelCellPojo> excelCellList = new ArrayList<ExcelCellPojo>();
            List<ExcelCellPojo> excelExtraCellList = new ArrayList<ExcelCellPojo>();
            Elements tds = tr.select("td");
            //单元格起始列
            int beginColCell = 0;
            for (Element td : tds) {
                String rowspan = "".equals(td.attr("rowspan"))?"1":td.attr("rowspan");
                String colspan = "".equals(td.attr("colspan"))?"1":td.attr("colspan");
                String value = "";


                // 添加单元格的起始行，结束行，起始列，结束列
                ExcelCellPojo excelCellPojo = new ExcelCellPojo(beginRowCell,
                        beginRowCell + Integer.valueOf(rowspan) - 1,
                        beginColCell,
                        beginColCell + Integer.valueOf(colspan) - 1);
                if (!td.attr("esi-type").equals(Constant.TdEsiType.DATA)) {
                    value = td.text();
                }
                excelCellPojo.setCellValue(value);
                if (td.attr("class").equals("real")) {
                    excelCellPojo.setNodeType(ExcelCellPojo.real);
                } else {
                    excelCellPojo.setNodeType(ExcelCellPojo.virtual);
                }
                excelCellList.add(excelCellPojo);
                beginColCell = beginColCell + Integer.valueOf(colspan);
            }
            excelRowPojo.setExcelCellList(excelCellList);
            excelRowPojo.setExcelExtraCellList(excelExtraCellList);
            excelRowPojoList.add(excelRowPojo);
            beginRowCell++;
        }
        return excelRowPojoList;
    }
    /**
     * 按行导入单元格内容
     *
     * @param trs
     * @param sheet
     */
    public static void importExcelToHtml(Elements trs, HSSFSheet sheet) {
        List<ExcelRowPojo> excelRowList = getExcelRowList(trs);
        setExcelExtraCell(excelRowList);
        List<ExcelRowPojo> excelRowPojoList = new ArrayList<ExcelRowPojo>();
        //单元格起始行
        int beginRowCell = 2;
        for (Element tr : trs) {
            Elements tds = tr.select("td");
            //单元格起始列
            int beginColCell = 0;
            int colNum = 0;
            ExcelRowPojo excelRow = excelRowList.get(beginRowCell-2);
            List<ExcelCellPojo> excelCellList = excelRow.getExcelCellList();
            for (Element td : tds) {
                ExcelCellPojo excelCell = excelCellList.get(colNum);
                List<ExcelCellPojo> excelExtraCellList = excelRow.getExcelExtraCellList();
                String rowspan = "".equals(td.attr("rowspan"))?"1":td.attr("rowspan");
                String colspan = "".equals(td.attr("colspan"))?"1":td.attr("colspan");
                String value = "";
                // 单元格的起始行，结束行，起始列，结束列
/*                beginRowCell,
                  beginRowCell + Integer.valueOf(rowspan) - 1,
                  beginColCell,
                  beginColCell + Integer.valueOf(colspan) - 1*/
                if (td.attr("esi-type").equals(Constant.TdEsiType.DATA)) {
                    int[] colCell = excelCell.getColCell(beginColCell, beginRowCell + Integer.valueOf(rowspan) - 1, excelExtraCellList);
                    HSSFRow row = sheet.getRow(beginRowCell);
                    String cellValue = getCellFormatValue(row.getCell(colCell[0]));
                    td.html(cellValue);
                }
                beginColCell = beginColCell + Integer.valueOf(colspan);
                colNum++;
            }
            beginRowCell++;
        }
    }
    /**
     * 按行导入单元格内容
     *
     * @param trs
     * @param sheet
     */
    public static void importExcelToHtml2(Elements trs, HSSFSheet sheet) {
        List<ExcelRowPojo> excelRowPojoList = new ArrayList<ExcelRowPojo>();
        //单元格起始行
        int beginRowCell = 2;
        for (Element tr : trs) {
            Elements tds = tr.select("td");
            //单元格起始列
            int beginColCell = 0;
            for (Element td : tds) {
                String rowspan = "".equals(td.attr("rowspan"))?"1":td.attr("rowspan");
                String colspan = "".equals(td.attr("colspan"))?"1":td.attr("colspan");
                String value = "";
                // 单元格的起始行，结束行，起始列，结束列
/*                beginRowCell,
                  beginRowCell + Integer.valueOf(rowspan) - 1,
                  beginColCell,
                  beginColCell + Integer.valueOf(colspan) - 1*/
                if (td.attr("esi-type").equals(Constant.TdEsiType.DATA)) {
                    HSSFRow row = sheet.getRow(beginRowCell);
                    String cellValue = getCellFormatValue(row.getCell(beginColCell));
                    td.html(cellValue);
                }
                beginColCell = beginColCell + Integer.valueOf(colspan);
            }
            beginRowCell++;
        }
    }

    /**
     * 根据HSSFCell类型设置数据
     *
     * @param cell
     * @return
     */
    private static String getCellFormatValue(HSSFCell cell) {

        String cellvalue = "";
        if (cell != null) {
            // 判断当前Cell的Type
            switch (cell.getCellType()) {
                // 如果当前Cell的Type为NUMERIC
                case HSSFCell.CELL_TYPE_NUMERIC:
                case HSSFCell.CELL_TYPE_FORMULA: {
                    // 判断当前的cell是否为Date
                    if (HSSFDateUtil.isCellDateFormatted(cell)) {
                        // 如果是Date类型则，转化为Data格式

                        //方法1：这样子的data格式是带时分秒的：2011-10-12 0:00:00
                        //cellvalue = cell.getDateCellValue().toLocaleString();

                        //方法2：这样子的data格式是不带带时分秒的：2011-10-12
                        Date date = cell.getDateCellValue();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        cellvalue = sdf.format(date);

                    }
                    // 如果是纯数字
                    else {
                        // 取得当前Cell的数值
                        DataFormatter dataFormatter = new HSSFDataFormatter();
                        dataFormatter.createFormat(cell);
                        cellvalue = dataFormatter.formatCellValue(cell);
                        try {
                            Double.parseDouble(cellvalue);
                        } catch (NumberFormatException e) {
                            cellvalue = String.valueOf(cell.getNumericCellValue());
                        }


                    }
                    break;
                }
                // 如果当前Cell的Type为STRIN
                case HSSFCell.CELL_TYPE_STRING:
                    // 取得当前的Cell字符串
                    cellvalue = cell.getRichStringCellValue().getString();
                    break;
                // 默认的Cell值
                default:
                    cellvalue = " ";
            }
        } else {
            cellvalue = "";
        }
        return cellvalue;

    }
}
