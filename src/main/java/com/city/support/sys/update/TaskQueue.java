package com.city.support.sys.update;


import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.Iterator;
import java.util.Vector;

/**
 * Created by wys on 2016/5/3.
 *
 * 任务队列
 */
public class TaskQueue {
    private Vector<Executor> executors = new Vector();
    private Vector<Task> taskVector = new Vector();
    private Context context;

    public TaskQueue(Context context) {
        this.context = context;
    }

    public boolean addTask(Task task) {
        Boolean result = false;
        if (task.taskReady()) {
            task.setTaskQueue(this);
            task.setContext(this.context);
            taskVector.add(task);
            notifyExcutor(task);
            result = true;
        }
        return result;
    }

    public void removeTask(Task task) {
        Iterator<Task> taskIterator = taskVector.iterator();
        Task item = null;
        while (taskIterator.hasNext()) {
            item = taskIterator.next();
            if (item.equals(task) && task.canRemove()) {
                item.release();//释放任务执行的操作
                taskIterator.remove();
            }
        }
    }

    private void notifyExcutor(Task task) {
        for (Executor executor : executors) {
            if (executor.getType() != null && executor.getType().equals(task.getType())) {
                executor.addTask(task);
                task.setExecutor(executor);
                break;
            }
        }
    }


    //添加监听任务器
    public void addExecutor(Executor executor) {
        if (executor != null)
            this.executors.add(executor);
    }

    public void removeExecutor(Executor executor) {
        Iterator<Executor> executorIterator = executors.iterator();
        Executor item = null;
        while (executorIterator.hasNext()) {
            item = executorIterator.next();
            if (item.equals(executor)) {
                executorIterator.remove();
            }
        }
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
