package com.city.app.favorite.controller;

import com.city.app.dataDict.entity.AppDataDictMenu;
import com.city.app.dataDict.service.AppDataDictService;
import com.city.app.favorite.dao.AppFavoriteDao;
import com.city.app.favorite.entity.AppPush;
import com.city.app.favorite.service.AppPushService;
import com.city.common.controller.BaseController;
import com.city.common.util.ListUtil;
import com.city.common.util.StringUtil;
import com.city.support.sys.user.entity.User;
import com.city.support.sys.user.pojo.CurrentUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by wxl on 2016/4/16.
 */
@Controller
@RequestMapping("/app/favorite/push")
public class AppPushController extends BaseController {
    @Autowired
    private AppDataDictService appDataDictService;
    @Autowired
    private AppFavoriteDao appFavoriteDao;
    @Autowired
    private AppPushService appPushService;


    @RequestMapping("/pushManageJsp")
    public String pushManageJsp() {
        return "app/favorite/pushManage";
    }

    /**
     * 查询所有下级
     */
    @RequestMapping("/queryDownDictMenus")
    @ResponseBody
    public List<AppDataDictMenu> queryDownDictMenus(Integer menuId, String name, Integer status) {
        List<Integer> menuIds = appDataDictService.queryDownMenus(menuId, null);
        return appFavoriteDao.queryExistContentMenus(ListUtil.getArrStr(menuIds), name, status);
    }

    /**
     * 查询
     */
    @RequestMapping("/getAllPushByReceivers")
    @ResponseBody
    public List getAllPushByReceivers(String receivers, String name, HttpServletRequest request) {
        return appPushService.queryByFlag(receivers, name, request);
    }

    /**
     * 根据用户获取 未读的数量
     *
     * @param receiver 手机端 用户id
     */
    @RequestMapping("/hasPush")
    @ResponseBody
    public boolean hasPush(Integer receiver) {
        return appPushService.hasPush(receiver);
    }

    /**
     * 查询用户的推送
     */
    @RequestMapping("/queryByFlag")
    @ResponseBody
    public List<Map<String, Object>> queryByFlag(Integer receiver, HttpServletRequest request) {
        return appPushService.queryByFlag(receiver.toString(), null, request);
    }

    /**
     * 设为已读
     */
    @RequestMapping("/setReaded")
    @ResponseBody
    public Map<String, Object> setReaded(Integer receiver, Integer menuId) {
        Map<String, Object> result;
        try {
            appPushService.setReaded(receiver, menuId);
            result = genSuccessMsg("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            result = genFaultMsg("保存失败,服务端运行异常!");
        }
        return result;
    }

    /**
     * 删除
     */
    @RequestMapping("/deleteAppPush")
    @ResponseBody
    public Map<String, Object> deleteAppPush(String ids) {
        Map<String, Object> result = null;
        try {
            appPushService.delete(ids);
            result = genSuccessMsg(null, "删除成功", 200);
        } catch (Exception e) {
            e.printStackTrace();
            result = genFaultMsg(null, "删除失败", 500);
        }
        return result;

    }

    /**
     * 发送推送保存
     */
    @RequestMapping("/saveAppPushes")
    @ResponseBody
    public Map<String, Object> saveAppPushes(String appPushs_str, HttpServletRequest request) {
        Date date = new Date();
        User user = CurrentUser.getCurrentUser(request).getUser();
        List<AppPush> appPushs = new ArrayList<>();
        if (StringUtil.trimNotEmpty(appPushs_str)) {
            appPushs = new Gson().fromJson(appPushs_str, new TypeToken<List<AppPush>>() {
            }.getType());
        }
        for (AppPush appPush : appPushs) {
            appPush.setUserId(user.getId());
            appPush.setTime(date);
            appPush.setFlag(0);
        }
        Map<String, Object> result = null;
        try {
            appPushService.saveAppPushes(appPushs);
            result = genSuccessMsg("发送成功");
        } catch (Exception e) {
            e.printStackTrace();
            result = genFaultMsg("发送失败,服务端响应异常!");
        }
        return result;
    }
}
