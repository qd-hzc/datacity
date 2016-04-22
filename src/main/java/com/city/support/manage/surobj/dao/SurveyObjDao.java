package com.city.support.manage.surobj.dao;

import com.city.common.dao.BaseDao;
import com.city.support.manage.surobj.entity.SurveyObj;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wys on 2016/1/15.
 */
@Repository
public class SurveyObjDao extends BaseDao<SurveyObj> {
    public List<SurveyObj> querySurObjByGroupId(Integer groupId) {
        String hql = "from SurveyObj t where t.surveyObjGroupId = ? order by t.surveyObjSort";
        Object[] param = {groupId};
        return this.queryWithParamsByHQL(hql, param);
    }

    public void delSurObjByGroupId(Integer id) {
        String hql = " delete SurveyObj t where t.surveyObjGroupId = ?";
        Object[] param = {id};
        this.updateWithParamsByHQL(hql,param);
    }

    public boolean isExist(SurveyObj surveyObj) {
        String hql = "from SurveyObj t where t.surveyObjGroupId =? and t.surveyObjId =?";
        Object[] params = {surveyObj.getSurveyObjGroupId(),surveyObj.getSurveyObjId()};
        List<SurveyObj> list = this.queryWithParamsByHQL(hql,params);
        if(list==null||list.size()<1){
            return false;
        }else{
            return true;
        }
    }
}
