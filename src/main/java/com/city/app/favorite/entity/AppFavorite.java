package com.city.app.favorite.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by wxl on 2016/4/8.
 * 手机端 订阅收藏
 */
@Entity
@Table(name = "APP_FAVORITE")
public class AppFavorite {
    public static final int TYPE_PUSH = 1;//推送
    public static final int TYPE_DESCRIBE = 2;//订阅

    private Integer id;
    private Integer menuId;//
    private Integer rootId;//模块的menuId
    private Integer year;//订阅内容的时间:年
    private Integer period;//订阅内容的时间:月(季等)
    private Date orderDate;//订阅的时间
    private Integer orderType;//类型,订阅或推送
    private Integer sender;//推送(订阅)人
    private Integer receiver;//接收人 若为订阅,与订阅人一致即可

    public AppFavorite() {
    }

    @Id
    @Column
    @SequenceGenerator(name = "appFavoriteGen", sequenceName = "APP_FAVORITE_SEQ")
    @GeneratedValue(generator = "appFavoriteGen")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "MENU_ID")
    public Integer getMenuId() {
        return menuId;
    }

    public void setMenuId(Integer menuId) {
        this.menuId = menuId;
    }

    @Column(name = "ROOT_ID")
    public Integer getRootId() {
        return rootId;
    }

    public void setRootId(Integer rootId) {
        this.rootId = rootId;
    }

    @Column
    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    @Column
    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    @Column(name = "ORDER_DATE")
    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    @Column(name = "ORDER_TYPE")
    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    @Column
    public Integer getSender() {
        return sender;
    }

    public void setSender(Integer sender) {
        this.sender = sender;
    }

    @Column
    public Integer getReceiver() {
        return receiver;
    }

    public void setReceiver(Integer receiver) {
        this.receiver = receiver;
    }
}
