package com.city.support.manage.unit.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by zhoutao on 2015/12/30.
 * 数据格式 表
 */
@Entity
@Table(name = "SPT_MGR_DATA_TYPE")
public class DataType implements Serializable {

    private Integer id;         //id
    private String name;        //名称
    private String dataFormat; //数据格式
    private String comments;    //备注说明

    @Id
    @SequenceGenerator(name = "datatTypeGenerator",sequenceName = "DATATYPE_SEQ")
    @GeneratedValue(generator = "datatTypeGenerator")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "NAME",length = 200)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "DATAFORMAT",length = 100)
    public String getDataFormat() {
        return dataFormat;
    }

    public void setDataFormat(String dataFormat) {
        this.dataFormat = dataFormat;
    }

    @Column(name = "COMMENTS",length = 500)
    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
