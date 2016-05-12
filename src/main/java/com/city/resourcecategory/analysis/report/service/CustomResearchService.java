package com.city.resourcecategory.analysis.report.service;

import com.city.common.pojo.Page;
import com.city.common.util.ConvertUtil;
import com.city.resourcecategory.analysis.common.service.TimeRangeService;
import com.city.resourcecategory.analysis.report.dao.CustomResearchBarInfoDao;
import com.city.resourcecategory.analysis.report.dao.CustomResearchDao;
import com.city.resourcecategory.analysis.report.dao.CustomResearchStyleDao;
import com.city.resourcecategory.analysis.report.entity.CustomResearchEntity;
import com.city.resourcecategory.analysis.report.entity.CustomResearchStyleEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by HZC on 2016/2/24.
 */
@Service
public class CustomResearchService {

    @Autowired
    private CustomResearchDao researchDao;

    @Autowired
    private CustomResearchStyleDao researchStyleDao;

    @Autowired
    private TimeRangeService rangeService;

    @Autowired
    private CustomResearchBarInfoDao researchBarInfoDao;

    /**
     * 返回自定义查询
     * <pre>
     *     根据id，查询自定义查询
     * </pre>
     *
     * @param id
     * @return
     * @author hzc
     * @createDate 2016-2-25
     */
    public CustomResearchEntity getCustomResearchById(int id) {
        return researchDao.queryById(id);
    }

    /**
     * 返回自定义查询表样
     *
     * @param id
     * @return
     * @author hzc
     * @createDate 2016-2-26
     */
    public CustomResearchStyleEntity getCustomResearchStyleByResearchId(int id) {
        return researchStyleDao.selectByResearchId(id);
    }

    /**
     * 返回自定义查询
     * <pre>
     *     根据groupId，查询自定义查询
     * </pre>
     *
     * @param groupId
     * @return
     * @author hzc
     * @createDate 2016-2-24
     */
    public List<CustomResearchEntity> getCustomResearchsByGroupId(int groupId) {
        return researchDao.selectResearchsByGroupId(groupId);
    }

    /**
     * 保存自定义查询
     *
     * @param entity
     * @author hzc
     * @createDate 2016-2-24
     */
    public CustomResearchEntity saveCustomResearch(CustomResearchEntity entity) {
        Integer id = entity.getId();
        CustomResearchEntity researchEntity = new CustomResearchEntity();
        boolean isNew = false;
        if (null != id && id > 0) {
//            更新
            researchEntity = getCustomResearchById(id);
            ConvertUtil<CustomResearchEntity> convertUtil = new ConvertUtil<>();
            convertUtil.replication(entity, researchEntity, CustomResearchEntity.class.getName());
            if (null == entity.getDataSet()) {
                researchEntity.setDataSet(null);
            }
        } else {
            isNew = true;
//            新建
            researchEntity = entity;
            researchEntity.setStatus(1);
        }
        researchDao.saveOrUpdate(researchEntity, Boolean.FALSE);
        if (isNew) {
//            新建自定义查询表样
            CustomResearchStyleEntity styleEntity = new CustomResearchStyleEntity();
            styleEntity.setCustomResearchId(researchEntity.getId());
            styleEntity.setCreatorId(researchEntity.getCreatorId());
            styleEntity.setCreateDate(researchEntity.getCreateDate());
            styleEntity.setStatus(1);
            researchStyleDao.saveOrUpdate(styleEntity, Boolean.FALSE);
        }
        return researchEntity;
    }

    /**
     * 删除自定义查询
     *
     * @param id
     * @author hzc
     * @createDate 2016-2-25
     */
    public void deleteResearchById(Integer id) {
        researchDao.deleteById(id);
        researchStyleDao.deleteByResearchId(id);
        rangeService.deleteByResearchId(id);
        researchBarInfoDao.deleteByResearchId(id);
    }


    /**
     * 更新自定义查询
     *
     * @param entity
     * @author hzc
     * @createDate 2016-3-2
     */
    public void updateEntity(CustomResearchEntity entity) {
        ConvertUtil<CustomResearchEntity> convertUtil = new ConvertUtil<>();
        CustomResearchEntity researchEntity = getCustomResearchById(entity.getId());
        convertUtil.replication(entity, researchEntity, CustomResearchEntity.class.getName());
        researchDao.saveOrUpdate(researchEntity, Boolean.FALSE);
    }

    /**
     * 返回true
     * <pre>
     *      保存表样，成功返回true
     * </pre>
     *
     * @param content
     * @param styleId
     * @author hzc
     * @createDate 2016-2-3
     */
    public boolean saveReportStyle(String content, Integer styleId) {
        CustomResearchStyleEntity style = researchStyleDao.selectByResearchId(styleId);
        style.setRptStyle(content);
        researchStyleDao.update(style, Boolean.FALSE);
        return Boolean.TRUE;
    }

    /**
     * 返回所有自定义查询表
     *
     * @return
     * @author crx
     * @createDate 2016-3-14
     */
    public List<CustomResearchEntity> getAllCustomResearch() {
        return researchDao.selectAll();
    }

    /**
     * 返回分析报表模板
     * <pre>
     *     根据分析报表名字，查询分析报表
     * </pre>
     *
     * @param name 分析报表名字
     * @return
     * @author hzc
     * @createDate 2016-5-6
     */
    public List<CustomResearchEntity> getCustomResearchByName(String name) {
        return researchDao.selectResearchByName(name);
    }
}

