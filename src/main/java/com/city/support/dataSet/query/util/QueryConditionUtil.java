package com.city.support.dataSet.query.util;

import com.city.common.pojo.Constant;
import com.city.common.util.EsiLogUtil;
import com.city.common.util.ListUtil;
import com.city.support.dataSet.entity.DataSetData;
import com.city.support.dataSet.query.pojo.RptDataPojo;
import com.city.support.dataSet.query.pojo.TimePojo;
import com.city.support.regime.collection.entity.ReportData;

import java.util.*;

/**
 * Created by wxl on 2016/3/3.
 * 查询条件
 */
public class QueryConditionUtil {

    /**
     * 拼接数据集条件
     * 解析数据及数据,将其转换成 Map<Integer, List<DataSetData>>,以dataType为key
     *
     * @param conditions 查询条件
     */
    public static Map<Integer, List<DataSetData>> formatCondition(List<DataSetData> conditions, List<Integer> items) {
        Map<Integer, List<DataSetData>> result = new HashMap<>();
        List<DataSetData> metaTypeDatas = null;
        if (conditions != null && conditions.size() > 0) {
            for (DataSetData data : conditions) {
                Integer dataType = data.getDataType();
                metaTypeDatas = result.get(dataType);
                if (metaTypeDatas == null) {
                    metaTypeDatas = new ArrayList<>();
                }
                metaTypeDatas.add(data);
                result.put(dataType, metaTypeDatas);
            }
        }
        //筛选指标
        if (items != null && items.size() > 0) {
            List<DataSetData> itemConds = result.get(Constant.MetadataType.ITEM);
            if (itemConds != null && itemConds.size() > 0) {
                Iterator<DataSetData> iterator = itemConds.iterator();
                while (iterator.hasNext()) {
                    DataSetData itemCond = iterator.next();
                    if (!items.contains(Integer.parseInt(itemCond.getDataValue()))) {//不包含则移除掉
                        iterator.remove();
                    }
                }
            }
        }
        return result;
    }

    /**
     * 生成查询数据SQL
     *
     * @param conditionMap 解析后的数据集数据
     * @param times        时间条件,转换后的
     */
    public static String createRptDataSql(Map<Integer, List<DataSetData>> conditionMap, List<TimePojo> times) {
        StringBuilder queryCondition = createQueryCondition(conditionMap);
        queryCondition.insert(0, "select * from SPT_RGM_RPT_DATA where ");
        //添加时间条件
        if (times != null && times.size() > 0) {
            queryCondition.append(" and (1!=1");
            for (TimePojo time : times) {
                queryCondition.append(" or (").append("year=").append(time.getYear()).append(" and month=").append(time.getPeriod()).append(")");
            }
            queryCondition.append(")");
        }
        EsiLogUtil.debug(EsiLogUtil.getLogInstance(QueryConditionUtil.class), "\n查询SQL:" + queryCondition.toString());
        return queryCondition.toString();
    }

    /**
     * 查询时间sql
     *
     * @param conditionMap 解析后的数据集数据
     */
    public static String createRptTimeSql(Map<Integer, List<DataSetData>> conditionMap) {
        StringBuilder queryCondition = createQueryCondition(conditionMap);
        queryCondition.insert(0, "select distinct year,month from SPT_RGM_RPT_DATA where ").append(" order by year desc,month desc");
        EsiLogUtil.debug(EsiLogUtil.getLogInstance(QueryConditionUtil.class), "\n查询时间SQL:" + queryCondition.toString());
        return queryCondition.toString();
    }

