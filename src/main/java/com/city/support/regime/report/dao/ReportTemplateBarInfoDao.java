package com.city.support.regime.report.dao;

import com.city.common.dao.BaseDao;
import com.city.support.regime.report.entity.ReportTemplateBarInfo;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wxl on 2016/1/14 0014.
 */
@Repository
public class ReportTemplateBarInfoDao extends BaseDao<ReportTemplateBarInfo> {

    /**
     * 返回报表主宾栏信息
     *
     * @param styleId    表样id,可为空
     * @param isMainBar  类型：1：主栏；0：宾栏,可为空
     * @param parentId   父节点,可为空
     * @param isProperty 是否为属性节点
     */
    public List<ReportTemplateBarInfo> getBarInfos(Integer styleId, Integer parentId, Integer isMainBar, Integer isProperty) {
        StringBuilder sb = new StringBuilder("from ReportTemplateBarInfo where 1=1");
        if (styleId != null) {
            sb.append(" and styleId=").append(styleId);
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
    /**
     * 根据表样id查询是否有主宾栏
     */
    public boolean isHasBar(Integer styleId) {
        StringBuilder sb = new StringBuilder("from ReportTemplateBarInfo where 1=1");
        if (styleId != null) {
            sb.append(" and styleId=").append(styleId);
        }
        List l=queryByHQL(sb.toString());
        if(l.size()>0){
            return true;
        }
        return false;
    }


    /**
     * 清除当前样式的主宾蓝信息
     */
    public void clearStyleBarInfos(String styleIds) {
        updateByHQL("delete from ReportTemplateBarInfo where styleId in (" + styleIds + ")");
    }

}
