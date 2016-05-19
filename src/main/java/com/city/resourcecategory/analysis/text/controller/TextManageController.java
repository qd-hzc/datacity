package com.city.resourcecategory.analysis.text.controller;

import com.city.common.controller.BaseController;
import com.city.common.pojo.Constant;
import com.city.common.util.EsiJsonParamUtil;
import com.city.common.util.EsiLogUtil;
import com.city.common.util.SessionUtil;
import com.city.resourcecategory.analysis.text.entity.*;
import com.city.resourcecategory.analysis.text.service.*;
import com.city.support.dataSet.query.pojo.TimePojo;
import com.city.support.sys.user.entity.User;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wxl on 2016/3/15.
 */
@Controller
@RequestMapping("/support/resourceCategory/analysis/text")
public class TextManageController extends BaseController {

    @Autowired
    private TextThemeService textThemeService;

    @Autowired
    private TextContentService textContentService;
    @Autowired
    private TextModelService textModelService;
    @Autowired
    private TextLabelService textLabelService;
    @Autowired
    private TextDataService textDataService;

    /**
     * 进入文字分析界面
     */
    @RequestMapping("/textManageJsp")
    public ModelAndView textManageJsp() {
        ModelAndView mv = new ModelAndView("resourceCategory/analysis/text/textManage");
        Gson gson = new Gson();
        List<TextModel> textModelList = textModelService.queryTextModel(null, 1);
        List<Map<String, Object>> textModels = new ArrayList<>();
        for (TextModel textModel : textModelList) {
            Map<String, Object> map = new HashMap<>();
            map.put("text", textModel.getName());
            map.put("value", textModel.getId());
            textModels.add(map);
        }
        mv.addObject("textModel", gson.toJson(textModels));
        List<TextLabel> textLabelList = textLabelService.queryTextLabels(null, null);
        List<Map<String, Object>> textLabels = new ArrayList<>();
        for (TextLabel textLabel : textLabelList) {
            Map<String, Object> map = new HashMap<>();
            map.put("text", textLabel.getName());
            map.put("value", textLabel.getId());
            textLabels.add(map);
        }
        mv.addObject("textLabel", gson.toJson(textLabels));

        return mv;
    }
    /**
     * 进入文字分析发布界面
     */
    @RequestMapping("/textSubmit")
    public ModelAndView textSubmitJsp(Integer themeId) {
        ModelAndView mv = new ModelAndView("resourceCategory/analysis/text/textSubmit");
        mv.addObject("themeId", themeId);
        return mv;
    }
    /**
     * 进入文字分析内容界面
     */
    @RequestMapping("/textContent")
    public ModelAndView textContentJsp(Integer contentId) {
        ModelAndView mv = new ModelAndView("resourceCategory/analysis/text/textContent");
        mv.addObject("contentId", contentId);
        return mv;
    }
    /**
     * 查询所有分析主题
     */
    @RequestMapping("/queryTextTheme")
    @ResponseBody
    public Map<String, Object> queryTextTheme() {
        Map<String, Object> result = null;
        List<TextTheme> datas = null;
        try {
            datas = textThemeService.queryAllTextTheme();
            result = genSuccessMsg(datas, "查询成功", null);
        } catch (Exception e) {
            e.printStackTrace();
            EsiLogUtil.error(getLog(), e.getMessage());
            result = genFaultMsg(null, "查询失败", null);
        }
        return result;
    }
    /**
     * 查询所有分析主题
     */
    @RequestMapping("/queryTextThemeList")
    @ResponseBody
    public List<TextTheme> queryTextThemeList() {
        return textThemeService.queryAllTextTheme();
    }
    /**
     * 根据条件查询所有分析主题
     */
    @RequestMapping("/queryTextThemeByCondition")
    @ResponseBody
    public Map<String, Object> queryTextThemeByCondition(String name) {
        Map<String, Object> result = null;
        List<TextTheme> datas = null;
        try {
            datas = textThemeService.queryTextThemeByCondition(name);
            result = genSuccessMsg(datas, "查询成功", null);
        } catch (Exception e) {
            e.printStackTrace();
            EsiLogUtil.error(getLog(), e.getMessage());
            result = genFaultMsg(null, "查询失败", null);
        }
        return result;
    }

