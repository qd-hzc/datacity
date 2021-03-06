package com.city.resourcecategory.analysis.text.service;

import com.city.common.pojo.Constant;
import com.city.common.pojo.Page;
import com.city.common.util.ConvertUtil;
import com.city.common.util.ListUtil;
import com.city.resourcecategory.analysis.text.dao.TextContentDao;
import com.city.resourcecategory.analysis.text.dao.TextLabelLinkDao;
import com.city.resourcecategory.analysis.text.dao.TextModelDao;
import com.city.resourcecategory.analysis.text.dao.TextThemeDao;
import com.city.resourcecategory.analysis.text.entity.TextContent;
import com.city.resourcecategory.analysis.text.entity.TextLabelLink;
import com.city.resourcecategory.analysis.text.entity.TextModel;
import com.city.resourcecategory.analysis.text.entity.TextTheme;
import com.city.support.dataSet.query.pojo.TimePojo;
import com.city.support.dataSet.query.pojo.TimeRangePojo;
import com.city.support.dataSet.query.service.QueryRptService;
import com.city.support.sys.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by wgx on 2016/3/16.
 */
@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
public class TextContentService {
    @Autowired
    private TextContentDao textContentDao;
    @Autowired
    private TextLabelLinkService textLabelLinkService;
    @Autowired
    private TextLabelService textLabelService;
    @Autowired
    private TextLabelLinkDao textLabelLinkDao;
    @Autowired
    private TextThemeDao textThemeDao;
    @Autowired
    private TextModelDao textModelDao;
    @Autowired
    private QueryRptService queryRptService;

    public List<TextContent> queryAllTextContentByThemeId(User user, Integer themeId, String contentSortType, String name, Integer status) {
        List<TextContent> result = null;
        if (themeId != null) {
            result = textContentDao.queryByThemeId(user, themeId, contentSortType, name, status);
           try{
               for (TextContent textContent : result) {
                   // 如果内容为空，读取关联模板的内容
                   if ((textContent.getContent() == null || "".equals(textContent.getContent())) && textContent.getStatus() != Constant.TEXT_CONTENT_STATUS.CHECKED) {
                       TextTheme textTheme = textThemeDao.queryById(textContent.getTheme().getId());
                       if (textTheme.getModelId() != null) {
                           TextModel textModel = textModelDao.queryById(textTheme.getModelId());
                           textContent.setContent(textModel.getContent());
                       }
                   }
                   List<TextLabelLink> textLabelLinkList = textLabelLinkService.queryTextLabelsByContentId(textContent.getId());
                   String labelIds = "";
                   for (TextLabelLink textLabelLink : textLabelLinkList) {
                       labelIds += textLabelLink.getLabel().getId() + ",";
                   }
                   if (!"".equals(labelIds)) {
                       labelIds = labelIds.substring(0, labelIds.length() - 1);
                   }
                   textContent.setLabelIds(labelIds);
               }
           }catch (Exception e){
               e.printStackTrace();
           }

        }
        return result;
    }

    /**
     * 根据分析主题和时间获取
     *
     * @param themeId   主题id
     * @param timePojos 时间
     * @param status    审核状态
     */
    public List<TextContent> queryByTime(Integer themeId, List<TimePojo> timePojos, Integer status) {
        TextTheme theme = textThemeDao.queryById(themeId);
        if (theme != null) {
            if (status == null) {
                status = Constant.TEXT_CONTENT_STATUS.CHECKED;
            }
            if (ListUtil.notEmpty(timePojos)) {
                TimePojo t = timePojos.get(0);//第一个
                if (t instanceof TimeRangePojo) {
                    TimeRangePojo t0 = (TimeRangePojo) t;
                    if (t0.getType() == Constant.TIMERANGE.BAOGAOQI) {//报告期数,按时间排序,返回最新的数个
                        return textContentDao.queryByTime(themeId, t0, status);
                    }
                }
                //转换时间,根据时间获取
                List<TimePojo> times = queryRptService.convertTime(timePojos, null, null);
                return textContentDao.queryByTime(themeId, times, theme.getContentSortType(), status);
            } else {//返回主题下所有已审核
                return textContentDao.queryByThemeId(null, themeId, theme.getContentSortType(), null, status);
            }
        }
        return null;
    }

