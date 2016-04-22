package com.city.app.dataDict.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by wxl on 2016/3/21.
 * 数据字典
 */
@Entity
@Table(name = "APP_DATA_DICT")
public class AppDataDict implements Serializable {
    @Id
    @Column
    @SequenceGenerator(name = "appDataDictGen", sequenceName = "APP_DATA_DICT_SEQ")
    @GeneratedValue(generator = "appDataDictGen")
    private Integer id;
    @Column(name = "MENU_ID")
    private Integer menuId;//所属目录id
    @Column(name = "DATA_TYPE")
    private Integer dataType;//数据类型
    @Column(name = "DATA_NAME")
    private String dataName;
    @Column(name = "DATA_VALUE")
    private Integer dataValue;
    @Column(name = "GROUP_NAME")
    private String groupName;//分组名,手动输入即可
    @Column(name = "DISPLAY_TYPE")
    private Integer displayType;//展示类型,默认图表展示
    @Column(name = "SORT_INDEX")
    private Integer sortIndex;//排序
    @Column
    private Integer status;//是否显示

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMenuId() {
        return menuId;
    }

    public void setMenuId(Integer menuId) {
        this.menuId = menuId;
    }

    public Integer getDataType() {
        return dataType;
    }

    public void setDataType(Integer dataType) {
        this.dataType = dataType;
    }

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public Integer getDataValue() {
        return dataValue;
    }

    public void setDataValue(Integer dataValue) {
        this.dataValue = dataValue;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Integer getDisplayType() {
        return displayType;
    }

    public void setDisplayType(Integer displayType) {
        this.displayType = displayType;
    }

    public Integer getSortIndex() {
        return sortIndex;
    }

    public void setSortIndex(Integer sortIndex) {
        this.sortIndex = sortIndex;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
