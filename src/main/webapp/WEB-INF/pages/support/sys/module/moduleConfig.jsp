<%--
  Created by IntelliJ IDEA.
  User: wgx
  Date: 2016/1/21
  Time: 9:46
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<html>
<head>
    <title>模块管理</title>
    <jsp:include page="/WEB-INF/pages/common/sysConstant.jsp"></jsp:include>
</head>
<body>
<script type="text/javascript" src="<%=request.getContextPath()%>/City/support/sys/module/addModuleWin.js"></script>
<div id="moduleConfig" style="width: 100%;height: 100%"></div>
<script>
    Ext.onReady(function () {

        function addModule() {

            var nodeModel = moduleTree.getSelectionModel();
            var nodes = nodeModel.getSelection();

            var node = null;
            if (nodes != null && nodes.length > 0) {
                //获取选中的模块
                node = nodes[0]
            }
            if (node != null) {
                var win = Ext.addModuleWin.init(node.data.id, function (data) {
                    if (node.lastChild) {
                        data.moduleSort = node.lastChild.data.index + 1;
                    } else {
                        data.moduleSort = 0
                    }
                    Ext.Ajax.request({
                        url: GLOBAL_PATH + "/support/sys/module/addModule",
                        method: 'POST',
                        params: data,
                        success: function (response, opts) {
                            var obj = Ext.decode(response.responseText);
                            if (obj.success) {
                                var data = obj.datas;
                                data.leaf = true;
                                moduleTree.expandNode(node)
                                node.appendChild(data);
                            }
                        }
                    })
                    win.close();
                });
            } else {
                Ext.Msg.alert('注意', '请选择上级模块');
            }

        };
        function delModule() {
            var nodeModel = moduleTree.getSelectionModel();
            var nodes = nodeModel.getSelection();
            var node = null;
            if (nodes != null && nodes.length > 0) {
                node = nodes[0];
                var parentNode = node.parentNode;
                if (node.getId() != 0) {
                    Ext.Msg.confirm('注意', '是否要刪除该模块及其下级模块', function (btnStr) {
                        if (btnStr == 'yes') {
                            var pNode = node.parentNode;
                            pNode.removeChild(node);
                            node.pNode = pNode;
                            Ext.Array.each(pNode.childNodes, function (childNode) {
                                childNode.set('moduleSort', childNode.data.index);
                            });
                            moduleStore.sync();
                        }
                    });
                } else {
                    Ext.Msg.alert('注意', '该节点不能删除');
                }
            } else {
                Ext.Msg.alert('注意', '请选择要删除的模块');
            }
        }

        //model
        var moduleModel = createModel('ModuleModel', function () {
            Ext.define('ModuleModel', {
                extend: 'Ext.data.Model',
                idProperty: 'id',
                fields: [{
                    name: 'id'
                }, {
                    name: 'moduleName',
                    type: 'string'
                }, {
                    name: 'text',
                    type: 'string'
                }, {
                    name: 'moduleShortName',
                    type: 'string'
                }, {
                    name: 'moduleType',
                    type: 'string'
                }, {
                    name: 'moduleIndex',
                    type: 'string'
                }, {
                    name: 'moduleConfig',
                    type: 'string'
                }, {
                    name: 'moduleParams',
                    type: 'string'
                }, {
                    name: 'moduleIcon',
                    type: 'string'
                }, {
                    name: 'modulePic',
                    type: 'string'
                }, {
                    name: 'moduleDesc',
                    type: 'string'
                }, {
                    name: 'moduleComment',
                    type: 'string'
                }, {
                    name: 'moduleSort',
                    type: 'int'
                }, {
                    name: 'modulePid',
                    type: 'int'
                }, {
                    name: 'moduleState',
                    type: 'int'
                }]
            })
        });
        //store
        var moduleStore = Ext.create('Ext.data.TreeStore', {
            model: 'ModuleModel',
            proxy: {
                type: 'ajax',
                api: {
                    read: GLOBAL_PATH + '/support/sys/module/queryAllModuleTree',
                    update: GLOBAL_PATH + '/support/sys/module/updateModule',
                    destroy: GLOBAL_PATH + '/support/sys/module/removeModule'
                },
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
                id: MODULE_TYPE.ROOT
            },
            rootVisible: false,
            autoLoad: true,
            listeners: {
                write: function (store, operate, callback) {
                    Ext.Msg.alert('提示', operate._resultSet.message);
                }
            }
        });
        //右键菜单
        var moduleMenu = Ext.create('Ext.menu.Menu', {
            width: 100,
            renderTo: Ext.getBody(),
            items: [{
                text: '添加下级模块',
                handler: addModule
            }, {
                text: '删除模块',
                handler: delModule
            }]
        });
        var moduleTree = new Ext.tree.Panel({
            region: 'west',
            width: '20%',
            //height: '100%',
            store: moduleStore,
            displayField: 'moduleName',
            rootVisible: false,
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
                allowParentInsert: true,
                listeners: {
                    drop: function (node, data, overModel, dropPosition, eOpts) {
                        var dragNode = data.records[0];
                        var pNode = dragNode.parentNode;
                        dragNode.set('modulePid', pNode.getId());
                        Ext.Array.each(pNode.childNodes, function (child) {
                            child.set('moduleSort', child.data.index);
                        });
                        moduleStore.sync();
                    }
                    /*drop: function (nodeView, dragEvent, overModel, dropPosition, eOpts) {
                     var node = dragEvent.records[0];
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
                     node.set('modulePid', pId);
                     Ext.Array.each(bnodes, function (cnode) {
                     cnode.set('moduleSort', cnode.data.index);
                     });
                     moduleStore.sync();
                     }*/
                }
            },
            tbar: [
                {
                    id: 'triggerModule',
                    xtype: 'triggerfield',
                    width: '100%',
                    emptyText: '搜索模块',
                    triggerCls: 'x-form-clear-trigger',
                    onTriggerClick: function () {
                        this.reset();
                    },
                    listeners: {
                        change: function () {
                            moduleTree.collapseAll();
                            var moduleFilter = new Ext.util.Filter({
                                filterFn: function (node) {
                                    var str = moduleTree.down('#triggerModule').getValue();
                                    var reg = new RegExp(str, 'i');
                                    var reg = new RegExp(str, 'i');

                                    var visible = reg.test(node.get('moduleName'));
                                    if (visible && str != '') {
                                        moduleTree.expandNode(node.parentNode, false)
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
                            moduleStore.filter(moduleFilter);
                            moduleStore.filters.clear();
                        }
                    }
                }
            ],
            listeners: {//点击行触发事件
                itemclick: function (_this, record, item, index, e, eOpts) {
                    if (record.get('modulePid') != -1) {
                        moduleForm.loadRecord(record)
                    }
                    if (moduleMenu) {
                        moduleMenu.hide();
                    }
                },
                containerclick: function () {
                    if (moduleMenu) {
                        moduleMenu.hide();
                    }
                },
                itemcontextmenu: function (_this, record, itemId, index, e) {
                    e.preventDefault();
                    moduleMenu.showAt(e.getPoint());
                }
            }
        });
        var moduleStateGroup = Ext.create('Ext.form.RadioGroup', {
            fieldLabel: '模块状态',
            labelAlign: 'right',
            columnWidth: .5,
            items: [{
                name: 'moduleState',
                inputValue: '1',
                boxLabel: '启用',
                checked: true
            }, {
                name: 'moduleState',
                inputValue: '0',
                boxLabel: '禁用'
            }]
        });
        //模块修改表单
        var moduleForm = new Ext.form.Panel({
            region: 'center',
            frame: false,
            autoWidth: true,
            height: '100%',
            layout: {
                type: 'vbox',
                align: 'right'
            },
            width: '100%',
            items: [{
                xtype: 'fieldset',
                width: '100%',
                margin: '10 10 0 10',
                padding: '0 0 20 0',
                title: '模块信息', // title or checkboxToggle creates fieldset header
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
                        fieldLabel: '模块id',
                        name: 'id'
                    }, {
                        fieldLabel: '模块名称',
                        allowBlank: false,
                        name: 'moduleName'
                    }, {
                        fieldLabel: '模块简称',
                        name: 'moduleShortName'
                    }]
                }, {
                    xtype: 'panel',
                    defaults: {
                        columnWidth: 1 / 2,
                        margin: '5 20',
                        labelAlign: 'right',
                        labelWidth: 80
                    },
                    items: [moduleStateGroup, {
                        xtype: 'combobox',
                        name: 'moduleType',
                        fieldLabel: '模块类型',
                        displayField: 'text',
                        valueField: 'value',
                        store: new Ext.data.Store({
                            fields: ['text', 'value'],
                            data: [{
                                text: '系统',
                                value: MODULE_TYPE.SYSMOD
                            }, {
                                text: '模块',
                                value: MODULE_TYPE.MODMOD
                            }, {
                                text: '功能',
                                value: MODULE_TYPE.FUNMOD
                            }, {
                                text: '操作',
                                value: MODULE_TYPE.OPMOD
                            }, {
                                text: '目录',
                                value: MODULE_TYPE.DIRMOD
                            }]
                        }),
                        value: MODULE_TYPE.MODMOD,
                        columnWidth: 0.5,
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
                        fieldLabel: '模块首页',
                        name: 'moduleIndex'
                    }, {
                        fieldLabel: '配置页面',
                        name: 'moduleConfig'
                    }, {
                        fieldLabel: '模块参数',
                        name: 'moduleParams'
                    }, {
                        fieldLabel: '模块描述',
                        name: 'moduleDesc'
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
                        fieldLabel: '模块图标',
                        name: 'moduleIcon'
                    }, {
                        fieldLabel: '模块图片',
                        name: 'modulePic'
                    }]
                }, {
                    xtype: 'panel',
                    defaults: {
                        xtype: 'textarea',
                        columnWidth: 1,
                        margin: '5 20',
                        labelAlign: 'right',
                        labelWidth: 80
                    },
                    items: [{
                        fieldLabel: '备注',
                        name: 'moduleComment'
                    }]
                }]
            }, {
                xtype: 'panel',
                frame: false,
                border: false,
                padding: '0 10 0 0',
                items: [{
                    xtype: 'displayfield',
                    border: false,
                    columnWidth: .92
                }, {
                    xtype: 'button',
                    width: 100,
                    columnWith: .08,
                    text: '保存',
                    handler: function () {
                        var nodeModel = moduleTree.getSelectionModel();
                        var nodes = nodeModel.getSelection();
                        var node = null;
                        if (nodes != null && nodes.length > 0) {
                            var datas = moduleForm.getValues();
                            node = nodes[0];
                            if (node.id != 0) {
                                node.set('moduleName', datas.moduleName);
                                node.set('moduleShortName', datas.moduleShortName);
                                node.set("moduleState", datas.moduleState);
                                node.set("moduleType", datas.moduleType);

                                node.set('moduleIndex', datas.moduleIndex);
                                node.set("moduleConfig", datas.moduleConfig);
                                node.set("moduleParams", datas.moduleParams);
                                node.set("moduleDesc", datas.moduleDesc);

                                node.set("moduleIcon", datas.moduleIcon);
                                node.set("modulePic", datas.modulePic);
                                node.set("moduleComment", datas.moduleComment);
                                moduleStore.sync();
                            } else {
                                Ext.Msg.alert('注意', '该节点不能修改');
                            }
                        }
                    }
                }
                ]
            }]
        });
        var modulePanel = new Ext.panel.Panel({
            width: '100%',
            height: '100%',
            layout: 'border',
            border: false,
            renderTo: 'moduleConfig',
            items: [moduleTree, moduleForm],
            tbar: [{
                xtype: 'button',
                text: '添加模块',
                handler: addModule
            }, {
                xtype: 'button',
                text: '删除模块',
                handler: delModule
            }],
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
                        if (Obj) {
                            Obj.height = Obj.height - 40;
                            this.updateBox(Obj)
                        }

                    });
                },
                close: function (closeAction) {
                    if (closeAction) {
                        this.destroy();
                    }
                }
            }
        })

    });
</script>
</body>
</html>
