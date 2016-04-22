<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2015/12/31 0031
  Time: 下午 3:52
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<html>
<head>
    <title>指标分组配置</title>
    <meta charset="UTF-8"/>
    <jsp:include page="itemImp.jsp"/>
</head>
<body>
<div id="itemGroupContainer" style="width:100%;height:100%;"></div>

<script src="<%=request.getContextPath()%>/City/support/manage/item/addItemGroupWin.js"></script>
<script src="<%=request.getContextPath()%>/City/support/manage/item/addItemGroupInfoWin.js"></script>
<script src="<%=request.getContextPath()%>/City/support/manage/item/addItemGroupInfoBatchWin.js"></script>

<script>
    Ext.onReady(function () {
        //参数管理
        var commonParams = {
            groupParams: {
                name: '',
                status: 1
            },
            infoParams: {
                itemName: '',
                status: 1,
                includeDownLevel: false
            },
            infoSorted: true
        };
        //左侧分组树
        var groupStore = new Ext.data.TreeStore({
            fields: ['id', 'name', 'parentId', 'status', 'comments', 'sortIndex', 'leaf', 'children'],
            root: {
                expanded: 'true',
                id: 0,
                text: '指标体系'
            },
            proxy: {
                type: 'ajax',
                api: {
                    read: Global_Path + '/queryGroups'
                }
            },
//            autoSync: true,
            nodeParam: 'parentId',
            listeners: {
                beforeload: function (_this, operation) {
                    operation.params = commonParams.groupParams;
                }
            }
        });
        //分组树
        var groupTree = new Ext.tree.Panel({
            flex: 1,
            height: '100%',
            store: groupStore,
//            enableDD: true,
            viewConfig: {
                plugins: {
                    ptype: 'treeviewdragdrop',
                    ddGroup: 'groupSortGroup'
                },
                listeners: {
                    beforedrop: function (node, data, overModel, dropPosition, dropHandlers) {
                        if (commonParams.groupParams.name) {
                            Ext.Msg.alert('提示', '搜索时不可排序');
                            return false;
                        }
                        //执行拖拽过程
                        dropHandlers.wait = true;
                        //与后台交互
                        Ext.Ajax.request({
                            url: Global_Path + '/saveGroupSorts',
                            params: {
                                moveId: data.records[0].get('id'),
                                overId: overModel.get('id'),
                                overParentId: overModel.get('parentId'),
                                dropPosition: dropPosition
                            },
                            success: function (response) {
                                dropHandlers.processDrop();
                            },
                            failure: function (response, opts) {
                                Ext.Msg.alert('失败', "保存失败");
                                dropHandlers.cancelDrop();
                            }
                        });
                    }
                }
            },
            rootVisible: true,
            tbar: [{
                xtype: 'textfield',
                fieldLabel: '查询',
                labelWidth: 50,
                width: 150,
                labelAlign: 'right',
                listeners: {
                    change: function (_this, n, o) {
                        commonParams.groupParams.name = n;
                        groupStore.reload({
                            params: commonParams.groupParams, callback: function () {
                                if (n) {
                                    groupTree.expandAll();
                                }
                            }
                        });
                    }
                }
            }, {
                xtype: 'combobox',
                fieldLabel: '状态',
                labelWidth: 50,
                width: 120,
                labelAlign: 'right',
                displayField: 'text',
                valueField: 'value',
                store: new Ext.data.Store({
                    fields: ['text', 'value'],
                    data: [{text: '全部', value: null}, {text: '启用', value: 1}, {text: '废弃', value: 0}]
                }),
                value: commonParams.groupParams.status,
                listeners: {
                    change: function (_this, n, o) {
                        commonParams.groupParams.status = n;
                        groupStore.reload({
                            params: commonParams.groupParams, callback: function () {
                                if (commonParams.groupParams.name) {
                                    groupTree.expandAll();
                                }
                            }
                        });
                    }
                }
            }],
            listeners: {
                itemclick: function (_this, record) {
                    if (commonParams.treeMenu) {
                        commonParams.treeMenu.hide();
                    }
                    commonParams.infoParams.groupId = record.get('id');
                    infoStore.load({params: commonParams.infoParams});
                },
                itemcontextmenu: function (_this, record, item, index, e) {
                    //设置触发点击事件
                    groupTree.fireEvent('itemclick', _this, record);
                    //阻止浏览器默认行为
                    e.preventDefault();
                    //菜单
                    var menu = commonParams.treeMenu;
                    if (menu) {
                        menu.hide();
                    }
                    menu = commonParams.treeMenu = new Ext.menu.Menu({
                        renderTo: Ext.getBody(),
                        items: [{
                            text: '添加下级',
                            iconCls: 'Add',
                            handler: function () {
                                //未展开时添加会有异常,先展开
                                if (!(record.isLeaf() || record.isExpanded())) {
                                    record.expand();
                                }
                                Ext.addItemGroupWin.init(true, record);
                            }
                        }, {
                            text: '添加同级',
                            iconCls: 'Controladdblue',
                            disabled: !record.get('id'),
                            handler: function () {
                                Ext.addItemGroupWin.init(true, record.parentNode);
                            }
                        }, '-', {
                            text: '修改',
                            iconCls: 'Pageedit',
                            disabled: !record.get('id'),
                            handler: function () {
                                Ext.addItemGroupWin.init(false, record);
                            }
                        }, {
                            text: '删除',
                            iconCls: 'Delete',
                            disabled: !record.get('id'),
                            handler: function () {
                                Ext.Msg.confirm('提示', '确定删除?', function (btn) {
                                    if (btn == 'yes') {
                                        //发送请求
                                        Ext.Ajax.request({
                                            url: Global_Path + '/removeGroups',
                                            params: {
                                                id: record.get('id')
                                            },
                                            success: function (response, opts) {
                                                var obj = Ext.decode(response.responseText);
                                                Ext.Msg.alert('成功', obj.msg);
                                                record.remove();
                                                //清空表格内容
                                                infoStore.removeAll();
                                            },
                                            failure: function (response, opts) {
                                                Ext.Msg.alert('失败', '删除失败');
                                            }
                                        });
                                    }
                                });
                            }
                        }, '-', {
                            text: '添加指标',
                            iconCls: 'Pageadd',
                            disabled: !record.get('id'),
                            handler: function () {
                                Ext.addItemGroupInfoWin.init(record, null, function () {
                                    infoStore.reload({params: commonParams.infoParams});
                                });
                            }
                        }, {
                            text: '批量添加指标',
                            iconCls: 'Packageadd',
                            disabled: !record.get('id'),
                            handler: function () {
                                addItemGroupInfoBatch();
                            }
                        }]
                    });
                    menu.showAt(e.getPoint());
                },
                containercontextmenu: function () {
                    if (commonParams.treeMenu) {
                        commonParams.treeMenu.hide();
                    }
                }
            }
        });
        //分组信息
        var infoStore = new Ext.data.Store({
            fields: ['id', 'groupId', 'item', 'itemName', 'groupName', 'status', 'comments', 'caliberId', 'department', 'sortIndex'],
            proxy: {
                type: 'ajax',
                url: Global_Path + '/getInfosByGroup',
                extraParams: commonParams.infoParams
            },
            autoLoad: false
        });
        var infoContainerMenu = new Ext.menu.Menu({
            renderTo: Ext.getBody(),
            items: [{
                text: '添加指标',
                iconCls: 'Pageadd',
                handler: function () {
                    addItemGroupInfo();
                }
            }, {
                text: '批量添加指标',
                iconCls: 'Packageadd',
                handler: function () {
                    addItemGroupInfoBatch();
                }
            }]
        });
        var infoMenu = new Ext.menu.Menu({
            renderTo: Ext.getBody(),
            items: [{
                text: '修改',
                iconCls: 'Pageedit',
                handler: function () {
                    var sel = infoGrid.getSelectionModel().getSelection();
                    if (sel.length == 1) {
                        Ext.addItemGroupInfoWin.init(null, sel[0], function () {
                            infoStore.reload({params: commonParams.infoParams});
                        });
                    } else {
                        Ext.Msg.alert('警告', '请选中一个指标信息修改');
                    }
                }
            }, {
                text: '删除',
                iconCls: 'Delete',
                handler: function () {
                    var sel = infoGrid.getSelectionModel().getSelection();
                    if (sel.length) {
                        var ids = [];
                        for (var i = 0; i < sel.length; i++) {
                            ids.push(sel[i].get('id'));
                        }
                        Ext.Msg.confirm('警告', '确定删除?', function (btn) {
                            if (btn == 'yes') {
                                //发送请求删除
                                Ext.Ajax.request({
                                    url: Global_Path + '/removeItemGroupInfos',
                                    params: {
                                        ids: ids.join(',')
                                    },
                                    success: function (response, opts) {
                                        var obj = Ext.decode(response.responseText);
                                        Ext.Msg.alert('成功', obj.msg);
                                        //刷新
                                        infoStore.reload({params: commonParams.infoParams});
                                    },
                                    failure: function (response, opts) {
                                        Ext.Msg.alert('失败', "删除失败");
                                    }
                                });
                            }
                        })
                    }
                }
            }, '-', {
                text: '保存顺序',
                iconCls: 'Pagesave',
                handler: function () {
                    if (!commonParams.infoSorted) {
                        var datas = [];
                        var len = infoStore.getCount();
                        for (var i = 0; i < len; i++) {
                            datas.push(infoStore.getAt(i).get('id') + ':' + (i + 1));
                        }
                        //发送请求,保存顺序
                        Ext.Ajax.request({
                            url: Global_Path + '/saveItemGroupInfoSorts',
                            params: {
                                datas: datas.join(',')
                            },
                            success: function (response, opts) {
                                var obj = Ext.decode(response.responseText);
                                Ext.Msg.alert('成功', obj.msg);
                                commonParams.infoSorted = true;
                            },
                            failure: function (response, opts) {
                                Ext.Msg.alert('失败', "操作失败");
                            }
                        });
                    } else {
                        Ext.Msg.alert('提示', '无需保存顺序');
                    }
                }
            }]
        });
        var infoGrid = new Ext.grid.Panel({
            flex: 3,
            height: '100%',
            selType: 'checkboxmodel',
            store: infoStore,
            viewConfig: {
                plugins: {
                    ptype: 'gridviewdragdrop',
                    ddGroup: 'infoSortGroup'
                },
                listeners: {
                    beforedrop: function () {
                        var infoParams = commonParams.infoParams;
                        if (infoParams.includeDownLevel || infoParams.itemName) {
                            Ext.Msg.alert('提示', '搜索时不支持排序!');
                            return false;
                        }
                        //将caliberSorted设为false
                        commonParams.infoSorted = false;
                    }
                }
            },
            columns: [{
                text: '名称',
                dataIndex: 'itemName',
                flex: 2
            }, {
                text: '分组名',
                dataIndex: 'groupName',
                flex: 1
            }, {
                text: '口径',
                dataIndex: 'caliberId',
                flex: 1,
                renderer: function (data, m, record) {
                    var calibers = record.get('item').itemCalibers;
                    if (calibers && calibers.length) {
                        for (var i = 0; i < calibers.length; i++) {
                            if (data == calibers[i].id) {
                                return calibers[i].name;
                            }
                        }
                    }
                    return '';
                }
            }, {
                text: '部门',
                dataIndex: 'department',
                flex: 1,
                renderer: function (data) {
                    if (data) {
                        return data.depName;
                    }
                    return '';
                }
            }, {
                text: '状态',
                xtype: 'booleancolumn',
                dataIndex: 'status',
                trueText: '启用',
                falseText: '废弃',
                flex: 1
            }, {
                text: '备注',
                dataIndex: 'comments',
                flex: 2
            }],
            tbar: [{
                xtype: 'checkbox',
                fieldLabel: '包含下级',
                labelWidth: 70,
                labelAlign: 'right',
                value: !!commonParams.infoParams.includeDownLevel,
                listeners: {
                    change: function (_this, n, o) {
                        commonParams.infoParams.includeDownLevel = n;
                        infoStore.reload({params: commonParams.infoParams});
                    }
                }
            }, {
                xtype: 'textfield',
                fieldLabel: '搜索',
                labelWidth: 50,
                width: 150,
                labelAlign: 'right',
                listeners: {
                    change: function (_this, n, o) {
                        commonParams.infoParams.itemName = n;
                        infoStore.reload({params: commonParams.infoParams});
                    }
                }
            }, {
                xtype: 'combobox',
                fieldLabel: '状态',
                labelWidth: 50,
                width: 120,
                labelAlign: 'right',
                displayField: 'text',
                valueField: 'value',
                store: new Ext.data.Store({
                    fields: ['text', 'value'],
                    data: [{text: '全部', value: null}, {text: '启用', value: 1}, {text: '废弃', value: 0}]
                }),
                value: commonParams.infoParams.status,
                listeners: {
                    change: function (_this, n, o) {
                        commonParams.infoParams.status = n;
                        infoStore.reload({params: commonParams.infoParams});
                    }
                }
            }, '->', {
                xtype: 'button',
                text: '添加指标',
                iconCls: 'Add',
                handler: function () {
                    addItemGroupInfo();
                }
            }, {
                xtype: 'button',
                iconCls: 'Packageadd',
                text: '批量添加指标',
                handler: function () {
                    addItemGroupInfoBatch();
                }
            }],
            listeners: {
                containercontextmenu: function (_this, e) {
                    //取消冒泡
                    e.preventDefault();
                    //弹出菜单
                    infoContainerMenu.showAt(e.getPoint());
                },
                itemcontextmenu: function (_this, record, itemId, index, e) {
                    //取消冒泡
                    e.preventDefault();
                    //弹出菜单
                    infoMenu.showAt(e.getPoint());
                },
                containerclick: function () {
                    infoContainerMenu.hide();
                    infoMenu.hide();
                },
                cellclick: function () {
                    infoContainerMenu.hide();
                    infoMenu.hide();
                }
            }
        });
        //容器
        new Ext.resizablePanel({
            width: '100%',
            height: '100%',
            renderTo: 'itemGroupContainer',
            layout: 'hbox',
            items: [groupTree, infoGrid]
        });

        //添加指标
        function addItemGroupInfo() {
            var sel = groupTree.getSelectionModel().getSelection();
            if (sel.length) {
                var record = sel[0];
                if (record.get('id')) {
                    Ext.addItemGroupInfoWin.init(record, null, function () {
                        infoStore.reload({params: commonParams.infoParams});
                    });
                } else {
                    Ext.Msg.alert('警告', '不能再根节点下添加指标');
                }
            } else {
                Ext.Msg.alert('提示', '请先选择一个分组来添加');
            }g
        }

        //批量添加指标
        function addItemGroupInfoBatch() {
            var sel = groupTree.getSelectionModel().getSelection();
            if (sel.length) {
                var record = sel[0];
                if (record.get('id')) {
                    Ext.addItemGroupInfoBatchWin.init(record, function () {
                        infoStore.reload({params: commonParams.infoParams});
                    });
                } else {
                    Ext.Msg.alert('警告', '不能再根节点下添加指标');
                }
            } else {
                Ext.Msg.alert('提示', '请先选择一个分组来添加');
            }
        }

    });
</script>
</body>
</html>
