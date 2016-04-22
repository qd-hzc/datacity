package com.city.common.util.tree;

/**
 * Created by wys on 2016/1/4.
 */
public class GenTreeWatcher {
    private String watcherName;

    public GenTreeWatcher() {
    }

    public GenTreeWatcher(String watcherName) {
        this.watcherName = watcherName;
    }

    public void operate(Object[] args) {

    }

    public String getWatcherName() {
        return watcherName;
    }

    public void setWatcherName(String watcherName) {
        this.watcherName = watcherName;
    }
}
