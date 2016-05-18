package com.city.support.regime.collection.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.city.common.pojo.Constant;
import com.city.support.regime.collection.pojo.ExcelCellPojo;
import com.city.support.regime.collection.pojo.ExcelRowPojo;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 文件批量导出工具
 *
 * @author chenyongqiang
 */
public class ExportUtils {
    /**
     * 文件批量导出
     *
     * @param response  响应体
     * @param filePaths 文件路径的集合
     * @param zipType   导出类型：single和multi
     * @param path      工程的绝对路径
     */
    public static void export(HttpServletResponse response, List<String> filePaths, String zipType, String path) {
        export(response, filePaths, zipType, path, null);
    }

    /**
     * 文件批量导出
     *
     * @param response    响应体
     * @param filePaths   文件路径的集合
     * @param zipType     导出类型：single和multi
     * @param path        工程的绝对路径
     * @param outFileName 输出文件名
     */
    public static void export(HttpServletResponse response, List<String> filePaths, String zipType, String path, String outFileName) {
        //多报表下载，以zip形式打包下载
        //一共采用两种方式打包，第一种是以文件夹的形式打包，另一种是以多文件的形式打包
        //第一种方式
        if (zipType != null && zipType.equals("single")) {//multi和single两种方式，默认是multi
            //创建一个临时文件夹
            String dirName = "";
            if (outFileName != null && outFileName.trim().length() > 0) {
                dirName = outFileName;
            } else {
                SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd_HH_mm");
                dirName = "报表打包下载_" + format.format(new Date());
            }
            File dir = new File(path + File.separator + dirName);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            //把临时文件夹放到自动清除冗余文件器中
            AutoCleanRedundanceFiles.putRedundanceFiles(path + File.separator + dirName);
            //把文件拷贝到临时文件夹中去
            for (int i = 0; i < filePaths.size(); i++) {
                FileOutputStream outputStream = null;
                FileInputStream inputStream = null;
                try {
                    outputStream = new FileOutputStream(path + File.separator + dirName);
                    inputStream = new FileInputStream(filePaths.get(i));
                    byte[] buffer = new byte[1024];
                    int byteread = 0;
                    int bytesum = 0;
                    while ((byteread = inputStream.read(buffer)) != -1) {
                        bytesum += byteread;
                        outputStream.write(buffer, 0, byteread);
                    }
                    outputStream.flush();
                    outputStream.close();
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            createZipSingleFile(response, dir, dirName);//下载
            AutoCleanRedundanceFiles.deleteDir(dir);//删除临时文件夹
        } else {//第二种方式
            String dirName = "";
            if (outFileName != null && outFileName.trim().length() > 0) {
                dirName = outFileName;
            } else {
                dirName = "报表打包下载_" + DateTools.getDate("yyyy_MM_dd_HH_mm_ss");
            }
            //找到需要下的文件
            File files[] = new File[filePaths.size()];
            for (int i = 0; i < filePaths.size(); i++) {
                files[i] = new File(filePaths.get(i));
            }
            createZipMultiFile(response, files, dirName);//下载
            //把临时文件夹放到自动清除冗余文件器中
            AutoCleanRedundanceFiles.putRedundanceFiles(path + File.separator + dirName);
        }
    }

    /**
     * 替换文件路径的分隔符（把文件分隔符统一化）
     *
     * @param source
     * @return
     * @author cehnyongqiang
     */
    public static String repalceAllSeparator(String source) {
        if (source != null)
            return source.replaceAll("\\\\", "/").replaceAll("//", "/");
        return source;
    }

    /**
     * 报表类型转换
     * author: chenyongqiang
     *
     * @param fileType
     * @return
     */
    public static String getContentTypeByType(String fileType) {
        if (".txt".equals(fileType)) {
            return "text/plain";
        } else if (".doc".equals(fileType) || ".docx".equals(fileType)) {
            return "application/msword";
        } else if (".xls".equals(fileType) || ".xlsx".equals(fileType)) {
            return "application/vnd.ms-excel";
        } else if (".ppt".equals(fileType)) {
            return "application/vnd.ms-powerpoint";
        } else if (".xml".equals(fileType)) {
            return "text/xml";
        } else if (".pdf".equals(fileType)) {
            return "application/pdf";
        } else if (".jpg".equals(fileType) || ".jpeg".equals(fileType)) {
            return "image/jpeg";
        } else if (".gif".equals(fileType)) {
            return "image/gif";
        } else if (".png".equals(fileType)) {
            return "image/png";
        } else if (".bmp".equals(fileType)) {
            return "image/bmp";
        } else if (".zip".equals(fileType)) {
            return "application/zip";
        } else if (".torrent".equals(fileType)) {
            return "application/x-bittorrent";
        } else if (".mp3".equals(fileType)) {
            return "audio/mp3";
        } else if (".exe".equals(fileType)) {
            return "application/octet-stream";
        } else if (".avi".equals(fileType)) {
            return "video/x-msvideo";
        } else {
            return "application/x-msdownload";
        }
    }

    /**
     * 压缩多个文件（multi方式）
     *
     * @param response
     * @param inputFiles
     * @param zipName
     * @author chenyongqiang
     */
    private static void createZipMultiFile(HttpServletResponse response, File[] inputFiles, String zipName) {
        ZipOutputStream zos = null;
        try {
            response.addHeader("Content-disposition",
                    "attachment;filename=" + new String((zipName + ".rar").getBytes("GBK"), "ISO-8859-1"));
            zos = new ZipOutputStream(response.getOutputStream());
            zos.setEncoding("gb2312");
            for (int i = 0; i < inputFiles.length; i++) {
                writeZipFile(inputFiles[i], zos, "");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (zos != null)
                    zos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 压缩文件和文件夹（single方式）
     *
     * @param response
     * @param inputFile
     * @param zipName
     * @author chenyongqiang
     */
    private static void createZipSingleFile(HttpServletResponse response, File inputFile, String zipName) {
        ZipOutputStream zos = null;
        try {
            response.addHeader("Content-disposition",
                    "attachment;filename=" + new String((zipName + ".rar").getBytes("GBK"), "ISO-8859-1"));
            zos = new ZipOutputStream(response.getOutputStream());
            zos.setEncoding("GBK");
            writeZipFile(inputFile, zos, "");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (zos != null)
                    zos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 采用递归方式
     *
     * @param f
     * @param zos
     * @param hiberarchy
     * @author chenyongqiang
     */
    private static void writeZipFile(File f, ZipOutputStream zos, String hiberarchy) {
        if (f.exists()) {
            if (f.isDirectory()) {
                hiberarchy += f.getName() + File.separator;
                File[] fif = f.listFiles();
                for (int i = 0; i < fif.length; i++) {
                    writeZipFile(fif[i], zos, hiberarchy);
                }
            } else {
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(f);
                    ZipEntry ze = new ZipEntry(hiberarchy + f.getName());//压缩文件
                    zos.putNextEntry(ze);
                    int nNumber;
                    byte[] buf = new byte[100 * 1024];
                    while ((nNumber = fis.read(buf)) != -1) {
                        zos.write(buf, 0, nNumber);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (fis != null)
                            fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }

    }

    /**
     * 设置单元格样式
     *
     * @param wb
     * @param align 水平样式
     * @return
     */
    public static HSSFCellStyle getHssfCellStyle(HSSFWorkbook wb, short align) {
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
    public static void setRegionBorder(int border, CellRangeAddress region, Sheet sheet, Workbook wb, int nodeType) {
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
     * @param hasData
     */
    public static List<ExcelRowPojo> getExcelRowList(Elements trs,Boolean hasData) {
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
                String rowspan = "".equals(td.attr("rowspan")) ? "1" : td.attr("rowspan");
                String colspan = "".equals(td.attr("colspan")) ? "1" : td.attr("colspan");
                String value = "";


                // 添加单元格的起始行，结束行，起始列，结束列
                ExcelCellPojo excelCellPojo = new ExcelCellPojo(beginRowCell,
                        beginRowCell + Integer.valueOf(rowspan) - 1,
                        beginColCell,
                        beginColCell + Integer.valueOf(colspan) - 1);
                if (!td.attr("esi-type").equals(Constant.TdEsiType.DATA)) {
                    value = td.text();
                }else if(hasData!=null&&hasData){
                    if(td.select("input").size()==0){
                        value = td.text();
                    }else{
                        value = td.select("input").first().attr("value");
                    }
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
        List<ExcelRowPojo> excelRowList = getExcelRowList(trs,false);
        setExcelExtraCell(excelRowList);
        List<ExcelRowPojo> excelRowPojoList = new ArrayList<ExcelRowPojo>();
        //单元格起始行
        int beginRowCell = 2;
        for (Element tr : trs) {
            Elements tds = tr.select("td");
            //单元格起始列
            int beginColCell = 0;
            int colNum = 0;
            ExcelRowPojo excelRow = excelRowList.get(beginRowCell - 2);
            List<ExcelCellPojo> excelCellList = excelRow.getExcelCellList();
            for (Element td : tds) {
                ExcelCellPojo excelCell = excelCellList.get(colNum);
                List<ExcelCellPojo> excelExtraCellList = excelRow.getExcelExtraCellList();
                String rowspan = "".equals(td.attr("rowspan")) ? "1" : td.attr("rowspan");
                String colspan = "".equals(td.attr("colspan")) ? "1" : td.attr("colspan");
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
    public static void importExcelToHtml(Elements trs, XSSFSheet sheet) {
        List<ExcelRowPojo> excelRowList = getExcelRowList(trs,false);
        setExcelExtraCell(excelRowList);
        List<ExcelRowPojo> excelRowPojoList = new ArrayList<ExcelRowPojo>();
        //单元格起始行
        int beginRowCell = 2;
        for (Element tr : trs) {
            Elements tds = tr.select("td");
            //单元格起始列
            int beginColCell = 0;
            int colNum = 0;
            ExcelRowPojo excelRow = excelRowList.get(beginRowCell - 2);
            List<ExcelCellPojo> excelCellList = excelRow.getExcelCellList();
            for (Element td : tds) {
                ExcelCellPojo excelCell = excelCellList.get(colNum);
                List<ExcelCellPojo> excelExtraCellList = excelRow.getExcelExtraCellList();
                String rowspan = "".equals(td.attr("rowspan")) ? "1" : td.attr("rowspan");
                String colspan = "".equals(td.attr("colspan")) ? "1" : td.attr("colspan");
                String value = "";
                // 单元格的起始行，结束行，起始列，结束列
/*                beginRowCell,
                  beginRowCell + Integer.valueOf(rowspan) - 1,
                  beginColCell,
                  beginColCell + Integer.valueOf(colspan) - 1*/
                if (td.attr("esi-type").equals(Constant.TdEsiType.DATA)) {
                    int[] colCell = excelCell.getColCell(beginColCell, beginRowCell + Integer.valueOf(rowspan) - 1, excelExtraCellList);
                    XSSFRow row = sheet.getRow(beginRowCell);
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
                String rowspan = "".equals(td.attr("rowspan")) ? "1" : td.attr("rowspan");
                String colspan = "".equals(td.attr("colspan")) ? "1" : td.attr("colspan");
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

    /**
     * 根据XSSFCell类型设置数据
     *
     * @param cell
     * @return
     */
    private static String getCellFormatValue(XSSFCell cell) {

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


