package com.city.app.staffValid.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by wxl on 2016/5/18.
 */
@Entity
@Table(name = "APP_LOGIN_STATUS")
public class AppLoginStatus implements Serializable {
    private Integer id;
    private String uuid;//移动端唯一标识
    private Date failTime;//登录失败 记录时间

    @Id
    @Column
    @SequenceGenerator(name = "appLoginStatus", sequenceName = "APP_LOGIN_STATUS_SEQ")
    @GeneratedValue(generator = "appLoginStatus")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Column
    public Date getFailTime() {
        return failTime;
    }

    public void setFailTime(Date failTime) {
        this.failTime = failTime;
    }
}
