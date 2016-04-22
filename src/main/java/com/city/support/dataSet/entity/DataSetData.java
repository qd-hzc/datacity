package com.city.support.dataSet.entity;

import javax.persistence.*;

/**
 * Created by wxl on 2016/2/22.
 * 数据集中的数据信息,即时间框架,统计对象,分组目录
 */
@Entity
@Table(name = "SPT_DATASET_DATA")
public class DataSetData {
    private Integer id;
    //数据集id
    private Integer dataSetId;
    private Integer dataType;
    private String dataName;
    private String dataValue;
    //若是统计对象类型,则dataInfo1表示统计对象类型
    //若是指标类型,则dataInfo1表示指标口径
    private Integer dataInfo1;
    //若是统计对象类型,则dataInfo1表示统计地区
    //若是指标类型,则dataInfo2表示部门
    private Integer dataInfo2;
    //若是指标类型,则dataInfo2表示所属报表
    private Integer dataInfo3;

    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "dataSetDataGen", sequenceName = "SPT_DATASET_DATA_SEQ")
    @GeneratedValue(generator = "dataSetDataGen")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "DATASET_ID", nullable = false)
    public Integer getDataSetId() {
        return dataSetId;
    }

    public void setDataSetId(Integer dataSetId) {
        this.dataSetId = dataSetId;
    }

    @Column(name = "DATA_TYPE", nullable = false)
    public Integer getDataType() {
        return dataType;
    }

    public void setDataType(Integer dataType) {
        this.dataType = dataType;
    }

    @Column(name = "DATA_NAME", nullable = false)
    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    @Column(name = "DATA_VALUE", nullable = false)
    public String getDataValue() {
        return dataValue;
    }

    public void setDataValue(String dataValue) {
        this.dataValue = dataValue;
    }

    @Column(name = "DATA_INFO1")
    public Integer getDataInfo1() {
        return dataInfo1;
    }

    public void setDataInfo1(Integer dataInfo1) {
        this.dataInfo1 = dataInfo1;
    }

    @Column(name = "DATA_INFO2")
    public Integer getDataInfo2() {
        return dataInfo2;
    }

    public void setDataInfo2(Integer dataInfo2) {
        this.dataInfo2 = dataInfo2;
    }

    @Column(name = "DATA_INFO3")
    public Integer getDataInfo3() {
        return dataInfo3;
    }

    public void setDataInfo3(Integer dataInfo3) {
        this.dataInfo3 = dataInfo3;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataSetData that = (DataSetData) o;

        if (!dataType.equals(that.dataType)) return false;
        if (!dataValue.equals(that.dataValue)) return false;
        if (dataInfo1 != null ? !dataInfo1.equals(that.dataInfo1) : that.dataInfo1 != null) return false;
        if (dataInfo2 != null ? !dataInfo2.equals(that.dataInfo2) : that.dataInfo2 != null) return false;
        return dataInfo3 != null ? dataInfo3.equals(that.dataInfo3) : that.dataInfo3 == null;

    }

    @Override
    public int hashCode() {
        int result = dataType.hashCode();
        result = 31 * result + dataValue.hashCode();
        result = 31 * result + (dataInfo1 != null ? dataInfo1.hashCode() : 0);
        result = 31 * result + (dataInfo2 != null ? dataInfo2.hashCode() : 0);
        result = 31 * result + (dataInfo3 != null ? dataInfo3.hashCode() : 0);
        return result;
    }
}
