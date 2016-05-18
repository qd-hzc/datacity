<%--
  Created by IntelliJ IDEA.
  User: wxl
  Date: 2016/4/16
  Time: 13:43
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="en">
<head>
    <title>推送管理</title>
    <meta charset="UTF-8"/>
    <jsp:include page="../../common/appDataDictImp.jsp"></jsp:include>
    <style>
        .x-tree-icon {
            background-size: 16px;
        }
    </style>
    <script>
        var APP_DATADICT_PATH = '<%=request.getContextPath()%>/app/dataDict';
        var userId = '<%=request.getAttribute("loginName")%>'
        Ext.onReady(function () {
            var dPushParams = {
                menuId: '',
                name: '',
                status: ''
            };
            var ppushParams = {
                receivers: '',
                name: ''

            }
//=========================================待推送begin===========================================================
            var dPushCenterGridStore = Ext.create('Ext.data.Store', {
                fields: ['name', 'status'],
                proxy: {
                    type: 'ajax',
                    url: '<%=request.getContextPath()%>/app/favorite/push/queryDownDictMenus'
                },
                autoLoad: false
            });

            var dPushCenterGrid = Ext.create('Ext.grid.Panel', {
                region: 'center',
                store: dPushCenterGridStore,
                selModel: 'checkboxmodel',
                columns: [{
                    text: '名称',
                    dataIndex: 'name',
                    flex: 1
                }, {
                    text: '状态',
                    dataIndex: 'status',
                    flex: 1,
                    renderer: function (value) {
                        if (value == 1) {
                            return '显示';
                        }
                        return '隐藏';
                    }
                }, {
                    text: '操作',
                    renderer: function () {
                        return '<a style="color:#0000FF">预览</a>';
                    }
                }],
                height: '100%',
                width: '50%',
                tbar: [{
                    xtype: 'triggertext',
                    handler: function (_this, n) {
                        dPushParams.name = n;
                        dPushCenterGridStore.reload({params: dPushParams});
                    }
                }, {
                    xtype: 'radiogroup',
                    fieldLabel: '状态',
                    labelWidth: 40,
                    labelAlign: 'right',
                    width: 200,
                    items: [
                        {boxLabel: '全部', name: 'status', inputValue: '', checked: true},
                        {boxLabel: '显示', name: 'status', inputValue: '1'},
                        {boxLabel: '隐藏', name: 'status', inputValue: '0'}
                    ],
                    listeners: {
                        change: function (_this, n) {
                            dPushParams.status = n;
                            dPushCenterGridStore.reload({params: dPushParams});
                        }
                    }
                }, '->', {
                    text: '推送',
                    iconCls: 'Basketgo',
                    handler: function () {
                        var menuInfo = dPushCenterGrid.getSelectionModel().getSelection();
                        pushMenus(menuInfo);
                    }
                }],
                listeners: {
                    cellclick: function (_this, td, cellIndex, record) {
                        if (cellIndex == 3) {
                            previewContent(record);
                        }
                    }
                }
            });
            //内容表格
            //字典表格
            var gridParams = {
                includeDownLevel: false,
                name: '',
                menuId: 0,
                status: ''
            };
            var dictStore = new Ext.data.Store({
                fields: ['id', 'menuId', 'dataType', 'dataName', 'dataValue', 'groupName', 'displayType', 'sortIndex', 'status'],
                groupField: 'groupName',
                proxy: {
                    type: 'ajax',
                    api: {
                        read: APP_DATADICT_PATH + '/queryDicts',
                        update: APP_DATADICT_PATH + '/saveDicts',
                        destroy: APP_DATADICT_PATH + '/deleteDicts'
                    }
                },
                autoLoad: false
            });
            var dictMenu = new Ext.menu.Menu({
                items: [{
                    text: '修改',
                    iconCls: 'Pageedit',
                    handler: function () {
                        var sel = dictGrid.getSelectionModel().getSelection();
                        if (sel.length == 1) {
                            var record = sel[0];
                            Ext.dataDict.EditDataDictInfoWin.init(gridParams.menuId, record, function (data) {
                                record.set(data, {dirty: false});
                            });
                        } else {
                            Ext.Msg.alert('提示', '请选中一条来修改!');
                        }
                    }
                }, {
                    text: '删除',
                    iconCls: 'Delete',
                    handler: function () {
                        Ext.Msg.confirm('提示', '确定删除?', function (btn) {
                            if (btn == 'yes') {
                                var sel = dictGrid.getSelectionModel().getSelection();
                                dictStore.remove(sel);
                                dictStore.sync();
                            }
                        });
                    }
                }]
            });
            var dictGrid = new Ext.grid.Panel({
                hidden: true,
                store: dictStore,
                region: 'center',
                width: '50%',
                height: '100%',
                selModel: 'checkboxmodel',
                features: [{
                    ftype: 'grouping',
                    groupHeaderTpl: '分组:{name} ({rows.length}项)'
                }],
                columns: [{
                    text: '数据名',
                    dataIndex: 'dataName',
                    flex: 2
                }, {
                    text: '数据类型',
                    dataIndex: 'dataType',
                    flex: 1,
                    renderer: function (value) {
                        return DATA_DICT_TYPE.getStr(value);
                    }
                }, {
                    text: '显示类型',
                    dataIndex: 'displayType',
                    flex: 1,
                    renderer: function (value) {
                        return DATA_DICT_DISPLAY_TYPE.getStr(value);
                    }
                }, {
                    text: '状态',
                    dataIndex: 'status',
                    flex: 1,
                    renderer: function (value) {
                        if (value) {
                            return '显示';
                        }
                        return '隐藏';
                    }
                }],
                viewConfig: {
                    plugins: {
                        ptype: 'gridviewdragdrop'
                    },
                    listeners: {
                        beforedrop: function () {
                            var sel = dPushLeftTree.getSelectionModel().getSelection();
                            if (sel.length) {
                                if (sel[0].get('type') == 2) {
                                    return true;
                                }
                            }
                            Ext.Msg.alert('提示', '移动数据目录在此处不可排序!');
                            return false;
                        },
                        drop: function () {
                            //按照顺序重排
                            var count = dictStore.getCount();
                            if (count) {
                                for (var i = 0; i < count; i++) {
                                    var record = dictStore.getAt(i);
                                    record.set('sortIndex', i);
                                }
                                dictStore.sync();
                            }
                        }
                    }
                },
                tbar: [{
                    xtype: 'triggertext',
                    width: 110,
                    emptyText: '请输入名称检索',
                    handler: function (_this, n) {
                        gridParams.name = n;
                        dictStore.reload({params: gridParams});
                    }
                }, '->', {
                    text: '预览',
                    iconCls: 'Zoom',
                    handler: function () {
                        var sel = dPushLeftTree.getSelectionModel().getSelection();
                        if (sel.length) {
                            var record = sel[0];
                            if (dictStore.getCount()) {
                                previewContent(record);
                            } else {
                                Ext.Msg.alert('提示', '无内容不可预览o(╯□╰)o');
                            }
                        }
                    }
                }, {
                    text: '推送',
                    iconCls: 'Basketgo',
                    handler: function () {
                        var sel = dPushLeftTree.getSelectionModel().getSelection();
                        pushMenus(sel);
                    }
                }, '-', {
                    text: '添加',
                    iconCls: 'Add',
                    handler: function () {
                        var sel = dPushLeftTree.getSelectionModel().getSelection();
                        if (sel.length) {
                            if (sel[0].get('type') == 1) {
                                Ext.Msg.alert('提示', '移动数据目录不可在此处添加内容!');
                                return false;
                            }
                        }
                        if (gridParams.menuId && gridParams.menuId > 0) {
                            Ext.dataDict.EditDataDictInfoWin.init(gridParams.menuId, null, function (data) {
                                dictStore.insert(0, data);
                            });
                        } else {
                            Ext.Msg.alert('提示', '请选中一个目录来添加!');
                        }
                    }
                }],
                listeners: {
                    rowcontextmenu: function (_this, record, tr, rowIndex, e) {
                        e.preventDefault();
                        var sel = dPushLeftTree.getSelectionModel().getSelection();
                        if (sel.length) {
                            if (sel[0].get('type') == 2) {
                                dictMenu.showAt(e.getXY());
                            }
                        }
                    },
                    rowclick: function () {
                        dictMenu.hide();
                    },
                    containerclick: function () {
                        dictMenu.hide();
                    },
                    containercontextmenu: function (_this, e) {
                        e.preventDefault();
                        dictMenu.hide();
                    }
                }
            });
            //目录树
            createModel('AppDataDictMenu', function () {
                Ext.define('AppDataDictMenu', {
                    extend: 'Ext.data.TreeModel',
                    fields: [
                        {name: 'id', type: 'int'},
                        {name: 'name', type: 'string'},
                        {name: 'parentId', type: 'int'},
                        {name: 'roleId', type: 'int'},
                        {name: 'status', type: 'int'},
                        {name: 'menuIcon'},
                        {name: 'menuBg'},
                        {name: 'icon', type: 'string'},
                        {name: 'sortIndex', type: 'int'},
                        {name: 'type', type: 'int'}
                    ]
                });
            });
            var dPushLeftStore = Ext.create('Ext.data.TreeStore', {
                model: 'AppDataDictMenu',
                parentIdProperty: 'parentId',
                root: {
                    id: 0,
                    name: '根',
                    type: 2
                },
                proxy: {
                    type: 'ajax',
                    api: {
                        read: APP_DATADICT_PATH + '/queryDictMenus',
                        update: APP_DATADICT_PATH + '/saveDictMenus',
                        destroy: APP_DATADICT_PATH + '/deleteDictMenus'
                    },
                    extraParams: {
                        showPushTree: true
                    }
                },
                autoLoad: true
            });
            var menu;//目录
            var dPushLeftTree = Ext.create('Ext.tree.Panel', {
                rootVisible: false,
                store: dPushLeftStore,
                displayField: 'name',
                region: 'west',
                height: '100%',
                width: '20%',
                tbar: [{
                    xtype: 'triggertext',
                    handler: function (_this, n) {
                        queryTreeByLocal(dPushLeftTree, dPushLeftStore, 'name', n);
                    }
                }],
                listeners: {
                    itemclick: function (_this, record) {
                        var children = record.childNodes;
                        if (children.length) {
                            dPushCenterGrid.show();
                            dictGrid.hide();
                            dPushParams.menuId = record.get('id');
                            dPushCenterGridStore.load({params: dPushParams});
                        } else {
                            dPushCenterGrid.hide();
                            dictGrid.show();
                            gridParams.menuId = record.get('id');
                            dictStore.reload({params: gridParams});
                        }
                    },
                    itemcontextmenu: function (_this, record, item, index, e) {
                        e.preventDefault();
                        if (record.get('type') == 2) {
                            if (menu) {
                                menu.destroy();
                            }
                            menu = new Ext.menu.Menu({
                                items: [{
                                    text: '添加下级',
                                    iconCls: 'Add',
                                    handler: function () {
                                        Ext.dataDict.EditDataDictWin.init(record, null, function (data) {
                                            if (record.isLeaf()) {
                                                record.set('leaf', false, {dirty: false});//设为非叶子节点
                                                record.set('expanded', true, {dirty: false});//展开
                                            } else {
                                                if (!record.isExpanded()) {
                                                    record.expand();
                                                }
                                            }
                                            var node = record.createNode(data);
                                            record.appendChild(node);
                                        });
                                    }
                                }, {
                                    text: '添加同级',
                                    hidden: record.get('id') <= 0,
                                    iconCls: 'Pageadd',
                                    handler: function () {
                                        var pnode = record.parentNode;
                                        Ext.dataDict.EditDataDictWin.init(pnode, null, function (data) {
                                            if (!pnode.isExpanded()) {
                                                pnode.expand();
                                            }
                                            var node = pnode.createNode(data);
                                            pnode.appendChild(node);
                                        });
                                    }
                                }, '-', {
                                    text: '修改',
                                    hidden: record.get('id') <= 0,
                                    iconCls: 'Pageedit',
                                    handler: function () {
                                        Ext.dataDict.EditDataDictWin.init(null, record, function (data) {
                                            delete(data.leaf);//不需要更新树节点的状态信息
                                            record.set(data, {dirty: false});
                                        });
                                    }
                                }, {
                                    text: '删除',
                                    hidden: record.get('id') <= 0,
                                    iconCls: 'Delete',
                                    handler: function () {
                                        Ext.Msg.confirm('提示', '确定删除?', function (btn) {
                                            if (btn == 'yes') {
                                                record.remove();
                                                dPushLeftStore.sync();
                                            }
                                        });
                                    }
                                }]
                            });
                            menu.showAt(e.getXY());
                        }
                    },
                    containercontextmenu: function (_this, e) {
                        e.preventDefault();
                        if (menu) {
                            menu.hide();
                        }
                    },
                    containerclick: function () {
                        if (menu) {
                            menu.hide();
                        }
                    }
                }
            });
            //人员表格
            var renyuanStore = new Ext.data.Store({
                fields: ['id', 'name', 'phone', 'duty'],
                autoLoad: true,
                proxy: {
                    url: GLOBAL_PATH + '/app/personValid/queryStaffs',
                    type: 'ajax'
                }
            });
            var renyuanGrid = new Ext.grid.Panel({
                hidden: true,
                store: renyuanStore,
                region: 'east',
                height: '100%',
                width: '30%',
                selModel: 'checkboxmodel',
                columns: [
                    {text: '名称', dataIndex: 'name', flex: 1},
                    {text: '电话', dataIndex: 'phone', flex: 1},
                    {text: '职位', dataIndex: 'duty', flex: 1}
                ],
                tbar: ['<b>选择人员</b>', '->', {}]
            });
            //人员树
            var renyuanTreeStore = new Ext.data.TreeStore({
                fields: ['id', 'name'],
                root: {
                    id: 0,
                    name: '人员',
                    checked: false
                },
                proxy: {
                    type: 'ajax',
                    url: GLOBAL_PATH + '/app/personValid/getStaffTree'
                },
                autoLoad: true
            });
            var renyuanTree = new Ext.tree.Panel({
                store: renyuanTreeStore,
                region: 'east',
                height: '100%',
                width: '30%',
                tbar: ['<b>选择人员</b>', {
                    xtype: 'triggertext',
                    handler: function (_this, n) {
                        queryTreeByLocal(renyuanTree, renyuanTreeStore, 'name', n);
                    }
                }],
                displayField: 'name'
            });
            //复选框联动
            renyuanTree.on('checkchange', function (node, checked) {
                checkChild(node, checked);
                checkFather(node, checked);
            }, renyuanTree);
            var panelDPush = new Ext.resizablePanel({
                title: '待推送',
                width: '100%',
                height: '100%',
                layout: 'border',
                border: false,
                items: [dPushLeftTree, dPushCenterGrid, dictGrid, renyuanTree, renyuanGrid]
            });

//=========================================待推送end===========================================================

//=========================================已推送begin===========================================================
            var yPushCenterGridStore = Ext.create('Ext.data.Store', {
                fields: ['id', 'name', 'menuId', 'userId', 'receiver', 'time', 'flag', 'userName', 'receiverName', 'depName'],
                autoLoad: false,
                proxy: {
                    url: GLOBAL_PATH + '/app/favorite/push/getAllPushByReceivers',
                    type: 'ajax'
                }
            });

            var yPushLeftStore = Ext.create('Ext.data.Store', {
                fields: ['id', 'name', 'phone', 'duty'],
                autoLoad: true,
                proxy: {
                    url: GLOBAL_PATH + '/app/personValid/queryStaffs',
                    type: 'ajax'
                }
            });

            var yPushCenterGrid = Ext.create('Ext.grid.Panel', {
                title: '推送信息',
                region: 'center',
                store: yPushCenterGridStore,
                tbar: [{
                    xtype: 'textfield',
                    fieldLabel: '内容名称',
                    labelAlign: 'right',
                    labelWidth: 70,
                    listeners: {
                        change: function (_this, newValue, oldValue, eOpts) {
                            ppushParams.name = newValue;
                        }
                    }

                }, {
                    xtype: 'button',
                    text: '查询',
                    iconCls: 'Find',
                    handler: function () {
                        yPushCenterGridStore.load({params: ppushParams})
                    }
                }, '->', {
                    type: 'button', text: '删除推送', handler: function () {
                        var sels = yPushCenterGrid.getSelection();
                        var ids = '';
                        var idArr = [];
                        if (sels && sels.length > 0) {
                            Ext.each(sels, function (rec) {
                                idArr.push(rec.get('id'));
                            });
                            ids = idArr.join(',');
                            Ext.Msg.confirm('警告', '是否删除推送', function (btn) {
                                if (btn == 'yes') {
                                    //TODO 删除
                                    Ext.Ajax.request({
                                        url: GLOBAL_PATH + '/app/favorite/push/deleteAppPush',
                                        params: {
                                            ids: ids
                                        },
                                        success: function (response) {
                                            var result = Ext.decode(response.responseText);
                                            Ext.Msg.alert('信息', result.msg);
                                            yPushCenterGridStore.reload();
                                        }
                                    });

                                }
                            });
                        } else {
                            Ext.Msg.alert('警告', '请选择要删除的推送');
                        }
                    }
                }],
                columns: [
                    {text: '内容名称', dataIndex: 'name', flex: 2},
                    {text: '发送人', dataIndex: 'userName', flex: 1},
                    {text: '发送部门', dataIndex: 'depName', flex: 1},
                    {text: '接收人', dataIndex: 'receiverName', flex: 1},
                    {text: '时间', dataIndex: 'time', flex: 1},
                    {
                        text: '状态', dataIndex: 'flag', flex: 1, renderer: function (value) {
                        if (value == 1) {
                            return '已读';
                        }
                        return '未读';
                    }
                    }
                ],
                selModel: {
                    selType: 'checkboxmodel'
                },
                width: '80%',
                height: '100%'
            });
            var yPushLeftGrid = Ext.create('Ext.grid.Panel', {
                title: '接收人员',
                store: yPushLeftStore,
                region: 'west',
                columns: [
                    {text: '名称', dataIndex: 'name', flex: 1},
                    {text: '职位', dataIndex: 'duty', flex: 1}
                ],
                selModel: {
                    selType: 'checkboxmodel'
                },
                width: '20%',
                height: '100%',
                listeners: {
                    select: function (_this, record) {
                        var _this = this;
                        var recs = _this.getSelection();
                        var receivers = '';
                        var i = 0;
                        Ext.each(recs, function (record) {
                            if (i != recs.length - 1) {
                                receivers += record.get('id') + ',';
                            } else {
                                receivers += record.get('id')
                            }
                            i++;
                        })
                        ppushParams.receivers = receivers;
                        yPushCenterGridStore.load({params: ppushParams});
                    }
                }

            });
            var panelYPush = new Ext.panel.Panel({
                title: '已推送',
                width: '100%',
                height: '100%',
                layout: 'border',
                items: [yPushLeftGrid, yPushCenterGrid]
            });
//=========================================已推送end===========================================================
            var pushTab = Ext.create('Ext.tab.Panel', {
                width: '100%',
                height: '100%',
                renderTo: 'pushTab',
                items: [panelDPush, panelYPush]
            });

            //推送
            function pushMenus(menuSel) {
                //获取人员
                var receivers = renyuanTree.getSelectionModel().getSelection();
                if (menuSel.length > 0 && receivers.length > 0) {
                    var appPushs = [];
                    for (var i = 0; i < menuSel.length; i++) {
                        for (var j = 0; j < receivers.length; j++) {
                            var rec = receivers[j];
                            if (rec.isLeaf()) {
                                var appPush = {
                                    name: menuSel[i].get("name"),
                                    menuId: menuSel[i].get("id"),
                                    receiver: rec.get("id")
                                };
                                appPush = Ext.encode(appPush);
                                appPushs.push(appPush);
                            }
                        }
                    }
                    appPushs.join(",");
                    appPushs = "[" + appPushs + "]";
                    Ext.Ajax.request({
                        url: GLOBAL_PATH + '/app/favorite/push/saveAppPushes',
                        params: {
                            appPushs_str: appPushs
                        },
                        success: function (response) {
                            Ext.Msg.alert("提示", "推送成功");
                        },
                        failure: function (response) {
                            Ext.Msg.alert("提示", "推送失败");
                        }
                    })
                } else {
                    Ext.Msg.alert("提示", "请选择内容和收件人");
                }
            }
        });

        /**
         * 预览内容
         */
        function previewContent(record) {
            open(APP_DATADICT_PATH + '/previewPage?menuId=' + record.get('id') + '&name=' + record.get('name'));
        }

        /**
         * 级联子节点
         */
        function checkChild(node, checked) {
            node.set('checked', checked);
            node.eachChild(function (childNode) {
                checkChild(childNode, checked);
            });
        }

        /**
         * 级联父节点
         */
        function checkFather(node, checked) {
            //判断同级有没有被选中，不需要管下级
            node.set('checked', checked);
            if (!checked) {
                if (node.parentNode) {
                    var needContinue = true;
                    node.parentNode.eachChild(function (childNode) {
                        if (childNode.get('checked')) {
                            needContinue = false;
                        }
                    });
                    if (needContinue)
                        checkFather(node.parentNode, checked);
                }
            } else {
                if (node.parentNode) {
                    checkFather(node.parentNode, checked);
                }
            }
        }

    </script>
</head>
<body>
<div id="pushTab" style="width: 100%;height: 100%"></div>
<script src="<%=request.getContextPath()%>/City/common/queryTreeByLocal.js"></script>
<script src="<%=request.getContextPath()%>/City/app/dataDict/editDataDictMenuWin.js"></script>
<script src="<%=request.getContextPath()%>/City/app/dataDict/editDataDictInfoWin.js"></script>
</body>
</html>
