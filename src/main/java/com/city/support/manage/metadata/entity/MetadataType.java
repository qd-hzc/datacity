package com.city.support.manage.metadata.entity;
import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by wgx on 2016/1/13.
 * 系统元数据类型表
 */
@Entity
@Table(name = "SPT_MGR_METADATA_TYPE")
public class MetadataType implements Serializable {

    public final static Integer UNITDATA=1;   //数据类型
    public final static Integer AREA=21;      //地区等级
    public final static Integer TIME_YEAR=49;      //时间类型-年

    Integer id;              //id
    String name;            //名称
    String infoPage;       //数据格式
    String comments;       //备注说明


    @Id
    @Column(name = "ID", unique = true, nullable = false)
    @SequenceGenerator(name = "matadatatTypeGenerator",sequenceName = "METADATATYPE_SEQ")
    @GeneratedValue(generator = "matadatatTypeGenerator")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "NAME", length=200)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "INFOPAGE", length = 200)
    public String getInfoPage() {
        return infoPage;
    }

    public void setInfoPage(String infoPage) {
        this.infoPage = infoPage;
    }

    @Column(name = "COMMENTS",length = 1000)
    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
