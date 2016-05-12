package com.city.common.pojo;

/**
 * 系统配置实体
 */
public class SystemConfigPojo {
    /**
     * 是否使用拦截器拦截请求路径和参数,如果拦截则打印出来
     */
    private boolean debug;
    /**
     * 是否显示系统日志
     */
    private boolean showSysLog;
    /**
     * 部门父id 角色权限管理==>报表权限 使用
     */
    private Integer depParentId;
    /**
     * 默认调查对象(地区类型)
     */
    private Integer defaultAreaId;
    /**
     * 重点关注显示数据期数
     */
    private Integer mainFocusSpan;
    /**
     * 系统元数据 数据类型
     */
    private Integer unitDataType;
    /**
     * 系统元数据 地区等级
     */
    private Integer areaType;
    /**
     * 系统元数据 时间类型-年
     */
    private Integer yearType;
    /**
     * 系统元数据 人员职务
     */
    private Integer dutyType;

    public boolean isShowSysLog() {
        return showSysLog;
    }

    public void setShowSysLog(boolean showSysLog) {
        this.showSysLog = showSysLog;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public Integer getDepParentId() {
        return depParentId;
    }

    public void setDepParentId(Integer depParentId) {
        this.depParentId = depParentId;
    }

    public Integer getDefaultAreaId() {
        return defaultAreaId;
    }

    public void setDefaultAreaId(Integer defaultAreaId) {
        this.defaultAreaId = defaultAreaId;
    }

    public Integer getMainFocusSpan() {
        return mainFocusSpan;
    }

    public void setMainFocusSpan(Integer mainFocusSpan) {
        this.mainFocusSpan = mainFocusSpan;
    }

    public Integer getUnitDataType() {
        return unitDataType;
    }

    public void setUnitDataType(Integer unitDataType) {
        this.unitDataType = unitDataType;
    }

    public Integer getAreaType() {
        return areaType;
    }

    public void setAreaType(Integer areaType) {
        this.areaType = areaType;
    }

    public Integer getYearType() {
        return yearType;
    }

    public void setYearType(Integer yearType) {
        this.yearType = yearType;
    }

    public Integer getDutyType() {
        return dutyType;
    }

    public void setDutyType(Integer dutyType) {
        this.dutyType = dutyType;
    }
}
