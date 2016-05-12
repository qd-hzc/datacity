/**
 * Created by Administrator on 2015/12/30 0030.
 * 添加或修改指标窗口
 */
createModel('Ext.addItemWin', function () {
    Ext.define('Ext.addItemWin', {
        extend: 'Ext.window.Window',
        width: 600,
        modal: true,
        title: '添加指标窗口'
    });
});

/**
 * 初始化
 */
Ext.addItemWin.init = function (record, fn) {
    var form = new Ext.form.Panel({
        width: '100%',
        layout: 'column',
        items: [{
            xtype: 'hidden',
            name: 'id'
        }, {
            xtype: 'hidden',
            name: 'sortIndex'
        }, {
            xtype: 'textfield',
            name: 'name',
            allowBlank: false,
            fieldLabel: '指标名<b style="color:red">*</b>',
            labelWidth: 70,
            labelAlign: 'right',
            columnWidth: 0.45,
            margin: '20 0 0 0'
        }, {
            xtype: 'textfield',
            name: 'code',
            fieldLabel: '指标代码',
            labelWidth: 70,
            labelAlign: 'right',
            columnWidth: 0.45,
            margin: '20 0 0 0'
        }, {
            xtype: 'combobox',
            name: 'type',
            fieldLabel: '类型',
            editable: false,
            labelWidth: 70,
            labelAlign: 'right',
            displayField: 'text',
            valueField: 'value',
            store: new Ext.data.Store({
                fields: ['text', 'value'],
                data: [{text: '标准类型', value: 1}, {text: '自用类型', value: 0}]
            }),
            value: 1,
            columnWidth: 0.45,
            margin: '20 0 0 0'
        }, {
            xtype: 'combobox',
            name: 'status',
            fieldLabel: '状态',
            editable: false,
            labelWidth: 70,
            labelAlign: 'right',
            displayField: 'text',
            valueField: 'value',
            store: new Ext.data.Store({
                fields: ['text', 'value'],
                data: [{text: '启用', value: 1}, {text: '废弃', value: 0}]
            }),
            value: 1,
            columnWidth: 0.45,
            margin: '20 0 0 0'
        }, {
            xtype: 'querypicker',
            name: 'depId',
            fieldLabel: '部门<b style="color:red">*</b>',
            allowBlank: false,
            forceSelection: true,
            labelWidth: 70,
            labelAlign: 'right',
            columnWidth: 0.45,
            margin: '20 0 0 0',
            //valueField: 'id',
            displayField: 'depName',
            store: new Ext.data.TreeStore({
                fields: ['id', 'depName'],
                proxy: {
                    type: 'ajax',
                    url: GLOBAL_PATH + '/support/sys/dep/queryDepTreeByName'
                },
                root: {
                    id: 0,
                    depName: '组织机构',
                    expanded: true
                },
                autoLoad: true
            }),
            queryParam: 'depName'
        }, {
            xtype: 'combobox',
            name: 'caliberId',
            fieldLabel: '默认口径',
            labelWidth: 70,
            labelAlign: 'right',
            displayField: 'name',
            valueField: 'id',
            store: new Ext.data.Store({
                fields: ['id', 'name'],
                data: record ? record.get('itemCalibers') : []
            }),
            disabled: !record,
            columnWidth: 0.45,
            margin: '20 0 0 0',
            forceSelection: true
        }, {
            xtype: 'textarea',
            name: 'comments',
            fieldLabel: '备注',
            labelWidth: 70,
            labelAlign: 'right',
            columnWidth: 0.9,
            margin: '20 0 20 0'
        }]
    });
    var win = new Ext.addItemWin({
        items: [form],
        buttons: [{
            text: '保存',
            handler: function () {
                if (form.isValid()) {
                    form.submit({
                        url: Global_Path + '/saveItem',
                        success: function (form, action) {
                            Ext.Msg.alert('成功', action.result.msg);
                            win.close();
                            if (fn) {
                                fn(action.result.datas);
                            }
                        },
                        failure: function (form, action) {
                            Ext.Msg.alert('失败', action.result.msg);
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
        var dep = record.get('department');
        if (dep) {
            form.down('[name="depId"]').setValue(dep.id);
        }
        win.setTitle('修改指标窗口');
    }
    win.show();
    return win;
};
