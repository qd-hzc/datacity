package com.city.support.regime.collection.entity;

import com.city.support.regime.report.entity.ReportTemplate;
import com.city.support.sys.user.entity.Department;

import javax.persistence.*;

/**
 * Created by wgx on 2016/1/28.
 */
@Entity
@Table(name = "SPT_RGM_RPT_INFO")
public class ReportInfo {
    public static String RPT_STATUS = "全部";//待填报
    public static String RPT_STATUS_0 = "待填报";//待填报
    public static String RPT_STATUS_1 = "草稿";//草稿
    public static String RPT_STATUS_2 = "待审核";//待审
    public static String RPT_STATUS_3 = "已审核";//已审
    public static String RPT_STATUS_4 = "已驳回";//已驳回
    public static int SUBMIT_STATUS_0 = 0;//未报
    public static int SUBMIT_STATUS_1 = 1;//当期上报
    public static int SUBMIT_STATUS_2 = 2;//逾期上报
    public static int SUBMITDAYADWANCE = 20;//提前上报日期
    // 报表生成状态
    public static int SUCCESS = 0; //成功
    public static int FAIL = -1;   //失败
    public static int EXIST = -2;  //已经存在

    private Integer id;
    /**
     * 报表名称
     */
    private String name;
    /**
     * 报表时间
     */
    private String time;
    /**
     * 报表年份
     */
    private Integer year;
    /**
     * 报表月份
     */
    private Integer month;
    /**
     * 报表类型
     */
    private Integer type;
    /**
     * 报送周期 年,半年,季,月
     */
    private Integer period;
    /**
     * 报表状态：待填报，草稿，待审，已审
     * 0：全部；1:待填报；2：草稿；3：待审；4：已审；5：已驳回
     */
    private Integer rptStatus;
    /**
     * 填报状态：当期，逾期，未报
     */
    private Integer submitStatus;
    /**
     * 报送部门id
     */
    private Integer dptId;
    /**
     * 表样id
     */
    private Integer rptStyleId;
    /**
     * 模板id
     */
    private Integer tmpId;
    /**
     * 期后上报天数
     */
    private Integer submitDaysDelay;

    private String rejectInfo;

    @Transient
    public String getRejectInfo() {
        return rejectInfo;
    }

    public void setRejectInfo(String rejectInfo) {
        this.rejectInfo = rejectInfo;
    }

    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "reportInfoGen", sequenceName = "SPT_RGM_RPT_INFO_SEQ")
    @GeneratedValue(generator = "reportInfoGen")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "TIME", nullable = false)
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Column(name = "YEAR")
    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    @Column(name = "MONTH")
    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    @Column(name = "TYPE", nullable = false)
    public Integer getType() {
        return type;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    @Column(name = "PERIOD")
    public Integer getPeriod() {
        return period;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Column(name = "RPT_STATUS")
    public Integer getRptStatus() {
        return rptStatus;
    }

    public void setRptStatus(Integer rptStatus) {
        this.rptStatus = rptStatus;
    }

    @Column(name = "SUBMIT_STATUS")
    public Integer getSubmitStatus() {
        return submitStatus;
    }

    public void setSubmitStatus(Integer submitStatus) {
        this.submitStatus = submitStatus;
    }

    @Column(name = "DPT_ID")
    public Integer getDptId() {
        return dptId;
    }

    public void setDptId(Integer dptId) {
        this.dptId = dptId;
    }

    @Column(name = "RPT_STYLE_ID")
    public Integer getRptStyleId() {
        return rptStyleId;
    }

    public void setRptStyleId(Integer rptStyleId) {
        this.rptStyleId = rptStyleId;
    }

    @Column(name = "RPT_TMP_ID")
    public Integer getTmpId() {
        return tmpId;
    }

    public void setTmpId(Integer tmpId) {
        this.tmpId = tmpId;
    }

    @Column(name = "SUBMIT_DAYS_DELAY")
    public Integer getSubmitDaysDelay() {
        return submitDaysDelay;
    }

    public void setSubmitDaysDelay(Integer submitDaysDelay) {
        this.submitDaysDelay = submitDaysDelay;
    }

    @Override
    public String toString() {
        return "ReportInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", time='" + time + '\'' +
                ", type=" + type +
                ", rptStatus='" + rptStatus + '\'' +
                ", submitStatus=" + submitStatus +
                ", dptId=" + dptId +
                ", rptStyleId=" + rptStyleId +
                ", tmpId=" + tmpId +
                ", submitDaysDelay=" + submitDaysDelay +
                '}';
    }
}
