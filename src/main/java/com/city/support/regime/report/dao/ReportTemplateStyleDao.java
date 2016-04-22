package com.city.support.regime.report.dao;

import com.city.common.dao.BaseDao;
import com.city.support.regime.report.entity.ReportTemplateStyle;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wxl on 2016/1/14 0014.
 */
@Repository
public class ReportTemplateStyleDao extends BaseDao<ReportTemplateStyle> {

    /**
     * 根据模板id获取表样
     *
     * @param tmpIds 报表模板id
     */
    public List<ReportTemplateStyle> getRptStyleByTmp(String tmpIds) {
        String hql = "from ReportTemplateStyle where reportTemplate.id in (" + tmpIds + ") order by beginYear,beginPeriod";
        return queryByHQL(hql);
    }

    /**
     * 删除表样
     */
    public void removeRptTmpStyles(String ids) {
        String hql = "delete from ReportTemplateStyle where id in (" + ids + ")";
        updateByHQL(hql);
    }

    /**
     * 返回报表模板表样集合
     * <pre>
     *     根据表样id集合，查询集合
     * </pre>
     *
     * @param styleIds 表样ids
     * @return
     * @author hzc
     * @createDate 2016-4-18
     */
    public List<ReportTemplateStyle> selectStylesByIds(String styleIds) {
        return queryByHQL("from ReportTemplateStyle where id in (" + styleIds + ")");
    }
}
