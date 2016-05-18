package com.city.support.sys.update;

/**
 * Created by wys on 2016/5/3.
 */
public class Context {
    private TaskQueue taskQueue;

    public Context() {
        this.onCreate();
        if (this.taskQueue == null) {
            setTaskQueue(new TaskQueue(this));
            this.taskQueue.addExecutor(new EsiExecutor());
            this.taskQueue.addExecutor(new EsiAnalyExecutor());
        }
        this.onInit();
    }

    public void onCreate() {

    }

    public void onInit() {

    }

    public void onDestroy() {

    }

    public TaskQueue getTaskQueue() {
        return taskQueue;
    }

    public void setTaskQueue(TaskQueue taskQueue) {
        this.taskQueue = taskQueue;
    }

}
