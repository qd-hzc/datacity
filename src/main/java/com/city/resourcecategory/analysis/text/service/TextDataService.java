package com.city.resourcecategory.analysis.text.service;

import com.city.common.pojo.Constant;
import com.city.common.util.ListUtil;
import com.city.resourcecategory.analysis.text.dao.TextContentDao;
import com.city.resourcecategory.analysis.text.dao.TextDataDao;
import com.city.resourcecategory.analysis.text.dao.TextThemeDao;
import com.city.resourcecategory.analysis.text.entity.TextContent;
import com.city.resourcecategory.analysis.text.entity.TextData;
import com.city.support.sys.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wgx on 2016/3/18.
 */
@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
public class TextDataService {
    @Autowired
    TextDataDao textDataDao;
    @Autowired
    TextThemeDao textThemeDao;
    @Autowired
    TextContentDao textContentDao;

    public List<TextData> addTextData(List<TextData> datas, User user) {
        Integer dataId = null;
        List<TextData> result = new ArrayList<>();
        for (TextData data : datas) {
            dataId = data.getId();
            if (dataId == null && !hasTextData(data)) {
                textDataDao.insert(data, true);
                result.add(data);
            }/* else {
                ConvertUtil<TextData> cu = new ConvertUtil<>();
                TextData textData = textDataDao.queryById(dataId);
                cu.replication(data, textData, TextData.class.getName());
                textDataDao.update(textData, true);
                result.add(textData);
            }*/
        }
        return result;
    }

    public Boolean hasTextData(List<TextData> textDataList) {
        for (TextData textData : textDataList) {
            Boolean isExist = hasTextData(textData);
            if (isExist) {
                return true;
            }
        }
        return false;
    }

    public Boolean hasTextData(TextData textData) {
        long count = 0;
        count = (long) textDataDao.checkTextData(textData).get(0);
        if (textData.getForeignType() == Constant.TEXT_TYPE.CONTENT) {
            TextContent textContent = textContentDao.queryById(textData.getForeignId());
            TextData themeTextData = new TextData();
            themeTextData.setDataType(textData.getDataType());
            themeTextData.setDataValue(textData.getDataValue());
            themeTextData.setForeignId(textContent.getTheme().getId());
            themeTextData.setForeignType(Constant.TEXT_TYPE.THEME);
            long countTheme = (long) textDataDao.checkTextData(themeTextData).get(0);
            count += countTheme;
        }
        if (count > 0) {
            return true;
        }
        return false;
    }

    public void deleteTextData(List<TextData> datas) {
        for (TextData textData : datas) {
            Integer id = textData.getId();
            textDataDao.deleteById(id);
        }
    }

    public List<TextData> queryByForeignIdAndType(Integer foreignId, Integer foreignType) {
        return textDataDao.queryByForeignIdAndType(foreignId, foreignType);
    }

    /**
     * 根据 数据内容查询分析主题或分析图表
     *
     * @param foreignType 外键类型
     * @param dataName        内容名称
     * @param dataValues  内容id
     */
    public List queryForeignObjs(Integer foreignType, String dataName, String dataValues) {
        List<Integer> idList = textDataDao.queryForeignIds(foreignType, dataName, dataValues);
        if (ListUtil.notEmpty(idList)) {
            String ids = ListUtil.getArrStr(idList);
            if (foreignType == 1) {//分析主题
                return textThemeDao.queryTextThemeByIds(ids);
            }
            //分析内容
            return textContentDao.queryByIds(ids);
        }
        return null;
    }
}
