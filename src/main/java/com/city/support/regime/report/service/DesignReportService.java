package com.city.support.regime.report.service;

import com.city.common.event.EsiEvent;
import com.city.common.event.watcher.EsiEventWatched;
import com.city.common.event.watcher.SystemLogWatched;
import com.city.common.pojo.Constant;
import com.city.common.util.table.builder.GenSynthesisStrUtil;
import com.city.common.util.table.pojo.EsiNode;
import com.city.common.util.table.pojo.EsiProperty;
import com.city.common.util.table.pojo.EsiTable;
import com.city.support.manage.item.dao.ItemCaliberDao;
import com.city.support.manage.item.dao.ItemGroupDao;
import com.city.support.manage.item.dao.ItemGroupInfoDao;
import com.city.support.manage.item.entity.ItemCaliber;
import com.city.support.manage.item.entity.ItemGroup;
import com.city.support.manage.item.entity.ItemGroupInfo;
import com.city.support.manage.item.service.ItemService;
import com.city.support.manage.itemdict.dao.ItemDictDao;
import com.city.support.manage.itemdict.entity.SptMgrItemDictEntity;
import com.city.support.manage.surobj.dao.SurveyObjDao;
import com.city.support.manage.surobj.dao.SurveyObjGroupDao;
import com.city.support.manage.surobj.entity.SurveyObj;
import com.city.support.manage.surobj.entity.SurveyObjGroup;
import com.city.support.manage.timeFrame.dao.TimeFrameDao;
import com.city.support.manage.timeFrame.entity.TimeFrame;
import com.city.support.regime.collection.util.SystemLogUtils;
import com.city.support.regime.report.dao.ReportTemplateBarInfoDao;
import com.city.support.regime.report.dao.ReportTemplateStyleDao;
import com.city.support.regime.report.entity.ReportTemplateBarInfo;
import com.city.support.regime.report.entity.ReportTemplateStyle;
import com.city.support.regime.report.pojo.MetaDataPojo;
import com.city.support.sys.log.entity.SystemLog;
import com.city.support.sys.user.dao.DepartmentDao;
import com.city.support.sys.user.entity.Department;
import com.city.support.sys.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 表样设计类
 * Created by HZC on 2016/1/15.
 */
@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
public class DesignReportService {
    @Autowired
    private ReportTemplateBarInfoDao barInfoDao;
    @Autowired
    private ReportTemplateStyleDao reportTemplateStyleDao;
    @Autowired
    private ItemGroupDao itemGroupDao;
    @Autowired
    private ItemCaliberDao itemCaliberDao;
    @Autowired
    private DepartmentDao departmentDao;
    @Autowired
    private ItemGroupInfoDao itemGroupInfoDao;
    @Autowired
    private TimeFrameDao timeFrameDao;
    @Autowired//统计对象分组
    private SurveyObjGroupDao surveyObjGroupDao;
    @Autowired//统计对象
    private SurveyObjDao surveyObjDao;
    @Autowired
    private ItemDictDao itemDictDao;
    //    指标
    @Autowired
    private ItemService itemService;

    private EsiEventWatched eventWatchedLog;

    @Autowired
    public DesignReportService(SystemLogWatched eventWatchedLog) {
        this.eventWatchedLog = eventWatchedLog;
    }

    /**
     * 返回表样设计分组信息
     */
    public List getGroupInfoTrees() {
        //节点
        List<Map<String, Object>> result = new ArrayList<>();
        //时间框架
        Map<String, Object> tf = new HashMap<>();
        tf.put("text", "时间框架");
        tf.put("dataType", Constant.MetadataType.SYSTEM_DESCRIBE_TYPE);
        tf.put("dataValue", 0);
        tf.put("allowDrag", false);
        tf.put("leaf", false);
        tf.put("children", getAllTF());
        result.add(tf);
        //指标目录
        Map<String, Object> itemMenu = new HashMap<>();
        itemMenu.put("text", "指标分组目录");
        itemMenu.put("dataType", Constant.MetadataType.SYSTEM_DESCRIBE_TYPE);
        itemMenu.put("dataValue", 0);
        itemMenu.put("allowDrag", false);
        itemMenu.put("leaf", false);
        itemMenu.put("children", getAllItemDict(0));
        result.add(itemMenu);
        //统计对象
        Map<String, Object> objMap = new HashMap<>();
        objMap.put("text", "统计对象");
        objMap.put("dataType", Constant.MetadataType.RESEARCH_OBJ_GROUP);
        objMap.put("dataValue", 0);
        objMap.put("allowDrag", false);
        objMap.put("leaf", false);
        objMap.put("children", getAllResearObjs());
        result.add(objMap);
        return result;
    }

