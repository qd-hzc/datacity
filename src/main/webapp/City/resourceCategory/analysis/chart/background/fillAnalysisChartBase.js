/**
 * Created by wys on 2016/1/21.
 */
createModel('Ext.fillAnalysisChartBase', function () {
    Ext.define('Ext.fillAnalysisChartBase', {
        extend: 'Ext.window.Window',
        width: 600,
        height: 250,
        closeAction: 'destroy',
        modal: true
    });
});
Ext.fillAnalysisChartBase.init = function (fnt, groupId, record) {
    var chartBaseModel = createModel("chartBaseModel", function () {
        Ext.define('chartBaseModel', {
            extend: 'Ext.data.Model',
            idProperty: 'id',
            fields: [
                {name: 'id', type: 'int'},
                {name: 'title', type: 'string'},
                {name: 'subTitle', type: 'string'},
                {name: 'chartType', type: 'int'},
                {name: 'periodType', type: 'int'},
                {name: 'timeline', type: 'string'},
                {name: 'plug', type: 'string'},
                {name: 'chartStyle', type: 'string'},
                {name: 'groupId', type: 'int'},
                {name: 'chartSort', type: 'int'},
            ]
        });
    });
    var chartTypeStore = Ext.create('Ext.data.Store', {
        fields: ['chartTypeCH', 'chartType'],
        data: [
            {"chartTypeCH": ANALYSISCHART_TYPE.TYPE_STATIC_CH, "chartType": ANALYSISCHART_TYPE.TYPE_STATIC},
            {"chartTypeCH": ANALYSISCHART_TYPE.TYPE_DYMIC_CH, "chartType": ANALYSISCHART_TYPE.TYPE_DYMIC}
        ]
    });
    var chartTypeComb = Ext.create('Ext.form.ComboBox', {
        fieldLabel: '图表类型',
        store: chartTypeStore,
        columnWidth: 0.5,
        value: ANALYSISCHART_TYPE.TYPE_STATIC,
        queryMode: 'local',
        editable: false,
        allowBlank: false,
        name: 'chartType',
        displayField: 'chartTypeCH',
        valueField: 'chartType'
    });
    var periodTypeStore = Ext.create('Ext.data.Store', {
        fields: ['periodTypeCH', 'periodType'],
        data: [
            {"periodTypeCH": PERIOD_TYPE.YEAR_CH, "periodType": PERIOD_TYPE.YEAR},
            {"periodTypeCH": PERIOD_TYPE.HALF_CH, "periodType": PERIOD_TYPE.HALF},
            {"periodTypeCH": PERIOD_TYPE.QUARTER_CH, "periodType": PERIOD_TYPE.QUARTER},
            {"periodTypeCH": PERIOD_TYPE.MONTH_CH, "periodType": PERIOD_TYPE.MONTH}
        ]
    });
    var periodTypeComb = Ext.create('Ext.form.ComboBox', {
        fieldLabel: '周期',
        store: periodTypeStore,
        allowBlank: false,
        autoSelect: true,
        editable: false,
        value: PERIOD_TYPE.YEAR,
        columnWidth: 0.5,
        queryMode: 'local',
        name: 'periodType',
        displayField: 'periodTypeCH',
        valueField: 'periodType'
    });
    var plugStore = Ext.create('Ext.data.Store', {
        fields: ['chartTypeCH', 'chartType'],
        data: [
            {"plugCH": "百度图表", "plug": "echarts"},
            {"plugCH": "high图表", "plug": "highcharts"}
        ]
    });
    var plugComb = Ext.create('Ext.form.ComboBox', {
        fieldLabel: '插件',
        store: plugStore,
        editable: false,
        columnWidth: 0.5,
        value: 'echarts',
        queryMode: 'local',
        name: 'plug',
        allowBlank: false,
        displayField: 'plugCH',
        valueField: 'plug'
    });

    var isCreate = false;


    var form = new Ext.form.Panel({
        layout: 'vbox',
        width: '100%',
        height: '100%',
        defaults: {
            border: 0
        },
        items: [{
            xtype: 'panel',
            layout: 'column',
            width: '100%',
            defaults: {
                xtype: 'textfield',
                margin: '5 15',
                labelWidth: 80
            },
            items: [{
                fieldLabel: '标题<b style="color:red">*</b>',
                name: 'title',
                columnWidth: 0.5,
                allowBlank: false,
                maxLength:50,
                validator:function(text){
                    if(text.length && text.replace(/\s+/g, "").length<text.length){
                        return "不允许输入空格！";
                    }else {
                        return true;
                    }
                }
            }, {
                fieldLabel: '副标题',
                name: 'subTitle',
                columnWidth: 0.5,
                validator:function(text){
                    if(text.length && text.replace(/\s+/g, "").length<text.length){
                        return "不允许输入空格！";
                    }else {
                        return true;
                    }
                }

            }]
        }, {
            xtype: 'panel',
            layout: 'column',
            width: '100%',
            defaults: {
                xtype: 'textfield',
                margin: '5 15',
                labelWidth: 80
            },
            items: [chartTypeComb, periodTypeComb]
        }, {
            xtype: 'panel',
            layout: 'column',
            width: '100%',
            defaults: {
                xtype: 'textfield',
                margin: '5 15',
                labelWidth: 80
            },
            items: [{
                fieldLabel: '时间轴',
                name: 'timeline',
                columnWidth: 0.5,
                allowBlank: false,
                value: '0',
                validator: function (value) {
                    var re = new RegExp("^\\d+$", 'i');
                    var result = re.test(value);
                    if (result)
                        return true;
                    else
                        return '请输入数字';
                }
            }, plugComb]
        }, {
            xtype: 'panel',
            layout: 'column',
            width: '100%',
            defaults: {
                xtype: 'textfield',
                margin: '5 15',
                labelWidth: 80
            },
            items: [{
                fieldLabel: '样式',
                name: 'chartStyle',
                columnWidth: 0.5
            }, {
                fieldLabel: 'id',
                xtype: 'numberfield',
                name: 'id',
                hidden: true,
                columnWidth: 0.5
            }, {
                id: 'analysisChartBaseGroupId',
                fieldLabel: 'groupId',
                xtype: 'numberfield',
                name: 'groupId',
                hidden: true
            }]
        }]
    });
    if (!record) {
        isCreate = true;
    } else {
        form.loadRecord(record)
    }
    var groupIdField = Ext.getCmp('analysisChartBaseGroupId');
    groupIdField.setValue(groupId);
    var win = new Ext.fillAnalysisChartBase({
        title: "分析图表分组",
        layout: 'fit',
        items: [form],
        buttons: [{
            text: '保存',
            handler: function () {
                if (fnt && form.isValid()) {
                    fnt(form.getForm().getFieldValues());
                    win.close();
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