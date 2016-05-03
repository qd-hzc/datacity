package com.city.app.staffValid.dao;

import com.city.app.push.dao.PushStateDao;
import com.city.app.push.entity.PushState;
import com.city.app.staffValid.entity.AppPerson;
import com.city.common.dao.BaseDao;
import com.city.common.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wxl on 2016/3/28.
 */
@Repository
public class AppPersonDao extends BaseDao<AppPerson> {
    @Autowired
    private PushStateDao pushStateDao;

    /**
     * 根据条件查询
     *
     * @param name      名称
     * @param validCode 校验码
     * @param depIds    部门
     */
    public List<AppPerson> queryStaffs(String name, String validCode, String depIds) {
        StringBuilder sb = new StringBuilder("from AppPerson where 1=1");
        if (StringUtil.trimNotEmpty(name)) {
            sb.append(" and name like '%").append(name.trim()).append("%'");
        }
        if (StringUtil.trimNotEmpty(validCode)) {//完全匹配!
            sb.append(" and validCode=").append(validCode);
        }
        if (StringUtil.trimNotEmpty(depIds)) {
            sb.append(" and department.id in (").append(depIds).append(")");
        }
        sb.append(" order by id desc");
        return queryByHQL(sb.toString());
    }

    /**
     * 查询所有
     */
    public List<AppPerson> queryAllStaffs(String ids) {
        StringBuilder sb = new StringBuilder("from AppPerson where 1=1");
        if (StringUtil.trimNotEmpty(ids)) {
            sb.append(" and id in (").append(ids).append(")");
        }
        return queryByHQL(sb.toString());
    }

    /**
     * 查询含有某验证码的人
     */
    public AppPerson queryStaffByValidCode(String validCode) {
        StringBuilder sb = new StringBuilder("from AppPerson where 1=1");
        if (StringUtil.trimNotEmpty(validCode)) {
            sb.append(" and validCode=").append(validCode);
        }
        return unqueryByHQL(sb.toString());

    }

    /**
     * 修改人员
     *
     * @param appPerson 人员
     */
    public void saveStaff(AppPerson appPerson) {
        //设置角色
        if (appPerson.getRole() == null || appPerson.getRole().getId() == null) {
            appPerson.setRole(null);
        }
        //修改
        if (appPerson.getId() != null) {
            update(appPerson, false);
        } else {
            //添加
            String validCode = createValidcode();
            if (!isHasStaffs(validCode)) {
                appPerson.setValidCode(validCode);
                insert(appPerson, false);
            } else {
                saveStaff(appPerson);
            }

            //add by zt 添加人员后需要在推送状态表中增加此人员
            PushState ps = new PushState();
            ps.setAppUserId(appPerson.getId());
            pushStateDao.save(ps);
        }

    }

    /**
     * 删除人员
     *
     * @param id 人员id
     */
    public void deleteStaff(Integer id) {
        if (id != null) {
            String hql = "delete from AppPerson where id=" + id;
            updateByHQL(hql);
        }
    }

    /**
     * 查询是否含有此验证码的人员
     */
    public boolean isHasStaffs(String validCode) {
        StringBuilder sb = new StringBuilder("from AppPerson where 1=1");
        if (StringUtil.trimNotEmpty(validCode)) {//完全匹配!
            sb.append(" and validCode=").append(validCode);
        }
        if (queryByHQL(sb.toString()).size() > 0)
            return true;
        return false;
    }

    /**
     * 产生一个随机数
     */
    private String createValidcode() {
        int x = (int) (Math.random() * 9000 + 1000);
        return x + "";
    }

    /**
     * 查询手机用户,用来获取名字
     */
    public List<AppPerson> queryPersons(String personIds) {
        StringBuilder sb = new StringBuilder("from AppPerson where id in (").append(personIds).append(")");
        return super.queryByHQL(sb.toString());
    }
}
