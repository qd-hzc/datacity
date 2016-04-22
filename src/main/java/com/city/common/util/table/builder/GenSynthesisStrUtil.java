package com.city.common.util.table.builder;

import com.city.common.util.table.pojo.EsiProperty;
import com.city.common.util.table.pojo.EsiTable;
import com.city.common.util.table.pojo.EsiTdUnit;
import com.city.support.manage.item.dao.ItemInfoDao;
import com.city.support.manage.item.entity.ItemInfo;
import com.city.support.regime.report.dao.ReportTemplateDao;
import com.city.support.regime.report.entity.ReportTemplate;
import com.google.gson.Gson;
import org.jsoup.nodes.Element;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wxl on 2016/2/29.
 * 生成综合表表样 加上单位
 */
public class GenSynthesisStrUtil extends GenTableStrUtil<ReportTemplate> {

    public GenSynthesisStrUtil(EsiTable esiTable, HttpServletRequest request) {
        super(esiTable, request);
    }

    @Override
    protected void initTmp(Integer tmpId) {
        ReportTemplateDao tmpDao = ctx.getBean(ReportTemplateDao.class);
        tmp = tmpDao.queryById(tmpId);
    }

    @Override
    protected String getTHeadText() {
        return tmp.getName();
    }

    @Override
    protected String getTFootText() {
        return tmp.getRptComments();
    }

}
