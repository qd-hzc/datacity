package com.city.app.push.pojo;

import com.city.app.push.service.PushStateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by zhoutao on 2016/4/19.
 */
@Component
public class PushStateTask {
    @Autowired
    private PushStateService pushStateService;
    /**
     * 定时重置推送状态
     */
    public void reset(){
        pushStateService.reset();
    }
}
