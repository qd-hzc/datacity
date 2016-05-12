package com.city.resourcecategory.analysis.text.controller;

import com.city.common.controller.BaseController;
import com.city.common.util.EsiJsonParamUtil;
import com.city.resourcecategory.analysis.text.entity.TextLabel;
import com.city.resourcecategory.analysis.text.entity.TextModel;
import com.city.resourcecategory.analysis.text.service.TextLabelService;
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
 * Created by wxl on 2016/3/17.
 */
@Controller
@RequestMapping("/support/resourceCategory/analysis/text")
public class TextLabelController extends BaseController {
    @Autowired
    private TextLabelService textLabelService;

    /**
     * 查询所有标签
     *
     * @param name 名称
     * @param tier 标签分级
     */
    @RequestMapping("/queryTextLabels")
    @ResponseBody
    public List<TextLabel> queryTextLabels(String name, Integer tier) {
        return textLabelService.queryTextLabels(name, tier);
    }
    /**
     * 查询所有标签
     */
    @RequestMapping("/queryAllTextLabels")
    @ResponseBody
    public String queryTextLabels() {
        Gson gson = new Gson();
        List<TextLabel> textLabelList = textLabelService.queryTextLabels(null, null);
        List<Map<String, Object>> textLabels = new ArrayList<>();
        for (TextLabel textLabel : textLabelList) {
            Map<String, Object> map = new HashMap<>();
            map.put("text", textLabel.getName());
            map.put("value", textLabel.getId());
            textLabels.add(map);
        }
        return gson.toJson(textLabels);
    }
    /**
     * 保存标签
     */
    @RequestMapping("/saveTextLabel")
    @ResponseBody
    public Map<String, Object> saveTextLabel(TextLabel textLabel) {
        Map<String, Object> result = null;
        try {
            textLabelService.saveTextModel(textLabel);
            result = genSuccessMsg("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            result = genFaultMsg("保存失败,该名称已存在!");
        }
        return result;
    }

    /**
     * 删除标签
     */
    @RequestMapping("/removeTextLabels")
    @ResponseBody
    public Map<String, Object> removeTextLabels(HttpServletRequest request){
        EsiJsonParamUtil<TextLabel> util=new EsiJsonParamUtil<>();
        Map<String, Object> result = null;
        try {
            List<TextLabel> textLabels = util.parseObjToList(request, TextLabel.class);
            textLabelService.removeTextLabels(textLabels);
            result=genSuccessMsg("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            result=genFaultMsg("删除失败,服务器操作异常");
        }
        return result;
    }
}
