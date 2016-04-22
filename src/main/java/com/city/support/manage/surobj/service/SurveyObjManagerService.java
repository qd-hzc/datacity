package com.city.support.manage.surobj.service;

import com.city.common.event.EsiEvent;
import com.city.common.event.watcher.EsiEventWatched;
import com.city.common.event.watcher.SurObjWatched;
import com.city.common.util.ConvertUtil;
import com.city.support.manage.area.dao.AreaDao;
import com.city.support.manage.area.entity.SptMgrAreaEntity;
import com.city.support.manage.surobj.dao.ExtendSurveyObjDao;
import com.city.support.manage.surobj.dao.SurveyObjDao;
import com.city.support.manage.surobj.dao.SurveyObjGroupDao;
import com.city.support.manage.surobj.entity.ExtendSurveyObj;
import com.city.support.manage.surobj.entity.SurveyObj;
import com.city.support.manage.surobj.entity.SurveyObjGroup;
import com.city.support.sys.user.entity.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by wys on 2016/1/15.
 */
@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
public class SurveyObjManagerService {
    @Autowired
    private ExtendSurveyObjDao extendSurveyObjDao;
    @Autowired
    private AreaDao areaDao;
    @Autowired
    private SurveyObjGroupDao surveyObjGroupDao;
    @Autowired
    private SurveyObjDao surveyObjDao;
    EsiEventWatched esiEventWatched;

    @Autowired
    public SurveyObjManagerService(SurObjWatched surObjWatched) {
        esiEventWatched = surObjWatched;
    }

    /**
     * @return
     */
    public List<ExtendSurveyObj> queryExtSurveyObj() {
        return extendSurveyObjDao.queryAll();
    }

    /**
     * @param data
     * @param areaId
     */
    public void addExtSurveyObj(ExtendSurveyObj data, Integer areaId) {
        SptMgrAreaEntity area = areaDao.queryById(areaId);
        data.setSurArea(area);
        extendSurveyObjDao.insert(data, true);
    }

    /**
     * @param data
     */
    public void updateExtSurveyObj(ExtendSurveyObj data, String surAreaIdStr) {

        ExtendSurveyObj extendSurveyObj = extendSurveyObjDao.queryById(data.getId());
        ConvertUtil<ExtendSurveyObj> convertUtil = new ConvertUtil<>();
        convertUtil.replication(data, extendSurveyObj, ExtendSurveyObj.class.getName());
        SptMgrAreaEntity area = null;
        if (surAreaIdStr != null && !"".equals(surAreaIdStr)) {
            area = areaDao.queryById(Integer.parseInt(surAreaIdStr));
            if (area != null) {
                extendSurveyObj.setSurArea(area);
            }
        }
        extendSurveyObjDao.update(extendSurveyObj, true);
    }

    /**
     * @param data
     */
    public void removeExtSurveyObj(ExtendSurveyObj data) {
        extendSurveyObjDao.delete(data, true);
    }

    public List<SurveyObjGroup> querySurGroupByName(String groupName) {
        return surveyObjGroupDao.querySurObjGroupByName(groupName);
    }

    public void addSurObjGroup(SurveyObjGroup surObjGroup) {
        surveyObjGroupDao.insert(surObjGroup, true);
    }

    public void updateSurObjGroup(List<SurveyObjGroup> surObjGroupList) {
        ConvertUtil<SurveyObjGroup> convertUtil = new ConvertUtil<>();

        for (SurveyObjGroup surObjGroup : surObjGroupList) {
            SurveyObjGroup data = surveyObjGroupDao.queryById(surObjGroup.getId());
            convertUtil.replication(surObjGroup, data, SurveyObjGroup.class.getName());
            surveyObjGroupDao.update(data, true);
        }
    }

    public void delSurObjGroup(List<SurveyObjGroup> surObjGroupList) {
        EsiEvent e = null;

        for (SurveyObjGroup surveyObjGroup : surObjGroupList) {
            e = new EsiEvent();
            e.setEventName(SurObjWatched.B4DELSURGROUP);
            e.getArgs().put(SurObjWatched.PARAM_SURGROUP, surveyObjGroup);
            esiEventWatched.notifyAllListener(e);
            surveyObjDao.delSurObjByGroupId(surveyObjGroup.getId());
            surveyObjGroupDao.delete(surveyObjGroup, true);
        }
    }

    public void addSurObj(List<SurveyObj> surObjList) {
        boolean isExist = false;
        for (SurveyObj surveyObj : surObjList) {
            isExist = surveyObjDao.isExist(surveyObj);
            if (!isExist)
                surveyObjDao.insert(surveyObj, true);
        }
    }

    public void delSurObj(List<SurveyObj> surObjList) {
        for (SurveyObj surveyObj : surObjList) {
            surveyObjDao.delete(surveyObj, true);
        }
    }

    public List<SurveyObj> querySurObjByGroupId(Integer groupId) {

        return surveyObjDao.querySurObjByGroupId(groupId);
    }

    public void updateSurObj(List<SurveyObj> surObjList) {
        ConvertUtil<SurveyObj> convertUtil = new ConvertUtil<>();
        for (SurveyObj surveyObj : surObjList) {
            SurveyObj data = surveyObjDao.queryById(surveyObj.getId());
            convertUtil.replication(surveyObj, data, SurveyObj.class.getName());
            surveyObjDao.update(data, true);
        }
    }

    public List<SurveyObj> queryAllSurObj() {
        return surveyObjDao.queryAll();
    }
}
