package com.city.resourcecategory.analysis.text.service;

import com.city.resourcecategory.analysis.text.dao.TextLabelLinkDao;
import com.city.resourcecategory.analysis.text.entity.TextLabelLink;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by wgx on 2016/3/21.
 */
@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
public class TextLabelLinkService {
    @Autowired
    private TextLabelLinkDao textLabelLinkDao;

    /**
     * 根据内容id查询所有标签
     *
     */
    public List<TextLabelLink> queryTextLabelsByContentId(Integer id) {
        return textLabelLinkDao.queryByContentId(id);
    }

}
