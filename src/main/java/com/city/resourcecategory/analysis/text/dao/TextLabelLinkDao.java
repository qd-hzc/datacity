package com.city.resourcecategory.analysis.text.dao;

import com.city.common.dao.BaseDao;
import com.city.resourcecategory.analysis.text.entity.TextLabel;
import com.city.resourcecategory.analysis.text.entity.TextLabelLink;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wxl on 2016/3/15.
 */
@Repository
public class TextLabelLinkDao extends BaseDao<TextLabelLink> {

    /**
     * 清除关联内容
     *
     * @param contentId
     */
    public void clearLabelLink(Integer contentId) {
        StringBuilder sb = new StringBuilder("delete from TextLabelLink where contentId=").append(contentId);
        updateByHQL(sb.toString());
    }
    /**
     * 清除关联内容
     *
     * @param label
     */
    public void clearLabelLink(TextLabel label) {
        StringBuilder sb = new StringBuilder("delete from TextLabelLink where label.id=").append(label.getId());
        updateByHQL(sb.toString());
    }
    public List<TextLabelLink> queryByContentId(Integer contentId) {
        StringBuilder sb = new StringBuilder("from TextLabelLink where 1=1 ");
        if (contentId != null) {
            sb.append("and contentId =").append(contentId);
        }
        return queryByHQL(sb.toString());
    }
}
