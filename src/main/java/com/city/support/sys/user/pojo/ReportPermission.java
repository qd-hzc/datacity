package com.city.support.sys.user.pojo;

/**
 * Created by zhoutao on 2016/3/9.
 * 报表权限信息 DTO
 */
public class ReportPermission {
    private Integer reportTmpId;//报表模板id
    private boolean read;       //可读权限
    private boolean write;      //可写权限
    private boolean APPROVAL;   //可审核权限

    public Integer getReportTmpId() {
        return reportTmpId;
    }

    public void setReportTmpId(Integer reportTmpId) {
        this.reportTmpId = reportTmpId;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isWrite() {
        return write;
    }

    public void setWrite(boolean write) {
        this.write = write;
    }

    public boolean isAPPROVAL() {
        return APPROVAL;
    }

    public void setAPPROVAL(boolean APPROVAL) {
        this.APPROVAL = APPROVAL;
    }
}
