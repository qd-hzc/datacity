package com.city.support.manage.unit.entity;

import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Created by zhoutao on 2015/12/30.
 * 计量单位 表
 */
@Entity
@Table(name = "SPT_MGR_UNIT")
public class Unit implements Serializable{

    private Integer id;                         //id 主键
    private String name;                        //名称
    private boolean standard;                 //是否是基准单位
    private Unit standardUnit;                //基准单位id
    private String equivalentValue;           //跟基准单位换算值
    private boolean internationalStandard;  //是否是国际标准
    private DataType dataType;                  //数据类型
    private String dataFormat;                  //数据格式
    private Integer order;                      //排序
    private UnitType unitType;                  //单位类型
    private String comments;                    //备注说明

    @Id
    @SequenceGenerator(name = "unitGenerator",sequenceName = "UNIT_SEQ")
    @GeneratedValue(generator = "unitGenerator")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "NAME",length =200)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "STANDARD")
    public boolean isStandard() {
        return standard;
    }

    public void setStandard(boolean standard) {
        this.standard = standard;
    }

    @OneToOne(cascade= CascadeType.ALL, targetEntity = Unit.class)
    @JoinColumn(name = "STANDARD_ID")
    public Unit getStandardUnit() {
        return standardUnit;
    }

    public void setStandardUnit(Unit standardUnit) {
        this.standardUnit = standardUnit;
    }

    @Column(name = "EQUIVALENTVALUE", length = 100)
    public String getEquivalentValue() {
        return equivalentValue;
    }

    public void setEquivalentValue(String equivalentValue) {
        this.equivalentValue = equivalentValue;
    }

    @Column(name = "INTERNATIONALSTANDARD")
    public boolean isInternationalStandard() {
        return internationalStandard;
    }

    public void setInternationalStandard(boolean internationalStandard) {
        this.internationalStandard = internationalStandard;
    }

    @ManyToOne(targetEntity = DataType.class)
    @JoinColumn(name = "DATATYPE_ID")
    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    @Column(name = "DATAFORMAT", length =100 )
    public String getDataFormat() {
        return dataFormat;
    }

    public void setDataFormat(String dataFormat) {
        this.dataFormat = dataFormat;
    }

    @Column(name = "ORDERS")
    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    @ManyToOne(targetEntity = UnitType.class)
    @JoinColumn(name = "UNITTYPE_ID")
    public UnitType getUnitType() {
        return unitType;
    }

    public void setUnitType(UnitType unitType) {
        this.unitType = unitType;
    }

    @Column(name = "COMMENTS", length = 500)
    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
