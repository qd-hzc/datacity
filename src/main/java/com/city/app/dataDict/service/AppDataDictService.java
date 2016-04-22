package com.city.app.dataDict.service;

import com.city.app.dataDict.dao.AppDataDictDao;
import com.city.app.dataDict.dao.AppDataDictMenuDao;
import com.city.app.dataDict.entity.AppDataDict;
import com.city.app.dataDict.entity.AppDataDictMenu;
import com.city.common.util.ConvertUtil;
import com.city.common.util.ListUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wxl on 2016/3/23.
 */
@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
public class AppDataDictService {
    @Autowired
    private AppDataDictDao appDataDictDao;
    @Autowired
    private AppDataDictMenuDao appDataDictMenuDao;

    /**
     * 查询数据字典
     *
     * @param name    名
     * @param menuIds 目录
     */
    public List<AppDataDict> queryDicts(String name, List<Integer> menuIds, Integer status) {
        return appDataDictDao.queryDict(ListUtil.getArrStr(menuIds), name, status);
    }

    /**
     * 获取所有下级(包含本级)
     *
     * @param menuId  目录
     * @param roleIds 角色
     * @return
     */
    public List<Integer> queryDownMenus(Integer menuId, String roleIds) {
        List<Integer> menuIds = null;
        if (menuId != null) {
            menuIds = new ArrayList<>();
            menuIds.add(menuId);
            List<AppDataDictMenu> menus = appDataDictMenuDao.queryDictMenu(menuId, roleIds, null, null);
            if (ListUtil.notEmpty(menus)) {
                for (AppDataDictMenu menu : menus) {
                    menuIds.addAll(queryDownMenus(menu.getId(), roleIds));
                }
            }
        }
        return menuIds;
    }

    /**
     * 根据分组名划分
     *
     * @param menuId 目录id
     */
    public Map<String, List<AppDataDict>> queryDictsForGroup(Integer menuId) {
        List<AppDataDict> dicts = appDataDictDao.queryDict(menuId.toString(), null, 1);//所有menuId下显示的内容
        //根据分组分
        Map<String, List<AppDataDict>> result = null;
        if (ListUtil.notEmpty(dicts)) {
            result = new HashMap<>();
            for (AppDataDict dict : dicts) {
                String groupName = dict.getGroupName();
                List<AppDataDict> groupDicts = result.get(groupName);
                if (!ListUtil.notEmpty(groupDicts)) {
                    groupDicts = new ArrayList<>();
                    result.put(groupName, groupDicts);
                }
                groupDicts.add(dict);
            }
        }
        return result;
    }

    /**
     * 保存数据字典:单个,用于添加和修改
     *
     * @param dict 数据字典
     */
    public void saveDict(AppDataDict dict) {
        appDataDictDao.saveOrUpdate(dict, false);
    }

    /**
     * 删除
     */
    public void deleteDicts(List<AppDataDict> dicts) {
        if (ListUtil.notEmpty(dicts)) {
            for (AppDataDict dict : dicts) {
                appDataDictDao.delete(dict, true);
            }
        }
    }

    /**
     * 保存数据字典:多个,用于排序
     *
     * @param dicts
     */
    public void saveDict(List<AppDataDict> dicts) {
        if (ListUtil.notEmpty(dicts)) {
            ConvertUtil<AppDataDict> util = new ConvertUtil<>();
            for (AppDataDict dict : dicts) {
                if (dict.getId() != null) {
                    AppDataDict appDataDict = appDataDictDao.loadById(dict.getId());
                    util.apply(appDataDict, dict, AppDataDict.class);
                    appDataDictDao.update(appDataDict, true);
                } else {
                    appDataDictDao.insert(dict, true);
                }
            }
        }
    }
}
