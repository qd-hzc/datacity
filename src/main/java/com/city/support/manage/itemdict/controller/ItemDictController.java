package com.city.support.manage.itemdict.controller;

import com.city.common.controller.BaseController;
import com.city.common.util.EsiJsonParamUtil;
import com.city.common.util.SessionUtil;
import com.city.support.manage.itemdict.entity.SptMgrItemDictEntity;
import com.city.support.manage.itemdict.service.ItemDictService;
import com.city.support.manage.pojo.DragAndDropVO;
import com.city.support.sys.user.entity.User;
import com.google.gson.Gson;
import org.apache.commons.beanutils.BeanUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by HZC on 2016/1/5.
 */
@Controller
@RequestMapping("/itemDict")
public class ItemDictController extends BaseController {

    @Autowired
    private ItemDictService itemDictService;

    /**
     * 返回指标分组目录管理主页面
     *
     * @return
     * @author hzc
     * @createDate 2015-12-31
     */
    @RequestMapping("/index")
    public String index() {
        return "support/manage/item-dict/index";
    }

    /**
     * 保存或者更新指标分组目录
     *
     * @return
     * @author hzc
     * @createDate 2015-12-30
     */
    @RequestMapping("/souItemDict")
    @ResponseBody
    public Object souItemDict(SptMgrItemDictEntity entity, HttpServletRequest request) {
        Date date = new Date();
        User user = SessionUtil.getUser(request.getSession());
        if (entity.getId() < 1) {
            entity.setCreateDate(date);
            entity.setCreatorId(user.getId());
        } else {
            entity.setUpdateDate(date);
            entity.setUpdaterId(user.getId());
        }
        boolean result = itemDictService.saveOrUpdate(entity);
        if (result) {
            return genSuccessMsg("保存成功", "添加成功", null);
        } else {
            return genFaultMsg("保存或者更新指标分组目录失败", "目录名称重复", null);
        }
    }

    /**
     * 返回下级指标分组目录
     * <pre>
     *     根据指标分组目录id，查询下级所有指标分组目录
     * </pre>
     *
     * @return
     */
    @RequestMapping("/getItemDictByParent")
    @ResponseBody
    public Object getItemDictByParent(HttpServletRequest request) {
        String ItemDictId = request.getParameter("node");
        if (StringUtils.isEmpty(ItemDictId)) {
            ItemDictId = "0";
        }
        return itemDictService.getItemDictByParent(Integer.parseInt(ItemDictId));
    }

    /**
     * 新建指标分组目录
     *
     * @return
     * @author hzc
     * @createDate 2016-01-04
     */
    @RequestMapping("/saveItemDict")
    @ResponseBody
    public Object saveItemDict(SptMgrItemDictEntity entity, HttpServletRequest request) {
        Date date = new Date();
        User user = SessionUtil.getUser(request.getSession());
        entity.setCreateDate(date);
        entity.setCreatorId(user.getId());
        entity.setLeaf(true);
        boolean result = itemDictService.saveItemDict(entity);
        if (result) {
            Gson gson = new Gson();
            Map map = null;
            try {
                map = BeanUtils.describe(entity);
            } catch (Exception e) {
                return genFaultMsg("保存或者更新指标分组目录失败", "调用失败", null);
            }
            map.put("id", entity.getId());
            map.put("text", entity.getName());
            map.put("leaf", true);
            String jsonStr = gson.toJson(map);
            return genSuccessMsg(jsonStr, "保存成功", null);
        } else {
            return genFaultMsg("保存或者更新指标分组目录失败", "目录名称重复", null);
        }
    }

    /**
     * 拖拽排序
     *
     * @param dragAndDrop
     * @return
     * @author hzc
     * @createDate 2016-1-6
     */
    @RequestMapping("/dragAndDrop")
    @ResponseBody
    public Object dragAndDrop(DragAndDropVO dragAndDrop) {

        String moveParentId = dragAndDrop.getMoveParentId();
        dragAndDrop.setMoveParentId(moveParentId);

        String overParentId = dragAndDrop.getOverParentId();
        dragAndDrop.setOverParentId(overParentId);

        boolean result = itemDictService.saveDragAndDrop(dragAndDrop);
        return result;
    }

    /**
     * 删除指标分组目录
     * <pre>
     *     包括子
     * </pre>
     *
     * @return
     * @author hzc
     * @createDate 2016-2-15
     */
    @RequestMapping("/deleteItemDict")
    @ResponseBody
    public Object deleteItemDict(HttpServletRequest request) {
        String itemDictIds = request.getParameter("id");
        if (StringUtils.isEmpty(itemDictIds)) {
            return genFaultMsg("请求失败", "操作失败，请稍候再试", null);
        }
        try {
            itemDictService.deleteItemDicts(Integer.parseInt(itemDictIds));
        } catch (Exception e) {
            String sqlState = ((ConstraintViolationException) e).getSQLState();
            if (sqlState.equals("23000")) {
                return genFaultMsg("请求失败", "地区已经被引用，请先删除引用，再删除该地区", null);
            }
        }
        return genSuccessMsg("请求成功", "操作成功", null);
    }

    /**
     * 保存指标分组目录顺序
     *
     * @param request
     * @return
     */
    @RequestMapping("/saveItemDictSorts")
    @ResponseBody
    public Map<String, Object> saveItemDictSorts(HttpServletRequest request) {
        Map<String, Object> result = null;
        EsiJsonParamUtil<SptMgrItemDictEntity> util = new EsiJsonParamUtil<>();
        try {
            List<SptMgrItemDictEntity> list = util.parseObjToList(request, SptMgrItemDictEntity.class);
            itemDictService.saveGroupSorts(list);
            result = genSuccessMsg("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            result = genFaultMsg("保存失败,服务端运行异常!");
        }
        return result;
    }
}
