package com.city.support.regime.collection.service;

import com.city.common.pojo.Constant;
import com.city.common.util.SessionUtil;
import com.city.support.regime.collection.dao.ReportInfoDao;
import com.city.support.regime.collection.entity.ReportData;
import com.city.support.regime.collection.entity.ReportDataId;
import com.city.support.regime.collection.entity.ReportInfo;
import com.city.support.regime.collection.pojo.RptHtmlPojo;
import com.city.support.regime.collection.util.ImportUtils;
import com.city.support.regime.report.dao.ReportTemplateDao;
import com.city.support.regime.report.dao.ReportTemplateStyleDao;
import com.city.support.regime.report.entity.ReportTemplate;
import com.city.support.regime.report.entity.ReportTemplateStyle;
import com.city.support.regime.report.service.ReportManageService;
import com.city.support.sys.user.entity.User;
import com.city.support.sys.user.pojo.CurrentUser;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.parser.XmlTreeBuilder;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wgx on 2016/5/6.
 */
@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
public class ReportExtractService {

    @Autowired
    private ReportInfoDao reportInfoDao;

    @Autowired
    private ReportTemplateDao reportTemplateDao;
    @Autowired
    private ReportTemplateStyleDao reportTemplateStyleDao;
    @Autowired
    private ReportManageService reportManageService;
    @Autowired
    private ReportDataService reportDataService;
    @Autowired
    private ReportInfoService reportInfoService;

    /**
     * 数据抽取 - 抽取徐州老平台报表
     * 数据域 tr.tr_con_style td m
     * @param rptInfoId      报表id
     * @param html            抽取网页
     * @param request
     */
    public Boolean extractHtml(Integer rptInfoId, String html, HttpServletRequest request) {

        try {
            // 获取当前用户
            User user = SessionUtil.getUser(request.getSession());
            if (html != null && rptInfoId != null) {
                Document doc = Jsoup.parse(html, "", new Parser(new XmlTreeBuilder()));
                Elements tables = doc.select("table");
                if (tables.size() > 0) {
                    List<List<String>> trDataList = new ArrayList<>();
                    Element table = tables.first();
                    Elements tbodys = table.select("tbody");//表格内容
                    if (tbodys.size() > 0) {
                        Elements trs = tbodys.select("tr.tr_con_style");
                        for (Element tr : trs) {
                            List<String> dataList = new ArrayList<>();
                            Elements tds = tr.select("td");
                            for (Element td : tds) {
                                Elements ms = td.select("m");
                                for (Element m : ms) {
                                    dataList.add(m.text());
                                }
                            }
                            trDataList.add(dataList);
                        }
                    }
                    ReportInfo reportInfo = reportInfoDao.queryById(rptInfoId);
                    ReportTemplateStyle reportTemplateStyle = reportManageService.getRptStyleByRptInfo(reportInfo);
                    String styleHtml = (reportTemplateStyle != null) ? reportTemplateStyle.getRptStyle() : null;
                    if (styleHtml != null) {
                        Document styleDoc = Jsoup.parse(styleHtml, "", new Parser(new XmlTreeBuilder()));
                        Elements styleTables = styleDoc.select("table");
                        // 将抽取的html中的数据添加此报表的表样中
                        Elements trs = styleTables.select("tr");
                        int i=0;
                        for (Element tr: trs) {
                            Elements tds = tr.select("td");
                            int j=0;
                            for (Element td:tds) {
                                if (td.attr("esi-type").equals(Constant.TdEsiType.DATA)) {
                                    td.html("<input value='" + trDataList.get(i).get(j) + "'/>");
                                    j++;
                                }
                            }
                            if(j>0){
                                i++;
                            }
                        }
                        List<RptHtmlPojo> rptHtmlList = ImportUtils.getRptHtml(reportInfo.getId(), Constant.DATA_TYPE.NUMBER, Constant.COLLECTION_TYPE.EXTRACT, reportInfo.getTime(), reportInfo.getYear(), reportInfo.getMonth(), styleDoc, reportInfo.getDptId());
                        reportDataService.saveOrSubmitRptDataList(user, rptHtmlList, reportInfo.getRptStatus(), reportInfo);

                        if (reportInfo.getRptStatus() != Constant.RPT_STATUS.REJECT) {
                            //修改报表状态
                            reportInfoService.updateStatus(user, reportInfo.getId(), Constant.RPT_STATUS.DRAFT, request);
                        }
                        return true;
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }
}
