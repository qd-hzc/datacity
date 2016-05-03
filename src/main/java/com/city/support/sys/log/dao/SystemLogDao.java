package com.city.support.sys.log.dao;

import com.city.common.dao.BaseDao;
import com.city.common.pojo.Page;
import com.city.support.manage.timeFrame.entity.TimeFrame;
import com.city.support.sys.log.entity.SystemLog;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.stereotype.Repository;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by wgx on 2016/2/23.
 */
@Repository
public class SystemLogDao extends BaseDao<SystemLog> {

    //按照排序获取所有系统日志
    public List<SystemLog> getByOrder(String name, Date sDate, Date eDate) {
        StringBuilder sb = new StringBuilder("from SystemLog  where 1=1 ");
        if (name != null && name != "") {
            sb.append(" and ( userName like '%").append(name).append("%'");
            sb.append(" or info like '%").append(name).append("%' ) ");
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        to_date('2007-09-07 00:00:00','yyyy-mm-dd hh24:mi:ss')
        if (null != sDate) {
            sb.append(" and operateDate >= to_date('").append(format.format(sDate)).append("','yyyy-mm-dd hh24:mi:ss')");
        }
        if (null != eDate) {
            sb.append(" and operateDate <= to_date('").append(format.format(eDate)).append("','yyyy-mm-dd hh24:mi:ss')");
        }

        sb.append(" order by operateDate desc");
        return queryByHQL(sb.toString());
    }

    /**
     * 返回日志数量
     *
     * @param name
     * @param sDate
     * @param eDate
     * @return
     */
    public Integer selectLogCount(String name, Date sDate, Date eDate) {
        StringBuffer sb = new StringBuffer("select count(id) from SystemLog where 1=1 ");
        if (name != null && name != "") {
            sb.append(" and ( userName like '%").append(name).append("%'");
            sb.append(" or info like '%").append(name).append("%' )");
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        to_date('2007-09-07 00:00:00','yyyy-mm-dd hh24:mi:ss')
        if (null != sDate) {
            String format1 = format.format(sDate);
            sb.append(" and operateDate >= to_date('").append(format1).append("','yyyy-mm-dd hh24:mi:ss')");
        }
        if (null != eDate) {
            sb.append(" and operateDate <= to_date('").append(format.format(eDate)).append("','yyyy-mm-dd hh24:mi:ss')");
        }

        return Integer.parseInt(String.valueOf(unqueryByHQL(sb.toString())));
    }

    /**
     * 按搜索条件查询系统日志
     *
     * @param page
     * @param name
     * @param sDate
     * @param eDate @return
     */
    public List<SystemLog> findSystemLogByCondition(Page page, String name, Date sDate, Date eDate) {
        StringBuilder sb = new StringBuilder("from SystemLog  where 1=1 ");
        if (name != null && name != "") {
            sb.append(" and ( userName like '%").append(name).append("%'");
            sb.append(" or info like '%").append(name).append("%' ) ");
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        to_date('2007-09-07 00:00:00','yyyy-mm-dd hh24:mi:ss')
        if (null != sDate) {
            sb.append(" and operateDate >= to_date('").append(format.format(sDate)).append("','yyyy-mm-dd hh24:mi:ss')");
        }
        if (null != eDate) {
            sb.append(" and operateDate <= to_date('").append(format.format(eDate)).append("','yyyy-mm-dd hh24:mi:ss')");
        }

        sb.append(" order by operateDate desc");

        return queryWithPageByHQL(sb.toString(), page);
    }

    /**
     * 创建Excel文件
     *
     * @param response
     * @param text     表格列名称
     * @param list     按照查询条件得到要导出为Excel的系统日志
     * @return
     * @author CRX
     * @createDate 2016-4-29
     */
    public Object createExcel(HttpServletResponse response, String text, List<SystemLog> list) {

        // 第一步，创建一个webbook，对应一个Excel文件
        HSSFWorkbook wb = new HSSFWorkbook();
        // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet,并设置列宽
        HSSFSheet sheet = wb.createSheet("日志列表");
        sheet.setColumnWidth(0, 5000);
        sheet.setColumnWidth(1, 20000);
        sheet.setColumnWidth(2, 6000);
        sheet.setColumnWidth(3, 5000);
        // 第三步，在sheet中添加表头第0行
        HSSFRow row = sheet.createRow((int) 0);
        // 第四步，创建单元格，并设置值表头
        HSSFCellStyle style = wb.createCellStyle();
        HSSFFont font = wb.createFont();
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);//粗体显示
        style.setFont(font);
        String[] texts = text.split(",");
        for (int i = 0; i < texts.length; i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellValue(texts[i]);
            cell.setCellStyle(style);

        }
        for (int j = 0; j < list.size(); j++) {
            row = sheet.createRow(j + 1);
            SystemLog systemLog = list.get(j);
            // 第四步，创建单元格，并设置值  
            row.createCell(0).setCellValue(systemLog.getUserName());
            row.createCell(1).setCellValue(systemLog.getInfo());
            row.createCell(2).setCellValue(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(systemLog.getOperateDate()));
            row.createCell(3).setCellValue(systemLog.getIp());
        }
        response.reset();
        response.setContentType("application/msexcel;charset=UTF-8");
        try {
            response.addHeader("Content-Disposition", "attachment;filename=\""
                    + new String(("系统日志" + ".xls").getBytes("GBK"),
                    "ISO8859_1") + "\"");
            OutputStream out = response.getOutputStream();
            wb.write(out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

