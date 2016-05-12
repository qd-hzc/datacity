/**
 * Created by wys on 2016/3/3.
 */
createModel('Ext.fillChartInfoWin', function () {
    Ext.define('Ext.fillChartInfoWin', {
        extend: 'Ext.window.Window',
        width: 450,
        height: 250,
        closeAction: 'destroy',
        modal: true
    });
});
Ext.fillChartInfoWin.init = function (fnt, records, infoType) {
    var isSingle = true;
    var rec = null;
    if (records && records.length > 1) {
        isSingle = false;
    } else if (records && records.length > 0) {
        rec = records[0];
    }
//==========================名称begin============================
    var nameField = new Ext.form.field.Text({
        name: 'name',
        fieldLabel: '名称<b style="color:red">*</b>',
        columnWidth: 1,
        labelWidth: 60,
        margin: '5 15',
        maxLength:50,
        allowBlank: false,  // requires a non-empty value
        validator:function(text){
            if(text.length && text.replace(/\s+/g, "").length<text.length){
                return "不允许输入空格！";
            }else {
                return true;
            }
        }
    });
    var namePanel = new Ext.panel.Panel({
        width: '100%',
        layout: 'column',
        border: 0,
        items: [nameField]
    });
    //==========================名称end============================
    if (infoType == ANALYSISCHART_INFO.TYPE_SERIES) {
        //==========================第一行begin=============================
        var chartTypeStore = Ext.create('Ext.data.Store', {
            fields: ['chartTypeCH', 'chartType'],
            data: [
                {"chartTypeCH": ANALYSISCHART_INFO.CHART_LINE_CH, "chartType": ANALYSISCHART_INFO.CHART_LINE},
                {"chartTypeCH": ANALYSISCHART_INFO.CHART_CURVE_CH, "chartType": ANALYSISCHART_INFO.CHART_CURVE},
                {"chartTypeCH": ANALYSISCHART_INFO.CHART_COLUMN_CH, "chartType": ANALYSISCHART_INFO.CHART_COLUMN},
                {"chartTypeCH": ANALYSISCHART_INFO.CHART_PIE_CH, "chartType": ANALYSISCHART_INFO.CHART_PIE},
                {"chartTypeCH": ANALYSISCHART_INFO.CHART_SCATTER_CH, "chartType": ANALYSISCHART_INFO.CHART_SCATTER},
                {"chartTypeCH": ANALYSISCHART_INFO.CHART_MAP_CH, "chartType": ANALYSISCHART_INFO.CHART_MAP}
            ]
        });
        var chartTypeComb = Ext.create('Ext.form.ComboBox', {
            fieldLabel: '图表类型',
            store: chartTypeStore,
            labelWidth: 60,
            columnWidth: 0.5,
            margin: '5 15',
            value: ANALYSISCHART_INFO.CHART_LINE_CH,
            queryMode: 'local',
            editable: false,
            allowBlank: false,
            name: 'chartType',
            displayField: 'chartTypeCH',
            valueField: 'chartType'
        });
        var axisStore = Ext.create('Ext.data.Store', {
            fields: ['axisCH', 'axis'],
            data: [
                {"axisCH": ANALYSISCHART_INFO.LEFTAXIS_CH, "axis": ANALYSISCHART_INFO.LEFTAXIS},
                {"axisCH": ANALYSISCHART_INFO.RIGHTAXIS_CH, "axis": ANALYSISCHART_INFO.RIGHTAXIS}
            ]
        });
        var axisComb = Ext.create('Ext.form.ComboBox', {
            fieldLabel: '坐标轴',
            store: axisStore,
            columnWidth: 0.5,
            labelWidth: 60,
            margin: '5 15',
            value: ANALYSISCHART_INFO.LEFTAXIS,
            queryMode: 'local',
            editable: false,
            allowBlank: false,
            name: 'axis',
            displayField: 'axisCH',
            valueField: 'axis'
        });
        var firstPanel = new Ext.panel.Panel({
            width: '100%',
            layout: 'column',
            border: 0,
            items: [chartTypeComb, axisComb]
        });

        //==========================第一行end=============================

        //==========================第二行begin=============================
        var groupField = new Ext.form.field.Text({
            name: 'group',
            fieldLabel: '分组',
            columnWidth: 0.5,
            labelWidth: 60,
            margin: '5 15',
            allowBlank: true  // requires a non-empty value
        });
        var isShowStore = Ext.create('Ext.data.Store', {
            fields: ['isShowCH', 'isShow'],
            data: [
                {"isShowCH": ANALYSISCHART_INFO.SERIES_SHOW_CH, "isShow": ANALYSISCHART_INFO.SERIES_SHOW},
                {"isShowCH": ANALYSISCHART_INFO.SERIES_HIDE_CH, "isShow": ANALYSISCHART_INFO.SERIES_HIDE}
            ]
        });
        var isShowComb = Ext.create('Ext.form.ComboBox', {
            fieldLabel: '是否显示',
            store: isShowStore,
            columnWidth: 0.5,
            labelWidth: 60,
            margin: '5 15',
            value: ANALYSISCHART_INFO.SERIES_SHOW,
            queryMode: 'local',
            editable: false,
            allowBlank: false,
            name: 'isShow',
            displayField: 'isShowCH',
            valueField: 'isShow'
        });
        var secondPanel = new Ext.panel.Panel({
            width: '100%',
            layout: 'column',
            border: 0,
            items: [groupField, isShowComb]
        });

        //==========================第二行end=============================
    }
    //==========================说明begin============================
    var chartInfoField = new Ext.form.field.TextArea({
        name: 'info',
        fieldLabel: '说明',
        columnWidth: 1,
        labelWidth: 60,
        margin: '5 15',
        allowBlank: true  // requires a non-empty value
    });
    var chartInfoPanel = new Ext.panel.Panel({
        width: '100%',
        layout: 'column',
        border: 0,
        items: [chartInfoField]
    });
    //==========================说明end============================
    var form = new Ext.form.Panel({
        layout: 'vbox'
    });
    if (infoType == ANALYSISCHART_INFO.TYPE_SERIES) {
        if (isSingle) {
            form.add(namePanel)
        }
        form.add([firstPanel, secondPanel, chartInfoPanel]);
    } else {
        if(isSingle)
            form.add([namePanel, chartInfoPanel]);
        else
            form.add(chartInfoPanel);
    }
    if (isSingle) {
        form.loadRecord(rec);
    }
    var win = new Ext.fillChartInfoWin({
        title: "分析图表分组",
        layout: 'fit',
        items: [form],
        buttons: [{
            text: '保存',
            handler: function () {
                if (fnt && form.isValid(), records) {
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