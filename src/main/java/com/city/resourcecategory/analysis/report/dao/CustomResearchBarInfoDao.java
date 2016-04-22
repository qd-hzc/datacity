package com.city.resourcecategory.analysis.report.dao;

import com.city.common.dao.BaseDao;
import com.city.resourcecategory.analysis.report.entity.CustomResearchBarInfoEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by HZC on 2016/3/3.
 */
@Repository
public class CustomResearchBarInfoDao extends BaseDao<CustomResearchBarInfoEntity> {
    /**
     * 清空主宾栏信息
     *
     * @param s
     * @mender hzc
     * @modifiedDate 2016-3-3
     */
    public void clearStyleBarInfos(String s) {
        updateByHQL("delete from CustomResearchBarInfoEntity where customResearchId in (" + s + ")");
    }

    /**
     * 返回报表主宾栏信息
     *
     * @param customResearchId    表样id,可为空
     * @param parentId   父节点,可为空
     * @param isMainBar  类型：1：主栏；0：宾栏,可为空
     * @param isProperty 是否为属性节点
     * @mender hzc
     * @modifiedDate 2016-3-3
     */
    public List<CustomResearchBarInfoEntity> getBarInfos(Integer customResearchId, Integer parentId, Integer isMainBar, Integer isProperty) {
        StringBuilder sb = new StringBuilder("from CustomResearchBarInfoEntity where 1=1");
        if (customResearchId != null) {
            sb.append(" and customResearchId=").append(customResearchId);
        }
        if (isMainBar != null) {
            sb.append(" and isMainBar=").append(isMainBar);
        }
        if (parentId != null) {
            sb.append(" and parentId=").append(parentId);
        }
        if (isProperty != null) {
            sb.append(" and isProperty=").append(isProperty);
        }
        sb.append(" order by sortIndex");
        return queryByHQL(sb.toString());
    }

    public void deleteByResearchId(Integer id) {
        updateByHQL("delete from CustomResearchBarInfoEntity where customResearchId=" + id);
    }
}
