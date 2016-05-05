package com.city.support.sys.update;

import java.util.Vector;

/**
 * Created by wys on 2016/5/3.
 * <p/>
 * 任务执行器
 */
public class EsiExecutor implements Executor {

    private String type = "getRpt";
    private Vector<Task> taskVector = new Vector();

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public void addTask(Task task) {
        taskVector.add(task);
    }

    @Override
    public void removeTask(Task task) {

    }
}
