package com.city.app.dataDict.service;

import com.city.app.dataDict.dao.AppDataDictDao;
import com.city.app.dataDict.dao.AppDataDictMenuDao;
import com.city.app.dataDict.entity.AppDataDict;
import com.city.app.dataDict.entity.AppDataDictMenu;
import com.city.app.dataDict.icon.dao.AppDataDictMenuIconDao;
import com.city.app.dataDict.icon.entity.AppDataDictMenuIcon;
import com.city.common.pojo.AppConstant;
import com.city.common.pojo.Constant;
import com.city.common.util.ConvertUtil;
import com.city.common.util.ListUtil;
import com.city.common.util.StringUtil;
import com.city.common.util.tree.PackageLocalListToTree;
import com.city.support.dataSet.dao.DataSetDataDao;
import com.city.support.dataSet.entity.DataSetData;
import com.city.support.dataSet.query.pojo.RptDataPojo;
import com.city.support.dataSet.query.pojo.TimePojo;
import com.city.support.dataSet.query.service.QueryRptService;
import com.city.support.manage.timeFrame.dao.TimeFrameDao;
import com.city.support.manage.timeFrame.entity.TimeFrame;
import com.city.support.regime.collection.entity.ReportData;
import com.city.support.sys.user.dao.RoleDao;
import com.city.support.sys.user.entity.Role;
import com.city.support.sys.user.entity.User;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by wxl on 2016/3/21.
 */
@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
public class AppDataDictMenuService {
    @Autowired
    private AppDataDictMenuDao appDataDictMenuDao;
    @Autowired
    private AppDataDictMenuIconDao appDataDictMenuIconDao;
    @Autowired
    private AppDataDictDao appDataDictDao;
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private DataSetDataDao dataSetDataDao;
    @Autowired
    private QueryRptService queryRptService;
    @Autowired
    private TimeFrameDao timeFrameDao;

    /**
     * 返回节点树
     */
    public List<AppDataDictMenu> queryDictMenus(User user, String projectPath, boolean showPushTree) {
        //用户 角色
//        String roleIds = getRoles(user);
        String roleIds = null;//todo 角色暂时不需要,需要时解开注释即可
        List<AppDataDictMenu> menus = appDataDictMenuDao.queryDictMenu(roleIds, showPushTree ? null : 1);
        if (ListUtil.notEmpty(menus)) {
            for (AppDataDictMenu menu : menus) {
                //设置图标
                menu.setIconByPath(projectPath);
            }
        }
        //若推送树显示,添加根节点
        if (showPushTree) {
            //移动数据目录
            AppDataDictMenu root = new AppDataDictMenu();
            root.setId(-1);
            root.setParentId(0);
            root.setName("移动数据目录");
            root.setType(AppDataDictMenu.TYPE_CONFIG);
            menus.add(root);
            //推送数据目录
            root = new AppDataDictMenu();
            root.setId(-2);
            root.setParentId(0);
            root.setName("推送数据目录");
            root.setType(AppDataDictMenu.TYPE_PUSH);
            menus.add(root);
        }
        return menus;
    }

    /**
     * 根据节点和名字查询数据字典目录
     *
     * @param user   当前用户
     * @param nodeId 查询的节点
     * @param name   名字
     */
    public Map<String, Object> queryDictMenus(User user, Integer nodeId, String name, final String projectPath) {
//        String roleIds = getRoles(user);
        String roleIds = null;//todo 角色暂时不需要,需要时解开注释即可
        //所有拥有权限且为显示状态的menu
        List<AppDataDictMenu> allMenus = queryDictMenusByParent(nodeId, roleIds, projectPath);
        //符合关键字搜索的menu
        List<AppDataDictMenu> menus = filterByName(allMenus, name);
        //组装成树
        PackageLocalListToTree<AppDataDictMenu> util = new PackageLocalListToTree<AppDataDictMenu>(allMenus) {
            @Override
            public Map<String, Object> getEntityMap(AppDataDictMenu appDataDictMenu) {
                Map<String, Object> entityMap = super.getEntityMap(appDataDictMenu);
                //设置图标
                AppDataDictMenuIcon menuIcon = appDataDictMenu.getMenuIcon();
                if (menuIcon != null) {
                    entityMap.put("menuIcon", projectPath + AppDataDictMenuIcon.iconPreUrl + menuIcon.getPath());
                }
                //设置背景
                AppDataDictMenuIcon menuBg = appDataDictMenu.getMenuBg();
                if (menuBg != null) {
                    entityMap.put("menuBg", projectPath + AppDataDictMenuIcon.bgPreUrl + menuBg.getPath());
                }
                List<AppDataDict> dicts = appDataDictDao.queryByMenu(appDataDictMenu.getId());
                entityMap.put("dirFlag", (dicts.size() == 0));
                return entityMap;
            }
        };
        //下级所有节点
        List<Map<String, Object>> maps = util.packageListToTree(menus, nodeId, false);
        //根节点
        if (ListUtil.notEmpty(maps)) {
            AppDataDictMenu menu = appDataDictMenuDao.queryById(nodeId);
            if (menu == null) {
                menu = new AppDataDictMenu();
                menu.setId(nodeId);
                menu.setName("数据字典目录");
            }
            Map<String, Object> entityMap = util.getEntityMap(menu);
            entityMap.put("children", maps);
            return entityMap;
        }
        return null;
    }

