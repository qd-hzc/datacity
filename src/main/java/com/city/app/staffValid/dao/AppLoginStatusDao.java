package com.city.app.staffValid.dao;

import com.city.app.staffValid.entity.AppLoginStatus;
import com.city.common.dao.BaseDao;
import com.city.common.pojo.Page;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by wxl on 2016/5/18.
 */
@Repository
public class AppLoginStatusDao extends BaseDao<AppLoginStatus> {

    /**
     * 登陆成功,清除失败登录记录
     */
    public void clearFailLogin(String uuid) {
        String hql = "delete from AppLoginStatus where uuid='" + uuid + "'";
        updateByHQL(hql);
    }

    /**
     * 查询登录失败记录
     */
    public List<AppLoginStatus> queryFailLogins(String uuid) {
        String hql = "from AppLoginStatus where uuid='" + uuid + "'" + "order by failTime desc";
        Page page = new Page();
        page.setStart(1);
        page.setLimit(3);
        return queryWithPageByHQL(hql, page);
    }
}
