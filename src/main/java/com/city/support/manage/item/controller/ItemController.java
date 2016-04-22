package com.city.support.manage.item.controller;

import com.city.common.controller.BaseController;
import com.city.common.pojo.Page;
import com.city.support.manage.item.entity.Item;
import com.city.support.manage.item.entity.ItemCaliber;
import com.city.support.manage.item.entity.ItemInfo;
import com.city.support.manage.item.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/12/28 0028.
 */
@Controller
@RequestMapping("/support/manage/item")
public class ItemController extends BaseController{
    @Autowired
    private ItemService itemService;

    /**
     * 进入指标配置界面
     * @return
     */
    @RequestMapping("/itemConfigPage")
    public String itemConfigPage(){
        return "support/manage/item/itemCongif";
    }

    /**
     * 获取指标
     * @return
     */
    @RequestMapping("/getItemsForPage")
    @ResponseBody
    public Page getItemsForPage(String name, Integer status, Page page){
        return itemService.getItemsForPage(name,status,page);
    }

    /**
     * 保存指标
     */
    @RequestMapping("/saveItem")
    @ResponseBody
    public Map<String, Object> saveItem(Item item,Integer depId){
        Map<String,Object> result;
        try {
            itemService.saveItem(item,depId);
            result= genSuccessMsg(item,"保存成功",200);
        } catch (Exception e) {
            e.printStackTrace();
            result=genFaultMsg(null,"已存在该指标",500);
        }
        return result;
    }

    /**
     * 修改指标状态
     */
    @RequestMapping("/setItemStatus")
    @ResponseBody
    public Map<String, Object> setItemStatus(String ids, Integer status){
        Map<String,Object> result;
        try {
            itemService.setItemStatus(ids,status);
            result= genSuccessMsg(null,"操作成功",200);
        } catch (Exception e) {
            e.printStackTrace();
            result=genFaultMsg(null,"操作失败",500);
        }
        return result;
    }

    /**
     * 删除指标
     */
    @RequestMapping("/removeItems")
    @ResponseBody
    public Map<String, Object> removeItems(String items){
        Map<String,Object> result;
        try {
            itemService.removeItems(items);
            result= genSuccessMsg(null,"删除成功",200);
        } catch (Exception e) {
            e.printStackTrace();
            result=genFaultMsg(null,"删除失败",500);
        }
        return result;
    }

    /**
     * 根据指标获取口径
     */
    @RequestMapping("/queryCalibersByItem")
    @ResponseBody
    public List<ItemCaliber> queryCalibersByItem(Integer itemId){
        return itemService.queryCalibersByItem(itemId);
    }

    /**
     * 添加口径
     */
    @RequestMapping("/saveCaliber")
    @ResponseBody
    public Map<String, Object> saveCaliber(ItemCaliber caliber){
        Map<String,Object> result;
        try {
            itemService.saveCaliber(caliber);
            result= genSuccessMsg(caliber,"保存成功",200);
        } catch (Exception e) {
            e.printStackTrace();
            result=genFaultMsg(null,"保存失败",500);
        }
        return result;
    }

    /**
     * 删除口径
     */
    @RequestMapping("/removeCalibers")
    @ResponseBody
    public Map<String, Object> removeCalibers(String calibers){
        Map<String,Object> result;
        try {
            itemService.removeCalibers(calibers);
            result= genSuccessMsg(null,"删除成功",200);
        } catch (Exception e) {
            e.printStackTrace();
            result=genFaultMsg(null,"删除失败",500);
        }
        return result;
    }

    /**
     * 保存口径顺序
     */
    @RequestMapping("/saveCaliberSorts")
    @ResponseBody
    public Map<String, Object> saveCaliberSorts(String datas){
        Map<String,Object> result;
        try {
            itemService.saveCaliberSorts(datas);
            result= genSuccessMsg(null,"保存成功",200);
        } catch (Exception e) {
            e.printStackTrace();
            result=genFaultMsg(null,"保存成功",500);
        }
        return result;
    }

    @RequestMapping("/getItemInfosByItem")
    @ResponseBody
    public List<ItemInfo> getItemInfosByItem(Integer itemId){
        return itemService.getItemInfosByItem(itemId);
    }

    /**
     * 保存指标信息
     */
    @RequestMapping("/saveItemInfo")
    @ResponseBody
    public Map<String, Object> saveItemInfo(ItemInfo itemInfo,Integer unitId, Integer timeFrameId, Integer dataTypeId){
        Map<String,Object> result;
        try {
            itemService.saveItemInfo(itemInfo,unitId,timeFrameId,dataTypeId);
            result= genSuccessMsg(itemInfo,"保存成功",200);
        } catch (Exception e) {
            e.printStackTrace();
            result=genFaultMsg(null,"保存失败",500);
        }
        return result;
    }

    /**
     * 删除指标信息
     */
    @RequestMapping("/removeItemInfos")
    @ResponseBody
    public Map<String, Object> removeItemInfos(String ids){
        Map<String,Object> result;
        try {
            itemService.removeItemInfos(ids);
            result= genSuccessMsg(null,"删除成功",200);
        } catch (Exception e) {
            e.printStackTrace();
            result=genFaultMsg(null,"删除失败",500);
        }
        return result;
    }
}