    /**
     * 查询某节点下所有显示状态的节点,以list形式返回
     */
    private List<AppDataDictMenu> queryDictMenusByParent(Integer menuId, String roleIds, String projectPath) {
        List<AppDataDictMenu> result = new ArrayList<>();
        List<AppDataDictMenu> menus = appDataDictMenuDao.queryDictMenu(menuId, roleIds, null, 1);
        if (ListUtil.notEmpty(menus)) {
            for (AppDataDictMenu menu : menus) {
                menu.setIconByPath(projectPath);
                result.add(menu);
                result.addAll(queryDictMenusByParent(menu.getId(), roleIds, projectPath));
            }
        }
        return result;
    }

    /**
     * 根据名称筛选
     */
    public List<AppDataDictMenu> filterByName(List<AppDataDictMenu> allMenus, String name) {
        if (!StringUtil.trimNotEmpty(name)) {
            return allMenus;
        }
        List<AppDataDictMenu> menus = new ArrayList<>();
        if (ListUtil.notEmpty(allMenus)) {
            for (AppDataDictMenu menu : allMenus) {
                if (menu.getName().toUpperCase().contains(name.trim().toUpperCase())) {
                    menus.add(menu);
                }
            }
        }
        return menus;
    }

    /**
     * 根据用户获取角色,管理员返回null,即不做检索,其他返回自己所拥有的角色
     *
     * @param user
     * @return
     */
    public String getRoles(User user) {
        String roleIds = null;
        if (!user.isAdmin()) {
            //角色
            Set<Role> roles = user.getRoles();
            if (ListUtil.notEmpty(roles)) {
                StringBuilder sb = new StringBuilder();
                for (Role role : roles) {
                    sb.append(role.getId()).append(",");
                }
                roleIds = sb.deleteCharAt(sb.length() - 1).toString();
            } else {
                roleIds = "-1";
            }
        }
        return roleIds;
    }

    /**
     * 复制目录
     *
     * @param fromId    拖拽的节点
     * @param toId      拖拽到的父节点
     * @param sortIndex 排序
     */
    public void copyDictMenus(Integer fromId, Integer toId, Integer sortIndex) throws Exception {
        AppDataDictMenu menu = appDataDictMenuDao.queryById(fromId);
        menu.setSortIndex(sortIndex);
        //复制本级
        Integer newId = copyMenu(menu, toId);
        //复制下级
        copyDownMenus(fromId, newId);
    }

    /**
     * 复制下级
     *
     * @param oldPId 原来的父节点
     * @param newPId 新的父节点
     */
    private void copyDownMenus(Integer oldPId, Integer newPId) throws Exception {
        List<AppDataDictMenu> menus = appDataDictMenuDao.queryDictMenu(oldPId, null, null, null);
        if (ListUtil.notEmpty(menus)) {
            for (AppDataDictMenu menu : menus) {
                //复制本级
                Integer newId = copyMenu(menu, newPId);
                //复制下级
                copyDownMenus(menu.getId(), newId);
            }
        }
    }

    /**
     * 复制单个目录
     *
     * @param menu     要复制的目录
     * @param parentId 父节点
     * @return 复制后的id
     * @throws Exception
     */
    private Integer copyMenu(AppDataDictMenu menu, Integer parentId) throws Exception {
        //复制
        AppDataDictMenu menuC = (AppDataDictMenu) BeanUtils.cloneBean(menu);
        menuC.setParentId(parentId);
        appDataDictMenuDao.insert(menuC, false);
        //复制内容
        Integer newId = menuC.getId();
        List<AppDataDict> dicts = appDataDictDao.queryByMenuAndType(menu.getId(), null);
        if (ListUtil.notEmpty(dicts)) {
            for (AppDataDict dict : dicts) {
                AppDataDict dictC = (AppDataDict) BeanUtils.cloneBean(dict);
                dictC.setMenuId(newId);
                //保存
                appDataDictDao.insert(dictC, false);
            }
        }
        return newId;
    }


