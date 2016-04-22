package com.city.support.dataSet.query.util;

import com.city.common.pojo.Constant;
import com.city.common.util.ListUtil;
import com.city.common.util.search.ConditionUtilImp;
import com.city.common.util.table.builder.GenTableStrUtil;
import com.city.support.dataSet.query.pojo.RptDataPojo;
import com.city.support.dataSet.query.pojo.TimePojo;
import com.city.support.regime.collection.entity.ReportData;
import com.google.gson.Gson;
import org.apache.commons.collections.MapUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by wxl on 2016/3/3.
 * 向表样中填充数据
 */
public class FillDataUtil {
    private static Gson gson = new Gson();

    /**
     * 向表样中填充数据
     *
     * @param table 表样
     * @param datas 要填充的数据
     * @return Element 填好数据的表样
     */
    public static Element fillTableData(Element table, List<RptDataPojo> datas) {
        if (table != null && datas != null && datas.size() > 0) {
            //条件工具类
            ConditionUtilImp conditionUtilImp = new ConditionUtilImp();
            //数据域
            Elements tds = table.getElementsByAttributeValue(GenTableStrUtil.TdAttrName.TYPE, Constant.TdEsiType.DATA);
            //迭代数据域,填充数据
            Iterator<Element> iterator = tds.iterator();
            Element td = null;
            while (iterator.hasNext()) {
                td = iterator.next();
                //单元格中带有的数据信息
                String conditionStr = td.attr(GenTableStrUtil.TdAttrName.DATA_VALUE);
                HashMap condition = gson.fromJson(conditionStr, HashMap.class);
                //根据数据信息获取当前时间对应的数据并填充
                ReportData data = conditionUtilImp.getData(condition, conditionUtilImp.getRptDatasByTime(condition, datas));
                if (data != null) {
                    String itemValue = data.getItemValue();
                    if (itemValue != null) {
                        td.text(itemValue);
                    }
                }
            }
        }
        return table;
    }

    /**
     * 替换表样中的时间
     *
     * @param table    表样
     * @param timePojo 传入的最新时间
     * @return 替换后的表样
     */
    public static Element fillTime(Element table, TimePojo timePojo) {
        //匹配所有数据域
        Elements dataTds = table.getElementsByAttributeValue(GenTableStrUtil.TdAttrName.TYPE, Constant.TdEsiType.DATA);
        if (dataTds.size() == 0) {//固定时间直接返回,不需要填
            return table;
        }
        //是否为年
        boolean isYear = timePojo.getFrequency() == Constant.PeriodType.YEAR;
        //填充数据域
        for (Element dataTd : dataTds) {
            Map data = gson.fromJson(dataTd.attr(GenTableStrUtil.TdAttrName.DATA_VALUE), HashMap.class);

//            String year = (String) data.get(GenTableStrUtil.ProAttrName.YEAR);
//            String time = (String) data.get(GenTableStrUtil.ProAttrName.TIME);
            String time = MapUtils.getString(data, GenTableStrUtil.ProAttrName.TIME);
            String year = MapUtils.getString(data, GenTableStrUtil.ProAttrName.YEAR);
            //延期后的时间
            TimePojo periodSpanPojo = new TimePojo(timePojo, getPeriodSpan(isYear ? year : time));
            data.put(GenTableStrUtil.ProAttrName.YEAR, periodSpanPojo.getYear());
            data.put(GenTableStrUtil.ProAttrName.TIME, periodSpanPojo.getPeriod());
            dataTd.attr(GenTableStrUtil.TdAttrName.DATA_VALUE, gson.toJson(data).replaceAll("\"", "'"));
//            dataTd.text(periodSpanPojo.getYear() + "," + periodSpanPojo.getPeriod());
        }
        //填充主宾蓝  获取到的年和期度的单元格数量应为一致,直接按序号替换即可
        Elements yearTds = table.getElementsMatchingOwnText("\\$y(-\\d+)?\\$");
        if (isYear) {//根据年从单元格中获取
            if (ListUtil.notEmpty(yearTds)) {
                for (Element yearTd : yearTds) {
                    //设置年份
                    String tdStr = yearTd.text();
                    yearTd.text(getPreTimeStr(tdStr) + (timePojo.getYear() - getPeriodSpan(tdStr)) + getSuffixStr(tdStr));
                }
            }
        } else {//根据月份设置月份和年份
            Elements periodTds = table.getElementsMatchingOwnText("\\$m(-\\d+)?\\$");
            int index = 0;
            if (ListUtil.notEmpty(periodTds)) {
                for (Element periodTd : periodTds) {
                    //延期后的时间
                    String tdStr = periodTd.text();
                    TimePojo periodSpanPojo = new TimePojo(timePojo, getPeriodSpan(tdStr));
                    //设置月份
                    String frequencyName = Constant.FrequencyType.getFrequencyName(periodSpanPojo.getFrequency(), periodSpanPojo.getPeriod());
                    periodTd.text(getPreTimeStr(tdStr) + frequencyName + getSuffixStr(tdStr));
                    //设置年份
                    yearTds.get(index++).text(periodSpanPojo.getYear() + "");
                }
            }
        }
        //迭代年份单元格,合并单元格
        if (dataTds.size() >= 2) {
            if (ListUtil.notEmpty(yearTds)) {
                //合并年份单元格
                String esiType = yearTds.get(0).attr(GenTableStrUtil.TdAttrName.TYPE);
                //获取rowspan或colspan的key
                String spanKey = esiType.equals(Constant.TdEsiType.MAIN_BAR) ? GenTableStrUtil.TdAttrName.ROWSPAN : GenTableStrUtil.TdAttrName.COLSPAN;
                Iterator<Element> iterator = yearTds.iterator();
                //迭代前一项
                Element yearTd = iterator.next();
                String yearStr = yearTd.text();
                int span = Integer.parseInt(yearTd.attr(spanKey));
                while (iterator.hasNext()) {
                    Element nextYearTd = iterator.next();
                    String nextYearStr = nextYearTd.text();
                    if (yearStr.equals(nextYearStr)) {//合并单元格
                        int nextSpan = Integer.parseInt(nextYearTd.attr(spanKey));
                        nextYearTd.remove();
                        span += nextSpan;
                        yearTd.attr(spanKey, span + "");
                    } else {//将当前单元格设为前项
                        yearTd = nextYearTd;
                        yearStr = nextYearStr;
                        span = Integer.parseInt(yearTd.attr(spanKey));
                    }
                }
            }
        }
        return table;
    }

    /**
     * 获取报告期数
     *
     * @param holderStr 占位符 $y-i$,或$m-i},为数字
     */
    private static int getPeriodSpan(String holderStr) {
        int periodSpan = 0;
        if (holderStr != null) {
            int begin = holderStr.indexOf("$");
            int end = holderStr.lastIndexOf("$");
            if (end - begin >= 4) {//$m-1$
                periodSpan = Integer.parseInt(holderStr.substring(begin + 3, end));
            }
        }
        return periodSpan;
    }

    /**
     * 时间前缀
     */
    private static String getPreTimeStr(String holderStr) {
        String str = "";
        if (holderStr != null) {
            int begin = holderStr.indexOf("$");
            if (begin > 0) {
                str = holderStr.substring(0, begin);
            }
        }
        return str;
    }

    /**
     * 时间后缀
     */
    private static String getSuffixStr(String holderStr) {
        String str = "";
        if (holderStr != null) {
            int end = holderStr.lastIndexOf("$");
            if (end < holderStr.length() - 1) {
                str = holderStr.substring(end + 1);
            }
        }
        return str;
    }
}
