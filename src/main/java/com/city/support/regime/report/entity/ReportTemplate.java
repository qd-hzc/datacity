package com.city.support.regime.report.entity;

import com.city.support.sys.user.entity.Department;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by wxl on 2016/1/13 0013.
 * 报表模版
 */
@Entity
@Table(name = "SPT_RGM_RPT_TMP")
public class ReportTemplate implements Serializable {
    private Integer id;
    private String name;
    //    分组id
    private Integer groupId;
    /**
     * 表号
     */
    private String rptCode;
    /**
     * 报表模板类型,原始表（综合表）
     */
    private Integer rptType;
    /**
     * 报送频率,报送周期下需要生成报表的报告期,多选
     */
    private String frequency;
    /**
     * 报表所属部门
     */
    private Department department;
    /**
     * 期后上报天数
     */
    private Integer submitDaysDelay;
    /**
     * 默认调查对象类型 地区,名录,其他
     */
    private Integer researchObjType;
    /**
     * 默认调查对象id
     */
    private Integer researchObjId;
    /**
     * 报送周期 年,半年,季,月
     */
    private Integer period;
    /**
     * 开始年
     */
    private Integer beginYear;
    /**
     * 结束年,一直有效则为0
     */
    private Integer endYear;
    /**
     * 开始月(季,年,半年)
     */
    private Integer beginPeriod;
    /**
     * 结束月(季,年,半年),一直有效则为0
     */
    private Integer endPeriod;
    /**
     * 报表说明
     */
    private String rptExplain;
    /**
     * 报表备注
     */
    private String rptComments;
    /**
     * 状态
     */
    private Integer status;

    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "reportTemplateGen", sequenceName = "SPT_RGM_RPT_TMP_SEQ")
    @GeneratedValue(generator = "reportTemplateGen")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "GROUP_ID")
    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    @Column(name = "NAME", nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "RPT_CODE")
    public String getRptCode() {
        return rptCode;
    }

    public void setRptCode(String rptCode) {
        this.rptCode = rptCode;
    }

    @Column(name = "RPT_TYPE", nullable = false)
    public Integer getRptType() {
        return rptType;
    }

    public void setRptType(Integer rptType) {
        this.rptType = rptType;
    }

    @Column(name = "FREQUENCY", nullable = false)
    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    @OneToOne
    @JoinColumn(name = "DEP_ID")
    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    @Column(name = "SUBMIT_DAYS_DELAY", nullable = false)
    public Integer getSubmitDaysDelay() {
        return submitDaysDelay;
    }

    public void setSubmitDaysDelay(Integer submitDaysDelay) {
        this.submitDaysDelay = submitDaysDelay;
    }

    @Column(name = "RESEARCH_OBJ_TYPE")
    public Integer getResearchObjType() {
        return researchObjType;
    }

    public void setResearchObjType(Integer researchObjType) {
        this.researchObjType = researchObjType;
    }

    @Column(name = "RESEARCH_OBJ_ID")
    public Integer getResearchObjId() {
        return researchObjId;
    }

    public void setResearchObjId(Integer researchObjId) {
        this.researchObjId = researchObjId;
    }

    @Column(name = "PERIOD", nullable = false)
    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    @Column(name = "BEGIN_YEAR", nullable = false)
    public Integer getBeginYear() {
        return beginYear;
    }

    public void setBeginYear(Integer beginYear) {
        this.beginYear = beginYear;
    }

    @Column(name = "END_YEAR", nullable = false)
    public Integer getEndYear() {
        return endYear;
    }

    public void setEndYear(Integer endYear) {
        this.endYear = endYear;
    }

    @Column(name = "BEGIN_PERIOD", nullable = false)
    public Integer getBeginPeriod() {
        return beginPeriod;
    }

    public void setBeginPeriod(Integer beginPeriod) {
        this.beginPeriod = beginPeriod;
    }

    @Column(name = "END_PERIOD", nullable = false)
    public Integer getEndPeriod() {
        return endPeriod;
    }

    public void setEndPeriod(Integer endPeriod) {
        this.endPeriod = endPeriod;
    }

    @Column(name = "RPT_EXPLAIN", length = 800)
    public String getRptExplain() {
        return rptExplain;
    }

    public void setRptExplain(String rptExplain) {
        this.rptExplain = rptExplain;
    }

    @Column(name = "RPT_COMMENTS", length = 800)
    public String getRptComments() {
        return rptComments;
    }

    public void setRptComments(String rptComments) {
        this.rptComments = rptComments;
    }

    @Column(name = "STATUS", nullable = false)
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ReportTemplate{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", rptCode='" + rptCode + '\'' +
                ", rptType=" + rptType +
                ", frequency='" + frequency + '\'' +
                ", department=" + department +
                ", submitDaysDelay=" + submitDaysDelay +
                ", researchObjType=" + researchObjType +
                ", researchObjId=" + researchObjId +
                ", period=" + period +
                ", beginYear=" + beginYear +
                ", endYear=" + endYear +
                ", beginPeriod=" + beginPeriod +
                ", endPeriod=" + endPeriod +
                ", rptExplain='" + rptExplain + '\'' +
                ", rptComments='" + rptComments + '\'' +
                ", status=" + status +
                '}';
    }
}
