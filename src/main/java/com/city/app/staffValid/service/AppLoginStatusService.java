package com.city.app.staffValid.service;

import com.city.app.staffValid.dao.AppLoginStatusDao;
import com.city.app.staffValid.entity.AppLoginStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by wxl on 2016/5/18.
 */
@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
public class AppLoginStatusService {
    @Autowired
    private AppLoginStatusDao appLoginStatusDao;

    /**
     * 下次能登陆的时间
     */
    public long nextLoginTime(String uuid) {
        List<AppLoginStatus> loginStatuses = appLoginStatusDao.queryFailLogins(uuid);
        int size = loginStatuses.size();
        if (size >= 3) {
            //判断最后三次登陆
            AppLoginStatus loginStatus = loginStatuses.get(size - 3);
            long time = System.currentTimeMillis() - loginStatus.getFailTime().getTime();
            if (time < 60 * 60 * 100) {//小于一小时
                return time;
            }
        }
        return 0;
    }

    /**
     * 登录失败,保存次数
     */
    public void saveFailLogin(String uuid) {
        AppLoginStatus appLoginStatus = new AppLoginStatus();
        appLoginStatus.setUuid(uuid);
        appLoginStatus.setFailTime(new Date());
        appLoginStatusDao.insert(appLoginStatus, false);
    }

    /**
     * 清除失败的登录
     */
    public void clearFailLogin(String uuid) {
        appLoginStatusDao.clearFailLogin(uuid);
    }
}
