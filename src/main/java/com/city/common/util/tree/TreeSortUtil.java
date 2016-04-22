package com.city.common.util.tree;

import com.city.support.manage.pojo.DragAndDropVO;

import java.util.List;

/**
 * 排序的entity与获取时是否过滤状态有关
 * Created by HZC on 2016/1/6.
 */
public abstract class TreeSortUtil<T> {

    /**
     * 获取序号
     *
     * @param t
     * @return
     */
    protected abstract int getSort(T t);

    /**
     * 设置序号
     *
     * @param t
     * @param sort
     */
    protected abstract void setSort(T t, int sort);

    /**
     * 设置父id
     *
     * @param t
     * @param parentId
     */
    protected abstract void setParentId(T t, int parentId);

    /**
     * 根据id获取entity
     *
     * @param id
     * @return
     */
    protected abstract T getEntityById(int id);

    /**
     * 根据父id获取集合
     *
     * @param id
     * @return
     */
    protected abstract List<T> getEntitiesByParent(int id);

    /**
     * 更新entity
     *
     * @param t
     */
    protected abstract void updateEntity(T t);

    /**
     * 设置是否为叶子节点
     *
     * @param over
     * @param b
     */
    protected abstract void setLeaf(T over, boolean b);

    /**
     * 拖拽排序
     *
     * @param dragAndDrop
     * @return
     * @author hzc
     * @createDate 2016-1-6
     */
    public boolean saveDragAndDrop(DragAndDropVO dragAndDrop) {

        /*
            1、before：
                a、如果 moveParentId ！= overParentId ，则 move 的原同级重新排序，执行 b。
                  否则执行 c。
                b、moveParentId = overParentId ，moveSort = overSort ，over 及以后的都 sort + 1
                c、如果 moveSort > overSort ，执行 moveSort = overSort , overSort =< sort < moveSort 的都 sort + 1。
                  如果 moveSort < overSort ， moveSort = overSort - 1, moveSort < sort < overSort 的所有 sort -1
            2、after：
                a、如果 moveParentId ！= overParentId ，则 move 的原同级重新排序，执行 b。
                  否则执行 c。
                b、moveParentId = overParentId ，moveSort = overSort + 1，over 以后的都 sort + 1
                c、如果 moveSort > overSort , moveSort = overSort + 1, moveSort > sort > overSort 的 sort + 1。
                  如果 moveSort < overSort , moveSort = overSort , moveSort < sort <= overSort 的 sort -1.
            3、append
                a、move 的原同级重新排序，执行 b。
                b、moveParentId = overId ，moveSort = over 的子的个数 + 1
         */
        String moveIdS = dragAndDrop.getMoveId();
        int moveId = Integer.parseInt(moveIdS);
        String overIdS = dragAndDrop.getOverId();
        int overId = Integer.parseInt(overIdS);
        String moveParentIdS = dragAndDrop.getMoveParentId();
        int moveParentId = Integer.parseInt(moveParentIdS);
        String overParentIdS = dragAndDrop.getOverParentId();
        int overParentId = Integer.parseInt(overParentIdS);
        T move = getEntityById(moveId);
        T over = getEntityById(overId);
        switch (dragAndDrop.getDropPosition()) {
            case "before":
                if (!moveParentIdS.equals(overParentIdS)) {
//                    查询 move 原同级
                    sortMoveYuanTongJi(move, moveParentId);
//                    新的 sort 排序
                    sortMoveNewForBeforeB(overParentId, move, over);
                } else {
                    if (getSort(move) > getSort(over)) {
                        sortMoveNewForBeforeCPlus(overParentId, move, over);
                    } else {
                        sortMoveNewForBeforeC(overParentId, move, over);
                    }
                }
                break;
            case "after":
                if (!moveParentIdS.equals(overParentIdS)) {
//                    查询 move 原同级
                    sortMoveYuanTongJi(move, moveParentId);
//                    新的 sort 排序
                    sortMoveNewForAfterB(overParentId, move, over);
                } else {
                    if (getSort(move) > getSort(over)) {
                        sortMoveNewForAfterCPlus(overParentId, move, over);
                    } else {
                        sortMoveNewForAfterC(overParentId, move, over);
                    }
                }
                break;
            case "append":
//                查询 move 原同级
                sortMoveYuanTongJi(move, moveParentId);
                sortMoveNewForAppend(overId, move);
                setLeaf(over, false);
                updateEntity(over);
                break;
        }
        return true;
    }


    /**
     * 重新排序 append 方式的拖动的 节点
     *
     * @param overId
     * @param move
     * @author hzc
     * @createDate 2016-1-6
     */
    private void sortMoveNewForAppend(int overId, T move) {
        setParentId(move, overId);
        List<T> itemDictByParent = getEntitiesByParent(overId);
        setSort(move, itemDictByParent.size() + 1);
        updateEntity(move);
    }

    /**
     * 排序新的树顺序
     * <pre>
     *     如果 moveSort < overSort , moveSort = overSort , moveSort < sort <= overSort 的 sort -1.
     * </pre>
     *
     * @param overParentId 停留节点的父id
     * @param move         移动节点
     * @param over         停留节点
     * @author hzc
     * @createDate 2016-1-6
     */
    private void sortMoveNewForAfterC(int overParentId, T move, T over) {
        setParentId(move, overParentId);
        Integer overSort = getSort(over);
        Integer moveSort = getSort(move);
        List<T> overItemDicts = getEntitiesByParent(overParentId);
        for (int i = 0; i < overItemDicts.size(); i++) {
            T entity = overItemDicts.get(i);
            Integer sort = getSort(entity);
            if (sort <= overSort && sort > moveSort) {
                setSort(entity, sort - 1);
                updateEntity(entity);
            }
        }
        setSort(move, overSort);
        updateEntity(move);
    }

