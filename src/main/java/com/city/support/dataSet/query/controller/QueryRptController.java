package com.city.support.dataSet.query.controller;

import com.city.common.controller.BaseController;
import com.city.support.dataSet.query.service.QueryRptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by wxl on 2016/3/3.
 */
@Controller
@RequestMapping("/support/dataSet/query")
public class QueryRptController extends BaseController {
    @Autowired
    private QueryRptService queryRptService;


}
