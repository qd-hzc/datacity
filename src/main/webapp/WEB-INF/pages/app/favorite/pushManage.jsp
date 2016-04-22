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
                        var receivers = renyuanGrid.getSelectionModel().getSelection();
                        if (menuInfo.length > 0 && receivers.length > 0) {
                            var appPushs = [];
                            for (var i = 0; i < menuInfo.length; i++) {
                                for (var j = 0; j < receivers.length; j++) {
                                    var appPush = {
                                        name: menuInfo[i].get("name"),
                                        menuId: menuInfo[i].get("id"),
                                        receiver: receivers[j].get("id"),
                                    };
                                    appPush = Ext.encode(appPush);
                                    appPushs.push(appPush);

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
                }]
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
                        {name: 'sortIndex', type: 'int'}
                    ]
                });
            });
            var dPushLeftStore = Ext.create('Ext.data.TreeStore', {
                model: 'AppDataDictMenu',
                parentIdProperty: 'parentId',
                root: {
                    id: 0,
                    name: '移动数据目录'
                },
                proxy: {
                    type: 'ajax',
                    api: {
                        read: APP_DATADICT_PATH + '/queryDictMenus',
                        update: APP_DATADICT_PATH + '/saveDictMenus',
                        destroy: APP_DATADICT_PATH + '/deleteDictMenus'
                    }
                },
                autoLoad: true
            });

            var dPushLeftTree = Ext.create('Ext.tree.Panel', {
                store: dPushLeftStore,
                displayField: 'name',
                region: 'west',
                height: '100%',
                width: '20%',
                tbar: ['<b>请选择内容</b>', {
                    xtype: 'triggertext',
                    handler: function (_this, n) {
                        queryTreeByLocal(dPushLeftTree, dPushLeftStore, 'name', n);
                    }
                }],
                listeners: {
                    itemclick: function (_this, record) {
                        dPushParams.menuId = record.get('id');
                        dPushCenterGridStore.load({params: dPushParams});
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
            var panelDPush = new Ext.resizablePanel({
                title: '待推送',
                width: '100%',
                height: '100%',
                layout: 'border',
                border: false,
                items: [dPushLeftTree, dPushCenterGrid, renyuanGrid]
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
                            Ext.Msg.alert('警告', '是否删除推送', function (btn) {
                                if (btn == 'ok') {
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
                            Ext.Msg.alert('警告', '请选择要删除的推送', function (btn) {
                            });
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
                    {text: '名称', dataIndex: 'name'},
                    {text: '电话', dataIndex: 'phone', flex: 1},
                    {text: '职位', dataIndex: 'duty'}
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
        });

    </script>
</head>
<body>
<div id="pushTab" style="width: 100%;height: 100%"></div>
<script src="<%=request.getContextPath()%>/City/common/queryTreeByLocal.js"></script>
</body>
</html>
