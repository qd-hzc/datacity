package com.city.support.manage.item.service;

import com.city.common.event.EsiEvent;
import com.city.common.event.listener.EsiListenerAdapter;
import com.city.common.event.watcher.DepWatched;
import com.city.common.event.watcher.EsiEventWatched;
import com.city.common.util.tree.PackageListToTree;
import com.city.support.manage.item.dao.ItemDao;
import com.city.support.manage.item.dao.ItemGroupDao;
import com.city.support.manage.item.dao.ItemGroupInfoDao;
import com.city.support.manage.item.entity.Item;
import com.city.support.manage.item.entity.ItemGroup;
import com.city.support.manage.item.entity.ItemGroupInfo;
import com.city.support.sys.user.entity.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by Administrator on 2015/12/29 0029.
 */
@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
public class ItemGroupService extends PackageListToTree<ItemGroup> {
    @Autowired
    private ItemGroupDao itemGroupDao;
    @Autowired
    private ItemDao itemDao;
    @Autowired
    private ItemGroupInfoDao itemGroupInfoDao;

    private EsiEventWatched esiEventWatched;

    @Autowired
    public ItemGroupService(DepWatched depWatched) {
        esiEventWatched = depWatched;
        esiEventWatched.addListener(new EsiListenerAdapter() {
            @Override
            public boolean handlerEvent(EsiEvent eEvent) {
                //删除前
                if (DepWatched.BEFOREDELETE.equals(eEvent.getEventName())) {
                    //清空指标的默认部门
                    itemGroupInfoDao.clearDepId((String) eEvent.getArgs().get(DepWatched.PARAM_DEPIDS));
                }
                return true;
            }
        }, null);
    }

    /**
     * 获取分组
     *
     * @return
     */
    public List<Map<String, Object>> queryGroups(String name, Integer status) {
        boolean isNeedColor = name != null && name.trim().length() > 0;
        List<ItemGroup> groups = itemGroupDao.queryGroups(name, status);
        return packageListToTree(groups, isNeedColor);
    }

    /**
     * 保存分组
     *
     * @param itemGroup
     */
    public void saveGroup(ItemGroup itemGroup) {
        //设置顺序
        if (itemGroup.getSortIndex() == null) {
            List sorts = itemGroupDao.getMaxSort(itemGroup.getParentId());
            itemGroup.setSortIndex(getIndex(sorts));
        }
        //添加分组内容
        if (itemGroup.getId() != null) {
            itemGroup.setGroupInfos(itemGroupInfoDao.getInfosByGroup("", null, itemGroup.getId().toString()));
        }
        //保存
        itemGroupDao.saveOrUpdate(itemGroup, true);
    }

    /**
     * 保存分组内容
     */
    public void saveGroupInfo(ItemGroupInfo itemGroupInfo, Integer itemId, Integer depId) {
        //设置指标
        Item item = new Item();
        item.setId(itemId);
        itemGroupInfo.setItem(item);
        //设置部门
        Department dep = new Department();
        dep.setId(depId);
        itemGroupInfo.setDepartment(dep);
        //设置顺序
        if (itemGroupInfo.getSortIndex() == null) {
            List sorts = itemGroupInfoDao.getMaxSort(itemGroupInfo.getGroupId());
            itemGroupInfo.setSortIndex(getIndex(sorts));
        }
        itemGroupInfoDao.saveOrUpdate(itemGroupInfo, true);
    }

