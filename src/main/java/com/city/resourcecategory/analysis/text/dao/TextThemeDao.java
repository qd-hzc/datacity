package com.city.resourcecategory.analysis.text.dao;

import com.city.common.dao.BaseDao;
import com.city.resourcecategory.analysis.text.entity.TextTheme;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wxl on 2016/3/15.
 */
@Repository
public class TextThemeDao extends BaseDao<TextTheme> {

    public void deleteById(Integer id) {
        String hql = "delete TextTheme t where t.id = ?";
        Query q = getSession().createQuery(hql);
        q.setInteger(0, id);
        q.executeUpdate();
    }

    public List<TextTheme> queryTextThemeByCondition(String name) {
        StringBuilder sb = new StringBuilder("from TextTheme where 1=1");
        if (name != null && !"".equals(name)) {
            sb.append(" and( name like '%").append(name).append("%' or infos like '%").append(name).append("%')");
        }
        sb.append(" order by sortIndex");
        return queryByHQL(sb.toString());
    }

    public List<TextTheme> queryTextThemeByModelId(Integer modelId) {
        StringBuilder sb = new StringBuilder("from TextTheme where modelId=").append(modelId);
        return queryByHQL(sb.toString());
    }

    /**
     * 根据主键集合查询
     */
    public List<TextTheme> queryTextThemeByIds(String ids) {
        StringBuilder sb = new StringBuilder("from TextTheme where id in (").append(ids).append(") order by sortIndex");
        return queryByHQL(sb.toString());
    }
}
