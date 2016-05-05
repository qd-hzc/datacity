package com.city.support.sys.update;

/**
 * Created by wys on 2016/5/3.
 */
public interface Executor {

    public String getType();

    public void addTask(Task task);
    public void removeTask(Task task);
}
