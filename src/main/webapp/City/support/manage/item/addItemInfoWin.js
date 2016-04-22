/**
 * Created by Administrator on 2016/1/5 0005.
 * 添加指标下的时间框架和单位
 */
createModel('Ext.addItemInfoWin', function () {
    Ext.define('Ext.addItemInfoWin', {
        extend: 'Ext.window.Window',
        width: 500,
        modal: true,
        title: '添加指标信息窗口'
    });
});
/**
 * 添加/保存指标信息窗口
 * @param itemId 指标id,修改时可为空
 * @param record 信息,添加时为空即可
 * @param fn 回调
 */
Ext.addItemInfoWin.init = function (itemId, record, fn) {
    var form = new Ext.form.Panel({
        width: '100%',
        layout: 'column',
        items: [{
            xtype: 'hidden',
            name: 'id'
        }, {
            xtype: 'hidden',
            name: 'itemId',
            value: itemId
        }, {
            xtype: 'textfield',
            name: 'name',
            allowBlank: true,
            fieldLabel: '信息名<b style="color:red">*</b>',
            labelWidth: 70,
            labelAlign: 'right',
            columnWidth: 0.9,
            margin: '20 0 0 0',
            hidden: true
        }, {
            xtype: 'combobox',
            name: 'timeFrameId',
            allowBlank: false,
            forceSelection: true,
            fieldLabel: '时间框架<b style="color:red">*</b>',
            labelWidth: 70,
            labelAlign: 'right',
            displayField: 'name',
            valueField: 'id',
            store: new Ext.data.Store({
                fields: ['id', 'name'],
                proxy: {
                    url: GLOBAL_PATH + '/support/manage/timeFrame/findTimeFrameByName',
                    type: 'ajax'
                },
                autoLoad: true
            }),
            columnWidth: 0.45,
            margin: '20 0 0 0',
            minChars: 0,
            queryParam: 'name'
        }, {
            xtype: 'combobox',
            name: 'unitId',
            allowBlank: false,
            forceSelection: true,
            fieldLabel: '单位<b style="color:red">*</b>',
            labelWidth: 70,
            labelAlign: 'right',
            displayField: 'name',
            valueField: 'id',
            store: new Ext.data.Store({
                fields: ['id', 'name', 'dataType', 'dataFormat'],
                proxy: {
                    url: GLOBAL_PATH + '/support/unit/unitManager/findUnitByName',
                    type: 'ajax'
                },
                autoLoad: true
            }),
            columnWidth: 0.45,
            margin: '20 0 0 0',
            minChars: 0,
            queryParam: 'name',
            listeners: {
                select: function (_this, rec) {
                    //数据类型
                    form.down('[name="dataTypeId"]').setValue(rec.get('dataType').id);
                    //数据格式
                    form.down('[name="dataFormat"]').setValue(rec.get('dataFormat'));
                }
            }
        }, {
            xtype: 'combobox',
            name: 'dataTypeId',
            allowBlank: false,
            forceSelection: true,
            fieldLabel: '数据类型<b style="color:red">*</b>',
            labelWidth: 70,
            labelAlign: 'right',
            displayField: 'name',
            valueField: 'id',
            store: new Ext.data.Store({
                fields: ['id', 'name', 'dataFormat'],
                proxy: {
                    url: GLOBAL_PATH + '/support/unit/unitManager/findDataTypeByName',
                    type: 'ajax'
                },
                autoLoad: true
            }),
            columnWidth: 0.45,
            margin: '20 0 20 0',
            minChars: 0,
            queryParam: 'name',
            listeners: {
                select: function (_this, rec) {
                    //数据格式
                    form.down('[name="dataFormat"]').setValue(rec.get('dataFormat'));
                }
            }
        }, {
            xtype: 'textfield',
            name: 'dataFormat',
            allowBlank: false,
            fieldLabel: '数据格式<b style="color:red">*</b>',
            labelWidth: 70,
            labelAlign: 'right',
            columnWidth: 0.45,
            margin: '20 0 20 0'
        }]
    });
    var win = new Ext.addItemInfoWin({
        items: [form],
        buttons: [{
            text: '保存',
            handler: function () {
                if (form.isValid()) {
                    form.submit({
                        url: Global_Path + '/saveItemInfo',
                        success: function (form, action) {
                            Ext.Msg.alert('成功', action.result.msg);
                            win.close();
                            if (fn) {
                                fn(action.result.datas);
                            }
                        },
                        failure: function (form, action) {
                            Ext.Msg.alert('失败', "保存失败");
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
        //加载下拉框
        form.down('[name="unitId"]').setValue(record.get('unit').id);
        form.down('[name="timeFrameId"]').setValue(record.get('timeFrame').id);
        form.down('[name="dataTypeId"]').setValue(record.get('dataType').id);
        win.setTitle('修改指标信息窗口');
    }
    win.show();
};
