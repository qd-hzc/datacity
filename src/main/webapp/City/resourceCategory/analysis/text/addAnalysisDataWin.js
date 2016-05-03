/**
 * Created by wgx on 2016/3/18.
 */
createModel('Ext.addAnalysisDataWin', function () {
    Ext.define('Ext.addAnalysisDataWin', {
        extend: 'Ext.window.Window',
        width: 500,
        height: 350,
        closeAction: 'destroy',
        modal: true
    });
});
function getContents(combo, record) {
    /**
     * 根据内容类型获取内容，并设置到内容的combobox中
     */
    var container = Ext.getCmp('dataName');
    var type = parseInt(record.getId());
    var url = contextPath;
    switch (type) {
        case 1://综合表:不分报告期
            url += '/support/regime/report/getAllReport';
            var config = {proxy: {type: 'ajax', api: {read: url}}};
            container.getStore().setConfig(config).load();
            break;
        case 2://自定义表
            url += '/resourcecategory/analysis/report/designCustomResearch/getAllCustomResearch';
            var config = {proxy: {type: 'ajax', api: {read: url}}};
            container.getStore().setConfig(config).load();
            break;
        case 3://图表
            url += '/support/resourcecategory/analysis/chart/queryAllChartExceptMap';
            var config = {proxy: {type: 'ajax', api: {read: url}}};
            container.getStore().setConfig(config).load();
            break;
        case 4://地图
            url += '/support/resourcecategory/analysis/chart/queryAllMap';
            var config = {proxy: {type: 'ajax', api: {read: url}}};
            container.getStore().setConfig(config).load();
            break;
    }

}
Ext.addAnalysisDataWin.init = function (record, foreignType, fn) {
    var form = new Ext.form.Panel({
        width: '100%',
        height: '100%',
        layout: 'column',
        border: false,
        items: [{
            xtype: 'hidden',
            name: 'id'
        }, {
            xtype: 'hidden',
            name: 'foreignType',
            value: foreignType
        }, {
            xtype: 'hidden',
            name: 'foreignId',
            value: record.getId()
        }, {
            id: "dataValue",
            xtype: 'hidden',
            name: 'dataValue'
        }, {
            xtype: 'combobox',
            name: 'dataType',
            fieldLabel: '类型',
            labelWidth: 70,
            labelAlign: 'right',
            allowBlank: false,
            displayField: 'name',
            valueField: 'id',
            store: new Ext.data.Store({
                fields: ['id', 'name'],
                data: TEXT_DATA_TYPE.getArr(),
                autoLoad: true
            }),
            columnWidth: 0.9,
            margin: '20 0 0 0',
            listeners: {
                select: function (combo, record, eOpts) {
                    getContents(combo, record);
                }
            }
        }, {
            xtype: 'combobox',
            id: 'dataName',
            name: 'dataName',
            fieldLabel: '名称',
            labelWidth: 70,
            labelAlign: 'right',
            allowBlank: false,
            displayField: 'name',
            valueField: 'name',
            store: new Ext.data.Store({
                fields: ['id', 'name'],
                autoLoad: true
            }),
            columnWidth: 0.9,
            margin: '20 0 50 0',
            listeners: {
                select: function (combo, record, eOpts) {
                    var dataValue = Ext.getCmp('dataValue');
                    dataValue.setValue(record.getId());
                }
            }
        }]
    });
    var win = new Ext.window.Window({
        title: '添加内容窗口',
        width: 500,
        modal: true,
        items: [form],
        buttons: [{
            text: '保存',
            handler: function () {
                if (form.isValid()) {
                    if (fn) {
                        fn(form.getForm().getFieldValues());
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

}

