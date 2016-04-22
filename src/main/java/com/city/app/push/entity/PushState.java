package com.city.app.push.entity;

import javax.persistence.*;

/**
 * Created by zhoutao on 2016/4/19.
 */
@Table(name = "APP_Push_State")
@Entity
public class PushState {
    private Integer id;
    private Integer appUserId;
    private boolean pushState = true;//true :新的推送消息 false:旧的推送消息

    @Id
    @SequenceGenerator(name = "pushStateGenerator", sequenceName = "APP_PUSHSTATE_SEQ")
    @GeneratedValue(generator = "pushStateGenerator")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "APPUSERID")
    public Integer getAppUserId() {
        return appUserId;
    }

    public void setAppUserId(Integer appUserId) {
        this.appUserId = appUserId;
    }

    @Column(name = "PUSHSTATE")
    public boolean isPushState() {
        return pushState;
    }

    public void setPushState(boolean pushState) {
        this.pushState = pushState;
    }
}
