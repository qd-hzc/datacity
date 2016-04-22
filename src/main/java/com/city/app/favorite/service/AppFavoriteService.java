package com.city.app.favorite.service;

import com.city.app.dataDict.entity.AppDataDictMenu;
import com.city.app.favorite.dao.AppFavoriteDao;
import com.city.app.favorite.entity.AppFavorite;
import com.city.app.staffValid.dao.AppPersonDao;
import com.city.app.staffValid.entity.AppPerson;
import com.city.common.util.ListUtil;
import com.city.common.util.StringUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by wxl on 2016/4/8.
 */
@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
public class AppFavoriteService {
    @Autowired
    private AppFavoriteDao appFavoriteDao;
    @Autowired
    private AppPersonDao appPersonDao;

    /**
     * 推送
     *
     * @param userId    用户id
     * @param receivers 接收人
     * @param menuId    目录id
     * @param rootId    模块对应的menuId
     * @param year      内容时间:年;若内容不需要时间,可不传
     * @param period    内容时间:月;若内容不需要时间,可不传
     */
    public void push(Integer userId, String receivers, Integer menuId, Integer rootId, Integer year, Integer period) {
        if (StringUtil.trimNotEmpty(receivers)) {
            String[] receiverIds = receivers.trim().split(",");
            for (String receiverId : receiverIds) {
                save(userId, Integer.parseInt(receiverId.trim()), menuId, rootId, year, period, AppFavorite.TYPE_PUSH);
            }
        }
    }

    /**
     * 订阅
     *
     * @param userId 用户id
     * @param menuId 目录id
     * @param rootId 模块对应的menuId
     * @param year   内容时间:年;若内容不需要时间,可不传
     * @param period 内容时间:月;若内容不需要时间,可不传
     */
    public void describe(Integer userId, Integer menuId, Integer rootId, Integer year, Integer period) {
        save(userId, userId, menuId, rootId, year, period, AppFavorite.TYPE_DESCRIBE);
    }

    /**
     * 根据id取消订阅或推送
     */
    public void cancelOrder(String ids) {
        appFavoriteDao.delete(ids);
    }

    /**
     * 取消订阅
     */
    public void cancelDescribe(Integer userId, Integer menuId, Integer year, Integer period) {
        appFavoriteDao.cancelOrder(menuId, year, period, userId, null, AppFavorite.TYPE_DESCRIBE);
    }

    /**
     * 取消推送给我
     */
    public void cancelPushToMe(Integer userId, Integer menuId, Integer year, Integer period) {
        appFavoriteDao.cancelOrder(menuId, year, period, null, userId, AppFavorite.TYPE_PUSH);
    }

    /**
     * 取消我推送给别人
     */
    public void cancelMyPush(Integer userId, Integer menuId, Integer year, Integer period) {
        appFavoriteDao.cancelOrder(menuId, year, period, userId, null, AppFavorite.TYPE_PUSH);
    }


    //保存
    private void save(Integer sender, Integer receiver, Integer menuId, Integer rootId, Integer year, Integer period, Integer orderType) {
        List<AppFavorite> appFavorites = appFavoriteDao.queryFavorites(menuId, year, period, sender, receiver, orderType);
        if (ListUtil.notEmpty(appFavorites)) {//已存在,更新时间
            AppFavorite appFavorite = appFavorites.get(0);
            appFavorite.setRootId(rootId);
            appFavorite.setOrderDate(new Date());
            appFavoriteDao.update(appFavorite, false);
        } else {
            AppFavorite appFavorite = new AppFavorite();
            appFavorite.setMenuId(menuId);
            appFavorite.setRootId(rootId);
            appFavorite.setOrderType(orderType);
            appFavorite.setOrderDate(new Date());
            appFavorite.setYear(year);
            appFavorite.setPeriod(period);
            appFavorite.setSender(sender);
            appFavorite.setReceiver(receiver);
            appFavoriteDao.insert(appFavorite, false);
        }
    }

    //是否已被订阅
    public boolean isDescribed(Integer userId, Integer menuId, Integer year, Integer period) {
        return ListUtil.notEmpty(appFavoriteDao.queryFavorites(menuId, year, period, userId, userId, AppFavorite.TYPE_DESCRIBE));
    }

