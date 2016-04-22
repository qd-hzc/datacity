package com.city.resourcecategory.analysis.text.service;

import com.city.resourcecategory.analysis.text.dao.TextModelDao;
import com.city.resourcecategory.analysis.text.entity.TextModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by wxl on 2016/3/17.
 */
@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
public class TextModelService {
    @Autowired
    private TextModelDao textModelDao;
    @Autowired
    private TextThemeService textThemeService;
    /**
     * 根据名称查询模板
     *
     * @param name
     */
    public List<TextModel> queryTextModel(String name, Integer status) {
        return textModelDao.queryTextModel(name, status);
    }

    /**
     * 保存模板
     *
     * @param textModel 文字分析模板
     * @param userId    用户id
     */
    public void saveTextModel(TextModel textModel, Integer userId) {
        if (textModel.getId() != null) {
            TextModel model = textModelDao.queryById(textModel.getId());
            model.setName(textModel.getName());
            model.setStatus(textModel.getStatus());
            model.setContent(textModel.getContent());
            model.setUpdateTime(new Date());
            model.setUpdator(userId);
            textModelDao.update(model, false);
        } else {
            textModel.setCreateTime(new Date());
            textModel.setCreator(userId);
            textModelDao.insert(textModel, false);
        }
    }

    /**
     * 删除模板
     */
    public void removeTextModels(List<TextModel> textModels) {
        if (textModels != null && textModels.size() > 0) {
            for (TextModel model : textModels) {
                textModelDao.delete(model, true);
                textThemeService.deleteTextThemeModelId(model.getId(),null);
            }
        }
    }
}
