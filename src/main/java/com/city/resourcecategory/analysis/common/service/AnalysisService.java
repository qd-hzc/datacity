package com.city.resourcecategory.analysis.common.service;

import com.city.common.pojo.Constant;
import com.city.common.pojo.Page;
import com.city.common.util.StringUtil;
import com.city.resourcecategory.analysis.chart.service.BasicChartConfigService;
import com.city.resourcecategory.analysis.common.dao.ResourceDao;
import com.city.resourcecategory.analysis.common.entity.QueryResourceVO;
import com.city.resourcecategory.analysis.report.service.CustomResearchService;
import com.city.resourcecategory.analysis.text.service.TextContentService;
import com.city.support.manage.item.dao.ItemGroupDao;
import com.city.support.manage.item.entity.ItemGroup;
import com.city.support.manage.item.entity.ItemGroupInfo;
import com.city.support.regime.report.service.DesignReportService;
import com.city.support.regime.report.service.ReportManageService;
import com.city.support.sys.user.pojo.CurrentUser;
import com.city.support.sys.user.pojo.ReportPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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

    @Autowired
    private TextContentService textContentService;

    @Autowired
    private BasicChartConfigService chartConfigService;

    @Autowired
    private CustomResearchService researchService;

    @Autowired
    private ReportManageService reportManageService;

    @Autowired
    private ResourceDao resourceDao;

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

    /**
     * 返回报表分析，图表分析，综合表，文字分析
     * <pre>
     *     根据搜索内容，搜索数据资源，根据数据资源的名称模糊匹配，
     *     查询匹配到的报表分析，图表分析，综合表，文字分析；
     *     综合表带权限过滤，匹配到的验证用户权限，无权限查看的综合表不返回；
     *     返回结果分页显示
     *     extraParam：
     *          搜索内容资源类型
     * </pre>
     *
     * @param currentUser 当前用户
     * @param text        搜索内容
     * @param type        搜索内容资源类型：1、全部，2、综合表，3、报表分析，4、图表分析，5、文字分析
     * @param page        分页
     * @return
     * @author hzc
     * @createDate 2016-5-12
     */
    public Page queryResource(CurrentUser currentUser, String text, Integer type, Page page) {
        List<QueryResourceVO> list = new ArrayList();
        int count = 0;
        switch (type) {
            case 1:
                List permissionList = getReportPermissionList(currentUser);
                list = getAllResource(permissionList, text, page);
                count = getAllResourceCount(permissionList, text);
                break;
            case 2:
                list = getReportForSearch(currentUser, text, page);
                count = getReportForSearchCount(currentUser, text);
                break;
            case 3:
                list = getResearchForSearch(text, page);
                count = getResearchForSearchCount(text);
                break;
            case 4:
                list = getChartForSearch(text, page);
                count = getChartForSearchCount(text);
                break;
            case 5:
                list = getTextForSearch(text, page);
                count = getTextForSearchCount(text);
                break;
        }
        LinkedList<QueryResourceVO> result = new LinkedList<>();
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                QueryResourceVO vo = new QueryResourceVO(list.get(i));
                String name = vo.getName();
                if (StringUtil.notEmpty(text)) {
                    vo.setName(name.replace(text, "<span style='color:red;font-weight:700'>" + text + "</span>"));
                } else {
                    vo.setName(name);
                }
                result.add(vo);
            }
        }
        page.setDatas(result);
        page.setTotal(count);
        return page;
    }

    /**
     * 返回所有查询的资源的数量
     * <pre>
     *     查询综合表，过滤综合表的用户权限，
     *     其他资源暂时不过滤权限，
     *     模糊搜索，匹配资源名称，分页
     * </pre>
     *
     * @param permissionList 综合表权限：有可读可写权限的综合表id
     * @param text           搜索名称
     * @return
     * @author hzc
     * @createDate 2016-5-16
     */
    private int getAllResourceCount(List permissionList, String text) {
        StringBuffer ids = new StringBuffer();
        if (permissionList.size() > 0) {
            for (int i = 0; i < permissionList.size(); i++) {
                ids.append(permissionList.get(i)).append(",");
            }
            ids.append("-1");
        }
        return resourceDao.getAllResourceCount(ids.toString(), text);
    }

    /**
     * 返回所有查询的资源
     * <pre>
     *     查询综合表，过滤综合表的用户权限，
     *     其他资源暂时不过滤权限，
     *     模糊搜索，匹配资源名称，分页
     * </pre>
     *
     * @param permissionList 综合表权限：有可读可写权限的综合表id
     * @param text           搜索名称
     * @param page           分页
     * @return
     * @author hzc
     * @createDate 2016-5-16
     */
    private List getAllResource(List permissionList, String text, Page page) {
        StringBuffer ids = new StringBuffer();
        if (permissionList.size() > 0) {
            for (int i = 0; i < permissionList.size(); i++) {
                ids.append(permissionList.get(i)).append(",");
            }
            ids.append("-1");
        }
        List<QueryResourceVO> allResource = resourceDao.getAllResource(ids.toString(), text, page);
        return allResource;
    }

    /**
     * 返回用户权限下综合表id集合
     *
     * @param currentUser
     * @return
     * @author hzc
     * @createDate 2016-5-16
     */
    private List getReportPermissionList(CurrentUser currentUser) {
        Map<Integer, ReportPermission> permissionMap = currentUser.getReportPermissionMap();
        Set<Integer> integers = permissionMap.keySet();
        Iterator<Integer> iterator = integers.iterator();
        List list = new LinkedList();
        while (iterator.hasNext()) {
            Integer next = iterator.next();
            ReportPermission reportPermission = permissionMap.get(next);
            if (reportPermission.isRead() && reportPermission.isWrite()) {
                list.add(next);
            }
        }
        if (null == list || list.size() < 1) {
            return null;
        }
        return list;
    }

    /**
     * 返回true / false
     * <pre>
     *     检查集合是否为空，不为空返回true，空则返回false
     * </pre>
     *
     * @param list
     * @return
     */
    private boolean checkListSize(List list) {
        if (null != list && list.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 搜索文字分析，返回匹配数量
     *
     * @param text
     * @return
     * @author hzc
     * @createDate 2016-5-13
     */
    private int getTextForSearchCount(String text) {
        return textContentService.getTextForSearchCount(text);
    }

    /**
     * 搜索文字分析，返回匹配的文字分析
     * <pre>
     *     模糊查询，匹配名称，分页
     * </pre>
     *
     * @param text
     * @param page
     * @return
     * @author hzc
     * @createDate 2016-5-13
     */
    private List getTextForSearch(String text, Page page) {
        return textContentService.getTextForSearch(text, page);
    }

    /**
     * 返回图表分析数量
     * <pre>
     *     根据图表名称，模糊查询图表分析，返回匹配数量
     * </pre>
     *
     * @param text
     * @return
     * @author hzc
     * @createDate 2016-5-13
     */
    private int getChartForSearchCount(String text) {
        return chartConfigService.getChartForSearchCount(text);
    }

    /**
     * 返回图表分析
     * <pre>
     *     根据图表名称，模糊查询图表分析，分页
     * </pre>
     *
     * @param text
     * @param page
     * @return
     * @author hzc
     * @createDate 2016-5-13
     */
    private List getChartForSearch(String text, Page page) {
        return chartConfigService.getChartForSearch(text, page);
    }

    /**
     * 返回分析报表数量
     * <pre>
     *     根据分析报表名称查询，返回匹配数量
     * </pre>
     *
     * @param text
     * @return
     * @author hzc
     * @createDate 2016-5-13
     */
    private int getResearchForSearchCount(String text) {
        return researchService.getResearchForSearchCount(text);
    }

    /**
     * 返回分析报表
     * <pre>
     *     根据分析报表名称，模糊查询
     * </pre>
     *
     * @param text
     * @param page
     * @return
     * @author hzc
     * @createDate 2016-5-13
     */
    private List getResearchForSearch(String text, Page page) {
        return researchService.getResearchForSearch(text, page);
    }

    /**
     * 返回综合表模糊查询匹配到的数量
     * <pre>
     *     根据综合表名称，模糊查询，返回所有数量
     *     验证用户权限：可读、可写
     * </pre>
     *
     * @param currentUser
     * @param text
     * @return
     * @author hzc
     * @createDate 2016-5-13
     */
    private int getReportForSearchCount(CurrentUser currentUser, String text) {
        return reportManageService.getReportForSearchCount(currentUser, text);
    }

    /**
     * 返回综合表
     * <pre>
     *     根据综合表名称，模糊查询
     *     分页，验证权限：可读、可写
     * </pre>
     *
     * @param currentUser
     * @param text
     * @param page
     * @return
     */
    private List getReportForSearch(CurrentUser currentUser, String text, Page page) {
        return reportManageService.getReportForSearch(currentUser, text, page);
    }
}
