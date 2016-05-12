/**
 * 编辑数据字典数据窗口
 */
createModel('Ext.dataDict.EditDataDictInfoWin', function () {
    Ext.define('Ext.dataDict.EditDataDictInfoWin', {
        extend: 'Ext.window.Window',
        width: 550,
        modal: true,
        title: '数据字典数据'
    });
});
/**
 * 编辑数据字典窗口
 * @param menuId 目录
 * @param record 选中的记录
 * @param fn 回调
 */
Ext.dataDict.EditDataDictInfoWin.init = function (menuId, record, fn) {
    var form = new Ext.form.Panel({
        width: '100%',
        layout: 'column',
        border: false,
        items: [{
            xtype: 'hidden',
            name: 'id'
        }, {
            xtype: 'hidden',
            name: 'menuId',
            value: menuId
        }, {
            xtype: 'hidden',
            name: 'sortIndex'
        }, {
            xtype: 'textfield',
            name: 'dataName',
            fieldLabel: '名称<b style="color: red">*</b>',
            labelWidth: 70,
            labelAlign: 'right',
            allowBlank: false,
            columnWidth: 0.45,
            margin: '20 0 0 0'
        }, {
            xtype: 'textfield',
            name: 'groupName',
            fieldLabel: '分组<b style="color: red">*</b>',
            labelWidth: 70,
            labelAlign: 'right',
            allowBlank: false,
            columnWidth: 0.45,
            margin: '20 0 0 0'
        }, {
            xtype: 'combo',
            name: 'displayType',
            fieldLabel: '展示类型<b style="color: red">*</b>',
            labelWidth: 70,
            labelAlign: 'right',
            allowBlank: false,
            columnWidth: 0.45,
            margin: '20 0 0 0',
            store: new Ext.data.Store({
                fields: ['name', 'id'],
                data: DATA_DICT_DISPLAY_TYPE.getArr()
            }),
            value: DATA_DICT_DISPLAY_TYPE.CHART,
            displayField: 'name',
            valueField: 'id',
            editable: false
        }, {
            xtype: 'combo',
            name: 'status',
            fieldLabel: '状态<b style="color: red">*</b>',
            labelWidth: 70,
            labelAlign: 'right',
            allowBlank: false,
            columnWidth: 0.45,
            margin: '20 0 0 0',
            store: new Ext.data.Store({
                fields: ['name', 'id'],
                data: [{id: 1, name: '显示'}, {id: 0, name: '隐藏'}]
            }),
            value: 1,
            displayField: 'name',
            valueField: 'id',
            editable: false
        }, {
            xtype: 'combo',
            name: 'dataType',
            fieldLabel: '数据类型<b style="color: red">*</b>',
            labelWidth: 70,
            labelAlign: 'right',
            allowBlank: false,
            columnWidth: 0.45,
            margin: '20 0 20 0',
            store: new Ext.data.Store({
                fields: ['name', 'id'],
                data: DATA_DICT_TYPE.getArr()
            }),
            displayField: 'name',
            valueField: 'id',
            editable: false,
            listeners: {
                change: function (_this, n, o) {
                    //清空数据,并重新加载
                    var valueCombo = form.getForm().findField("dataValue");
                    valueCombo.setValue('');
                    valueCombo.getStore().load({params: {dataType: n}});
                    var statusField = form.getForm().findField("status");
                    if (n == DATA_DICT_TYPE.DATA_SET) {//数据集,设为隐藏并不可编辑
                        statusField.setValue(0);
                        statusField.setEditable(false);
                    } else if (o == DATA_DICT_TYPE.DATA_SET) {//从其他切换到数据集
                        statusField.setValue(1);
                        statusField.setEditable(true);
                    }
                }
            }
        }, {
            xtype: 'combo',
            name: 'dataValue',
            fieldLabel: '选择数据<b style="color: red">*</b>',
            labelWidth: 70,
            labelAlign: 'right',
            allowBlank: false,
            columnWidth: 0.45,
            margin: '20 0 20 0',
            store: new Ext.data.Store({
                fields: ['name', 'id'],
                proxy: {
                    type: 'ajax',
                    url: contextPath + '/resourcecategory/analysis/common/analysis/queryDataByType'
                },
                autoLoad: false,
                listeners: {
                    beforeload: function (_this, opration) {
                        var params = opration.getParams();
                        if (!params) {
                            params = {};
                        }
                        params.dataType = form.getForm().findField("dataType").getValue();
                        opration.setParams(params)
                    }
                }
            }),
            displayField: 'name',
            valueField: 'id',
            listeners: {
                "select": function (combo, record) {
                    form.getForm().findField("dataName").setValue(record.get("name"));
                }
            },
            queryParam: 'name',
            minChars: 0,
            forceSelection: true
        }]
    });
    if (record) {
        form.loadRecord(record);
    }
    var win = new Ext.dataDict.EditDataDictInfoWin({
        items: [form],
        buttons: [{
            text: '保存并新增',
            handler: function () {
                if (form.isValid()) {
                    form.submit({
                        url: APP_DATADICT_PATH + '/saveDict',
                        success: function (form, action) {
                            win.close();
                            Ext.dataDict.EditDataDictInfoWin.init(menuId, record, fn);
                            if (fn) {
                                fn(Ext.decode(action.result.datas));

                            }
                        },
                        failure: function () {
                            Ext.Msg.alert('提示', '请求发送失败!');
                        }
                    });
                }
            }
        }, {
            text: '保存',
            handler: function () {
                if (form.isValid()) {
                    form.submit({
                        url: APP_DATADICT_PATH + '/saveDict',
                        success: function (form, action) {
                            win.close();
                            if (fn) {
                                fn(Ext.decode(action.result.datas));
                            }
                        },
                        failure: function () {
                            Ext.Msg.alert('提示', '请求发送失败!');
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
    win.show();
};
