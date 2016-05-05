<%--
  Created by IntelliJ IDEA.
  User: Paul
  Date: 2016/1/13
  Time: 16:19
  To change this template use File | Settings | File Templates.
  TODO 拖动节点复制功能,点击节点显示实虚节点
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String contextPath = request.getContextPath();
%>
<!doctype html>
<html lang="en">
<head>
    <title>设计报表</title>
    <jsp:include page="../../../common/imp.jsp"/>
    <jsp:include page="../../../common/metaDataImp.jsp"/>
    <jsp:include page="../../../common/sysConstant.jsp"/>
    <link rel="stylesheet" href="<%=contextPath%>/City/support/regime/report/css/esiTable.css">
</head>
<body>
<script type="text/javascript">

    var indexPanel;
    var contextPath = "<%=contextPath%>";
    //报表类型
    var rptDesignType = RPT_DESIGN_TYPE.SYNTHESIS;
    //表样信息
    var style =${style};

    //    表样的html
    var htmlTable;
    var period = style.reportTemplate.period;
    //表样类型
    var styleTypes =${styleTypes};
    var styleType = style.styleType;
    var styleTypeStr = '';
    for (var i = 0; i < styleTypes.length; i++) {
        if (styleType == styleTypes[i].value) {
            styleTypeStr = styleTypes[i].text;
            break;
        }
    }
    //源数据类型
    var metaDataTypes =${metaDataTypes};
    var MARGIN_ROW_SPACE = '4 8';
    //单位选择器
    var tdUnitSelector = {};
    //渲染页面
    Ext.onReady(function () {
        //指标体系store
        var itemGroupStore = Ext.create('Ext.data.TreeStore', {
            fields: ['dataType', 'dataName', 'text', 'dataValue', 'dataInfo1', 'dataInfo2', 'children', 'leaf'],
            proxy: {
                type: 'ajax',
                api: {
                    read: contextPath + '/support/regime/report/designReport/getItemTree'
                }
            },
            root: {
                expanded: true,
                id: 0,
                text: "根节点"
            }
        });
        //指标体系树
        var itemNorthTree = Ext.create('Ext.tree.Panel', {
            height: '50%',
            store: itemGroupStore,
            rootVisible: false,
            region: 'north',
            viewConfig: {
                plugins: {
                    ptype: 'treeviewdragdrop',
                    ddGroup: 'ddTreeGroup',//自定义名字
                    enableDrop: false
                }
            },
            tbar: ['<b>指标</b>', {
                xtype: 'textfield',
                emptyText: '在此处查询指标',
                triggerCls: 'x-form-clear-trigger',
                onTriggerClick: function () {
                    this.reset();
                },
                listeners: {
                    change: function (_this, n, o) {
                        queryTreeByLocal(itemNorthTree, itemGroupStore, 'dataName', n);
                    }
                }
            }]
        });
        //分组store
        var groupStore = Ext.create('Ext.data.TreeStore', {
            fields: ['dataType', 'dataName','text', 'dataValue', 'dataInfo1', 'dataInfo2', 'children', 'leaf'],
            proxy: {
                type: 'ajax',
                api: {
                    read: contextPath + '/support/regime/report/designReport/getGroupInfoTrees'
                }
            },
            root: {
                text: '根节点',
                expanded: true
            }
        });
        //分组树
        var itemSouthTree = Ext.create('Ext.tree.Panel', {
            height: '50%',
            store: groupStore,
            rootVisible: false,
            region: 'south',
            viewConfig: {
                plugins: {
                    ptype: 'treeviewdragdrop',
                    ddGroup: 'ddTreeGroup',//自定义名字
                    enableDrop: false
                }
            },
            tbar: ['<b>分组</b>', {
                xtype: 'textfield',
                emptyText: '在此处查询内容',
                triggerCls: 'x-form-clear-trigger',
                onTriggerClick: function () {
                    this.reset();
                },
                listeners: {
                    change: function (_this, n) {
                        queryTreeByLocal(itemSouthTree, groupStore, 'dataName', n);
                    }
                }
            }]
        });
        //主栏store
        var mainBarStore = Ext.create('Ext.data.TreeStore', {
            fields: ['dataName', 'text', 'dataValue', 'dataInfo1', 'dataInfo2', 'dataType', 'isRealNode', 'isProperty', 'leaf', 'expanded', 'children'],
            proxy: {
                type: 'ajax',
                api: {
                    read: contextPath + '/support/regime/report/designReport/getReportBarTree'
                },
                extraParams: {
                    isMainBar: 1,
                    styleId: style.id
                }
            },
            root: {
                expanded: true,
                text: "主栏"
            }
        });
        //主栏
        var mainBarTree = Ext.create('Ext.tree.Panel', {
            height: '50%',
            store: mainBarStore,
            rootVisible: true,
            region: 'north',
            enableDD: true,//是否支持拖拽效果
            viewConfig: {
                name: 'barTreeView',
                plugins: {
                    ptype: 'treeviewdragdrop',
                    ddGroup: 'ddTreeGroup',//自定义名字
                    expandDelay: 100
                },
                allowCopy: true,
                listeners: {
                    'beforedrop': function (n, data, overModel, dropPosition, dropHandlers) {
                        var thisNode = data.records[0];
                        //校验父节点
                        if (!overModel.isRoot()) {
                            if (!checkTreeNodeValid(thisNode, overModel.parentNode)) {
                                Ext.Msg.alert('提示', '类型重复!');
                                return false;
                            }
                        }
                        //校验另外一棵树的节点
                        if (!checkAnotherTreeValid(thisNode, guestBarTree)) {
                            Ext.Msg.alert('提示', '该类型只能在同一侧存在');
                            return false;
                        }
                        dropHandler(data, overModel, dropHandlers);
                    }
                }
            },
            listeners: {
                'itemclick': function (_this, record) {
                    guestBarTree.getSelectionModel().deselectAll();
                    nodeInfoStore.loadRawData(getNodeInfo(record));
                    if (barMenu) {
                        barMenu.hide();
                    }
                },
                itemcontextmenu: itemcontextHandler,
                containerclick: function () {
                    if (barMenu) {
                        barMenu.hide();
                    }
                }
            }
        });
        //宾栏store
        var guestBarStore = Ext.create('Ext.data.TreeStore', {
            fields: ['dataName','text', 'dataValue', 'dataInfo1', 'dataInfo2', 'dataType', 'isRealNode', 'isProperty', 'leaf', 'expanded', 'children'],
            proxy: {
                type: 'ajax',
                api: {
                    read: contextPath + '/support/regime/report/designReport/getReportBarTree'
                },
                extraParams: {
                    isMainBar: 0,
                    styleId: style.id
                }
            },
            root: {
                expanded: true,
                text: "宾栏"
            }
        });
        //菜单
        var barMenu;
        //宾栏
        var guestBarTree = Ext.create('Ext.tree.Panel', {
            height: '50%',
            store: guestBarStore,
            rootVisible: true,
            region: 'south',
            enableDD: true,//是否支持拖拽效果
            viewConfig: {
                name: 'barTreeView',
                plugins: {
                    ptype: 'treeviewdragdrop',
                    ddGroup: 'ddTreeGroup'
                },
                allowCopy: true,
                listeners: {
                    'beforedrop': function (n, data, overModel, dropPosition, dropHandlers) {
                        var thisNode = data.records[0];
                        //校验父节点
                        if (!overModel.isRoot()) {
                            if (!checkTreeNodeValid(thisNode, overModel.parentNode)) {
                                Ext.Msg.alert('提示', '类型重复!');
                                return false;
                            }
                        }
                        //校验另外一棵树的节点
                        if (!checkAnotherTreeValid(thisNode, mainBarTree)) {
                            Ext.Msg.alert('提示', '该类型只能在同一侧存在');
                            return false;
                        }
                        dropHandler(data, overModel, dropHandlers, mainBarTree);
                    }
                }
            },
            listeners: {
                'itemclick': function (_this, record) {
                    mainBarTree.getSelectionModel().deselectAll();
                    nodeInfoStore.loadRawData(getNodeInfo(record));
                    if (barMenu) {
                        barMenu.hide();
                    }
                },
                itemcontextmenu: itemcontextHandler,
                containerclick: function () {
                    if (barMenu) {
                        barMenu.hide();
                    }
                }
            }
        });
        //节点信息表格store
        var nodeInfoStore = new Ext.data.Store({
            fields: ['dataName','text', 'dataValue', 'dataInfo1', 'dataInfo2', 'dataType', 'isRealNode', 'isProperty'],
            data: []
        });
        //节点信息表格菜单
        var nodeInfoMenu = new Ext.menu.Menu({
            renderTo: Ext.getBody(),
            items: [{
                text: '修改',
                iconCls: 'Pageedit',
                handler: function () {
                    //选中的节点
                    var selModel;
                    if (mainBarTree.getSelectionModel().hasSelection()) {
                        selModel = mainBarTree.getSelectionModel().getSelection()[0];
                    } else if (guestBarTree.getSelectionModel().hasSelection()) {
                        selModel = guestBarTree.getSelectionModel().getSelection()[0];
                    } else {
                        Ext.Msg.alert('提示', '未选中节点不能保存!');
                        return;
                    }
                    //修改
                    var sel = tablePanelNorth.getSelectionModel().getSelection();
                    if (sel.length == 1) {
                        var record = sel[0];
                        if (record.get('dataType') == METADATA_TYPE.ITEM) {
                            Ext.addNodeInfoWin.init(record, function () {
                                saveProperties(selModel);
                            });
                        } else {
                            Ext.Msg.alert('提示', '只能对指标修改');
                        }
                    } else {
                        Ext.Msg.alert('提示', '请选中一条信息修改');
                    }
                }
            }, {
                text: '删除',
                iconCls: 'Delete',
                handler: function () {
                    //选中的节点
                    var selModel;
                    if (mainBarTree.getSelectionModel().hasSelection()) {
                        selModel = mainBarTree.getSelectionModel().getSelection()[0];
                    } else if (guestBarTree.getSelectionModel().hasSelection()) {
                        selModel = guestBarTree.getSelectionModel().getSelection()[0];
                    } else {
                        Ext.Msg.alert('提示', '未选中节点不能删除!');
                        return;
                    }
                    //删除
                    var sel = tablePanelNorth.getSelectionModel().getSelection();
                    if (sel) {
                        nodeInfoStore.remove(sel);
                        saveProperties(selModel);
                    }
                }
            }]
        });
        //节点和单元格信息区
        var tablePanelNorth = Ext.create('Ext.grid.Panel', {
            height: '100%',
            flex: 2,
            border: 0,
            store: nodeInfoStore,
            viewConfig: {
                plugins: {
                    ptype: 'gridviewdragdrop',
                    ddGroup: 'ddTreeGroup',
                    enableDrag: false
                },
                listeners: {
                    'beforedrop': function (n, data, overModel, dropPosition, dropHandlers) {
                        if (data.view.name == 'barTreeView') {//从主栏或者宾栏拖拽过来的
                            Ext.Msg.alert('提示', '不能拖主宾栏的信息');
                            return false;
                        }
                        var record = data.records[0];
                        var selModel;
                        var anotherTree;
                        if (mainBarTree.getSelectionModel().hasSelection()) {
                            selModel = mainBarTree.getSelectionModel().getSelection()[0];
                            anotherTree = guestBarTree;
                        } else if (guestBarTree.getSelectionModel().hasSelection()) {
                            selModel = guestBarTree.getSelectionModel().getSelection()[0];
                            anotherTree = mainBarTree;
                        } else {
                            Ext.Msg.alert('提示', '未选中节点不能保存!');
                            return false;
                        }
                        if (selModel.isRoot()) {
                            Ext.Msg.alert('提示', '不能向主宾栏拖信息');
                            return false;
                        }
                        //校验信息
                        if (!checkTreeNodeValid(record, selModel)) {
                            Ext.Msg.alert('提示', '类型重复!');
                            return false;
                        }
                        //校验另一棵树信息
                        if (!checkAnotherTreeValid(record, anotherTree)) {
                            Ext.Msg.alert('提示', '该类型只能存在一侧!');
                            return false;
                        }
                        //校验完毕,继续
                        var obj = getSimpleObj(record.data);
                        obj.isProperty = 1;
                        nodeInfoStore.add(obj);
                        saveProperties(selModel);
                        dropHandlers.cancelDrop();
                    }
                }
            },
            listeners: {
                itemclick: function () {
                    nodeInfoMenu.hide();
                },
                itemcontextmenu: function (_this, record, item, index, e) {
                    e.preventDefault();
                    nodeInfoMenu.showAt(e.getPoint());
                },
                containerclick: function () {
                    nodeInfoMenu.hide();
                }
            },
            columns: [{
                text: '数据类型',
                dataIndex: 'dataType',
                flex: 0.5,
                renderer: function (data) {
                    for (var i = 0; i < metaDataTypes.length; i++) {
                        if (metaDataTypes[i].value == data) {
                            return metaDataTypes[i].text;
                        }
                    }
                    return '';
                }
            }, {
                text: '数据名',
                dataIndex: 'dataName',
                flex: 1
            }, {
                text: '附加信息',
                flex: 1,
                renderer: function (data, m, record) {
                    var dataInfo1 = record.get('dataInfo1');
                    var dataType = record.get('dataType');
                    //第一个附加信息
                    var str1 = '';
                    if (dataInfo1) {
                        if (dataType == METADATA_TYPE.ITEM) {//指标类型,则为口径
                            var obj = getDataInfo(dataType, dataInfo1, 1);
                            if (obj) {
                                str1 = '口径:' + obj.name;
                            }
                        } else if (dataType == METADATA_TYPE.RESEARCH_OBJ) {//调查对象类型,则为调查对象分类
                            str1 = '统计类型:' + SUROBJ_TYPE.getCH(parseInt(dataInfo1));
                        }
                    }
                    //第二个附加信息
                    var dataInfo2 = record.get('dataInfo2');
                    var str2 = '';
                    if (dataInfo2) {
                        if (dataType == METADATA_TYPE.ITEM) {//指标类型,则为口径
                            var obj = getDataInfo(dataType, dataInfo2, 2);
                            if (obj) {
                                str2 = '部门:' + obj.depName;
                            }
                        }
                    }
                    return str1 + ' ' + str2;
                }
            }],
            tbar: ['<b>节点信息</b>', '->', {
                xtype: 'button',
                text: '隐藏基本信息',
                handler: function (btn) {
                    var panel = Ext.getCmp('baseInfo');
                    if (panel.isHidden()) {//隐藏时显示出来
                        panel.show();
                        //设置按钮的文本信息
                        btn.setConfig('text', '隐藏基本信息');
                    } else {
                        panel.hide();
                        //设置按钮的文本信息
                        btn.setConfig('text', '显示基本信息');
                    }
                }
            }]
        });
        //单位下拉store
        var selUnitStore = new Ext.data.Store({
            fields: ['id', 'name'],
            proxy: {
                type: 'ajax',
                url: contextPath + '/support/unit/unitManager/findUnitByName'
            },
            autoLoad: true
        });
        //单元格选择单位区域
        var tdSelPanel = new Ext.panel.Panel({
            height: '100%',
            flex: 1,
            hidden: true,
            layout: 'column',
            items: [{//单位
                xtype: 'combobox',
                fieldLabel: '选择单位',
                labelWidth: 70,
                labelAlign: 'right',
                store: selUnitStore,
                displayField: 'name',
                valueField: 'id',
                columnWidth: 0.9,
                margin: '20 0 0 0',
                listeners: {
                    select: function (combo, record) {
                        //设置数据格式
                        var formatField = tdSelPanel.down('[name=dataFormat]');
                        var dataFormat = record.get('dataFormat');
                        formatField.setValue(dataFormat);
                        //修改表格
                        var unitId = record.get('id');
                        var unitName = record.get('name');
                        var td = $('table.esi td.active');
                        td.attr('esi-data-unit', unitId);
                        td.attr('esi-data-unitname', unitName);
                        td.attr('esi-data-format', dataFormat);
                        td.html(unitName + '<br>' + dataFormat);
                        //修改选择器
                        var tdData = Ext.decode(td.attr('esi-data-value'));
                        tdUnitSelector[getTdSelectorKey(tdData)] = {
                            unitId: unitId,
                            unitName: unitName,
                            dataFormat: dataFormat
                        };
                    }
                }
            }, {//数据格式
                xtype: 'textfield',
                name: 'dataFormat',
                fieldLabel: '数据格式',
                labelWidth: 70,
                labelAlign: 'right',
                columnWidth: 0.9,
                margin: '20 0 0 0',
                listeners: {
                    change: function (field, n) {
                        var td = $('table.esi td.active');
                        td.attr('esi-data-format', n);
                        var unitName = td.attr('esi-data-unitname') || '';
                        td.html(unitName + '<br>' + n);
                        //修改选择器
                        var tdData = Ext.decode(td.attr('esi-data-value'));
                        tdUnitSelector[getTdSelectorKey(tdData)] = {
                            unitId: td.attr('esi-data-unit'),
                            unitName: unitName,
                            dataFormat: n
                        };
                    }
                }
            }],
            tbar: ['<b>单元格信息</b>', '->', {
                xtype: 'button',
                text: '隐藏',
                handler: function () {
                    tdSelPanel.hide();
                }
            }]
        });
        //信息区域
        var nodeTdInfoPanel = new Ext.panel.Panel({
            height: 170,
            border: 0,
            width: '100%',
            layout: 'hbox',
            items: [tablePanelNorth, tdSelPanel]
        });
        //表格区域
        var tableStr = new Ext.panel.Panel({
            border: 0,
            scrollable: true,
            id: 'tableRenderPanel',
            html: style.designStyle,
            listeners: {
                render: initTdClickHandler
            }
        });
        var tablePanelCenter = Ext.create('Ext.panel.Panel', {
            flex: 1,
            width: '100%',
            layout: 'fit',
            tbar: ['<b>表样</b>', '-', {
                xtype: 'button',
                text: '主宾栏置换',
                handler: function () {
                    //获取主宾栏的根节点
                    var mainRoot = mainBarTree.getRootNode();
                    var guestRoot = guestBarTree.getRootNode();
                    //交换
                    mainBarTree.setRootNode(guestRoot);
                    guestBarTree.setRootNode(mainRoot);
                }
            }, '->', {
                xtype: 'button',
                text: '生成表样',
                handler: function () {
                    genTableStr(mainBarTree, guestBarTree, function (data) {
                        if (data.success) {
                            //加载表样
                            tableStr.update(data.datas, false);
                            initTdClickHandler();
//                            hzc
                            htmlTable = data.datas;
                            style.designStyle = htmlTable;
                        } else {
                            Ext.Msg.alert('提示', data.msg);
                        }
                    });
                }
            }, '-', {
                xtype: 'button',
                text: '保存',
                iconCls: 'Pagesave',
                handler: function () {
                    saveBarInfos(mainBarTree, guestBarTree, function (data) {
                        if (data.success) {
                            Ext.Msg.alert('提示', '保存成功');
                            //加载表样
                            tableStr.update(data.datas, false);
                            initTdClickHandler();
//                            hzc
                            htmlTable = data.datas;
                            style.designStyle = htmlTable;
                        } else {
                            Ext.Msg.alert('提示', data.msg);
                        }
                    });
                }
            }],
            items: [tableStr]
        });
        //tab面板
        var tabPanel = new Ext.tab.Panel({
            width: '100%',
            height: '100%',
            shadow: true,
            items: [{//内容面板
                xtype: 'resizablepanel',
                layout: 'border',
                title: '内容',
                frame: false,
                width: '100%',
                height: '100%',
                border: false,
                items: [{//基本信息面板
                    xtype: 'panel',
                    region: 'north',
                    border: 0,
                    items: [{
                        xtype: 'panel',
                        layout: 'column',
                        id: 'baseInfo',
                        items: [{
                            xtype: 'textfield',
                            fieldLabel: '报表名称',
                            labelWidth: 70,
                            labelAlign: 'right',
                            width: '40%',
                            margin: '8 0',
                            allowBlank: false,
                            value: style.reportTemplate.name,
                            editable: false
                        }, {
                            xtype: 'fieldcontainer',
                            fieldLabel: '时间频度',
                            labelWidth: 70,
                            labelAlign: 'right',
                            defaultType: 'radiofield',
                            defaults: {
                                flex: 1
                            },
                            layout: 'hbox',
                            width: '40%',
                            margin: '8 0',
                            items: [{
                                boxLabel: '年',
                                name: 'frequency',
                                inputValue: '1',
                                margin: '0 10',
                                value: period == 1,
                                disabled: period != 1
                            }, {
                                boxLabel: '半年',
                                name: 'frequency',
                                inputValue: '2',
                                margin: '0 10',
                                value: period == 2,
                                disabled: period != 2
                            }, {
                                boxLabel: '季',
                                name: 'frequency',
                                inputValue: '3',
                                margin: '0 10',
                                value: period == 3,
                                disabled: period != 3
                            }, {
                                boxLabel: '月',
                                name: 'frequency',
                                inputValue: '4',
                                margin: '0 10',
                                value: period == 4,
                                disabled: period != 4
                            }]
                        }, {
                            xtype: 'displayfield',
                            width: '20%',
                            fieldLabel: '表样类型',
                            value: '<b>' + styleTypeStr + '</b>',
                            margin: '8 0'
                        }]
                    }]
                }, {//操作区域面板
                    xtype: 'panel',
                    width: '100%',
                    height: '100%',
                    border: false,
                    region: 'center',
                    layout: 'border',
                    items: [{//树结构区
                        xtype: 'panel',
                        layout: 'border',
                        width: '30%',
                        height: '100%',
                        border: false,
                        region: 'west',
                        items: [{//元数据选择区
                            xtype: 'panel',
                            layout: 'border',
                            width: '50%',
                            height: '100%',
                            border: false,
                            region: 'west',
                            items: [itemNorthTree, itemSouthTree]
                        }, {//主宾栏操作区
                            xtype: 'panel',
                            width: '50%',
                            height: '100%',
                            border: false,
                            region: 'center',
                            layout: 'border',
                            items: [mainBarTree, guestBarTree]
                        }]
                    }, {//报表编辑区
                        xtype: 'panel',
                        border: false,
                        region: 'center',
                        width: 'auto',
                        height: '100%',
                        layout: 'vbox',
                        items: [nodeTdInfoPanel, tablePanelCenter]
                    }]
                }]
            }, {
                title: '表样设计',
                items: [
                    {
                        xtype: 'panel',
                        html: '<div id="container" style="width:100%;"></div>',
                        tbar: [
                            {
                                xtype: 'button',
                                text: '保存',
                                handler: function () {
                                    MINE.saveContent(style.id);
                                }
                            }, '-', {
                                xtype: 'button',
                                text: '重置',
                                handler: function () {
                                    MINE.resetStyle(style);
                                }
                            }
                        ],
                        listeners: {
                            afterrender: function () {
                                MINE.reportStyle(style);
                            }
                        }
                    }
                ]
            }],
            listeners: {
                tabchange: function (tabPanel, newCard, oldCard, eOpts) {
                    var title = newCard.getTitle();
                    if (title == '表样设计') {
                        if (!MINE.getContent())
                            MINE.setContent(htmlTable);
                    }
                }
            }
        });
        //容器
        indexPanel = new Ext.container.Viewport({
            layout: 'fit',
            items: [tabPanel]
        });

        /**
         * 右键点击节点事件
         */
        function itemcontextHandler(_this, record, item, index, e) {
            //阻止浏览器默认行为
            e.preventDefault();
            //菜单
            if (barMenu) {
                barMenu.hide();
            }
            var menu = barMenu = new Ext.menu.Menu({
                renderTo: Ext.getBody(),
                items: [{
                    text: '添加下级',
                    iconCls: 'Add',
                    handler: function () {
                        //未展开时添加会有异常,先展开
                        if (record.isLeaf()) {
                            record.set('leaf', false);
                            record.set('expanded', true);
                            record.set('loaded', true);
                        }
                        Ext.addNodeInfoWin.init(null, function (obj) {
                            obj.leaf = true;
                            obj.dataType = METADATA_TYPE.SYSTEM_DESCRIBE_TYPE;//描述字段
                            var node = getNodeByObj(obj, record);
                            record.appendChild(node);
                        });
                    }
                }, {
                    text: '添加同级',
                    iconCls: 'Controladdblue',
                    disabled: !record.get('id'),
                    handler: function () {
                        var rec = record.parentNode;
                        Ext.addNodeInfoWin.init(null, function (obj) {
                            obj.leaf = true;
                            obj.dataType = METADATA_TYPE.SYSTEM_DESCRIBE_TYPE;//描述字段
                            var node = getNodeByObj(obj, rec);
                            rec.appendChild(node);
                        });
                    }
                }, '-', {
                    text: '修改节点',
                    iconCls: 'Pageedit',
                    disabled: !record.get('id'),
                    handler: function () {
                        Ext.addNodeInfoWin.init(record);
                    }
                }, {
                    text: '删除节点',
                    iconCls: 'Delete',
                    disabled: !record.get('id'),
                    handler: function () {
                        record.remove();
                        nodeInfoStore.removeAll();
                    }
                }]
            });
            menu.showAt(e.getPoint());
        }

        /**
         * 拖拽松开事件
         */
        function dropHandler(data, overModel, dropHandlers) {
            //叶子节点则修改为非叶子
            if (overModel.isLeaf()) {
                overModel.set('leaf', false);
                overModel.set('expanded', true);
                overModel.set('loaded', true);
            }
            var pnode = data.records[0].parentNode.copy();
            var node = data.records[0].copy();
            if (data.view.name == 'barTreeView') {//从主栏或者宾栏拖拽过来的
                if (data.copy) {//复制节点
                    //取消拖拽
                    dropHandlers.cancelDrop();
                    infoDropCopyHandler(node, overModel, function (res) {
                        copyNode(res, overModel, node, pnode, data);
                    });
                } else {
                    appendAll(node, pnode);
                }
                return;
            }
            switch (node.get('dataType')) {
                case METADATA_TYPE.ITEM_GROUP://指标体系
                case METADATA_TYPE.ITEM_MENU://指标分组目录
                case METADATA_TYPE.RESEARCH_OBJ_GROUP://调查对象分组
                    groupDropHandler(node, pnode, overModel);
                    break;
                case METADATA_TYPE.ITEM://指标
                    node.set('dataInfo2', style.reportTemplate.department.id);
                    itemHandler(node, overModel);
                    break;
                case METADATA_TYPE.TIME_FRAME://时间框架
                case METADATA_TYPE.RESEARCH_OBJ://调查对象
                    timeFrameHandler(node, overModel);
                    break;
                default://默认直接拖拽
                    return;
            }
            //取消拖拽
            dropHandlers.cancelDrop();
        }

        /**校验另一棵树*/
        function checkAnotherTreeValid(thisNode, anotherTree) {
            var dataType = thisNode.get('dataType');
            //对指标分组目录类型不做校验,主宾蓝之间不做校验
            if (dataType == METADATA_TYPE.ITEM_MENU || dataType == METADATA_TYPE.SYSTEM_DESCRIBE_TYPE) {
                return true;
            }
            //校验另外一棵树的节点
            var props = [];
            var leaves = [];
            getTreeNodes(anotherTree.getRootNode(), null, leaves);
            if (leaves.length) {
                for (var i = 0; i < leaves.length; i++) {
                    props = props.concat(leaves[i].properties);
                }
                return isPropsValid(dataType, props);
            }
            return true;
        }

        /**校验拖拽节点*/
        function checkTreeNodeValid(thisNode, overModel) {
            var dataType = thisNode.get('dataType');
            //对指标分组目录类型不做校验,主宾蓝之间不做校验
            if (dataType == METADATA_TYPE.ITEM_MENU || dataType == METADATA_TYPE.SYSTEM_DESCRIBE_TYPE) {
                return true;
            }
            var props = [];
            while (overModel && !overModel.isRoot()) {
                var nodeProps = overModel.get('properties');
                if (nodeProps && nodeProps.length) {
                    props = props.concat(nodeProps);
                }
                overModel = overModel.parentNode;
            }
            //校验当前拖动节点
            if (props.length) {//校验
                return isPropsValid(dataType, props);
            }
            return true;
        }

        /**校验属性信息*/
        function isPropsValid(dataType, props) {
            var type = ',' + dataType + ',';
            if (dataType == METADATA_TYPE.ITEM_GROUP) {
                type += METADATA_TYPE.ITEM + ',';
            } else if (dataType == METADATA_TYPE.RESEARCH_OBJ_GROUP) {
                type += METADATA_TYPE.RESEARCH_OBJ + ',';
            }
            for (var i = 0; i < props.length; i++) {
                if (type.indexOf(',' + props[i].dataType + ',') >= 0) {
                    return false;
                }
            }
            return true;
        }


        /**
         * 拖拽分组触发的事件
         * @param node 拖动的节点
         * @param pnode 拖动节点的父节点
         * @param overModel 目标节点
         */
        function groupDropHandler(node, pnode, overModel) {
            Ext.dragGroupWin.init(node, overModel, function (data) {
                var levelType = data.levelType;
                var level = data.level;
                if (levelType == 1) {//添加同级 以父节点的下级处理
                    overModel = overModel.parentNode;
                    levelType = 2;
                }
                if (levelType == 2) {//添加下级
                    //校验
                    if (!checkTreeNodeValid(node, overModel)) {
                        Ext.Msg.alert('提示', '类型重复!');
                        return;
                    }
                    //校验成功,继续
                    node.set('dataName', data.dataName);
                    node.set('text', data.dataName);
                    if (level == 1) {//拖动下级
                        //将子集添加
                        appendAll(node, overModel);
                    } else if (level == 2) {//拖动本级
                        node.set('children', null);
                        overModel.appendChild(getNodeByObj(node.data, pnode));
                    } else if (level == 3) {//拖动全部
                        node = getNodeByObj(node.data, pnode);
                        appendAll(node);
                        overModel.appendChild(node);
                    }
                } else {//加为属性
                    //校验
                    if (!checkTreeNodeValid(node, overModel)) {
                        Ext.Msg.alert('提示', '类型重复!');
                        return;
                    }
                    //校验成功,继续
                    node.set('isProperty', 1);
                    if (level == 2) {//拖动本级
                        node.set('children', null);
                        var properties = overModel.get('properties');
                        if (!properties) {
                            properties = [];
                        }
                        properties.push(getSimpleObj(node.data));
                        overModel.set('properties', properties);
                        overModel.set('dataName', data.dataName);
                        overModel.set('text', data.dataName);
                    } else if (level == 3) {//全部
                        //将子集添加
                        appendAll(node, overModel);
                        //设置属性
                        node.set('children', null);
                        var properties = overModel.get('properties');
                        if (!properties) {
                            properties = [];
                        }
                        properties.push(getSimpleObj(node.data));
                        overModel.set('properties', properties);
                    }
                }
            });
        }

        /**
         * 获取节点基本信息
         */
        function getNodeInfo(node) {
            return node.get('properties');
        }

        /**
         * 将sourceNode的所有子节点添加給node,sourceNode和node可为同一节点
         * @param sourceNode 提供子节点的节点
         * @param node 目标节点
         */
        function appendAll(sourceNode, node) {
            if (!node) {
                node = sourceNode;
            }
            var children = sourceNode.get('children');
            if (children && children.length) {
                for (var i = 0; i < children.length; i++) {
                    var n = children[i];
                    var nNode = getNodeByObj(n, sourceNode);
                    if (n.children) {
                        appendAll(nNode);
                    }
                    node.appendChild(nNode);
                }
            }
        }

        /**提取节点信息,返回node*/
        function getNodeByObj(obj, pnode) {
            var properties = [];
//            如果节点有属性，则属性保留
            if (obj.properties) {
                properties = obj.properties;
            }
//            如果节点为新建主宾栏节点，则复制属性为节点信息
            if (obj.dataType != METADATA_TYPE.SYSTEM_DESCRIBE_TYPE) {
                properties.push(getSimpleObj(obj));
            }
            var nodeInfo = {
                dataName: obj.dataName,
                text:obj.dataName,
                dataType: METADATA_TYPE.SYSTEM_DESCRIBE_TYPE,
                isRealNode: obj.isRealNode || 0,
                isProperty: 1,
                children: (obj.children ? obj.children : []),
                leaf: obj.leaf,
                expanded: obj.expanded,
                properties: properties
            };
            return pnode.createNode(nodeInfo);
        }

        /**获取简版obj*/
        function getSimpleObj(obj) {
            var dataInfo2 = obj.dataInfo2;
            if (rptDesignType == RPT_DESIGN_TYPE.SYNTHESIS && obj.dataType == METADATA_TYPE.ITEM) {//综合表 指标类型,则部门为报表部门,否则可自选
                dataInfo2 = style.reportTemplate.department.id;
            }
            return {
                dataName: obj.dataName,
                text:obj.dataName,
                dataValue: obj.dataValue,
                dataType: obj.dataType,
                dataInfo1: obj.dataInfo1,
                dataInfo2: dataInfo2,
                isRealNode: obj.isRealNode || 0,
                isProperty: 1,
                children: (obj.children ? obj.children : [])
            };
        }

        /**
         * 拖拽时间框架
         * @param record 拖动的节点
         * @param overModel 停留的节点
         */
        function timeFrameHandler(record, overModel) {
            showAddTimeFrameWindow(record, overModel.get('root'), function (target, recordNew, formData) {
                if (target == 'same') {
                    var parentNode = overModel.parentNode;
                    //校验
                    if (!checkTreeNodeValid(record, parentNode)) {
                        Ext.Msg.alert('提示', '类型重复!');
                        return;
                    }
                    //校验成功,继续
                    record.set('dataType', METADATA_TYPE.SYSTEM_DESCRIBE_TYPE);
                    var newNode = getNodeByObj(recordNew.data, parentNode);
                    parentNode.appendChild(newNode);
                } else if (target == 'low') {
                    //校验
                    if (!checkTreeNodeValid(record, overModel)) {
                        Ext.Msg.alert('提示', '类型重复!');
                        return;
                    }
                    //校验成功,继续
                    record.set('dataType', METADATA_TYPE.SYSTEM_DESCRIBE_TYPE);
                    var newNode = getNodeByObj(recordNew.data, overModel);
                    overModel.appendChild(newNode);
                } else if (target == 'property') {
                    //校验
                    if (!checkTreeNodeValid(record, overModel)) {
                        Ext.Msg.alert('提示', '类型重复!');
                        return;
                    }
                    //校验成功,继续
                    formData.isProperty = 1;
                    var properties = overModel.get('properties');
                    if (!properties) {
                        properties = [];
                    }
                    properties.push(getSimpleObj(formData));
                    overModel.set('properties', properties);
                }
            });
        }

        /**保存属性信息*/
        function saveProperties(node) {
            //获取所有
            var proInfos = [];
            var count = nodeInfoStore.getCount();
            if (count) {
                for (var i = 0; i < count; i++) {
                    proInfos.push(getSimpleObj(nodeInfoStore.getAt(i).data));
                }
            }
            //设置属性节点
            node.set('properties', proInfos);
        }

        /**
         * 拖拽指标
         * @param record 拖拽的节点
         * @param overModel 停留的节点
         */
        function itemHandler(record, overModel) {
            showAddItemWindow(record, overModel.get('root'), function (target, recordNew, formData) {
                if (target == 'same') {
                    var pnode = overModel.parentNode;
                    //校验
                    if (!checkTreeNodeValid(record, pnode)) {
                        Ext.Msg.alert('提示', '类型重复!');
                        return;
                    }
                    //校验成功,继续
                    record.set('dataType', METADATA_TYPE.SYSTEM_DESCRIBE_TYPE);
                    var nn = getNodeByObj(recordNew.data, pnode);
                    pnode.appendChild(nn);
                } else if (target == 'low') {
                    //校验
                    if (!checkTreeNodeValid(record, overModel)) {
                        Ext.Msg.alert('提示', '类型重复!');
                        return;
                    }
                    //校验成功,继续
                    record.set('dataType', METADATA_TYPE.SYSTEM_DESCRIBE_TYPE);
                    var nn = getNodeByObj(recordNew.data, overModel);
                    overModel.appendChild(nn);
                } else if (target == 'property') {
                    //校验
                    if (!checkTreeNodeValid(record, overModel)) {
                        Ext.Msg.alert('提示', '类型重复!');
                        return;
                    }
                    //校验成功,继续
                    formData.isProperty = 1;
                    var properties = overModel.get('properties');
                    if (!properties) {
                        properties = [];
                    }
                    properties.push(getSimpleObj(formData));
                    overModel.set('properties', properties);
                }
            });
        }

        /**
         * 获取附加信息
         */
        function getDataInfo(dataType, dataInfo, dataInfoType) {
            var obj;
            Ext.Ajax.request({
                url: contextPath + '/support/regime/report/designReport/getDataInfo',
                async: false,
                params: {
                    dataType: dataType,
                    dataInfo: dataInfo,
                    dataInfoType: dataInfoType
                },
                success: function (response) {
                    obj = Ext.decode(response.responseText);
                }
            });
            return obj;
        }

        /**
         * 主宾栏复制节点
         * @param data
         * @param overModel
         * @param node
         * @param pnode
         */
        function copyNode(rs, overModel, node, pnode, data) {
            var levelType = rs.levelType;
            var level = rs.level;
            if (levelType == 1) {//添加同级 以父节点的下级处理
                overModel = overModel.isRoot() ? overModel : overModel.parentNode;
                levelType = 2;
            }
            if (levelType == 2) {//添加下级
                node.set('dataName', rs.dataName);
                node.set('text', rs.dataName);
                if (level == 1) {//拖动下级
                    //将子集添加
                    var rc = data.records[0].childNodes;
                    for (var n = 0; n < rc.length; n++) {
                        copy(rc[n], overModel);
                    }
                } else if (level == 2) {//拖动本级
                    node.set('children', null);
                    overModel.appendChild(getNodeByObj(node.data, pnode));
                } else if (level == 3) {//拖动全部
                    var rc = data.records[0];
                    copy(rc, overModel);
                }
            }
        }

        /**
         * 复制节点
         * 根据节点的childNodes属性复制,包括本节点
         * @param rc
         * @param overModel
         */
        function copy(rc, overModel) {
            var conf = {
                dataName: rc.get('dataName'),
                text:rc.get('dataName'),
                dataType: rc.get('dataType'),
                dataValue: rc.get('dataValue'),
                dataInfo1: rc.get('dataInfo1'),
                dataInfo2: rc.get('dataInfo2'),
                isProperty: rc.get('isProperty'),
                isRealNode: rc.get('isRealNode'),
                children: rc.get('children'),
                properties: rc.get('properties'),
                expanded: rc.get('expanded'),
                leaf: rc.get('leaf')
            };
            var n = overModel.createNode(conf);
            var cn = rc.childNodes;
            if (cn && cn.length > 0) {
                for (var i = 0; i < cn.length; i++) {
                    copy(cn[i], n);
                }
            }
            overModel.appendChild(n);
        }

        /**
         * 注册表格的单元格点击事件
         */
        function initTdClickHandler() {
            //隐藏thead,tfoot
            $('table.esi thead,table.esi tfoot').hide();
            //隐藏单位选择信息框
            tdSelPanel.hide();
            tdUnitSelector = {};
            //初始化单位
            $('table.esi td[esi-type=data]').each(function (index, ele) {
                var td = $(this);
                var unitName = td.attr('esi-data-unitname');
                var unit = td.attr('esi-data-unit');
                var dataformat = td.attr('esi-data-format');
                if (unit && dataformat) {
                    td.html(unitName + '<br>' + dataformat);
                    //添加进单位选择器
                    var tdData = Ext.decode(td.attr('esi-data-value'));
                    tdUnitSelector[getTdSelectorKey(tdData)] = {
                        unitId: unit,
                        unitName: unitName,
                        dataFormat: dataformat
                    };
                }
            });
            //初始化点击时间
            $('table.esi td[esi-type=data]').click(function () {
                var td = $(this);
                //设置状态
                $('table.esi td').removeClass('active');
                td.addClass('active');
                //数据
                var obj = td.attr('esi-data-value');
                var unit = td.attr('esi-data-unit');
                if (unit) {
                    tdSelPanel.down('combobox').setValue(unit);
                } else {
                    tdSelPanel.down('combobox').setValue(null);
                }
                var dataFormat = td.attr('esi-data-format');
                if (dataFormat) {
                    tdSelPanel.down('[name=dataFormat]').setValue(dataFormat);
                } else {
                    tdSelPanel.down('[name=dataFormat]').setValue('');
                }
                tdSelPanel.show();
            });
        }

        //获取选择器的键
        function getTdSelectorKey(tdData) {
            var itemId = tdData.item;
            var timeframeId = tdData.timeframe;
            var itemdictId = tdData.itemdict || '0';
            var surobjId = tdData.surobj || 0;
            return itemId + ',' + timeframeId + ',[' + itemdictId + '],' + surobjId;
        }
    });
</script>
<script src="<%=contextPath%>/City/support/regime/report/design/addNodeInfoWin.js"></script>
<script src="<%=contextPath%>/City/common/queryTreeByLocal.js"></script>
<script src="<%=contextPath%>/City/support/regime/report/design/addItemWin.js"></script>
<script src="<%=contextPath%>/City/support/regime/report/design/dragGroupWin.js"></script>
<script src="<%=contextPath%>/City/support/regime/report/design/addTimeFrameWin.js"></script>
<script src="<%=contextPath%>/City/support/regime/report/design/copyNodeWin.js"></script>
<script src="<%=contextPath%>/City/support/regime/report/design/genTableStr.js"></script>
<script src="<%=contextPath%>/Plugins/ueditor/ueditor.config.js"></script>
<script src="<%=contextPath%>/Plugins/ueditor/ueditor.all.min.js"></script>
<script type="text/javascript" charset="utf-8" src="<%=contextPath%>/Plugins/ueditor/lang/zh-cn/zh-cn.js"></script>
<script src="<%=contextPath%>/City/support/regime/report/ueditor.config.js"></script>
<script src="<%=contextPath%>/City/support/regime/report/reportStyle.js"></script>
</body>
</html>