    /**
     * 添加、更新分析主题
     * @param request
     * @return
     */
    @RequestMapping("/updateTextTheme")
    @ResponseBody
    public Map<String, Object> updateTextTheme(HttpServletRequest request) {
        Map<String, Object> result = null;
        List<TextTheme> datas = null;
        try {
            //获取当前用户
            User user = SessionUtil.getUser(request.getSession());
            EsiJsonParamUtil<TextTheme> paramUtil = new EsiJsonParamUtil<>();
            datas = paramUtil.parseObjToList(request, TextTheme.class);
            Map map = textThemeService.updateTextTheme(datas, user);
            datas = (List<TextTheme>)map.get("datas");
            if(datas.size()>0) {
                result = genSuccessMsg(datas, "保存成功", null);
            }else{
                if((boolean)map.get("nameRepeat")){
                    result = genFaultMsg(null, "分析主题名称不能重复！", null);
                }else {
                    result = genFaultMsg(null, "保存失败", null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            EsiLogUtil.error(getLog(), e.getMessage());
            result = genFaultMsg(null, "保存失败", null);
        }
        return result;
    }

    @RequestMapping("/deleteTextTheme")
    @ResponseBody
    public Map<String, Object> deleteTextTheme(HttpServletRequest request) {
        Map<String, Object> result = null;
        List<TextTheme> datas = null;
        try {
            EsiJsonParamUtil<TextTheme> paramUtil = new EsiJsonParamUtil<>();
            datas = paramUtil.parseObjToList(request, TextTheme.class);
            textThemeService.deleteTextTheme(datas);
            result = genSuccessMsg(null, "删除成功", null);
        } catch (Exception e) {
            e.printStackTrace();
            EsiLogUtil.error(getLog(), e.getMessage());
            result = genFaultMsg(null, "删除失败", null);
        }
        return result;
    }

    /**
     * 查询所有分析内容
     */
    @RequestMapping("/queryTextContentByThemeId")
    @ResponseBody
    public Map<String, Object> queryTextContentByThemeId(HttpServletRequest request) {
        Map<String, Object> result = null;
        List<TextContent> datas = null;
        EsiJsonParamUtil<TextContent> paramUtil = new EsiJsonParamUtil<>();
        String themeIdStr = null;
        String contentSortType = null;
        Integer themeId = null;
        String name = null;
        String statusStr = null;
        Integer status = null;
        try {
            //获取当前用户
            themeIdStr = paramUtil.getParam(request, "themeId");
            contentSortType = paramUtil.getParam(request, "contentSortType");
            themeId = Integer.parseInt(themeIdStr);
            name = paramUtil.getParam(request, "name");
            statusStr = paramUtil.getParam(request, "status");
            if (statusStr != null && !"".equals(statusStr)) {
                status = Integer.parseInt(statusStr);
            }
            datas = textContentService.queryAllTextContentByThemeId(null,themeId, contentSortType, name, status);
            result = genSuccessMsg(datas, "查询成功", null);
        } catch (Exception e) {
            e.printStackTrace();
            EsiLogUtil.error(getLog(), e.getMessage());
            result = genFaultMsg(null, "查询失败", null);
        }
        return result;
    }

    @RequestMapping("/updateTextContent")
    @ResponseBody
    public Map<String, Object> updateTextContent(HttpServletRequest request, Integer themeId) {
        Map<String, Object> result = null;
        List<TextContent> datas = null;
        EsiJsonParamUtil<TextContent> paramUtil = new EsiJsonParamUtil<>();
        try {
            //获取当前用户
            User user = SessionUtil.getUser(request.getSession());
            datas = paramUtil.parseObjToList(request, TextContent.class);
            Map map = textContentService.updateTextContent(datas, user, themeId);
            datas = (List<TextContent>)map.get("datas");
            if(datas.size()>0) {
                result = genSuccessMsg(datas, "保存成功", null);
            }else{
                if((boolean)map.get("nameRepeat")){
                    result = genFaultMsg(null, "同一分析主题的文字分析名称不能重复！", null);
                }else {
                    result = genFaultMsg(null, "保存失败", null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            EsiLogUtil.error(getLog(), e.getMessage());
            result = genFaultMsg(null, "保存失败", null);
        }
        return result;
    }

    /**
     * 添加分析数据
     * @param request
     * @param themeId
     * @return
     */
    @RequestMapping("/addTextContent")
    @ResponseBody
    public Map<String, Object> addTextContent(HttpServletRequest request,Integer id, Integer themeId,String name,String content,String analysisDate) {
        Map<String, Object> result = null;
        TextContent data = new TextContent();
        List<TextContent> datas = null;
        try {
            //获取当前用户
            User user = SessionUtil.getUser(request.getSession());
            Map map = textContentService.addTextContent(user,id, themeId,name,content,analysisDate);
            datas = (List<TextContent>)map.get("datas");
            if(datas.size()>0) {
                result = genSuccessMsg(datas, "保存成功", null);
            }else{
                if((boolean)map.get("nameRepeat")){
                    result = genFaultMsg(null, "文字分析名称已存在！", null);
                }else {
                    result = genFaultMsg(null, "保存失败", null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            EsiLogUtil.error(getLog(), e.getMessage());
            result = genFaultMsg(null, "保存失败!", null);
        }
        return result;
    }
    /**
     * 审核内容
     *
     * @param ids    要审核的内容
     * @param status 驳回还是通过审核
     */
    @RequestMapping("/checkTextContent")
    @ResponseBody
    public Map<String, Object> checkTextContent(String ids, Integer status) {
        Map<String, Object> result = null;
        try {
            textContentService.checkTextContent(ids, status);
            result = genSuccessMsg("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            result = genFaultMsg("操作失败,服务端运行异常!");
        }
        return result;
    }

    @RequestMapping("/deleteTextContent")
    @ResponseBody
    public Map<String, Object> deleteTextContent(HttpServletRequest request, Integer themeId) {
        Map<String, Object> result = null;
        List<TextContent> datas = null;
        try {
            EsiJsonParamUtil<TextContent> paramUtil = new EsiJsonParamUtil<>();
            datas = paramUtil.parseObjToList(request, TextContent.class);
            if (datas.size() > 0) {
                textContentService.deleteTextContent(datas);
                result = genSuccessMsg(null, "删除成功", null);
            } else {
                result = genFaultMsg(null, "删除失败", null);
            }

        } catch (Exception e) {
            e.printStackTrace();
            EsiLogUtil.error(getLog(), e.getMessage());
            result = genFaultMsg(null, "删除失败", null);
        }
        return result;
    }

    /**
     * 查询所有分析数据
     */
    @RequestMapping("/queryTextData")
    @ResponseBody
    public Map<String, Object> queryDataByThemeIdOrContentId(HttpServletRequest request, Integer foreignId, Integer foreignType) {
        Map<String, Object> result = null;
        List<TextData> datas = null;
        try {
            if (foreignId != null && foreignType != null) {
                if (foreignType == Constant.TEXT_TYPE.CONTENT) {
                    TextContent textContent = textContentService.queryById(foreignId);
                    datas = textDataService.queryByForeignIdAndType(textContent.getTheme().getId(), Constant.TEXT_TYPE.THEME);
                    List<TextData> contentDatas = textDataService.queryByForeignIdAndType(foreignId, foreignType);
                    for (TextData textData : contentDatas) {
                        datas.add(textData);
                    }
                } else {
                    datas = textDataService.queryByForeignIdAndType(foreignId, foreignType);
                }
                result = genSuccessMsg(datas, "查询成功", null);
            } else {
                result = genFaultMsg(null, "查询失败", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            EsiLogUtil.error(getLog(), e.getMessage());
            result = genFaultMsg(null, "查询失败", null);
        }
        return result;
    }

    @RequestMapping("/addAnalysisData")
    @ResponseBody
    public Map<String, Object> addAnalysisData(HttpServletRequest request) {
        Map<String, Object> result = null;
        List<TextData> datas = null;
        EsiJsonParamUtil<TextData> paramUtil = new EsiJsonParamUtil<>();
        try {
            //获取当前用户
            User user = SessionUtil.getUser(request.getSession());
            datas = paramUtil.parseObjToList(request, TextData.class);
            Boolean isTextDataExist = textDataService.hasTextData(datas);
            datas = textDataService.addTextData(datas, user);
            if (datas.size() > 0) {
                result = genSuccessMsg(datas, "添加成功", null);
            } else {
                if (isTextDataExist) {
                    result = genFaultMsg(null, "此分析数据已添加", null);
                } else {
                    result = genFaultMsg(null, "添加失败", null);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            EsiLogUtil.error(getLog(), e.getMessage());
            result = genFaultMsg(null, "添加失败", null);
        }
        return result;
    }

    @RequestMapping("/deleteAnalysisData")
    @ResponseBody
    public Map<String, Object> deleteAnalysisData(HttpServletRequest request) {
        Map<String, Object> result = null;
        List<TextData> datas = null;
        try {
            EsiJsonParamUtil<TextData> paramUtil = new EsiJsonParamUtil<>();
            datas = paramUtil.parseObjToList(request, TextData.class);
            if (datas.size() > 0) {
                textDataService.deleteTextData(datas);
                result = genSuccessMsg(null, "删除成功", null);
            } else {
                result = genFaultMsg(null, "删除失败", null);
            }

        } catch (Exception e) {
            e.printStackTrace();
            EsiLogUtil.error(getLog(), e.getMessage());
            result = genFaultMsg(null, "删除失败", null);
        }
        return result;
    }

    /**
     * 根据数据查询分析主题
     *
     * @param dataName   数据名
     * @param dataValues 数据
     */
    @RequestMapping("/queryTextThemesByData")
    @ResponseBody
    public List queryTextThemesByData(String dataName, String dataValues) {
        return textDataService.queryForeignObjs(1, dataName, dataValues);
    }

    /**
     * 根据数据查询分析内容
     *
     * @param dataName   数据名
     * @param dataValues 数据
     */
    @RequestMapping("/queryTextContentsByData")
    @ResponseBody
    public List queryTextContentsByData(String dataName, String dataValues) {
        return textDataService.queryForeignObjs(2, dataName, dataValues);
    }

    /**
     * 根据id查询分析内容
     */
    @RequestMapping("/queryContentById")
    @ResponseBody
    public TextContent queryContentById(Integer contentId) {
        return textContentService.queryById(contentId);
    }

    /**
     * 根据分析主题和时间获取
     *
     * @param themeId 主题id
     * @param time    时间
     */
    @RequestMapping("/queryContentByTime")
    @ResponseBody
    public List<TextContent> queryByTime(Integer themeId, TimePojo time) {
        List<TimePojo> times = new ArrayList<>();
        times.add(time);
        return textContentService.queryByTime(themeId, times, Constant.TEXT_CONTENT_STATUS.CHECKED);
    }
}
