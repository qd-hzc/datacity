package com.city.support.manage.item;

import com.city.common.pojo.Page;
import com.city.support.manage.item.entity.Item;
import com.city.support.manage.item.entity.ItemCaliber;
import com.city.support.manage.item.service.ItemService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/12/29 0029.
 */
@RunWith(SpringJUnit4ClassRunner.class)  //使用junit4进行测试
@ContextConfiguration
        (locations = {"classpath:spring-context.xml",
                "classpath:spring-hibernate.xml", "classpath:spring-mvc.xml"})
//加载配置文件
public class TestItem {
    @Autowired
    private ItemService itemService;
    @Test
    public void testSave(){
//        Item item=new Item();
//        item.setName("指标测试");
//        item.setStatus(1);
//        item.setType(1);
//        item.setDepartment(1);
//        item.setCaliberId(1);
//
//        ItemCaliber itemCaliber = new ItemCaliber();
//        itemCaliber.setName("口径测试");
//        itemCaliber.setItemId(item.getId());
//        itemCaliber.setStatisticsMethod("抽样调查");
//        List<ItemCaliber>itemCalibers=new ArrayList<ItemCaliber>();
//        itemCalibers.add(itemCaliber);
//
//        item.setItemCalibers(itemCalibers);
//        itemService.saveItem(item, depId);
    }

    @Test
    public void testDelete(){
        List<Item> items = itemService.getItemsForPage("",null,new Page()).getDatas();
        if(items!=null&&items.size()>0 ){
            Item item=items.get(0);
            itemService.deleteItem(item);
        }
    }
}

