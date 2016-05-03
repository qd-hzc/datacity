package com.city.common.util;

import com.city.support.sys.user.entity.User;
import com.city.support.sys.user.pojo.CurrentUser;

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

    /**
     * 设置当前用户
     *
     * @param session
     * @param user
     * @author hzc
     * @crateDate 2016-5-3
     */
    public static void setCurrentUser(HttpSession session, CurrentUser user) {
        session.setAttribute("currentUser", user);
    }

    /**
     * 获取当前用户
     *
     * @param session
     * @return
     * @author hzc
     * @createDate 2016-5-3
     */
    public static CurrentUser getCurrentUser(HttpSession session) {
        return (CurrentUser) session.getAttribute("currentUser");
    }
}
