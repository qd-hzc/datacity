package com.city.support.manage.item.entity;

import com.city.support.sys.user.entity.Department;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by Administrator on 2015/12/29 0029.
 * 指标分组内容
 */
@Entity
@Table(name = "SPT_MGR_ITEMGROUPINFO", uniqueConstraints = @UniqueConstraint(columnNames = {"GROUP_ID", "ITEM_ID"}))
public class ItemGroupInfo implements Serializable {
    /**
     * 主键
     */
    private Integer id;
    /**
     * 分组(指标体系)id
     */
    private Integer groupId;
    /**
     * 指标
     */
    private Item item;
    /**
     * 指标显示名,默认为指标名,可修改
     */
    private String itemName;
    /**
     * 分组名
     */
    private String groupName;
    /**
     * 指标状态,当源数据的状态为启用时,该状态可任意修改,否则只能为废弃状态
     */
    private Integer status;
    /**
     * 备注
     */
    private String comments;
    /**
     * 口径,默认为源数据的默认口径,可修改
     */
    private Integer caliberId;
    /**
     * 部门,默认为指标的默认部门,可修改
     */
    private Department department;
    /**
     * 排序
     */
    private Integer sortIndex;

    /**
     * 构造
     */
    public ItemGroupInfo() {
    }

    /**
     * 通过指标构造
     *
     * @param item
     */
    public ItemGroupInfo(Item item) {
        this.caliberId = item.getCaliberId();
        this.department = item.getDepartment();
        this.itemName = item.getName();
        this.status = item.getStatus();
        this.item = item;
    }

    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "itemGroupInfoGen", sequenceName = "SPT_MGR_ITEMGROUPINFO_SEQ")
    @GeneratedValue(generator = "itemGroupInfoGen")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "GROUP_ID")
    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    @OneToOne
    @JoinColumn(referencedColumnName = "ID", name = "ITEM_ID", nullable = false)
    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    @Column(name = "ITEM_NAME")
    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    @Column(name = "GROUP_NAME")
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Column(name = "STATUS", nullable = false)
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Column(name = "COMMENTS", length = 500)
    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @Column(name = "CALIBER_ID")
    public Integer getCaliberId() {
        return caliberId;
    }

    public void setCaliberId(Integer caliberId) {
        this.caliberId = caliberId;
    }

    @OneToOne
    @JoinColumn(name = "DEP_ID")
    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    @Column(name = "SORT_INDEX")
    public Integer getSortIndex() {
        return sortIndex;
    }

    public void setSortIndex(Integer sortIndex) {
        this.sortIndex = sortIndex;
    }
}
