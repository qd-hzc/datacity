package com.city.support.manage.metadata.service;

import com.city.common.event.EsiEvent;
import com.city.common.event.listener.EsiListenerAdapter;
import com.city.common.event.watcher.EsiEventWatched;
import com.city.common.event.watcher.MetadataWatched;
import com.city.common.pojo.Constant;
import com.city.common.pojo.Page;
import com.city.common.util.ConvertUtil;
import com.city.support.manage.metadata.dao.MetadataInfoDao;
import com.city.support.manage.metadata.entity.MetadataInfo;
import com.city.support.manage.metadata.entity.MetadataType;
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
 * Created by wgx on 2016/1/13.
 */
@Service
@Transactional
public class MetadataInfoService {

    @Autowired
    MetadataInfoDao metadataInfoDao;

    private EsiEventWatched esiEventWatched;

    @Autowired
    public MetadataInfoService(MetadataWatched metadataWatched) {
        esiEventWatched = metadataWatched;
        esiEventWatched.addListener(new EsiListenerAdapter() {
            @Override
            public boolean handlerEvent(EsiEvent eEvent) {
                //删除前
                if(MetadataWatched.BEFOREDELETE.equals(eEvent.getEventName())){
                    //清空指标的
                }
                return true;
            }
        }, null);
    }
    /**
     * 获取所有的系统元数据
     * @return
     */
    public List<MetadataInfo> findAll() {
        return metadataInfoDao.getAll();
    }
    /**
     * 根据id获取系统元数据信息
     * @param id   id
     * @return  MetadataInfo 系统元数据信息
     */
    public MetadataInfo findById(Integer id){
        return metadataInfoDao.getById(id);
    }
    /**
     * 根据类型和分页信息获取系统元数据信息
     * @param type    元数据类型
     * @return  MetadataInfo 系统元数据信息
     */
    public List<MetadataInfo> findByType(Integer type,Page page){
        if(page!=null){
            return metadataInfoDao.getPageByType(type,page);
        }
        return metadataInfoDao.getByType(type);
    }

    /**
     * 获取全部年份
     * @param sortType 排序方式,如果为1,则正序排列,为-1则倒叙排列,默认正序
     */
    public List<Map<String, Object>> getAllYears(Integer sortType,Integer beginItem){
        List<Map<String,Object>> result=new ArrayList<>();
        List<MetadataInfo> years = metadataInfoDao.getByType(Constant.systemConfigPojo.getYearType());
        if(years!=null&&years.size()>0){
            Map<String,Object> yearStr=null;
            if(sortType!=null&&sortType<0){//倒叙排列
                yearStr=new HashMap<>();
                if(beginItem==null) {
                    yearStr.put("text", "一直有效");
                    yearStr.put("value", 0);
                    result.add(yearStr);
                }
                for(int i=years.size()-1;i>=0;i--){
                    MetadataInfo year= years.get(i);
                    yearStr=new HashMap<>();
                    yearStr.put("text",year.getName());
                    yearStr.put("value",year.getCode());
                    result.add(yearStr);
                }
            }else{//则正序排列
                for(MetadataInfo year:years){
                    yearStr=new HashMap<>();
                    yearStr.put("text",year.getName());
                    yearStr.put("value",year.getCode());
                    result.add(yearStr);
                }
            }
        }
        return result;
    }

    /**
     * 增加系统元数据
     * @param metadataInfo
     */
    public void save(MetadataInfo metadataInfo) {
        metadataInfoDao.insert(metadataInfo);
    }
    /**
     * 修改系统元数据
     * @param metadataInfo
     */
    public void update(MetadataInfo metadataInfo) {
        metadataInfoDao.update(metadataInfo);
    }
    /**
     * 修改系统元数据
     * @param metadataInfoList
     */
    public int update(List<MetadataInfo> metadataInfoList) {
        int integer = 0;
        boolean nameRepeat = false;
        ConvertUtil<MetadataInfo> convertUtil = new ConvertUtil<>();
        MetadataInfo mf =null;
        for(MetadataInfo metadataInfo:metadataInfoList){
            mf = metadataInfoDao.queryById(metadataInfo.getId());
            List<MetadataInfo> metadataInfos = null;
            if(metadataInfo.getName()!=null) {
                metadataInfos = metadataInfoDao.getByTypeAndName(metadataInfo.getType(), metadataInfo.getName());
            }
            if(metadataInfos==null||metadataInfos.size()==0) {
                convertUtil.replication(metadataInfo, mf, MetadataInfo.class.getName());
                metadataInfoDao.update(mf);
                integer++;
            }else{
                nameRepeat = true;
            }
        }
        metadataInfoDao.flush();
        if(integer==0&&nameRepeat){
            return Constant.RequestResult.EXIST;
        }else if(integer>0){
            return Constant.RequestResult.SUCCESS;
        }
        return Constant.RequestResult.FAIL;
    }
    /**
     * 删除系统元数据
     * @param metadataInfo
     */
    public void delete(MetadataInfo metadataInfo){
        metadataInfoDao.update(metadataInfo);
    }

    /**
     * 批量删除
     * @param mis   系统元数据list集合
     */
    public void batchDelete(List<MetadataInfo> mis){
        StringBuilder sb = new StringBuilder("");
        //发送删除事件
        EsiEvent e = new EsiEvent();
        e.setEventName(MetadataWatched.BEFOREDELETE);
        e.getArgs().put(MetadataWatched.PARAM_INFO,mis);
        esiEventWatched.notifyAllListener(e);
        if(CollectionUtils.isNotEmpty(mis)){
            for(MetadataInfo mi : mis) {
                sb.append(mi.getId())
                        .append(",");
            }
            if(StringUtils.contains(sb, ",")){
                String ids = sb.toString();
                ids = ids.substring(0, ids.lastIndexOf(","));

                metadataInfoDao.batchDelete(ids);//批量删除
            }
        }
    }
    /**
     * 根据名称获取系统元数据
     * @param name          查询名称
     * @return
     */
    public List<MetadataInfo> findByName(String name){
        return metadataInfoDao.getByName(name);
    }

    public List<MetadataInfo> getByTypeAndName(Integer type, String name) {
        return metadataInfoDao.getByTypeAndName(type,name);
    }
}
