package com.city.support.sys.update;

import java.util.Vector;
import java.util.concurrent.ExecutorService;

import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2016/5/9.
 */



        import java.util.Vector;
        import java.util.concurrent.ExecutorService;
        import java.util.concurrent.Executors;

/**
 * Created by wys on 2016/5/3.
 * <p/>
 * 任务执行器
 */
public class EsiAnalyExecutor implements Executor {
    private ExecutorService exe = Executors.newFixedThreadPool(10);
    private String type = "analysisHtml";
    private Vector<Task> taskVector = new Vector();
    public EsiAnalyExecutor(){
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public void addTask(final Task task) {
//        taskVector.add(task);
        exe.submit(new Runnable() {
            @Override
            public void run() {


                task.getEsiAction().execute(task);
            }
        });
    }

    @Override
    public void removeTask(Task task) {

    }
}
