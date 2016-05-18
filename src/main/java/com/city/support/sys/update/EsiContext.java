package com.city.support.sys.update;

import com.city.support.sys.update.pojo.RptData;
import com.city.support.sys.update.pojo.TmpData;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wys on 2016/5/3.
 */
@Component
public class EsiContext extends Context {
    final static String TASKFILE = "E:/test.json";
    @Autowired
    private EsiRule esiRule;
    private List<TmpData> tmpDatas;


    //创建时执行的代码
    @Override
    public void onCreate() {
    }

    //初始化时执行的代码
    @Override
    public void onInit() {
        //初始化时读取任务所需的数据
        tmpDatas = getTmpDatas();
    }

    public void createTask() {
        for (TmpData tmpData : tmpDatas) {
            if ("用电量".equals(tmpData.getName()) || "电信".equals(tmpData.getName())) {

                for (RptData rptData : tmpData.getRpts()) {
                    EsiTaskData data = new EsiTaskData();
                    data.addData("tmpOldId", tmpData.getTmpOldId());
                    data.addData("name", tmpData.getName());
                    data.addData("createRptYear", rptData.getCreateRptYear());
                    data.addData("createRptScope", rptData.getCreateRptScope());
                    data.addData("msg", "抓取");
                    //任务执行的命令
                    Action command = new EsiAction(this.esiRule, "getHtml");
                    //创建任务
                    Task getRptTask = new Task("getHtml", data);

                    getRptTask.setEsiAction(command);
                    //将任务添加到任务队列中
                    getTaskQueue().addTask(getRptTask);
                }
            }
        }
    }

    /**
     * 读取要执行的报表
     *
     * @return
     * @throws IOException
     */
    private List<TmpData> getTmpDatas() {
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        List<TmpData> result = new ArrayList();
        try {
            inputStream = new FileInputStream(TASKFILE);
            inputStreamReader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(inputStreamReader);
            String currentStr;
            StringBuffer stringBuffer = new StringBuffer();
            while ((currentStr = bufferedReader.readLine()) != null) {
                stringBuffer.append(currentStr);
            }
            JsonArray jsonArray = null;
            JsonParser jp = new JsonParser();
            JsonElement je = jp.parse(stringBuffer.toString());

            if (je.isJsonArray()) {
                jsonArray = je.getAsJsonArray();

            } else {
                //解析错误
            }
            Gson g = new Gson();

            TmpData tmpData = null;
            for (int i = 0; i < jsonArray.size(); i++) {
                tmpData = g.fromJson(jsonArray.get(i), TmpData.class);
                result.add(tmpData);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null)
                    inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }


}
