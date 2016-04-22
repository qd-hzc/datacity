package com.city.resourcecategory.analysis.common.entity;

import com.city.common.pojo.Constant;

import javax.persistence.*;
import java.util.Date;

/**
 * 自定义查询：时间范围类
 * Created by HZC on 2016/2/29.
 */
@Entity
@Table(name = "RC_COMMON_TIME_RANGE")
public class TimeRangeEntity {

    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "RC_COMMON_TIME_RANGE_GEN", sequenceName = "RC_COMMON_TIME_RANGE_SEQ")
    @GeneratedValue(generator = "RC_COMMON_TIME_RANGE_GEN")
    private Integer id;
    //    外键：报表分析外键；图表分析外键
    @Column(name = "FOREIGN_ID")
    private Integer foreignId;
    //    关联类型：1：报表类型，2：图表类型
    @Column(name = "FOREIGN_TYPE")
    private Integer foreignType;

    //    时间范围类型
//    Constant.TIMERANGE.LIANXU
//    Constant.TIMERANGE.XUANZE
//    Constant.TIMERANGE.BAOGAOQI
    @Column(name = "TYPE")
    private Integer type;

    //    数据类型
    //DATA_TYPE
    //        连续：开始年
//    public static final Integer DATA_BEGIN_YEAR = 1;
//    //        连续：开始期度
//    public static final Integer DATA_BEGIN_PERIOD = 2;
//    //        连续：结束年
//    public static final Integer DATA_END_YEAR = 3;
//    //        连续：结束期度
//    public static final Integer DATA_END_PERIOD = 4;
//    //        选择：年份
//    public static final Integer DATA_YEAR = 5;
//    //        选择：期度
//    public static final Integer DATA_PERIOD = 6;
//    //        报告期数
//    public static final Integer DATA_NUMBER = 7;
    @Column(name = "DATA_TYPE")
    private Integer dataType;
    //    数据值
    @Column(name = "DATA_VALUE")
    private Integer dataValue;
    //    状态
    @Column(name = "STATUS")
    private Integer status;

    @Column(name = "CREATE_DATE")
    private Date createDate;
    @Column(name = "CREATE_ID")
    private Integer creatorId;
    @Column(name = "UPDATE_DATE")
    private Date updateDate;
    @Column(name = "UPDATE_ID")
    private Integer updaterId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getForeignId() {
        return foreignId;
    }

    public void setForeignId(Integer foreignId) {
        this.foreignId = foreignId;
    }

    public Integer getForeignType() {
        return foreignType;
    }

    public void setForeignType(Integer foreignType) {
        this.foreignType = foreignType;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getDataType() {
        return dataType;
    }

    public void setDataType(Integer dataType) {
        this.dataType = dataType;
    }

    public Integer getDataValue() {
        return dataValue;
    }

    public void setDataValue(Integer dataValue) {
        this.dataValue = dataValue;
    }

    /**
     * Getter for property 'status'.
     *
     * @return Value for property 'status'.
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * Setter for property 'status'.
     *
     * @param status Value to set for property 'status'.
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * Getter for property 'createDate'.
     *
     * @return Value for property 'createDate'.
     */
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * Setter for property 'createDate'.
     *
     * @param createDate Value to set for property 'createDate'.
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    /**
     * Getter for property 'creatorId'.
     *
     * @return Value for property 'creatorId'.
     */
    public Integer getCreatorId() {
        return creatorId;
    }

    /**
     * Setter for property 'creatorId'.
     *
     * @param creatorId Value to set for property 'creatorId'.
     */
    public void setCreatorId(Integer creatorId) {
        this.creatorId = creatorId;
    }

    /**
     * Getter for property 'updateDate'.
     *
     * @return Value for property 'updateDate'.
     */
    public Date getUpdateDate() {
        return updateDate;
    }

    /**
     * Setter for property 'updateDate'.
     *
     * @param updateDate Value to set for property 'updateDate'.
     */
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    /**
     * Getter for property 'updaterId'.
     *
     * @return Value for property 'updaterId'.
     */
    public Integer getUpdaterId() {
        return updaterId;
    }

    /**
     * Setter for property 'updaterId'.
     *
     * @param updaterId Value to set for property 'updaterId'.
     */
    public void setUpdaterId(Integer updaterId) {
        this.updaterId = updaterId;
    }
}