    /**
     * 排序新的树顺序
     * <pre>
     *     如果 moveSort > overSort , moveSort = overSort + 1, moveSort > sort > overSort 的 sort + 1。
     * </pre>
     *
     * @param overParentId 停留节点的父id
     * @param move         移动节点
     * @param over         停留节点
     * @author hzc
     * @createDate 2016-1-6
     */
    private void sortMoveNewForAfterCPlus(int overParentId, T move, T over) {
        setParentId(move, overParentId);
        Integer overSort = getSort(over);
        Integer moveSort = getSort(move);
        List<T> overItemDicts = getEntitiesByParent(overParentId);
        for (int i = 0; i < overItemDicts.size(); i++) {
            T entity = overItemDicts.get(i);
            Integer sort = getSort(entity);
            if (sort > overSort && sort < moveSort) {
                setSort(entity, sort + 1);
                updateEntity(entity);
            }
        }
        setSort(move, overSort + 1);
        updateEntity(move);
    }

    /**
     * 排序新的树顺序
     * <pre>
     *     moveParentId = overParentId ，moveSort = overSort + 1，over 以后的都 sort + 1
     * </pre>
     *
     * @param overParentId 停留节点的父id
     * @param move         移动节点
     * @param over         停留节点
     * @author hzc
     * @createDate 2016-1-6
     */
    private void sortMoveNewForAfterB(int overParentId, T move, T over) {
        setParentId(move, overParentId);
        Integer overSort = getSort(over);
        List<T> overItemDicts = getEntitiesByParent(overParentId);
        for (int i = 0; i < overItemDicts.size(); i++) {
            T entity = overItemDicts.get(i);
            Integer sort = getSort(entity);
            if (sort > overSort) {
                setSort(entity, sort + 1);
                updateEntity(entity);
            }
        }
        setSort(move, overSort + 1);
        updateEntity(move);
    }

    /**
     * 排序新的树顺序
     * <pre>
     *     如果 moveSort < overSort ， moveSort = overSort - 1, moveSort < sort < overSort 的所有 sort -1
     * </pre>
     *
     * @param overParentId 停留节点的父id
     * @param move         移动节点
     * @param over         停留节点
     * @author hzc
     * @createDate 2016-1-6
     */
    private void sortMoveNewForBeforeC(int overParentId, T move, T over) {
        setParentId(move, overParentId);
        Integer overSort = getSort(over);
        Integer moveSort = getSort(move);
        List<T> overItemDicts = getEntitiesByParent(overParentId);
        for (int i = 0; i < overItemDicts.size(); i++) {
            T entity = overItemDicts.get(i);
            Integer sort = getSort(entity);
            if (sort < overSort && sort > moveSort) {
                setSort(entity, sort - 1);
                updateEntity(entity);
            }
        }
        setSort(move, overSort - 1);
        updateEntity(move);
    }

    /**
     * 排序新的树顺序
     * <pre>
     *     如果 moveSort > overSort ，执行 moveSort = overSort , overSort =< sort < moveSort 的都 sort + 1。
     * </pre>
     *
     * @param overParentId 停留节点的父id
     * @param move         移动节点
     * @param over         停留节点
     * @author hzc
     * @createDate 2016-1-6
     */
    private void sortMoveNewForBeforeCPlus(int overParentId, T move, T over) {
        setParentId(move, overParentId);
        Integer overSort = getSort(over);
        Integer moveSort = getSort(move);
        List<T> overItemDicts = getEntitiesByParent(overParentId);
        for (int i = 0; i < overItemDicts.size(); i++) {
            T entity = overItemDicts.get(i);
            Integer sort = getSort(entity);
            if (sort >= overSort && sort < moveSort) {
                setSort(entity, sort + 1);
                updateEntity(entity);
            }
        }
        setSort(move, overSort);
        updateEntity(move);
    }

    /**
     * 排序新的树顺序
     * <pre>
     *     moveParentId = overParentId ，moveSort = overSort ，over 及以后的都 sort + 1
     * </pre>
     *
     * @param overParentId 停留节点的父id
     * @param move         移动节点
     * @param over         停留节点
     * @author hzc
     * @createDate 2016-1-6
     */
    private void sortMoveNewForBeforeB(int overParentId, T move, T over) {
        setParentId(move, overParentId);
        Integer overSort = getSort(over);
        List<T> overItemDicts = getEntitiesByParent(overParentId);
        for (int i = 0; i < overItemDicts.size(); i++) {
            T entity = overItemDicts.get(i);
            Integer sort = getSort(entity);
            if (sort >= overSort) {
                setSort(entity, i + 2);
                updateEntity(entity);
            }
        }
        setSort(move, overSort);
        updateEntity(move);
    }

    /**
     * 重新排序移动的节点 move 的同级的顺序
     *
     * @param itemDict     移动的节点 pojo
     * @param moveParentId 移动的节点的父id
     * @author hzc
     * @createDate 2016-1-6
     */
    private void sortMoveYuanTongJi(T itemDict, int moveParentId) {
        List<T> moveTongJiS = getEntitiesByParent(moveParentId);
        Integer itemSort = getSort(itemDict);
        for (int i = 0; i < moveTongJiS.size(); i++) {
            T entity = moveTongJiS.get(i);
            Integer sort = getSort(entity);
//                        所有 move 后的 sort 都 -1
            if (sort > itemSort) {
                setSort(entity, i);
                updateEntity(entity);
            }
        }
    }
}
