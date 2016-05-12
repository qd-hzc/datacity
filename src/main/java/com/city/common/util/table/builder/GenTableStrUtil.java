package com.city.common.util.table.builder;

import com.city.common.pojo.Constant;
import com.city.common.util.EsiLogUtil;
import com.city.common.util.ListUtil;
import com.city.common.util.StringUtil;
import com.city.common.util.table.pojo.*;
import com.city.support.manage.item.dao.ItemInfoDao;
import com.city.support.manage.item.entity.ItemInfo;
import com.google.gson.Gson;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by wxl on 2016/1/26 0026.
 * 根据主宾蓝生成表格
 */
public abstract class GenTableStrUtil<T> {
    private EsiTable esiTable;
    private List<EsiNode> mainBar;
    private Integer mainMaxDepth = 0;//主栏最大深度
    private boolean mainNotNull;//主栏非空
    private List<EsiNode> guestBar;
    private Integer guestMaxDepth = 0;//宾栏最大深度
    private boolean guestNotNull;//宾栏非空
    private List<Element> guestLeaves = new ArrayList<>();//宾栏的叶子节点
    //默认单元格单位选择器,该选择器使用的key为 指标,时间框架,如1,2
    //而通常的选择器使用:指标,时间框架,[指标分组目录],调查对象;如 1,2,[3,4],2
    private Map<String, EsiTdUnit> defaultUnitSelector = new HashMap<>();
    //容器,用于获取bean
    protected ApplicationContext ctx;
    //模板
    protected T tmp = null;

    //表格的属性名
    public static class TableAttrName {
        //报表id
        public static final String RPT_ID = "esi-rptid";
        //模板id
        public static final String TMP_ID = "esi-tmpid";
        //表格的class
        public static final String TABLE_CLASS = "esi";
        public static final String CELL_SPACING = "cellspacing";

        //        thead的样式
        public static final String THEAD_CLASS = "esi-thead";
        //        tfoot的样式
        public static final String TFOOT_CLASS = "esi-tfoot";
    }

    //单元格的属性名
    public static class TdAttrName {
        public static final String COLSPAN = "colspan";
        public static final String ROWSPAN = "rowspan";
        //元素类型
        public static final String TYPE = "esi-type";
        //单元格单位
        public static final String UNIT = "esi-data-unit";
        //单元格单位名称
        public static final String UNIT_NAME = "esi-data-unitname";
        //单元格数据格式
        public static final String DATA_FORMAT = "esi-data-format";
        //单元格的数据
        public static final String DATA_VALUE = "esi-data-value";
        //实节点样式
        public static final String REALNODE_CLASS = "real";

        //        单元格水平居中
        public static final String ALIGN = "align";
        //        单元格竖向居中
        public static final String VALIGN = "valign";
        //        单元格样式
        public static final String CLASS = "class";
    }

    /**
     * 表格样式中的class类
     */
    public static class Classes {
        //        左边框为0像素
        public static final String BORDERLEFT_0 = "borderleft-0";
    }

    //数据域中对应的键值对名
    public static class ProAttrName {
        public static final String DEP = "depid";
        public static final String ITEM = "item";
        public static final String TIME_FRAME = "timeframe";
        public static final String ITEM_DICT = "itemdict";
        public static final String ITEM_GROUP = "itemgroup";
        public static final String ITEM_CALIBER = "itemcaliber";
        public static final String SUR_OBJ = "surobj";
        public static final String SUR_OBJ_TYPE = "surobjtype";
        public static final String SUR_OBJ_GROUP = "surobjgroup";
        public static final String AREA = "area";
        public static final String YEAR = "year";
        public static final String TIME = "time";
        public static final String RPT = "rptid";
        public static final String TMP = "tmpid";
    }

    //构造函数,传入表格主宾蓝信息
    public GenTableStrUtil(EsiTable esiTable, HttpServletRequest request) {
        this.esiTable = esiTable;
        this.mainBar = esiTable.getMainBar();
        this.guestBar = esiTable.getGuestBar();
        //初始化节点 主栏
        if (mainBar != null && mainBar.size() > 0) {
            mainNotNull = true;
            for (EsiNode node : mainBar) {
                initNodeSpan(node, true);
            }
        } else {
            mainNotNull = false;
        }
        //初始化节点 宾栏
        if (guestBar != null && guestBar.size() > 0) {
            guestNotNull = true;
            for (EsiNode node : guestBar) {
                initNodeSpan(node, false);
            }
        } else {
            guestNotNull = false;
        }
        if (request != null) {
            ctx = WebApplicationContextUtils.getWebApplicationContext(request.getServletContext());
            initTmp(esiTable.getTmpId());
            initDefaultUnitSelector();
        }
    }

