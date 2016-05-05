<%@page language="java" pageEncoding="UTF-8" %>
<html>
<head>
    <title>用户权限管理</title>
    <style type="text/css">
        .prev {
            background-image: url(<%=request.getContextPath()%>/Plugins/extjs/resources/icons/arrow_left.png) !important;
        }

        .next {
            background-image: url(<%=request.getContextPath()%>/Plugins/extjs/resources/icons/arrow_right.png) !important;
        }

    </style>
</head>
<body>
<script type="text/javascript" src="<%=request.getContextPath()%>/Plugins/extjs/ux/TreePicker.js"></script>
<script type="text/javascript"
        src="<%=request.getContextPath()%>/City/support/sys/user/userManage/userAddWin.js"></script>
<script type="text/javascript"
        src="<%=request.getContextPath()%>/City/support/sys/user/userManage/userModify.js"></script>
<script type="text/javascript"
        src="<%=request.getContextPath()%>/City/support/sys/user/userManage/catUserWin.js"></script>

<div id="userManageId" style="width: 100%;height: 100%;"></div>
<script type="text/javascript">
    Ext.onReady(function () {
        var childId = '<%=request.getParameter("childId")%>';
        //Ext.QuickTips.init();
        // 定义用户model
        var moduleModel = createModel('userModel', function () {
            Ext.define('userModel', {
                extend: 'Ext.data.Model',
                fields: [{
                    name: 'id'
                }, {
                    name: 'userName',
                    type: 'string'
                }, {
                    name: 'loginName',
                    type: 'string'
                }, {
                    name: 'loginPwd',
                    type: 'string'
                }, {
                    name: 'duty',
                    type: 'string'
                }, {
                    name: 'email',
                    type: 'string'
                }, {
                    name: 'mobilePhone',
                    type: 'string'
                }, {
                    name: 'sex',
                    type: 'string'
                }, {
                    name: 'state',
                    type: 'string'
                }, {
                    name: 'userInfo',
                    type: 'string'
                }, {
                    name: 'department'
                }]
            });
        });
        //定义用户数据源
        var userStore = Ext.create('Ext.data.Store', {
            model: 'userModel',
            pageSize: 15,//extjs3.x放在PagingToolbar里面。
            autoLoad: true,
            proxy: {
                type: 'ajax',//defaultProxyType : 'memory'
                timeout: 15 * 60 * 1000,//默认是15分钟
                //请求后台，执行查询用户数据操作
                url: GLOBAL_PATH + '/support/sys/user/userManage/findUsersPageByNameOrLoginName',
                actionMethods: {//默认：{create: 'POST', read: 'GET', update: 'POST', destroy: 'POST'}
                    read: 'POST'
                },
                reader: {
                    type: 'json',
                    rootProperty: 'datas',
                    totalProperty: 'total',
                    idProperty: 'id'
                }
            },
            listeners: {
                'beforeload': function (store, operation, eOpts) {
                    //在store去后台load数据时先需要传递查询条件
                    var userDisplayName = userDisplayNameForQuery.getValue();
                    var loginName = loginNameForQuery.getValue();
                    userStore.getProxy().extraParams = {
                        name: userDisplayName,
                        loginName: loginName
                    }
                }
            }
        });
        //userStore.load();
        //定义用户复选框模型
        var userCheckBoxModel = Ext.create('Ext.selection.CheckboxModel', {
            selType: 'checkboxmodel',
            checkOnly: true,//单击列不选中，只有点击选框才选中
            mode: 'MULTI',//多选 "SINGLE"/"SIMPLE"/"MULTI"
            injectCheckbox: 'first'//把复选框放在第一位
        });

        var userDisplayNameForQuery = Ext.create('Ext.form.field.Text', {
            //xtype : 'textfield',
            //id : 'searchName',
            hideLabel: true,
            width: 200,
            listeners: {
                specialkey: function (field, e) {
                    if (e.getKey() == e.ENTER || e.getKey() == e.TAB) {
                        userStore.loadPage(1, {
                            params: {start: 0, limit: userStore.pageSize}
                        });
                    }
                }
            }
        });

        var loginNameForQuery = Ext.create('Ext.form.field.Text', {
            //xtype : 'textfield',
            //id : 'searchName',
            hideLabel: true,
            width: 200,
            listeners: {
                specialkey: function (field, e) {
                    if (e.getKey() == e.ENTER || e.getKey() == e.TAB) {
                        userStore.loadPage(1, {
                            params: {start: 0, limit: userStore.pageSize}
                        });
                    }
                }
            }
        });

        var isPageRender = false;
        //gridpanel，定义用户展示面板
        var userGridPanel = Ext.create('Ext.grid.Panel', {
            //renderTo : 'userManageId',
            //id : childId,
            frame: false,
            border: false,
            forceFit: true,
            selModel: userCheckBoxModel,
            store: userStore,
            height: '100%',
            flex: 1,
            autoScroll: true,
            loadMask: {
                msg: '正在加载数据...'
            },
            columns: [new Ext.grid.RowNumberer({
                header: '序号',
                align: 'center',
                flex: .2
            }), {
                header: '用户名',
                dataIndex: 'userName',
                sortable: true,
                align: 'center',
                flex: 1
            }, {
                header: '登录名',
                dataIndex: 'loginName',
                sortable: true,
                align: 'center',
                flex: 1
            }, {
                header: '部门',
                dataIndex: 'department',
                sortable: true,
                align: 'center',
                flex: 1.5,
                renderer: function (value, obj, record, rowIndex, colIndex, store, view) {
                    return value.depName;
                }
            }, {
                header: '查看用户权限信息',
                dataIndex: 'modify',
                sortable: true,
                align: 'center',
                flex: .5,
                renderer: function (value, obj, record, rowIndex, colIndex, store, view) {
                    return '<div style="cursor:pointer;*cursor:hand !important;*cursor:hand;"><a style="text-decoration:none" href="#">查看</a></div>';
                }
            }, {
                header: '修改',
                dataIndex: 'modify',
                sortable: true,
                align: 'center',
                flex: .5,
                renderer: function (value, obj, record, rowIndex, colIndex, store, view) {
                    return '<div style="cursor:pointer;*cursor:hand !important;*cursor:hand;"><a style="text-decoration:none" href="#">修改</a></div>';
                }
            }],
            stripeRows: true,//斑马线
            tbar: ['-', '用户名', userDisplayNameForQuery, '-', '登录名', loginNameForQuery, {
                iconCls: 'query',
                text: '查询',
                handler: function () {//第一页
                    userStore.loadPage(1, {//Ext.data.Operation
                        //start : 0,
                        params: {
                            start: 0,
                            limit: userStore.pageSize
                        }
                    });
                }
            }, '->', {
                xtype: 'button',
                iconCls: 'Add',
                text: '添加',
                handler: function () {
                    // 添加用户
                    Ext.userAddWin.show(function (data) {
                        userStore.loadPage(1, {//Ext.data.Operation
                            //start : 0,
                            params: {
                                start: 0,
                                limit: userStore.pageSize
                            }
                        });
                    }, '');


                }
            }, '-', {
                xtype: 'button',
                iconCls: 'Delete',
                text: '删除',
                handler: function () {
                    var records = userGridPanel.getSelectionModel().getSelection();
                    if (!records || records.length == 0) {
                        Ext.Msg.alert('提示', '请选择要删除的用户');
                        return;
                    }
                    var userIds = '[';
                    for (var i = 0; i < records.length; i++) {
                        userIds += records[i].get('id') + ','; //多条删除，id相加，中间以逗号区分，组成字符串
                    }
                    Ext.Msg.confirm('询问', '确定删除？', function (btn) {
                        if (btn === 'yes') {
                            Ext.Ajax.request({
                                // 请求后台，执行删除用户操作
                                url: GLOBAL_PATH + '/support/sys/user/userManage/deleteUser',
                                waitTitle: '提示',
                                waitMsg: '正在操作...',
                                method: 'POST',
                                params: {
                                    userIds: userIds.substring(0, userIds.length - 1) + "]"
                                },
                                success: function (response, opts) {
                                    Ext.Msg.alert('提示', "删除成功!");
                                    userStore.loadPage(1, {//Ext.data.Operation
                                        start: 0,
                                        limit: userStore.pageSize
                                    });
                                },
                                failure: function (response, opts) {
                                }
                            });
                        }
                    });
                }
            }],
            bbar: ['->', new Ext.PagingToolbar({
                store: userStore,
                border: false,
                displayInfo: true,
                displayMsg: '显示第{0}条 到{1}条记录，一共{2}条',
                emptyMsg: "没有数据"
            })],
            listeners: {
                'cellclick': function (_this, td, cellIndex, record, tr, rowIndex, e, eOpts) {
                    // alert(record.get('userDepName'));
                    if (cellIndex == 5) {//查看
                        Ext.catUserWin.show(function (data) {

                        }, record);
                    }
                    if (cellIndex == 6) {//修改
                        Ext.userModify.show(function (data) {
                            userStore.loadPage(1, {//Ext.data.Operation
                                //start : 0,
                                params: {
                                    start: 0,
                                    limit: userStore.pageSize
                                }
                            });
                        }, record);
                    }
                }, 'afterrender': function (_this) {
                    //userStore.load();
                }
            },
            myUpdateBox: function (obj) {//切换标签页时会自动隐藏分页栏，需要把分页栏的高度固定。
                //还有另外一个问题是当gridpanel中的行数过多而出现竖滚动条时，这时会出现横滚动条。
                //this.updateBox({height : obj.height - this.down('pagingtoolbar').height, width : obj.width});
            }
        });

        new Ext.panel.Panel({
            width: '100%',
            height: '100%',
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            border: false,
            renderTo: 'userManageId',
            items: [userGridPanel],
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
                }
            }
        });

    });
</script>

</body>
</html>