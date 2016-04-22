/**
 * Created by wgx on 2016/3/3.
 */
//添加时间的窗口
Ext.define('Ext.addStructureTimeDataWin', {
    extend: 'Ext.window.Window',
    width: 500,
    title: '选择时间窗口',
    closeAction: 'destroy',
    modal: true
});
//fres: 频度,fn:回调
Ext.addStructureTimeDataWin.init = function (fres, chartId, fn, record) {


    //公共变量
    var commonObj = {
        timeScope: 1,
        initParams: {
            bt: '',//开始报告期
            by: '',//开时年
            et: '',//结束报告期
            ey: '',//结束年
            mt: [],//多选报告期
            my: [],//多选年
            n: ''//最新报告期
        }
    };
    if (record) {
        if (Ext.decode(record.get('metaExt'))) {
            var timeRange = Ext.decode(record.get('metaExt')).timeRange;
            commonObj.timeScope = timeRange[0].type;
            var my=[];
            var mt=[];
            for (var i = 0; i < timeRange.length; i++) {
                switch (timeRange[i].dataType) {
                    case 1:
                        commonObj.initParams.by=timeRange[i].dataValue;
                        break;
                    case 2:
                        commonObj.initParams.bt=timeRange[i].dataValue;
                        break;
                    case 3:
                        commonObj.initParams.ey=timeRange[i].dataValue;
                        break;
                    case 4:
                        commonObj.initParams.et=timeRange[i].dataValue;
                        break;
                    case 5:
                        my.push(timeRange[i].dataValue);

                        break;
                    case 6:
                        mt.push(timeRange[i].dataValue);

                        break;
                    case 7:
                        commonObj.initParams.n=timeRange[i].dataValue;
                        break;
                }
            }
            if(my.length){
                commonObj.initParams.my=my;
            }
            if(mt.length){
                commonObj.initParams.mt=mt;
            }
        }

    }
    //连续选择面板
    var continuousTimes = new Ext.panel.Panel({
        width: '100%',
        height: '100%',
        layout: 'column',
        border: 0,
        hidden: commonObj.timeScope != 1,
        items: [{
            xtype: 'combo',
            id: 'beginYearWinCombo',
            store: new Ext.data.Store({
                fields: ['value', 'text'],
                proxy: {
                    type: 'ajax',
                    url: GLOBAL_PATH + '/support/manage/metadata/getAllYears',
                    extraParams: {
                        sortType: 1
                    }
                },
                autoLoad: true
            }),
            fieldLabel: '开始年',
            labelWidth: 70,
            labelAlign: 'right',
            columnWidth: 0.45,
            margin: '10 0 0 10',
            displayField: 'text',
            valueField: 'value',
            queryMode: 'local',
            value: commonObj.initParams.by
        }, {
            xtype: 'combo',
            id: 'beginTimeWinCombo',
            store: Ext.create('Ext.data.Store', {
                fields: ['text', 'value'],
                data: fres
            }),
            fieldLabel: '开始报告期',
            labelWidth: 70,
            labelAlign: 'right',
            columnWidth: 0.45,
            margin: '10 0 0 10',
            displayField: 'text',
            valueField: 'value',
            queryMode: 'local',
            value: commonObj.initParams.bt
        }, {
            xtype: 'combo',
            id: 'endYearWinCombo',
            store: new Ext.data.Store({
                fields: ['value', 'text'],
                proxy: {
                    type: 'ajax',
                    url: GLOBAL_PATH + '/support/manage/metadata/getAllYears',
                    extraParams: {
                        sortType: -1,
                        beginItem: 1
                    }
                },
                autoLoad: true
            }),
            fieldLabel: '结束年',
            labelWidth: 70,
            labelAlign: 'right',
            columnWidth: 0.45,
            margin: '10 0 0 10',
            displayField: 'text',
            valueField: 'value',
            queryMode: 'local',
            value: commonObj.initParams.ey
        }, {
            xtype: 'combo',
            id: 'endTimeWinCombo',
            store: Ext.create('Ext.data.Store', {
                fields: ['text', 'value'],
                data: fres.reverse()
            }),
            fieldLabel: '结束报告期',
            labelWidth: 70,
            labelAlign: 'right',
            columnWidth: 0.45,
            margin: '10 0 0 10',
            displayField: 'text',
            valueField: 'value',
            queryMode: 'local',
            value: commonObj.initParams.et
        }]
    });
    //多选选择窗口
    var multiSelectTimes = new Ext.panel.Panel({
        width: '100%',
        height: '100%',
        layout: 'column',
        border: 0,
        hidden: commonObj.timeScope != 2,
        items: [{
            xtype: 'combo',
            id: 'multiYearWinCombo',
            store: new Ext.data.Store({
                fields: ['value', 'text'],
                proxy: {
                    type: 'ajax',
                    url: GLOBAL_PATH + '/support/manage/metadata/getAllYears',
                    extraParams: {
                        sortType: -1,
                        beginItem: 1
                    }
                },
                autoLoad: true
            }),
            fieldLabel: '选择年',
            labelWidth: 70,
            labelAlign: 'right',
            columnWidth: 0.45,
            margin: '10 0 0 10',
            displayField: 'text',
            valueField: 'value',
            queryMode: 'local',
            multiSelect: true,
            value: commonObj.initParams.my
        }, {
            xtype: 'combo',
            id: 'multiTimeWinCombo',
            store: Ext.create('Ext.data.Store', {
                fields: ['text', 'value'],
                data: fres
            }),
            fieldLabel: '选择报告期',
            labelWidth: 70,
            labelAlign: 'right',
            columnWidth: 0.45,
            margin: '10 0 0 10',
            displayField: 'text',
            valueField: 'value',
            queryMode: 'local',
            multiSelect: true,
            value: commonObj.initParams.mt
        }]
    });
    //最新报告期窗口
    var newestTimes = new Ext.panel.Panel({
        width: '100%',
        height: '100%',
        layout: 'column',
        border: 0,
        hidden: commonObj.timeScope != 3,
        items: [{
            xtype: 'numberfield',
            id: 'newestTimesWinField',
            fieldLabel: '最新报告期',
            labelWidth: 70,
            labelAlign: 'right',
            columnWidth: 0.9,
            margin: '10 0 0 10',
            value: commonObj.initParams.n
        }]
    });
    //时间轴
    var timeDataPanel = new Ext.panel.Panel({
        width: '100%',
        items: [{
            xtype: 'panel',
            width: '100%',
            border: 0,
            layout: 'vbox',
            margin: '10 0 0 0',
            items: [{
                xtype: 'radiogroup',
                fieldLabel: '时间跨度',
                labelWidth: 70,
                labelAlign: 'right',
                width: '100%',
                flex: 1,
                margin: '10 0 0 20',
                columns: 3,
                vertical: true,
                items: [
                    {boxLabel: '连续报告期', name: 'timeScope', inputValue: 1, checked: commonObj.timeScope == 1},
                    {boxLabel: '选择报告期', name: 'timeScope', inputValue: 2, checked: commonObj.timeScope == 2},
                    {boxLabel: '最新报告期', name: 'timeScope', inputValue: 3, checked: commonObj.timeScope == 3}
                ],
                listeners: {
                    change: function (view, n, o) {
                        commonObj.timeScope = n.timeScope;
                        changeSel();
                    }
                }
            }, {
                xtype: 'panel',
                width: '100%',
                margin: '10 0 30 20',
                flex: 2,
                border: 0,
                items: [continuousTimes, multiSelectTimes, newestTimes]
            }]
        }]
    });
    var win = new Ext.addStructureTimeDataWin({
        items: [timeDataPanel],
        buttons: [{
            text: '保存',
            handler: function () {
                var result = getSelData();
                if (result) {
                    if (fn) {
                        fn(result);
                        win.close();
                    }
                } else {
                    Ext.Msg.alert('提示', '请补全资料');
                }
            }
        }, {
            text: '关闭',
            handler: function () {
                win.close();
            }
        }]
    });
    //切换选择
    function changeSel() {
        var timeScope = commonObj.timeScope;
        //切换先是隐藏的面板  continuousTimes,multiSelectTimes,newestTimes
        continuousTimes.setVisible(timeScope == 1);
        multiSelectTimes.setVisible(timeScope == 2);
        newestTimes.setVisible(timeScope == 3);
    }

    //获取选择的数据
    function getSelData() {
        var timeScope = commonObj.timeScope;
        //根据获取方式不同取值
        if (timeScope == 1) {//连续报告期
            var byVal = Ext.getCmp('beginYearWinCombo').getValue();
            var eyVal = Ext.getCmp('endYearWinCombo').getValue();
            var btVal = Ext.getCmp('beginTimeWinCombo').getValue();
            var etVal = Ext.getCmp('endTimeWinCombo').getValue();
            if (byVal && eyVal && btVal && etVal) {
                var timeRange = [{
                    dataType: METADATA_TYPE.DATA_BEGIN_YEAR,
                    dataValue: byVal,
                    type: METADATA_TYPE.LIANXU
                },
                    {dataType: METADATA_TYPE.DATA_BEGIN_PERIOD, dataValue: btVal, type: METADATA_TYPE.LIANXU},
                    {dataType: METADATA_TYPE.DATA_END_YEAR, dataValue: eyVal, type: METADATA_TYPE.LIANXU},
                    {dataType: METADATA_TYPE.DATA_END_PERIOD, dataValue: etVal, type: METADATA_TYPE.LIANXU}];
                var obj = {
                    foreignId: chartId,
                    foreignType: RPT_DESIGN_TYPE.TYPE_CHART,
                    timeRange: timeRange
                }
                return obj;
            }
            return false;
        }
        if (timeScope == 2) {//选择报告期

            var myVal = Ext.getCmp('multiYearWinCombo').getValue();
            var mtVal = Ext.getCmp('multiTimeWinCombo').getValue();
            if (myVal && mtVal) {
                var timeRange = [];
                //年
                for (var i = 0; i < myVal.length; i++) {
                    timeRange.push({
                        dataType: METADATA_TYPE.DATA_YEAR,
                        dataValue: myVal[i],
                        type: METADATA_TYPE.XUANZE
                    });
                }
                //报告期
                for (var i = 0; i < mtVal.length; i++) {
                    timeRange.push({
                        dataType: METADATA_TYPE.DATA_PERIOD,
                        dataValue: mtVal[i],
                        type: METADATA_TYPE.XUANZE
                    });
                }
                var obj = {
                    foreignId: chartId,
                    foreignType: RPT_DESIGN_TYPE.TYPE_CHART,
                    timeRange: timeRange
                }
                return obj;
            }
            return false;
        }
        //最新报告期
        var val = Ext.getCmp('newestTimesWinField').getValue();
        if (val) {
            var obj = {
                foreignId: chartId,
                foreignType: RPT_DESIGN_TYPE.TYPE_CHART,
                timeRange: [{dataType: METADATA_TYPE.DATA_NUMBER, dataValue: val, type: METADATA_TYPE.BAOGAOQI}]
            }
            return obj;
        }
        return false;
    }

    win.show();
    return win;
}