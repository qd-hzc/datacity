package com.city.common.util.tree;

import com.city.common.pojo.DragDropPojo;

import java.util.List;

/**
 * Created by Administrator on 2016/1/7 0007.
 * 移动树节点排序
 */
public abstract class TreeDDSort<T> {

    /**
     * 保存排序
     * <pre>
     *     注意! 一定要在子类中重写,否则不会进入事物,也就不会更新!!!
     * </pre>
     */
    public void saveDragAndDrop(DragDropPojo dragDropPojo) {
        String dropPosition = dragDropPojo.getDropPosition();
        int moveId = dragDropPojo.getMoveId();
        int overId = dragDropPojo.getOverId();
        Integer overParentId = dragDropPojo.getOverParentId();
        //根据拖动位置进行相应的操作
        if (dropPosition.equals("append")) {
            appendNode(moveId, overId);
        } else {
            moveNode(moveId, overId, overParentId, dropPosition);
        }

    }

    /**
     * 添加到节点下
     *
     * @param moveId 要添加的节点
     * @param overId 目标节点
     */
    private void appendNode(int moveId, int overId) {
        T movedEntity = getEntityById(moveId);
        //所有目标节点所移动位置所在的同级,拖放到最后
        List<T> ts = getEntitiesByParent(overId);
        if (ts != null && ts.size() > 0) {//目标所移到的位置 同级不为空,设为最大
            T overEntity = ts.get(ts.size() - 1);
            int sort = getSort(overEntity);
            setSort(movedEntity, sort + 1);
        } else {//目标所移到的位置 同级为空,设为1
            setSort(movedEntity, 1);
        }
        //设置父节点
        setParentId(movedEntity, overId);
        //保存排序
        updateEntity(movedEntity);
    }

    /**
     * 移动到节点位置
     *
     * @param moveId       要添加的节点
     * @param overId       目标节点
     * @param overParentId 目标节点的父节点
     * @param dropPosition 拖动的位置
     */
    private void moveNode(int moveId, int overId, int overParentId, String dropPosition) {
        T movedEntity = getEntityById(moveId);
        //所有目标节点所移动位置所在的同级,拖放到最后
        List<T> ts = getEntitiesByParent(overParentId);
        for (T t : ts) {
            if (getEntityKey(t) == overId) {
                //定位
                int index = ts.indexOf(t);
                //移动
                if (dropPosition.equals("after")) {
                    setSort(movedEntity, getSort(t));
                    moveNodeForAfter(ts, index, moveId);
                } else {
                    setSort(movedEntity, getSort(t)-1);
                    moveNodeForBefore(ts, index, moveId);
                }
                //设更新节点
                setParentId(movedEntity, overParentId);
                updateEntity(movedEntity);

                break;
            }
        }
    }

    /**
     * 向前移动list
     *
     * @param ts     所有同级节点
     * @param index  目标节点所在的位置
     * @param moveId 要移动的节点
     */
    private void moveNodeForBefore(List<T> ts, int index, int moveId) {
        //需要移动的list
        List<T> list = ts.subList(0, index);
        //移动
        moveList(list, moveId, -1);
    }

    /**
     * 向后移动list
     *
     * @param ts     所有同级节点
     * @param index  目标节点所在的位置
     * @param moveId 要移动的节点
     */
    private void moveNodeForAfter(List<T> ts, int index, int moveId) {
        //需要移动的list
        List<T> list = ts.subList(index, ts.size());
        //移动
        moveList(list, moveId, 1);
    }

    /**
     * 移动节点
     *
     * @param ts     所有同级节点
     * @param moveId 要移动的节点
     * @param adden  前移赋值为-1,后移赋值为1
     */
    private void moveList(List<T> ts, int moveId, int adden) {
        if (ts != null && ts.size() > 0) {
            for (T t : ts) {
                //排除目标节点,将其他节点后移
                if (getEntityKey(t) != moveId) {
                    int sort = getSort(t) + adden;
                    setSort(t, sort);
                    updateEntity(t);
                }
            }
        }
    }

    /**
     * 获取实体的排序字段
     */
    protected abstract int getSort(T t);

    /**
     * 设置实体的排序字段
     */
    protected abstract void setSort(T t, int sort);

    /**
     * 设置父id
     */
    protected abstract void setParentId(T t, int parentId);

    /**
     * 根据id获取entity
     */
    protected abstract T getEntityById(int id);

    /**
     * 根据父id获取集合
     * <pre>
     *     注意! 一定要按照排序字段进行排序,否则排序会出问题
     * </pre>
     */
    protected abstract List<T> getEntitiesByParent(int id);

    /**
     * 更新entity
     */
    protected abstract void updateEntity(T t);

    /**
     * 获取实体的主键
     */
    protected abstract int getEntityKey(T t);
}