    /**
     * 初始化
     */
    protected abstract void initTmp(Integer tmpId);

    /**
     * 初始化节点的span
     *
     * @param node      节点
     * @param isMainBar 是否为主栏
     * @return 节点的span信息
     */
    private int initNodeSpan(EsiNode node, boolean isMainBar) {
        if (!node.isLeaf()) {
            List<EsiNode> children = node.getChildren();
            if (children != null && children.size() > 0) {
                int span = 0;
                for (EsiNode child : children) {
                    span += initNodeSpan(child, isMainBar);
                }
                node.setSpan(span);
                return span;
            }
        }
        //设置最大深度
        int depth = node.getDepth();
        if (isMainBar) {//主栏
            if (depth > mainMaxDepth) {
                mainMaxDepth = depth;
            }
        } else {
            if (depth > guestMaxDepth) {
                guestMaxDepth = depth;
            }
        }
        //设置span
        node.setSpan(1);
        return 1;
    }

    //生成表格信息
    public Element genTable() {
        Element tableStr = null;
        if (mainNotNull) {//主栏不为空
            if (guestNotNull) {//宾栏不为空,固定表格
                tableStr = genStaticTable();
            } else {//宾栏为空,宾栏不定长表格

            }
        } else {//主栏为空
            if (guestNotNull) {//宾栏不为空,主栏不定长表格

            } else {//宾栏为空,主宾蓝不定长表格,暂不支持此类型

            }
        }
        EsiLogUtil.debug(EsiLogUtil.getLogInstance(this.getClass()), "\n" + tableStr.toString());
        return tableStr;
    }

    //生成固定表格
    private Element genStaticTable() {
        //表格
        Element table = new Element(Tag.valueOf("table"), "");
        table.addClass(TableAttrName.TABLE_CLASS);
        table.attr(TableAttrName.CELL_SPACING, "0");
        if (esiTable.getTmpId() != null) {//设置模板id
            table.attr(TableAttrName.TMP_ID, esiTable.getTmpId().toString());
        }
        //生成表格的结构
        //thead
        Element thead = new Element(Tag.valueOf("thead"), "");
        thead.addClass(TableAttrName.THEAD_CLASS);
        table.appendChild(thead);
        //tbody
        Element tbody = new Element(Tag.valueOf("tbody"), "");
        table.appendChild(tbody);
        //tfoot
        Element tfoot = new Element(Tag.valueOf("tfoot"), "");
        tfoot.addClass(TableAttrName.TFOOT_CLASS);
        table.appendChild(tfoot);
        //开始添加tbody内容
        //初始化宾栏的行数
        Element[] trs = new Element[guestMaxDepth];
        for (int i = 0; i < trs.length; i++) {
            Element tr = new Element(Tag.valueOf("tr"), "");
            tbody.appendChild(tr);
            trs[i] = tr;
        }
        //插入第一个单元格,主宾蓝交叉位置
        Element td = new Element(Tag.valueOf("td"), "");
        td.attr(TdAttrName.ROWSPAN, guestMaxDepth.toString());
        td.attr(TdAttrName.COLSPAN, mainMaxDepth.toString());
        td.attr(TdAttrName.CLASS, Classes.BORDERLEFT_0);
        trs[0].appendChild(td);
        //开始插入宾栏
        for (EsiNode node : guestBar) {
            insertGuestTds(node, trs, guestMaxDepth);
        }
        //开始插入主栏
        List<Element> mainTrs = insertMainTds(mainBar, null, mainMaxDepth);
        for (Element tr : mainTrs) {
            tbody.appendChild(tr);
        }
        //插入thead内容
        if (tmp != null) {
            String headColSpan = mainMaxDepth + guestLeaves.size() + "";//colspan
            //插入thead内容
            Element theadTd = new Element(Tag.valueOf("td"), "");
            theadTd.attr(TdAttrName.COLSPAN, headColSpan);
            theadTd.appendText(getTHeadText());
            theadTd.attr(TdAttrName.ALIGN, "center");
            thead.appendChild(theadTd);
            //插入tfoot内容
            String tfootText = getTFootText();
            if (tfootText != null && tfootText.trim().length() > 0) {
                Element tfootTd = new Element(Tag.valueOf("td"), "");
                tfootTd.attr(TdAttrName.COLSPAN, headColSpan);
                tfootTd.appendText(tfootText != null ? tfootText : "");
                tfoot.appendChild(tfootTd);
            } else {
                tfoot.remove();
            }
        } else {
            thead.remove();
            tfoot.remove();
        }
        return table;
    }

