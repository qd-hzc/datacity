package com.city.common.util.table.pojo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by wxl on 2016/1/27 0027.
 * 表格的主宾蓝信息
 */
public class EsiTable implements Serializable {
    private List<EsiNode> mainBar;//主栏信息
    private List<EsiNode> guestBar;//宾栏信息
    private Map<String, EsiTdUnit> unitSelector;//单位选择器
    private Integer tmpType;//报表id
    private Integer tmpId;//报表模板id

    public List<EsiNode> getMainBar() {
        return mainBar;
    }

    public void setMainBar(List<EsiNode> mainBar) {
        this.mainBar = mainBar;
    }

    public List<EsiNode> getGuestBar() {
        return guestBar;
    }

    public void setGuestBar(List<EsiNode> guestBar) {
        this.guestBar = guestBar;
    }

    public Map<String, EsiTdUnit> getUnitSelector() {
        return unitSelector;
    }

    public void setUnitSelector(Map<String, EsiTdUnit> unitSelector) {
        this.unitSelector = unitSelector;
    }

    public Integer getTmpType() {
        return tmpType;
    }

    public void setTmpType(Integer tmpType) {
        this.tmpType = tmpType;
    }

    public Integer getTmpId() {
        return tmpId;
    }

    public void setTmpId(Integer tmpId) {
        this.tmpId = tmpId;
    }
}