    /**
     * 保存目录
     */
    public void saveDictMenu(AppDataDictMenu menu) {
        //设置图标
        AppDataDictMenuIcon menuIcon = menu.getMenuIcon();
        if (menuIcon != null && menuIcon.getId() != null) {
            AppDataDictMenuIcon appDataDictMenuIcon = appDataDictMenuIconDao.queryById(menuIcon.getId());
            menu.setMenuIcon(appDataDictMenuIcon);
        } else {
            menu.setMenuIcon(null);
        }
        //设置背景
        AppDataDictMenuIcon menuBg = menu.getMenuBg();
        if (menuBg != null && menuBg.getId() != null) {
            AppDataDictMenuIcon appDataDictMenuIcon = appDataDictMenuIconDao.queryById(menuBg.getId());
            menu.setMenuBg(appDataDictMenuIcon);
        } else {
            menu.setMenuBg(null);
        }
        //设置排序,按照父节点下最大的排序来设置
        if (menu.getSortIndex() == null) {
            menu.setSortIndex(queryMaxSort(menu.getParentId()));
        }
        appDataDictMenuDao.saveOrUpdate(menu, false);
    }

    /**
     * 保存目录:多个
     */
    public void saveDictMenu(List<AppDataDictMenu> menus) {
        if (ListUtil.notEmpty(menus)) {
            ConvertUtil<AppDataDictMenu> util = new ConvertUtil<>();
            //转换并保存
            for (AppDataDictMenu menu : menus) {
                if (menu.getId() != null) {
                    AppDataDictMenu curMenu = appDataDictMenuDao.loadById(menu.getId());
                    //赋值
                    util.apply(curMenu, menu, AppDataDictMenu.class);
                    appDataDictMenuDao.update(curMenu, true);
                } else {
                    appDataDictMenuDao.insert(menu, true);
                }
            }
        }
    }

    /**
     * 删除目录
     */
    public void deleteDictMenus(List<AppDataDictMenu> menus) {
        if (ListUtil.notEmpty(menus)) {
            for (AppDataDictMenu menu : menus) {
                //删除目录,先删除内容
                appDataDictDao.deleteDictsByMenu(menu.getId());
                appDataDictMenuDao.delete(menu, true);
            }
        }
    }

    /**
     * 根据名称获取角色
     *
     * @param user 当前用户
     * @param name 关键字
     */
    public List<Role> getRoles(User user, String name) {
        if (!StringUtil.trimNotEmpty(name)) {
            name = "";
        }
        List<Role> roles = null;
        if (user.isAdmin()) {
            roles = roleDao.getByName(name);
        } else {
            Set<Role> roleSet = user.getRoles();
            if (ListUtil.notEmpty(roleSet)) {
                roles = new ArrayList<>();
                for (Role role : roleSet) {
                    if (role.getName().contains(name)) {
                        roles.add(role);
                    }
                }
            }
        }
        for (Role role : roles) {
            role.setCreateUser(null);
        }
        return roles;
    }

    /**
     * 根据数据集查询时间
     */
    public List<TimePojo> queryRptTime(Integer menuId) {
        List<TimePojo> times = null;
        List<Integer> dataSetIds = appDataDictDao.queryDownDataSets(menuId);
        if (ListUtil.notEmpty(dataSetIds)) {
            times = new ArrayList<>();
            Set<TimePojo> timeSet = new HashSet<>();
            for (int dataSetId : dataSetIds) {
                List<TimePojo> timePojos = queryRptService.queryRptTime(dataSetId, null);
                if (ListUtil.notEmpty(timePojos)) {
                    timeSet.addAll(timePojos);
                }
            }
            times.addAll(timeSet);
            Collections.sort(times);//排序
        }
        return times;
    }