    //获取thead内容
    protected abstract String getTHeadText();

    //获取tfoot内容
    protected abstract String getTFootText();

    /**
     * 插入宾栏的单元格
     *
     * @param node     节点
     * @param trs      宾栏的行数
     * @param maxDepth 宾栏的最大深度
     */
    private void insertGuestTds(EsiNode node, Element[] trs, int maxDepth) {
        Element td = new Element(Tag.valueOf("td"), "");
        if (node.isRealNode()) {//实节点
            td.addClass(TdAttrName.REALNODE_CLASS);
        } else {
            td.appendText(node.getDataName());
        }
        //设置colspan
        td.attr(TdAttrName.COLSPAN, node.getSpan().toString());
        //设置rowspan
        if (node.isLeaf()) {
            td.attr(TdAttrName.ROWSPAN, (maxDepth - node.getDepth() + 1) + "");
            //设置属性
            addProperties(td, node.getProperties());
        } else {
            td.attr(TdAttrName.ROWSPAN, "1");
        }
        //设置单元格类型
        td.attr(TdAttrName.TYPE, Constant.TdEsiType.GUEST_BAR);
        td.attr(TdAttrName.ALIGN, "center");
        td.attr(TdAttrName.VALIGN, "middle");
        //添加进去
        trs[node.getDepth() - 1].appendChild(td);
        //遍历子集
        if (node.isLeaf()) {
            Element tdClone = td.clone();
            tdClone.text("");
            tdClone.removeClass(TdAttrName.REALNODE_CLASS);
            tdClone.attr(TdAttrName.ROWSPAN, "1");
            guestLeaves.add(tdClone);
        } else {
            List<EsiNode> children = node.getChildren();
            for (EsiNode child : children) {
                insertGuestTds(child, trs, maxDepth);
            }
        }
    }

    /**
     * 插入主栏的单元格,返回插入后的结果
     *
     * @param nodes    要插入的节点,包含下级节点
     * @param tr       将单元格插入的行,若为空,则新建一个
     * @param maxDepth 最大深度
     */
    private List<Element> insertMainTds(List<EsiNode> nodes, Element tr, int maxDepth) {
        List<Element> trs = new ArrayList<>();
        int index = 0;
        //遍历
        for (EsiNode node : nodes) {
            Element td = new Element(Tag.valueOf("td"), "");
            if (node.isRealNode()) {//实节点
                td.addClass(TdAttrName.REALNODE_CLASS);
            } else {
                td.appendText(node.getDataName());
            }
            //设置rowspan
            td.attr(TdAttrName.ROWSPAN, node.getSpan().toString());
            if (node.getDepth() > 1) {
                td.attr("esi-left", "true");
            }
            //设置colspan
            if (node.isLeaf()) {
                td.attr(TdAttrName.COLSPAN, (maxDepth - node.getDepth() + 1) + "");
            } else {
                td.attr(TdAttrName.COLSPAN, "1");
            }
            if (node.getDepth() == 1 && !node.isRealNode()) {
                td.attr(TdAttrName.CLASS, Classes.BORDERLEFT_0);
            }
            //设置单元格类型
            td.attr(TdAttrName.TYPE, Constant.TdEsiType.MAIN_BAR);
            //将单元格插入行
            if (index++ == 0) {//第一个值
                if (tr == null) {
                    tr = new Element(Tag.valueOf("tr"), "");
                }
                tr.appendChild(td);
                trs.add(tr);
            } else {
                tr = new Element(Tag.valueOf("tr"), "");
                tr.appendChild(td);
                trs.add(tr);
            }
            //将行插入
            if (node.isLeaf()) {//把所有数据域加入
                //添加属性属性
                addProperties(td, node.getProperties());
                insertDataTds(tr, node.getProperties());
            } else {//把所有子集的行加入
                trs.addAll(insertMainTds(node.getChildren(), tr, maxDepth));
            }
        }
        return trs;
    }

