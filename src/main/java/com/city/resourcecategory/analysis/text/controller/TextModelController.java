package com.city.resourcecategory.analysis.text.controller;

import com.city.common.controller.BaseController;
import com.city.common.util.EsiJsonParamUtil;
import com.city.resourcecategory.analysis.text.entity.TextModel;
import com.city.resourcecategory.analysis.text.service.TextModelService;
import com.city.support.sys.user.pojo.CurrentUser;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
public class TextModelController extends BaseController {
    @Autowired
    private TextModelService textModelService;

    /**
     * 查询模板
     *
     * @param name
     */
    @RequestMapping("/queryTextModel")
    @ResponseBody
    public List<TextModel> queryTextModel(String name, Integer status) {
        return textModelService.queryTextModel(name, status);
    }
    /**
     * 查询所有启用模板
     *
     */
    @RequestMapping("/queryAllTextModels")
    @ResponseBody
    public String queryAllTextModel() {
        Gson gson = new Gson();
        List<TextModel> textModelList = textModelService.queryTextModel(null, 1);
        List<Map<String, Object>> textModels = new ArrayList<>();
        for (TextModel textModel : textModelList) {
            Map<String, Object> map = new HashMap<>();
            map.put("text", textModel.getName());
            map.put("value", textModel.getId());
            textModels.add(map);
        }
        return gson.toJson(textModels);
    }
    /**
     * 保存模板
     */
    @RequestMapping("/saveTextModel")
    @ResponseBody
    public Map<String, Object> saveTextModel(TextModel textModel,HttpServletRequest request) {
        Map<String, Object> result = null;
        try {
            Integer userId = CurrentUser.getCurrentUser(request).getUser().getId();
            textModelService.saveTextModel(textModel,userId);
            result = genSuccessMsg("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            result = genFaultMsg("保存失败,服务器端响应失败");
        }
        return result;
    }

    /**
     * 删除模板
     */
    @RequestMapping("/removeTextModels")
    @ResponseBody
    public Map<String, Object> removeTextModels(HttpServletRequest request){
        EsiJsonParamUtil<TextModel> util=new EsiJsonParamUtil<>();
        Map<String, Object> result = null;
        try {
            List<TextModel> textModels = util.parseObjToList(request, TextModel.class);
            textModelService.removeTextModels(textModels);
            result=genSuccessMsg("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            result=genFaultMsg("删除失败,服务器操作异常");
        }
        return result;
    }


}
