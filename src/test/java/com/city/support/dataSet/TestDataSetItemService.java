package com.city.support.dataSet;

import com.city.common.pojo.Constant;
import com.city.support.dataSet.service.DataSetDataService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by wxl on 2016/2/23.
 */
@RunWith(SpringJUnit4ClassRunner.class)  //使用junit4进行测试
@ContextConfiguration
        (locations = {"classpath:spring-context.xml",
                "classpath:spring-hibernate.xml", "classpath:spring-mvc.xml"})
public class TestDataSetItemService {
    @Autowired
    private DataSetDataService dataSetDataService;

    @Test
    public void test1(){
        dataSetDataService.queryBarInfos(Constant.MetadataType.RESEARCH_OBJ,"23,67");
    }

    @Test
    public void test2(){
        String aa="";
        Integer integer = Integer.valueOf(aa);
        System.out.println(integer);
    }
}
