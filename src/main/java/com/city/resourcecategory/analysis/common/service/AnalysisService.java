package com.city.resourcecategory.analysis.common.service;

import com.city.common.pojo.Constant;
import com.city.support.manage.item.dao.ItemGroupDao;
import com.city.support.manage.item.entity.ItemGroup;
import com.city.support.manage.item.entity.ItemGroupInfo;
import com.city.support.regime.report.service.DesignReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by HZC on 2016/2/29.
 */
@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
public class AnalysisService {
    @Autowired
    private DesignReportService reportService;
    @Autowired
    private ItemGroupDao itemGroupDao;

    /**
     * 返回表样设计分组信息
     *
     * @author hzc
     * @createDate 2016-2-29
     */
    public List getGroupInfoTrees() {
        //节点
        List<Map<String, Object>> result = reportService.getGroupInfoTrees();

        HashMap<String, Object> time = new HashMap<>();
        time.put("dataName", "时间");
        time.put("text", "时间");
        time.put("dataType", Constant.MetadataType.TIME);
        time.put("dataValue", 0);
        time.put("allowDrag", true);
        time.put("leaf", true);
        result.add(time);
        return result;
    }

    public List getGroupInfoTreeForChart(boolean isDynamic) {
        //节点
        List<Map<String, Object>> result = reportService.getGroupInfoTrees();
        List<Map<String, Object>> dymicMeta = new ArrayList<>();
        Map<String, Object> item = null;
        if (isDynamic) {
            item = new HashMap<>();
            item.put("dataName", "动态元数据");
            item.put("text", "动态元数据");
            item.put("dataType", Constant.MetadataType.SYSTEM_DESCRIBE_TYPE);
            item.put("dataValue", 0);
            item.put("allowDrag", false);
            item.put("leaf", false);
            item.put("children", dymicMeta);
            result.add(item);
            item = new HashMap<>();
            item.put("dataName", "动态时间");
            item.put("text", "动态时间");
            item.put("dataType", Constant.MetadataType.DYNAMIC_TIME);
            item.put("dataValue", 0);
            item.put("allowDrag", true);
            item.put("leaf", true);
            dymicMeta.add(item);
            item = new HashMap<>();
            item.put("dataName", "动态指标");
            item.put("text", "动态指标");
            item.put("dataType", Constant.MetadataType.DYNAMIC_ITEM);
            item.put("dataValue", 0);
            item.put("allowDrag", true);
            item.put("leaf", true);
            dymicMeta.add(item);
            item = new HashMap<>();
            item.put("dataName", "动态时间框架");
            item.put("text", "动态时间框架");
            item.put("dataType", Constant.MetadataType.DYNAMIC_TIMEFRAME);
            item.put("dataValue", 0);
            item.put("allowDrag", true);
            item.put("leaf", true);
            dymicMeta.add(item);
            item = new HashMap<>();
            item.put("dataName", "动态统计对象");
            item.put("text", "动态统计对象");
            item.put("dataType", Constant.MetadataType.DYNAMIC_SUROBJ);
            item.put("dataValue", 0);
            item.put("allowDrag", true);
            item.put("leaf", true);
            dymicMeta.add(item);
            item = new HashMap<>();
            item.put("dataName", "动态分组目录");
            item.put("text", "动态分组目录");
            item.put("dataType", Constant.MetadataType.DYNAMIC_ITEMGROUP);
            item.put("dataValue", 0);
            item.put("allowDrag", true);
            item.put("leaf", true);
            dymicMeta.add(item);
        }
        item = new HashMap<>();
        item.put("dataName", "时间");
        item.put("text", "时间");
        item.put("dataType", Constant.MetadataType.TIME);
        item.put("dataValue", 0);
        item.put("allowDrag", true);
        item.put("leaf", true);
        result.add(item);
        return result;
    }


    /**
     * 暂未使用 指标分组及指标树
     *
     * @return
     */
    public List<Map<String, Object>> getItemGroupTree() {
        List<ItemGroup> itemGroups = itemGroupDao.queryAll();
        List<ItemGroupInfo> itemGroupInfos = new ArrayList<>();
        for (ItemGroup itemGroup : itemGroups) {
            itemGroupInfos.addAll(itemGroup.getGroupInfos());
        }
        return packgeItemGroup(itemGroups, itemGroupInfos);
    }

    /**
     * 封装指标树
     *
     * @param groups
     * @param itemGroupInfos
     * @return
     */
    private List<Map<String, Object>> packgeItemGroup(List<ItemGroup> groups, List<ItemGroupInfo> itemGroupInfos) {
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> item = null;
        for (ItemGroup itemGroup : groups) {
            item = new HashMap<>();
            item.put("id", itemGroup.getId());
            item.put("dataName", itemGroup.getName());
            item.put("iconCls", "Package");
            item.put("dataValue", itemGroup.getId());
            item.put("dataType", Constant.MetadataType.ITEM_GROUP);
            item.put("parentId", itemGroup.getParentId());
            item.put("loaded", true);
            result.add(item);
        }
        for (ItemGroupInfo itemGroupInfo : itemGroupInfos) {
            item = new HashMap<>();
            item.put("id", Integer.parseInt(itemGroupInfo.getId().toString() + itemGroupInfo.getGroupId().toString()));
            item.put("dataValue", itemGroupInfo.getItem().getId());
            item.put("iconCls", "Page");
            item.put("dataName", itemGroupInfo.getItemName());
            item.put("loaded", true);
            item.put("dataType", Constant.MetadataType.ITEM);
            item.put("parentId", itemGroupInfo.getGroupId());
            result.add(item);
        }
        return result;
    }
}
