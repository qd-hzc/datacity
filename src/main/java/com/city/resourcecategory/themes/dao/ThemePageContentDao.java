package com.city.resourcecategory.themes.dao;

import com.city.common.dao.BaseDao;
import com.city.resourcecategory.themes.entity.ThemePageContent;
import org.springframework.stereotype.Repository;

/**
 * Created by  CRX on 2016/3/14.
 */
@Repository
public class ThemePageContentDao extends BaseDao<ThemePageContent> {

    /**
     * 清空所有content
     *
     * @param themePageId
     * @author crx
     * @createDate 2016-3-14
     */
    public void clearContentsByThemePageId(Integer themePageId) {
        updateByHQL("delete from ThemePageContent where themePageId = " + themePageId);
    }
}
