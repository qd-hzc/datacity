package com.city.resourcecategory.analysis.text.dao;

import com.city.common.dao.BaseDao;
import com.city.resourcecategory.analysis.text.entity.TextLabel;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wxl on 2016/3/15.
 */
@Repository
public class TextLabelDao extends BaseDao<TextLabel> {

    /**
     * 查询所有标签
     *
     * @param name 名称
     * @param tier 标签分级
     */
    public List<TextLabel> queryTextLabels(String name, Integer tier) {
        StringBuilder sb = new StringBuilder("from TextLabel where 1=1 ");
        if (name != null && name.trim().length() > 0) {
            sb.append("and name like '%").append(name.trim()).append("%'");
        }
        if (tier != null) {
            sb.append(" and tier=").append(tier);
        }
        sb.append(" order by tier desc");
        return queryByHQL(sb.toString());
    }
}
