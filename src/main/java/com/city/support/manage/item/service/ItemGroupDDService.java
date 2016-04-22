package com.city.support.manage.item.service;

import com.city.common.pojo.DragDropPojo;
import com.city.common.util.tree.TreeDDSort;
import com.city.support.manage.item.dao.ItemGroupDao;
import com.city.support.manage.item.entity.ItemGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Administrator on 2016/1/6 0006.
 * 拖拽排序
 */
@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
public class ItemGroupDDService extends TreeDDSort<ItemGroup> {
    @Autowired
    private ItemGroupDao itemGroupDao;

    @Override
    public void saveDragAndDrop(DragDropPojo dragDropPojo) {
        super.saveDragAndDrop(dragDropPojo);
    }

    @Override
    protected int getSort(ItemGroup itemGroup) {
        return itemGroup.getSortIndex();
    }

    @Override
    protected void setSort(ItemGroup itemGroup, int sort) {
        itemGroup.setSortIndex(sort);
    }

    @Override
    protected void setParentId(ItemGroup itemGroup, int parentId) {
        itemGroup.setParentId(parentId);
    }

    @Override
    protected ItemGroup getEntityById(int id) {
        return itemGroupDao.loadById(id);
    }

    @Override
    protected List<ItemGroup> getEntitiesByParent(int id) {
        return itemGroupDao.getDownGroups(id);
    }

    @Override
    protected void updateEntity(ItemGroup itemGroup) {
        itemGroupDao.update(itemGroup, true);
    }

    @Override
    protected int getEntityKey(ItemGroup itemGroup) {
        return itemGroup.getId();
    }
}
