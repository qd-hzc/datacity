package com.city.support.manage.area.entity;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by HZC on 2015/12/30.
 */
@Entity
@Table(name = "SPT_MGR_AREA")
public class SptMgrAreaEntity {

    public final static Integer ENABLE = 1;
    public final static Integer DISABLE = 0;

    private Integer id;
    private Integer parentId;
    private String code;
    private String name;
    //    对应元数据中的sort值
    private Integer regionLevel;
    private String nameEn;
    private String longitude;
    private String latitude;
    private Integer isStandard;
    private String comments;
    private Integer creatorId;
    private Date createDate;
    private Integer updaterId;
    private Date updateDate;
    private Integer status;
    private Integer sort;
    private Boolean leaf;
    private String jsonSvg;
    private String mapType;

    /*
    * 树显示名称*/
    private String text;
    /*
    * 上传jsonSvg文件*/
    private CommonsMultipartFile file;
    /*
    * parentId的字符串*/
    private String parentIds;

    @Transient
    public String getParentIds() {
        return parentIds;
    }

    public void setParentIds(String parentIds) {
        this.parentIds = parentIds;
    }

    @Transient
    public CommonsMultipartFile getFile() {
        return file;
    }

    public void setFile(CommonsMultipartFile file) {
        this.file = file;
    }

    @Transient
    public String getText() {
        return this.getName();
    }

    public void setText(String text) {
        this.text = text;
    }

    @Id
    @Column(name = "ID", unique = true, nullable = false)
    @GeneratedValue(generator = "generator")
    @GenericGenerator(name = "generator", strategy = "increment")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Basic
    @Column(name = "PARENT_ID")
    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    @Basic
    @Column(name = "CODE")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Basic
    @Column(name = "NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "REGION_LEVEL")
    public Integer getRegionLevel() {
        return regionLevel;
    }

    public void setRegionLevel(Integer regionLevel) {
        this.regionLevel = regionLevel;
    }

    @Basic
    @Column(name = "NAME_EN")
    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    @Basic
    @Column(name = "LONGITUDE")
    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    @Basic
    @Column(name = "LATITUDE")
    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    @Basic
    @Column(name = "IS_STANDARD")
    public Integer getIsStandard() {
        return isStandard;
    }

    public void setIsStandard(Integer isStandard) {
        this.isStandard = isStandard;
    }

    @Basic
    @Column(name = "COMMENTS")
    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @Basic
    @Column(name = "CREATOR_ID")
    public Integer getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Integer creatorId) {
        this.creatorId = creatorId;
    }

    @Basic
    @Column(name = "CREATE_DATE")
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Basic
    @Column(name = "UPDATER_ID")
    public Integer getUpdaterId() {
        return updaterId;
    }

    public void setUpdaterId(Integer updaterId) {
        this.updaterId = updaterId;
    }

    @Basic
    @Column(name = "UPDATE_DATE")
    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    @Basic
    @Column(name = "STATUS")
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Basic
    @Column(name = "SORT")
    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    @Basic
    @Column(name = "LEAF", nullable = false, columnDefinition = "varchar2(1)")
    public Boolean getLeaf() {
        return leaf;
    }

    public void setLeaf(Boolean leaf) {
        this.leaf = leaf;
    }

    @Basic
    @Column(name = "JSON_SVG")
    public String getJsonSvg() {
        return jsonSvg;
    }

    public void setJsonSvg(String jsonSvg) {
        this.jsonSvg = jsonSvg;
    }

    @Basic
    @Column(name = "MAP_TYPE")
    public String getMapType() {
        return mapType;
    }

    public void setMapType(String mapType) {
        this.mapType = mapType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SptMgrAreaEntity that = (SptMgrAreaEntity) o;

        if (leaf != that.leaf) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (parentId != null ? !parentId.equals(that.parentId) : that.parentId != null) return false;
        if (code != null ? !code.equals(that.code) : that.code != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (regionLevel != null ? !regionLevel.equals(that.regionLevel) : that.regionLevel != null) return false;
        if (nameEn != null ? !nameEn.equals(that.nameEn) : that.nameEn != null) return false;
        if (longitude != null ? !longitude.equals(that.longitude) : that.longitude != null) return false;
        if (latitude != null ? !latitude.equals(that.latitude) : that.latitude != null) return false;
        if (isStandard != null ? !isStandard.equals(that.isStandard) : that.isStandard != null) return false;
        if (comments != null ? !comments.equals(that.comments) : that.comments != null) return false;
        if (creatorId != null ? !creatorId.equals(that.creatorId) : that.creatorId != null) return false;
        if (createDate != null ? !createDate.equals(that.createDate) : that.createDate != null) return false;
        if (updaterId != null ? !updaterId.equals(that.updaterId) : that.updaterId != null) return false;
        if (updateDate != null ? !updateDate.equals(that.updateDate) : that.updateDate != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;
        if (sort != null ? !sort.equals(that.sort) : that.sort != null) return false;
        if (jsonSvg != null ? !jsonSvg.equals(that.jsonSvg) : that.jsonSvg != null) return false;
        return !(mapType != null ? !mapType.equals(that.mapType) : that.mapType != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (parentId != null ? parentId.hashCode() : 0);
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (regionLevel != null ? regionLevel.hashCode() : 0);
        result = 31 * result + (nameEn != null ? nameEn.hashCode() : 0);
        result = 31 * result + (longitude != null ? longitude.hashCode() : 0);
        result = 31 * result + (latitude != null ? latitude.hashCode() : 0);
        result = 31 * result + (isStandard != null ? isStandard.hashCode() : 0);
        result = 31 * result + (comments != null ? comments.hashCode() : 0);
        result = 31 * result + (creatorId != null ? creatorId.hashCode() : 0);
        result = 31 * result + (createDate != null ? createDate.hashCode() : 0);
        result = 31 * result + (updaterId != null ? updaterId.hashCode() : 0);
        result = 31 * result + (updateDate != null ? updateDate.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (sort != null ? sort.hashCode() : 0);
        result = 31 * result + (leaf ? 1 : 0);
        result = 31 * result + (jsonSvg != null ? jsonSvg.hashCode() : 0);
        result = 31 * result + (mapType != null ? mapType.hashCode() : 0);
        return result;
    }
}
