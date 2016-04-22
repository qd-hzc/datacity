package com.city.support.sys.user.entity;

import com.city.support.sys.module.entity.Module;
import com.city.support.sys.user.entity.Role;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;

/**
 * Created by wys on 2015/12/30.
 * 删除角色或模块时注意维护这个实体，因为是单向的问题所以没有级联删除
 */
@Entity
@Table(name = "SPT_SYS_ROLE_MODULE")
public class RoleModule {
    private Integer id;
    private Role role;
    private Module module;

    @Id
    @GeneratedValue(generator = "na")
    @GenericGenerator(name = "na", strategy = "native", parameters = {@Parameter(name = "sequence", value = "MODULE_SEQ")})
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "MODULE_ID")
    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    @ManyToOne
    @JoinColumn(name = "ROLE_ID")
    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
