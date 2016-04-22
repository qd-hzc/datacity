package com.city.resourcecategory.analysis.chart.service;

import com.city.common.pojo.Constant;
import com.city.common.util.ConvertUtil;
import com.city.resourcecategory.analysis.chart.dao.AnalysisChartInfoDao;
import com.city.resourcecategory.analysis.chart.dao.AnalysisChartStructureDao;
import com.city.resourcecategory.analysis.chart.entity.AnalysisChartInfo;
import com.city.resourcecategory.analysis.chart.entity.AnalysisChartStructure;
import com.city.resourcecategory.analysis.common.dao.TimeRangeDao;
import com.city.resourcecategory.analysis.common.service.TimeRangeService;
import com.city.support.manage.item.entity.Item;
import com.city.support.manage.item.entity.ItemCaliber;
import com.city.support.manage.item.service.ItemService;
import com.city.support.regime.report.dao.ReportTemplateDao;
import com.city.support.regime.report.entity.ReportTemplate;
import com.city.support.sys.user.dao.DepartmentDao;
import com.city.support.sys.user.entity.Department;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wys on 2016/2/29.
 */
@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
public class BasicChartStructureService {
    @Autowired
    private AnalysisChartStructureDao analysisChartStructureDao;
    @Autowired
    private AnalysisChartInfoDao analysisChartInfoDao;
    @Autowired
    private ItemService itemService;
    @Autowired
    private ReportTemplateDao reportTemplateDao;
    @Autowired
    private DepartmentDao departmentDao;
    @Autowired
    private TimeRangeDao timeRangeDao;

    public List<AnalysisChartStructure> queryChartStructure(Integer chartId, Integer structureType) {
        List<AnalysisChartStructure> result = null;
        result = analysisChartStructureDao.queryChartStructure(chartId, structureType);
        return result;
    }
    public AnalysisChartStructure queryChartStructureById(Integer id) {
        return analysisChartStructureDao.queryById(id);
    }
    public List<AnalysisChartStructure> updateChartStructure(List<AnalysisChartStructure> datas) {
        Integer dataId = null;
        List<AnalysisChartStructure> result = new ArrayList<>();
        for (AnalysisChartStructure data : datas) {
            dataId = data.getId();
            if (dataId == null) {
                analysisChartStructureDao.insert(data, true);
                result.add(data);
            } else {
                ConvertUtil<AnalysisChartStructure> cu = new ConvertUtil<>();
                AnalysisChartStructure structure = analysisChartStructureDao.queryById(dataId);
                cu.replication(data, structure, AnalysisChartStructure.class.getName());
                analysisChartStructureDao.update(structure, true);
                result.add(structure);
            }
        }
        return result;
    }

    public void delChartStructure(List<AnalysisChartStructure> datas,Boolean drag) {
        for (AnalysisChartStructure data : datas) {
            if(drag==null) {
                analysisChartStructureDao.deleteById(data.getId());
                //根据图表结构id删除时间范围
                timeRangeDao.clearTimeRange(Constant.TIMERANGE.TYPE_CHART, data.getId());
            }
            analysisChartInfoDao.deleteByStructureId(data.getId());
        }
    }

    public List<AnalysisChartInfo> queryChartInfo(Integer chartId, Integer infoType) {
        return analysisChartInfoDao.queryChartInfo(chartId, infoType);
    }


    public List<AnalysisChartInfo> updateChartInfo(List<AnalysisChartInfo> datas) {
        Integer dataId = null;
        List<AnalysisChartInfo> result = new ArrayList<>();
        AnalysisChartInfo chartInfo = null;
        AnalysisChartStructure structure = null;
        for (AnalysisChartInfo data : datas) {
            dataId = data.getId();

            if (dataId == null) {
                chartInfo = analysisChartInfoDao.queryByIdStructureId(data.getChartId(), data.getStructureId());
                if (chartInfo != null) {
                    continue;
                }
//                structure = analysisChartStructureDao.queryById(data.getStructureId());
                analysisChartInfoDao.insert(data, true);
                result.add(data);
            } else {
                ConvertUtil<AnalysisChartInfo> cu = new ConvertUtil<>();
                chartInfo = analysisChartInfoDao.queryById(dataId);
                cu.replication(data, chartInfo, AnalysisChartInfo.class.getName());
                analysisChartInfoDao.update(chartInfo, true);
                result.add(chartInfo);
            }
        }
        return result;
    }

    /**
     * 查询指标口径
     */
    public List<ItemCaliber> getItemCaliberByItemId(Integer id) {
        return itemService.queryCalibersByItem(id);
    }

    /**
     * 查询指标所属部门
     */
    public List<Department> getItemDepByItemId(Integer id) {
        Item item = itemService.getItemById(id);
        List depIdList = reportTemplateDao.queryDepId(id);
        String ids = "0";
        if (item != null) {
            ids = ids + "," + item.getDepartment().getId();
        }
        if (depIdList.size() != 0) {
            ids = ids + "," + StringUtils.join(depIdList.toArray(), ",");
        }
        return departmentDao.getDepByIds(ids);
    }

    /**
     * 查询指标所属报表
     */
    public List<ReportTemplate> getItemReportInfoByItemId(Integer id) {
        return reportTemplateDao.queryByItemId(id);
    }

    public void delChartInfo(List<AnalysisChartInfo> datas) {
        AnalysisChartStructure structure = null;
        for (AnalysisChartInfo data : datas) {
            analysisChartInfoDao.delete(data, false);

        }
    }

    /**
     * 根据图表信息查询所有图表结构
     * @param analysisChartCategory
     * @return
     */
    public List<AnalysisChartStructure> queryChartStructureByChartInfo(AnalysisChartInfo analysisChartCategory) {
        analysisChartCategory.getStructureId();

        return null;
    }
}
