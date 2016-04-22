package com.city.support.regime.collection.entity;

import com.city.support.sys.user.entity.Role;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by wys on 2016/2/3.
 */
@Entity
@Table(name = "SPT_RGM_RPT_IMPORTRULE")
public class ImportRule {
    private Integer id;
    private String ruleName;
    private Set<Role> roles = new HashSet<>();
    private Set<ExcelMap> excelMaps = new HashSet<>();

    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "importRuleGen", sequenceName = "SPT_RGM_RPT_IMPORTRULE_SEQ")
    @GeneratedValue(generator = "importRuleGen")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "RULENAME")
    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinTable(
            name = "SPT_RGM_IMPORTRULE_EXCELMAP",
            joinColumns = @JoinColumn(name = "RULE_ID"),
            inverseJoinColumns = @JoinColumn(name = "EXCELMAP_ID")
    )
    public Set<ExcelMap> getExcelMaps() {
        return excelMaps;
    }

    public void setExcelMaps(Set<ExcelMap> excelMaps) {
        this.excelMaps = excelMaps;
    }

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "SPT_RGM_IMPORTRULE_ROLE",
            joinColumns = @JoinColumn(name = "RULE_ID"),
            inverseJoinColumns = @JoinColumn(name = "ROLE_ID")
    )
    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
