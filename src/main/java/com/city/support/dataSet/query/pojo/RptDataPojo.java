package com.city.support.dataSet.query.pojo;

import com.city.support.regime.collection.entity.ReportData;

import java.util.List;

/**
 * Created by wxl on 2016/3/5.
 * 返回数据格式
 */
public class RptDataPojo {
    //时间
    private TimePojo time;
    //数据
    private List<ReportData> datas;

    public RptDataPojo() {
    }

    public RptDataPojo(TimePojo time, List<ReportData> datas) {
        this.time = time;
        this.datas = datas;
    }

    public TimePojo getTime() {
        return time;
    }

    public void setTime(TimePojo time) {
        this.time = time;
    }

    public List<ReportData> getDatas() {
        return datas;
    }

    public void setDatas(List<ReportData> datas) {
        this.datas = datas;
    }
}
