package com.city.support.sys.update;

/**
 * Created by wys on 2016/5/3.
 */
public class Task {
    //任务类型
    public String type = "";
    //任务状态
    private TaskState state=TaskState.READY;
    //任务所在的队列
    private TaskQueue taskQueue;
    //任务的执行器
    private Executor executor;
    //任务携带的命令
    private Action esiAction;
    //任务携带的数据
    private EsiTaskData esiTaskData;
    //任务的执行环境
    private Context context;

    public Task(String type, EsiTaskData esiTaskData) {
        this.type = type;
        this.esiTaskData = esiTaskData;
    }

    public boolean taskReady() {
        Boolean result = false;
        if (!this.state.equals(TaskState.EXECUTE)) {
            this.state = TaskState.READY;
            result = true;
        }

        return result;
    }

    public boolean taskWait() {
        Boolean result = false;
        if (!this.state.equals(TaskState.EXECUTE)) {
            this.state = TaskState.WAIT;
            result = true;
        }
        return result;
    }

    public void release() {
        if (!this.state.equals(TaskState.EXECUTE)) {
            if (taskQueue != null) {
                this.state = TaskState.RELEASE;
                //看看是否从执行器和队列里移除
                /*taskQueue.removeTask(this);
                executor.removeTask(this);*/
            }
        }
    }

    public boolean canRemove() {
        //执行状态中的人物不能移除
        if (this.state.equals(TaskState.EXECUTE))
            return false;
        else
            return true;
    }

    public Executor getExecutor() {
        return executor;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public EsiTaskData getEsiTaskData() {
        return esiTaskData;
    }

    public void setEsiTaskData(EsiTaskData esiTaskData) {
        this.esiTaskData = esiTaskData;
    }

    public Action getEsiAction() {
        return esiAction;
    }

    public void setEsiAction(Action esiAction) {
        this.esiAction = esiAction;
    }

    public TaskState getState() {
        return state;
    }

    public TaskQueue getTaskQueue() {
        return taskQueue;
    }

    public void setTaskQueue(TaskQueue taskQueue) {
        this.taskQueue = taskQueue;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
