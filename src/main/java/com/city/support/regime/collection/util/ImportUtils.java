package com.city.support.regime.collection.util;

import com.city.common.pojo.Constant;
import com.city.common.util.table.builder.GenTableStrUtil;
import com.city.support.regime.collection.controller.ReportExcelController;
import com.city.support.regime.collection.entity.ReportDataId;
import com.city.support.regime.collection.pojo.RptHtmlPojo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by wgx on 2016/3/1.
 */
public class ImportUtils {
    /**
     * 获取根据html获取报表信息
     * @param reportId
     * @param dataType
     * @param collectionType
     * @param time
     * @param doc
     * @throws Exception
     */
    public static  List<RptHtmlPojo> getRptHtml(Integer reportId, Integer dataType, Integer collectionType, String time,Integer year,Integer month, Document doc,Integer depId) throws Exception {
        List<RptHtmlPojo> rptHtmlList = new ArrayList<RptHtmlPojo>();
        Elements tables = doc.select("table");
        Integer tmpId = null;
        if(tables.size()>0){
            tmpId = Integer.valueOf(tables.first().attr(GenTableStrUtil.TableAttrName.TMP_ID));
        }
        Elements tds = doc.select("td");
        String explain="";
        Elements tfoots = doc.select("tfoot");
        if(tfoots.size()>0)
            explain = tfoots.first().select("td").html();
        for (Element td : tds) {
            if (td.attr(GenTableStrUtil.TdAttrName.TYPE).equals(Constant.TdEsiType.DATA)) {
                RptHtmlPojo rptHtml = new RptHtmlPojo(reportId,tmpId,dataType,collectionType,time,explain);
                Map<String, String> map = getRptDataIdMap(td);
                rptHtml.setMap(map);
                ReportDataId reportDataId = setRptDataId(map, year,month, reportId,depId);
                rptHtml.setReportDataId(reportDataId);
                String value ="";
                if(td.select("input").size()==0){
                    value = td.html();
                }else{
                    value = td.select("input").first().attr("value");
                }
                String unit = "";
                String dataFormat = "";
                Integer unitId= null;
                if (td.attr(GenTableStrUtil.TdAttrName.UNIT) != null && !"".equals(td.attr(GenTableStrUtil.TdAttrName.UNIT))) {//单位
                    unitId = Integer.valueOf(td.attr(GenTableStrUtil.TdAttrName.UNIT));
                    rptHtml.setUnitId(unitId);
                }
                if (td.attr(GenTableStrUtil.TdAttrName.UNIT_NAME) != null) {//单位
                    unit = td.attr(GenTableStrUtil.TdAttrName.UNIT_NAME);
                    rptHtml.setUnit(unit);
                }
                if (td.attr(GenTableStrUtil.TdAttrName.DATA_FORMAT) != null) {//数据格式
                    dataFormat = td.attr(GenTableStrUtil.TdAttrName.DATA_FORMAT);
                    rptHtml.setDataFormat(dataFormat);
                }
                if (value != null && !"".equals(value)) {
                    rptHtml.setValue(value);
                }
                rptHtmlList.add(rptHtml);
            }

        }
        return rptHtmlList;
    }

    /**
     * 获取表样中主键信息
     *
     * @param td
     * @return
     */
    public static Map<String, String> getRptDataIdMap(Element td) {
        String esiDataValue = td.attr(GenTableStrUtil.TdAttrName.DATA_VALUE).trim().replace("\'", "\"");//.substring(1,td.attr("esi-data-value").trim().length()-1);
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        return gson.fromJson(esiDataValue, type);
    }

    /**
     * 设置联合主键
     * @param map
     * @param year
     * @param month
     * @param reportId
     * @return
     */
    public static ReportDataId setRptDataId(Map<String, String> map, Integer year,Integer month, Integer reportId,Integer depId){
        ReportDataId reportDataId = new ReportDataId();
        if (map.get(GenTableStrUtil.ProAttrName.TIME_FRAME) != null) {
            reportDataId.setTimeFrame((int) (double) Double.valueOf(map.get(GenTableStrUtil.ProAttrName.TIME_FRAME)));
        } else {
            reportDataId.setTimeFrame(0);
        }
        if (map.get(GenTableStrUtil.ProAttrName.ITEM) != null) {
            reportDataId.setItem((int) (double) Double.valueOf(map.get(GenTableStrUtil.ProAttrName.ITEM)));
        } else {
            reportDataId.setItem(0);
        }
        if (map.get(GenTableStrUtil.ProAttrName.ITEM_DICT) != null) {
            reportDataId.setItemDict(map.get(GenTableStrUtil.ProAttrName.ITEM_DICT));
        } else {
            reportDataId.setItemDict("0");
        }
        if (map.get(GenTableStrUtil.ProAttrName.ITEM_CALIBER) != null && !"".equals(map.get(GenTableStrUtil.ProAttrName.ITEM_CALIBER))) {
            reportDataId.setItemCaliber((int) (double) Double.valueOf(map.get(GenTableStrUtil.ProAttrName.ITEM_CALIBER)));
        } else {
            reportDataId.setItemCaliber(Constant.DEFAULT_ITEM_MENU);
        }
        if (map.get(GenTableStrUtil.ProAttrName.SUR_OBJ) != null) {
            reportDataId.setSurobj((int) (double) Double.valueOf(map.get(GenTableStrUtil.ProAttrName.SUR_OBJ)));
        } else {
            reportDataId.setSurobj(Constant.systemConfigPojo.getDefaultAreaId());
        }
        if (map.get(GenTableStrUtil.ProAttrName.SUR_OBJ_TYPE) != null) {
            reportDataId.setSurobjType((int) (double) Double.valueOf(map.get(GenTableStrUtil.ProAttrName.SUR_OBJ_TYPE)));
        } else {
            reportDataId.setSurobjType(Constant.DEFAULT_ITEM_MENU);
        }
        if (map.get(GenTableStrUtil.ProAttrName.DEP) != null && !"".equals(map.get(GenTableStrUtil.ProAttrName.DEP))) {
            reportDataId.setDepId((int) (double) Double.valueOf(map.get(GenTableStrUtil.ProAttrName.DEP)));
        } else {
            reportDataId.setDepId(depId);
        }
        if (map.get(GenTableStrUtil.ProAttrName.AREA) != null && !"".equals(map.get(GenTableStrUtil.ProAttrName.AREA))) {
            reportDataId.setAreaId((int) (double) Double.valueOf(map.get(GenTableStrUtil.ProAttrName.AREA)));
        }else if(reportDataId.getSurobjType()==Constant.ResearchObjType.AREA){
            reportDataId.setAreaId((int) (double) Double.valueOf(map.get(GenTableStrUtil.ProAttrName.SUR_OBJ)));
        } else {
            reportDataId.setAreaId(Constant.systemConfigPojo.getDefaultAreaId());
        }
        reportDataId.setYear(year);
        reportDataId.setMonth(month);
        reportDataId.setRptId(reportId);
        return reportDataId;
    }

}
