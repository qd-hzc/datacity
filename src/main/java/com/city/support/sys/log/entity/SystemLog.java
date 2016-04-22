package com.city.support.sys.log.entity;

import com.city.common.pojo.Constant;
import com.city.support.sys.user.entity.User;
import com.city.support.sys.user.pojo.CurrentUser;

import javax.persistence.*;
import java.util.Date;


/**
 * Created by wgx on 2016/2/23.
 */
@Entity
@Table(name = "SPT_SYSTEM_LOG")
public class SystemLog {

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

    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "systemLogGen", sequenceName = "SPT_SYSTEM_LOG_SEQ")
    @GeneratedValue(generator = "systemLogGen")
    private Integer id;
    /**
     * 用户id
     */
    @Column(name = "USER_ID")
    private Integer userId;
    /**
     * 用户名
     */
    @Column(name = "USER_NAME")
    private String userName;
    /**
     * 操作类型：Constant.OPERATE_TYPE
     */
    @Column(name = "OPERATE_TYPE")
    private Integer operateType;
    /**
     * 操作日期
     */
    @Column(name = "OPERATE_DATE")
    private Date operateDate;
    /**
     * 资源目标id
     */
    @Column(name = "SOURCE_ID")
    private Integer sourceId;
    //    资源目标类型
    @Column(name = "SOURCE_TYPE")
    private Integer sourceType;
    /**
     * 文字描述信息：记录用户干了什么的文字描述
     * 根据sourceType和operateType及sourceId拼接字符串
     */
    @Column(name = "INFO", length = 1000)
    private String info;
    //    ip地址
    @Column(name = "IP")
    private String ip;
    //    内容：json字符串
    @Column(name = "CONTENTS", length = 1500)
    private String contents;
    //    操作方法名
    @Column(name = "METHOD")
    private String method;
    //    类型：1:手机端，2:电脑端
    @Column(name = "USER_TYPE")
    private Integer userType;

    public SystemLog() {
    }

    /**
     * @param currentUser
     * @param sourceId
     * @param sourceType
     * @param userType
     * @param operateType
     * @param info
     * @param contents
     * @param method
     */
    public SystemLog(CurrentUser currentUser, Integer sourceId, Integer sourceType, Integer userType, Integer operateType, String info, String contents, String method) {
        User user = currentUser.getUser();
        this.userId = user.getId();
        this.sourceId = sourceId;
        this.sourceType = sourceType;
        this.userName = user.getUserName();
        this.operateType = operateType;
        this.operateDate = new Date();
        this.info = info;
        this.ip = user.getIp();
        this.contents = contents;
        this.method = method;
        this.userType = userType;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public Integer getOperateType() {
        return operateType;
    }

    public void setOperateType(Integer operateType) {
        this.operateType = operateType;
    }


    public Date getOperateDate() {
        return operateDate;
    }

    public void setOperateDate(Date operateDate) {
        this.operateDate = operateDate;
    }


    public Integer getSourceId() {
        return sourceId;
    }

    public void setSourceId(Integer sourceId) {
        this.sourceId = sourceId;
    }

    public Integer getSourceType() {
        return sourceType;
    }

    public void setSourceType(Integer sourceType) {
        this.sourceType = sourceType;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer type) {
        this.userType = type;
    }
}
