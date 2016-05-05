package com.city.support.sys.module.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;

/**
 * Created by wys on 2015/12/30.
 */

@Entity
@Table(name = "SPT_SYS_MODULE")
public class Module {
    public final static Integer SYSMOD = 1;//系统
    public final static Integer MODMOD = 2;//模块
    public final static Integer FUNMOD = 3;//功能
    public final static Integer OPMOD = 4;//操作
    public final static Integer DIRMOD = 5;//目录
    public final static Integer ENABLE = 1;
    public final static Integer DISABLE = 0;
    public final static Integer ROOT = 0;
    public final static Integer SUPPORTID = 1;//应用支撑系统
    /**
     * 模块id
     */
    private Integer id;
    /**
     * 模块名称
     */
    private String moduleName;
    /**
     * 模块简称
     */
    private String moduleShortName;
    /**
     * 模块类型
     */
    private Integer moduleType;
    /**
     * 模块首页
     */
    private String moduleIndex;
    /**
     * 模块配置页面
     */
    private String moduleConfig;
    /**
     * 模块参数
     */
    private String moduleParams;
    /**
     * 模块图标
     */
    private String moduleIcon;
    /**
     * 模块图片
     */
    private String modulePic;
    /**
     * 模块描述
     */
    private String moduleDesc;
    /**
     * 模块备注
     */
    private String moduleComment;
    /**
     * 模块排序
     */
    private Integer moduleSort;
    /**
     * 上级模块
     */
    private Integer modulePid;
    /**
     * 模块状态
     */
    private Integer moduleState;

    private String text;

    @Id
    @GeneratedValue(generator = "na")
    @GenericGenerator(name = "na", strategy = "native", parameters = {@Parameter(name = "sequence", value = "MODULE_SEQ")})
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "MODULE_COMMENT")
    public String getModuleComment() {
        return moduleComment;
    }

    public void setModuleComment(String moduleComment) {
        this.moduleComment = moduleComment;
    }

    @Column(name = "MODULE_CONFIG")
    public String getModuleConfig() {
        return moduleConfig;
    }

    public void setModuleConfig(String moduleConfig) {
        this.moduleConfig = moduleConfig;
    }

    @Column(name = "MODULE_DESC")
    public String getModuleDesc() {
        return moduleDesc;
    }

    public void setModuleDesc(String moduleDesc) {
        this.moduleDesc = moduleDesc;
    }

    @Column(name = "MODULE_ICON")
    public String getModuleIcon() {
        return moduleIcon;
    }

    public void setModuleIcon(String moduleIcon) {
        this.moduleIcon = moduleIcon;
    }

    @Column(name = "MODULE_INDEX")
    public String getModuleIndex() {
        return moduleIndex;
    }

    public void setModuleIndex(String moduleIndex) {
        this.moduleIndex = moduleIndex;
    }

    @Column(name = "MODULE_NAME")
    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
        this.text = moduleName;
    }

    @Column(name = "MODULE_PARAMS")
    public String getModuleParams() {
        return moduleParams;
    }

    public void setModuleParams(String moduleParams) {
        this.moduleParams = moduleParams;
    }

    @Column(name = "MODULE_PIC")
    public String getModulePic() {
        return modulePic;
    }

    public void setModulePic(String modulePic) {
        this.modulePic = modulePic;
    }

    @Column(name = "MODULE_SNAME")
    public String getModuleShortName() {
        return moduleShortName;
    }

    public void setModuleShortName(String moduleShortName) {
        this.moduleShortName = moduleShortName;
    }

    @Column(name = "MODULE_TYPE")
    public Integer getModuleType() {
        return moduleType;
    }

    public void setModuleType(Integer moduleType) {
        this.moduleType = moduleType;
    }

    @Column(name = "MODULE_SORT")
    public Integer getModuleSort() {
        return moduleSort;
    }

    public void setModuleSort(Integer moduleSort) {
        this.moduleSort = moduleSort;
    }

    @Column(name = "MODULE_PID")
    public Integer getModulePid() {
        return modulePid;
    }

    public void setModulePid(Integer modulePid) {
        this.modulePid = modulePid;
    }


    @Column(name = "MODULE_STATE")
    public Integer getModuleState() {
        return moduleState;
    }

    public void setModuleState(Integer moduleState) {
        this.moduleState = moduleState;
    }

    @Transient
    public String getText() {
        return this.text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Module module = (Module) o;

        return id.equals(module.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
