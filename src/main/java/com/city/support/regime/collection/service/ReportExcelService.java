package com.city.support.regime.collection.service;

import com.city.common.pojo.Constant;
import com.city.support.regime.collection.entity.ReportInfo;
import com.city.support.regime.collection.pojo.ExcelCellPojo;
import com.city.support.regime.collection.pojo.ExcelRowPojo;
import com.city.support.regime.collection.util.AutoCleanRedundanceFiles;
import com.city.support.regime.collection.util.ExportUtils;
import com.city.support.regime.report.entity.ReportTemplate;
import com.city.support.regime.report.service.ReportManageService;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wgx on 2016/5/16.
 */
@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
public class ReportExcelService {

    @Autowired
    private ReportManageService reportManageService;
    @Autowired
    private ReportInfoService reportInfoService;

    /**
     * 批量导出excel
     *
     * @param response
     * @param request
     * @param isOne    是否导出为一张excel
     * @param hasData  是否包含已填报的数据
     */
    public void batchExportToExcel(HttpServletResponse response, HttpServletRequest request, String reportList, Boolean isOne, Boolean hasData) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<Map<String, String>>>() {
        }.getType();
        List<Map<String, String>> list = gson.fromJson(reportList, type);
        String rptInfoIds = list.get(0).get("rptInfoIds");
        String rptTmpIds = list.get(0).get("rptTmpIds");
        if (isOne != null && isOne) {
            //下载路径
            String basePath = request.getSession().getServletContext().getRealPath("/");
            //打包插件路径
            String exportPath = basePath + "download";

            List<String> fileNameList = new ArrayList();// 用于存放生成的文件名称
            String showName = exportToExcel(fileNameList, exportPath, rptInfoIds, rptTmpIds, hasData);
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
                    showName = exportToExcel(fileNameList, exportPath, rptInfoIds, rptTmpId, hasData);
                }
                if (rptTmpIdList.length == 1) {
                    ExportUtils.export(response, fileNameList, "multi", exportPath, showName);
                } else {
                    ExportUtils.export(response, fileNameList, "multi", exportPath);
                }
            }

        }
    }
    /**
     * 按照模板id导出excel
     *
     * @param rptInfoIds 报表id
     * @param rptTmpIds  模板id
     */
    public String exportToExcel(List<String> fileNameList, String path, String rptInfoIds, String rptTmpIds, Boolean hasData) {

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
                    //ReportTemplateStyle reportTemplateStyle = reportManageService.getRptStyleByRptInfo(reportInfo);
                    Integer rptTmpId = reportInfo.getTmpId();
                    Integer period = reportInfo.getPeriod();
                    Integer year = reportInfo.getYear();
                    Integer month = reportInfo.getMonth();
                    String sheetTime = year + Constant.FrequencyType.YEAR_STRING + Constant.FrequencyType.getFrequencyName(period, month);
                    // 在webbook中添加一个sheet,对应Excel文件中的sheet
                    HSSFSheet sheet = null;
                    if (hasData != null && hasData) {
                        //    sheet = wb.createSheet(sheetTime);
                        sheet = wb.createSheet(/*name + Constant.UNDERLINE +*/ sheetTime);
                    } else {
                        sheet = wb.createSheet(/*name + Constant.UNDERLINE +*/ sheetTime + Constant.UNDERLINE + rptTmpId);
                    }

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


                    if (reportInfo.getId() != null) {
                        String styleHtml = reportInfoService.getRptInfoHtml(reportInfo.getId());
                        //String styleHtml = reportTemplateStyle.getRptStyle();
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
                                    if (collength == 0) {
                                        Elements tmpTds = tbodys.first().select("tr").first().select("td");
                                        for (Element td : tmpTds) {
                                            collength += Integer.valueOf(td.attr("colspan"));
                                        }
                                    }
                                    Elements trs = tbodys.select("tr");
                                    List<ExcelRowPojo> excelRowList = ExportUtils.getExcelRowList(trs, hasData);
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


                        } else {

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
}
