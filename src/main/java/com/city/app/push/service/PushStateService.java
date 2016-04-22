package com.city.app.push.service;

import com.city.app.push.dao.PushStateDao;
import com.city.app.push.entity.PushState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Schedules;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by zhoutao on 2016/4/19.
 */
@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
public class PushStateService {
    @Autowired
    private PushStateDao pushStateDao;

    public void saveOrUpdate(PushState pushState){
        pushStateDao.saveOrUpdate(pushState, false);
    }

    /**
     * 获取用户推送状态
     * @param appUserId
     * @return
     */
    public  PushState findByAppUserId(Integer appUserId){
        PushState pushState = pushStateDao.findByAppUserId(appUserId);
        if(pushState!=null && pushState.isPushState())//如果推送状态为未推送  修改设置为已推送
            pushStateDao.updateState(appUserId);
        return pushState;
    }

    public void reset(){
        pushStateDao.reset();
    }
}
