package com.city.support.manage.itemdict.service;

import com.city.common.pojo.Constant;
import com.city.common.util.ConvertUtil;
import com.city.common.util.ListUtil;
import com.city.common.util.tree.PackageListToTree;
import com.city.support.manage.itemdict.dao.ItemDictDao;
import com.city.support.manage.itemdict.entity.SptMgrItemDictEntity;
import com.city.support.manage.pojo.DragAndDropVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 指标分组目录类
 * Created by HZC on 2015/12/31.
 */
@Service
@Transactional
public class ItemDictService extends PackageListToTree<SptMgrItemDictEntity> {

    @Autowired
    private ItemDictDao itemDictDao;

    /**
     * 返回true或者false
     * <pre>
     *      保存或者更新指标分组目录，成功返回true，保存失败返回false
     * </pre>
     *
     * @param entity
     * @author hzc
     * @createDate 2015-12-30
     */
    public boolean saveOrUpdate(SptMgrItemDictEntity entity) {
        Integer parentId = entity.getParentId();
        List<SptMgrItemDictEntity> list = getItemDictByParent(parentId);
        String name = entity.getName();
        List<SptMgrItemDictEntity> newList = new LinkedList<>();
        for (SptMgrItemDictEntity lst : list) {
            if (lst.getName().equals(name)) {//查询是否有重复名称
                newList.add(lst);
            }
        }
        if (newList.size() == 0) {
            Integer itemDictId = entity.getId();
            //保存之前填充entity中为空的字段与数据库相同
            if (itemDictId > 0) {
                SptMgrItemDictEntity itemDictById = getItemDictById(itemDictId);
                ConvertUtil<SptMgrItemDictEntity> convertUtil = new ConvertUtil<SptMgrItemDictEntity>();
                convertUtil.replication(entity, itemDictById, SptMgrItemDictEntity.class.getName());
                itemDictDao.saveOrUpdate(itemDictById, Boolean.FALSE);
            } else {
                itemDictDao.saveOrUpdate(entity, Boolean.FALSE);
            }
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }

    }


    /**
     * 返回指标分组目录集合
     * <pre>
     *     根据指标分组目录id查询该指标分组目录的所有下级指标分组目录
     * </pre>
     *
     * @param itemDictId
     * @return
     * @author hzc
     * @createDate 2015-12-31
     */
    public List<SptMgrItemDictEntity> getItemDictByParent(Integer itemDictId) {
        List<SptMgrItemDictEntity> itemDictEntities = itemDictDao.getSptMgrItemDictEntities(itemDictId);
        return itemDictEntities;
    }


    /**
     * 返回指标分组目录
     * <pre>
     *     根据id返回指标分组目录
     * </pre>
     *
     * @param itemDictId
     * @return
     * @author hzc
     * @createDate 2015-12-31
     */
    public SptMgrItemDictEntity getItemDictById(Integer itemDictId) {
        return itemDictDao.queryById(itemDictId);
    }

    /**
     * 返回保存指标分组目录结果
     * <pre>
     *     保存成功返回true，保存失败返回false
     * </pre>
     *
     * @param entity
     * @return
     */
    public boolean saveItemDict(SptMgrItemDictEntity entity) {
        String name = entity.getName();
//        List<SptMgrItemDictEntity> dicts = itemDictDao.getItemDictByName(name);
        Integer parentId = entity.getParentId();
        List sorts = itemDictDao.getMaxSort(parentId);
        entity.setSort(getIndex(sorts));
//        if (null == dicts || dicts.size() < 1) {
        //保存的指标分组目录有父，则获取所有同级，并添加该指标分组目录排序，同时添加指标分组目录等级
        SptMgrItemDictEntity itemDictParent = getItemDictById(parentId);
        if (null != itemDictParent) {
            itemDictParent.setLeaf(false);
            itemDictDao.saveOrUpdate(itemDictParent, Boolean.FALSE);
        }
        itemDictDao.saveOrUpdate(entity, Boolean.FALSE);
        return Boolean.TRUE;
//        } else {
//            return Boolean.FALSE;
//        }
    }

