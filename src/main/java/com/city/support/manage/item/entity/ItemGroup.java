package com.city.support.manage.item.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2015/12/29 0029.
 * 指标体系
 */
@Entity
@Table(name = "SPT_MGR_ITEMGROUP", uniqueConstraints = @UniqueConstraint(columnNames = {"PARENT_ID", "NAME"}))
public class ItemGroup implements Serializable {
    /**
     * 主键
     */
    private Integer id;
    /**
     * 分组名
     */
    private String name;
    /**
     * 父id
     */
    private Integer parentId;
    /**
     * 分组状态,废弃(0)或启用(1)
     */
    private Integer status;
    /**
     * 备注
     */
    private String comments;
    /**
     * 排序
     */
    private Integer sortIndex;
    /**
     * 分组内容
     */
    private List<ItemGroupInfo> groupInfos;

    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "itemGroupGen", sequenceName = "SPT_MGR_ITEMGROUP_SEQ")
    @GeneratedValue(generator = "itemGroupGen")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "NAME", nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "PARENT_ID", nullable = false)
    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
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

    @Column(name = "SORT_INDEX")
    public Integer getSortIndex() {
        return sortIndex;
    }

    public void setSortIndex(Integer sortIndex) {
        this.sortIndex = sortIndex;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_ID")
    @OrderBy("sortIndex")
    public List<ItemGroupInfo> getGroupInfos() {
        return groupInfos;
    }

    public void setGroupInfos(List<ItemGroupInfo> groupInfos) {
        this.groupInfos = groupInfos;
    }
}