    /**
     * 添加修改分析主题
     *
     * @param datas
     * @return
     */
    public Map<String, Object> updateTextContent(List<TextContent> datas, User user, Integer themeId) {
        Map<String, Object> map = new HashMap<>();
        boolean nameRepeat = false;
        Integer dataId = null;
        List<TextContent> result = new ArrayList<>();
        for (TextContent data : datas) {
            dataId = data.getId();
            String labelIds = data.getLabelIds();
            if (themeId != null) {
                if (dataId == null) {
                    List<TextContent> textContentList = textContentDao.queryByName(data.getName(), themeId);
                    if (textContentList.size() == 0) {
                        TextTheme textTheme = new TextTheme();
                        textTheme.setId(themeId);
                        data.setTheme(textTheme);
                        data.setCreator(user.getId());
                        data.setCreatorName(user.getUserName());
                        data.setCreateTime(new Date());
                        data.setStatus(Constant.TEXT_CONTENT_STATUS.WAIT_CHECK);
                        TextTheme textTheme1 = textThemeDao.queryById(themeId);
                        if (textTheme1.getModelId() != null) {
                            TextModel textModel = textModelDao.queryById(textTheme1.getModelId());
                            data.setContent(textModel.getContent());
                        }
                        textContentDao.insert(data, true);
                        result.add(data);
                        textLabelService.linkLabel(data.getId(), labelIds);
                    } else {
                        nameRepeat = true;
                    }
                } else {
                    List<TextContent> textContentList = null;
                    if (data.getName() != null) {
                        textContentList = textContentDao.queryByNameAndId(data.getName(), themeId, dataId);
                    }
                    if (textContentList == null || textContentList.size() == 0) {
                        ConvertUtil<TextContent> cu = new ConvertUtil<>();
                        TextContent textContent = textContentDao.queryById(dataId);
                        cu.replication(data, textContent, TextContent.class.getName());
                        textContent.setUpdator(user.getId());
                        textContent.setUpdatorName(user.getUserName());
                        textContent.setUpdateTime(new Date());
                        textContentDao.update(textContent, true);
                        result.add(textContent);
                        textLabelService.linkLabel(dataId, labelIds);
                    } else {
                        nameRepeat = true;
                    }
                }
            }
        }
        map.put("datas", result);
        map.put("nameRepeat", nameRepeat);
        return map;
    }

    /**
     * 添加分析主题(前台添加)
     *
     * @param user
     * @param themeId
     * @param name
     * @param content
     * @param analysisDate
     * @return
     */

    public Map<String, Object> addTextContent(User user, Integer id, Integer themeId, String name, String content, String analysisDate) {
        TextContent result = new TextContent();
        List<TextContent> datas = new ArrayList<>();
        if (id == null) {
            if (themeId != null) {
                List<TextContent> textContentList = queryAllTextContentByThemeId(null, themeId, null, null, null);
                TextTheme textTheme = new TextTheme();
                textTheme.setId(themeId);
                result.setTheme(textTheme);
                result.setCreator(user.getId());
                result.setCreatorName(user.getUserName());
                result.setCreateTime(new Date());
                result.setStatus(Constant.TEXT_CONTENT_STATUS.WAIT_CHECK);
                result.setSortIndex(textContentList.size());
                result.setType(Constant.TEXT_CONTENT_TYPE.COMMON);
                result.setContent(content);
                result.setName(name);
                result.setAnalysisDate(new Date(analysisDate));
                datas.add(result);
                //textContentDao.insert(result, true);
            }
        } else {
            TextContent data = new TextContent();
            data.setName(name);
            data.setContent(content);
            data.setAnalysisDate(new Date(analysisDate));
            ConvertUtil<TextContent> cu = new ConvertUtil<>();
            TextContent textContent = textContentDao.queryById(id);
            cu.replication(data, textContent, TextContent.class.getName());
            textContent.setUpdator(user.getId());
            textContent.setUpdatorName(user.getUserName());
            textContent.setUpdateTime(new Date());
            //textContentDao.update(textContent, true);
            datas.add(textContent);
        }
        Map<String, Object> map = updateTextContent(datas, user, themeId);
        return map;
    }

    /**
     * 删除分析内容
     *
     * @param datas
     */
    public void deleteTextContent(List<TextContent> datas) {
        for (TextContent textContent : datas) {
            Integer id = textContent.getId();
            textContentDao.deleteById(id);
            // 删除关联标签
            textLabelLinkDao.clearLabelLink(id);
        }
    }

    /**
     * 根据id查询分析内容
     *
     * @param id
     */
    public TextContent queryById(Integer id) {
        return textContentDao.queryById(id);
    }

    /**
     * 根据id查询分析内容
     *
     * @param id
     */
    public String getTextContentById(Integer id) {
        TextContent textContent = textContentDao.queryById(id);
        return textContent != null ? textContent.getContent() : "";
    }

    /**
     * 审核内容
     *
     * @param ids    要审核的内容
     * @param status 驳回还是通过审核
     */
    public void checkTextContent(String ids, Integer status) {
        textContentDao.checkTextContent(ids, status);
    }


    /**
     * 搜索文字分析，返回匹配数量
     *
     * @param text
     * @return
     * @author hzc
     * @createDate 2016-5-13
     */
    public int getTextForSearchCount(String text) {
        return textContentDao.selectForSearchCount(text);
    }

    /**
     * 搜索文字分析，返回匹配的文字分析
     * <pre>
     *     模糊查询，匹配名称，分页
     * </pre>
     *
     * @param text
     * @param page
     * @return
     * @author hzc
     * @createDate 2016-5-13
     */
    public List getTextForSearch(String text,Page page) {
        return textContentDao.selectForSearch(text,page);
    }
}