    /**
     * 插入数据域
     *
     * @param tr         将单元格插入的行
     * @param properties 要添加的属性
     */
    private void insertDataTds(Element tr, List<EsiProperty> properties) {
        if (guestLeaves.size() > 0) {
            for (Element td : guestLeaves) {
                Element tdClone = td.clone();
                tdClone.attr(TdAttrName.TYPE, Constant.TdEsiType.DATA);
                tdClone.attr(TdAttrName.ALIGN, "right");
                tdClone.attr(TdAttrName.VALIGN, "middle");
                addProperties(tdClone, properties);
                //此处设置数据域的单位和数据格式
                addTdUnit(tdClone);
                tr.appendChild(tdClone);
            }
        }
    }

    /**
     * 給单元格添加属性
     *
     * @param td         要添加属性的单元格
     * @param properties 要添加的属性
     */
    private void addProperties(Element td, List<EsiProperty> properties) {
        if (properties != null && properties.size() > 0) {
            //获取原来的属性
            Map<String, Object> pro = getProMap(td.attr(TdAttrName.DATA_VALUE));
            List<Integer> itemMenus = getItemMenuList(pro);
            for (EsiProperty property : properties) {
                switch (property.getDataType()) {
                    case Constant.MetadataType.ITEM://指标
                        pro.put(ProAttrName.ITEM, property.getDataValue());
                        pro.put(ProAttrName.ITEM_CALIBER, property.getDataInfo1());
                        String depByItem = getDepByItem(property);
                        if (StringUtil.notEmpty(depByItem)) {
                            pro.put(ProAttrName.DEP, depByItem);
                        }
                        break;
                    case Constant.MetadataType.ITEM_GROUP://指标体系分组
                        pro.put(ProAttrName.ITEM_GROUP, property.getDataValue());
                        break;
                    case Constant.MetadataType.ITEM_MENU://指标分组目录
                        itemMenus.add(property.getDataValue());
                        //排序加入
                        Collections.sort(itemMenus);
                        String itemMenuStr = ListUtil.getArrStr(itemMenus);
                        pro.put(ProAttrName.ITEM_DICT, itemMenuStr);
                        break;
                    case Constant.MetadataType.RESEARCH_OBJ_GROUP://调查对象分组
                        pro.put(ProAttrName.SUR_OBJ_GROUP, property.getDataValue());
                        break;
                    case Constant.MetadataType.RESEARCH_OBJ://调查对象类型
                        pro.put(ProAttrName.SUR_OBJ, property.getDataValue());
                        pro.put(ProAttrName.SUR_OBJ_TYPE, property.getDataInfo1());
                        //设置地区
                        pro.put(ProAttrName.AREA, property.getDataInfo2());
                        break;
                    case Constant.MetadataType.TIME_FRAME://时间框架
                        pro.put(ProAttrName.TIME_FRAME, property.getDataValue());
                        break;
                    case Constant.MetadataType.TIME://时间类型
                        //设置时间
                        if (Integer.parseInt(property.getDataInfo1()) == Constant.TIMERANGE.INFO_YEAR) {//年份数据
                            if (property.getDataInfo2().equals(Constant.TIMERANGE.BAOGAOQI.toString())) {//期数
                                pro.put(ProAttrName.YEAR, property.getDataName());
                            } else {
                                pro.put(ProAttrName.YEAR, property.getDataValue());
                            }
                        } else {//期度数据
                            if (property.getDataInfo2().equals(Constant.TIMERANGE.BAOGAOQI.toString())) {//期数
                                pro.put(ProAttrName.TIME, property.getDataName());
                            } else {
                                pro.put(ProAttrName.TIME, property.getDataValue());
                            }
                        }
                        break;
                }
            }
            //设为属性
            Gson gson = new Gson();
            String proStrs = gson.toJson(pro);
            td.attr(TdAttrName.DATA_VALUE, proStrs.replaceAll("\"", "'"));
        }
    }