    /**
     * 查询
     *
     * @param menuId    目录id
     * @param year      内容:年
     * @param period    内容:月
     * @param sender    推送(订阅)人
     * @param receiver  接收人
     * @param orderType 类型,订阅或推送
     */
    public List<AppFavorite> queryFavorites(Integer menuId, Integer year, Integer period, Integer sender, Integer receiver, Integer orderType) {
        return appFavoriteDao.queryFavorites(menuId, year, period, sender, receiver, orderType);
    }

    /**
     * 重新封装列表
     */
    public List<Map<String, Object>> packageList(List<AppFavorite> appFavorites) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (ListUtil.notEmpty(appFavorites)) {
            Set<Integer> menuIds = new HashSet<>();//目录id
            Set<Integer> personIds = new HashSet<>();//手机端用户
            for (AppFavorite appFavorite : appFavorites) {
                menuIds.add(appFavorite.getMenuId());
                menuIds.add(appFavorite.getRootId());

                personIds.add(appFavorite.getSender());
                personIds.add(appFavorite.getReceiver());
            }
            //查询目录
            List<AppDataDictMenu> menus = appFavoriteDao.queryMenus(ListUtil.getArrStr(menuIds));
            //查询人员
            List<AppPerson> appPersons = appPersonDao.queryPersons(ListUtil.getArrStr(personIds));
            //重新组装数据
            for (AppFavorite appFavorite : appFavorites) {
                Map<String, Object> fav = packageBean(appFavorite, menus, appPersons);
                if (fav == null)
                    continue;
                result.add(fav);
            }
        }
        return result;
    }

    /**
     * 封装单个对象
     *
     * @param appFavorite 订阅或推送内容
     * @param menus       所有相关目录
     * @param appPersons  手机端用户
     */
    private Map<String, Object> packageBean(AppFavorite appFavorite, List<AppDataDictMenu> menus, List<AppPerson> appPersons) {
        Map<String, Object> fav = null;
        try {
            //本身名字
            AppDataDictMenu curMenu = getMenuName(menus, appFavorite.getMenuId());
            if (curMenu == null) {
                return fav;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
            fav = BeanUtils.describe(appFavorite);
            fav.put("menuName", curMenu.getName());
            //根节点名字
            AppDataDictMenu rootMenu = getMenuName(menus, appFavorite.getRootId());
            fav.put("rootName", rootMenu != null ? rootMenu.getName() : "");
            //父节点名字
            AppDataDictMenu parentMenu = getMenuName(menus, curMenu.getParentId());
            fav.put("parentName", parentMenu != null ? parentMenu.getName() : "");
            //时间
            fav.put("orderDate", sdf.format(appFavorite.getOrderDate()));
            //设置名字
            fav.put("senderName", getPersonName(appPersons, appFavorite.getSender()));
            fav.put("receiverName", getPersonName(appPersons, appFavorite.getReceiver()));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return fav;
    }

    /**
     * 获取目录信息
     */
    private AppDataDictMenu getMenuName(List<AppDataDictMenu> menus, Integer menuId) {
        if (menuId != null && ListUtil.notEmpty(menus)) {
            for (AppDataDictMenu menu : menus) {
                if (menu.getId().equals(menuId)) {
                    return menu;
                }
            }
        }
        return null;
    }

    /**
     * 获取人员名字
     */
    private String getPersonName(List<AppPerson> appPersons, Integer personId) {
        if (personId != null && ListUtil.notEmpty(appPersons)) {
            if (personId == 0) {
                return "系统管理员";
            }
            for (AppPerson person : appPersons) {
                if (person.getId().equals(personId)) {
                    return person.getName();
                }
            }
        }
        return "";
    }

    public List<AppFavorite> getAllPushByReceivers(String receivers, int typePush) {

        String[] receiveArr = null;
        List<AppFavorite> tmpAppFavoriteList = null;
        List<AppFavorite> result = new ArrayList<>();
        if (receivers != null)
            receiveArr = receivers.split(",");
        if (receiveArr != null) {
            for (String receiveId : receiveArr) {
                tmpAppFavoriteList = appFavoriteDao.queryFavorites(null, null, null, null, Integer.parseInt(receiveId), AppFavorite.TYPE_PUSH);
                result.addAll(tmpAppFavoriteList);
            }
        }
        return result;
    }
}
