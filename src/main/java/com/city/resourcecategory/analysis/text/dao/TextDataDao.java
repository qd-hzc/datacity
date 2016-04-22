package com.city.resourcecategory.analysis.text.dao;

import com.city.common.dao.BaseDao;
import com.city.common.util.StringUtil;
import com.city.resourcecategory.analysis.text.entity.TextData;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wxl on 2016/3/15.
 */
@Repository
public class TextDataDao extends BaseDao<TextData> {
    public void deleteById(Integer id) {
        String hql = "delete TextData t where t.id = ?";
        Query q = getSession().createQuery(hql);
        q.setInteger(0, id);
        q.executeUpdate();
    }

    public List<TextData> queryByForeignIdAndType(Integer foreignId, Integer foreignType) {
        StringBuilder hql = new StringBuilder("From TextData t where t.foreignId = ? and t.foreignType =?");
        Object[] param = {foreignId, foreignType};
        return queryWithParamsByHQL(hql.toString(), param);
    }

    public List checkTextData(TextData textData) {
        StringBuilder sb = new StringBuilder("select count(*) from TextData where 1=2");
        if (textData != null) {
            Integer dataType = textData.getDataType();
            Integer dataValue = textData.getDataValue();
            Integer foreignId = textData.getForeignId();
            Integer foreignType = textData.getForeignType();
            if (dataType != null && dataValue != null && foreignId != null && foreignType != null) {
                sb.append(" or( dataType ='").append(dataType).append("'");
                sb.append(" and dataValue ='").append(dataValue).append("'");
                sb.append(" and foreignId ='").append(foreignId).append("'");
                sb.append(" and foreignType ='").append(foreignType).append("')");
            }
        }
        return queryByHQL(sb.toString());
    }

    /**
     * 根据 数据内容查询分析主题或分析图表
     *
     * @param foreignType 外键类型
     * @param dataName    内容名称
     * @param dataValues  内容id
     */
    public List queryForeignIds(Integer foreignType, String dataName, String dataValues) {
        StringBuilder sb = new StringBuilder("select foreignId from TextData where foreignType=").append(foreignType);
        if (StringUtil.trimNotEmpty(dataName)) {
            sb.append(" and dataName like '%").append(dataName.trim()).append("%'");
        }
        if (StringUtil.trimNotEmpty(dataValues)) {
            sb.append(" and dataValue in (").append(dataName).append(")");
        }
        return queryByHQL(sb.toString());
    }
}
