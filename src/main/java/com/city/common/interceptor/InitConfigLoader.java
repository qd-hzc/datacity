package com.city.common.interceptor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContextEvent;

import com.city.common.util.ue.LoadUEConfig;
import org.springframework.web.context.ContextLoaderListener;

import com.city.common.pojo.Constant;
import com.city.common.pojo.SystemConfigPojo;

/**
 * 重写上下文拦截器,加载配置信息到Constant中
 *
 * @author wxl
 */
public class InitConfigLoader extends ContextLoaderListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        super.contextInitialized(event);
        InputStream is = null;
        try {
            is = InitConfigLoader.class.getClassLoader().getResourceAsStream("systemConfig.properties");
            Properties pro = new Properties();
            pro.load(is);
            SystemConfigPojo systemConfigPojo = Constant.systemConfigPojo;
            systemConfigPojo.setDebug(new Boolean(pro.getProperty("debug", "false")));
            systemConfigPojo.setShowSysLog(new Boolean(pro.getProperty("showSysLog", "false")));
            systemConfigPojo.setDepParentId(Integer.valueOf(pro.getProperty("depParentId", "0")));
            systemConfigPojo.setDefaultAreaId(Integer.valueOf(pro.getProperty("defaultAreaId", "0")));
            systemConfigPojo.setMainFocusSpan(Integer.valueOf(pro.getProperty("mainFocusSpan", "8")));
            systemConfigPojo.setUnitDataType(Integer.valueOf(pro.getProperty("unitDataType", "1")));
            systemConfigPojo.setAreaType(Integer.valueOf(pro.getProperty("areaType", "21")));
            systemConfigPojo.setYearType(Integer.valueOf(pro.getProperty("yearType", "49")));
            systemConfigPojo.setDutyType(Integer.valueOf(pro.getProperty("dutyType", "2")));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //加载ue配置
        LoadUEConfig.loadUEConfig(event.getServletContext().getContextPath());
    }

}
