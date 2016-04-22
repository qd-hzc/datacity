package com.city.support.sys.user.entity;

import javax.persistence.*;

/**
 * Created by zhoutao on 2016/3/8.
 */
@Entity
@Table(name="SPT_SYS_USER_RPT_PERMISSION")
public class UserRptPermission {
    private Integer id;
    private User user;
    private RptPermission rptPermission;

    @Id
    @SequenceGenerator(name = "userRptPermGenerator", sequenceName = "SPT_SYS_USER_RPT_PERM_SEQ")
    @GeneratedValue(generator = "userRptPermGenerator")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "USER_ID")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @ManyToOne(targetEntity = RptPermission.class)
    @JoinColumn(name="PERM_ID")
    public RptPermission getRptPermission() {
        return rptPermission;
    }

    public void setRptPermission(RptPermission rptPermission) {
        this.rptPermission = rptPermission;
    }
}
