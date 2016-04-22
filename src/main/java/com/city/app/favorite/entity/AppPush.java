package com.city.app.favorite.entity;

import com.city.app.staffValid.entity.AppPerson;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Administrator on 2016/4/18.
 */
@Entity
@Table(name="APP_PUSH")
public class AppPush {
    @Id
    @Column
    @SequenceGenerator(name = "appPush", sequenceName = "APP_PUSH_SEQ")
    @GeneratedValue(generator = "appPush")
    private Integer id;
    //Ãû³Æ
    @Column
    private String name;
    @Column
    private Integer menuId;
    @Column
    private Integer userId;
    @Column
    private Integer receiver;
    @Column
    private Date time;
    @Column
    private Integer  flag;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String id) {
        this.name = name;
    }

    public Integer getMenuId() {
        return menuId;
    }

    public void setMenuId(Integer menuId) {
        this.menuId = menuId;
    }

    public Integer getUserId(){
        return userId;
    }

    public void setUserId(Integer userId){
        this.userId=userId;
    }

    public Integer getReceiver(){
        return receiver;
    }

    public void setReceiver(Integer receiver){
        this.receiver=receiver;
    }

    public Date getTime(){
        return time;
    }

    public void setTime(Date time){
        this.time=time;
    }

    public Integer getFlag(){
        return flag;
    }

    public void setFlag(Integer flag){
        this.flag=flag;
    }




}
