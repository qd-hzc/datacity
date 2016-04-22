package com.city.support.manage.unit.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.*;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Set;

/**
 * Created by zhoutao on 2015/12/30.
 * 单位类型 表
 */
@Entity
@Table(name = "SPT_MGR_UNIT_TYPE")
public class UnitType implements Serializable {

    private Integer id;     //id 主键
    private String name;    //名称
    private String comments;//备注说明

    @Id
    @SequenceGenerator(name = "unitTypeGenerator",sequenceName = "UNITTYPE_SEQ")
    @GeneratedValue(generator = "unitTypeGenerator")
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

    @Column(name = "COMMENTS",length = 500)
    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

}
