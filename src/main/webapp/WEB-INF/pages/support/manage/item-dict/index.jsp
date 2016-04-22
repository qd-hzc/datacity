<%--
  Created by IntelliJ IDEA.
  User: hzc
  Date: 2015/12/31
  Time: 16:49
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<%
    String contextPath = request.getContextPath();
%>
<head>
    <title>指标分组目录管理</title>
</head>
<body>
<script src="<%=contextPath%>/City/support/manage/item-dict/add-same-win.js" type="text/javascript"></script>
<script src="<%=contextPath%>/City/support/manage/item-dict/add-sub-win.js" type="text/javascript"></script>
<script>
    var contextPath = "<%=contextPath%>";
    var MARGIN_ROW_SPACE = '8 0 0 0';
    Ext.onReady(function () {
        /*
         * 目录树Store
         */
        var leftTreeItemDictStore = Ext.create('Ext.data.TreeStore', {
            proxy: {
                type: 'ajax',
                api: {
                    read: contextPath + '/itemDict/getItemDictByParent',
                    update: contextPath + '/itemDict/saveItemDictSorts'
                }
            },
            root: {
                expanded: true,
                id: 0,
                name: "指标分组目录"
            }
        });
        /*
         * 目录树
         */
        var leftTreeItemDictTreePanel = Ext.create('Ext.tree.Panel', {
            store: leftTreeItemDictStore,
            region: 'west',
            rootVisible: true,
            width: '18%',
            border: false,
            frame: false,
            displayField: 'name',
            height: '100%',
            autoScroll: true,
            enableDD: true,// 是否支持拖拽效果
            viewConfig: {
                plugins: {
                    ptype: 'treeviewdragdrop'
                },
                listeners: {
                    drop: function (node, data, overModel, dropPosition, dropHandlers) {
                        var dragNode = data.records[0];
                        var pNode = dragNode.parentNode;
                        dragNode.set('parentId', pNode.getId());
                        Ext.Array.each(pNode.childNodes, function (child) {
                            child.set('sort', child.data.index);
                        });
                        leftTreeItemDictStore.sync();
                    }
                }
            },
            listeners: {
                itemclick: function (_this, record, item, index, e, eOpts) {
                    itemDictInfoPanel.getForm().loadRecord(record);
                },
                itemcontextmenu: function (_this, record, item, index, e) {
                    e.preventDefault();
                    if (window.itemDictInfoMenu) {
                        window.itemDictInfoMenu.hide();
                    }
                    showItemDictInfoMenu(_this, record, item, index, e);
                },
                containercontextmenu: function (_this, e) {
                    e.preventDefault();
                }
            }
        });

        /**
         * 显示右键菜单
         */
        function showItemDictInfoMenu(_this, record, item, index, e) {
            //节点信息表格菜单
            var itemDictInfoMenu = new Ext.menu.Menu({
                renderTo: Ext.getBody(),
                items: [{
                    text: '添加下级',
                    iconCls: 'Add',
                    handler: function () {
                        var record = leftTreeItemDictTreePanel.getSelectionModel().getSelection();
                        if (record && record.length > 0) {
                            var selection = record[0];
                            Ext.itemDictManageAddSubWin.show(selection, function (model) {
                                selection.set('leaf', false);
                                selection.set('expanded', true);
                                selection.appendChild(model);
                            });
                        } else {
                            Ext.Msg.alert('提示', '请选择目录');
                        }
                    }
                }, {
                    text: '添加同级',
                    disabled: record.get('id') == 0,
                    iconCls: 'Controladdblue',
                    handler: function () {
                        var record = leftTreeItemDictTreePanel.getSelectionModel().getSelection();
                        if (record && record.length > 0) {
                            Ext.itemDictManageAddSameWin.show(record[0].parentNode, function (model) {
                                record[0].parentNode.appendChild(model);
                            });
                        } else {
                            var root = leftTreeItemDictTreePanel.getRootNode();
                            Ext.itemDictManageAddSameWin.show(root, function (model) {
                                root.set('leaf', false);
                                root.set('expanded', true);
                                root.appendChild(model);
                            });
                        }
                    }
                }, '-', {
                    text: '删除',
                    disabled: record.get('id') == 0,
                    iconCls: 'Delete',
                    handler: function () {
                        Ext.Msg.confirm('提示', '确定要删除吗？', function (id) {
                            if (id == 'yes') {
                                var record = leftTreeItemDictTreePanel.getSelectionModel().getSelection();
                                deleteItemDict(record);
                            }
                        })
                    }
                }]
            });

            /**
             * 删除指标分组目录
             */
            function deleteItemDict(record) {
                $.ajax({
                    type: 'post',
                    url: contextPath + '/itemDict/deleteItemDict',
                    data: {id: record[0].get('id')},
                    dataType: 'json',
                    success: function (data) {
                        Ext.Msg.alert('提示', data.msg,
                                function (id) {
                                    if ('ok' == id) {
                                        if (data.code == 200) {
                                            record[0].remove();
                                        }
                                    }
                                }
                        );

                    },
                    error: function () {
                        Ext.Msg.alert('提示', '系统繁忙，请稍候再试');
                    }
                })
            }

            itemDictInfoMenu.showAt(e.getPoint());
            window.itemDictInfoMenu = itemDictInfoMenu;
        }


        /*
         * id
         * */
        var itemDictId = Ext.create('Ext.form.field.Hidden', {
            name: 'id',
            fieldLabel: '目录Id',
            labelWidth: 100,
            labelAlign: 'right',
            columnWidth: .45,
            border: false
        });

        /*
         * parentId
         */
        var itemDictParentId = Ext.create('Ext.form.field.Hidden', {
            name: 'parentId',
            fieldLabel: '目录父Id',
            labelWidth: 100,
            labelAlign: 'right',
            columnWidth: .45,
            border: false
        });
        /*
         * 目录名称*/
        var itemDictName = Ext.create('Ext.form.field.Text', {
            name: 'name',
            fieldLabel: '目录名称',
            labelWidth: 100,
            labelAlign: 'right',
            columnWidth: .7,
            anchor: '80%',
            border: false,
            width: '45%'
        });
        /*
         * 行政区划代码
         * */
        var itemDictCode = Ext.create('Ext.form.field.Number', {
            name: 'code',
            columnWidth: .7,
            fieldLabel: '目录代码',
            anchor: '80%',
            labelWidth: 100,
            labelAlign: 'right',
            border: false,
            width: '45%',
            allowBlank: false,
            blankText: '必填项',
            maxLength: 10,
            enforceMaxLength: true,
            regex: /^\d{0,}$/,
            regexText: "只能输入数字",
            allowDecimals: false,
            hideTrigger: true,
            keyNavEnabled: false,
            mouseWheelEnabled: false
        });
        /*
         * 第一行
         * */
        var firstLineItemDict = Ext.create('Ext.panel.Panel', {
            layout: {
                type: 'hbox'
            },
            margin: MARGIN_ROW_SPACE,
            border: false,
            items: [itemDictId, itemDictParentId, itemDictName, itemDictCode]
        });
        /*
         * 目录状态
         * */
        var enabledItemDict = Ext.create('Ext.form.field.Radio', {
            name: 'status',
            boxLabel: '启用',
            checked: true,
            inputValue: 1
        });
        var disabledItemDict = Ext.create('Ext.form.field.Radio', {
            name: 'status',
            boxLabel: '禁用',
            checked: false,
            inputValue: 0
        });
        var itemDictStatusGroup = Ext.create('Ext.form.RadioGroup', {
            name: 'statusGroup',
            fieldLabel: '目录状态',
            labelAlign: 'right',
            columnWidth: .8,
            items: [enabledItemDict, disabledItemDict]
        });
        /*
         * 第二行
         * */
        var secondLineItemDict = Ext.create('Ext.panel.Panel', {
            layout: 'column',
            border: false,
            margin: MARGIN_ROW_SPACE,
            items: [itemDictStatusGroup]
        });

        /*
         * 目录备注
         * */
        var itemDictComments = Ext.create('Ext.form.field.TextArea', {
            name: 'comments',
            fieldLabel: '目录备注',
            labelWidth: 100,
            labelAlign: 'right',
            columnWidth: .8,
            height: 100,
            anchor: '80%',
            maxLength: 255,
            maxLengthText: '超过长度限制',
            border: false,
            enforceMaxLength: true
        });
        /*
         * 第三行
         * */
        var thirdLineItemDict = Ext.create('Ext.panel.Panel', {
            layout: 'column',
            border: false,
            margin: MARGIN_ROW_SPACE,
            items: [itemDictComments]
        });

        /*
         * 表单
         * */
        var itemDictInfoPanel = Ext.create('Ext.form.Panel', {
            region: 'center',
            frame: false,
            autoWidth: true,
            height: '100%',
            items: [{
                xtype: 'fieldset',
                title: '目录信息',
                margin: '10 10 10 10',
                padding: '0 0 20 0',
                items: [firstLineItemDict, secondLineItemDict, thirdLineItemDict]
            }, {
                xtype: 'panel',
                layout: 'column',
                border: false,
                margin: '0 10 10 10',
                items: [{
                    xtype: 'displayfield',
                    border: false,
                    columnWidth: .92
                }, {
                    xtype: 'button',
                    text: '保存',
                    width: 70,
                    columnWidth: .08,
                    margin: '0 0 0 0',
                    handler: function () {
                        var record = leftTreeItemDictTreePanel.getSelectionModel().getSelection();
                        if (record.length < 1) {
                            Ext.Msg.alert('提示', '请选择需要修改的目录');
                            return;
                        }
                        if (itemDictInfoPanel.getForm().isValid()) {
                            itemDictInfoPanel.getForm().submit({
                                url: contextPath + '/itemDict/souItemDict',
                                clientValidation: true,
                                waitTitle: '提示',
                                waitMsg: '正在提交数据...',
                                method: 'POST',
                                success: function (form, action) {
                                    try {
                                        Ext.Msg.alert('提示', action.result.datas);
                                        var record = leftTreeItemDictTreePanel.getSelectionModel().getSelection();
                                        var obj = Ext.apply(record[0].getData(), itemDictInfoPanel.getForm().getValues());//copy两个对象的属性和值
                                        record[0].set(obj);
                                        record[0].set('text', obj.name);
                                    } catch (e) {
                                        alert(e);
                                    }
                                    leftTreeItemDictStore.load();
                                },
                                failure: function (form, action) {
                                    Ext.Msg.alert('提示', action.result.msg);
                                }
                            });
                        } else {
                            Ext.Msg.alert('提示', '请按要求填写红框内信息');
                        }
                    }
                }]
            }]
        });
        Ext.create('Ext.panel.Panel', {
            renderTo: 'itemDictManageId',
            layout: 'border',
            height: '100%',
            border: false,
            frame: false,
            width: '100%',
            items: [leftTreeItemDictTreePanel, itemDictInfoPanel],
            listeners: {
                render: function () {
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
                }
            },
            myUpdateBox: function (obj) {
                this.updateBox(obj);
            }
        });
    });
</script>
<div id='itemDictManageId' style="width: 100%;height: 100%;"></div>
</body>
</html>
