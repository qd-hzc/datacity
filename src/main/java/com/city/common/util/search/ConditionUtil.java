package com.city.common.util.search;

import com.city.support.regime.collection.entity.ReportData;

import java.util.List;

/**
 * Created by wys on 2016/2/29.
 */
public interface ConditionUtil<T> {
    /**
     * @param conditionObj 查询对象
     * @param metaType     元数据类型
     * @param resultClass  返回值类型
     * @return 元数据
     */
    Object getCondition(T conditionObj, Integer metaType, Class resultClass);

    /**
     * @param conditionObj 查询对象
     * @param data         数据
     * @return 是否匹配
     */
    boolean match(T conditionObj, ReportData data);

    /**
     * @param conditionObj 查询对象
     * @param datas        数据
     * @return 匹配的数据List
     */
    List<ReportData> getDatas(T conditionObj, List<ReportData> datas);

    /**
     * @param conditionObj 查询对象
     * @param datas        数据
     * @return 匹配的数据
     */
    ReportData getData(T conditionObj, List<ReportData> datas);
}
