package com.city.app.dataDict.icon.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by wxl on 2016/3/30.
 * 数据字典的图标
 */
@Entity
@Table(name = "APP_DATA_DICT_MENU_ICON")
public class AppDataDictMenuIcon implements Serializable {
    //相对于项目根路径下的图标路径前缀
    public static final String iconPreUrl = "/City/app/dataDict/images/icons/";
    //相对于项目根路径下的背景路径前缀
    public static final String bgPreUrl = "/City/app/dataDict/images/bgs/";
    @Id
    @Column
    @SequenceGenerator(name = "appDataDictMenuIconGen", sequenceName = "appDataDictMenuIconSeq")
    @GeneratedValue(generator = "appDataDictMenuIconGen")
    private Integer id;
    @Column
    private String name;
    @Column
    private String path;
    @Column
    private Integer type;//类型,表示图标或背景

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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
