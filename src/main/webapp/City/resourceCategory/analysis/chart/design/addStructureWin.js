/**
 * Created by wgx on 2016/3/2.
 */
/**
 * @param record 拖动目标节点
 * @param callback 回调函数
 */
function showAddStructureWindow(record, callback) {
    //公共变量
    var commonObj = {
        initParams: {
            caliber: '',
            dep: '',
            rptTmp: ''
        }
    };
    console.log(record);
    var name = record.get('dataName');
    var itemId = record.get('dataValue') ? record.get('dataValue') : record.get('metaId');
    if (record.get('metaExt')) {
        var metaExt = Ext.decode(record.get('metaExt'));
        commonObj.initParams.caliber = metaExt.caliber;
        commonObj.initParams.dep = metaExt.dep;
        commonObj.initParams.rptTmp = metaExt.rptTmp;
    }
    var form = Ext.create('Ext.form.Panel', {
        width: '100%',
        layout: 'column',
        items: [{
            xtype: 'hidden',
            name: 'dataType',
            value: record.get('dataType')
        }, {
            xtype: 'hidden',
            name: 'dataValue',
            value: itemId
        }, {
            xtype: 'combobox',
            id: "caliber",
            fieldLabel: '口径',
            labelWidth: 70,
            columnWidth: 0.9,
            name: 'caliber',
            margin: '20 0 10 0',
            labelAlign: 'right',
            displayField: 'name',
            valueField: 'id',
            value: commonObj.initParams.caliber ? commonObj.initParams.caliber : null,
            store: Ext.create('Ext.data.Store', {
                fields: ['id', 'name'],
                proxy: {
                    type: 'ajax',
                    url: GLOBAL_PATH + '/support/resourcecategory/analysis/chart/getItemCaliber',
                    extraParams: {
                        itemId: itemId
                    }
                },
                autoLoad: true
            })
        }, {
            xtype: 'combobox',
            id: 'dep',
            fieldLabel: '部门<b style="color:red">*</b>',
            labelWidth: 70,
            columnWidth: 0.9,
            name: 'dep',
            margin: '10 0 10 0',
            labelAlign: 'right',
            displayField: 'depName',
            valueField: 'id',
            allowBlank: false,
            value: commonObj.initParams.dep ? commonObj.initParams.dep : null,
            store: Ext.create('Ext.data.Store', {
                fields: ['id', 'depName'],
                proxy: {
                    type: 'ajax',
                    url: GLOBAL_PATH + '/support/resourcecategory/analysis/chart/getItemDep',
                    extraParams: {
                        itemId: itemId
                    }
                },
                autoLoad: true
            })
        }, {
            xtype: 'combobox',
            id: 'rptTmp',
            fieldLabel: '报表',
            labelWidth: 70,
            columnWidth: 0.9,
            name: 'rptTmp',
            margin: '10 0 20 0',
            labelAlign: 'right',
            displayField: 'name',
            valueField: 'id',
            value: commonObj.initParams.rptTmp ? commonObj.initParams.rptTmp : null,
            store: Ext.create('Ext.data.Store', {
                fields: ['id', 'name'],
                proxy: {
                    type: 'ajax',
                    url: GLOBAL_PATH + '/support/resourcecategory/analysis/chart/getItemReportTemplate',
                    extraParams: {
                        itemId: itemId
                    }
                },
                autoLoad: true
            })
        }]
    });

    var mw = Ext.create('Ext.window.Window', {
        width: 500,
        modal: true,
        title: '信息',
        items: [form],
        buttons: [
            {
                text: '确定',
                handler: function () {
                    var addStructureResult = form.getForm().getValues();
                    if (form.isValid()) {
                        console.log(getMeatExtObj(addStructureResult))
                        callback(getMeatExtObj(addStructureResult));
                        mw.close();
                    }
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
    /** 获取meatExt*/
    function getMeatExtObj(obj) {
        return {
            caliber: obj.caliber,
            caliberName: Ext.getCmp('caliber').displayTplData && Ext.getCmp('caliber').displayTplData[0] ? Ext.getCmp('caliber').displayTplData[0].name : "0",
            dep: obj.dep,
            depName: Ext.getCmp('dep').displayTplData[0].depName,
            rptTmp: obj.rptTmp,
            rptName: Ext.getCmp('rptTmp').displayTplData && Ext.getCmp('rptTmp').displayTplData[0] ? Ext.getCmp('rptTmp').displayTplData[0].name : "",
        };
    }
}
