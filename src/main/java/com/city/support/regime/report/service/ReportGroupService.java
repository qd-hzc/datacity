package com.city.support.regime.report.service;

import com.city.common.event.EsiEvent;
import com.city.common.event.watcher.EsiEventWatched;
import com.city.common.event.watcher.SystemLogWatched;
import com.city.common.util.ConvertUtil;
import com.city.common.util.ListUtil;
import com.city.common.util.tree.PackageListToTree;
import com.city.support.regime.collection.util.SystemLogUtils;
import com.city.support.regime.report.dao.ReportGroupDao;
import com.city.support.regime.report.entity.ReportGroup;
import com.city.support.sys.log.entity.SystemLog;
import com.city.support.sys.user.pojo.CurrentUser;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 综合表分组
 * Created by HZC on 2016/4/15.
 */
@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
public class ReportGroupService extends PackageListToTree<ReportGroup> {

    @Autowired
    private ReportGroupDao groupDao;

    private EsiEventWatched eventWatched;

    @Autowired
    public ReportGroupService(SystemLogWatched eventWatched) {
        this.eventWatched = eventWatched;
    }

    /**
     * 返回综合表分组
     * <pre>
     *     根据综合表名字和状态，查询所有符合条件的综合表分组
     *     把分组结果封装成树结构
     * </pre>
     *
     * @param name   综合表名称
     * @param status 综合表状态
     * @return
     * @author hzc
     * @createDate 2016-4-15
     */
    public List<Map<String, Object>> getReportGroups(String name, Integer status) {
        boolean isNeedColor = name != null && name.trim().length() > 0;
        List<ReportGroup> groups = groupDao.selectGroups(name, status);
        return packageListToTree(groups, isNeedColor);
    }

    /**
     * 返回综合表分组
     * <pre>
     *     根据id，查询综合表分组
     * </pre>
     *
     * @param entityKey
     * @return
     */
    @Override
    protected ReportGroup getEntityById(Integer entityKey) {
        return groupDao.queryById(entityKey);
    }

    @Override
    public Map<String, Object> getEntityMap(ReportGroup reportGroup) {
        Map<String, Object> entityMap = super.getEntityMap(reportGroup);
        entityMap.put("name", reportGroup.getName());
        entityMap.put("status", reportGroup.getStatus());
        entityMap.put("sort", reportGroup.getSort());
        entityMap.put("comments", reportGroup.getComments());
        return entityMap;
    }

    /**
     * 返回所有下级分组id，包括本groupId
     *
     * @param groupId 综合表分组id
     * @return
     * @author hzc
     * @createDate 2016-4-15
     */
    public List<Integer> getDownGroups(int groupId) {
        List<Integer> itemGroups = new ArrayList<>();
        List<ReportGroup> groups = groupDao.selectGroupsByParentId(groupId);
        itemGroups.add(groupId);
        if (groups != null && groups.size() > 0) {
            for (ReportGroup group : groups) {
                itemGroups.addAll(getDownGroups(group.getId()));
            }
        }
        return itemGroups;
    }

    /**
     * 返回综合表分组
     * <pre>
     *     根据父id，查询综合表分组
     * </pre>
     *
     * @param groupId
     * @return
     * @author hzc
     * @createDate 2016-4-15
     */
    private List<ReportGroup> getReportGroupsByParentId(int groupId) {
        return groupDao.selectGroupsByParentId(groupId);
    }

    /**
     * 保存综合表分组
     *
     * @param currentUser 当前用户
     * @param reportGroup
     * @author hzc
     * @createDate 2016-4-15
     */
    public void saveGroup(CurrentUser currentUser, ReportGroup reportGroup) throws IllegalAccessException {
        //设置顺序
        if (reportGroup.getSort() == null) {
            List sorts = groupDao.getMaxSort(reportGroup.getParentId());
            reportGroup.setSort(getIndex(sorts));
        }

        //保存
        groupDao.saveOrUpdate(reportGroup, true);

        EsiEvent esiEvent = new EsiEvent();
        esiEvent.setEventName(SystemLogWatched.SYS_LOG);
        SystemLog log = SystemLogUtils.createLog(currentUser.getUser(), SystemLog.INSERT, reportGroup, SystemLog.REPORT_GROUP, "saveGroup");

        esiEvent.getArgs().put(SystemLogWatched.SYS_LOG, log);
        eventWatched.notifyAllListener(esiEvent);
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
     * 删除分组
     * <pre>
     *      根据id集合，删除该分组下的所有综合表分组
     * </pre>
     *
     * @param currentUser
     * @param id          分组id
     * @author hzc
     * @createDate 2016-4-15
     */
    public void removeGroups(CurrentUser currentUser, Integer id) throws IllegalAccessException {
        //查询所有下级分组并删除
        List<Integer> groups = getDownGroups(id);
        String groupStr = groups.toString();
        String ids = groupStr.substring(1, groupStr.length() - 1);

//        记录操作日志
        List<ReportGroup> list = getReportGroupsByIds(ids);
        List<SystemLog> removeGroups = SystemLogUtils.createLogList(currentUser.getUser(), SystemLog.DELETE, list, SystemLog.REPORT_GROUP, "removeGroups");

        EsiEvent esiEvent = new EsiEvent();
        esiEvent.setEventName(SystemLogWatched.SYS_LOG_LIST);
        esiEvent.getArgs().put(SystemLogWatched.SYS_LOG_LIST, removeGroups);

        //删除分组
        groupDao.removeGroups(ids);

        eventWatched.notifyAllListener(esiEvent);
    }

    /**
     * 返回综合表分组
     * <pre>
     *     根据id，查询综合表分组
     * </pre>
     *
     * @param id
     * @return
     * @author hzc
     * @createDate 2016-4-15
     */
    public ReportGroup getGroupById(Integer id) {
        return groupDao.loadById(id);
    }

    /**
     * 保存综合表分组顺序
     *
     * @param groups
     * @author hzc
     * @createDate 2016-4-15
     */
    public void saveGroupSorts(List<ReportGroup> groups) {
        if (ListUtil.notEmpty(groups)) {
            ConvertUtil<ReportGroup> util = new ConvertUtil<>();
            //转换并保存
            for (ReportGroup group : groups) {
                if (group.getId() != null) {
                    ReportGroup curGroup = getEntityById(group.getId());
                    //赋值
                    util.apply(curGroup, group, ReportGroup.class);
                    groupDao.update(curGroup, true);
                } else {
                    groupDao.insert(group, true);
                }
            }
        }
    }

    /**
     * 返回综合表分组
     * <pre>
     *     根据分组id集合，返回所有综合表分组
     * </pre>
     *
     * @param ids
     * @return
     * @author hzc
     * @createDate 2016-4-18
     */
    public List<ReportGroup> getReportGroupsByIds(String ids) {
        return groupDao.selectGroupsByIds(ids);
    }
}
