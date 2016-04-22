package com.city.support.manage.surobj.controller;

import com.city.common.controller.BaseController;
import com.city.support.manage.surobj.service.SurveyObjManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by wys on 2016/1/21.
 */
@Controller
@RequestMapping("/support/manage/surobj")
public class SurObjHandleController extends BaseController {
    @Autowired
    private SurveyObjManagerService surveyObjManagerService;


}
