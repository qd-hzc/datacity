package com.city.app.staffValid.controller;


import com.city.app.staffValid.entity.AppPerson;
import com.city.app.staffValid.service.AppPersonService;
import com.city.common.controller.BaseController;

import com.city.common.util.StringUtil;
import com.city.support.regime.collection.util.ExportUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;


/**
 * Created by Administrator on 2016/3/30.
 */
@Controller
@RequestMapping("/app/staffValid/excel")
public class AppStaffExcelController extends BaseController {
    @Autowired
    private AppPersonService appPersonService;
    /**
     * 导出excel
     *
     *
     * @return
     */
    @RequestMapping("/batchExportToExcel")
    @ResponseBody
    public void batchExportToExcel(HttpServletResponse response, HttpServletRequest request,String staffs) throws IOException {
       /* List<AppPerson> persons=new ArrayList<>();
        if(StringUtil.trimNotEmpty(staffs)){
            persons=new Gson().fromJson(staffs,new TypeToken<List<AppPerson>>(){}.getType());
        }*/
//        List<AppPerson> persons=new Gson().fromJson(staffs,new TypeToken<List<AppPerson>>(){}.getType());
        OutputStream outputStream = response.getOutputStream();;
        InputStream inputStream =exportExcel(staffs);
        String downName=new String("人员.xls".getBytes(),"iso-8859-1");
        response.setHeader("Content-disposition","attachment; filename="
                +downName);
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1){
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.close();

//        System.out.print(staffs);


    }
    /**
     * 导出Excel
     * @return
     */
    public InputStream exportExcel(String  staffs1){
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("sheet1");

        HSSFRow row = sheet.createRow(0);

        HSSFCell cell = row.createCell( 0);
        cell.setCellValue("编号");

        cell = row.createCell( 1);
        cell.setCellValue("姓名");

        cell = row.createCell(2);
        cell.setCellValue("电话");

        cell = row.createCell( 3);
        cell.setCellValue("邮箱");

        cell = row.createCell( 4);
        cell.setCellValue("部门");

        cell = row.createCell( 5);
        cell.setCellValue("职务");

        cell = row.createCell( 6);
        cell.setCellValue("验证码");

      /* cell = row.createCell((short) 7);
        cell.setCellValue("角色");*/

        cell = row.createCell(7);
        cell.setCellValue("说明");


       List<AppPerson> staffs = appPersonService.queryAllStaffs(staffs1);
        for (int i = 0; i < staffs.size(); ++i){
            AppPerson staff = staffs.get(i);

            row = sheet.createRow(i + 1);

            cell = row.createCell(0);
            cell.setCellValue(staff.getId());

            cell = row.createCell(1);
            cell.setCellValue(staff.getName());

            cell = row.createCell( 2);
            cell.setCellValue(staff.getPhone());

            cell = row.createCell(3);
            cell.setCellValue(staff.getEmail());

            cell = row.createCell( 4);
            if(staff.getDepartment()==null){
                cell.setCellValue("");
            }else{
                cell.setCellValue(staff.getDepartment().getDepName());
            }


            cell = row.createCell( 5);
            cell.setCellValue(staff.getDuty());

            cell = row.createCell( 6);
            cell.setCellValue(staff.getValidCode());

          /*  cell = row.createCell((short) 7);
            cell.setCellValue(staff.getRole().getName());*/
            cell = row.createCell( 7);
            cell.setCellValue(staff.getComments());

        }

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try{
            wb.write(os);
        }catch (IOException e){
            e.printStackTrace();
        }
        byte[] content = os.toByteArray();
        InputStream is = new ByteArrayInputStream(content);
        return is;
    }


}
