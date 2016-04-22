package com.city.support.manage.area.controller;

import com.city.common.controller.BaseController;
import com.city.common.util.EsiJsonParamUtil;
import com.city.common.util.EsiLogUtil;
import com.city.common.util.SessionUtil;
import com.city.support.manage.area.entity.SptMgrAreaEntity;
import com.city.support.manage.area.service.AreaService;
import com.city.support.manage.pojo.DragAndDropVO;
import com.city.support.sys.user.entity.User;
import com.google.gson.Gson;
import org.apache.commons.beanutils.BeanUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import sun.misc.Regexp;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 地区
 * 提供地区相关控制方法
 * Created by HZC on 2015/12/30.
 */
@Controller
@RequestMapping("/area")
public class AreaController extends BaseController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AreaService areaService;

    /**
     * 返回地区管理主页面
     *
     * @return
     * @author hzc
     * @createDate 2015-12-31
     */
    @RequestMapping("/index")
    public String index() {
        return "support/manage/area/index";
    }

    /**
     * 保存或者更新地区
     *
     * @return
     * @author hzc
     * @createDate 2015-12-30
     */
    @RequestMapping("/souArea")
    @ResponseBody
    public Object souArea(SptMgrAreaEntity entity, HttpServletRequest request) {
        Date date = new Date();
        User user = SessionUtil.getUser(request.getSession());
        if (entity.getId() < 1) {
            entity.setCreateDate(date);
            entity.setCreatorId(user.getId());
        } else {
            entity.setUpdateDate(date);
            entity.setUpdaterId(user.getId());
        }
        String parentIds = entity.getParentIds();
        if (!StringUtils.isEmpty(parentIds)) {
            entity.setParentId(Integer.parseInt(parentIds));
        } else {
            entity.setParentId(0);
        }
        String originalFilename = entity.getFile().getOriginalFilename();
        if (!StringUtils.isEmpty(originalFilename)) {
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            if (!suffix.equals(".json") && !suffix.equals(".svg")) {
                return genFaultMsg("地图格式不正确", "调用失败", null);
            }
            String realPath = request.getSession().getServletContext().getRealPath("/upload/map");
            entity.setJsonSvg(realPath);
        }
        boolean result = areaService.saveOrUpdate(entity);
        if (result) {
            return genSuccessMsg("保存成功", "保存成功", null);
        } else {
            return genFaultMsg("保存或者更新地区失败", "调用失败", null);
        }
    }

    /**
     * 返回下级地区
     * <pre>
     *     根据地区id，查询下级所有地区
     * </pre>
     *
     * @return
     */
    @RequestMapping("/getAreaByParent")
    @ResponseBody
    public Object getAreaByParent(HttpServletRequest request) {
        String areaId = request.getParameter("node");
        if (StringUtils.isEmpty(areaId)) {
            areaId = "0";
        }
        return areaService.getAreaByParent(Integer.parseInt(areaId));
    }

    /**
     * 新建地区
     *
     * @return
     * @author hzc
     * @createDate 2016-01-04
     */
    @RequestMapping("/saveArea")
    @ResponseBody
    public Object saveArea(SptMgrAreaEntity entity, HttpServletRequest request) {
        Date date = new Date();
        User user = SessionUtil.getUser(request.getSession());
        entity.setCreateDate(date);
        entity.setCreatorId(user.getId());
        entity.setLeaf(true);
        String parentIds = entity.getParentIds();
        if (!StringUtils.isEmpty(parentIds)) {
            entity.setParentId(Integer.parseInt(parentIds));
        } else {
            entity.setParentId(0);
        }
        String contextPath = request.getContextPath();
        entity.setJsonSvg(contextPath);
        boolean result = areaService.saveArea(entity);
        if (result) {
            Gson gson = new Gson();
            Map map = null;
            try {
                map = BeanUtils.describe(entity);
            } catch (Exception e) {
                return genFaultMsg("保存或者更新地区失败", "调用失败", null);
            }
            map.put("id", entity.getId());
            map.put("text", entity.getName());
            map.put("leaf", true);
            String jsonStr = gson.toJson(map);
            return genSuccessMsg(jsonStr, "保存成功", null);
        } else {
            return genFaultMsg("保存或者更新地区失败", "调用失败", null);
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

        boolean result = areaService.saveDragAndDrop(dragAndDrop);
        return result;
    }

    /**
     * 读取地图文件
     *
     * @param areaId
     * @return
     * @throws IOException
     * @author hzc
     * @createDate 2016-1-8
     */
    @RequestMapping("/getMapJson")
    @ResponseBody
    public Object getMapJson(Integer areaId) throws IOException {
        SptMgrAreaEntity entity = areaService.getAreaById(areaId);
        String jsonSvg = entity.getJsonSvg();
        FileInputStream fis = null;
        String s = new String();
        try {
            //创建流对象
            fis = new FileInputStream(jsonSvg);
            //读取数据，并将读取到的数据存储到数组中
            byte[] data = new byte[1024]; //数据存储的数组
            int len;
            while ((len = fis.read(data)) != -1) {
                //解析数据
                s += new String(data, 0, len, "utf-8");
            }
            //输出字符串
            System.out.println(s);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭流，释放资源
            fis.close();
        }
        return s;
    }

    /**
     * 返回地区树（状态为启用）
     *
     * @param areaName
     * @param checkTreeFlag
     * @return
     */
    @RequestMapping("/queryAreaByName")
    @ResponseBody
    public Map<String, Object> queryAreaByName(String areaName, Integer checkTreeFlag) {
        Map<String, Object> result = null;
        Map<String, Object> areaTree = null;
        List areaList = null;
        boolean isCheckTree = false;
        if (checkTreeFlag != null && checkTreeFlag.equals(1)) {
            isCheckTree = true;
        }
        try {
            if (areaName == null || "".equals(areaName)) {
                areaTree /*areaList*/ = areaService.queryAreaByName("", isCheckTree);
            } else {
                areaTree /*areaList*/ = areaService.queryAreaByName(areaName, isCheckTree);
            }
            result = genSuccessMsg(areaTree, "查询成功", null);
        } catch (Exception e) {
            e.printStackTrace();
            EsiLogUtil.error(getLog(), e.getMessage());
            result = genFaultMsg(null, "查询失败", null);
        }
        return areaTree;
    }

    /**
     * 删除地区
     *
     * @param request
     * @return
     * @author hzc
     * @createDate2016-2-16
     */
    @RequestMapping("/deleteArea")
    @ResponseBody
    public Object deleteArea(HttpServletRequest request) {
        String id = request.getParameter("id");
        if (StringUtils.isEmpty(id)) {
            return genFaultMsg("请求失败", "操作失败，请稍候再试", null);
        }
        try {
            areaService.deleteArea(Integer.parseInt(id));
        } catch (Exception e) {
            String sqlState = ((ConstraintViolationException) e).getSQLState();
            if (sqlState.equals("23000")) {
                return genFaultMsg("请求失败", "地区已经被引用，请先删除引用，再删除该地区", null);
            }
        }
        return genSuccessMsg("请求成功", "操作成功", null);
    }

    /**
     * 保存地区排序顺序
     *
     * @param request
     * @return
     */
    @RequestMapping("/saveAreaSorts")
    @ResponseBody
    public Map<String, Object> saveAreaSorts(HttpServletRequest request) {
        Map<String, Object> result = null;
        EsiJsonParamUtil<SptMgrAreaEntity> util = new EsiJsonParamUtil<>();
        try {
            List<SptMgrAreaEntity> groups = util.parseObjToList(request, SptMgrAreaEntity.class);
            areaService.saveGroupSorts(groups);
            result = genSuccessMsg("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            result = genFaultMsg("保存失败,服务端运行异常!");
        }
        return result;
    }
}