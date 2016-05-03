package com.city.support.manage.area.service;

import com.city.common.util.ConvertUtil;
import com.city.common.util.EsiLogUtil;
import com.city.common.util.ListUtil;
import com.city.support.manage.area.dao.AreaDao;
import com.city.support.manage.area.entity.SptMgrAreaEntity;
import com.city.support.manage.area.util.GenAreaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.*;
import java.util.*;

/**
 * 地区
 * 提供地区相关方法
 * Created by HZC on 2015/12/30.
 */
@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
public class AreaService {

    @Autowired
    private AreaDao areaDao;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * 返回true或者false
     * <pre>
     *      保存或者更新地区，成功返回true，保存失败返回false
     * </pre>
     *
     * @param entity
     * @author hzc
     * @createDate 2015-12-30
     */
    public boolean saveOrUpdate(SptMgrAreaEntity entity) {
        entity = uploadJsonSvg(entity);
        Integer areaId = entity.getId();
        //保存之前填充entity中为空的字段与数据库相同
        if (areaId > 0) {
            SptMgrAreaEntity areaById = getAreaById(areaId);
            ConvertUtil<SptMgrAreaEntity> convertUtil = new ConvertUtil<SptMgrAreaEntity>();
            convertUtil.replication(entity, areaById, SptMgrAreaEntity.class.getName());
            areaDao.saveOrUpdate(areaById, Boolean.FALSE);
        } else {
            areaDao.saveOrUpdate(entity, Boolean.FALSE);
        }
        return Boolean.TRUE;
    }

    /**
     * 上传地图文件，返回bean，bean中保存文件路径
     *
     * @param entity
     * @return
     * @author hzc
     * @createDate 2016-1-5
     */
    private SptMgrAreaEntity uploadJsonSvg(SptMgrAreaEntity entity) {
        CommonsMultipartFile file = entity.getFile();
        if (!file.isEmpty()) {
            try {
                String originalFilename = file.getOriginalFilename();
                String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
                String mapType = suffix.substring(1);
                entity.setMapType(mapType);
                UUID uuid = UUID.randomUUID();
                String uuidName = uuid.toString() + suffix;
                String fileName = entity.getJsonSvg() + File.separator + uuidName;

                OutputStream os = new FileOutputStream(new File(fileName));

//                fileName = File.separator + "upload" + File.separator + "map" + File.separator + uuidName;
                fileName = "/upload/map/" + uuidName;

                entity.setJsonSvg(fileName);
                //写入本地磁盘
                InputStream is = file.getInputStream();
                byte[] bs = new byte[1024];
                int len;
                while ((len = is.read(bs)) != -1) {
                    os.write(bs, 0, len);
                }
                os.close();
                is.close();
            } catch (IOException e) {
                EsiLogUtil.error(log, "hzc-message : something wrong happened when save file (json or svg);\n" + e.getMessage());
            }
        }
        return entity;
    }

    /**
     * 返回地区集合
     * <pre>
     *     根据地区id查询该地区的所有下级地区
     * </pre>
     *
     * @param areaId
     * @return
     * @author hzc
     * @createDate 2015-12-31
     */
    public List<SptMgrAreaEntity> getAreaByParent(Integer areaId) {
        List<SptMgrAreaEntity> areaEntities = areaDao.getAreasByParentId(areaId);
        for (SptMgrAreaEntity entity : areaEntities) {
            entity.setText(entity.getName());
            entity.setParentIds(entity.getParentId().toString());
        }
        return areaEntities;
    }


    /**
     * 返回地区
     * <pre>
     *     根据id返回地区
     * </pre>
     *
     * @param areaId
     * @return
     * @author hzc
     * @createDate 2015-12-31
     */
    public SptMgrAreaEntity getAreaById(Integer areaId) {
        return areaDao.queryById(areaId);
    }

