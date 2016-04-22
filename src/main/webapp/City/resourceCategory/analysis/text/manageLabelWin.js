/**
 * 管理标签窗口
 * Created by wxl on 2016/3/17.
 */
createModel('Ext.text.ManageLabelWin', function () {
    Ext.define('Ext.text.ManageLabelWin', {
        extend: 'Ext.window.Window',
        title: '管理标签窗口',
        width: 450,
        height: '80%',
        layout: 'fit',
        modal: true,
        listeners: {
            beforeclose: function (panel, eOpts) {
                Ext.Ajax.request({
                    url: GLOBAL_PATH + '/support/resourceCategory/analysis/text/queryAllTextLabels',
                    method: 'POST',
                    success: function (response, opts) {
                        var result = Ext.JSON.decode(response.responseText);
                        textLabel = Ext.JSON.decode(result);
                        if (commonParams.contentParams.themeId) {
                            contentStore.reload({params: commonParams.contentParams});
                        }
                    }
                })
            }
        }
    });
});
/**
 * 初始化管理标签窗口
 */
Ext.text.ManageLabelWin.init = function () {
    //公共参数
    var commonParams = {
        queryParams: {
            name: '',
            tier: ''
        }
    };
    //标签表格
    var labelStore = new Ext.data.Store({
        fields: ['id', 'name', 'tier'],
        proxy: {
            type: 'ajax',
            api: {
                read: TEXT_CONTEXT_PATH + '/queryTextLabels',
                destroy: TEXT_CONTEXT_PATH + '/removeTextLabels'
            }
        },
        autoLoad: true
    });
    //右键菜单
    var menu = new Ext.menu.Menu({
        renderTo: Ext.getBody(),
        items: [{
            text: '修改',
            iconCls: 'Pageedit',
            handler: function () {
                var sel = labelGrid.getSelectionModel().getSelection();
                if (sel.length == 1) {
                    Ext.text.ManageLabelWin.editContentWin(function () {
                        labelStore.reload();
                    }, sel[0]);
                } else {
                    Ext.Msg.alert('提示', '请选中一条记录修改!')
                }
            }
        }, {
            text: '删除',
            iconCls: 'Delete',
            handler: function () {
                var sel = labelGrid.getSelectionModel().getSelection();
                if (sel.length) {
                    Ext.Msg.confirm('警告', '确认删除?', function (btn) {
                        if (btn == 'yes') {
                            labelStore.remove(sel);
                            labelStore.sync({
                                success: function () {
                                    labelStore.reload();
                                }
                            });
                        }
                    });
                }
            }
        }]
    });
    //表格
    var labelGrid = new Ext.grid.Panel({
        width: '100%',
        store: labelStore,
        selModel: 'checkboxmodel',
        columns: [{
            text: '标签名',
            dataIndex: 'name',
            flex: 2
        }, {
            text: '级别',
            dataIndex: 'tier',
            flex: 1,
            align: 'center'
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
                    labelStore.reload({params: commonParams.queryParams});
                }
            }
        }, {
            xtype: 'numberfield',
            emptyText: '输入级别查询',
            minValue: 0,
            listeners: {
                change: function (_this, n) {
                    if (_this.isValid()) {
                        commonParams.queryParams.tier = n;
                        //查询
                        labelStore.reload({params: commonParams.queryParams});
                    }
                }
            }
        }, '->', {
            text: '添加',
            iconCls: 'Add',
            handler: function () {
                //添加内容
                Ext.text.ManageLabelWin.editContentWin(function () {
                    labelStore.reload();
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
            }
        }
    });
    //窗口
    var win = new Ext.text.ManageLabelWin({
        items: [labelGrid],
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
 * 编辑内容
 * @param fn 回调
 * @param record 一条记录
 */
Ext.text.ManageLabelWin.editContentWin = function (fn, record) {
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
            fieldLabel: '标签名<b style="color:red;">*</b>',
            labelWidth: 70,
            labelAlign: 'right',
            columnWidth: 0.9,
            alignBlank: false,
            margin: '20 0 0 0'
        }, {
            xtype: 'numberfield',
            name: 'tier',
            fieldLabel: '级别<b style="color:red;">*</b>',
            labelWidth: 70,
            labelAlign: 'right',
            columnWidth: 0.9,
            alignBlank: false,
            margin: '20 0 20 0',
            value: 0
        }]
    });
    var win = new Ext.window.Window({
        title: '添加内容窗口',
        width: 400,
        modal: true,
        items: [form],
        buttons: [{
            text: '保存',
            handler: function () {
                if (form.isValid()) {
                    form.submit({
                        url: TEXT_CONTEXT_PATH + '/saveTextLabel',
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
