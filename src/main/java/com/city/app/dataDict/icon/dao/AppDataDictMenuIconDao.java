package com.city.app.dataDict.icon.dao;

import com.city.app.dataDict.icon.entity.AppDataDictMenuIcon;
import com.city.common.dao.BaseDao;
import com.city.common.util.StringUtil;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wxl on 2016/3/30.
 */
@Repository
public class AppDataDictMenuIconDao extends BaseDao<AppDataDictMenuIcon> {

    /**
     * 根据名字查询
     */
    public List<AppDataDictMenuIcon> queryByType(String name, Integer type) {
        StringBuilder sb = new StringBuilder("from AppDataDictMenuIcon where 1=1");
        if (StringUtil.trimNotEmpty(name)) {
            sb.append(" and name like '%").append(name.trim()).append("%'");
        }
        if (type != null) {
            sb.append(" and type=").append(type);
        }
        return queryByHQL(sb.toString());
    }
}
