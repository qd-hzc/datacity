package com.city.common;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)  //使用junit4进行测试
@ContextConfiguration
        (locations = {"classpath:spring-context.xml",
                "classpath:spring-hibernate.xml", "classpath:spring-mvc.xml"})
public class BaseTest {

}
