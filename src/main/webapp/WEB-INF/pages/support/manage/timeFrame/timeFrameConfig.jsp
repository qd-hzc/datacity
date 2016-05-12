<%--
  Created by IntelliJ IDEA.
  User: wgx
  Date: 2016/1/6
  Time: 13:57
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<html>
<head>
    <title>时间框架管理</title>
    <meta charset="UTF-8"/>
</head>
<body>
<div id="timeFrameContainer" style="width:100%;height:100%;"></div>
<script src="<%=request.getContextPath()%>/City/support/manage/timeFrame/addTimeFrameWin.js"></script>
<script>


    Ext.onReady(function () {

        /**
         * 修改时间框架
         */
        function updateTimeFrame() {
            var sel = timeFrameGrid.getSelectionModel().getSelection();
            if (sel.length) {
                if (sel.length == 1) {
                    var win = Ext.addTimeFrameWin.init(sel[0], function (rec) {
                        var datas = timeFrameGrid.getSelectionModel().getSelection()[0];
                        datas.set('name', rec.name);
                        datas.set('code', rec.code);
                        datas.set('equation', rec.equation);
                        if (rec.order) {
                            datas.set('order', rec.order);
                        } else {
                            datas.set('order', 0);
                        }
                        datas.set('comments', rec.comments);
                        timeFrameStore.sync({
                            failure: function () {
                                timeFrameStore.reload();
                            }
                        });
                        win.close();
                    });

                } else {
                    Ext.Msg.alert('提示', '请选择一个时间框架');
                }
            } else {
                Ext.Msg.alert('提示', '未选中时间框架');
            }
        }

        /**
         * 删除时间框架
         */
        function deleteTimeFrame() {
            var sel = timeFrameGrid.getSelectionModel().getSelection();
            if (sel && sel.length) {
                Ext.Msg.confirm('警告', '确定要删除么?', function (btn) {
                    if (btn == 'yes') {
                        timeFrameStore.remove(sel);
                        timeFrameStore.sync();
                    }
                });
            } else {
                Ext.Msg.alert('提示', '未选中时间框架');
            }
        }

        //时间框架model
        createModel('TimeFrame', function () {
            Ext.define('TimeFrame', {
                extend: 'Ext.data.Model',
                fields: [{
                    name: 'id',
                    type: 'int'
                }, {
                    name: 'name',
                    type: 'string'
                }, {
                    name: 'code',
                    type: 'string'
                }, {
                    name: 'order',
                    type: 'int'
                }, {
                    name: 'equation',
                    type: 'string'
                }, {
                    name: 'comments',
                    type: 'string'
                }]
            });
        });
        //时间框架数据源
        var timeFrameStore = new Ext.data.Store({
            model: 'TimeFrame',
            pageSize: 10,
            proxy: {
                type: 'ajax',
                api: {
                    read: GLOBAL_PATH + '/support/manage/timeFrame/getAllTimeFramesByOrder',
                    update: GLOBAL_PATH + '/support/manage/timeFrame/updateTimeFrame',
                    destroy: GLOBAL_PATH + '/support/manage/timeFrame/batchDeleteTimeFrames'
                },
                reader: {
                    type: 'json',
                    rootProperty: 'datas',
                    messageProperty: 'msg'
                },
            },
            autoLoad: true,
            listeners: {
                write: function (store, operate, callback) {
                    Ext.Msg.alert('提示', operate._resultSet.message);
                }
            }

        });
        //右键菜单
        var timeFrameMenu = new Ext.menu.Menu({
            renderTo: Ext.getBody(),
            items: [{
                text: '修改时间框架',
                iconCls: 'Pageedit',
                handler: function () {
                    updateTimeFrame();
                }

            }, {
                text: '删除时间框架',
                iconCls: 'Delete',
                handler: function () {
                    deleteTimeFrame();
                }
            }]
        })
        // 时间框架表格
        var timeFrameGrid = new Ext.grid.Panel({
            width: '100%',
            flex: 1,
            border: false,
            selType: 'checkboxmodel',
            store: timeFrameStore,
            columns: [{
                text: '时间框架名称',
                dataIndex: 'name',
                flex: 1
            }, {
                text: '时间框架代码',
                dataIndex: 'code',
                flex: 1
            }, {
                text: '时间框架备注',
                dataIndex: 'comments',
                flex: 1
            }, {
                text: '时间框架公式',
                dataIndex: 'equation',
                flex: 1
            }, {
                text: '时间框架排序',
                dataIndex: 'order',
                flex: 1
            }],
            tbar: ['<b>时间框架管理</b>',
                '->', {
                    xtype: 'button',
                    text: '添加时间框架',
                    handler: function () {
                        var win = Ext.addTimeFrameWin.init(null, function (data) {
                            if (!data.order) {
                                data.order = 0;
                            }
                            //timeFrameStore.reload();
                            Ext.Ajax.request({
                                url: GLOBAL_PATH + "/support/manage/timeFrame/saveTimeFrame",
                                method: 'POST',
                                params: data,
                                success: function (response, opts) {
                                    var result = Ext.JSON.decode(response.responseText);
                                    if (result.success) {
                                        timeFrameStore.add(result.datas[0]);
                                        timeFrameStore.reload();
                                        Ext.Msg.alert('成功', result.msg);
                                    } else {
                                        Ext.Msg.alert('失败', result.msg);
                                    }

                                }
                            })
                            win.close();

                        });
                    }
                }, '-', {
                    xtype: 'button',
                    text: '修改时间框架',
                    handler: function () {
                        updateTimeFrame();
                    }
                }, '-', {
                    xtype: 'button',
                    text: '删除时间框架',
                    handler: function () {
                        deleteTimeFrame();
                    }

                }],
            bbar: {
                xtype: 'pagingtoolbar',
                store: timeFrameStore,
                displayInfo: true
            },
            listeners: {
                itemcontextmenu: function (_this, record, itemId, index, e) {
                    e.preventDefault();
                    timeFrameMenu.showAt(e.getPoint());
                },
                cellclick: function () {
                    if (timeFrameMenu) {
                        timeFrameMenu.hide();
                    }
                },
                containerclick: function () {
                    if (timeFrameMenu) {
                        timeFrameMenu.hide();
                    }
                },
                containercontextmenu: function (_this, e) {
                    //取消冒泡
                    e.preventDefault();
                    if (timeFrameMenu) {
                        timeFrameMenu.hide();
                    }
                }
            }
        });

        new Ext.panel.Panel({
            width: '100%',
            height: '100%',
            layout: 'vbox',
            border: false,
            renderTo: 'timeFrameContainer',
            items: [timeFrameGrid],
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