    /**
     * 批量添加分组信息
     *
     * @param itemIds
     */
    public void saveItemGroupInfosBatch(String itemIds, Integer groupId, String groupName) {
        //设置顺序
        List sorts = itemGroupInfoDao.getMaxSort(groupId);
        int index = getIndex(sorts);
        //所选中的指标
        List<Item> items = itemDao.getItemsByIds(itemIds);
        //根据id顺序排列
        String[] ids = itemIds.split(",");
        final List<String> idList = Arrays.asList(ids);
        Collections.sort(items, new Comparator<Item>() {
            @Override
            public int compare(Item o1, Item o2) {
                return idList.indexOf(o1.getId().toString()) - idList.indexOf(o2.getId().toString());
            }
        });
        //根据当前指标查询分组中是否已包含该指标
        List<ItemGroupInfo> datas = itemGroupInfoDao.getInfosByGroupAndItems(itemIds, groupId);
        //保存
        ItemGroupInfo info = null;
        for (Item item : items) {
            if (!isIncludeItem(datas, item.getId())) {
                info = new ItemGroupInfo(item);
                info.setGroupName(groupName);
                info.setGroupId(groupId);
                info.setSortIndex(index++);
                itemGroupInfoDao.insert(info, true);
            }
        }
    }

    //判断是否分组中已包含指标
    private boolean isIncludeItem(List<ItemGroupInfo> datas, Integer itemId) {
        if (datas != null && datas.size() > 0) {
            for (ItemGroupInfo info : datas) {
                if (info.getItem().getId().equals(itemId)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 删除分组
     */
    public void removeGroups(int groupId) {
        //查询所有下级分组并删除
        List<Integer> groups = getDownGroups(groupId);
        String groupStr = groups.toString();
        String ids = groupStr.substring(1, groupStr.length() - 1);
        //删除分组内容
        itemGroupInfoDao.removeItemGroupInfosByGroupIds(ids);
        //删除分组
        itemGroupDao.removeGroups(ids);
    }

    /**
     * 根据分组获取信息
     */
    public List<ItemGroupInfo> getInfosByGroup(String itemName, Integer status, Integer groupId, boolean includeDownLevel) {
        String groups;
        List<Integer> groupList = getDownGroups(groupId);
        if (includeDownLevel) {
            String groupStrs = groupList.toString();
            groups = groupStrs.substring(1, groupStrs.length() - 1);
        } else {
            groups = groupId.toString();
        }
        return itemGroupInfoDao.getInfosByGroup(itemName, status, groups);
    }


    /**
     * 删除分组内容
     *
     * @param ids
     */
    public void removeItemGroupInfos(String ids) {
        itemGroupInfoDao.removeItemGroupInfos(ids);
    }


    /**
     * 保存分组信息排序
     */
    public void saveItemGroupInfoSorts(String datas) {
        String[] infos = datas.split(",");
        for (String info : infos) {
            String[] c = info.split(":");
            itemGroupInfoDao.saveSorts(Integer.parseInt(c[0]), Integer.parseInt(c[1]));
        }
    }

    /**
     * 查询下级分组
     */
    private List<Integer> getDownGroups(int groupId) {
        List<Integer> itemGroups = new ArrayList<>();
        List<ItemGroup> groups = itemGroupDao.getDownGroups(groupId);
        itemGroups.add(groupId);
        if (groups != null && groups.size() > 0) {
            for (ItemGroup group : groups) {
                itemGroups.addAll(getDownGroups(group.getId()));
            }
        }
        return itemGroups;
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

    @Override
    protected ItemGroup getEntityById(Integer entityKey) {
        return itemGroupDao.loadById(entityKey);
    }

    @Override
    protected Integer getEntityKey(ItemGroup itemGroup) {
        return itemGroup.getId();
    }

    @Override
    protected Integer getParentId(ItemGroup itemGroup) {
        return itemGroup.getParentId();
    }

    @Override
    protected String getEntityName(ItemGroup itemGroup) {
        return itemGroup.getName();
    }

    @Override
    public Map<String, Object> getEntityMap(ItemGroup itemGroup) {
        Map<String, Object> entityMap = super.getEntityMap(itemGroup);
        entityMap.put("name", itemGroup.getName());
        entityMap.put("status", itemGroup.getStatus());
        entityMap.put("sortIndex", itemGroup.getSortIndex());
        entityMap.put("comments", itemGroup.getComments());
        return entityMap;
    }
}
