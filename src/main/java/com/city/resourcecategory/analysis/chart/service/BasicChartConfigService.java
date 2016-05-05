package com.city.resourcecategory.analysis.chart.service;

import com.city.common.pojo.Constant;
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
        for(AnalysisChartBase analysisChartBase: analysisChartBaseList){
            Integer count = analysisChartInfoDao.getMapCount(analysisChartBase.getId());
            if(count==0){
                analysisChartBase.setName(analysisChartBase.getTitle());
                result.add(analysisChartBase);
            }
        }
        return result;
    }
    public List<AnalysisChartBase> queryAllMap() {
        List<AnalysisChartBase> result = new ArrayList<>();
        List<AnalysisChartBase> analysisChartBaseList = analysisChartBaseDao.queryAllChart();
        for(AnalysisChartBase analysisChartBase: analysisChartBaseList){
            Integer count = analysisChartInfoDao.getMapCount(analysisChartBase.getId());
            if(count>0){
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
    public List<AnalysisChartGroup> updateAnalysisChartGroup(List<AnalysisChartGroup> chartGroupList) {
        ConvertUtil<AnalysisChartGroup> cu = new ConvertUtil<>();
        AnalysisChartGroup data = null;
        Integer chartGroupId = null;
        List<AnalysisChartGroup> result = new ArrayList<>();
        for (AnalysisChartGroup tmpChartGroup : chartGroupList) {
            chartGroupId = tmpChartGroup.getId();
            if (chartGroupId != null) {
                data = analysisChartGroupDao.queryById(tmpChartGroup.getId());
                if (data != null) {
                    cu.replication(tmpChartGroup, data, AnalysisChartGroup.class.getName());
                    analysisChartGroupDao.update(data, true);
                    result.add(data);
                }
            } else {
                List<AnalysisChartGroup> analysisChartGroupList = analysisChartGroupDao.getByAllName(tmpChartGroup.getName());
                if(analysisChartGroupList.size()==0) {
                    analysisChartGroupDao.insert(tmpChartGroup, true);
                    result.add(tmpChartGroup);
                }
            }
        }
        return result;
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
    public List<AnalysisChartBase> updateAnalysisChartBase(List<AnalysisChartBase> chartBaseList) {
        ConvertUtil<AnalysisChartBase> cu = new ConvertUtil<>();
        AnalysisChartBase data = null;
        Integer chartBaseId = null;
        List<AnalysisChartBase> result = new ArrayList<>();
        for (AnalysisChartBase tmpChartBase : chartBaseList) {
            chartBaseId = tmpChartBase.getId();
            if (chartBaseId != null) {
                data = analysisChartBaseDao.queryById(tmpChartBase.getId());
                if (data != null) {
                    cu.replication(tmpChartBase, data, AnalysisChartBase.class.getName());
                    analysisChartBaseDao.update(data, true);
                    result.add(data);
                }
            } else {
                analysisChartBaseDao.insert(tmpChartBase, true);
                result.add(tmpChartBase);
            }
        }
        return result;
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
                    analysisChartStructure.getMetaType() == Constant.MetadataType.DYNAMIC_TIMEFRAME||
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
}
