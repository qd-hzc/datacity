/**
 * 编辑指标窗口
 * Created by wxl on 2016/2/26.
 */
createModel('Ext.dataSet.EditItemWin', function () {
    Ext.define('Ext.dataSet.EditItemWin', {
        extend: 'Ext.window.Window',
        width: 550,
        modal: true,
        title: '编辑指标窗口'
    });
});
/**
 * 编辑指标窗口
 * @param record 当前指标
 * @param fn
 */
Ext.dataSet.EditItemWin.init = function (record, fn) {
    var itemId = record.get('dataValue');
    var form = new Ext.form.Panel({
        width: '100%',
        layout: 'column',
        items: [{
            xtype: 'hidden',
            name: 'id'
        }, {
            xtype: 'hidden',
            name: 'dataType'
        }, {
            xtype: 'hidden',
            name: 'dataValue'
        }, {
            xtype: 'hidden',
            name: 'dataSetId'
        }, {
            xtype: 'textfield',
            name: 'dataName',
            editable: false,
            columnWidth: 0.45,
            fieldLabel: '指标名',
            labelWidth: 70,
            labelAlign: 'right',
            margin: '20 0 20 0'
        }, {
            xtype: 'combobox',
            name: 'dataInfo1',
            columnWidth: 0.45,
            fieldLabel: '指标口径',
            labelWidth: 70,
            labelAlign: 'right',
            store: new Ext.data.Store({
                fields: ['id', 'name'],
                proxy: {
                    type: 'ajax',
                    url: baseUrl + '/getInfoList',
                    extraParams: {
                        infoType: DATA_INFO_TYPE.CALIBER,
                        itemId: itemId
                    }
                },
                autoLoad: true
            }),
            valueField: 'id',
            displayField: 'name',
            queryModeL: 'local',
            enableRegEx: true,
            margin: '20 0 20 0'
        }, {
            xtype: 'combobox',
            name: 'dataInfo2',
            columnWidth: 0.45,
            fieldLabel: '上报部门',
            labelWidth: 70,
            labelAlign: 'right',
            store: new Ext.data.Store({
                fields: ['id', 'name'],
                proxy: {
                    type: 'ajax',
                    url: baseUrl + '/getInfoList',
                    extraParams: {
                        infoType: DATA_INFO_TYPE.DEP,
                        itemId: itemId
                    }
                },
                autoLoad: true
            }),
            valueField: 'id',
            displayField: 'name',
            queryModeL: 'local',
            enableRegEx: true,
            margin: '0 0 20 0'
        }, {
            xtype: 'combobox',
            name: 'dataInfo3',
            columnWidth: 0.45,
            fieldLabel: '所属报表',
            labelWidth: 70,
            labelAlign: 'right',
            store: new Ext.data.Store({
                fields: ['id', 'name'],
                proxy: {
                    type: 'ajax',
                    url: baseUrl + '/getInfoList',
                    extraParams: {
                        infoType: DATA_INFO_TYPE.RPT,
                        itemId: itemId
                    }
                },
                autoLoad: true
            }),
            valueField: 'id',
            displayField: 'name',
            queryModeL: 'local',
            enableRegEx: true,
            margin: '0 0 20 0'
        }]
    });
    form.loadRecord(record);
    var win = new Ext.dataSet.EditItemWin({
        items: [form],
        buttons: [{
            text: '保存',
            handler: function () {
                form.submit({
                    url: baseUrl + '/editDataSetData',
                    success: function (form, action) {
                        if (action.result.success) {
                            win.close();
                            fn();
                        } else {
                            Ext.Msg.alert('失败', action.result.msg);
                        }
                    },
                    failure: function (form, action) {
                        if (action.failureType == Ext.form.action.Action.SERVER_INVALID) {
                            Ext.Msg.alert('失败', action.result.msg);
                        } else {
                            Ext.Msg.alert('失败', '保存失败');
                        }
                    }
                });
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