    /**
     * 获取所有统计对象
     */
    private List<MetaDataPojo> getAllResearObjs() {
        List<MetaDataPojo> result = null;
        //所有分组
        List<SurveyObjGroup> groups = surveyObjGroupDao.querySurObjGroupByName("");
        if (groups != null && groups.size() > 0) {
            result = new ArrayList<>();
            MetaDataPojo groupStr = null;
            for (SurveyObjGroup group : groups) {
                //统计对象存在时才把分组加进去
                List<SurveyObj> objs = surveyObjDao.querySurObjByGroupId(group.getId());
                if (objs != null && objs.size() > 0) {
                    //分组
                    groupStr = new MetaDataPojo();
                    groupStr.setDataName(group.getSurveyObjGroupName());
                    groupStr.setDataType(Constant.MetadataType.RESEARCH_OBJ_GROUP);
                    groupStr.setDataValue(group.getId());
                    groupStr.setLeaf(false);
                    //统计对象
                    List<MetaDataPojo> objStrs = new ArrayList<>();
                    MetaDataPojo objStr = null;
                    for (SurveyObj obj : objs) {
                        objStr = new MetaDataPojo();
                        objStr.setDataName(obj.getSurveyObjName());
                        objStr.setDataType(Constant.MetadataType.RESEARCH_OBJ);
                        objStr.setDataValue(obj.getSurveyObjId());
                        //调查对象类型
                        objStr.setDataInfo1(obj.getSurveyObjType().toString());
                        //地区
                        objStr.setDataInfo2(obj.getSurveyObjAreaId().toString());
                        objStr.setLeaf(true);
                        objStrs.add(objStr);
                    }
                    groupStr.setChildren(objStrs);
                    result.add(groupStr);
                }
            }
        }
        return result;
    }

    /**
     * 获取所有时间框架
     */
    private List<MetaDataPojo> getAllTF() {
        List<TimeFrame> tfs = timeFrameDao.getByOrder();
        List<MetaDataPojo> result = null;
        if (tfs != null && tfs.size() > 0) {
            result = new ArrayList<>();
            MetaDataPojo pojo = null;
            for (TimeFrame tf : tfs) {
                pojo = new MetaDataPojo();
                pojo.setDataName(tf.getName());
                pojo.setDataType(Constant.MetadataType.TIME_FRAME);
                pojo.setDataValue(tf.getId());
                pojo.setLeaf(true);
                pojo.setDataInfo1(tf.getCode());
                result.add(pojo);
            }
        }
        return result;
    }

    /**
     * 获取所有指标分组目录
     */
    private List<MetaDataPojo> getAllItemDict(Integer dictId) {
        //获取下级指标分组目录
        List<SptMgrItemDictEntity> itemMenus = itemDictDao.getItemDictsByParentId(dictId);
        List<MetaDataPojo> result = null;
        if (itemMenus != null && itemMenus.size() > 0) {
            //指标体系下的指标
            result = new ArrayList<>();
            MetaDataPojo menuPojo = null;
            for (SptMgrItemDictEntity itemMenu : itemMenus) {
                //分组
                menuPojo = new MetaDataPojo();
                menuPojo.setDataType(Constant.MetadataType.ITEM_MENU);
                menuPojo.setDataName(itemMenu.getName());
                menuPojo.setDataValue(itemMenu.getId());
                //查询下级
                List<MetaDataPojo> sub = getAllItemDict(itemMenu.getId());
                if (sub != null && sub.size() > 0) {
                    List<MetaDataPojo> children = new ArrayList<>();
                    menuPojo.setLeaf(false);
                    children.addAll(sub);
                    menuPojo.setChildren(children);
                } else {
                    menuPojo.setLeaf(true);
                }
                result.add(menuPojo);
            }
        }
        return result;
    }

