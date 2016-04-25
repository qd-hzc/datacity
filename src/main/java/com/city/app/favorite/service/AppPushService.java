package com.city.app.favorite.service;

import com.city.app.favorite.dao.AppPushDao;
import com.city.app.favorite.entity.AppPush;
import com.city.app.staffValid.dao.AppPersonDao;
import com.city.app.staffValid.entity.AppPerson;
import com.city.common.util.ConvertUtil;
import com.city.common.util.ListUtil;
import com.city.support.sys.user.dao.UserDao;
import com.city.support.sys.user.entity.Department;
import com.city.support.sys.user.entity.User;
import com.city.support.sys.user.pojo.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/4/18.
 */
@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
public class AppPushService {
    @Autowired
    private AppPushDao appPushDao;
    @Autowired
    private AppPersonDao appPersonDao;
    @Autowired
    private UserDao userDao;

    /**
     * 查询
     */
    public List<Map<String, Object>> queryByFlag(String receivers, String name, HttpServletRequest request) {
        return packageList(appPushDao.queryByReceivers(receivers, name), request);
    }


    /**
     * 封装
     */
    private List<Map<String, Object>> packageList(List<AppPush> appPushs, HttpServletRequest request) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (ListUtil.notEmpty(appPushs)) {
            Map<String, Object> map = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (AppPush appPush : appPushs) {
                map = new HashMap<>();
                map.put("id", appPush.getId());
                map.put("name", appPush.getName());
                map.put("menuId", appPush.getMenuId());
                map.put("receiver", appPush.getReceiver());
                map.put("time", sdf.format(appPush.getTime()));
                map.put("flag", appPush.getFlag());
                //部门
                Integer userId = appPush.getUserId();
                User user = userDao.queryById(userId);
                map.put("userId", userId);
                map.put("userName", user.getUserName());
                Department dep = user.getDepartment();//部门
                if (dep != null) {//发送部门
                    map.put("depName", dep.getDepName());
                    map.put("depId", dep.getId());
                }
                //接收人名字
                AppPerson person = appPersonDao.queryById(appPush.getReceiver());
                if (person != null) {
                    map.put("receiverName", person.getName());
                }
                result.add(map);
            }
        }
        return result;
    }

    /**
     * 单个查询
     */
    public AppPush queryOne(String receiver, String name) {
        return appPushDao.queryOne(receiver, name);
    }

    public void delete(String ids) {
        appPushDao.delete(ids);
    }

    /**
     * �������ͱ���
     *
     * @param appPush
     */
    public void saveAppPush(AppPush appPush) {
        appPushDao.saveAppPush(appPush);
    }

    /**
     * �������ͱ���
     *
     * @param appPushs
     */
    public void saveAppPushes(List<AppPush> appPushs) {
        for (AppPush push : appPushs) {
            List<AppPush> alreadyPushes = appPushDao.queryByInfo(push.getMenuId(), push.getReceiver());
            if (ListUtil.notEmpty(alreadyPushes)) {
                AppPush alreadyPush = alreadyPushes.get(0);
                alreadyPush.setTime(push.getTime());
                alreadyPush.setUserId(push.getUserId());
                alreadyPush.setFlag(0);
                appPushDao.update(alreadyPush, false);
            } else {
                appPushDao.insert(push, false);
            }
        }
    }

    /**
     * 查询是否包含未读
     */
    public boolean hasPush(Integer receiver) {
        List<AppPush> appPushs = appPushDao.queryPushByFlag(receiver, 0);
        return ListUtil.notEmpty(appPushs);
    }

    /**
     * 设为已读
     *
     * @param receiver
     * @param menuId
     */
    public void setReaded(Integer receiver, Integer menuId) {
        appPushDao.setReaded(receiver, menuId);
    }

}
