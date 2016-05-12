<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 2015/12/31
  Time: 11:24
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
    <jsp:include page="/WEB-INF/pages/common/sysConstant.jsp"></jsp:include>
</head>
<body>
<script type="text/javascript" src="<%=request.getContextPath()%>/City/support/sys/dep/addDepWin.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/City/support/sys/dep/addPersonWin.js"></script>
<div id="depConfig" style="width: 100%;height: 100%"></div>
<script type="text/javascript">
    Ext.onReady(function () {

        function addDep() {
            var nodeModel = depTree.getSelectionModel();
            var nodes = nodeModel.getSelection();
            var node = null;
            if (nodes != null && nodes.length > 0) {
                //获取选中的部门
                node = nodes[0]
            }
            if (node != null) {
                var win = Ext.addDepWin.init(node.data.id, function (data) {
                    if (node.lastChild)
                        data.sort = node.lastChild.data.index + 1;
                    else
                        data.sort = 0
                    Ext.Ajax.request({
                        url: GLOBAL_PATH + "/support/sys/dep/addDep",
                        method: 'POST',
                        params: data,
                        success: function (response, opts) {
                            var obj = Ext.decode(response.responseText);
                            if (obj.success) {
                                var data = obj.datas;
                                data.leaf = true;
                                depTree.expandNode(node)
                                node.appendChild(data);
                            }
                        }
                    })
                    win.close();
                });
            } else {
                Ext.Msg.alert('注意', '请选择上级部门');
            }

        };
        function delDep() {
            var nodeModel = depTree.getSelectionModel();
            var nodes = nodeModel.getSelection();
            var node = null;
            if (nodes != null && nodes.length > 0) {
                node = nodes[0];
                var parentNode = node.parentNode;
                if (node.getId() != 0) {
                    Ext.Msg.confirm('注意', '是否要刪除该部门及其下级部门', function (btnStr) {
                        if (btnStr == 'yes') {
                            var pNode = node.parentNode;
                            pNode.removeChild(node);
                            node.pNode = pNode;
                            Ext.Array.each(pNode.childNodes, function (childNode) {
                                childNode.set('sort', childNode.data.index);
                            });
                            treeStore.sync();
                        }
                    });
                } else {
                    Ext.Msg.alert('注意', '该节点不能删除');
                }
            } else {
                Ext.Msg.alert('注意', '请选择要删除的部门');
            }
        }

        var depModel = createModel('Dep', function () {
            Ext.define('Dep', {
                extend: 'Ext.data.Model',
                idProperty: 'id',
                fields: [
                    {name: 'id'},
                    {name: 'depName', type: 'string'},
                    {name: 'depShortName', type: 'string'},
                    {name: 'depLevel', type: 'int'},
                    {name: 'depFax', type: 'string'},
                    {name: 'depEmail', type: 'string'},
                    {name: 'pDep', type: 'int'},
                    {name: 'text', type: 'string'},
                    {name: 'sort', type: 'int'}
                ]
            });
        });
        var treeStore = Ext.create('Ext.data.TreeStore', {
            autoLoad: true,
            model: 'Dep',
            parentIdProperty:'pDep',
            proxy: {
                type: 'ajax',
                api: {
                    update: GLOBAL_PATH + '/support/sys/dep/updateDep',
                    destroy: GLOBAL_PATH + '/support/sys/dep/removeDep'
                },
                url: GLOBAL_PATH + '/support/sys/dep/queryAllDep',
                reader: {
                    messageProperty: 'msg',
                    type: 'json'
                },
                listeners: {
                    exception: function (proxy, response, operation) {

                        Ext.Array.each(operation._records, function (curnode) {
                            if (curnode) {
                                curnode.reject();
                                if (operation.action == 'destroy') {
                                    if (curnode.parentNode == null) {
                                        curnode.pNode.insertChild(curnode.data.index, curnode);
                                    }
                                }
                            }
                        });

                        Ext.Msg.alert({
                            title: '错误',
                            msg: operation.error, //proxy.reader.getMessage(),
                        });
                        return false;
                    }
                }
            },
            root: {
                id: 0,
                depName: '组织机构'
            },
            rootVisible: true,
            listeners: {
                write: function (store, operate, callback) {
                    Ext.Msg.alert('提示', operate._resultSet.message);
                }
            }
        });
        var depMenu = Ext.create('Ext.menu.Menu', {
            width: 100,
//            floating: false,  // usually you want this set to True (default)
            renderTo: Ext.getBody(),  // usually rendered by it's containing component
            items: [{
                text: '添加下级部门',
                handler: addDep
            }, {
                text: '删除部门',
                handler: delDep
            }]
        });
        var depTree = new Ext.tree.Panel({
            displayField: 'depName',
            region: 'west',
            width: '20%',
            height: '100%',
            store: treeStore,
            rootVisible: true,
            autoScroll: true,
            viewConfig: {
                plugins: {
                    ptype: 'treeviewdragdrop',
                    dragZone: {
                        afterDragOver: function (zone) {
                            var rec = zone.overRecord
                            if (rec) {
                                rec.set('loaded', true);
                                rec.set('leaf', false);
                            }

                        }
                    }
                },
                listeners: {
                    drop: function (node, data, overModel, dropPosition, eOpts) {
                        var dragNode = data.records[0];
                        var pNode = dragNode.parentNode;
                        dragNode.set('pDep', pNode.getId());
                        Ext.Array.each(pNode.childNodes, function (child) {
                            child.set('sort', child.data.index);
                        });
                        treeStore.sync();
                    }
                    /*drop: function (nodeView, dragEvent, overModel, dropPosition, eOpts) {
                        /!*var node = dragEvent.records[0];
                        var isLeaf = !overModel.hasChildNodes();
                        var isRoot = overModel.isRoot();
                        var bnodes = null;
                        var pId = null;
                        if (isLeaf) {
                            bnodes = overModel.parentNode.childNodes;
                            pId = overModel.parentNode.getId();
                        } else {
                            bnodes = overModel.childNodes;
                            pId = overModel.getId();
                        }
                        node.set('pDep',pId);
                        Ext.Array.each(bnodes, function (cnode) {
                            cnode.set('sort', cnode.data.index);

                        });
                        treeStore.sync();*!/
                    }*/
                }
            },
            tbar: [
                {
                    id: 'triggerDep',
                    xtype: 'triggerfield',
                    width: '100%',
                    emptyText: '搜索部门',
                    triggerCls: 'x-form-clear-trigger',
                    onTriggerClick: function () {
                        this.reset();
                    },
                    listeners: {
                        change: function () {
                            depTree.collapseAll();
                            var depFilter = new Ext.util.Filter({
                                filterFn: function (node) {
                                    var str = depTree.down('#triggerDep').getValue();
                                    var reg = new RegExp(str, 'i');

                                    var visible = reg.test(node.get('depName'));
                                    if (visible && str != '') {
                                        depTree.expandNode(node.parentNode, false)
                                    }
                                    var children = node.childNodes;
                                    var len = children && children.length;
                                    for (var i = 0; i < len; i++) {
                                        if (children[i].get('visible')) {
                                            visible = children[i].get('visible');
                                            break;
                                        }
                                    }
                                    return visible;
                                }
                            });
                            treeStore.filter(depFilter);
                            treeStore.filters.clear();
                        }
                    }
                }
            ],
            listeners: {
                itemclick: function (_this, record, item, index, e, eOpts) {
                    if (record.get('pDep') != -1)
                        depForm.loadRecord(record)
                    personStore.depId = record.getId();
                    personStore.load({params: {depId: record.getId()}})
                    if (depMenu) {
                        depMenu.hide();
                    }
                },
                containerclick: function () {
                    if (depMenu) {
                        depMenu.hide();
                    }
                },
                itemcontextmenu: function (_this, record, itemId, index, e) {
                    e.preventDefault();
                    depMenu.showAt(e.getPoint());
                }
            }
        });
        //部门人员model
        var personModel = createModel('Person', function () {
            Ext.define('Person', {
                extend: 'Ext.data.Model',
                idProperty: 'id',
                fields: [
                    {name: 'id', type: 'int'},
                    {name: 'name', type: 'string'},
                    {name: 'duty', type: 'int'},
                    {name: 'connect', type: 'string'},
                    {name: 'email', type: 'string'},
                    {name: 'tel', type: 'string'},
                    {name: 'personInfo', type: 'string'},
                    {name: 'depId', type: 'int'}
                ]
            });
        });
        //部门人员store
        var personStore = new Ext.data.Store({
            model: 'Person',
            proxy: {
                type: 'ajax',
//                url: GLOBAL_PATH + '/support/sys/person/queryPerson',
                actionMethods: {create: 'POST', read: 'POST', update: 'POST', destroy: 'POST'},
                api: {
//                    create  : '/controller/new',
                    read: GLOBAL_PATH + '/support/sys/person/queryPerson',
                    update: GLOBAL_PATH + '/support/sys/person/updatePerson',
                    destroy: GLOBAL_PATH + '/support/sys/person/delPerson'
                },
                reader: {
                    type: 'json',
                    rootProperty: 'datas',
                    messageProperty: 'msg'
                }
            },
            autoLoad: false
        });
        //部门人员表格panel
        var personGrid = new Ext.grid.Panel({
            xtype: 'gridpanel',
            id: 'personGrid',
            tbar: ['->', {
                xtype: 'button', text: '添加人员', handler: function () {
                    if (personStore.depId) {
                        var win = Ext.addPersonWin.init(function (data) {
                            Ext.Ajax.request({
                                url: GLOBAL_PATH + "/support/sys/person/addPerson",
                                method: 'POST',
                                jsonData: data,
                                params: {depId: personStore.depId},
                                success: function (response, opts) {
                                    var result = Ext.JSON.decode(response.responseText);
                                    personStore.add(result.datas[0]);
                                }
                            })
                            win.close();
                        }, true);
                    }
                }
            }, {
                xtype: 'button', text: '修改人员', handler: function () {

                    var rec = personGrid.getSelection()[0];
                    if (rec) {
                        var win = Ext.addPersonWin.init(function (data) {
                            var rec = personGrid.getSelection()[0];
                            rec.set('name', data.name);
                            personStore.sync();
                        }, false, personGrid.getSelection()[0]);
                        personStore.sync();
                    }
                }
            }, {
                xtype: 'button', text: '删除人员', handler: function () {
                    var rec = personGrid.getSelection()[0];
                    if (rec) {
                        personStore.remove(rec)
                        personStore.sync();
                    }
                }
            }],
            margin: '10 0',
            height: '100%',
            flex: 4,
            width: '100%',
            title: '部门人员',
            store: personStore,
            columns: [
                {text: '人员姓名', dataIndex: 'name', flex: 1},
//                {text: 'Email', dataIndex: 'email', flex: 1},
                {text: '联系电话', dataIndex: 'tel', flex: 1}
            ],
        });

        //部门修改表单
        var depForm = new Ext.form.Panel({
            region: 'center',
            height: '100%',
            layout: {
                type: 'vbox',
                align: 'right'
            },
            bodyPadding: '5 5 0',
            width: '80%',
            items: [{
                xtype: 'fieldset',
                flex: 2,
                width: '100%',
                height: 200,
                title: '部门信息', // title or checkboxToggle creates fieldset header
                defaults: {
                    border: false,
                    width: '100%',
                    layout: 'column'
                },
                items: [{
                    xtype: 'panel',
                    defaults: {
                        xtype: 'textfield',
                        columnWidth: 1 / 2,
                        margin: '5 20',
                        labelAlign: 'right',
                        labelWidth: 80
                    },
                    items: [{
                        xtype: 'hidden',
                        fieldLabel: '部门id',
                        name: 'id'
                    }, {
                        fieldLabel: '部门名称',
                        allowBlank: false,
                        name: 'depName'
                    }, {
                        title: 'Column 2',
                        fieldLabel: '部门简称',
                        name: 'depShortName'
                    }]
                }, {
                    xtype: 'panel',
                    defaults: {
                        xtype: 'textfield',
                        columnWidth: 1 / 2,
                        margin: '5 20',
                        labelAlign: 'right',
                        labelWidth: 80
                    },
                    items: [{
                        fieldLabel: '部门级别',
                        name: 'depLevel'
                    }, {
                        fieldLabel: '部门电话'
                    }]
                }, {
                    xtype: 'panel',
                    defaults: {
                        xtype: 'textfield',
                        columnWidth: 1 / 2,
                        margin: '5 20',
                        labelAlign: 'right',
                        labelWidth: 80
                    },
                    items: [{
                        fieldLabel: '部门传真',
                        name: 'depFax'
                    }, {
                        fieldLabel: '部门邮箱',
                        name: 'depEmail'
                    }]
                }]
            }, {
                xtype: 'button',
                width: 100,
                text: '保存',
                handler: function () {
                    var nodeModel = depTree.getSelectionModel();
                    var nodes = nodeModel.getSelection();
                    var node = null;
                    if (nodes != null && nodes.length > 0) {
                        var datas = depForm.getValues();
                        node = nodes[0];
                        if (node.id != 0) {
                            node.set('depName', datas.depName);
                            node.set('depShortName', datas.depShortName);
                            node.set('depShortName', datas.depShortName);
                            node.set('depFax', datas.depFax);
                            node.set('depEmail', datas.depEmail);
                            treeStore.sync();
                        } else {
                            Ext.Msg.alert('注意', '该节点不能修改');
                        }
                    }
                }
            }, personGrid]
        });
        //部门管理配置
        depConfigPanel = new Ext.panel.Panel({
            renderTo: 'depConfig',
            layout: 'border',
            height: '100%',
            width: '100%',
            tbar: [{
                xtype: 'button',
                text: '添加部门',
                handler: addDep
            }, {
                xtype: 'button',
                text: '删除部门',
                handler: delDep
            }],
            items: [depTree, depForm],
            listeners: {
                render: function () {//初始化，监听窗口resize事件
                    if (indexPanel) {
                        var tabPanel = indexPanel.down('#tabCenter');
                        var myTab = tabPanel.getActiveTab();
                        if (myTab)
                            myTab.myPanel = this;
                    }
                    if (this.hasListener('reDR'))
                        this.un('reDR');
                    this.on('reDR', function (Obj) {
                        if (Obj)
                            this.updateBox(Obj)
                    });
                },
                close: function (closeAction) {
                    if (closeAction) {
                        this.destroy();
                    }
                }
            }
        });
    });
</script>
</body>
</html>
