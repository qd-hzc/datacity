package com.city.resourcecategory.analysis.report.service;

import com.city.common.pojo.Page;
import com.city.common.util.ConvertUtil;
import com.city.common.util.ListUtil;
import com.city.resourcecategory.analysis.report.dao.CustomResearchDao;
import com.city.resourcecategory.analysis.report.dao.ResearchGroupDao;
import com.city.resourcecategory.analysis.report.entity.CustomResearchEntity;
import com.city.resourcecategory.analysis.report.entity.ResearchGroupEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 自定义查询管理
 * Created by HZC on 2016/2/22.
 */
@Service
public class CustomResearchManageService {
    @Autowired
    private ResearchGroupDao groupDao;

    @Autowired
    private CustomResearchGroupTree groupTree;

    @Autowired
    private CustomResearchDao researchDao;

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
     * 获取分组
     * <pre>
     *     根据分组名称和状态查询，如果参数为空，则查询所有
     * </pre>
     *
     * @param name
     * @param status
     * @return
     * @author hzc
     * @createDate 2016-5-5
     */
    public List<Map<String, Object>> getResearchGroupTree(String name, Integer status) {
        boolean isNeedColor = name != null && name.trim().length() > 0;
        List<ResearchGroupEntity> groups = groupDao.selectGroups(name, status);
        return groupTree.packageListToTree(groups, isNeedColor);
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

    public Page getCustomResearchsByGroups(Page page, Integer groupId, String name, boolean includeDownLevel) {
        if (null == groupId) {
            groupId = 0;
        }
        String groups;
        if (includeDownLevel) {
            List<Integer> downGroups = getDownGroups(groupId);
            String groupStrs = downGroups.toString();
            groups = groupStrs.substring(1, groupStrs.length() - 1);
        } else {
            groups = groupId.toString();
        }

        List<CustomResearchEntity> tmps = researchDao.getRptTmpsByCondition(page, groups, name);
        page.setDatas(tmps);
        page.setTotal(researchDao.getTmpCountByCondition(name, groups));
        return page;
    }

    /**
     * 返回所有下级分组id，包括本groupId
     *
     * @param groupId 分组id
     * @return
     * @author hzc
     * @createDate 2016-5-5
     */
    private List<Integer> getDownGroups(Integer groupId) {
        List<Integer> itemGroups = new ArrayList<>();
        List<ResearchGroupEntity> groups = groupDao.selectGroupsByParentId(groupId);
        itemGroups.add(groupId);
        if (groups != null && groups.size() > 0) {
            for (ResearchGroupEntity group : groups) {
                itemGroups.addAll(getDownGroups(group.getId()));
            }
        }
        return itemGroups;
    }

    /**
     * 返回分析报表分组
     * <pre>
     *     根据分组名称，查询报表分组
     * </pre>
     *
     * @param name 分组名字
     * @return
     * @author hzc
     * @createDate 2016-5-6
     */
    public List<ResearchGroupEntity> getResearchGroupByName(String name) {
        return groupDao.selectGroupByName(name);
    }
}
