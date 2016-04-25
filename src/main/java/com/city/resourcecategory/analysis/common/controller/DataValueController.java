package com.city.resourcecategory.analysis.common.controller;

import com.city.common.controller.BaseController;
import com.city.common.pojo.Constant;
import com.city.common.util.ListUtil;
import com.city.common.util.StringUtil;
import com.city.resourcecategory.analysis.chart.service.BasicChartConfigService;
import com.city.resourcecategory.analysis.report.dao.CustomResearchDao;
import com.city.resourcecategory.analysis.text.dao.TextContentDao;
import com.city.resourcecategory.analysis.text.dao.TextThemeDao;
import com.city.support.dataSet.dao.DataSetDao;
import com.city.support.regime.report.dao.ReportTemplateDao;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wxl on 2016/3/23.
 * 根据dataType获取dataValue
 */
@Controller
public class DataValueController extends BaseController {
    @Autowired
    private ReportTemplateDao reportTemplateDao;
    @Autowired
    private CustomResearchDao customResearchDao;
    @Autowired
    private BasicChartConfigService basicChartConfigService;
    @Autowired
    private TextThemeDao textThemeDao;
    @Autowired
    private TextContentDao textContentDao;
    @Autowired
    private DataSetDao dataSetDao;

    /**
     * 根据名称和数据类型查询,在前台过滤
     *
     * @param dataType 数据类型
     */
    @RequestMapping("/resourcecategory/analysis/common/analysis/queryDataByType")
    @ResponseBody
    public List queryDataByType(Integer dataType, String name) {
        switch (dataType) {
            case Constant.THEME_CONTENT_TYPE.RPT_SYNTHESIZE://综合表
                return filterByName(reportTemplateDao.queryAll(), name);
            case Constant.THEME_CONTENT_TYPE.RPT_CUSTOM://分析报表
                return filterByName(customResearchDao.selectAll(), name);
            case Constant.THEME_CONTENT_TYPE.CHART://分析图表
                return filterByName(basicChartConfigService.queryAllChartExceptMap(), name);
            case Constant.THEME_CONTENT_TYPE.TEXT_THEME://分析主题
                return filterByName(textThemeDao.queryAll(), name);
            case Constant.THEME_CONTENT_TYPE.TEXT_DESC://文字分析
                return filterByName(textContentDao.queryByThemeId(null,null, null, null, Constant.TEXT_CONTENT_STATUS.CHECKED), name);
            case Constant.THEME_CONTENT_TYPE.DATA_SET://数据集
                return filterByName(dataSetDao.queryAll(), name);
            case Constant.THEME_CONTENT_TYPE.FILE://文件
            case Constant.THEME_CONTENT_TYPE.MAP://地图
            case Constant.THEME_CONTENT_TYPE.MENU://目录
            case Constant.THEME_CONTENT_TYPE.PAGE://页面
            default:
                return null;
        }
    }

    /**
     * 根据名字过滤
     */
    private List filterByName(List list, String name) {
        if (StringUtil.trimNotEmpty(name)) {
            List result = new ArrayList<>();
            try {
                if (ListUtil.notEmpty(list)) {
                    for (Object obj : list) {
                        Map map = BeanUtils.describe(obj);
                        if (((String) map.get("name")).contains(name.trim())) {
                            result.add(obj);
                        }
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            return result;
        }
        return list;
    }
}