    /**
     * 根据目录获取数据
     *
     * @param menuId 父节点,获取子节点所有的数据
     * @param time   时间
     */
    public Map<Integer, Map<String, Object>> queryRptData(Integer menuId, TimePojo time) {
        Map<Integer, Map<String, Object>> result = null;
        //首先获取所有下级(显示的)
        List<AppDataDictMenu> downMenus = appDataDictMenuDao.queryDictMenu(menuId, null, null, 1);
        if (ListUtil.notEmpty(downMenus)) {
            result = new HashMap<>();//结果
            Map<String, Object> info = null;//每个目录对应的数据
            //开始查询拼接
            for (AppDataDictMenu menu : downMenus) {
                List<AppDataDict> dataDicts = appDataDictDao.queryByMenuAndType(menu.getId(), AppConstant.DATA_DICT_TYPE.DATA_SET);//数据集
                if (ListUtil.notEmpty(dataDicts)) {
                    info = new HashMap<>();
                    AppDataDict dataDict = dataDicts.get(0);//数据集
                    //获取数据集时间框架
                    Integer dataSetId = dataDict.getDataValue();
                    List<DataSetData> conditions = dataSetDataDao.queryDataSetData(dataSetId, Constant.MetadataType.TIME_FRAME, null);
                    //数据集查询的数据
                    List<RptDataPojo> dataPojos = queryRptService.queryRptDatas(dataSetId, null, null);
                    info.put("curDatas", getTfCurDatas(dataPojos, time, conditions));//当前数据的值
                    info.put("preDatas", getPreDatas(dataPojos, time, conditions));//之前几期数据的值
                    result.put(menu.getId(), info);
                }
            }
        }
        return result;
    }

    /**
     * 获取当前期的时间框架的值
     *
     * @param datas 所有数据
     * @param tfs   时间框架
     */
    private List<Map<String, Object>> getTfCurDatas(List<ReportData> datas, List<DataSetData> tfs) {
        List<Map<String, Object>> result = null;
        if (ListUtil.notEmpty(datas) && ListUtil.notEmpty(tfs)) {
            result = new ArrayList<>();
            Map<String, Object> info = null;
            for (DataSetData tf : tfs) {
                Integer tfId = Integer.parseInt(tf.getDataValue());
                String value = null;
                //获取数值
                for (ReportData data : datas) {
                    if (data.getReportDataId().getTimeFrame().equals(tfId)) {//同一时间框架
                        value = data.getItemValue();
                        break;
                    }
                }
                info = new HashMap<>();
                //若有备注名,使用备注!
                TimeFrame timeFrame = timeFrameDao.queryById(tfId);
                String comments = timeFrame.getComments();
                info.put("name", StringUtil.trimNotEmpty(comments) ? comments : tf.getDataName());
                info.put("id", tfId);
                info.put("value", value);
                result.add(info);
            }
        }
        return result;
    }

    /**
     * 根据时间获取数据列表
     */
    private List<Map<String, Object>> getTfCurDatas(List<RptDataPojo> dataPojos, TimePojo time, List<DataSetData> tfs) {
        if (ListUtil.notEmpty(dataPojos)) {
            for (RptDataPojo pojo : dataPojos) {
                if (pojo.getTime().equals(time)) {
                    return getTfCurDatas(pojo.getDatas(), tfs);
                }
            }
        }
        return null;
    }

    /**
     * 获取之前几期的数据
     */
    private List<Map<String, Object>> getPreDatas(List<RptDataPojo> dataPojos, TimePojo time, List<DataSetData> tfs) {
        List<Map<String, Object>> result = null;
        if (ListUtil.notEmpty(dataPojos)) {
            result = new ArrayList<>();
            int count = 0;
            Map<String, Object> info = null;
            for (int i = dataPojos.size() - 1; i >= 0; i--) {
                RptDataPojo dataPojo = dataPojos.get(i);
                TimePojo curTime = dataPojo.getTime();//当前数据的时间
                if (time.compareTo(curTime) >= 0 && count++ < Constant.systemConfigPojo.getMainFocusSpan()) {//在前
                    info = new HashMap<>();
                    info.put("time", curTime);
                    info.put("datas", getTfCurDatas(dataPojo.getDatas(), tfs));
                    result.add(0, info);
                }
            }
        }
        return result;
    }

    /**
     * 查询最大排序
     */
    private int queryMaxSort(Integer parentId) {
        int maxSort = 1;
        List<Integer> list = appDataDictMenuDao.queryMaxSort(parentId);
        if (ListUtil.notEmpty(list)) {
            Integer m = list.get(0);
            if (m != null) {
                maxSort += list.get(0);
            }
        }
        return maxSort;
    }
}
