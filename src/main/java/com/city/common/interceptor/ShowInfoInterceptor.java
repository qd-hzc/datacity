package com.city.common.interceptor;

import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.city.common.pojo.Constant;
import com.city.common.util.EsiLogUtil;

/**
 * 请求拦截器,查看请求和参数
 *
 * @author wxl
 */
public class ShowInfoInterceptor extends HandlerInterceptorAdapter {
    Logger log = EsiLogUtil.getLogInstance(this.getClass());

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        //设置跨域请求
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS");
        response.addHeader("Access-Control-Allow-Headers", "x-app");
        //如果IE浏览器则设置头信息如下
        if ("IE".equals(request.getParameter("type"))) {
            response.addHeader("XDomainRequestAllowed", "1");
        }
        // 如果使用拦截器拦截路径
        if (Constant.systemConfigPojo.isDebug()) {
            Map names = request.getParameterMap();
            Set<String> keys = names.keySet();
            StringBuilder sb = new StringBuilder("请求拦截开始\n请求路径:----->").append(request.getRequestURI());
            for (String key : keys) {
                sb.append("\n").append(key).append("----->");
                String[] values = (String[]) names.get(key);
                for (String v : values) {
                    sb.append(v);
                }
            }
            EsiLogUtil.debug(log, sb.toString());
            EsiLogUtil.debug(log, "请求拦截结束");
        }
        return super.preHandle(request, response, handler);
    }
}
