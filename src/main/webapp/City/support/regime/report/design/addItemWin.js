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
function showAddItemWindow(record, root, callback) {
    //口径store
    //部门store
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
            margin: '20 0 0 0',
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
            xtype: 'combobox',
            fieldLabel: '口径',
            labelWidth: 70,
            columnWidth: 0.9,
            name: 'dataInfo1',
            margin: '20 0 20 0',
            labelAlign: 'right',
            displayField: 'name',
            valueField: 'id',
            value: record.get('dataInfo1'),
            store: Ext.create('Ext.data.Store', {
                fields: ['id', 'name'],
                proxy: {
                    type: 'ajax',
                    url: contextPath + '/support/regime/report/designReport/getItemCaliber',
                    extraParams: {
                        itemId: record.get('dataValue')
                    }
                },
                autoLoad: true
            })
        }, {
            xtype: 'combobox',
            fieldLabel: '部门<b style="color:red">*</b>',
            labelWidth: 70,
            name: 'dataInfo2',
            columnWidth: 0.9,
            margin: '0 0 20 0',
            labelAlign: 'right',
            display: 'none',
            displayField: 'depName',
            value: record.get('dataInfo2'),
            valueField: 'id',
            allowBlank: false,
            store: Ext.create('Ext.data.Store', {
                fields: ['id', 'depName'],
                proxy: {
                    type: 'ajax',
                    url: GLOBAL_PATH + '/support/resourcecategory/analysis/chart/getItemDep',
                    extraParams: {
                        itemId: record ? record.get('dataValue') : 0
                    }
                },
                autoLoad: true
            }),
            hidden: rptDesignType == RPT_DESIGN_TYPE.SYNTHESIS
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
                    var department = addItemResult.dataInfo2;
                    var caliber = addItemResult.dataInfo1;
                    var displayName = addItemResult.dataName;
                    if (!displayName && target != 'property') {
                        return;
                    }
                    record.set('dataInfo2', department);
                    record.set('dataInfo1', caliber);
                    addItemResult.dataName = name;
                    if (target != 'property') {
                        record.set('dataName', displayName);
                        record.set('text', displayName);
                        record.set('dataValue', null);
                        record.set('dataInfo1', null);
                        record.set('dataInfo2', null);
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
            text: obj.dataName,
            dataValue: obj.dataValue,
            dataType: obj.dataType,
            dataInfo1: obj.dataInfo1,
            dataInfo2: obj.dataInfo2,
            isRealNode: obj.isRealNode || 0,
            isProperty: obj.isProperty || 0
        };
    }
}
