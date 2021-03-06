package com.city.resourcecategory.analysis.chart.service;

import com.city.common.pojo.Constant;
import com.city.common.pojo.Page;
import com.city.common.util.ConvertUtil;
import com.city.resourcecategory.analysis.chart.dao.AnalysisChartBaseDao;
import com.city.resourcecategory.analysis.chart.dao.AnalysisChartGroupDao;
import com.city.resourcecategory.analysis.chart.dao.AnalysisChartInfoDao;
import com.city.resourcecategory.analysis.chart.entity.AnalysisChartBase;
import com.city.resourcecategory.analysis.chart.entity.AnalysisChartGroup;
import com.city.resourcecategory.analysis.chart.entity.AnalysisChartInfo;
import com.city.resourcecategory.analysis.chart.entity.AnalysisChartStructure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wys on 2016/2/22.
 */
@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
public class BasicChartConfigService {
    @Autowired
    private AnalysisChartGroupDao analysisChartGroupDao;

    @Autowired
    private AnalysisChartBaseDao analysisChartBaseDao;

    @Autowired
    private AnalysisChartInfoDao analysisChartInfoDao;

    @Autowired
    BasicChartStructureService basicChartStructureService;

    public List<AnalysisChartGroup> queryAnalysisChartGroupByPid(Integer pid) {
        List<AnalysisChartGroup> result = null;
        if (pid != null) {
            result = analysisChartGroupDao.queryAnalysisChartGroupByPid(pid);
        }
        return result;
    }

    public AnalysisChartBase queryAnalysisChartBaseById(Integer id) {
        return analysisChartBaseDao.queryById(id);
    }

    public List<AnalysisChartGroup> queryAllAnalysisChartGroup() {
        List<AnalysisChartGroup> result = null;
        result = analysisChartGroupDao.queryAll();
        for (AnalysisChartGroup tmpChartGroup : result) {
            for (AnalysisChartGroup tmp : result) {
                if (tmpChartGroup.getId().equals(tmp.getpId())) {
                    tmpChartGroup.setLeaf(false);
                    break;
                }
                tmpChartGroup.setLeaf(true);
            }
        }
        return result;
    }

    public List<AnalysisChartBase> queryAllChart() {
        List<AnalysisChartBase> result = null;
        result = analysisChartBaseDao.queryAllChart();
        return result;
    }

    public List<AnalysisChartBase> queryAllChartExceptMap() {
        List<AnalysisChartBase> result = new ArrayList<>();
        List<AnalysisChartBase> analysisChartBaseList = analysisChartBaseDao.queryAllChart();
        for (AnalysisChartBase analysisChartBase : analysisChartBaseList) {
            Integer count = analysisChartInfoDao.getMapCount(analysisChartBase.getId());
            if (count == 0) {
                analysisChartBase.setName(analysisChartBase.getTitle());
                result.add(analysisChartBase);
            }
        }
        return result;
    }

    public List<AnalysisChartBase> queryAllMap() {
        List<AnalysisChartBase> result = new ArrayList<>();
        List<AnalysisChartBase> analysisChartBaseList = analysisChartBaseDao.queryAllChart();
        for (AnalysisChartBase analysisChartBase : analysisChartBaseList) {
            Integer count = analysisChartInfoDao.getMapCount(analysisChartBase.getId());
            if (count > 0) {
                analysisChartBase.setName(analysisChartBase.getTitle());
                result.add(analysisChartBase);
            }
        }
        return result;
    }

