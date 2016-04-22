package com.city.support.regime.collection.pojo;

import com.city.support.regime.collection.controller.ReportInfoController;
import com.city.support.regime.collection.service.ReportInfoService;
import com.city.support.regime.report.dao.ReportTemplateDao;
import com.city.support.regime.report.entity.ReportTemplate;
import com.city.support.regime.report.entity.ReportTemplateStyle;
import com.city.support.regime.report.service.ReportManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.List;

/**
 * Created by wgx on 2016/2/3.
 */
@Component
public class FixedTime {
    @Autowired
    ReportManageService reportManageService;
    @Autowired
    ReportInfoService reportInfoService;

    /**
     * 用于判断计划是否在执行中（防止计划运行时间大于计划间隔时间）
     */
    private boolean isPlanExecuting = false;
    /**
     * 执行自动生成报表计划（自动生成当期报表）
     */
    public void createNowMonthReportInfos() {
        List<ReportTemplate> reportTemplateList =reportManageService.getAllRptTmp();
        reportInfoService.createReportInfos(null,reportTemplateList);
    }
    /**
     * 执行自动生成报表计划（自动生成上月报表）
     */
    public void createLastMonthReportInfos() {
        List<ReportTemplate> reportTemplateList =reportManageService.getAllRptTmp();
        reportInfoService.createLastMonthReportInfos(null, reportTemplateList);
    }
}
