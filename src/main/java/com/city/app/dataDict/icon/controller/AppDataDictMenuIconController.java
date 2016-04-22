package com.city.app.dataDict.icon.controller;

import com.city.app.dataDict.icon.entity.AppDataDictMenuIcon;
import com.city.app.dataDict.icon.service.AppDataDictMenuIconService;
import com.city.common.pojo.AppConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by wxl on 2016/3/30.
 */
@Controller
@RequestMapping("/app/dataDict")
public class AppDataDictMenuIconController {
    @Autowired
    private AppDataDictMenuIconService appDataDictMenuIconService;

    /**
     * 根据名字查询图标
     */
    @RequestMapping("/queryIcons")
    @ResponseBody
    public List<AppDataDictMenuIcon> queryIcons(String name) {
        return appDataDictMenuIconService.queryByType(name, AppConstant.DATA_DICT_MENU_ICON_TYPE.ICON);
    }

    /**
     * 根据名字查询背景
     */
    @RequestMapping("/queryBgs")
    @ResponseBody
    public List<AppDataDictMenuIcon> queryBgs(String name) {
        return appDataDictMenuIconService.queryByType(name, AppConstant.DATA_DICT_MENU_ICON_TYPE.BG);
    }

}
