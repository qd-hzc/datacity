package com.city.support.regime.collection.util;

import com.city.common.pojo.Constant;
import com.city.support.regime.collection.entity.ReportData;
import com.city.support.regime.collection.entity.ReportInfo;
import com.city.support.sys.log.entity.SystemLog;
import com.city.support.sys.user.entity.User;
import com.google.gson.Gson;

import java.lang.reflect.Field;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by wgx on 2016/4/16.
 */
public class SystemLogUtils {
    public static final int DRAFT = Constant.OPERATE_TYPE.DRAFT;//报表修改
    public static final int WAITING_PASS = Constant.OPERATE_TYPE.WAITING_PASS;//报表提交
    public static final int PASS = Constant.OPERATE_TYPE.PASS;//审核通过
    public static final int REJECT = Constant.OPERATE_TYPE.REJECT;//审核驳回
    public static final int DELETE = Constant.OPERATE_TYPE.DELETE;//删除
    public static final int INSERT = Constant.OPERATE_TYPE.INSERT;//新建
    public static final int UPDATE = Constant.OPERATE_TYPE.UPDATE;//更新
    public static final int FIND = Constant.OPERATE_TYPE.FIND;//查看
    public static final int COPY = Constant.OPERATE_TYPE.COPY;//复制

    public static final int PHONE = Constant.OPERATE_TYPE.PHONE;//手机端
    public static final int WEB = Constant.OPERATE_TYPE.WEB;//电脑端

    public static final int REPORT = Constant.LOG_SOURCE_TYPE.REPORT;//报表类型
    public static final int REPORT_TMP = Constant.LOG_SOURCE_TYPE.REPORT_TMP;//报表模板类型
    public static final int REPORT_DATA = Constant.LOG_SOURCE_TYPE.REPORT_DATA;//报表数据类型
    public static final int TMP_STYLE = Constant.LOG_SOURCE_TYPE.TMP_STYLE;//报表模板表样类型
    public static final int REPORT_GROUP = Constant.LOG_SOURCE_TYPE.REPORT_GROUP;//报表分组类型

    /**
     * 创建日志bean
     *
     * @param user        当前用户
     * @param operateType 操作类型
     * @param source      保存源对象（bean）
     * @param sourceType  对象类型
     * @param method      当前执行方法
     * @return
     * @throws IllegalAccessException
     */
    public static SystemLog createLog(User user, Integer operateType, Object source, Integer sourceType, String method) throws IllegalAccessException {
        Class<?> aClass = source.getClass();
        Field[] fields = aClass.getDeclaredFields();
        StringBuffer contents = new StringBuffer("{");
        String name = "";
        Integer id = -1;
        for (int i = 0; i < fields.length; i++) {
            Field f = fields[i];
            f.setAccessible(true);
            String fName = f.getName();
            Object o = f.get(source);
            try {
                if (((String) o).length() > 500) {
                    continue;
                }
            } catch (Exception e) {
            }
            contents.append("\"").append(fName).append("\"").append(":").append("\"").append(o).append("\"").append(",");
            if (fName.equals("name")) {
                name = (String) o;
            } else if (fName.equals("id")) {
                id = (Integer) o;
            }
        }
        String s = contents.toString();
        String contensS = s.substring(0, s.lastIndexOf(",")) + "}";
        String info = getInfo(operateType, sourceType, name);
        return createSystemLog(user, operateType, sourceType, id, info, method, contensS.toString(), SystemLog.WEB);
    }

    /**
     * 批量创建日志bean
     *
     * @param user        当前用户
     * @param operateType 操作类型
     * @param source      保存源对象（bean）
     * @param sourceType  对象类型
     * @param method      当前执行方法
     * @return
     * @throws IllegalAccessException
     */
    public static List<SystemLog> createLogList(User user, Integer operateType, List source, Integer sourceType, String method) throws IllegalAccessException {
        List<SystemLog> createReportInfoLog = new ArrayList<>();

        for (Object obj : source) {
            SystemLog systemLog = createLog(user, operateType, obj, sourceType, method);
            createReportInfoLog.add(systemLog);
        }

        return createReportInfoLog;
    }

    /**
     * 创建系统日志(报表)
     *
     * @param user        当前用户
     * @param operateType 操作类型
     * @param reportInfo  报表
     * @param method      方法
     * @return
     */
    public static SystemLog createReportInfoLog(User user, Integer operateType, ReportInfo reportInfo, String method) {
        Gson gson = new Gson();
        String rptTime = "(" + reportInfo.getYear() + "年" + Constant.FrequencyType.getFrequencyName(reportInfo.getPeriod(), reportInfo.getMonth()) + ")";
        String rejectInfo = "";
        if(operateType==REJECT) {
            rejectInfo = (reportInfo.getRejectInfo() != null&&!"".equals(reportInfo.getRejectInfo())) ? reportInfo.getRejectInfo() : "无";
        }
        String info = getInfo(operateType, SystemLog.REPORT, reportInfo.getName() + rptTime)+rejectInfo;
        return createSystemLog(user, operateType, SystemLog.REPORT, reportInfo.getId(), info, method, gson.toJson(reportInfo), SystemLog.WEB);
    }