    /**
     * 获取指标和指标分组树
     */
    public List<MetaDataPojo> getItemTree(Integer groupId) {
        //获取下级指标体系
        List<ItemGroup> groups = itemGroupDao.getDownGroups(groupId);
        List<MetaDataPojo> result = null;
        if (groups != null && groups.size() > 0) {
            //指标体系下的指标
            List<ItemGroupInfo> items = null;
            result = new ArrayList<>();
            MetaDataPojo groupPojo = null;
            for (ItemGroup group : groups) {
                //查询分租下指标
                items = itemGroupInfoDao.getInfosByGroup("", 1, group.getId().toString());
                //分组
                groupPojo = new MetaDataPojo();
                groupPojo.setDataType(Constant.MetadataType.ITEM_GROUP);
                groupPojo.setDataName(group.getName());
                groupPojo.setDataValue(group.getId());
                List<MetaDataPojo> children = new ArrayList<>();
                groupPojo.setChildren(children);
                groupPojo.setIconCls("Package");
                //查询下级
                List<MetaDataPojo> sub = getItemTree(group.getId());
                if (sub != null && sub.size() > 0) {
                    children.addAll(sub);
                }
                //查询指标
                if (items != null && items.size() > 0) {
                    for (ItemGroupInfo item : items) {
                        MetaDataPojo itemPojo = new MetaDataPojo();
                        itemPojo.setDataType(Constant.MetadataType.ITEM);
                        itemPojo.setDataName(item.getItemName());
                        itemPojo.setDataValue(item.getItem().getId());
                        itemPojo.setLeaf(true);
                        itemPojo.setIconCls("Page");
                        //默认口径
                        Integer caliberId = item.getCaliberId();
                        if (caliberId != null) {
                            itemPojo.setDataInfo1(caliberId.toString());
                        }
                        //默认部门
                        Department department = item.getDepartment();
                        if (department != null) {
                            itemPojo.setDataInfo2(department.getId().toString());
                        }
                        children.add(itemPojo);
                    }
                }
                groupPojo.setLeaf(children.size() == 0);
                result.add(groupPojo);
            }
        }
        return result;
    }

    /**
     * 根据id获取表样
     */
    public ReportTemplateStyle getRptStyleById(Integer styleId) {
        return reportTemplateStyleDao.queryById(styleId);
    }

    /**
     * 根据主键获取口径
     */
    public ItemCaliber getCaliberById(Integer caliberId) {
        return itemCaliberDao.queryById(caliberId);
    }

    /**
     * 根据主键获取部门
     */
    public Department getDepById(Integer depId) {
        return departmentDao.queryById(depId);
    }

    /**
     * 查询指标口径
     */
    public List<ItemCaliber> getItemCaliberByItemId(Integer id) {
        return itemService.queryCalibersByItem(id);
    }

    /**
     * 生成主宾树
     *
     * @param styleId   表样id
     * @param isMainBar 是否是主栏
     * @param parentId  父节点
     */
    public List<Map<String, Object>> getBarInfoTrees(Integer styleId, Integer isMainBar, Integer parentId) {
        List<Map<String, Object>> result = null;
        //首先查找非属性节点
        List<ReportTemplateBarInfo> barInfos = barInfoDao.getBarInfos(styleId, parentId, isMainBar, 0);
        if (barInfos != null && barInfos.size() > 0) {
            result = new ArrayList<>();
            Map<String, Object> barInfoStr = null;//节点
            List<ReportTemplateBarInfo> propInfos = null;//属性
            List<Map<String, Object>> subInfos = null;//下级
            for (ReportTemplateBarInfo barInfo : barInfos) {
                barInfoStr = genInfoMap(barInfo);
                //添加属性
                propInfos = barInfoDao.getBarInfos(styleId, barInfo.getId(), isMainBar, 1);
                if (propInfos != null && propInfos.size() > 0) {
                    List<Map<String, Object>> properties = new ArrayList<>();
                    for (ReportTemplateBarInfo propInfo : propInfos) {
                        //属性map
                        Map<String, Object> propMap = genInfoMap(propInfo);
                        properties.add(propMap);
                    }
                    barInfoStr.put("properties", properties);
                }
                //添加下级
                subInfos = getBarInfoTrees(styleId, isMainBar, barInfo.getId());
                if (subInfos != null && subInfos.size() > 0) {
                    barInfoStr.put("leaf", false);
                    barInfoStr.put("children", subInfos);
                }
                //添加节点
                result.add(barInfoStr);
            }
        }
        return result;
    }

    /**
     * 保存主宾蓝信息
     */
    public String saveBarInfos(User user, EsiTable table, Integer styleId, HttpServletRequest request) throws IllegalAccessException {
        //清除当前样式的所有主宾蓝信息
        barInfoDao.clearStyleBarInfos(styleId.toString());
        //保存主栏
        saveBarInfos(table.getMainBar(), 0, table.getTmpId(), styleId, 1);
        //保存宾栏
        saveBarInfos(table.getGuestBar(), 0, table.getTmpId(), styleId, 0);
        //生成的表样
        String tableStr = new GenSynthesisStrUtil(table, request).genTable().toString();
        //设置style的表样样式
        ReportTemplateStyle style = reportTemplateStyleDao.queryById(styleId);
        style.setDesignStyle(tableStr);
        reportTemplateStyleDao.update(style, false);

        EsiEvent esiEvent = new EsiEvent();
        esiEvent.setEventName(SystemLogWatched.SYS_LOG);
        esiEvent.getArgs().put(SystemLogWatched.SYS_LOG,
                SystemLogUtils.createLog(user, SystemLog.UPDATE, style, SystemLog.TMP_STYLE, "saveReportStyle"));
        eventWatchedLog.notifyAllListener(esiEvent);

        //返回生成的表样
        return tableStr;
    }

