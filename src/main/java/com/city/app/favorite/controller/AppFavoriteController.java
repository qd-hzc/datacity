package com.city.app.favorite.controller;

import com.city.app.favorite.entity.AppFavorite;
import com.city.app.favorite.service.AppFavoriteService;
import com.city.common.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * Created by wxl on 2016/4/8.
 */
@Controller
@RequestMapping("/app/favorite")
public class AppFavoriteController extends BaseController {
    @Autowired
    private AppFavoriteService appFavoriteService;

    /**
     * 获取所有已订阅
     */
    @RequestMapping("/getAllDescriptions")
    @ResponseBody
    public List<Map<String, Object>> getAllDescriptions(Integer userId) {
        List<AppFavorite> appFavorites = appFavoriteService.queryFavorites(null, null, null, userId, userId, AppFavorite.TYPE_DESCRIBE);
        return appFavoriteService.packageList(appFavorites);
    }

    /**
     * 获取所有推送信息
     */
    @RequestMapping("/getAllPush")
    @ResponseBody
    public List<Map<String, Object>> getAllPush(Integer userId) {
        List<AppFavorite> appFavorites = appFavoriteService.queryFavorites(null, null, null, null, userId, AppFavorite.TYPE_PUSH);
        return appFavoriteService.packageList(appFavorites);
    }

    /**
     * 获取所有推送信息
     */
    @RequestMapping("/getAllPushByReceivers")
    @ResponseBody
    public List<Map<String, Object>> getAllPushByReceivers(String receivers) {
        List<AppFavorite> appFavorites = appFavoriteService.getAllPushByReceivers(receivers, AppFavorite.TYPE_PUSH);
        return appFavoriteService.packageList(appFavorites);
    }

    /**
     * 获取所有我推送的
     */
    @RequestMapping("/getMyPush")
    @ResponseBody
    public List<Map<String, Object>> getMyPush(Integer userId) {
        List<AppFavorite> appFavorites = appFavoriteService.queryFavorites(null, null, null, userId, null, AppFavorite.TYPE_PUSH);
        return appFavoriteService.packageList(appFavorites);
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
    @RequestMapping("/describe")
    @ResponseBody
    public Map<String, Object> describe(Integer userId, Integer menuId, Integer rootId, Integer year, Integer period) {
        Map<String, Object> result;
        try {
            appFavoriteService.describe(userId, menuId, rootId, year, period);
            result = genSuccessMsg("订阅成功");
        } catch (Exception e) {
            e.printStackTrace();
            result = genFaultMsg("订阅失败,服务端运行异常!");
        }
        return result;
    }

    /**
     * 推送
     *
     * @param userId 用户id
     * @param menuId 目录id
     * @param rootId 模块对应的menuId
     * @param year   内容时间:年;若内容不需要时间,可不传
     * @param period 内容时间:月;若内容不需要时间,可不传
     */
    @RequestMapping("/push")
    @ResponseBody
    public Map<String, Object> push(Integer userId, String receivers, Integer menuId, Integer rootId, Integer year, Integer period) {
        Map<String, Object> result;
        try {
            if (receivers != null) {
                receivers = receivers.trim();
                receivers = receivers.substring(1);
                receivers = receivers.substring(0, receivers.length() - 1);
            }
            appFavoriteService.push(userId, receivers, menuId, rootId, year, period);
            result = genSuccessMsg("订阅成功");
        } catch (Exception e) {
            e.printStackTrace();
            result = genFaultMsg("订阅失败,服务端运行异常!");
        }
        return result;
    }

    /**
     * 根据id取消订阅或推送
     */
    @RequestMapping("/cancelOrders")
    @ResponseBody
    public Map<String, Object> cancelOrders(String ids) {
        Map<String, Object> result;
        try {
            appFavoriteService.cancelOrder(ids);
            result = genSuccessMsg("取消成功");
        } catch (Exception e) {
            e.printStackTrace();
            result = genFaultMsg("操作失败,服务器异常");
        }
        return result;
    }

    /**
     * 取消订阅
     *
     * @param userId 用户id
     * @param menuId 目录id
     * @param year   内容时间:年;若没有可不传
     * @param period 内容时间:月;若没有可不传
     */
    @RequestMapping("/cancelDescribe")
    @ResponseBody
    public Map<String, Object> cancelDescribe(Integer userId, Integer menuId, Integer year, Integer period) {
        Map<String, Object> result;
        try {
            appFavoriteService.cancelDescribe(userId, menuId, year, period);
            result = genSuccessMsg("取消成功");
        } catch (Exception e) {
            e.printStackTrace();
            result = genFaultMsg("操作失败,服务器异常");
        }
        return result;
    }

    /**
     * 取消推送给我
     *
     * @param userId 用户id
     * @param menuId 目录id
     * @param year   内容时间:年;若没有可不传
     * @param period 内容时间:月;若没有可不传
     */
    @RequestMapping("/cancelPushToMe")
    @ResponseBody
    public Map<String, Object> cancelPushToMe(Integer userId, Integer menuId, Integer year, Integer period) {
        Map<String, Object> result;
        try {
            appFavoriteService.cancelPushToMe(userId, menuId, year, period);
            result = genSuccessMsg("取消成功");
        } catch (Exception e) {
            e.printStackTrace();
            result = genFaultMsg("操作失败,服务器异常");
        }
        return result;
    }

    /**
     * 取消我推送给别人
     *
     * @param userId 用户id
     * @param menuId 目录id
     * @param year   内容时间:年;若没有可不传
     * @param period 内容时间:月;若没有可不传
     */
    @RequestMapping("/cancelMyPush")
    @ResponseBody
    public Map<String, Object> cancelMyPush(Integer userId, Integer menuId, Integer year, Integer period) {
        Map<String, Object> result;
        try {
            appFavoriteService.cancelMyPush(userId, menuId, year, period);
            result = genSuccessMsg("取消成功");
        } catch (Exception e) {
            e.printStackTrace();
            result = genFaultMsg("操作失败,服务器异常");
        }
        return result;
    }

    @RequestMapping("/feedback")
    @ResponseBody
    public Map<String, Object> feedback(Integer userId, String title, String content) {
        Map<String, Object> result;
        result = genSuccessMsg("反馈成功,感谢您的支持!");
        return result;
    }

    /**
     * 检查是否已订阅
     */
    @RequestMapping("/isDescribed")
    @ResponseBody
    public boolean isDescribed(Integer userId, Integer menuId, Integer year, Integer period) {
        return appFavoriteService.isDescribed(userId, menuId, year, period);
    }


}
