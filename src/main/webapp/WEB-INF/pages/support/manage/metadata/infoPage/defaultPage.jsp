<%--
  Created by IntelliJ IDEA.
  User: wgx
  Date: 2016/1/13
  Time: 20:03
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<html>
<head>
    <title>系统元数据信息默认页面</title>
    <meta charset="UTF-8"/>
</head>
<body>
<div id="defaultdataContainer" style="width:100%;height:100%;"></div>
<script>
    var type = ${type};
    Ext.onReady(function () {
        //Model
        var defaultdataModel = createModel('DefaultdataModel', function () {
            Ext.define('DefaultdataModel', {
                extend: 'Ext.data.Model',
                fields: [{
                    name: "id",
                    type: "int"
                }, {
                    name: "type",
                    type: "int"
                }, {
                    name: "name",
                    type: "string"
                }, {
                    name: "code",
                    type: "int"
                }, {
                    name: "comments",
                    type: "string"
                }]
            });
        })
        //数据源
        var defaultdataStore = new Ext.data.Store({
            model: 'DefaultdataModel',
            pageSize: 10,
            proxy: {
                type: 'ajax',
                api: {
                    read: GLOBAL_PATH + '/support/manage/metadata/getAllMetadataInfosByType?type=' + type,
                    update: GLOBAL_PATH+'/support/manage/metadata/updateMetadataInfo',
                    destroy: GLOBAL_PATH+'/support/manage/metadata/batchDeleteMetadataInfos'
                },
                reader: {
                    type: 'json',
                    rootProperty: 'datas',
                    messageProperty: 'msg'
                }
            },
            autoLoad: true,
            listeners: {
                write: function (store, operate, callback) {
                    Ext.Msg.alert('提示', operate._resultSet.message);
                }
            }

        })
        // 修改系统元数据
        function updateMatadataInfo() {
            var sel = defaultdataGrid.getSelectionModel().getSelection();
            if (sel.length == 1) {
                var record = sel[0];
                var win = Ext.addMetadataInfoWin.init(record, type, function (rec) {
                    var datas = defaultdataGrid.getSelectionModel().getSelection()[0];
                    datas.set('name', rec.name);
                    datas.set('code', rec.code);
                    if(rec.sort){
                        datas.set('sort', rec.sort);
                    }else{
                        datas.set('sort',0);
                    }
                    datas.set('comments', rec.comments);
                    defaultdataStore.sync();
                    win.close();
                });
            } else {
                Ext.Msg.alert('提示', '请选中一个元数据修改');
            }
        }
        //删除系统元数据
        function deleteMetadataInfo() {
            var sel = defaultdataGrid.getSelectionModel().getSelection();
            if (sel.length) {
                Ext.Msg.confirm('警告', '确定要删除么?', function (btn) {
                    if (btn == 'yes') {
                        var sel = defaultdataGrid.getSelectionModel().getSelection();
                        if (sel) {
                            defaultdataStore.remove(sel);
                            defaultdataStore.sync();
                        }
                    }
                });
            } else {
                Ext.Msg.alert('提示', '未选中元数据');
            }
        }
        //右键菜单
        var defaultdataMenu = new Ext.menu.Menu({
            renderTo: Ext.getBody(),
            items: [{
                text: '修改元数据',
                iconCls: 'Pageedit',
                handler: function () {
                    updateMatadataInfo();
                }
            }, {
                text: '删除元数据',
                iconCls: 'Delete',
                handler: function () {
                    deleteMetadataInfo();
                }
            }]
        })
        //表格
        var defaultdataGrid = new Ext.grid.Panel({
            width: '100%',
            flex:1,
            border: false,
            selType: 'checkboxmodel',
            store: defaultdataStore,
            columns: [{
                text: '元数据名称',
                dataIndex: 'name',
                flex: 1
            }, {
                text: '元数据代码',
                dataIndex: 'code',
                flex: 1
            },{
                text: '排序',
                dataIndex: 'sort',
                flex: 1
            }, {
                text: '备注',
                dataIndex: 'comments',
                flex: 1
            }],
            listeners: {
                cellclick: function (_this, td, cellIndex, record) {
                    //菜单操作
                    defaultdataMenu.hide();
                    if(parentMenu){
                        parentMenu.hide();
                    }
                    childMenu = null;
                },
                containercontextmenu: function (_this, e) {
                    //取消冒泡
                    e.preventDefault();
                    defaultdataMenu.hide();
                    if(parentMenu){
                        parentMenu.hide();
                    }
                    childMenu = null;
                },
                itemcontextmenu: function (_this, record, itemId, index, e) {
                    //取消冒泡
                    e.preventDefault();
                    //弹出菜单
                    defaultdataMenu.hide();
                    defaultdataMenu.showAt(e.getPoint());
                    if(parentMenu){
                        parentMenu.hide();
                    }
                    childMenu = defaultdataMenu;
                },
                containerclick: function () {
                    defaultdataMenu.hide();
                    if(parentMenu){
                        parentMenu.hide();
                    }
                    childMenu = null;
                }
            },
            tbar: ['->',{
                xtype: 'button',
                iconCls: 'Add',
                text: '添加元数据',
                handler: function () {
                    var win = Ext.addMetadataInfoWin.init(null, type, function (data) {
                        //defaultdataStore.reload();
                        if(!data.sort){
                            data.sort=0;
                        }
                        Ext.Ajax.request({
                            url: GLOBAL_PATH + "/support/manage/metadata/saveMetadataInfo",
                            method: 'POST',
                            params: data,
                            success: function (response, opts) {
                                var result = Ext.JSON.decode(response.responseText);
                                if (result.success) {
                                    defaultdataStore.add(result.datas);
                                    defaultdataStore.reload();
                                    Ext.Msg.alert('成功',result.msg);
                                }else{
                                    Ext.Msg.alert('失败',result.msg);
                                }

                            }
                        })
                        win.close();
                    });
                }
            }],
            bbar: {
                xtype: 'pagingtoolbar',
                store: defaultdataStore,
                displayInfo: true
            }

        })

        grid=new Ext.panel.Panel({
            width: '100%',
            height: '100%',
            border: false,
            layout: 'vbox',
            renderTo: 'defaultdataContainer',
            items: [defaultdataGrid]
        })


    })

</script>
</body>
</html>
