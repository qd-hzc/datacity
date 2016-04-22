/**
 * Created by wys on 2016/2/15.
 */
createModel('Ext.fillExcelMap', function () {
    Ext.define('Ext.fillExcelMap', {
        extend: 'Ext.window.Window',
        width: 625,
        //height: 535,
        modal: true
    });
});
/**
 * 初始化
 */
Ext.fillExcelMap.init = function (fnt, isCreate, record) {
    //报表表格
    createModel('ReportTemplate', function () {
        Ext.define('ReportTemplate', {
            extend: 'Ext.data.Model',
            fields: [{
                name: 'id',
                type: 'int'
            }, {
                name: 'name',
                type: 'string'
            }, {
                name: 'rptCode',
                type: 'string'
            }, {
                name: 'rptType',
                type: 'int'
            }, {
                name: 'frequency',
                type: 'string'
            }, {
                name: 'department'
            }, {
                name: 'submitDaysDelay',
                type: 'int'
            }, {
                name: 'researchObjType'
            }, {
                name: 'researchObjId'
            }, {
                name: 'period',
                type: 'int'
            }, {
                name: 'beginYear',
                type: 'int'
            }, {
                name: 'endYear',
                type: 'int'
            }, {
                name: 'beginPeriod',
                type: 'int'
            }, {
                name: 'endPeriod',
                type: 'int'
            }, {
                name: 'rptExplain',
                type: 'string'
            }, {
                name: 'rptComments',
                type: 'string'
            }, {
                name: 'status',
                type: 'int'
            }]
        });
    });
    var tmpStore = new Ext.data.Store({
        model: 'ReportTemplate',
        pageSize: 2,
        proxy: {
            type: 'ajax',
            url: GLOBAL_PATH + '/support/regime/report/getRptTmpsByCondition',
            reader: {
                type: 'json',
                rootProperty: 'datas'
            }
        },
        autoLoad: true
    });

    var form = new Ext.form.Panel({
        height: '100%',
        width: '100%',
        layout: 'vbox',
        default: {
            xtype: 'panel'
        },
        items: [{
            layout: 'column',
            border: 0,
            width: '100%',
            items: [{
                xtype: 'textfield',
                columnWidth: 0.5,
                margin: '5 15',
                labelWidth: 80,
                fieldLabel: 'excel名称',
                name: 'excelName'
            }, {
                xtype: 'textfield',
                margin: '5 15',
                columnWidth: 0.5,
                labelWidth: 80,
                fieldLabel: 'sheet名称',
                name: 'sheetName'
            }]
        }, {
            layout: 'column',
            width: '100%',
            border: 0,
            items: [{
                xtype: 'textfield',
                columnWidth: 0.5,
                margin: '5 15',
                labelWidth: 80,
                fieldLabel: '行',
                name: 'excelRow'
            }, {
                xtype: 'textfield',
                margin: '5 15',
                columnWidth: 0.5,
                labelWidth: 80,
                fieldLabel: '列',
                name: 'excelCol'
            }]
        }, {
            layout: 'column',
            width: '100%',
            border: 0,
            items: [{
                xtype: 'combo',
                columnWidth: 0.5,
                store: tmpStore,
                listConfig: {minWidth: 300},
                queryMode: 'remote',
                margin: '5 15',
                labelWidth: 80,
                queryParam: 'name',
                queryMode: 'remote',
                triggerAction: 'all',
                minChars: 1,
                pageSize: 2,
                displayField: 'name',
                valueField: 'id',
                fieldLabel: '报表',
                name: 'tmpId',
                listeners: {
                    select: function (comb, rec) {
                        var field = form.down('#tmpNameField');
                        field.setValue(rec.get('name'));
                    }
                }
            }, {
                xtype: 'textfield',
                id: 'tmpNameField',
                columnWidth: 0.5,
                hidden: true,
                margin: '5 15',
                labelWidth: 80,
                fieldLabel: '模板名称',
                name: 'tmpName'
            }]
        }]
    });
    if (!isCreate) {
        form.loadRecord(record)
    }
    var win = new Ext.fillExcelMap({
        layout: 'fit',
        items: [form],
        buttons: [{
            text: '保存',
            handler: function () {
                if (fnt) {
                    if (form.isValid()) {
                        fnt(form.getForm().getFieldValues());
                    }
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
    return win;
};