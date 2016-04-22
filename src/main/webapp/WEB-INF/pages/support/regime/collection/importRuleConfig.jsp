<%--
  Created by IntelliJ IDEA.
  User: wys
  Date: 2016/2/3
  Time: 9:15
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
    <script type="text/javascript"
            src="<%=request.getContextPath()%>/City/support/regime/collection/fillImportRule.js"></script>
    <script type="text/javascript"
            src="<%=request.getContextPath()%>/City/support/regime/collection/fillExcelMap.js"></script>
    <script type="text/javascript">
        Ext.onReady(function () {
            var ruleModel = createModel('ruleModel', function () {
                Ext.define('ruleModel', {
                    extend: 'Ext.data.Model',
                    idProperty: 'id',
                    fields: [
                        {name: 'id', type: 'int'},
                        {name: 'ruleName', type: 'string'}
                    ]
                });
            });
            var excelMapModel = createModel('excelMapModel', function () {
                Ext.define('excelMapModel', {
                    extend: 'Ext.data.Model',
                    idProperty: 'id',
                    fields: [
                        {name: 'id', type: 'int'},
                        {name: 'excelName', type: 'string'},
                        {name: 'sheetName', type: 'string'},
                        {name: 'excelRow', type: 'int'},
                        {name: 'excelCol', type: 'int'},
                        {name: 'tmpId', type: 'int'},
                        {name: 'tmpName', type: 'string'}
                    ]
                });
            });
            var ruleStore = new Ext.data.Store({
                model: 'ruleModel',
                autoLoad: true,
                proxy: {
                    type: 'ajax',
                    url: GLOBAL_PATH + "/support/regime/collection/queryImportRule",
                    actionMethods: {create: 'POST', read: 'POST', update: 'POST', destroy: 'POST'},
                    api: {
                        update: GLOBAL_PATH + "/support/regime/collection/updateImportRule",
                        destroy: GLOBAL_PATH + "/support/regime/collection/delImportRule"
                    },
                    reader: {
                        type: 'json',
                        model: 'ruleModel',
                        rootProperty: 'datas'
                    }
                }

            });
            var excelMapStore = new Ext.data.Store({
                model: 'excelMapModel',
                autoLoad: false,
                proxy: {
                    type: 'ajax',
                    url: GLOBAL_PATH + "/support/regime/collection/queryExcelMap",
                    api: {
                        update: GLOBAL_PATH + "/support/regime/collection/updateExcelMap",
                        destroy: GLOBAL_PATH + "/support/regime/collection/delExcelMap"
                    },
                    actionMethods: {create: 'POST', read: 'POST', update: 'POST', destroy: 'POST'},
                    reader: {
                        type: 'json',
                        model: 'excelMapModel',
                        rootProperty: 'datas'
                    }
                }

            });
            var roleGrid = new Ext.grid.Panel({
                width: '80%',
                height: 200,
                region: 'south'
            });
            var excelMapGrid = new Ext.grid.Panel({
                width: '80%',
                height: '80%',
                region: 'center',
                store: excelMapStore,
                columns: [
                    {text: '报表名称', dataIndex: 'tmpName', flex: 1},
                    {text: 'excel名称', dataIndex: 'excelName', flex: 1},
                    {text: 'sheet名称', dataIndex: 'sheetName', flex: 1},
                    {text: '行', dataIndex: 'excelRow', flex: 1},
                    {text: '列', dataIndex: 'excelCol', flex: 1}
                ],
                tbar: [{
                    type: 'button', text: '添加报表', handler: function () {
                        if (excelMapStore.importRuleId) {
                            var win = Ext.fillExcelMap.init(function (data) {
                                Ext.Ajax.request({
                                    url: GLOBAL_PATH + '/support/regime/collection/addExcelmap',
                                    jsonData: data,
                                    params: {importRuleId: excelMapStore.importRuleId},
                                    callback: function (option, success, response) {
                                        if (success) {
                                            var result = Ext.decode(response.responseText);
                                            var datas = result.datas;
                                            if (result.success) {
                                                excelMapStore.add(datas);
                                            } else {
                                                Ext.Msg.alert('警告', result.msg);
                                            }
                                            win.close();
                                        } else {
                                            Ext.Msg.alert('警告', '保存失败');
                                        }
                                    }

                                })


                            }, true, null);
                        }

                    }
                }, {
                    type: 'button', text: '修改报表', handler: function () {
                        var selModel = excelMapGrid.getSelectionModel();
                        var sels = excelMapGrid.getSelection();
                        var selected = null;
                        if (selModel.hasSelection()) {
                            selected = sels[0];
                            var win = Ext.fillExcelMap.init(function (excelMap) {
                                selected.set(excelMap);
                                excelMapStore.sync();
                                win.close();
                            }, false, selected);
                        }

                    }
                }, {
                    type: 'button', text: '删除报表', handler: function () {
                        var sels = excelMapGrid.getSelection();
                        if (sels && sels.length > 0) {
                            Ext.Msg.confirm('警告', '是否删除', function (btnStr) {
                                if ('yes' == btnStr) {
                                    excelMapStore.remove(sels);
                                    excelMapStore.sync({params: {importRuleId: excelMapStore.importRuleId}});
                                }
                            });
                        } else {
                            Ext.Msg.alert('警告', '请选择要删除的内容');
                        }

                    }
                }],
            });
            var ruleGrid = new Ext.grid.Panel({
                width: '20%',
                region: 'west',
                store: ruleStore,
                tbar: [{
                    type: 'button', text: '添加规则', handler: function () {
                        var win = Ext.fillImportRule.init(function (importRule) {
                            Ext.Ajax.request({
                                url: GLOBAL_PATH + '/support/regime/collection/addImportRule',
                                jsonData: importRule,
                                callback: function (option, success, response) {
                                    if (success) {
                                        var data = Ext.decode(response.responseText).datas;
                                        ruleStore.add(data);
                                        win.close();
                                    } else {
                                        Ext.Msg.alert('警告', '保存失败');
                                    }
                                }

                            })


                        }, true, null);

                    }
                }, {
                    type: 'button', text: '修改规则', handler: function () {
                        var selModel = ruleGrid.getSelectionModel();
                        var sels = ruleGrid.getSelection();
                        var selected = null;
                        if (selModel.hasSelection()) {
                            selected = sels[0];
                            var win = Ext.fillImportRule.init(function (importRule) {
                                selected.set('ruleName', importRule.ruleName);
                                ruleStore.sync();
                                win.close();
                            }, false, selected);
                        }

                    }
                }, {
                    type: 'button', text: '删除规则', handler: function () {
                        var selModel = ruleGrid.getSelectionModel();
                        var sels = ruleGrid.getSelection();
                        var selected = null;
                        if (selModel.hasSelection()) {
                            ruleStore.remove(sels);
                            ruleStore.sync();
                        }

                    }
                }],
                columns: [
                    {text: '规则名称', dataIndex: 'ruleName', flex: 1}
                ],
                listeners: {
                    itemclick: function (view, record) {
                        excelMapStore.load({
                            params: {importRuleId: record.getId()}
                        });
                        excelMapStore.importRuleId = record.getId();
                    }
                }
            });
            var centerPanel = new Ext.panel.Panel({
                width: '80%',
                region: 'center',
                layout: 'border',
                items: [excelMapGrid, roleGrid]
            });
            var importRuleConfigPanel = new Ext.resizablePanel({
                title: '导入规则配置',
                renderTo: 'importRuleConfigPanel',
                height: '100%',
                width: '100%',
                layout: 'border',
                items: [ruleGrid, centerPanel]

            });
        });
    </script>
</head>
<body>
<div id="importRuleConfigPanel" style="width: 100%;height: 100%"></div>
</body>
</html>
