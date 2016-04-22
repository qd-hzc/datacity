package com.city.support.manage.item.service;

import com.city.common.event.EsiEvent;
import com.city.common.event.listener.EsiListenerAdapter;
import com.city.common.event.watcher.DepWatched;
import com.city.common.event.watcher.EsiEventWatched;
import com.city.common.pojo.Page;
import com.city.support.manage.item.dao.ItemCaliberDao;
import com.city.support.manage.item.dao.ItemDao;
import com.city.support.manage.item.dao.ItemGroupInfoDao;
import com.city.support.manage.item.dao.ItemInfoDao;
import com.city.support.manage.item.entity.Item;
import com.city.support.manage.item.entity.ItemCaliber;
import com.city.support.manage.item.entity.ItemInfo;
import com.city.support.manage.timeFrame.entity.TimeFrame;
import com.city.support.manage.unit.entity.DataType;
import com.city.support.manage.unit.entity.Unit;
import com.city.support.sys.user.entity.Department;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/12/28 0028.
 */
@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
public class ItemService {
    @Autowired
    private ItemDao itemDao;
    @Autowired
    private ItemCaliberDao itemCaliberDao;
    @Autowired
    private ItemInfoDao itemInfoDao;
    @Autowired
    private ItemGroupInfoDao itemGroupInfoDao;

    private EsiEventWatched esiEventWatched;

    @Autowired
    public ItemService(DepWatched depWatched) {
        this.esiEventWatched = depWatched;
        esiEventWatched.addListener(new EsiListenerAdapter() {
            @Override
            public boolean handlerEvent(EsiEvent eEvent) {
                //删除前
                if(DepWatched.BEFOREDELETE.equals(eEvent.getEventName())){
                    //清空指标的默认部门
                    itemDao.clearDepId((String) eEvent.getArgs().get(DepWatched.PARAM_DEPIDS));
                }
                return true;
            }
        }, null);
    }

    /**
     * 保存指标
     *
     * @param item
     * @param depId
     */
    public void saveItem(Item item, Integer depId) {
        //设置指标
        Department dep = new Department();
        dep.setId(depId);
        item.setDepartment(dep);
        //设置顺序
        if (item.getSortIndex() == null) {
            List sorts = itemDao.getMaxSort();
            item.setSortIndex(getIndex(sorts));
        }
        if (item.getId() != null) {
            Item i = itemDao.loadById(item.getId());
            //指标对应的口径
            item.setItemCalibers(i.getItemCalibers());
            item.setItemInfos(i.getItemInfos());
            itemDao.getSession().merge(item);
        } else {
            itemDao.insert(item, false);
        }
    }

    /**
     * 查询指标
     */
    public Page getItemsForPage(String name, Integer status, Page page) {
        List<Item> items = itemDao.getItemsForPage(name, status, page);
        page.setDatas(items);
        if (items != null) {
            page.setTotal(itemDao.getItemsForPage(name, status, null).size());
        }
        return page;
    }
    /**
     * 根据id查询指标
     */
    public Item getItemById(Integer id) {
        return itemDao.queryById(id);
    }
    /**
     * 修改指标状态
     */
    public void setItemStatus(String ids, Integer status) {
        itemDao.setStatus(ids, status);
        itemGroupInfoDao.setStatus(ids, status);
    }

    /**
     * 删除指标
     *
     * @param itemStrs
     */
    public void removeItems(String itemStrs) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<Item>>() {
        }.getType();
        List<Item> items = gson.fromJson(itemStrs, type);
        if (items != null && items.size() > 0) {
            for (Item item : items) {
                String itemIds = item.getId().toString();
                //删除指标分组内容
                itemGroupInfoDao.removeItemGroupInfosByItemIds(itemIds);
                //删除指标下的口径
                removeCalibers(item.getItemCalibers());
                //删除指标下的指标信息
                itemInfoDao.removeItemInfosByItemIds(itemIds);
                //删除指标
                itemDao.delete(item, true);
            }
        }
    }

    /**
     * 删除
     */
    public void deleteItem(Item item) {
        itemDao.delete(item, true);
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
     * 保存口径
     *
     * @param itemCaliber
     */
    public void saveCaliber(ItemCaliber itemCaliber) {
        if (itemCaliber.getSortIndex() == null) {
            List sorts = itemCaliberDao.getMaxSort(itemCaliber.getItemId());
            itemCaliber.setSortIndex(getIndex(sorts));
        }
        itemCaliberDao.saveOrUpdate(itemCaliber, false);
    }

    /**
     * 根据指标获取口径
     *
     * @param itemId
     * @return
     */
    public List<ItemCaliber> queryCalibersByItem(Integer itemId) {
        if (itemId == null) {
            return new ArrayList<>();
        }
        return itemCaliberDao.queryCalibersByItem(itemId);
    }

    /**
     * 删除口径
     *
     * @param caliberStrs
     */
    public void removeCalibers(String caliberStrs) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<ItemCaliber>>() {
        }.getType();
        List<ItemCaliber> calibers = gson.fromJson(caliberStrs, type);
        removeCalibers(calibers);
    }

    /**
     * 删除口径
     *
     * @param calibers
     */
    public void removeCalibers(List<ItemCaliber> calibers) {
        if (calibers != null && calibers.size() > 0) {
            for (ItemCaliber caliber : calibers) {
                itemCaliberDao.delete(caliber, true);
                //清除对应指标的口径
                String caliberIds = caliber.getId().toString();
                itemDao.clearCaliber(caliberIds);
                itemGroupInfoDao.clearCaliber(caliberIds);
            }
        }
    }

    /**
     * 保存口径顺序
     */
    public void saveCaliberSorts(String datas) {
        String[] calibers = datas.split(",");
        for (String caliber : calibers) {
            String[] c = caliber.split(":");
            itemCaliberDao.saveSorts(Integer.parseInt(c[0]), Integer.parseInt(c[1]));
        }
    }

    /**
     * 根据指标获取
     *
     * @param itemId
     * @return
     */
    public List<ItemInfo> getItemInfosByItem(Integer itemId) {
        return itemInfoDao.getItemInfosByItem(itemId);
    }

    /**
     * 保存指标信息
     *
     * @param itemInfo
     */
    public void saveItemInfo(ItemInfo itemInfo, Integer unitId, Integer timeFrameId, Integer dataTypeId) {
        //设置单位
        Unit unit = new Unit();
        unit.setId(unitId);
        itemInfo.setUnit(unit);
        //设置时间框架
        TimeFrame tf = new TimeFrame();
        tf.setId(timeFrameId);
        itemInfo.setTimeFrame(tf);
        //设置数据类型
        DataType dataType = new DataType();
        dataType.setId(dataTypeId);
        itemInfo.setDataType(dataType);
        //保存
        itemInfoDao.saveOrUpdate(itemInfo, true);
    }

    /**
     * 删除指标信息
     */
    public void removeItemInfos(String ids) {
        itemInfoDao.removeItemInfos(ids);
    }

    public List<Item> queryAll(){
        return itemDao.queryAll();
    }
}