    /**
     * 返回保存地区结果
     * <pre>
     *     保存成功返回true，保存失败返回false
     * </pre>
     *
     * @param entity
     * @return
     */
    public boolean saveArea(SptMgrAreaEntity entity) {
        entity = uploadJsonSvg(entity);
        Integer parentId = entity.getParentId();
        List sorts = areaDao.getMaxSort(parentId);
        entity.setSort(getIndex(sorts));
        //保存的地区有父，则获取所有同级，并添加该地区排序，同时添加地区等级
        SptMgrAreaEntity areaParent = getAreaById(parentId);
        if (null != areaParent) {
            areaParent.setLeaf(false);
            areaDao.saveOrUpdate(areaParent, Boolean.FALSE);
        }
        areaDao.saveOrUpdate(entity, Boolean.FALSE);
        return Boolean.TRUE;
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
     * 排序
     */
    public void sortUtil() {
        List<SptMgrAreaEntity> entities = areaDao.getAllAreas();
        tree(entities, -1);
    }


    private void tree(List<SptMgrAreaEntity> list, int id) {
        Date date = new Date();
        for (int i = 0; i < list.size(); i++) {
            SptMgrAreaEntity ent = list.get(i);
            Integer id1 = ent.getParentId();
            if (id == id1) {
                continue;
            } else {
                id = id1;
                List<SptMgrAreaEntity> areaByParent = getAreaByParent(id1);
                for (int j = 0; j < areaByParent.size(); j++) {
                    SptMgrAreaEntity sptMgrAreaEntity = areaByParent.get(j);
                    sptMgrAreaEntity.setSort(j + 1);
                    sptMgrAreaEntity.setUpdateDate(date);
                    sptMgrAreaEntity.setUpdaterId(1);
                    areaDao.update(sptMgrAreaEntity, false);
                }
            }
            Integer id2 = ent.getId();
            List<SptMgrAreaEntity> entitys = getAreaByParent(id2);
            if (entitys.size() > 0) {
                tree(entitys, id1);
            }
        }
    }

    /**
     * 查询地区树（状态为启用）
     *
     * @param areaName
     * @param isCheckTree
     * @return
     */
    public Map<String, Object> /*List*/ queryAreaByName(String areaName, boolean isCheckTree) {
        List<SptMgrAreaEntity> areaList = areaDao.queryAreaByName(areaName);
        Set<SptMgrAreaEntity> areaSet = new HashSet<>();
        areaSet.addAll(areaList);
        Set<SptMgrAreaEntity> allParentArea = getParentArea(areaSet);
        areaList.addAll(allParentArea);
        HashSet h = new HashSet(areaList);
        areaList.clear();
        areaList.addAll(h);
        GenAreaUtil genAreaUtil = new GenAreaUtil();
        genAreaUtil.setIsCheckTree(isCheckTree);
        Map<String, Object> root = new HashMap<>();
        root.put("id", 0);
        root.put("name", "根结点");
//        root.put("parentId",0);
        Map<String, Object> result = genAreaUtil.genTree(root, areaList);
//        return areaList;
        return result;

    }

    private Set<SptMgrAreaEntity> getParentArea(Set<SptMgrAreaEntity> children) {
        Set<SptMgrAreaEntity> parents = new HashSet<>();
        SptMgrAreaEntity entity = null;
        SptMgrAreaEntity child = null;
        Iterator<SptMgrAreaEntity> it = children.iterator();
        while (it.hasNext()) {
            child = it.next();
            entity = this.getAreaById(child.getParentId());
            if (entity != null)
                parents.add(entity);
            else {
                if (child.getParentId() != 0)
                    it.remove();
            }
        }
        if (parents.size() > 0) {
            parents.addAll(getParentArea(parents));
        }
        return parents;
    }

    /**
     * 删除树节点
     * <pre>
     *     批量删除id为i的节点及其子节点
     * </pre>
     *
     * @param i
     * @author hzc
     * @createDate 2016-2-16
     */
    public void deleteArea(int i) throws Exception {
        List<SptMgrAreaEntity> list = new LinkedList<>();
        getItemDictTree(i, list);
        SptMgrAreaEntity itemDictById = getAreaById(i);
        list.add(itemDictById);
        delectItemDictByIds(list);
    }

    /**
     * 返回指标分组目录
     * <pre>
     *     根据id，获取该id所有子的集合
     * </pre>
     *
     * @param id
     * @return
     * @author hzc
     * @createDate 20160-2-16
     */
    private void getItemDictTree(int id, List list) {
        List<SptMgrAreaEntity> itemDictByParent = getAreaByParent(id);
        if (itemDictByParent.size() > 0) {
            for (int i = 0; i < itemDictByParent.size(); i++) {
                SptMgrAreaEntity SptMgrAreaEntity = itemDictByParent.get(i);
                list.add(SptMgrAreaEntity);
                getItemDictTree(SptMgrAreaEntity.getId(), list);
            }
        }
    }

    /**
     * 删除指标分组目录
     * <pre>
     *     批量删除指标分组目录
     * </pre>
     *
     * @param entities
     * @author hzc
     * @createDate 2016-2-16
     */
    private void delectItemDictByIds(List<SptMgrAreaEntity> entities) {
        areaDao.deleteByIds(entities);
    }

    /**
     * 保存地区序列号
     *
     * @param areas
     */
    public void saveGroupSorts(List<SptMgrAreaEntity> areas) {
        if (ListUtil.notEmpty(areas)) {
            ConvertUtil<SptMgrAreaEntity> util = new ConvertUtil<>();
            //转换并保存
            for (SptMgrAreaEntity area : areas) {
                if (area.getId() != null) {
                    SptMgrAreaEntity curGroup = getAreaById(area.getId());
                    //赋值
                    util.apply(curGroup, area, SptMgrAreaEntity.class);
                    areaDao.update(curGroup, false);
                } else {
                    areaDao.insert(area, true);
                }
            }
        }
    }
}
