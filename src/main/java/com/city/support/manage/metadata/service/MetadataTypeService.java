package com.city.support.manage.metadata.service;

import com.city.common.event.EsiEvent;
import com.city.common.event.listener.EsiListenerAdapter;
import com.city.common.event.watcher.DepWatched;
import com.city.common.event.watcher.EsiEventWatched;
import com.city.common.event.watcher.MetadataWatched;
import com.city.common.util.ConvertUtil;
import com.city.support.manage.metadata.entity.MetadataType;
import com.city.support.manage.metadata.dao.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by wgx on 2016/1/13.
 */
@Service
@Transactional
public class MetadataTypeService {

    @Autowired
    private MetadataTypeDao metadataTypeDao;
    @Autowired
    private MetadataInfoDao metadataInfoDao;

    private EsiEventWatched esiEventWatched;

    @Autowired
    public MetadataTypeService(MetadataWatched metadataWatched) {
        esiEventWatched = metadataWatched;
        esiEventWatched.addListener(new EsiListenerAdapter() {
            @Override
            public boolean handlerEvent(EsiEvent eEvent) {
                //删除前
                if(MetadataWatched.BEFOREDELETE.equals(eEvent.getEventName())){
                    //清空指标的
                    List<MetadataType> mts = (List)eEvent.getArgs().get(MetadataWatched.PARAM_TYPE);
                    StringBuilder sb = new StringBuilder("");
                    if(CollectionUtils.isNotEmpty(mts)){
                        for(MetadataType mt : mts) {
                            sb.append(mt.getId())
                                    .append(",");
                        }
                        if(StringUtils.contains(sb, ",")){
                            String ids = sb.toString();
                            ids = ids.substring(0, ids.lastIndexOf(","));
                            metadataInfoDao.batchDeleteByType(ids);//删除子项
                        }
                    }
                }
                return true;
            }
        }, null);
    }
    /**
     * 获取所有的系统元数据类型
     *
     * @return
     */
    public List<MetadataType> findAll() {
        return metadataTypeDao.getAll();
    }

    /**
     * 增加系统元数据类型
     *
     * @param metadataType
     */
    public void save(MetadataType metadataType) {
        metadataTypeDao.insert(metadataType);
    }

    /**
     * 修改系统元数据类型
     *
     * @param metadataType
     */
    public void update(MetadataType metadataType) {
        metadataTypeDao.update(metadataType);
    }
    /**
     * 修改系统元数据类型
     *
     * @param metadataTypeList
     */
    public void update(List<MetadataType> metadataTypeList) {
        ConvertUtil<MetadataType> convertUtil = new ConvertUtil<>();
        MetadataType mt=null;
        for(MetadataType metadataType: metadataTypeList){
            mt = metadataTypeDao.queryById(metadataType.getId());
            convertUtil.replication(metadataType,mt,MetadataType.class.getName());
            metadataTypeDao.update(mt);
        }
        metadataTypeDao.flush();

    }

    /**
     * 删除系统元数据类型
     *
     * @param metadataType
     */
    public void delete(MetadataType metadataType) {
        metadataTypeDao.update(metadataType);
    }

    /**
     * 批量删除
     * @param mts   系统元数据类型list集合
     */
    public void batchDelete(List<MetadataType> mts){
        StringBuilder sb = new StringBuilder("");
        //发送删除事件
        EsiEvent e = new EsiEvent();
        e.setEventName(MetadataWatched.BEFOREDELETE);
        e.getArgs().put(MetadataWatched.PARAM_TYPE,mts);
        esiEventWatched.notifyAllListener(e);
        if(CollectionUtils.isNotEmpty(mts)){
            for(MetadataType mt : mts) {
                sb.append(mt.getId())
                        .append(",");
            }
            if(StringUtils.contains(sb, ",")){
                String ids = sb.toString();
                ids = ids.substring(0, ids.lastIndexOf(","));
                metadataTypeDao.batchDelete(ids);//批量删除
            }
        }
    }

    /**
     * 根据id获取元数据类型
     * @param id
     * @return
     */
    public MetadataType findById(Integer id){
        return metadataTypeDao.getById(id);
    }

    /**
     * 根据名称获取元数据类型
     *
     * @param name 查询名称
     * @return
     */
    public List<MetadataType> findByName(String name) {
        return metadataTypeDao.getByName(name);
    }
}
