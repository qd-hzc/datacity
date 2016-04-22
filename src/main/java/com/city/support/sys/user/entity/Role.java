package com.city.support.sys.user.entity;

import com.city.support.sys.module.entity.Module;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * 角色表
 */
@Entity
@Table(name = "SPT_SYS_ROLE")
public class Role {
    public enum State{
        ENABLE,
        DISABLE
    }

    /**
     * 角色id
     */
    private Integer id;
    /**
     * 角色名称
     */
    private String name;
    /**
     * 角色备注
     */
    private String comments;
    /**
     * 角色状态
     */
    private State state = State.ENABLE;

    private List<Permission> permissions;

    private List<Module> modules;

    private User createUser;

    private Date createDate = new Date();

    @Id
    @GeneratedValue(generator = "na")
    @SequenceGenerator(name = "na", sequenceName = "ROLE_SEQ")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "NAME", length = 1000)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "COMMENTS", length = 1000)
    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "STATE")
    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    @ManyToMany(targetEntity = Permission.class, fetch = FetchType.EAGER)
    @JoinTable(name = "SPT_SYS_ROLE_PERMISSION",joinColumns = @JoinColumn(name = "ROLE_ID"),inverseJoinColumns = @JoinColumn(name="PERMISSION_ID"))
    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }

    @ManyToMany(targetEntity = Module.class, fetch = FetchType.EAGER)
    @JoinTable(name = "SPT_SYS_ROLE_MODULE",joinColumns = @JoinColumn(name = "ROLE_ID"),inverseJoinColumns = @JoinColumn(name="MODULE_ID"))
    public List<Module> getModules() {
        return modules;
    }

    public void setModules(List<Module> modules) {
        this.modules = modules;
    }

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "USER_ID")
    public User getCreateUser() {
        return createUser;
    }

    public void setCreateUser(User createUser) {
        this.createUser = createUser;
    }


    @Temporal(TemporalType.DATE)
    @Column(name = "CREATE_DATE")
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
