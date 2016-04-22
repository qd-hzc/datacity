package com.city.resourcecategory.analysis.text.service;

import com.city.resourcecategory.analysis.text.dao.TextLabelDao;
import com.city.resourcecategory.analysis.text.dao.TextLabelLinkDao;
import com.city.resourcecategory.analysis.text.entity.TextLabel;
import com.city.resourcecategory.analysis.text.entity.TextLabelLink;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by wxl on 2016/3/17.
 */
@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
public class TextLabelService {
    @Autowired
    private TextLabelDao textLabelDao;
    @Autowired
    private TextLabelLinkDao textLabelLinkDao;

    /**
     * 查询所有标签
     *
     * @param name 名称
     * @param tier 标签分级
     */
    public List<TextLabel> queryTextLabels(String name, Integer tier) {
        return textLabelDao.queryTextLabels(name, tier);
    }

    /**
     * 保存标签
     *
     * @param textLabel
     */
    public void saveTextModel(TextLabel textLabel) {
        textLabelDao.saveOrUpdate(textLabel, false);
    }

    /**
     * 删除标签
     *
     * @param textLabels
     */
    public void removeTextLabels(List<TextLabel> textLabels) {
        if (textLabels != null && textLabels.size() > 0) {
            for (TextLabel label : textLabels) {
                textLabelLinkDao.clearLabelLink(label);
                textLabelDao.delete(label, true);
            }
        }
    }

    /**
     * 关联标签
     *
     * @param contentId 分析内容id
     * @param labelIds  标签(可为多个)
     */
    public void linkLabel(Integer contentId, String labelIds) {
        if (labelIds != null && labelIds.trim().length() > 0) {
            String[] labelIdStrs = labelIds.split(",");//标签id
            TextLabelLink link = null;
            TextLabel label = null;
            //首先根据内容id清除
            textLabelLinkDao.clearLabelLink(contentId);
            for (String labelId : labelIdStrs) {
                link = new TextLabelLink();
                label = new TextLabel();
                label.setId(Integer.parseInt(labelId));
                link.setLabel(label);
                link.setContentId(contentId);
                textLabelLinkDao.insert(link, true);
            }
        }
    }
}
