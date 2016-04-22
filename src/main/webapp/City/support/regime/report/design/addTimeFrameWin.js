/**
 * 弹出添加指标确认窗口
 *
 * Created by HZC on 2016/1/20.
 */

/**
 * @param record 拖动目标节点
 * @param root 是否是root节点
 * @param callback 回调函数
 */
function showAddTimeFrameWindow(record, root, callback) {
    var name = record.get('dataName');
    var menuBar = Ext.create('Ext.form.Panel', {
        width: '100%',
        layout: 'column',
        items: [{
            xtype: 'radiogroup',
            name: 'radiogroup',
            fieldLabel: '拖拽级别',
            labelAlign: 'right',
            labelWidth: 70,
            columnWidth: 0.9,
            margin: '20 0 0 0',
            items: [
                {boxLabel: '加为下级', name: 'target', inputValue: 'low', checked: true},
                {boxLabel: '加为同级', name: 'target', inputValue: 'same', disabled: root},
                {boxLabel: '加为属性', name: 'target', inputValue: 'property', disabled: root}
            ],
            listeners: {
                change: function (_this, newValue) {
                    if (newValue.target == 'property') {
                        menuBar.down('[name="dataName"]').setDisabled(true);
                    } else {
                        menuBar.down('[name="dataName"]').setDisabled(false);
                    }
                }
            }
        }, {
            xtype: 'textfield',
            name: 'dataName',
            fieldLabel: '名称<b style="color:red">*</b>',
            labelWidth: 70,
            columnWidth: 0.9,
            margin: '20 0 20 0',
            labelAlign: 'right',
            allowBlank: false,
            value: name
        }, {
            xtype: 'hidden',
            name: 'dataType',
            value: record.get('dataType')
        }, {
            xtype: 'hidden',
            name: 'dataValue',
            value: record.get('dataValue')
        }, {
            xtype: 'hidden',
            name: 'dataInfo1',
            value: record.get('dataInfo1')
        }, {
            xtype: 'hidden',
            name: 'dataInfo2',
            value: record.get('dataInfo2')
        }]
    });
    var mw = Ext.create('Ext.window.Window', {
        width: 500,
        modal: true,
        title: '信息',
        items: [menuBar],
        buttons: [
            {
                text: '确定',
                handler: function () {
                    var addItemResult = menuBar.getForm().getValues();
                    var target = addItemResult.target;
                    var dataName = addItemResult.dataName;
                    if (!dataName && target != 'property') {
                        return;
                    }
                    addItemResult.dataName = name;
                    if (target != 'property') {
                        record.set('dataName', dataName);
                        record.set('dataValue', null);
                        var properties = [];
                        properties.push(getSimpleObj(addItemResult));
                        record.set('properties', properties);
                    }
                    callback(target, record, addItemResult);
                    mw.close();
                }
            },
            '-',
            {
                text: '取消',
                handler: function () {
                    mw.close();
                }
            }
        ]
    });
    mw.show();
    /**获取简版obj*/
    function getSimpleObj(obj) {
        return {
            dataName: obj.dataName,
            dataValue: obj.dataValue,
            dataType: obj.dataType,
            dataInfo1: obj.dataInfo1,
            dataInfo2: obj.dataInfo2,
            isRealNode: obj.isRealNode || 0,
            isProperty: obj.isProperty || 0
        };
    }
}
