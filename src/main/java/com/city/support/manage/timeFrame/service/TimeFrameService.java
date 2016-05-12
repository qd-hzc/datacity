package com.city.support.manage.timeFrame.service;

import com.city.common.pojo.Constant;
import com.city.common.pojo.Page;
import com.city.common.util.ConvertUtil;
import com.city.common.util.tree.PackageListToTree;
import com.city.support.manage.timeFrame.dao.TimeFrameDao;
import com.city.support.manage.timeFrame.entity.TimeFrame;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhoutao on 2015/12/30.
 * 时间框架管理Service
 */
@Service
@Transactional
public class TimeFrameService extends PackageListToTree<TimeFrame> {
    @Autowired
    private TimeFrameDao tfd;

    /**
     * 获取所有时间框架
     *
     * @return list
     */
    public List<TimeFrame> findAll() {
        return tfd.getAll();
    }

    /**
     * 按照排序获取所有时间框架
     *
     * @return list
     */
    public List<TimeFrame> findByOrder(Page page) {
        if (page != null) {
            return tfd.getPageByOrder(page);

        }
        return tfd.getByOrder();

    }

    /**
     * 保存时间框架
     *
     * @param tf 时间框架信息
     */
    public Integer save(TimeFrame tf) {
        if ("".equals(tf.getOrder()) || tf.getOrder() == null) {
            tf.setOrder(0);
        }
        String name = tf.getName();
        if (name != null && !"".equals(name)) {
            List<TimeFrame> timeFrameList = tfd.getByAllName(tf.getName());
            if (timeFrameList.size() == 0) {
                tfd.insert(tf);
                return Constant.RequestResult.SUCCESS;
            } else {
                return Constant.RequestResult.EXIST;
            }

        }
        return Constant.RequestResult.FAIL;
    }

    /**
     * 保存时间框架
     *
     * @param timeFrameList 时间框架信息
     */
    public void batchSave(List<TimeFrame> timeFrameList) {
        for (TimeFrame timeFrame : timeFrameList) {
            if ("".equals(timeFrame.getOrder()) || timeFrame.getOrder() == null) {
                timeFrame.setOrder(0);
            }
            String name = timeFrame.getName();
            if (name != null && "".equals(name)) {
                tfd.getByAllName(timeFrame.getName());
                tfd.insert(timeFrame, true);
            }
        }
        tfd.flush();
    }

    /**
     * 更新时间框架
     *
     * @param timeFrameList 时间框架信息
     */
    public Map<String, Object> update(List<TimeFrame> timeFrameList) {
        Map<String, Object> map = new HashMap<>();
        boolean nameRepeat = false;
        ConvertUtil<TimeFrame> convertUtil = new ConvertUtil<>();
        TimeFrame tf = null;
        List<TimeFrame> datas = new ArrayList<>();
        for (TimeFrame timeFrame : timeFrameList) {
            String name = timeFrame.getName();
            List<TimeFrame> timeFrames = null;
            if (name != null) {
                timeFrames = tfd.getByNameAndId(timeFrame.getName(), timeFrame.getId());
            }
            if (timeFrames == null || timeFrames.size() == 0) {
                tf = tfd.queryById(timeFrame.getId());
                if ("".equals(timeFrame.getOrder())) {
                    timeFrame.setOrder(0);
                }
                convertUtil.replication(timeFrame, tf, TimeFrame.class.getName());
                tfd.saveOrUpdate(tf, true);
                datas.add(tf);
            } else {
                nameRepeat = true;
            }
        }
        tfd.flush();
        map.put("datas", datas);
        map.put("nameRepeat", nameRepeat);
        return map;
    }

    /**
     * 更新时间框架
     *
     * @param tf 时间框架信息
     */
    public void update1(TimeFrame tf) {
        TimeFrame timeFrame = tfd.queryById(tf.getId());
        ConvertUtil<TimeFrame> convertUtil = new ConvertUtil<>();
        convertUtil.replication(tf, timeFrame, TimeFrame.class.getName());
        tfd.update(timeFrame);
    }

    /**
     * 批量删除时间框架
     *
     * @param tfs 时间框架list集合
     */
    public void batchDelete(List<TimeFrame> tfs) {
        StringBuilder sb = new StringBuilder("");
        if (CollectionUtils.isNotEmpty(tfs)) {
            for (TimeFrame tf : tfs) {
                sb.append(tf.getId())
                        .append(",");
            }
            if (StringUtils.contains(sb, ",")) {
                String ids = sb.toString();
                ids = ids.substring(0, ids.lastIndexOf(","));

                tfd.batchDelete(ids);//批量删除
            }
        }
    }

    /**
     * 根据名称获取时间框架
     *
     * @param name
     * @return
     */
    public List<TimeFrame> findByName(String name) {
        return tfd.getByName(name);
    }

    /**
     * 根据主键获取实体类
     *
     * @param entityKey
     */
    @Override
    public TimeFrame getEntityById(Integer entityKey) {
        return tfd.getById(entityKey);
    }

    /**
     * 获取主键
     *
     * @param timeFrame
     */
    @Override
    protected Integer getEntityKey(TimeFrame timeFrame) {
        return timeFrame.getId();
    }

    /**
     * 获取父id
     *
     * @param timeFrame
     */
    @Override
    protected Integer getParentId(TimeFrame timeFrame) {
        return 0;
    }

    /**
     * 获取名称
     *
     * @param timeFrame
     */
    @Override
    protected String getEntityName(TimeFrame timeFrame) {
        return timeFrame.getName();
    }

    @Override
    public Map<String, Object> getEntityMap(TimeFrame t) {
        Map<String, Object> depMap = new HashMap<String, Object>();
        depMap.put("id", getEntityKey(t));
        depMap.put("parentId", getParentId(t));
        depMap.put("text", getEntityName(t));
        depMap.put("leaf", true);
        depMap.put("type", Constant.MetadataType.TIME_FRAME);
        return depMap;
    }

    /**
     * 返回时间框架树结构
     *
     * @return
     * @author hzc
     * @createDate 2016-1-15
     */
    public List<Map<String, Object>> getAllTrees() {
        List<TimeFrame> all = findAll();
        return packageListToTree(all, Boolean.FALSE);
    }

    /**
     * 返回所有时间框架
     * <pre>
     *     根据时间框架id集合，返回所有时间框架
     * </pre>
     *
     * @param chartDatas id集合
     * @return
     * @author hzc
     * @createDate 2016-4-5
     */
    public List<TimeFrame> getTimeFramesByIds(ArrayList<Integer> chartDatas) {
        StringBuffer ids = new StringBuffer();
        if (null != chartDatas && chartDatas.size() > 0) {
            for (int i = 0; i < chartDatas.size(); i++) {
                ids.append(chartDatas.get(i));
                ids.append(",");
            }
            ids.append("-1");
        }
        return tfd.selectByIds(ids.toString());
    }
}
