package com.city.support.sys.update;


//import org.json.JSONArray;

import com.city.support.regime.collection.dao.ReportInfoDao;
import com.city.support.regime.collection.service.ReportInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wys on 2016/5/3.
 * <p/>
 * 任务执行者，方法名与命令同名且任务方法必须接收Task参数
 */
@Component
public class EsiRule {
    final static String DATAURL = "http://10.0.1.163:8088/dm_taian/public/DataQuery/findReportData";
    @Autowired
    private ReportInfoService reportInfoService;

    @Autowired
    public EsiRule(ReportInfoDao reportInfoDao) {
        System.out.println(reportInfoDao);
    }


    public void getRpt(Task task) {

        System.out.println(task.getEsiTaskData().getData("msg"));
        EsiTaskData data = new EsiTaskData();
        data.addData("rptId", task.getEsiTaskData().getData("rptId"));
        data.addData("msg", "分析" + task.getEsiTaskData().getData("rptId"));
        //任务执行的命令
        Action command = new EsiAction(this, "analysisRpt");
        //创建任务
        Task getRptTask = new Task("getRpt", data);
        getRptTask.setEsiAction(command);
        //将任务添加到任务队列中
        task.getTaskQueue().addTask(getRptTask);

    }

    public void analysisRpt(Task task) {
        System.out.println(task.getEsiTaskData().getData("msg"));

    }


    public void analysisHtml(Task task) {
        EsiTaskData data = task.getEsiTaskData();
        Integer newRptId = null;
        String html = null;
        if (data != null) {
            newRptId = (Integer) data.getData("newRptId");
            html = (String) data.getData("html");
            System.out.println(data.getData("msg"));
            //调用接口录入

            //reportExtractService.extractHtml(newRptId,html,request);
        }
    }

    public void getHtml(Task task) {
        try {
            EsiHttpClient esiHttpClient = new EsiHttpClient();
            EsiTaskData data = task.getEsiTaskData();
            Integer tmpOldId = null;
            String name = null;
            Integer year = null;
            Integer period = null;
            if (data != null) {
                tmpOldId = (Integer) data.getData("tmpOldId");
                year = (Integer) data.getData("createRptYear");
                period = (Integer) data.getData("createRptScope");
                name = (String) data.getData("name");
                System.out.println(data.getData("msg") + " " + period + ":" + year);


                //调用抓取html接口，调用之后创建分析任务添加到队列中
                //获取HTML信息
                Map<String, String> map = new HashMap();
                map.put("monthId", period.toString());
                map.put("rptTempId", tmpOldId.toString());
                map.put("yearId", year.toString());
                String resultHtml = esiHttpClient.sendRequest(DATAURL, map);
                System.out.println("内容" + resultHtml);

                //若存在该报表
                if (reportInfoService.getReportId(name, year, period) != 0 && resultHtml != null && !"".equals(resultHtml)) {
                    EsiTaskData dataRpts = new EsiTaskData();
                    //获取新的ID
                    dataRpts.addData("newRptId", reportInfoService.getReportId(name, year, period));
                    dataRpts.addData("html", resultHtml);
                    dataRpts.addData("msg", "分析任务");
                    //创建分析任务添加到队列中
                    //任务执行的命令
                    Action command = new EsiAction(this, "analysisHtml");
                    //创建任务
                    Task analysisHtmlTask = new Task("analysisHtml", dataRpts);
                    analysisHtmlTask.setEsiAction(command);
                    //将任务添加到任务队列中
                    task.getTaskQueue().addTask(analysisHtmlTask);
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
