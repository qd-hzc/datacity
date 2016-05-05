package com.city.resourcecategory.analysis.report.service;

import com.city.common.pojo.Constant;
import com.city.common.util.table.builder.GenRCStrUtil;
import com.city.common.util.table.pojo.EsiNode;
import com.city.common.util.table.pojo.EsiProperty;
import com.city.common.util.table.pojo.EsiTable;
import com.city.resourcecategory.analysis.report.dao.CustomResearchBarInfoDao;
import com.city.resourcecategory.analysis.report.dao.CustomResearchStyleDao;
import com.city.resourcecategory.analysis.report.entity.CustomResearchBarInfoEntity;
import com.city.resourcecategory.analysis.report.entity.CustomResearchStyleEntity;
import com.city.support.regime.report.entity.ReportTemplateStyle;
import com.city.support.sys.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 设计自定义查询
 * Created by HZC on 2016/2/25.
 */
@Service
public class DesignCustomResearchService {

    @Autowired
    private CustomResearchBarInfoDao barInfoDao;
    @Autowired
    private CustomResearchStyleDao researchStyleDao;

    /**
     * 保存主宾栏信息
     *
     * @param table
     * @param styleId
     * @param request
     * @throws Exception
     * @mender hzc
     * @modifiedDate 2-16-3-3
     */
    public String saveBarInfos(EsiTable table, EsiTable realTable, Integer styleId, HttpServletRequest request, User user) throws Exception {
        Date date = new Date();
        //清除当前样式的所有主宾蓝信息
        barInfoDao.clearStyleBarInfos(styleId.toString());
        //保存主栏
        saveBarInfos(table.getMainBar(), 0, styleId, true, user);
        //保存宾栏
        saveBarInfos(table.getGuestBar(), 0, styleId, false, user);
        //生成的表样
        String tableStr = new GenRCStrUtil(realTable, request).genTable().toString();
        //设置style的表样样式
        CustomResearchStyleEntity style = researchStyleDao.selectByResearchId(styleId);
//        style.setRptStyle(tableStr);
        style.setDesignStyle(tableStr);
        style.setUpdaterId(user.getId());
        style.setUpdateDate(date);
        researchStyleDao.update(style, false);
        return tableStr;
    }

    /**
     * 保存主宾栏信息
     *
     * @param nodes
     * @param parentId
     * @param styleId
     * @param isMain
     * @throws Exception
     * @mender hzc
     * @modifiedDate 2016-3-3
     */
    private void saveBarInfos(List<EsiNode> nodes, Integer parentId, Integer styleId, boolean isMain, User user) throws Exception {
        Date date = new Date();
        if (nodes != null && nodes.size() > 0) {
            int index = 1;
            for (EsiNode node : nodes) {
                CustomResearchBarInfoEntity barInfo = new CustomResearchBarInfoEntity();
                barInfo.setDataName(node.getDataName());
                barInfo.setParentId(parentId);
                barInfo.setSortIndex(index++);
                barInfo.setCustomResearchId(styleId);
                barInfo.setIsMainBar(isMain);
                barInfo.setDataType(Constant.MetadataType.SYSTEM_DESCRIBE_TYPE);
                barInfo.setIsRealNode(node.isRealNode());
                barInfo.setIsProperty(false);
                barInfo.setCreatorId(user.getId());
                barInfo.setCreateDate(date);
                barInfo.setStatus(1);
                barInfoDao.insert(barInfo, false);
                //保存属性信息
                saveBarProps(node.getProperties(), barInfo.getId(), styleId, isMain, user);
                //保存子级
                saveBarInfos(node.getChildren(), barInfo.getId(), styleId, isMain, user);
            }
        }
    }

    /**
     * 保存属性信息
     *
     * @param props
     * @param parentId
     * @param styleId
     * @param isMain
     * @throws Exception
     * @mender hzc
     * @modifiedDate 2016-3-3
     */
    private void saveBarProps(List<EsiProperty> props, Integer parentId, Integer styleId, boolean isMain, User user) throws Exception {
        Date date = new Date();
        if (props != null && props.size() > 0) {
            int index = 1;
            for (EsiProperty prop : props) {
                if (!prop.isExtend()) {
                    CustomResearchBarInfoEntity propInfo = new CustomResearchBarInfoEntity();
                    propInfo.setDataName(prop.getDataName());
                    propInfo.setParentId(parentId);
                    propInfo.setSortIndex(index++);
                    propInfo.setCustomResearchId(styleId);
                    propInfo.setIsMainBar(isMain);
                    propInfo.setDataType(prop.getDataType());
                    propInfo.setDataValue(prop.getDataValue());
                    propInfo.setDataInfo1(prop.getDataInfo1());
                    propInfo.setDataInfo2(prop.getDataInfo2());
                    propInfo.setIsRealNode(false);
                    propInfo.setIsProperty(true);
                    propInfo.setCreateDate(date);
                    propInfo.setCreatorId(user.getId());
                    propInfo.setStatus(1);
                    barInfoDao.insert(propInfo, false);
                }
            }
        }
    }

    /**
     * 生成主宾树
     *
     * @param styleId   表样id
     * @param isMainBar 是否是主栏
     * @param parentId  父节点
     * @mender hzc
     * @modifiedDate 2016-3-3
     */
    public List<Map<String, Object>> getBarInfoTrees(Integer styleId, Integer isMainBar, Integer parentId) {
        List<Map<String, Object>> result = null;
        //首先查找非属性节点
        List<CustomResearchBarInfoEntity> barInfos = barInfoDao.getBarInfos(styleId, parentId, isMainBar, 0);
        if (barInfos != null && barInfos.size() > 0) {
            result = new ArrayList<>();
            Map<String, Object> barInfoStr = null;//节点
            List<CustomResearchBarInfoEntity> propInfos = null;//属性
            List<Map<String, Object>> subInfos = null;//下级
            for (CustomResearchBarInfoEntity barInfo : barInfos) {
                barInfoStr = genInfoMap(barInfo);
                //添加属性
                propInfos = barInfoDao.getBarInfos(styleId, barInfo.getId(), isMainBar, 1);
                if (propInfos != null && propInfos.size() > 0) {
                    List<Map<String, Object>> properties = new ArrayList<>();
                    for (CustomResearchBarInfoEntity propInfo : propInfos) {
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
     * 根据实体生成map
     *
     * @param barInfo
     * @return
     * @mender hzc
     * @modifiedDate 2016-3-3
     */
    private Map<String, Object> genInfoMap(CustomResearchBarInfoEntity barInfo) {
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
}
