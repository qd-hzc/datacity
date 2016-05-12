package com.city.resourcecategory.themes.service;

import com.city.common.util.ConvertUtil;
import com.city.common.util.ListUtil;
import com.city.resourcecategory.themes.dao.ThemePageContentDao;
import com.city.resourcecategory.themes.dao.ThemePageDao;
import com.city.resourcecategory.themes.entity.ThemePage;
import com.city.resourcecategory.themes.entity.ThemePageContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

/**
 * Created by CRX on 2016/3/10.
 */

@Service
@Transactional
public class ManageThemesService {

    @Autowired
    private ThemePageDao pageDao;

    @Autowired
    private ThemePageContentDao pageContentDao;

    /**
     * 返回模版集合
     * <pre>
     *     根据parentId，查询模板
     * </pre>
     *
     * @param parentId
     * @return
     * @author CRX
     * @createDate 2016-3-10
     */
    public List<ThemePage> getThemePagesByParentId(int parentId) {
        return pageDao.selectPageByParentId(parentId);

    }

    /**
     * 返回一个菜单
     *
     * @param id
     * @return
     * @author CRX
     * @createDate 2016-3-10
     */
    public ThemePage getThemePageById(int id) {
        return pageDao.queryById(id);
    }

    /**
     * 保存或者更新菜单
     *
     * @param page
     * @author CRX
     * @createDate 2016-3-10
     */
    public ThemePage saveOrUpdateManageTheme(ThemePage page) {
        Integer id = page.getId();
        if (null != id) {
            ConvertUtil<ThemePage> convertUtil = new ConvertUtil<>();
            ThemePage themePage = getThemePageById(id);
            convertUtil.replication(page, themePage, ThemePage.class.getName());
            pageDao.saveOrUpdate(themePage, false);
            return themePage;
        } else {
            //设置当前page的索引
            //查询所有同级page
            Integer parentId = page.getParentId();
            ThemePage parent = getThemePageById(parentId);
            if (null != parent) {
                parent.setLeaf(Boolean.FALSE);
                pageDao.saveOrUpdate(parent, false);
            }
            List sorts = pageDao.getMaxSort(parentId);
            page.setSortIndex(getIndex(sorts));

            page.setStatus(1);
            page.setLeaf(Boolean.TRUE);
            pageDao.saveOrUpdate(page, false);
            return page;
        }

    }

    /**
     * 根据查询的List 获取顺序
     *
     * @param sorts
     * @return
     */
    private int getIndex(List sorts) {
        int s = 1;
        if (sorts != null && sorts.size() > 0) {
            Integer sort = (Integer) sorts.get(0);
            if (sort != null) {
                s += sort;
            }
        }
        return s;
    }

    /**
     * 删除模板及其所有子
     *
     * @param id
     * @author CRX
     * @createDate 2016-3-10
     */
    public void deleteThemePages(Integer id) {
        ThemePage page = getThemePageById(id);
        ArrayList<ThemePage> pages = new ArrayList<>();
        getAllThemePages(id, pages);
        pages.add(page);
        LinkedList<Integer> list = new LinkedList<>();
        for (ThemePage p : pages) {
            list.add(p.getId());
        }
        deleteThemePages(list);
    }

    /**
     * 获取所有子
     *
     * @param id   父亲
     * @param list
     * @author CRX
     * @createDate 2016-3-10
     */
    public void getAllThemePages(Integer id, List list) {
        List<ThemePage> themePages = getThemePagesByParentId(id);
        for (ThemePage page : themePages) {
            list.add(page);
            getAllThemePages(page.getId(), list);
        }
    }

    /**
     * 删除模板菜单
     *
     * @param id
     * @author CRX
     * @createDate 2016-3-10
     */
    public void deleteThemePageById(Integer id) {
        pageDao.deleteById(id);
    }

    /**
     * 批量删除模板菜单
     *
     * @param ids
     * @author crx
     * @createDate 2016-3-11
     */
    public void deleteThemePages(List<Integer> ids) {
        StringBuffer string = new StringBuffer();
        for (Integer id : ids) {
            string.append(id);
            string.append(",");
            pageContentDao.clearContentsByThemePageId(id);
        }
        string.append("-11");
        pageDao.deleteThemePages(string.toString());
    }


    /**
     * 保存模板菜单配置
     *
     * @param page
     * @return
     * @author crx
     * @createDate 2016-3-14
     */
    public ThemePage saveOrUpdate(ThemePage page) {
        ThemePage themePage = getThemePageById(page.getId());
        ConvertUtil<ThemePage> util = new ConvertUtil<>();
        Set<ThemePageContent> contents = page.getContents();
        if (null != contents && contents.size() > 0) {
            Iterator<ThemePageContent> it = contents.iterator();
            pageContentDao.clearContentsByThemePageId(page.getId());
            while (it.hasNext()) {
                ThemePageContent obj = it.next();
                pageContentDao.saveOrUpdate(obj, Boolean.FALSE);
            }
        }
        util.replication(page, themePage, ThemePage.class.getName());
        pageDao.saveOrUpdate(themePage, Boolean.FALSE);
        return themePage;
    }

    /**
     * 返回主题配置集合
     * <pre>
     *     根据主题ids返回所有配置主题
     * </pre>
     *
     * @param contentValue 主题ids:1,2,3,4
     * @return
     * @author hzc
     * @createDate 2016-3-21
     */
    public List<ThemePage> getThemesPageByIds(String contentValue) {
        return pageDao.selectPagesByIds(contentValue);
    }

    /**
     * 返回主题配置
     * <pre>
     *     根据配置id，查询所有父id为id的所有配置
     *     配置的状态为可用
     * </pre>
     *
     * @param id 配置主题id
     * @return
     * @author hzc
     * @createDate 2016-4-7
     */
    public List<ThemePage> getThemePagesByParentIdAndStatus(Integer id) {
        return pageDao.selectPagesByPIdAndStatus(id);
    }

    /**
     * 主题配置排序
     *
     * @param pages
     */
    public void saveThemeSort(List<ThemePage> pages) {
        if (ListUtil.notEmpty(pages)) {
            ConvertUtil<ThemePage> util = new ConvertUtil<>();
            //转换并保存
            for (ThemePage group : pages) {
                if (group.getId() != null) {
                    ThemePage curGroup = getThemePageById(group.getId());
                    //赋值
                    util.apply(curGroup, group, ThemePage.class);
                    pageDao.update(curGroup, false);
                } else {
                    pageDao.insert(group, true);
                }
            }
        }
    }

    /**
     * 查询名称是否重复  根据名称及父id
     * @param name
     * @param parentId
     * @return
     * @author CRX
     * @createDate 2016-5-10
     */
    public List<ThemePage> getThemePagesByNameAndPId(String name, Integer parentId) {
        return pageDao.queryByNameAndParentId(name, parentId);
    }
}
