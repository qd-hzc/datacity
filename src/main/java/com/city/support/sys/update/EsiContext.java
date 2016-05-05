package com.city.support.sys.update;

import org.springframework.stereotype.Component;

/**
 * Created by wys on 2016/5/3.
 */
//@Component
public class EsiContext extends Context {
    //在创建时初始化任务队列和规则
    @Override
    public void onCreate() {
        //初始化规则
        setEsiRule(new EsiRule());
    }

    //初始化时，创建任务。
    @Override
    public void onInit() {
        //任务所需的数据
        EsiTaskData data = new EsiTaskData();
        data.addData("rptId", 5);
        //任务执行的命令
        Action command = new EsiAction(this.getEsiRule(),"getRpt");
        //创建任务
        Task getRptTask = new Task("getRpt", data);
        getRptTask.setEsiAction(command);
        //将任务添加到任务队列中
        getTaskQueue().addTask(getRptTask);

    }


}