    /**
     * 添加、修改图表分组
     *
     * @param chartGroupList
     */
    public Map<String, Object> updateAnalysisChartGroup(List<AnalysisChartGroup> chartGroupList) {
        Map<String, Object> map = new HashMap<>();
        boolean nameRepeat = false;
        ConvertUtil<AnalysisChartGroup> cu = new ConvertUtil<>();
        AnalysisChartGroup data = null;
        Integer chartGroupId = null;
        List<AnalysisChartGroup> result = new ArrayList<>();
        for (AnalysisChartGroup tmpChartGroup : chartGroupList) {
            chartGroupId = tmpChartGroup.getId();
            if (chartGroupId != null) {
                data = analysisChartGroupDao.queryById(tmpChartGroup.getId());
                List<AnalysisChartGroup> analysisChartGroupList = null;
                if (tmpChartGroup.getpId() != null) {
                    analysisChartGroupList = analysisChartGroupDao.getByNameAndId(data.getName(), tmpChartGroup.getpId(), data.getId());
                } else if (tmpChartGroup.getName() != null) {
                    analysisChartGroupList = analysisChartGroupDao.getByNameAndId(tmpChartGroup.getName(), data.getpId(), data.getId());
                }
                if (data != null && (analysisChartGroupList == null || analysisChartGroupList.size() == 0)) {
                    cu.replication(tmpChartGroup, data, AnalysisChartGroup.class.getName());
                    analysisChartGroupDao.update(data, true);
                    result.add(data);
                } else if (analysisChartGroupList != null && analysisChartGroupList.size() > 0) {
                    nameRepeat = true;
                }
            } else {
                List<AnalysisChartGroup> analysisChartGroupList = analysisChartGroupDao.getByAllNameAndpId(tmpChartGroup.getName(), tmpChartGroup.getpId());
                if (analysisChartGroupList.size() == 0) {
                    analysisChartGroupDao.insert(tmpChartGroup, true);
                    result.add(tmpChartGroup);
                } else {
                    nameRepeat = true;
                }
            }
        }
        map.put("datas", result);
        map.put("nameRepeat", nameRepeat);
        return map;
    }

    public void delAnalysisChartGroup(List<AnalysisChartGroup> chartGroupList) {
        for (AnalysisChartGroup tmpChartGroup : chartGroupList) {
            analysisChartGroupDao.delete(tmpChartGroup, true);
        }
    }

    public List<AnalysisChartBase> queryAnalysisChartBaseByGroupId(Integer groupId) {
        List<AnalysisChartBase> result = null;
        if (groupId != null) {
            result = analysisChartBaseDao.queryAnalysisChartBaseByGroupId(groupId);
        }
        return result;
    }

    /**
     * 添加、修改图表基本信息
     *
     * @param chartBaseList
     */
    public Map<String, Object> updateAnalysisChartBase(List<AnalysisChartBase> chartBaseList) {
        Map<String, Object> map = new HashMap<>();
        boolean nameRepeat = false;
        ConvertUtil<AnalysisChartBase> cu = new ConvertUtil<>();
        AnalysisChartBase data = null;
        Integer chartBaseId = null;
        List<AnalysisChartBase> result = new ArrayList<>();
        for (AnalysisChartBase tmpChartBase : chartBaseList) {
            chartBaseId = tmpChartBase.getId();
            if (chartBaseId != null) {
                data = analysisChartBaseDao.queryById(tmpChartBase.getId());
                List<AnalysisChartBase> analysisChartBaseList = null;
                if (tmpChartBase.getTitle() != null) {
                    analysisChartBaseList = analysisChartBaseDao.getByNameAndId(tmpChartBase.getTitle(), data.getGroupId(), data.getId());
                }
                if (data != null && (analysisChartBaseList == null || analysisChartBaseList.size() == 0)) {
                    cu.replication(tmpChartBase, data, AnalysisChartBase.class.getName());
                    analysisChartBaseDao.update(data, true);
                    result.add(data);
                } else if (analysisChartBaseList != null && analysisChartBaseList.size() > 0) {
                    nameRepeat = true;
                }
            } else {
                List<AnalysisChartBase> AnalysisChartBaseList = analysisChartBaseDao.getByAllNameAndGroupId(tmpChartBase.getTitle(), tmpChartBase.getGroupId());
                if (AnalysisChartBaseList.size() == 0) {
                    analysisChartBaseDao.insert(tmpChartBase, true);
                    result.add(tmpChartBase);
                } else {
                    nameRepeat = true;
                }
            }
        }
        map.put("datas", result);
        map.put("nameRepeat", nameRepeat);
        return map;
    }