    /**
     * 创建系统日志(报表数据)
     *
     * @param user        当前用户
     * @param operateType 操作类型
     * @param reportInfo  报表
     * @param method      方法
     * @return
     */
    public static SystemLog createReportDataLog(User user, Integer operateType, ReportInfo reportInfo, String method, ReportData reportData) {
        Gson gson = new Gson();
        String rptTime = "(" + reportInfo.getYear() + "年" + Constant.FrequencyType.getFrequencyName(reportInfo.getPeriod(), reportInfo.getMonth()) + ")";
        String info = getInfo(operateType, SystemLog.REPORT_DATA, reportInfo.getName() + rptTime);
        return createSystemLog(user, operateType, SystemLog.REPORT_DATA, reportInfo.getId(), info, method, gson.toJson(reportData), SystemLog.WEB);
    }

    /**
     * 创建系统日志(报表)
     *
     * @param user           当前用户
     * @param operateType    操作类型
     * @param reportInfoList 报表List
     * @param method         方法
     * @return
     */
    public static List<SystemLog> createReportInfoLog(User user, Integer operateType, List<ReportInfo> reportInfoList, String method) {
        List<SystemLog> createReportInfoLog = new ArrayList<>();
        for (ReportInfo reportInfo : reportInfoList) {
            SystemLog systemLog = createReportInfoLog(user, operateType, reportInfo, method);
            createReportInfoLog.add(systemLog);
        }
        return createReportInfoLog;
    }

    /**
     * 创建系统日志(报表数据)
     *
     * @param user        当前用户
     * @param operateType 操作类型
     * @param reportInfo  报表
     * @param method      方法
     * @return
     */
    public static List<SystemLog> createReportDataLog(User user, Integer operateType, ReportInfo reportInfo, String method, List<ReportData> reportDataList) {
        List<SystemLog> createReportDataLog = new ArrayList<>();
        for (ReportData reportData : reportDataList) {
            SystemLog systemLog = createReportDataLog(user, operateType, reportInfo, method, reportData);
            createReportDataLog.add(systemLog);
        }
        return createReportDataLog;
    }

    private static String getInfo(Integer operateType, Integer sourceType, String reportInfoName) {
        String sourceName = "";
        switch (sourceType) {
            case REPORT:
                sourceName = "报表";
                break;
            case REPORT_TMP:
                sourceName = "报表模板";
                break;
            case TMP_STYLE:
                sourceName = "报表模板表样";
                break;
            case REPORT_DATA:
                sourceName = "报表数据";
                if (operateType == 2) {
                    operateType = INSERT;
                } else if (operateType < 6) {
                    operateType = UPDATE;
                }
                break;
            case REPORT_GROUP:
                sourceName = "报表分组";
                break;
            default:
        }
        String operateName = "";
        String operateNameSuf = "";
        switch (operateType) {
            case DRAFT:
                operateName = "暂存了";
                operateNameSuf = "(草稿状态)";
                break;
            case WAITING_PASS:
                operateName = "提交了";
                break;
            case PASS:
                operateName = "审核通过了";
                break;
            case REJECT:
                operateName = "审核驳回了";
                operateNameSuf = "，驳回原因：";
                break;
            case DELETE:
                operateName = "删除了";
                break;
            case INSERT:
                operateName = "新建了";
                break;
            case UPDATE:
                operateName = "更新了";
                break;
            case COPY:
                operateName = "复制了";
                break;
        }
        return operateName + '"' + reportInfoName + '"' + sourceName + operateNameSuf;
    }


    /**
     * @param user
     * @param operateType 操作类型
     */
    public static SystemLog createSystemLog(User user, Integer operateType, Integer sourceType, Integer sourceId, String info, String method, String contents, Integer userType) {
        SystemLog systemLog = new SystemLog();
        if (user != null) {
            systemLog.setUserId(user.getId());//用户id
            systemLog.setUserName(user.getUserName());//用户名
            systemLog.setIp(user.getIp());//ip地址
        } else {
            systemLog.setUserName("后台定时操作");//用户名
        }
        systemLog.setOperateType(operateType);//操作类型：Constant.OPERATE_TYPE
        systemLog.setInfo(info);//文字描述信息：记录用户干了什么的文字描述
        Date date = new Date();
        systemLog.setOperateDate(date);//操作日期
        systemLog.setSourceType(sourceType);
        systemLog.setSourceId(sourceId);//报表id
        systemLog.setContents(contents);//内容：json字符串
        systemLog.setMethod(method);//操作方法名
        systemLog.setUserType(userType);//类型：1:手机端，2:电脑端
        return systemLog;
    }
}
