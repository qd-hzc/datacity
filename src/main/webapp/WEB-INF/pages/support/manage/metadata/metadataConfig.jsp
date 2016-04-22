<%--
  Created by IntelliJ IDEA.
  User: wgx
  Date: 2016/1/13
  Time: 19:42
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<html>
<head>
    <title>系统元数据管理</title>
    <meta charset="UTF-8"/>
</head>
<body>
<div id="metadataContainer" style="width:100%;height:100%;"></div>
<script src="<%=request.getContextPath()%>/City/support/manage/metadata/addMetadataTypeWin.js"></script>
<script src="<%=request.getContextPath()%>/City/support/manage/metadata/infoPage/addUnitdataInfoWin.js"></script>
<script src="<%=request.getContextPath()%>/City/support/manage/metadata/infoPage/addMetadataInfoWin.js"></script>
<script>
    var grid = null;
    var parentMenu = null;
    var childMenu = null;
    Ext.onReady(function () {

        //系统元数据model
        var metadataTypeModel = createModel('MetadataType', function () {
            Ext.define('MetadataType', {
                extend: 'Ext.data.Model',
                fields: [{
                    name: 'id',
                    type: 'int'
                }, {
                    name: 'name',
                    type: 'string'
                }, {
                    name: 'infoPage',
                    type: 'string'
                }, {
                    name: 'comments',
                    type: 'string'
                }]
            });
        });
        //系统元数据数据源
        var metadataTypeStore = new Ext.data.Store({
            model: 'MetadataType',
            pageSize: 10,
            proxy: {
                type: 'ajax',
                api: {
                    read: '<%=request.getContextPath()%>/support/manage/metadata/getAllMetadataTypes',
                    update: GLOBAL_PATH + '/support/manage/metadata/updateMetadataType',
                    destroy: GLOBAL_PATH + '/support/manage/metadata/batchDeleteMetadataTypes'
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
        //修改系统元数据类型
        function updateMetadataType() {
            var sel = metadataTypeGrid.getSelectionModel().getSelection();
            if (sel.length == 1) {
                var record = sel[0];
                var win = Ext.addMetadataTypeWin.init(record, function (rec) {
                    var datas = metadataTypeGrid.getSelectionModel().getSelection()[0];
                    datas.set('name', rec.name);
                    datas.set('infoPage', rec.infoPage);
                    datas.set('comments', rec.comments);
                    metadataTypeStore.sync();
                    win.close();
                });
            } else {
                Ext.Msg.alert('提示', '请选中一个元数据类型修改');
            }
        }

        function deleteMetadataType() {
            var sel = metadataTypeGrid.getSelectionModel().getSelection();
            if (sel.length) {
                Ext.Msg.confirm('警告', '确定要删除么?', function (btn) {
                    if (btn == 'yes') {
                        var sel = metadataTypeGrid.getSelectionModel().getSelection();
                        if (sel) {
                            metadataTypeStore.remove(sel);
                            metadataTypeStore.sync();
                        }
                    }
                });
            } else {
                Ext.Msg.alert('提示', '未选中元数据类型');
            }
        }

        //右键菜单
        var metadataTypeMenu = new Ext.menu.Menu({
            renderTo: Ext.getBody(),
            items: [{
                text: '修改元数据类型',
                iconCls: 'Pageedit',
                handler: function () {
                    updateMetadataType();
                }
            }, {
                text: '删除元数据类型',
                iconCls: 'Delete',
                handler: function () {
                    deleteMetadataType();
                }
            }]
        })
        // 系统元数据表格
        var metadataTypeGrid = new Ext.grid.Panel({
            width: '100%',
            height: '100%',
            flex: 1,
            border: false,
            store: metadataTypeStore,
            columns: [{
                text: '系统元数据类型',
                dataIndex: 'name',
                flex: 1
            }],
            listeners: {
                cellclick: function (_this, td, cellIndex, record) {
                    metadataTypeMenu.hide();
                    var id = record.getId();
                    getMetadataInfoGrid(id);
                    if (childMenu) {
                        childMenu.hide();
                    }
                    parentMenu = null;
                },
                containercontextmenu: function (_this, e) {
                    //取消冒泡
                    e.preventDefault();
                    //弹出菜单
                    metadataTypeMenu.hide();
                    if (childMenu) {
                        childMenu.hide();
                    }
                    parentMenu = null;
                },
                itemcontextmenu: function (_this, record, itemId, index, e) {
                    //取消冒泡
                    e.preventDefault();
                    //弹出菜单
                    metadataTypeMenu.showAt(e.getPoint());
                    if (childMenu) {
                        childMenu.hide();
                    }
                    parentMenu = metadataTypeMenu;
                },
                containerclick: function () {
                    metadataTypeMenu.hide();
                    if (childMenu) {
                        childMenu.hide();
                    }
                    parentMenu = null;
                }
            },
            tbar: ['->', {
                xtype: 'button',
                iconCls: 'Add',
                text: '添加',
                handler: function () {
                    var win = Ext.addMetadataTypeWin.init(null, function (data) {
                        //metadataTypeStore.reload();
                        Ext.Ajax.request({
                            url: GLOBAL_PATH + "/support/manage/metadata/saveMetadataType",
                            method: 'POST',
                            params: data,
                            success: function (response, opts) {
                                var result = Ext.JSON.decode(response.responseText);
                                if (result.success) {
                                    metadataTypeStore.add(result.datas);
                                    metadataTypeStore.reload();
                                    getMetadataInfoGrid(result.datas.id);
                                    Ext.Msg.alert('成功', result.msg);
                                } else {
                                    Ext.Msg.alert('失败', result.msg);
                                }

                            }
                        })
                        win.close();
                    });
                }
            }]

        });
        var metadataInfoContainerPanel = new Ext.panel.Panel({
            //id: 'metadataInfoContainer',
            width: '100%',
            height: '100%',
            flex: 3,
            autoScroll: false,
            //frame: true,
            listeners: {

                resize: function (component, width, height, oldWidth, oldHeight, eOpts) {
                    //grid.setWidth(0);
                    if (grid) {
                        grid.setWidth(width);
                        grid.setHeight(height);
                    }

                },
                containerclick: function () {
                    //取消冒泡
                    e.preventDefault();
                    if (metadataTypeMenu) {
                        metadataTypeMenu.hide();
                    }

                }
            },
            loader: {
                url: GLOBAL_PATH + '/support/manage/metadata/metadataInfoConfig',
                autoLoad: true,
                loadMask: '正在加载...',
                closeAction: "destroy",
                scripts: true,
                renderer: function (loader, response, active) {
                    var text = response.responseText;
                    loader.getTarget().update(text, true, null);
                    return true;
                },
                nocache: true
            }
        });
        metadataInfoPanel = new Ext.panel.Panel({
            width: '100%',
            height: '100%',
            layout: 'hbox',
            border: false,
            renderTo: 'metadataContainer',
            items: [metadataTypeGrid, metadataInfoContainerPanel],
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
        function getMetadataInfoGrid(id) {
            if (id) {
                var urlid = '?id=' + id;
            } else {
                var urlid = '';
            }

            metadataInfoContainerPanel.getLoader().load({
                url: GLOBAL_PATH + '/support/manage/metadata/metadataInfoConfig' + urlid,
                autoLoad: true,
                loadMask: '正在加载...',
                closeAction: "destroy",
                scripts: true,
                renderer: function (loader, response, active) {
                    var text = response.responseText;
                    loader.getTarget().update(text, true, null);
                    return true;
                },
                nocache: true
            });
        }

    });

</script>
</body>
</html>