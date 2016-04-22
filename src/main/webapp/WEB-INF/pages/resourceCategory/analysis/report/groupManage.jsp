<%--
  Created by IntelliJ IDEA.
  User: Paul
  Date: 2016/2/22
  Time: 11:17
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<%
    String contextPath = request.getContextPath();
%>
<head>
    <title>自定义查询管理</title>
    <style>
        .esi-btn {
            margin: 0px 10px;
            cursor: pointer;
            padding: 13px;
            background-color: #3892D3;
            color: white;
        }
    </style>
</head>
<body>
<script>
    var contextPath = "<%=contextPath%>";
    var MARGIN_ROW_SPACE = '8 0 0 0';
    Ext.onReady(function () {
        var __RESEARCH_GROUP_ID;
//    自定义查询分组树
        var researchGroupTreeStore = Ext.create('Ext.data.TreeStore', {
            fields: ['id', 'parentId', 'name', 'leaf'],
            proxy: {
                type: 'ajax',
                api: {
                    read: contextPath + '/resourcecategory/analysis/report/customResearchManage/getResearchGroupTree'
                }
            },
            root: {
                expanded: true,
                id: 0,
                name: '自定义查询分组'
            }
        });
//        自定义查询分组树
        var researchGroupTree = Ext.create('Ext.tree.Panel', {
            width: '20%',
            height: '100%',
            store: researchGroupTreeStore,
            rootVisible: true,
            displayField: 'name',
            region: 'west',
            viewConfig: {
                plugins: {
                    ptype: 'treeviewdragdrop'
                },
                listeners: {
                    'beforedrop': function (node, data, overModel, dropPosition, dropHandlers) {//overModel是一个NodeInterface,data是一个object，有records、item、view等等。
                        dropHandlers.wait = true;
                        var overParentId = overModel.get('parentId');
                        var moveParentId = data.records[0].get('parentId');
                        var moveId = data.records[0].get('id');
                        var overId = overModel.get('id');
                        //与后台交互
                        Ext.Ajax.request({
                            url: contextPath + '/resourcecategory/analysis/report/customResearchManage/sortResearchGroup',
                            method: 'POST',
                            params: {
                                moveId: moveId,
                                overId: overId,
                                moveParentId: moveParentId,
                                overParentId: overParentId,
                                dropPosition: dropPosition
                            },
                            success: function (data) {
                                if (data) {
                                    dropHandlers.processDrop();
                                } else {
                                    dropHandlers.cancelDrop();
                                }
                            },
                            failure: function (response, opts) {
                                var result = Ext.decode(response.responseText);
                                dropHandlers.cancelDrop();
                            }
                        });
                    }
                }
            },
//            tbar: [{
//                xtype: 'textfield',
//                emptyText: '在此处查询',
//                triggerCls: 'x-form-clear-trigger',
//                onTriggerClick: function () {
//                    this.reset();
//                },
//                listeners: {
//                    change: function (_this, n, o) {
//                        queryTreeByLocal(researchGroupTree, researchGroupTreeStore, 'name', n);
//                    }
//                }
//            }],
            listeners: {
                itemcontextmenu: function (_this, record, item, index, e) {
                    e.preventDefault();
                    if (window.researchGroupInfoMenu) {
                        window.researchGroupInfoMenu.hide();
                    }
                    showResearchGroupInfoMenu(record, e);
                },
                itemclick: function (_this, record, item, index, e, eOpts) {
                    __RESEARCH_GROUP_ID = record.get('id');
                    if (__RESEARCH_GROUP_ID != 0) {
                        customResearchStore.load({params: {id: record.get('id')}})
                    }
                }
            }
        });

        /**
         * 显示自定义查询分组右键菜单
         */
        function showResearchGroupInfoMenu(r, e) {
            var record = researchGroupTree.getSelectionModel().getSelection();
            var researchGroupInfoMenu = new Ext.menu.Menu({
                renderTo: Ext.getBody(),
                items: [
                    {
                        text: '添加下级',
                        iconCls: 'Add',
                        handler: function () {
                            if (record && record.length > 0) {
                                //                                显示添加下级页面
                                var selection = record[0];
                                Ext.researchGroupContextMenuWin.init(selection, null, function (model) {
                                    selection.set('leaf', false);
                                    selection.expand();
                                    selection.appendChild(model);
                                });
                            }
                        }
                    }, {
                        text: '添加同级',
                        disabled: r.get('id') == 0,
                        iconCls: 'Controladdblue',
                        handler: function () {
                            if (record && record.length > 0) {
                                //                                 显示添加同级页面
                                var selection = record[0];
                                var parent = selection.parentNode;
                                Ext.researchGroupContextMenuWin.init(parent, null, function (model) {
                                    parent.appendChild(model);
                                });
                            }
                        }
                    }, '-', {
                        text: '修改',
                        disabled: r.get('id') == 0,
                        iconCls: 'Pageedit',
                        handler: function () {
                            if (record && record.length > 0) {
                                var selection = record[0];
                                var parent = selection.parentNode;
                                Ext.researchGroupContextMenuWin.init(parent, selection, function (model) {
                                    selection.set('name', model.name);
                                });
                            }
                        }
                    }, {
                        text: '删除',
                        disabled: r.get('id') == 0,
                        iconCls: 'Delete',
                        handler: function () {
                            Ext.Msg.confirm('提示', '确定要删除吗？', function (id) {
                                if (id == 'yes') {
                                    deleteResearchGroup(record);
                                }
                            });
                        }
                    }
                ]
            });

            /**
             * 删除自定义查询分组
             * @param r
             */
            function deleteResearchGroup(r) {
                $.ajax({
                    type: 'post',
                    url: contextPath + '/resourcecategory/analysis/report/customResearchManage/deleteResearchGroup',
                    data: {id: record[0].get('id')},
                    dataType: 'json',
                    success: function (data) {
                        Ext.Msg.alert('提示', data.msg, function (id) {
                            if ('ok' == id) {
                                if (data.code == 200) {
                                    record[0].remove();
                                }
                            }
                        });
                    },
                    error: function (data) {
                        Ext.Msg.alert('提示', '系统繁忙，请稍候再试');
                    }
                });
            }

            researchGroupInfoMenu.showAt(e.getPoint());
            window.researchGroupInfoMenu = researchGroupInfoMenu;
        }

//        自定义查询列表
        var customResearchStore = new Ext.data.Store({
            fields: ['id', 'name', 'resourceId', 'comments', 'type', 'period', 'beginYear', 'endYear', 'beginPeriod', 'endPeriod', 'status'],
//            pageSize: 20,
            proxy: {
                type: 'ajax',
                api: {
                    read: contextPath + '/resourcecategory/analysis/report/customResearchManage/getCustomResearchs'
                },
                reader: {
                    type: 'json',
                    rootProperty: 'datas'
                }
            },
            autoLoad: false
        });

//        自定义查询表格
        var customResearchGrid = new Ext.grid.Panel({
            width: '80%',
            region: 'center',
            store: customResearchStore,
            columns: [
                {
                    text: '序号',
                    dataIndex: 'id',
                    width: 80
                }, {
                    text: '名称',
                    dataIndex: 'name',
                    flex: 1
                }, {
                    text: '数据源',
                    dataIndex: 'dataSet',
                    flex: 1,
                    renderer: function (data) {
                        return data ? data.name : '';
                    }
                }, {
                    text: '时间频度',
                    dataIndex: 'period',
                    flex: 1,
                    renderer: function (data) {
                        var str = '';
                        switch (data) {
                            case 1:
                                str = '年';
                                break;
                            case 2:
                                str = '半年';
                                break;
                            case 3:
                                str = '季';
                                break;
                            case 4:
                                str = '月';
                                break;
                        }
                        return str;
                    }
                }, {
                    text: '说明',
                    dataIndex: 'comments',
                    flex: 1
                }
            ],
            tbar: ['自定义查询', '->',
                {
                    xtype: 'button',
                    text: '新建模板',
                    handler: function () {
//                        显示新建模板页面
                        if (!__RESEARCH_GROUP_ID) {
                            Ext.Msg.alert('提示', '请选择添加到的自定义查询分组');
                            return;
                        }
                        Ext.customResearchAddWin.init(__RESEARCH_GROUP_ID, null, function (model) {
                            customResearchStore.load({params: {id: __RESEARCH_GROUP_ID}});
                        });
                    }
                }
            ],
            listeners: {
                itemcontextmenu: function (_this, record, item, index, e) {
                    e.preventDefault();
                    nodeInfoMenu.showAt(e.getPoint());
                }
            }
        });
        //节点信息表格菜单
        var nodeInfoMenu = new Ext.menu.Menu({
            renderTo: Ext.getBody(),
            items: [
                {
                    text: '设计',
                    iconCls: 'Reportedit',
                    handler: function () {
                        var selection = customResearchGrid.getSelectionModel().getSelection()[0];
                        open(contextPath + '/resourcecategory/analysis/report/designCustomResearch/showDesignResearch?_cr=' + selection.get('id'));
                    }
                }, {
                    text: '修改',
                    iconCls: 'Pageedit',
                    handler: function () {
                        var selection = customResearchGrid.getSelectionModel().getSelection()[0];
                        Ext.customResearchAddWin.init(selection.get('researchGroupId'), selection, function (model) {
                            customResearchStore.load({params: {id: __RESEARCH_GROUP_ID}});
                        });
                    }
                }, {
                    text: '删除',
                    iconCls: 'Delete',
                    handler: function () {
                        var selection = customResearchGrid.getSelection()[0];
                        Ext.Msg.confirm('提示', '确定要删除吗？', function (id) {
                            if (id == 'yes') {
                                $.ajax({
                                    url: contextPath + '/resourcecategory/analysis/report/customResearchManage/deleteCustomResearch',
                                    type: 'post',
                                    data: {id: selection.get('id')},
                                    dataType: 'json',
                                    success: function (rs) {
                                        if (rs) {
                                            customResearchGrid.getStore().remove(selection);
                                        } else {
                                            Ext.Msg.alert('提示', '系统繁忙，请稍候再试');
                                        }
                                    },
                                    error: function () {
                                        Ext.Msg.alert('提示', '系统繁忙，请稍候再试');
                                    }
                                });
                            }
                        });
                    }
                }]
        });
//        主内容区域
        Ext.create('Ext.panel.Panel', {
            renderTo: 'customResearchManageId',
            layout: 'border',
            height: '100%',
            border: false,
            frame: false,
            width: '100%',
            items: [researchGroupTree, customResearchGrid],
            listeners: {
                render: function () {
                    if (indexPanel) {
                        var tabPanel = indexPanel.down('#tabCenter');
                        var myTab = tabPanel.getActiveTab();
                        if (myTab) {
                            myTab.myPanel = this;
                        }
                        if (this.hasListener('reDR')) {
                            this.un('reDR');
                        }
                        this.on('reDR', function (obj) {
                            if (obj) {
                                this.updateBox(obj);
                            }
                        });
                    }
                },
                myUpdateBox: function (obj) {
                    this.updateBox(obj);
                }
            }
        });
    });

</script>
<div id="customResearchManageId" style="width:100%;height: 100%;"></div>
<script src="<%=contextPath%>/City/common/queryTreeByLocal.js"></script>
<script src="<%=contextPath%>/City/resourceCategory/analysis/report/manage/context-menu-win.js"></script>
<script src="<%=contextPath%>/City/resourceCategory/analysis/report/manage/add-research.js"></script>
</body>
</html>
