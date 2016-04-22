package com.city.support.manage.item.entity;

import com.city.support.manage.timeFrame.entity.TimeFrame;
import com.city.support.manage.unit.entity.DataType;
import com.city.support.manage.unit.entity.Unit;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by Administrator on 2016/1/5 0005.
 * 指标的附加时间框架,单位
 */
@Entity
@Table(name = "SPT_MGR_ITEMINFO", uniqueConstraints = @UniqueConstraint(columnNames = {"ITEM_ID", "TIME_FRAME_ID"}))
public class ItemInfo implements Serializable {
    /**
     * 主键
     */
    private Integer id;
    /**
     * 指标id
     */
    private Integer itemId;
    /**
     * 名称
     */
    private String name;
    /**
     * 单位
     */
    private Unit unit;
    /**
     * 时间框架
     */
    private TimeFrame timeFrame;
    /**
     * 数据类型
     */
    private DataType dataType;
    /**
     * 数据格式
     */
    private String dataFormat;

    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "itemInfoGen", sequenceName = "SPT_MGR_ITEMINFO_SEQ")
    @GeneratedValue(generator = "itemInfoGen")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "ITEM_ID")
    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    @Column(name = "NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @OneToOne
    @JoinColumn(name = "UNIT_ID")
    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    @OneToOne
    @JoinColumn(name = "TIME_FRAME_ID")
    public TimeFrame getTimeFrame() {
        return timeFrame;
    }

    public void setTimeFrame(TimeFrame timeFrame) {
        this.timeFrame = timeFrame;
    }

    @OneToOne
    @JoinColumn(name = "DATA_TYPE_ID")
    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    @Column(name = "DATA_FORMAT")
    public String getDataFormat() {
        return dataFormat;
    }

    public void setDataFormat(String dataFormat) {
        this.dataFormat = dataFormat;
    }
}
