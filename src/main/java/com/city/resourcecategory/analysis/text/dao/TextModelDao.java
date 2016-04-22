package com.city.resourcecategory.analysis.text.dao;

import com.city.common.dao.BaseDao;
import com.city.resourcecategory.analysis.text.entity.TextModel;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wxl on 2016/3/15.
 */
@Repository
public class TextModelDao extends BaseDao<TextModel> {
    /**
     * 根据名称查询模板
     *
     * @param name
     */
    public List<TextModel> queryTextModel(String name, Integer status) {
        StringBuilder sb = new StringBuilder("from TextModel where 1=1");
        if (name != null && name.trim().length() > 0) {
            sb.append(" and name like '%").append(name).append("%'");
        }
        if (status != null) {
            sb.append(" and status = ").append(status);
        }
        sb.append(" order by id desc");
        return queryByHQL(sb.toString());
    }
}
