package com.city.support.dataSet.pojo;

import java.io.Serializable;

/**
 * Created by wxl on 2016/2/23.
 * 数据集的数据传输实体
 */
public class DataSetInfoPojo implements Serializable {
    private Integer id;
    private String name;

    public DataSetInfoPojo(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
