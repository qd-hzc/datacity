package com.city.app.push.controller;

import com.city.app.push.entity.PushState;
import com.city.app.push.service.PushStateService;
import com.city.common.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhoutao on 2016/4/19.
 */
@Controller
public class PushStateController extends BaseController {
    @Autowired
    private PushStateService pushStateService;

    @RequestMapping("/app/push/pushState/findPushStateByUserId")
    @ResponseBody
    public Map<String,Object> findPushStateByUserId(Integer userId){
        Map<String, Object> result = genSuccessMsg("获取推送数据成功！");

        PushState pushState = pushStateService.findByAppUserId(userId);
        if(pushState != null)
            result.put("pushState", pushState.isPushState());

        return result;
    }

}
