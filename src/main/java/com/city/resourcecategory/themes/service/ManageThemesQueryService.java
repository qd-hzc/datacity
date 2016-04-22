package com.city.resourcecategory.themes.service;

import com.city.common.util.tree.PackageListToTree;
import com.city.resourcecategory.themes.dao.ThemePageDao;
import com.city.resourcecategory.themes.entity.ThemePage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 模板picker查询类
 * Created by CRX on 2016/3/14.
 */
@Service
public class ManageThemesQueryService extends PackageListToTree<ThemePage> {

    @Autowired
    private ManageThemesService themesService;

    @Autowired
    private ThemePageDao pageDao;

    /**
     * 返回所有模板
     * <pre>
     *     根据名称模糊搜索
     * </pre>
     *
     * @param name
     * @return
     * @author crx
     * @createDate 2016-3-14
     */
    public List<ThemePage> getThemesTreeByName(String name) {
        return pageDao.getThemesByName(name);
    }

    @Override
    protected ThemePage getEntityById(Integer entityKey) {
        return themesService.getThemePageById(entityKey);
    }

    @Override
    protected Integer getEntityKey(ThemePage page) {
        return page.getId();
    }

    @Override
    protected Integer getParentId(ThemePage page) {
        return page.getParentId();
    }

    @Override
    protected String getEntityName(ThemePage page) {
        return page.getName();
    }

    @Override
    public Map<String, Object> getEntityMap(ThemePage page) {
        Map<String, Object> map = super.getEntityMap(page);
        map.put("name", page.getName());
        map.put("checked", false);
        return map;
    }
}
