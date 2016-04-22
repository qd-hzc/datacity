package com.city.support.manage.item;

import com.city.common.pojo.Page;
import com.city.support.manage.item.entity.Item;
import com.city.support.manage.item.entity.ItemGroup;
import com.city.support.manage.item.entity.ItemGroupInfo;
import com.city.support.manage.item.service.ItemGroupService;
import com.city.support.manage.item.service.ItemService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Created by Administrator on 2015/12/29 0029.
 */
@RunWith(SpringJUnit4ClassRunner.class)  //使用junit4进行测试
@ContextConfiguration
        (locations = {"classpath:spring-context.xml",
                "classpath:spring-hibernate.xml", "classpath:spring-mvc.xml"})
public class TestItemGroup {
    @Autowired
    private ItemGroupService itemGroupService;
    @Autowired
    private ItemService itemService;

    @Test
    public void testSave(){
        List<Item> items = itemService.getItemsForPage("",null,new Page()).getDatas();
        if(items!=null&&items.size()>0 ){
            ItemGroup itemGroup=new ItemGroup();
            itemGroup.setName("测试指标体系");
            itemGroup.setParentId(0);
            itemGroup.setStatus(1);

            Item item=items.get(0);
            ItemGroupInfo info=new ItemGroupInfo(item);
            info.setGroupName(itemGroup.getName());

            itemGroupService.saveGroup(itemGroup);
        }
    }

    @Test
    public void testDelete(){
        itemGroupService.removeGroups(0);
    }
}
