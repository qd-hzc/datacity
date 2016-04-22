package com.city.support.manage.metadata.entity;

import javax.persistence.*;

/**
 * Created by wgx on 2016/1/13.
 * 系统元数据信息表
 */
@Entity
@Table(name = "SPT_MGR_METADATA_INFO")
public class MetadataInfo {

    Integer id;           //id
    Integer type;         //元数据类型
    String name;          //名称
    Integer code;          //代码
    String comments;     //备注
    Integer sort;

    // 备用字段r1-r8
    String r1;
    String r2;
    String r3;
    String r4;
    String r5;
    String r6;
    String r7;
    String r8;

    @Id
    @Column(name = "ID", unique = true, nullable = false)
    @SequenceGenerator(name = "SPT_MGR_METADATA_TYPE", sequenceName = "METADATAINFO_SEQ")
    @GeneratedValue(generator = "SPT_MGR_METADATA_TYPE")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "TYPE", length = 50)
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Column(name = "NAME", length = 200)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "CODE", length = 50)
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Column(name = "COMMENTS", length = 1000)
    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @Column(name = "SORT", length = 50)
    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
    @Column(name = "R1", length = 200)
    public String getR1() {
        return r1;
    }

    public void setR1(String r1) {
        this.r1 = r1;
    }

    @Column(name = "R2", length = 200)
    public String getR2() {
        return r2;
    }

    public void setR2(String r2) {
        this.r2 = r2;
    }

    @Column(name = "R3", length = 200)
    public String getR3() {
        return r3;
    }

    public void setR3(String r3) {
        this.r3 = r3;
    }

    @Column(name = "R4", length = 200)
    public String getR4() {
        return r4;
    }

    public void setR4(String r4) {
        this.r4 = r4;
    }

    @Column(name = "R5", length = 200)
    public String getR5() {
        return r5;
    }

    public void setR5(String r5) {
        this.r5 = r5;
    }

    @Column(name = "R6", length = 200)
    public String getR6() {
        return r6;
    }

    public void setR6(String r6) {
        this.r6 = r6;
    }

    @Column(name = "R7", length = 200)
    public String getR7() {
        return r7;
    }

    public void setR7(String r7) {
        this.r7 = r7;
    }

    @Column(name = "R8", length = 200)
    public String getR8() {
        return r8;
    }

    public void setR8(String r8) {
        this.r8 = r8;
    }
}