    /**
     * 获取查询条件
     *
     * @param conditionMap
     * @return
     */
    private static StringBuilder createQueryCondition(Map<Integer, List<DataSetData>> conditionMap) {
        List<DataSetData> items = conditionMap.get(Constant.MetadataType.ITEM), tfs = conditionMap.get(Constant.MetadataType.TIME_FRAME),
                researchObjs = conditionMap.get(Constant.MetadataType.RESEARCH_OBJ), itemMenus = conditionMap.get(Constant.MetadataType.ITEM_MENU);
        StringBuilder sb = new StringBuilder("DATASTATUS=1 and");
        //设置指标条件
        sb.append(" (1!=1");
        if (items != null && items.size() > 0) {
            for (DataSetData item : items) {
                sb.append(" or (ITEM =").append(item.getDataValue());
                if (item.getDataInfo1() != null) {//口径
                    sb.append(" and ITEMCALIBER=").append(item.getDataInfo1());
                }
                if (item.getDataInfo2() != null) {//部门
                    sb.append(" and DEPID=").append(item.getDataInfo2());
                }
                if (item.getDataInfo3() != null) {//报表
                    sb.append(" and RPT_TMP_ID=").append(item.getDataInfo3());
                }
                sb.append(")");
            }
        }
        sb.append(")");
        //设置时间框架条件
        sb.append(" and (1!=1");
        if (tfs != null && tfs.size() > 0) {
            for (DataSetData tf : tfs) {
                sb.append(" or TIMEFRAME=").append(tf.getDataValue());
            }
        }
        sb.append(")");
        //设置统计对象条件
        if (ListUtil.notEmpty(researchObjs)) {
            sb.append(" and (1！=1 ");
            for (DataSetData obj : researchObjs) {
                sb.append(" or (SUROBJ=").append(obj.getDataValue()).append(" and SUROBJTYPE=").append(obj.getDataInfo1()).append(")");
            }
        } else {
            sb.append(" and (AREAID=").append(Constant.systemConfigPojo.getDefaultAreaId());
        }
        sb.append(")");
        //设置分组目录
        if (ListUtil.notEmpty(itemMenus)) {
            sb.append(" and (1！=1 ");
            for (DataSetData itemMenu : itemMenus) {
                sb.append(" or ITEMDICT='").append(itemMenu.getDataValue()).append("'");
            }
        } else {
            sb.append(" and (ITEMDICT='").append(Constant.DEFAULT_ITEM_MENU).append("'");
        }
        sb.append(")");
        return sb;
    }

    /**
     * 校验数据是否能查出数据
     *
     * @param conditionMap
     * @return
     */
    public static boolean checkConditionValid(Map<Integer, List<DataSetData>> conditionMap) {
        //指标数据
        List<DataSetData> items = conditionMap.get(Constant.MetadataType.ITEM);
        //时间框架数据
        List<DataSetData> tfs = conditionMap.get(Constant.MetadataType.TIME_FRAME);
        return items != null && items.size() > 0 && tfs != null && tfs.size() > 0;
    }

    /**
     * 将数据列表转为List<RptDataPojo>格式
     */
    public static List<RptDataPojo> convertRptDatas(List<ReportData> datas) {
        List<RptDataPojo> dataPojos = null;//转换后的数据格式
        if (datas != null && datas.size() > 0) {
            dataPojos = new ArrayList<>();
            Map<TimePojo, List<ReportData>> dataMap = new HashMap<>();//用于索引时间和数据
            List<ReportData> list = null;//时间对应的数据
            for (ReportData data : datas) {
                TimePojo time = new TimePojo(data.getReportDataId().getYear(), data.getReportDataId().getMonth());
                list = dataMap.get(time);
                if (list == null) {
                    list = new ArrayList<>();
                    dataMap.put(time, list);
                    dataPojos.add(new RptDataPojo(time, list));
                }
                list.add(data);
            }
            Collections.sort(dataPojos, new Comparator<RptDataPojo>() {
                @Override
                public int compare(RptDataPojo o1, RptDataPojo o2) {
                    return o1.getTime().compareTo(o2.getTime());
                }
            });
        }
        return dataPojos;
    }

    /**
     * 根据频度 过滤报告期
     *
     * @param times     所有时间
     * @param frequency 频度
     * @return 符合频度的报告期
     */
    public static List<TimePojo> filtTimes(List<TimePojo> times, int frequency) {
        List<TimePojo> ts = null;
        if (times != null && times.size() > 0) {
            ts = new ArrayList<>();
            for (TimePojo t : times) {
                if (Constant.PeriodType.isMatchPeriod(frequency, t.getPeriod())) {
                    ts.add(t);
                }
            }
        }
        return ts;
    }
}
