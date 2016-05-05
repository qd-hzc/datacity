package com.city.resourcecategory.analysis.report.service;

import com.city.common.util.tree.PackageListToTree;
import com.city.resourcecategory.analysis.report.entity.ResearchGroupEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by HZC on 2016/5/5.
 */
@Component
public class CustomResearchGroupTree extends PackageListToTree<ResearchGroupEntity> {

    @Autowired
    private CustomResearchManageService researchManageService;

    @Override
    public ResearchGroupEntity getEntityById(Integer entityKey) {
        return researchManageService.getResearchGroupById(entityKey);
    }
}
