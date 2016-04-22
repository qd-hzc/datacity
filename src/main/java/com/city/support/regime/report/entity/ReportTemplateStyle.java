package com.city.support.regime.report.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by wxl on 2016/1/13 0013.
 * 报表模板的表样
 */
@Entity
@Table(name = "SPT_RGM_RPT_TMPSTYLE", uniqueConstraints = @UniqueConstraint(columnNames = {"TMP_ID", "NAME"}))
public class ReportTemplateStyle implements Serializable {
    private Integer id;
    private String name;
    /**
     * 模板id
     */
    private ReportTemplate reportTemplate;
    /**
     * 表样类型
     */
    private Integer styleType;
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
     * 表样信息,使用大字段存储
     */
    private String rptStyle;

    /**
     * 报表设计表样,使用大字段存储
     */
    private String designStyle;

    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "reportTemplateStyleGen", sequenceName = "SPT_RGM_RPT_TMPSTYLE_SEQ")
    @GeneratedValue(generator = "reportTemplateStyleGen")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "NAME", nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToOne
    @JoinColumn(name = "TMP_ID")
    public ReportTemplate getReportTemplate() {
        return reportTemplate;
    }

    public void setReportTemplate(ReportTemplate reportTemplate) {
        this.reportTemplate = reportTemplate;
    }

    @Column(name = "STYLE_TYPE", nullable = false)
    public Integer getStyleType() {
        return styleType;
    }

    public void setStyleType(Integer styleType) {
        this.styleType = styleType;
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

    @Column(name = "RPT_STYLE", columnDefinition = "clob")
    public String getRptStyle() {
        return rptStyle;
    }

    public void setRptStyle(String rptStyle) {
        this.rptStyle = rptStyle;
    }

    @Column(name = "DESIGN_STYLE", columnDefinition = "clob")
    public String getDesignStyle() {
        return designStyle;
    }

    public void setDesignStyle(String designStyle) {
        this.designStyle = designStyle;
    }
}