    public void delAnalysisChartBase(List<AnalysisChartBase> chartBaseList) {
        for (AnalysisChartBase tmpChartBase : chartBaseList) {
            analysisChartBaseDao.delete(tmpChartBase, true);
        }
    }

    public Map<String, Object> getChartById(Integer id) {
        Map<String, Object> result = new HashMap<>();
        AnalysisChartBase analysisChartBase = queryAnalysisChartBaseById(id);
        result.put("chart", analysisChartBase);
        List<AnalysisChartInfo> analysisChartCategoryList = basicChartStructureService.queryChartInfo(id, Constant.STRUCTURE_TYPE.CATEGORY);
        List<HashMap> categoryList = new ArrayList<HashMap>();
        for (AnalysisChartInfo analysisChartCategory : analysisChartCategoryList) {
            HashMap map = new HashMap();
            map.put("info", analysisChartCategory);
            List<AnalysisChartStructure> chartStructureList = getChartStructureByChartInfo(analysisChartCategory);
            int isDynamic = 0;
            isDynamic = isDynamic(chartStructureList);
            map.put("isDynamic", isDynamic);
            map.put("condition", chartStructureList);
            categoryList.add(map);
        }
        result.put("category", categoryList);
        List<AnalysisChartInfo> analysisChartSeriesList = basicChartStructureService.queryChartInfo(id, Constant.STRUCTURE_TYPE.SERIES);
        List<HashMap> seriesList = new ArrayList<HashMap>();
        for (AnalysisChartInfo analysisChartSeries : analysisChartSeriesList) {
            HashMap map = new HashMap();
            map.put("info", analysisChartSeries);
            List<AnalysisChartStructure> chartStructureList = getChartStructureByChartInfo(analysisChartSeries);
            int isDynamic = 0;
            isDynamic = isDynamic(chartStructureList);
            map.put("isDynamic", isDynamic);
            map.put("condition", chartStructureList);
            seriesList.add(map);
        }
        result.put("series", seriesList);
        return result;
    }

    /**
     * 判断条件中是否含有动态指标和时间
     *
     * @param chartStructureList
     * @return
     */
    private int isDynamic(List<AnalysisChartStructure> chartStructureList) {
        for (AnalysisChartStructure analysisChartStructure : chartStructureList) {
            if (analysisChartStructure.getMetaType() == Constant.MetadataType.DYNAMIC_ITEM ||
                    analysisChartStructure.getMetaType() == Constant.MetadataType.DYNAMIC_ITEMGROUP ||
                    analysisChartStructure.getMetaType() == Constant.MetadataType.DYNAMIC_SUROBJ ||
                    analysisChartStructure.getMetaType() == Constant.MetadataType.DYNAMIC_TIMEFRAME ||
                    analysisChartStructure.getMetaType() == Constant.MetadataType.DYNAMIC_TIME) {
                return 1;
            }
        }
        return 0;
    }

    /**
     * 用来获取分类轴数据的条件
     *
     * @param analysisChartCategory
     * @return
     */
    private List<AnalysisChartStructure> getChartStructureByChartInfo(AnalysisChartInfo analysisChartCategory) {
        List<AnalysisChartStructure> analysisChartStructureList = new ArrayList<AnalysisChartStructure>();
        Integer structureId = analysisChartCategory.getStructureId();
        setChartStructure(analysisChartStructureList, structureId);
        return analysisChartStructureList;
    }

    /**
     * 获取子节点所有父节点的信息
     *
     * @param analysisChartStructureList
     * @param structureId
     */
    private void setChartStructure(List<AnalysisChartStructure> analysisChartStructureList, Integer structureId) {
        if (structureId != null && structureId != 0) {
            AnalysisChartStructure analysisChartStructure = basicChartStructureService.queryChartStructureById(structureId);
            Integer parentId = analysisChartStructure.getParentId();
            analysisChartStructureList.add(analysisChartStructure);
            setChartStructure(analysisChartStructureList, parentId);
        }
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
    public int getChartForSearchCount(String text) {
        return analysisChartBaseDao.selectForSearchCount(text);
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
    public List getChartForSearch(String text, Page page) {
        return analysisChartBaseDao.selectForSearch(text,page);
    }
}
