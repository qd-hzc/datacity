package com.city.support.manage.timeFrame.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by zhoutao on 2015/12/30.
 * 时间框架 表
 */
@Entity
@Table(name = "SPT_MGR_TIME_FRAME")
public class TimeFrame implements Serializable{

    private Integer id;     //id主键
    private String name;    //名称
    private String code;    //编码
    private String equation;//公式
    private Integer order;  //排序
    private String comments;//备注说明

    @Id
    @SequenceGenerator(name="timeGenerator",sequenceName = "TIME_SEQ")
    @GeneratedValue(generator = "timeGenerator")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name="NAME", unique = true, length = 200)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name="CODE", length = 100)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(name="EQUATION", length = 200)
    public String getEquation() {
        return equation;
    }

    public void setEquation(String equation) {
        this.equation = equation;
    }

    @Column(name="ORDERS")
    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    @Column(name="COMMENTS", length = 500)
    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
