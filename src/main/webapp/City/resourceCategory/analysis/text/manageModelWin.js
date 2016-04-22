/**
 * 管理模板窗口
 * Created by wxl on 2016/3/17.
 */
createModel('Ext.text.ManageModelWin', function () {
    Ext.define('Ext.text.ManageModelWin', {
        extend: 'Ext.window.Window',
        title: '管理模板窗口',
        width: 750,
        height: '80%',
        layout: 'fit',
        modal: true,
        listeners: {
            beforeclose:function( panel, eOpts ){
                Ext.Ajax.request({
                    url: GLOBAL_PATH + '/support/resourceCategory/analysis/text/queryAllTextModels',
                    method: 'POST',
                    success: function (response, opts) {
                        var result = Ext.JSON.decode(response.responseText);
                        textModel =Ext.JSON.decode(result);

                        themeStore.reload({params: commonParams.themeParams});
                    }
                })
            }
        }
    });
});

/**
 * 初始化管理模板窗口
 */
Ext.text.ManageModelWin.init = function (fn) {
    //公共参数
    var commonParams = {
        queryParams: {
            name: '',
            status: ''
        }
    };
    //store
    var modelStore = new Ext.data.Store({
        fields: ['id', 'name', 'content', 'status'],
        proxy: {
            type: 'ajax',
            api: {
                read: TEXT_CONTEXT_PATH + '/queryTextModel',
                destroy: TEXT_CONTEXT_PATH + '/removeTextModels'
            }
        },
        autoLoad: true
    });
    //右键菜单
    var menu = new Ext.menu.Menu({
        renderTo: Ext.getBody(),
        items: [{
            text: '删除',
            iconCls: 'Delete',
            handler: function () {
                var sel = modelGrid.getSelectionModel().getSelection();
                if (sel.length) {
                    Ext.Msg.confirm('警告', '确认删除?', function (btn) {
                        if (btn == 'yes') {
                            modelStore.remove(sel);
                            modelStore.sync({
                                success: function () {
                                    modelStore.reload();
                                }
                            });
                        }
                    });
                }
            }
        }]
    });
    //表格
    var modelGrid = new Ext.grid.Panel({
        width: '100%',
        store: modelStore,
        selModel: 'checkboxmodel',
        columns: [{
            text: '模板名',
            dataIndex: 'name',
            flex: 2
        }, {
            text: '状态',
            dataIndex: 'status',
            flex: 0.5,
            renderer: function (value) {
                if(value){
                    return '启用';
                }
                return '弃用';
            }
        }, {
            text: '编辑',
            flex: 0.5,
            renderer: function () {
                return '<a style="color: #3437ff;">编辑</a>';
            }
        }],
        tbar: [{
            xtype: 'textfield',
            emptyText: '输入名称查询',
            triggers: {
                clear: {
                    cls: 'x-form-clear-trigger',
                    handler: function () {
                        this.reset();
                    }
                }
            },
            listeners: {
                change: function (_this, n) {
                    commonParams.queryParams.name = n;
                    //查询
                    modelStore.reload({params: commonParams.queryParams});
                }
            }
        }, {
            xtype: 'combo',
            fieldLabel: '状态',
            width: 150,
            labelWidth: 40,
            labelAlign: 'right',
            editable: false,
            forceSelection: true,
            displayField: 'name',
            valueField: 'id',
            store: new Ext.data.Store({
                fields: ['id', 'name'],
                data: [[1, '启用'], [0, '弃用']]
            }),
            listeners: {
                change: function (_this, n) {
                    commonParams.queryParams.status = n;
                    //查询
                    modelStore.reload({params: commonParams.queryParams});
                }
            },
            triggers: {
                clear: {
                    cls: 'x-form-clear-trigger',
                    handler: function () {
                        this.reset();
                    }
                }
            }
        }, '->', {
            text: '添加',
            iconCls: 'Add',
            handler: function () {
                //添加内容
                Ext.text.ManageModelWin.editContentWin(function () {
                    modelStore.reload();
                });
            }
        }],
        listeners: {
            rowcontextmenu: function (_this, record, tr, rowIndex, e) {
                //阻止浏览器默认行为
                e.preventDefault();
                //弹出右键菜单
                menu.showAt(e.getXY());
            },
            containercontextmenu: function (_this, e) {
                //阻止浏览器默认行为
                e.preventDefault();
                menu.hide();
            },
            cellclick: function (_this, td, cellIndex, record) {
                if (cellIndex == 3) {//编辑
                    Ext.text.ManageModelWin.editContentWin(function () {
                        modelStore.reload();
                    }, record);
                }
            }
        }
    });
    //窗口
    var win = new Ext.text.ManageModelWin({
        items: [modelGrid],
        buttons: [{
            text: '关闭',
            handler: function () {
                win.close();
            }
        }]
    });
    win.show();
};

/**
 * 添加内容窗口
 */
Ext.text.ManageModelWin.editContentWin = function (fn, record) {
    var form = new Ext.form.Panel({
        width: '100%',
        layout: 'column',
        border: false,
        items: [{
            xtype: 'hidden',
            name: 'id'
        }, {
            xtype: 'textfield',
            name: 'name',
            fieldLabel: '模板名<b style="color:red;">*</b>',
            labelWidth: 70,
            labelAlign: 'right',
            columnWidth: 0.45,
            alignBlank: false,
            margin: '20 0 0 0'
        }, {
            xtype: 'combo',
            name: 'status',
            labelWidth: 70,
            fieldLabel: '状态',
            labelAlign: 'right',
            columnWidth: 0.45,
            value: 1,
            store: new Ext.data.Store({
                fields: ['id', 'name'],
                data: [[1, '启用'], [0, '弃用']]
            }),
            editable: false,
            forceSelection: true,
            displayField: 'name',
            valueField: 'id',
            margin: '20 0 0 0'
        }, {
            xtype: 'ueditor',
            name: 'content',
            columnWidth: 1,
            height: 300,
            margin: '20 0 0 0'
        }]
    });
    var win = new Ext.window.Window({
        title: '添加内容窗口',
        width: 800,
        modal: true,
        items: [form],
        buttons: [{
            text: '保存',
            handler: function () {
                if (form.isValid()) {
                    form.submit({
                        url: TEXT_CONTEXT_PATH + '/saveTextModel',
                        success: function (form, action) {
                            Ext.Msg.alert('成功', action.result.msg);
                            win.close();
                            fn();
                        },
                        failure: function (form, action) {
                            Ext.Msg.alert('失败', action.result ? action.result.msg : '保存失败');
                        }
                    });
                }
            }
        }, {
            text: '关闭',
            handler: function () {
                win.close();
            }
        }]
    });
    if (record) {
        form.loadRecord(record);
    }
    win.show();
};
