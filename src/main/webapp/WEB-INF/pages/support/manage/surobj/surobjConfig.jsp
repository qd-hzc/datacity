<%--
  Created by IntelliJ IDEA.
  User: wys
  Date: 2016/1/15
  Time: 11:55
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
    <jsp:include page="../../../common/sysConstant.jsp"></jsp:include>
    <%--<script type="text/javascript" src="<%=request.getContextPath()%>/Plugins/extjs/ux/TreePicker.js"></script>--%>
    <script type="text/javascript"
            src="<%=request.getContextPath()%>/City/support/manage/surobj/addExtSurObjWin.js"></script>
    <script type="text/javascript"
            src="<%=request.getContextPath()%>/City/support/manage/surobj/fillSurObjGroupWin.js"></script>
    <script type="text/javascript"
            src="<%=request.getContextPath()%>/City/support/manage/surobj/fillAreaObjWin.js"></script>
    <script type="text/javascript"
            src="<%=request.getContextPath()%>/City/support/manage/surobj/fillExtSurObjWin.js"></script>
    <script type="text/javascript">

        Ext.onReady(function () {
            var extObjModel = createModel('extSurObjModel', function () {
                Ext.define('extSurObjModel', {
                    extend: 'Ext.data.Model',
                    idProperty: 'id',
                    fields: [
                        {name: 'id', type: 'int'},
                        {name: 'surObjName', type: 'string'},
                        {name: 'surObjCode', type: 'string'},
                        {name: 'surObjInfo', type: 'string'},
                        {name: 'surAreaId', type: 'int'},
                        {name: 'surAreaName', type: 'string'}
                    ]
                });
            });
            var surObjModel = createModel('surObjModel', function () {
                Ext.define('surObjModel', {
                    extend: 'Ext.data.Model',
                    idProperty: 'id',
                    fields: [
                        {name: 'id', type: 'int'},
                        {name: 'surveyObjName', type: 'string'},
                        {name: 'surveyObjId', type: 'int'},
                        {name: 'surveyObjAreaId', type: 'int'},
                        {name: 'surveyObjAreaName', type: 'string'},
                        {name: 'surveyObjType', type: 'int'},
                        {name: 'surveyObjSort', type: 'int'},
                        {name: 'surveyObjGroupId', type: 'int'}
                    ]
                });
            });
            var objGroupModel = createModel('surObjGroupModel', function () {
                Ext.define('surObjGroupModel', {
                    extend: 'Ext.data.Model',
                    idProperty: 'id',
                    fields: [
                        {name: 'id', type: 'int'},
                        {name: 'surveyObjGroupName', type: 'string'},
                        {name: 'surveyObjGroupInfo', type: 'string'},
                        {name: 'surveyObjGroupType', type: 'int'},
                        {name: 'surveyObjGroupSort', type: 'int'}
                    ]
                });
            });
            //其它统计对象管理store
            var extSurObjStore = new Ext.data.Store({
                model: 'extSurObjModel',
                proxy: {
                    type: 'ajax',
                    actionMethods: {create: 'POST', read: 'POST', update: 'POST', destroy: 'POST'},
                    api: {
                        read: GLOBAL_PATH + '/support/manage/surobj/queryExtSurObj',
                        update: GLOBAL_PATH + '/support/manage/surobj/updateExtSurObj',
                        destroy: GLOBAL_PATH + '/support/manage/surobj/removeExtSurObj'
                    },
                    reader: {
                        type: 'json',
                        rootProperty: 'datas'
                    }
                },
                autoLoad: true
            });
            //统计对象分组store
            var surObjGroupStore = new Ext.data.Store({
                model: 'surObjGroupModel',
                proxy: {
                    type: 'ajax',
                    actionMethods: {create: 'POST', read: 'POST', update: 'POST', destroy: 'POST'},
                    api: {
                        read: GLOBAL_PATH + '/support/manage/surobj/querySurObjGroup',
                        update: GLOBAL_PATH + '/support/manage/surobj/updateSurObjGroup',
                        destroy: GLOBAL_PATH + '/support/manage/surobj/delSurObjGroup'
                    },
                    reader: {
                        type: 'json',
                        rootProperty: 'datas'
                    }
                },
                autoLoad: true
            });

            //统计对象Store
            var surObjStore = new Ext.data.Store({
                model: 'surObjModel',
                proxy: {
                    type: 'ajax',
                    actionMethods: {create: 'POST', read: 'POST', update: 'POST', destroy: 'POST'},
                    api: {
                        read: GLOBAL_PATH + '/support/manage/surobj/querySurObjByGroupId',
                        update: GLOBAL_PATH + '/support/manage/surobj/updateSurObj',
                        destroy: GLOBAL_PATH + '/support/manage/surobj/delSurObj'
                    },
                    reader: {
                        type: 'json',
                        rootProperty: 'datas'
                    }
                },
                autoLoad: false
            });
            //统计对象表格
            var surObjGrid = new Ext.grid.Panel({
                store: surObjStore,
                width: '70%',
                multiSelect:true,
                region: 'center',
                selType:'checkboxmodel',
                tbar: ['->', {
                    text: '添加地区统计对象', handler: function () {
                        if (surObjStore.surObjGroupId)
                            Ext.fillAreaObjWin.init(surObjStore);
                    }
                }, {
                    text: '添加其它统计对象', handler: function () {
                        if (surObjStore.surObjGroupId)
                            Ext.fillExtSurObjWin.init(surObjStore);
                    }
                }, /*{
                 text: '添加名录统计对象', handler: function () {

                 }
                 },*/ {
                    text: '删除统计对象', handler: function () {
                        var selModel = surObjGrid.getSelectionModel();
                        var sels = selModel.getSelection();
                        surObjStore.remove(sels);
                        surObjStore.sync();
                    }
                }],
                viewConfig: {
                    plugins: {
                        ptype: 'gridviewdragdrop'
                    },
                    listeners: {
                        drop: function (node, data, overModel, dropPosition, eOpts) {
                            var allRecords = surObjStore.data.items;
                            var store = surObjStore;
                            Ext.Array.each(allRecords, function (record) {
                                record.set('surveyObjSort', store.indexOf(record));
                            })
                            store.sync();
                        }
                    }
                },
                columns: [
                    {text: '统计对象名称', dataIndex: 'surveyObjName', flex: 0.3},
                    {
                        text: '类型', dataIndex: 'surveyObjType', flex: 0.2, renderer: function (data) {
                        return SUROBJ_TYPE.getCH(data);
                    }
                    },
                    {text: '地区', dataIndex: 'surveyObjAreaName', flex: 0.2},
                    {text: '编码', dataIndex: 'surveyObjCode', flex: 0.4}
                ]

            });

            //统计对象分组表格
            var surObjGroupGrid = new Ext.grid.Panel({
                store: surObjGroupStore,
                width: '30%',
                region: 'west',
                tbar: [
                    {
                        text: '添加分组',
                        handler: function () {
                            var win = Ext.fillSurObjGroupWin.init(null, function (data) {
                                Ext.Ajax.request({
                                    url: GLOBAL_PATH + "/support/manage/surobj/addSurObjGroup",
                                    method: 'POST',
                                    jsonData: data,
                                    success: function (response, opts) {
                                        var result = Ext.JSON.decode(response.responseText);
                                        surObjGroupStore.add(result.datas[0]);
                                        surObjGroupStore.load();
                                        win.close();
                                    }
                                })
                            });
                        }
                    },
                    {
                        text: '修改分组',
                        handler: function () {
                            var selM = surObjGroupGrid.getSelectionModel();
                            var sel = selM.getSelection()[0];
                            var win = null;
                            if (sel) {
                                win = Ext.fillSurObjGroupWin.init(sel, function (data) {
                                    sel.set('surveyObjGroupName', data.surveyObjGroupName);
                                    sel.set('surveyObjGroupInfo', data.surveyObjGroupInfo);
                                    sel.set('surveyObjGroupType', data.surveyObjGroupType);
                                    surObjGroupStore.sync();
                                    win.close();
                                });
                            }

                        }
                    },
                    {
                        text: '删除分组',
                        handler: function () {
                            var selM = surObjGroupGrid.getSelectionModel();
                            var sels = selM.getSelection();
                            if (sels.length > 0) {
                                Ext.Msg.confirm('警告', '确定要删除么?', function (btn) {
                                    if (btn == 'yes') {
                                        surObjGroupStore.remove(sels);
                                        surObjGroupStore.sync();
                                    }
                                });

                            }
                        }
                    }
                ],
                columns: [
                    {text: '分组名称', dataIndex: 'surveyObjGroupName', flex: 0.3},
                   /* {
                        text: '分组类型', dataIndex: 'surveyObjGroupType', flex: 0.2, renderer: function (data) {
                        return SUROBJ_TYPE.getCH(data);
                    }
                    },*/
                    {text: '分组备注', dataIndex: 'surveyObjGroupInfo', flex: 0.5}
                ],
                listeners: {
                    itemclick: function (_this, record, item, index, e, eOpts) {
                        var surObjGroupId = record.getId();
                        surObjStore.surObjGroupId = surObjGroupId
                        surObjStore.load({
                            params: {
                                groupId: surObjGroupId
                            }
                        });
                    }
                }

            });

            //其它统计对象表格
            var extSurObjGridPanel = new Ext.grid.Panel({
                store: extSurObjStore,
                tbar: ['->', {
                    text: '添加统计对象',
                    handler: function () {

                        var win = Ext.addExtSurObjWin.init(null, function (data) {
                            Ext.Ajax.request({
                                url: GLOBAL_PATH + "/support/manage/surobj/addExtSurObj",
                                method: 'POST',
                                jsonData: data,
                                params: {surAreaId: data.surAreaId},
                                success: function (response, opts) {
                                    var result = Ext.JSON.decode(response.responseText);
                                    extSurObjStore.add(result.datas[0]);
                                    extSurObjStore.load();
                                }
                            });

                            win.close();
                        });

                    }
                }, {
                    text: '修改统计对象', handler: function () {
                        var selModel = extSurObjGridPanel.getSelectionModel();
                        var sels = selModel.getSelection();
                        if (sels.length == 1)
                            var win = Ext.addExtSurObjWin.init(sels[0], function (data) {
                                var sel = extSurObjGridPanel.getSelectionModel().getSelection();
                                sel[0].set('surObjName', data.surObjName);
                                sel[0].set('surObjCode', data.surObjCode);
                                sel[0].set('surAreaId', data.surAreaId);
                                sel[0].set('surObjInfo', data.surObjInfo);
                                sel[0].set('surAreaName', data.areaName);
                                extSurObjStore.sync({params: {surAreaId: data.surAreaId}});
                                win.close()
                            });
                    }
                }, {
                    text: '删除统计对象', handler: function () {
                        var selModel = extSurObjGridPanel.getSelectionModel();
                        var sels = selModel.getSelection();
                        if (sels.length > 0) {
                            Ext.Msg.confirm('警告', '确定要删除么?', function (btn) {
                                if (btn == 'yes') {
                                    extSurObjStore.remove(sels);
                                    extSurObjStore.sync();
                                }
                            });
                        }
                    }
                }],
                columns: [
                    {text: '统计对象名称', dataIndex: 'surObjName', flex: 0.5},
                    {text: '统计对象编码', dataIndex: 'surObjCode', flex: 0.1},
                    {text: '统计对象备注', dataIndex: 'surObjInfo', flex: 0.5},
                    {
                        text: '统计对象地区',
                        dataIndex: 'surAreaName',
                        renderer: function (data, model, record, row, col, store, view) {

                            if (record && record.data && record.data.surArea) {
                                if (!record.isModified('surAreaId')) {
                                    record.data.surAreaId = record.data.surArea.id;

                                    return record.data.surArea.name;
                                } else {
                                    record.data.surArea.id = record.data.surAreaId;
                                    record.data.surArea.name = record.get('surAreaName');
                                    return record.data.surAreaName;
                                }
                            } else
                                return data;

                        }, flex: 0.2
                    }
                ]

            });

            //tab容器
            var objTabPanel = new Ext.tab.Panel({
                items: [{
                    title: '统计对象分组管理',
                    layout: 'border',
                    items: [surObjGroupGrid, surObjGrid]
                }, {
                    title: '其它统计对象管理',
                    items: [extSurObjGridPanel]
                }/*,{
                 title:'基本单位名录统计对象'
                 }*/]
            });

            //容器
            var configSurObjPanel = new Ext.resizablePanel({
                renderTo: 'surobjConfig',
                height: '100%',
                width: '100%',
                layout: 'fit',
//                title: '调查对象配置',
                items: [objTabPanel]
            });
        });

    </script>

</head>
<body>
<div id="surobjConfig" style="height: 100%;width: 100%"></div>
</body>
</html>
