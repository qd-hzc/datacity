package com.city.resourcecategory.analysis.report.service;

import com.city.common.util.ConvertUtil;
import com.city.common.util.ListUtil;
import com.city.resourcecategory.analysis.report.dao.ResearchGroupDao;
import com.city.resourcecategory.analysis.report.entity.ResearchGroupEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

/**
 * 自定义查询管理
 * Created by HZC on 2016/2/22.
 */
@Service
public class CustomResearchManageService {
    @Autowired
    private ResearchGroupDao groupDao;

    /**
     * 返回自定义查询
     * <pre>
     *     根据id查询
     * </pre>
     *
     * @param id
     * @return
     * @author hzc
     * @createDate 2016-2-24
     */
    public ResearchGroupEntity getResearchGroupById(int id) {
        return groupDao.queryById(id);
    }

    /**
     * 更新或者保存自定义查询
     *
     * @param entity
     * @author hzc
     * @createDate 2016-2-24
     */
    public void saveOrUpdate(ResearchGroupEntity entity) {
        groupDao.saveOrUpdate(entity, Boolean.FALSE);
    }

    /**
     * 返回自定义查询分组树信息
     *
     * @param i
     * @return
     * @author hzc
     * @createDate 2016-2-24
     */
    public List<ResearchGroupEntity> getResearchGroupTree(int i) {
        return groupDao.selectResearchGroupByParentId(i);
    }

    /**
     * 返回自定义查询分组
     * <pre>
     *     保存或者更新自定义查询分组
     * </pre>
     *
     * @param entity
     * @return
     * @author hzc
     * @createDate 2016-2-24
     */
    public ResearchGroupEntity saveOrUpdateGroup(ResearchGroupEntity entity) {
        Integer id = entity.getId();
        ResearchGroupEntity groupEntity = new ResearchGroupEntity();
        if (null != id && id > 0) {
//            更新
            groupEntity = getResearchGroupById(id);
            ConvertUtil<ResearchGroupEntity> convertUtil = new ConvertUtil<>();
            convertUtil.replication(entity, groupEntity, ResearchGroupEntity.class.getName());
        } else {
//            新建
            int parentId = entity.getParentId();
            ResearchGroupEntity parent = getResearchGroupById(parentId);
            if (null != parent) {
                parent.setLeaf(false);
                saveOrUpdate(parent);
            }
            groupEntity = entity;
            groupEntity.setLeaf(true);
            groupEntity.setStatus(1);
            List sorts = groupDao.getMaxSort(parentId);
            groupEntity.setSort(getIndex(sorts));
        }
        saveOrUpdate(groupEntity);
        return groupEntity;
    }

    /**
     * 根据查询的List 获取顺序
     *
     * @param sorts
     * @return
     */
    private int getIndex(List sorts) {
        int s = 1;
        if (sorts != null && sorts.size() > 0) {
            Integer sort = (Integer) sorts.get(0);
            if (sort != null) {
                s += sort;
            }
        }
        return s;
    }

    /**
     * 返回自定义查询分组
     * <pre>
     *     根据parentId查询所有自定义查询分组
     * </pre>
     *
     * @param parentId
     * @return
     * @author hzc
     * @createDate 2016-2-24
     */
    public List<ResearchGroupEntity> getResearchGroupByParentId(int parentId) {
        return groupDao.selectResearchGroupByParentId(parentId);
    }

    /**
     * 删除自定义查询分组
     *
     * @param id
     * @author hzc
     * @createDate 2016-2-24
     */
    public void deleteResearchGroups(int id) throws Exception {
        List<ResearchGroupEntity> list = new LinkedList<ResearchGroupEntity>();
        ResearchGroupEntity entity = getResearchGroupById(id);
        getAllResearchGroupByParentId(list, id);
        list.add(entity);
        groupDao.deleteResearchGroups(list);
    }

    /**
     * 查询parentId下的所有自定义查询分组
     *
     * @param list
     * @param id
     */
    private void getAllResearchGroupByParentId(List<ResearchGroupEntity> list, int id) {
        List<ResearchGroupEntity> entities = getResearchGroupByParentId(id);
        if (entities.size() > 0) {
            for (int i = 0; i < entities.size(); i++) {
                ResearchGroupEntity entity = entities.get(i);
                list.add(entity);
                getAllResearchGroupByParentId(list, entity.getId());
            }
        }
    }

    /**
     * 保存报表分组排序
     *
     * @param groups
     * @author hzc
     * @createDate 2016-4-28
     */
    public void saveGroupSorts(List<ResearchGroupEntity> groups) {
        if (ListUtil.notEmpty(groups)) {
            ConvertUtil<ResearchGroupEntity> util = new ConvertUtil<>();
            //转换并保存
            for (ResearchGroupEntity group : groups) {
                if (group.getId() != null) {
                    ResearchGroupEntity curGroup = getResearchGroupById(group.getId());
                    //赋值
                    util.apply(curGroup, group, ResearchGroupEntity.class);
                    groupDao.update(curGroup, false);
                } else {
                    groupDao.insert(group, true);
                }
            }
        }
    }
}
