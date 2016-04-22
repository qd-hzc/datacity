package com.city.support.sys;

import com.city.support.sys.module.entity.Module;
import com.city.support.sys.module.pojo.ModuleNode;
import com.city.support.sys.module.service.ModuleManagerService;
import com.city.support.sys.user.entity.User;
import com.city.support.sys.user.service.UserManagerService;
import com.google.gson.Gson;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

/**
 * Created by wys on 2015/12/30.
 */
@RunWith(SpringJUnit4ClassRunner.class)  //使用junit4进行测试
@ContextConfiguration
        (locations = {"classpath:spring-context.xml",
                "classpath:spring-hibernate.xml", "classpath:spring-mvc.xml"})
public class SysTest {
    @Autowired
    private ModuleManagerService moduleManagerService;
    @Autowired
    private UserManagerService userManagerService;

    @Test
    public void addModule() {
        Module md = new Module();
        md.setModuleName("数据集管理");
        md.setModuleShortName("数据集管理");
        md.setModuleType(Module.DIRMOD);
        md.setModulePid(2);
//        md.setModuleConfig("/support/dataSet/manageJsp");
        md.setModuleSort(3);
        md.setModuleDesc("基础支撑系统>资源管理>数据集管理");
        md.setModuleState(Module.ENABLE);
        moduleManagerService.addModule(md);

        Module module = new Module();
        module.setModuleName("数据集管理");
        module.setModuleShortName("数据集管理");
        module.setModuleType(Module.FUNMOD);
        module.setModulePid(md.getId());
        module.setModuleConfig("/support/dataSet/manageJsp");
        module.setModuleSort(1);
        module.setModuleDesc("基础支撑系统>资源管理>数据集管理>数据集管理");
        module.setModuleState(Module.ENABLE);
        moduleManagerService.addModule(module);
    }

    @Test
    public void getTree() {
        Map<String, Object> userMap = userManagerService.vailUser("admin", "mdss");
        User user = (User) userMap.get("data");
        Map root = moduleManagerService.getModuleTree(user, null);
//        Module module = moduleManagerService.getModuleById(1);
//        ModuleNode root = moduleManagerService.getModuleTree(user, module);
        Gson g = new Gson();

        System.out.println(g.toJson(root));
    }
}
