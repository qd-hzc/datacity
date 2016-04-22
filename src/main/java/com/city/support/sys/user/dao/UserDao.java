package com.city.support.sys.user.dao;

import com.city.common.dao.BaseDao;
import com.city.common.pojo.Page;
import com.city.support.sys.user.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDao extends BaseDao<User> {
    /**
     * 登陆名是否重复
     *
     * @param loginName
     * @return
     */
    public boolean vailLoginName(String loginName) {
        String hql = "From User t where t.loginName=?";
        Object[] params = {loginName};
        List<User> userList = this.queryWithParamsByHQL(hql, params);
        if (userList.size() > 0)
            return false;
        else
            return true;
    }

    public User vailUser(String userName, String userPwd) {
        String hql = "From User t where t.loginName=? and t.loginPwd = ? and t.state = "+User.STATE_ENABLE;
        Object[] params = {userName, userPwd};
        List<User> userList = this.queryWithParamsByHQL(hql, params);
        if (userList.size() > 0)
            return userList.get(0);
        else
            return null;

    }

    //根据用户名或登录名查询用户信息
    public List<User> getUserByNameOrLoginName(String name,String loginName){
        String hql = "From User t where t.loginName like '%"+loginName+"%' and t.userName like '%"+name+"%' and t.state = "+User.STATE_ENABLE+" order by t.id desc";

        return super.queryByHQL(hql);
    }

    //根据用户名或登录名查询用户信息   分页
    public List<User> getUserPageByNameOrLoginName(Page page,String name,String loginName){
        String hql = "From User t where t.loginName like '%"+loginName+"%' and t.userName like '%"+name+"%' and t.state = "+User.STATE_ENABLE+" order by t.id desc";

        return super.queryWithPageByHQL(hql, page);
    }

    //根据删除制定用户已管理角色
    public void deleteRoleByUserId(Integer userId){
        String sql = "delete from SPT_SYS_USER_ROLE r where r.USER_ID = "+userId;

        super.getSession().createSQLQuery(sql).executeUpdate();
    }

}
