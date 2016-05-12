package com.city.resourcecategory.themes.dao;

import com.city.common.dao.BaseDao;
import com.city.resourcecategory.themes.entity.ThemePage;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by cobra on 2016/3/10.
 */
@Repository
public class ThemePageDao extends BaseDao<ThemePage> {

    /**
     * 返回模板集合
     *
     * @param parentId
     * @return
     * @author CRX
     * @createDate 2016-3-10
     */
    public List<ThemePage> selectPageByParentId(int parentId) {
        return queryByHQL("from ThemePage where parentId = " + parentId + " order by sortIndex asc");
    }

    /**
     * 删除模板菜单
     * <pre>
     *     根据id，删除
     * </pre>
     *
     * @param id
     * @author crx
     * @createDate 2016-3-10
     */
    public void deleteById(int id) {
        updateByHQL("delete from ThemePage where id =" + id);
    }

    /**
     * 批量删除模板菜单
     *
     * @param s
     * @author crx
     * @createDate 2016-3-11
     */
    public void deleteThemePages(String s) {
        updateByHQL("delete from ThemePage where id in(" + s + ")");
    }

    /**
     * 返回所有模板
     * <pre>
     *     根据名称模糊搜索
     * </pre>
     *
     * @param depName
     * @return
     * @author crx
     * @createDate 2016-3-14
     */
    public List<ThemePage> getThemesByName(String depName) {
        StringBuilder sb = new StringBuilder("from ThemePage where 1=1");
        if (depName != null && depName.trim().length() > 0) {
            sb.append(" and name like '%").append(depName).append("%'");
        }
        sb.append(" order by sortIndex");
        return queryByHQL(sb.toString());
    }

    /**
     * 返回首页
     *
     * @return
     * @author hzc
     * @createDate 2016-3-18
     */
    public ThemePage selectIndex() {
        return unqueryByHQL("from ThemePage where name like '首页%' and status = 1");
    }

    /**
     * 返回主题配置集合
     * <pre>
     *     根据主题ids返回所有配置主题
     * </pre>
     *
     * @param contentValue 主题id
     * @return
     * @author hzc
     * @createDate 2016-3-21
     */
    public List<ThemePage> selectPagesByIds(String contentValue) {
        return queryByHQL("from ThemePage where id in (" + contentValue + ")");
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
    public List<ThemePage> selectPagesByPIdAndStatus(Integer id) {
        return queryByHQL("from ThemePage where status = 1 and parentId = " + id);
    }

    /**
     * 获取最大序号
     *
     * @param parentId
     * @return
     * @author hzc
     * @createDate 2016-4-29
     */
    public List getMaxSort(Integer parentId) {
        return queryByHQL("select max(sortIndex) from ThemePage where parentId = " + parentId);
    }

    /**
     * 查询名称是否重复  根据名称及父id
     * @param name
     * @param parentId
     * @return
     * @author CRX
     * @createDate 2016-5-10
     */
    public List<ThemePage> queryByNameAndParentId(String name, Integer parentId) {
        String hql = "from ThemePage t where t.name ='" + name + "' and t.parentId ='" + parentId +"'";
        return super.queryByHQL(hql);
    }
}