    /**
     * 保存主宾蓝信息
     */
    private void saveBarInfos(List<EsiNode> nodes, Integer parentId, Integer tmpId, Integer styleId, int isMain) {
        if (nodes != null && nodes.size() > 0) {
            int index = 1;
            for (EsiNode node : nodes) {
                ReportTemplateBarInfo barInfo = new ReportTemplateBarInfo();
                barInfo.setDataName(node.getDataName());
                barInfo.setParentId(parentId);
                barInfo.setSortIndex(index++);
                barInfo.setTmpId(tmpId);
                barInfo.setStyleId(styleId);
                barInfo.setIsMainBar(isMain);
                barInfo.setDataType(Constant.MetadataType.SYSTEM_DESCRIBE_TYPE);
                barInfo.setIsRealNode(node.isRealNode() ? 1 : 0);
                barInfo.setIsProperty(0);
                barInfoDao.insert(barInfo, true);
                //保存属性信息
                saveBarProps(node.getProperties(), barInfo.getId(), tmpId, styleId, isMain);
                //保存子级
                saveBarInfos(node.getChildren(), barInfo.getId(), tmpId, styleId, isMain);
            }
        }
    }

    /**
     * 保存属性信息
     */
    private void saveBarProps(List<EsiProperty> props, Integer parentId, Integer tmpId, Integer styleId, int isMain) {
        if (props != null && props.size() > 0) {
            int index = 1;
            for (EsiProperty prop : props) {
                if (!prop.isExtend()) {
                    ReportTemplateBarInfo propInfo = new ReportTemplateBarInfo();
                    propInfo.setDataName(prop.getDataName());
                    propInfo.setParentId(parentId);
                    propInfo.setSortIndex(index++);
                    propInfo.setTmpId(tmpId);
                    propInfo.setStyleId(styleId);
                    propInfo.setIsMainBar(isMain);
                    propInfo.setDataType(prop.getDataType());
                    propInfo.setDataValue(prop.getDataValue());
                    propInfo.setDataInfo1(prop.getDataInfo1());
                    propInfo.setDataInfo2(prop.getDataInfo2());
                    propInfo.setIsRealNode(0);
                    propInfo.setIsProperty(1);
                    barInfoDao.insert(propInfo, true);
                }
            }
        }
    }

    /**
     * 根据实体生成map
     */
    private Map<String, Object> genInfoMap(ReportTemplateBarInfo barInfo) {
        Map<String, Object> infoMap = new HashMap<>();
        infoMap.put("dataName", barInfo.getDataName());
        infoMap.put("text", barInfo.getDataName());
        infoMap.put("dataValue", barInfo.getDataValue());
        infoMap.put("dataType", barInfo.getDataType());
        infoMap.put("dataInfo1", barInfo.getDataInfo1());
        infoMap.put("dataInfo2", barInfo.getDataInfo2());
        infoMap.put("isRealNode", barInfo.getIsRealNode());
        infoMap.put("isProperty", barInfo.getIsProperty());
        infoMap.put("leaf", true);
        infoMap.put("expanded", true);
        return infoMap;
    }

    /**
     * 返回true
     * <pre>
     *      保存表样，成功返回true
     * </pre>
     *
     * @param user
     * @param content
     * @param styleId
     * @author hzc
     * @createDate 2016-2-3
     */
    public boolean saveReportStyle(User user, String content, Integer styleId) throws IllegalAccessException {
        ReportTemplateStyle style = reportTemplateStyleDao.queryById(styleId);
        style.setRptStyle(content);
        reportTemplateStyleDao.update(style, Boolean.FALSE);

        EsiEvent esiEvent = new EsiEvent();
        esiEvent.setEventName(SystemLogWatched.SYS_LOG);
        esiEvent.getArgs().put(SystemLogWatched.SYS_LOG,
                SystemLogUtils.createLog(user, SystemLog.UPDATE, style, SystemLog.TMP_STYLE, "saveReportStyle"));
        eventWatchedLog.notifyAllListener(esiEvent);

        return Boolean.TRUE;
    }

    /**
     * 根据表样id查询是否有主宾栏
     */
    public boolean isHasBar(Integer styleId) {
        return barInfoDao.isHasBar(styleId);
    }

}