    /**
     * 根据查询的List 获取顺序
     *
     * @param sorts
     * @return
     */
    private int getIndex(List sorts) {
        int s = 1;
        if (sorts != null && sorts.size() > 0) {
            Integer sort = (Integer) sorts.get(0);
            if (sort != null) {
                s += sort;
            }
        }
        return s;
    }

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
        SptMgrItemDictEntity move = getItemDictById(moveId);
        SptMgrItemDictEntity over = getItemDictById(overId);
        switch (dragAndDrop.getDropPosition()) {
            case "before":
                if (!moveParentIdS.equals(overParentIdS)) {
//                    查询 move 原同级
                    sortMoveYuanTongJi(move, moveParentId);
//                    新的 sort 排序
                    sortMoveNewForBeforeB(overParentId, move, over);
                } else {
                    if (move.getSort() > over.getSort()) {
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
                    if (move.getSort() > over.getSort()) {

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
    private void sortMoveNewForAppend(int overId, SptMgrItemDictEntity move) {
        move.setParentId(overId);
        List<SptMgrItemDictEntity> itemDictByParent = getItemDictByParent(overId);
        move.setSort(itemDictByParent.size() + 1);
        itemDictDao.update(move, false);
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
    private void sortMoveNewForAfterC(int overParentId, SptMgrItemDictEntity move, SptMgrItemDictEntity over) {
        move.setParentId(overParentId);
        Integer overSort = over.getSort();
        Integer moveSort = move.getSort();
        List<SptMgrItemDictEntity> overItemDicts = getItemDictByParent(overParentId);
        for (int i = 0; i < overItemDicts.size(); i++) {
            SptMgrItemDictEntity entity = overItemDicts.get(i);
            Integer sort = entity.getSort();
            if (sort <= overSort && sort > moveSort) {
                entity.setSort(sort - 1);
                itemDictDao.update(entity, false);
            }
        }
        move.setSort(overSort);
        itemDictDao.update(move, false);
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
    private void sortMoveNewForAfterCPlus(int overParentId, SptMgrItemDictEntity move, SptMgrItemDictEntity over) {
        move.setParentId(overParentId);
        Integer overSort = over.getSort();
        Integer moveSort = move.getSort();
        List<SptMgrItemDictEntity> overItemDicts = getItemDictByParent(overParentId);
        for (int i = 0; i < overItemDicts.size(); i++) {
            SptMgrItemDictEntity entity = overItemDicts.get(i);
            Integer sort = entity.getSort();
            if (sort > overSort && sort < moveSort) {
                entity.setSort(sort + 1);
                itemDictDao.update(entity, false);
            }
        }
        move.setSort(overSort + 1);
        itemDictDao.update(move, false);
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
    private void sortMoveNewForAfterB(int overParentId, SptMgrItemDictEntity move, SptMgrItemDictEntity over) {
        move.setParentId(overParentId);
        Integer overSort = over.getSort();
        List<SptMgrItemDictEntity> overItemDicts = getItemDictByParent(overParentId);
        for (int i = 0; i < overItemDicts.size(); i++) {
            SptMgrItemDictEntity entity = overItemDicts.get(i);
            Integer sort = entity.getSort();
            if (sort > overSort) {
                entity.setSort(sort + 1);
                itemDictDao.update(entity, false);
            }
        }
        move.setSort(overSort + 1);
        itemDictDao.update(move, false);
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
    private void sortMoveNewForBeforeC(int overParentId, SptMgrItemDictEntity move, SptMgrItemDictEntity over) {
        move.setParentId(overParentId);
        Integer overSort = over.getSort();
        Integer moveSort = move.getSort();
        List<SptMgrItemDictEntity> overItemDicts = getItemDictByParent(overParentId);
        for (int i = 0; i < overItemDicts.size(); i++) {
            SptMgrItemDictEntity entity = overItemDicts.get(i);
            Integer sort = entity.getSort();
            if (sort < overSort && sort > moveSort) {
                entity.setSort(sort - 1);
                itemDictDao.update(entity, false);
            }
        }
        move.setSort(overSort - 1);
        itemDictDao.update(move, false);
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
    private void sortMoveNewForBeforeCPlus(int overParentId, SptMgrItemDictEntity move, SptMgrItemDictEntity over) {
        move.setParentId(overParentId);
        Integer overSort = over.getSort();
        Integer moveSort = move.getSort();
        List<SptMgrItemDictEntity> overItemDicts = getItemDictByParent(overParentId);
        for (int i = 0; i < overItemDicts.size(); i++) {
            SptMgrItemDictEntity entity = overItemDicts.get(i);
            Integer sort = entity.getSort();
            if (sort >= overSort && sort < moveSort) {
                entity.setSort(sort + 1);
                itemDictDao.update(entity, false);
            }
        }
        move.setSort(overSort);
        itemDictDao.update(move, false);
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
    private void sortMoveNewForBeforeB(int overParentId, SptMgrItemDictEntity move, SptMgrItemDictEntity over) {
        move.setParentId(overParentId);
        Integer overSort = over.getSort();
        List<SptMgrItemDictEntity> overItemDicts = getItemDictByParent(overParentId);
        for (int i = 0; i < overItemDicts.size(); i++) {
            SptMgrItemDictEntity entity = overItemDicts.get(i);
            Integer sort = entity.getSort();
            if (sort >= overSort) {
                entity.setSort(i + 2);
                itemDictDao.update(entity, false);
            }
        }
        move.setSort(overSort);
        itemDictDao.update(move, false);
    }

    /**
     * 重新排序移动的节点 move 的同级的顺序
     *
     * @param itemDict     移动的节点 pojo
     * @param moveParentId 移动的节点的父id
     * @author hzc
     * @createDate 2016-1-6
     */
    private void sortMoveYuanTongJi(SptMgrItemDictEntity itemDict, int moveParentId) {
        List<SptMgrItemDictEntity> moveTongJiS = getItemDictByParent(moveParentId);
        Integer itemSort = itemDict.getSort();
        for (int i = 0; i < moveTongJiS.size(); i++) {
            SptMgrItemDictEntity entity = moveTongJiS.get(i);
            Integer sort = entity.getSort();
//                        所有 move 后的 sort 都 -1
            if (sort > itemSort) {
                entity.setSort(i);
                itemDictDao.update(entity, false);
            }
        }
    }

    /**
     * 返回所有有效指标分组目录
     *
     * @return
     * @author hzc
     * @createDate 2016-1-15
     */
    public List<SptMgrItemDictEntity> getAll() {
        return itemDictDao.findAll();
    }

    /**
     * 返回所有有效指标分组目录树结构
     *
     * @return
     * @author hzc
     * @createDate 2016-1-15
     */
    public List<Map<String, Object>> getAllTrees() {
        List<SptMgrItemDictEntity> all = getAll();
        return packageListToTree(all, Boolean.FALSE);
    }

    /**
     * 根据主键获取实体类
     *
     * @param entityKey
     */
    @Override
    protected SptMgrItemDictEntity getEntityById(Integer entityKey) {
        return getItemDictById(entityKey);
    }

    /**
     * 获取主键
     *
     * @param sptMgrItemDictEntity
     */
    @Override
    protected Integer getEntityKey(SptMgrItemDictEntity sptMgrItemDictEntity) {
        return sptMgrItemDictEntity.getId();
    }

    /**
     * 获取父id
     *
     * @param sptMgrItemDictEntity
     */
    @Override
    protected Integer getParentId(SptMgrItemDictEntity sptMgrItemDictEntity) {
        return sptMgrItemDictEntity.getParentId();
    }

    /**
     * 获取名称
     *
     * @param sptMgrItemDictEntity
     */
    @Override
    protected String getEntityName(SptMgrItemDictEntity sptMgrItemDictEntity) {
        return sptMgrItemDictEntity.getName();
    }

    @Override
    public Map<String, Object> getEntityMap(SptMgrItemDictEntity t) {
        Map<String, Object> depMap = new HashMap<String, Object>();
        depMap.put("id", getEntityKey(t));
        depMap.put("parentId", getParentId(t));
        depMap.put("text", getEntityName(t));
        depMap.put("leaf", true);
        depMap.put("type", Constant.MetadataType.ITEM_MENU);
        return depMap;
    }

    /**
     * 删除指标分组目录
     * <pre>
     *     包括子
     * </pre>
     *
     * @param i id
     * @author hzc
     * @createDate 2016-2-16
     */
    public void deleteItemDicts(int i) throws Exception {
        List<SptMgrItemDictEntity> list = new LinkedList<>();
        getItemDictTree(i, list);
        SptMgrItemDictEntity itemDictById = getItemDictById(i);
        list.add(itemDictById);
        delectItemDictByIds(list);
    }

    /**
     * 返回指标分组目录
     * <pre>
     *     根据id，获取该id所有子的集合
     * </pre>
     *
     * @param id
     * @return
     * @author hzc
     * @createDate 20160-2-16
     */
    private void getItemDictTree(int id, List list) {
        List<SptMgrItemDictEntity> itemDictByParent = getItemDictByParent(id);
        if (itemDictByParent.size() > 0) {
            for (int i = 0; i < itemDictByParent.size(); i++) {
                SptMgrItemDictEntity sptMgrItemDictEntity = itemDictByParent.get(i);
                list.add(sptMgrItemDictEntity);
                getItemDictTree(sptMgrItemDictEntity.getId(), list);
            }
        }
    }

    /**
     * 删除指标分组目录
     * <pre>
     *     批量删除指标分组目录
     * </pre>
     *
     * @param entities
     * @author hzc
     * @createDate 2016-2-16
     */
    private void delectItemDictByIds(List<SptMgrItemDictEntity> entities) {
        itemDictDao.deleteByIds(entities);
    }

    /**
     * 保存指标分组目录顺序
     *
     * @param list
     */
    public void saveGroupSorts(List<SptMgrItemDictEntity> list) {
        if (ListUtil.notEmpty(list)) {
            ConvertUtil<SptMgrItemDictEntity> util = new ConvertUtil<>();
            //转换并保存
            for (SptMgrItemDictEntity itemDict : list) {
                if (itemDict.getId() != null) {
                    SptMgrItemDictEntity curGroup = getEntityById(itemDict.getId());
                    //赋值
                    util.apply(curGroup, itemDict, SptMgrItemDictEntity.class);
                    itemDictDao.update(curGroup, false);
                } else {
                    itemDictDao.insert(itemDict, true);
                }
            }
        }
    }
}
