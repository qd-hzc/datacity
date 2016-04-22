package com.city.common.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2015/12/30 0030.
 * 分页
 */
public class Page implements Serializable {
    private Integer page;
    private Integer start;
    private Integer limit;
    private Integer total;
    private List datas;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List getDatas() {
        return datas;
    }

    public void setDatas(List datas) {
        this.datas = datas;
    }
}
