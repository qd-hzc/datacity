package com.city.resourcecategory.analysis.text.service;

import com.city.common.pojo.Constant;
import com.city.common.util.ConvertUtil;
import com.city.resourcecategory.analysis.text.dao.TextContentDao;
import com.city.resourcecategory.analysis.text.dao.TextThemeDao;
import com.city.resourcecategory.analysis.text.entity.TextContent;
import com.city.resourcecategory.analysis.text.entity.TextTheme;
import com.city.support.sys.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wgx on 2016/3/16.
 */
@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
public class TextThemeService {

    @Autowired
    TextThemeDao textThemeDao;
    @Autowired
    TextContentDao textContentDao;

    public List<TextTheme> queryAllTextTheme() {
        return textThemeDao.queryAll();
    }

    /**
     * 添加修改分析主题
     * @param datas
     * @return
     */
    public List<TextTheme> updateTextTheme(List<TextTheme> datas,User user) {
        Integer dataId = null;
        List<TextTheme> result = new ArrayList<>();
        for (TextTheme data : datas) {
            dataId = data.getId();
            if (dataId == null) {
                if(user!=null) {
                    data.setCreator(user.getId());
                    data.setCreateTime(new Date());
                }
                textThemeDao.insert(data, true);
                result.add(data);
            } else {
                ConvertUtil<TextTheme> cu = new ConvertUtil<>();
                TextTheme textTheme = textThemeDao.queryById(dataId);
                cu.replication(data, textTheme, TextTheme.class.getName());
                if(user!=null) {
                    textTheme.setUpdator(user.getId());
                    textTheme.setUpdateTime(new Date());
                }
                textThemeDao.update(textTheme, true);
                result.add(textTheme);
            }
        }
        return result;
    }

    /**
     * 删除分析主题
     * @param datas
     */
    public void deleteTextTheme(List<TextTheme> datas) {
        for(TextTheme textTheme: datas){
            Integer id = textTheme.getId();
            textContentDao.deleteByThemeId(id);
            textThemeDao.deleteById(id);

        }
    }

    /**
     * 查询分析主题
     * @param name
     * @return
     */
    public List<TextTheme> queryTextThemeByCondition(String name) {
        List<TextTheme> textThemeList = textThemeDao.queryTextThemeByCondition(name);
        for(TextTheme textTheme: textThemeList){
            long count = (long) textContentDao.queryCountByThemeId(textTheme.getId(), Constant.TEXT_CONTENT_STATUS.WAIT_CHECK+","+Constant.TEXT_CONTENT_STATUS.REJECT).get(0);
            textTheme.setUnChecked((int)count);
        }
        return textThemeDao.queryTextThemeByCondition(name);
    }
    /**
     * 查询分析主题
     * @param modelId
     * @return
     */
    public List<TextTheme> queryTextThemeByModelId(Integer modelId) {
        return textThemeDao.queryTextThemeByModelId(modelId);
    }
    /**
     * 查询分析主题及分析主题下的分析
     * @param id
     * @return
     */
    public TextTheme queryTextThemeById(Integer id) {
        TextTheme textTheme = textThemeDao.queryById(id);
        return textTheme;
    }

    public void deleteTextThemeModelId(Integer modelId,User user) {
        List<TextTheme> textThemeList =queryTextThemeByModelId(modelId);
        for(TextTheme textTheme: textThemeList){
            textTheme.setModelId(null);
        }
        updateTextTheme(textThemeList,user);
    }
}
