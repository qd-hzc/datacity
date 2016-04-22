package com.city.support.sys.user.controller;

import com.city.support.sys.user.service.DepQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * Created by wxl on 2016/1/7 0007.
 */
@Controller
public class DepQueryController {
    @Autowired
    private DepQueryService depQueryService;

    @RequestMapping("/support/sys/dep/queryDepTreeByName")
    @ResponseBody
    public List<Map<String, Object>> queryDepTreeByName(String depName){
        return depQueryService.packageListToTree(depQueryService.queryDepTreeByName(depName),depName!=null&&depName.trim().length()>0);
    }

}
