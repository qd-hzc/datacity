package com.city.common.util.table.builder;

import com.city.common.pojo.Constant;
import com.city.common.util.table.pojo.EsiProperty;
import com.city.common.util.table.pojo.EsiTable;
import com.city.resourcecategory.analysis.report.dao.CustomResearchDao;
import com.city.resourcecategory.analysis.report.entity.CustomResearchEntity;
import org.jsoup.nodes.Element;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by wxl on 2016/2/29.
 * 自定义查询 生成
 */
public class GenRCStrUtil extends GenTableStrUtil<CustomResearchEntity> {

    public GenRCStrUtil(EsiTable esiTable, HttpServletRequest request) {
        super(esiTable, request);
    }

    @Override
    protected void initTmp(Integer tmpId) {
        CustomResearchDao customResearchDao = ctx.getBean(CustomResearchDao.class);
        tmp = customResearchDao.queryById(tmpId);
    }

    @Override
    protected String getTHeadText() {
        return tmp.getName();
    }

    @Override
    protected String getTFootText() {
        return null;
    }
}
