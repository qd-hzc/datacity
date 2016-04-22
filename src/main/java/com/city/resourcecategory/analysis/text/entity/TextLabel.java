package com.city.resourcecategory.analysis.text.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by wxl on 2016/3/14.
 * 标签表
 */
@Entity
@Table(name = "RC_TEXT_LABEL")
public class TextLabel implements Serializable {
    @Id
    @Column
    @SequenceGenerator(name = "rcTextLabel", sequenceName = "RC_TEXT_LABEL_SEQ")
    @GeneratedValue(generator = "rcTextLabel")
    private Integer id;
    @Column(unique = true)
    private String name;//标签
    @Column
    private Integer tier;//层级

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTier() {
        return tier;
    }

    public void setTier(Integer tier) {
        this.tier = tier;
    }
}