    /**
     * 給单元格添加单位的属性
     *
     * @param td
     */
    private void addTdUnit(Element td) {
        //获取单元格属性信息
        String datas = td.attr(TdAttrName.DATA_VALUE);
        Map<String, Object> tdData = new Gson().fromJson(datas, HashMap.class);
        //指标
        int itemId = ((Double) tdData.get(ProAttrName.ITEM)).intValue();
        //时间框架
        int timeframeId = ((Double) tdData.get(ProAttrName.TIME_FRAME)).intValue();
        //分组目录
        Object itemDict = tdData.get(ProAttrName.ITEM_DICT);
        String itemDictId = "0";
        if (itemDict != null) {
            itemDictId = (String) itemDict;
        }
        //调查对象
        Object surObj = tdData.get(ProAttrName.SUR_OBJ);
        int surObjId = 0;
        if (surObj != null) {
            surObjId = ((Double) surObj).intValue();
        }
        //根据单位选择器获取
        if (esiTable.getUnitSelector() != null) {
            EsiTdUnit tdUnit = esiTable.getUnitSelector().get(itemId + "," + timeframeId + ",[" + itemDictId + "]," + surObjId);
            if (tdUnit != null) {//获取到了
                td.attr(TdAttrName.UNIT, tdUnit.getUnitId().toString());
                td.attr(TdAttrName.UNIT_NAME, tdUnit.getUnitName());
                td.attr(TdAttrName.DATA_FORMAT, tdUnit.getDataFormat());
                return;
            }
        }
        //根据默认单位选择器获取
        EsiTdUnit tdUnit = defaultUnitSelector.get(itemId + "," + timeframeId);
        if (tdUnit != null) {//获取到了
            td.attr(TdAttrName.UNIT, tdUnit.getUnitId().toString());
            td.attr(TdAttrName.UNIT_NAME, tdUnit.getUnitName());
            td.attr(TdAttrName.DATA_FORMAT, tdUnit.getDataFormat());
        }
    }

    /**
     * 根据属性值获取属性map
     */
    private Map<String, Object> getProMap(String dataValue) {
        if (dataValue != null && dataValue.trim().length() > 0) {
            //TODO 使用JSONObject来转化,可避免出现double类型数值
            return new Gson().fromJson(dataValue, HashMap.class);
        }
        return new HashMap<>();
    }

    /**
     * 根据属性map获取分组目录
     */
    private List<Integer> getItemMenuList(Map<String, Object> pro) {
        String itemMenuStrs = (String) pro.get(ProAttrName.ITEM_DICT);
        List<Integer> result = new ArrayList<>();
        if (itemMenuStrs != null && itemMenuStrs.trim().length() > 0) {
            String[] itemMenus = itemMenuStrs.split(",");
            for (String itemMenu : itemMenus) {
                result.add(Integer.parseInt(itemMenu.trim()));
            }
        }
        return result;
    }

    /**
     * 初始化默认选择器
     */
    private void initDefaultUnitSelector() {
        Map<String, EsiTdUnit> defaultUnitSelector = new HashMap<>();
        ItemInfoDao infoDao = ctx.getBean(ItemInfoDao.class);
        List<ItemInfo> itemInfos = infoDao.queryAll();
        if (itemInfos != null && itemInfos.size() > 0) {
            EsiTdUnit tdUnit = null;
            for (ItemInfo itemInfo : itemInfos) {
                tdUnit = new EsiTdUnit();
                tdUnit.setUnitId(itemInfo.getUnit().getId());
                tdUnit.setUnitName(itemInfo.getUnit().getName());
                tdUnit.setDataFormat(itemInfo.getDataFormat());
                String key = itemInfo.getItemId() + "," + itemInfo.getTimeFrame().getId();
                defaultUnitSelector.put(key, tdUnit);
            }
        }
        this.defaultUnitSelector = defaultUnitSelector;
    }

    /**
     * 根据指标获取部门
     *
     * @param property
     * @return
     */
    abstract protected String getDepByItem(EsiProperty property);

}

