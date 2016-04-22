package com.city.support.manage.item.entity;

import com.city.support.sys.user.entity.Department;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2015/12/28 0028.
 * 源数据管理系统 指标
 */
@Entity
@Table(name = "SPT_MGR_ITEM")
public class Item implements Serializable {
    /**
     * 主键
     */
    private Integer id;
    /**
     * 指标名
     */
    private String name;
    /**
     * 指标代码
     */
    private String code;
    /**
     * 指标类型,标准类型(1)或自用类型(0)
     */
    private Integer type;
    /**
     * 指标状态,废弃(0)或启用(1)
     */
    private Integer status;
    /**
     * 指标备注
     */
    private String comments;
    /**
     * 指标的默认部门id
     */
    private Department department;
    /**
     * 指标的默认口径id
     */
    private Integer caliberId;
    /**
     * 顺序
     */
    private Integer sortIndex;
    /**
     * 指标下的口径
     */
    private List<ItemCaliber> itemCalibers;
    /**
     * 指标下的时间框架和单位
     */
    private List<ItemInfo> itemInfos;

    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "itemGen", sequenceName = "SPT_MGR_ITEM_SEQ")
    @GeneratedValue(generator = "itemGen")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "NAME", nullable = false, unique = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "CODE")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(name = "TYPE", nullable = false)
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Column(name = "STATUS", nullable = false)
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Column(name = "COMMENTS", length = 1000)
    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @OneToOne
    @JoinColumn(name = "DEP_ID")
    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    @Column(name = "CALIBER_ID")
    public Integer getCaliberId() {
        return caliberId;
    }

    public void setCaliberId(Integer caliberId) {
        this.caliberId = caliberId;
    }

    @Column(name = "SORT_INDEX")
    public Integer getSortIndex() {
        return sortIndex;
    }

    public void setSortIndex(Integer sortIndex) {
        this.sortIndex = sortIndex;
    }

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "ITEM_ID")
    @OrderBy("sortIndex")
    public List<ItemCaliber> getItemCalibers() {
        return itemCalibers;
    }

    public void setItemCalibers(List<ItemCaliber> itemCalibers) {
        this.itemCalibers = itemCalibers;
    }

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "ITEM_ID")
    public List<ItemInfo> getItemInfos() {
        return itemInfos;
    }

    public void setItemInfos(List<ItemInfo> itemInfos) {
        this.itemInfos = itemInfos;
    }
}
