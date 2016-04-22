package com.city.common.util.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wys on 2016/1/4.
 */
public abstract class GenTreeUtil<T> {

    public final static String GENNODE = "genNode";
    public final static String BEFOREADDNODE = "beforeAddNode";
    public final static String ADDNODE = "addNode";
    private List<GenTreeWatcher> watchers = new ArrayList<GenTreeWatcher>();

    public Map<String, Object> genTree(Map<String, Object> node, List<T> datas) {
        Object treeId = null;
        Map<String, Object> child = null;
        List children = null;
        if (node == null) {
            node = new HashMap<>();
            node.put("id", 0);
            treeId = node.get("id");
        }
        treeId = getTreeId(node);
        for (T data : datas) {
            if (treeId.equals(getEntityPid(data))) {
                child = genTreeNode(data);
                addNode(node, child);
                genTree(child, datas);
            }
        }
        //设置该节点是否为叶子节点
        children = (List) node.get("children");
        node.put("leaf", isLeaf(node));

        Object[] args = {node, children, datas};
        this.notifyWatcher(GENNODE, args);
        return node;
    }

    //默认id可重写
    protected Object getTreeId(Map<String, Object> node) {
        Object id = null;
        id = node.get("id");
        if (id != null) {
            if(id instanceof String)
                id = Integer.valueOf((String)id);
            return id;
        } else {
            return 0;
        }
    }

    protected boolean isLeaf(Map<String, Object> node) {
        List children = null;
        children = (List) node.get("children");
        if (children != null) {
            return false;
        } else {
            return true;
        }
    }

    //获取节点的pid
    protected abstract Object getEntityPid(T entity);

    //生成节点Map
    public abstract Map<String, Object> genTreeNode(T entity);

    //添加子节点
    protected void addNode(Map<String, Object> node, Map child) {
        Object[] args = {node, child};
        this.notifyWatcher(BEFOREADDNODE, args);
        List children = null;
        children = (List) node.get("children");
        if (children != null) {
            children.add(child);
        } else {
            children = new ArrayList();
            children.add(child);
            node.put("children", children);
        }
        this.notifyWatcher(ADDNODE, args);
    }

    //通知监听
    protected void notifyWatcher(String watcherName, Object[] args) {
        for (GenTreeWatcher watcher : watchers) {
            if (watcherName != null && watcherName.equals(watcher.getWatcherName())) {
                watcher.operate(args);
            }
        }
    }

    //添加监听
    public void addWatcher(GenTreeWatcher watcher) {
        watchers.add(watcher);
    }

    //删除监听
    public void removeWatcher(GenTreeWatcher watcher) {
        watchers.remove(watcher);
    }

    //获取所有监听
    public List<GenTreeWatcher> getWatchers() {
        return watchers;
    }
}
