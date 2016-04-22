package com.city.common.util;

import com.city.support.sys.user.entity.User;

import javax.servlet.http.HttpSession;

/**
 * session类
 * session工具方法，给session对象添加值，获取session中某个值
 * Created by HZC on 2015/12/31.
 */
public class SessionUtil {

    /**
     * 保存User
     *
     * @param session
     * @param user
     */
    public static void setUser(HttpSession session, User user) {
        session.setAttribute("user", user);
    }

    /**
     * 返回User
     *
     * @param session
     * @return
     */
    public static User getUser(HttpSession session) {
        return (User) session.getAttribute("user");
    }
}
