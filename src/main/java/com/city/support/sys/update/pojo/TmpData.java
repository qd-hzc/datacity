package com.city.support.sys.update.pojo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/9.
 */
public class TmpData {
    private Integer tmpOldId;
    private String name;
    private List<RptData> rpts = new ArrayList();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<RptData> getRpts() {
        return rpts;
    }

    public void setRpts(List<RptData> rpts) {
        this.rpts = rpts;
    }

    public Integer getTmpOldId() {
        return tmpOldId;
    }

    public void setTmpOldId(Integer tmpOldId) {
        this.tmpOldId = tmpOldId;
    }
}
