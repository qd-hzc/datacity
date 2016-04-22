package com.city.support.manage.item.controller;

import com.city.common.controller.BaseController;
import com.city.common.pojo.DragDropPojo;
import com.city.support.manage.item.entity.ItemGroup;
import com.city.support.manage.item.entity.ItemGroupInfo;
import com.city.support.manage.item.service.ItemGroupDDService;
import com.city.support.manage.item.service.ItemGroupService;
import com.city.support.manage.pojo.DragAndDropVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/12/29 0029.
 */
@Controller
@RequestMapping("/support/manage/item")
public class ItemGroupController extends BaseController{
    @Autowired
    private ItemGroupService itemGroupService;
    @Autowired
    private ItemGroupDDService itemGroupDDService;

    /**
     * 进入分组配置界面
     */
    @RequestMapping("/itemGroupConfigPage")
    public String itemGroupConfigfPage(){
        return "support/manage/item/itemGroupConfig";
    }

    /**
     * 查询分组
     */
    @RequestMapping("/queryGroups")
    @ResponseBody
    public List<Map<String, Object>> queryGroups(String name,Integer status){
        return itemGroupService.queryGroups(name,status);
    }

    /**
     * 保存分组
     */
    @RequestMapping("/saveItemGroup")
    @ResponseBody
    public Map<String, Object> saveItemGroup(ItemGroup itemGroup){
        Map<String,Object> result;
        try {
            itemGroupService.saveGroup(itemGroup);
            result= genSuccessMsg(itemGroup,"保存成功",200);
        } catch (Exception e) {
            e.printStackTrace();
            result= genFaultMsg(null,"分组名已存在或服務端存在異常",500);
        }
        return result;
    }

    /**
     * 删除分组
     */
    @RequestMapping("/removeGroups")
    @ResponseBody
    public Map<String, Object> removeGroups(Integer id){
        Map<String,Object> result;
        try {
            itemGroupService.removeGroups(id);
            result= genSuccessMsg(null,"删除成功",200);
        } catch (Exception e) {
            e.printStackTrace();
            result= genFaultMsg(null,"删除失败",500);
        }
        return result;
    }

    @RequestMapping("/saveGroupSorts")
    @ResponseBody
    public Map<String, Object> saveGroupSorts(DragDropPojo dragDropPojo){
        Map<String,Object> result;
        try {
            itemGroupDDService.saveDragAndDrop(dragDropPojo);
            result= genSuccessMsg(null,"保存成功",200);
        } catch (Exception e) {
            e.printStackTrace();
            result= genFaultMsg(null,"保存失败",500);
        }
        return result;
    }

    /**
     * 根据分组获取信息
     */
    @RequestMapping("/getInfosByGroup")
    @ResponseBody
    public List<ItemGroupInfo> getInfosByGroup(String itemName, Integer status, Integer groupId, boolean includeDownLevel){
        return itemGroupService.getInfosByGroup(itemName, status, groupId, includeDownLevel);
    }

    /**
     * 保存分组信息
     */
    @RequestMapping("/saveItemGroupInfo")
    @ResponseBody
    public Map<String, Object> saveItemGroupInfo(ItemGroupInfo info, Integer itemId,Integer depId){
        Map<String,Object> result;
        try {
            itemGroupService.saveGroupInfo(info,itemId,depId);
            result= genSuccessMsg(info,"保存成功",200);
        } catch (Exception e) {
            e.printStackTrace();
            result= genFaultMsg(null,"保存失败",500);
        }
        return result;
    }

    /**
     * 批量添加分组信息
     */
    @RequestMapping("/saveItemGroupInfosBatch")
    @ResponseBody
    public Map<String, Object> saveItemGroupInfosBatch(String itemIds,Integer groupId,String groupName){
        Map<String,Object> result;
        try {
            itemGroupService.saveItemGroupInfosBatch(itemIds,groupId,groupName);
            result= genSuccessMsg(null,"保存成功",200);
        } catch (Exception e) {
            e.printStackTrace();
            result= genFaultMsg(null,"保存失败",500);
        }
        return result;
    }

    /**
     * 删除分组信息
     */
    @RequestMapping("/removeItemGroupInfos")
    @ResponseBody
    public Map<String, Object> removeItemGroupInfos(String ids){
        Map<String,Object> result;
        try {
            itemGroupService.removeItemGroupInfos(ids);
            result= genSuccessMsg(null,"删除成功",200);
        } catch (Exception e) {
            e.printStackTrace();
            result= genFaultMsg(null,"删除失败",500);
        }
        return result;
    }

    /**
     * 保存分组信息顺序
     */
    @RequestMapping("/saveItemGroupInfoSorts")
    @ResponseBody
    public Map<String, Object> saveItemGroupInfoSorts(String datas){
        Map<String,Object> result;
        try {
            itemGroupService.saveItemGroupInfoSorts(datas);
            result= genSuccessMsg(null,"保存成功",200);
        } catch (Exception e) {
            e.printStackTrace();
            result=genFaultMsg(null,"保存失败",500);
        }
        return result;
    }

}
