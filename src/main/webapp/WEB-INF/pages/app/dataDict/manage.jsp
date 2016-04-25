<%--
  Created by IntelliJ IDEA.
  User: wxl
  Date: 2016/3/21
  Time: 15:22
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="en">
<head>
    <title>数据字典管理</title>
    <meta charset="UTF-8"/>
    <style>
        html, body, #app_datadict_container {
            height: 100%;
        }

        .x-tree-icon {
            background-size: 16px;
        }
    </style>
    <jsp:include page="../../common/appDataDictImp.jsp"></jsp:include>
    <script>
        var APP_DATADICT_PATH = '<%=request.getContextPath()%>/app/dataDict';
    </script>
</head>
<body>
<div id="app_datadict_container"></div>
<script>
    Ext.onReady(function () {
        var menuParams = {
            name: '',
            status: -1
        };
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
        var menuStore = new Ext.data.TreeStore({
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
        var menu;//目录
        var menuTree = new Ext.tree.Panel({
            store: menuStore,
            displayField: 'name',
            flex: 1,
            height: '100%',
            tbar: [{
                xtype: 'triggertext',
                width: 120,
                handler: function (_this, n) {
//                    queryTreeByLocal(menuTree, menuStore, 'name', n);
                    menuParams.name = n;
                    queryStatusTreeByLocal(menuParams.name, menuParams.status);
                }
            }, '->', {
                xtype: 'combobox',
                width: 80,
                displayField: 'name',
                valueField: 'id',
                store: new Ext.data.Store({
                    fields: ['name', 'id'],
                    data: [{id: -1, name: '全部'}, {id: 0, name: '操作库'}, {id: 1, name: '结果库'}]
                }),
                editable: false,
                value: -1,
                listeners: {
//                    render: function (_this) {
//                        _this.select(_this.getStore().getAt(0));
//                    },
                    change: function (_this, n) {
                        console.log(n);
                        menuParams.status = n;
                        queryStatusTreeByLocal(menuParams.name, menuParams.status);
                    }
                }
            }],
            viewConfig: {
                allowCopy: true,
                plugins: {
                    ptype: 'treeviewdragdrop',
                    dragZone: {
                        afterDragOver: function (zone) {
                            var rec = zone.overRecord;
                            if (rec) {
                                rec.set('loaded', true);
                                rec.set('leaf', false);
                            }
                        }
                    }
                },
                listeners: {
                    drop: function (node, data) {
                        if (!data.copy) {//移动节点
                            var dragNode = data.records[0];
                            var pNode = dragNode.parentNode;
                            dragNode.set('parentId', pNode.getId());
                            Ext.Array.each(pNode.childNodes, function (child) {
                                child.set('sortIndex', child.data.index);
                            });
                            menuStore.sync();
                        }
                    },
                    beforedrop: function (node, data, overModel, dropPosition, dropHandlers) {
                        if (data.copy) {//复制节点,使用不同逻辑
                            dropHandlers.cancelDrop();
                            var fromId = data.records[0].get('id');
                            var toId;
                            var sortIndex = null;
                            if (dropPosition == 'append') {
                                toId = overModel.get('id');
                            } else {
                                toId = overModel.get('parentId');
                                sortIndex = overModel.get('sortIndex');
                            }
                            //发送请求
                            Ext.Ajax.request({
                                url: APP_DATADICT_PATH + '/copyDictMenus',
                                params: {
                                    fromId: fromId,
                                    toId: toId,
                                    sortIndex: sortIndex
                                },
                                waitMsg: '正在操作...',
                                success: function (response) {
                                    var obj = Ext.decode(response.responseText);
                                    Ext.Msg.alert('提示', obj.msg);
                                    //更新节点
                                    menuStore.reload();
                                },
                                failure: function () {
                                    Ext.Msg.alert('提示', "保存失败,请求异常!");
                                }
                            });
                        }
                    }
                }
            },
            listeners: {
                itemcontextmenu: function (_this, record, item, index, e) {
                    e.preventDefault();
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
                            hidden: record.get('id') == 0,
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
                            hidden: record.get('id') == 0,
                            iconCls: 'Pageedit',
                            handler: function () {
                                Ext.dataDict.EditDataDictWin.init(null, record, function (data) {
                                    delete(data.leaf);//不需要更新树节点的状态信息
                                    record.set(data, {dirty: false});
                                });
                            }
                        }, {
                            text: '删除',
                            hidden: record.get('id') == 0,
                            iconCls: 'Delete',
                            handler: function () {
                                Ext.Msg.confirm('提示', '确定删除?', function (btn) {
                                    if (btn == 'yes') {
                                        record.remove();
                                        menuStore.sync();
                                    }
                                });
                            }
                        }]
                    });
                    menu.showAt(e.getXY());
                },
                itemclick: function (_this, record) {
                    if (menu) {//隐藏菜单
                        menu.hide();
                    }
                    //加载表格
                    gridParams.menuId = record.get('id');
                    reloadGrid();
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
            store: dictStore,
            flex: 3,
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
                    drop: function (node, data) {
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
            tbar: ['<b>移动数据</b>', {
                xtype: 'checkbox',
                boxLabel: '包含下级',
                handler: function (_this, checked) {
                    gridParams.includeDownLevel = checked;
                    reloadGrid();
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
                        gridParams.status = n.status;
                        reloadGrid();
                    }
                }
            }, {
                xtype: 'triggertext',
                width: 110,
                emptyText: '请输入名称检索',
                handler: function (_this, n) {
                    gridParams.name = n;
                    reloadGrid();
                }
            }, '->', {
                text: '预览',
                iconCls: 'Zoom',
                handler: function () {
                    var sel = menuTree.getSelectionModel().getSelection();
                    //获取内容
                    if (sel.length) {
                        var menuRecord = sel[0];
                        var menuId = menuRecord.get('id');
                        var name= menuRecord.get('name');
                        //获取属于这个的数据
                        var count = dictStore.getCount();
                        var flag = false;
                        if (count) {
                            for (var i = 0; i < count; i++) {
                                var record = dictStore.getAt(i);
                                if (record.get('menuId') == menuId) {
                                    flag = true;
                                    break;
                                }
                            }
                        }
                        if (flag) {
                            //此处预览
                            open(APP_DATADICT_PATH + '/previewPage?menuId=' + menuId+'&name='+name);
                        } else {
                            Ext.Msg.alert('提示', '无内容不可预览o(╯□╰)o');
                        }
                    } else {
                        Ext.Msg.alert('提示', '请选择目录来预览页面!');
                    }
                }
            }, {
                text: '发布',
                iconCls: 'Transmit',
                handler: function () {
                    var sel = menuTree.getSelectionModel().getSelection();
                    if (sel.length) {
                        var record = sel[0];
                        if (record.get('status') == 1) {
                            Ext.Msg.alert('提示', '该目录已发布,无需再次发布!');
                        } else {
                            var menuId = record.get('id');
                            if (menuId == 0) {
                                Ext.Msg.alert('提示', '根目录不可发布!');
                            } else {
                                record.set('status', 1);
                                menuStore.sync({
                                    callback: function () {
                                        Ext.Msg.alert('提示', '发布成功');
                                    }
                                });
                            }
                        }
                    } else {
                        Ext.Msg.alert('提示', '请选择目录来发布!');
                    }
                }
            }, '-', {
                text: '添加',
                iconCls: 'Add',
                handler: function () {
                    if (gridParams.menuId) {
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
                    dictMenu.showAt(e.getXY());
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
        //布局
        new Ext.resizablePanel({
            width: '100%',
            height: '100%',
            layout: 'hbox',
            items: [menuTree, dictGrid],
            renderTo: 'app_datadict_container'
        });

        /**
         * 重新加载表格
         */
        function reloadGrid() {
            dictStore.reload({params: gridParams});
        }

        /**
         * 树的本地查询方法,将方法在文本框的change事件绑定即可
         * @param queryStr 查询的字符串
         * @param status 状态
         */
        function queryStatusTreeByLocal(queryStr, status) {
            menuTree.collapseAll();
            var depFilter = new Ext.util.Filter({
                filterFn: function (node) {
                    var reg = new RegExp(queryStr, 'i');
                    var statusEq = status == -1 || status == node.get('status');
                    var visible = reg.test(node.get('name')) && statusEq;
                    if (visible && queryStr != '') {
                        menuTree.expandNode(node.parentNode, false)
                    }
                    var children = node.childNodes;
                    var len = children && children.length;
                    for (var i = 0; i < len; i++) {
                        if (children[i].get('visible')) {
                            visible = true;
                            break;
                        }
                    }
                    return visible;
                }
            });
            menuStore.filter(depFilter);
            menuStore.filters.clear();
        }
    });
</script>
<script src="<%=request.getContextPath()%>/City/common/queryTreeByLocal.js"></script>
<script src="<%=request.getContextPath()%>/City/app/dataDict/editDataDictMenuWin.js"></script>
<script src="<%=request.getContextPath()%>/City/app/dataDict/editDataDictInfoWin.js"></script>
</body>
</html>
