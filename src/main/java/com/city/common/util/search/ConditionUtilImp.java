package com.city.common.util.search;

import com.city.common.util.StringUtil;
import com.city.common.util.table.builder.GenTableStrUtil;
import com.city.support.dataSet.query.pojo.RptDataPojo;
import com.city.support.dataSet.query.pojo.TimePojo;
import com.city.support.regime.collection.entity.ReportData;
import com.city.support.regime.collection.entity.ReportDataId;
import org.apache.commons.collections.MapUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wys on 2016/2/29.
 */
public class ConditionUtilImp implements ConditionUtil<Map> {

    @Override
    public Object getCondition(Map conditionObj, Integer metaType, Class resultClass) {
        return null;
    }

    @Override
    public boolean match(Map conditionObj, ReportData data) {
        if (data == null) {
            return false;
        }
        //指标
        int item = MapUtils.getIntValue(conditionObj, GenTableStrUtil.ProAttrName.ITEM);
        //时间框架
        int tf = MapUtils.getIntValue(conditionObj, GenTableStrUtil.ProAttrName.TIME_FRAME);
        //调查对象
        Integer surObj = MapUtils.getInteger(conditionObj, GenTableStrUtil.ProAttrName.SUR_OBJ);
        //分组目录
        String itemMenu = MapUtils.getString(conditionObj, GenTableStrUtil.ProAttrName.ITEM_DICT);
        if (!StringUtil.trimNotEmpty(itemMenu)) {//默认以0存储
            itemMenu = "0";
        }
        //部门
        Integer depId = MapUtils.getInteger(conditionObj, GenTableStrUtil.ProAttrName.DEP);
        //口径
        Integer caliberId = MapUtils.getInteger(conditionObj, GenTableStrUtil.ProAttrName.ITEM_CALIBER);
        if (caliberId == null) {//默认以0存储
            caliberId = 0;
        }
        //年月
        Integer year = MapUtils.getInteger(conditionObj, GenTableStrUtil.ProAttrName.YEAR);
        Integer time = MapUtils.getInteger(conditionObj, GenTableStrUtil.ProAttrName.TIME);
        ReportDataId reportDataId = data.getReportDataId();
        //判断
        boolean itemFlag = item == reportDataId.getItem();
        boolean tfFlag = tf == reportDataId.getTimeFrame();
        boolean surFlag = surObj == null || surObj.equals(reportDataId.getSurobj());
        boolean itemMenuFlag = itemMenu.equals(reportDataId.getItemDict());
        boolean depFlag = depId == null || depId.equals(reportDataId.getDepId());
        boolean caliberFlag = caliberId.equals(reportDataId.getItemCaliber());
        boolean yearFlag = year == null || year.equals(data.getReportDataId().getYear());
        boolean timeFlag = time == null || time.equals(data.getReportDataId().getMonth());
        return itemFlag && tfFlag && surFlag && itemMenuFlag && depFlag && caliberFlag && yearFlag && timeFlag;
    }

    @Override
    public List<ReportData> getDatas(Map conditionObj, List<ReportData> datas) {
        List<ReportData> result = null;
        if (datas != null && datas.size() > 0) {
            result = new ArrayList<>();
            for (ReportData data : datas) {
                if (match(conditionObj, data)) {
                    result.add(data);
                }
            }
        }
        return result;
    }

    @Override
    public ReportData getData(Map conditionObj, List<ReportData> datas) {
        if (datas != null && datas.size() > 0) {
            for (ReportData data : datas) {
                if (match(conditionObj, data)) {
                    return data;
                }
            }
        }
        return null;
    }

    /**
     * 根据时间获取List<ReportData>
     *
     * @param conditionObj 条件
     * @param datas        查询出的数据
     */
    public List<ReportData> getRptDatasByTime(Map conditionObj, List<RptDataPojo> datas) {
        //年月
        Integer year = MapUtils.getInteger(conditionObj, GenTableStrUtil.ProAttrName.YEAR);
        boolean yearNotNull = year != null;
        Integer period = MapUtils.getInteger(conditionObj, GenTableStrUtil.ProAttrName.TIME);
        boolean periodNotNull = period != null;
        //根据时间添加数据
        List<ReportData> rptDatas = null;
        if (datas != null && datas.size() > 0) {
            rptDatas = new ArrayList<>();
            for (RptDataPojo data : datas) {
                TimePojo time = data.getTime();
                //匹配年
                boolean yearMatch = true;
                if (yearNotNull) {
                    yearMatch = time.getYear() == year;
                }
                //匹配时间
                boolean periodMatch = true;
                if (periodNotNull) {
                    periodMatch = time.getPeriod() == period;
                }
                //添加数据
                if (yearMatch && periodMatch) {
                    //数据
                    List<ReportData> datas1 = data.getDatas();
                    if (datas1 != null && datas1.size() > 0) {
                        rptDatas.addAll(datas1);
                    }
                }
            }
        }
        return rptDatas;
    }
}
