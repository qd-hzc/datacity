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
}
