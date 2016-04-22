package com.city.support.manage.item.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by Administrator on 2015/12/28 0028.
 * 源数据管理系统 指标口径
 */
@Entity
@Table(name = "SPT_MGR_ITEMCALIBER")
public class ItemCaliber implements Serializable {
    /**
     * 主键
     */
    private Integer id;
    /**
     * 指标id,外键
     */
    private Integer itemId;
    /**
     * 口径名
     */
    private String name;
    /**
     * 指标解释
     */
    private String itemExplain;
    /**
     * 统计范围
     */
    private String statisticsScope;
    /**
     * 统计方法
     */
    private String statisticsMethod;
    /**
     * 计算方法
     */
    private String countMethod;
    /**
     * 排序
     */
    private Integer sortIndex;

    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "itemCaliberGen", sequenceName = "SPT_MGR_ITEMCALIBER_SEQ")
    @GeneratedValue(generator = "itemCaliberGen")
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

    @Column(name = "NAME", nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "ITEM_EXPLAIN", length = 500)
    public String getItemExplain() {
        return itemExplain;
    }

    public void setItemExplain(String itemExplain) {
        this.itemExplain = itemExplain;
    }

    @Column(name = "STATISTICS_SCOPE", length = 500)
    public String getStatisticsScope() {
        return statisticsScope;
    }

    public void setStatisticsScope(String statisticsScope) {
        this.statisticsScope = statisticsScope;
    }

    @Column(name = "STATISTICS_METHOD", length = 500)
    public String getStatisticsMethod() {
        return statisticsMethod;
    }

    public void setStatisticsMethod(String statisticsMethod) {
        this.statisticsMethod = statisticsMethod;
    }

    @Column(name = "COUNT_METHOD", length = 500)
    public String getCountMethod() {
        return countMethod;
    }

    public void setCountMethod(String countMethod) {
        this.countMethod = countMethod;
    }

    @Column(name = "SORT_INDEX")
    public Integer getSortIndex() {
        return sortIndex;
    }

    public void setSortIndex(Integer sortIndex) {
        this.sortIndex = sortIndex;
    }
}
