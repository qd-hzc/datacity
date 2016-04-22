package com.city.support.manage.surobj.dao;

import com.city.common.dao.BaseDao;
import com.city.support.manage.surobj.entity.SurveyObjGroup;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wys on 2016/1/15.
 */
@Repository
public class SurveyObjGroupDao extends BaseDao<SurveyObjGroup> {
    public List<SurveyObjGroup> querySurObjGroupByName(String groupName) {
        String hql = "from SurveyObjGroup t where t.surveyObjGroupName like ? order by t.surveyObjGroupSort";
        Object[] params = {"%" + groupName + "%"};
        return this.queryWithParamsByHQL(hql, params);
    }

}
