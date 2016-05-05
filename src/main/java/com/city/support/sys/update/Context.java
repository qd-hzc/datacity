package com.city.support.sys.update;

/**
 * Created by wys on 2016/5/3.
 */
public class Context {
    private TaskQueue taskQueue;
    private EsiRule esiRule;

    public Context() {
        this.onCreate();
        if (this.taskQueue == null) {
            this.taskQueue = new TaskQueue(this);
            this.taskQueue.addExecutor(new EsiExecutor());
        }

        if (this.esiRule == null) {
            this.esiRule = new EsiRule();
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

    public EsiRule getEsiRule() {
        return esiRule;
    }

    public void setEsiRule(EsiRule esiRule) {
        this.esiRule = esiRule;
    }
}
