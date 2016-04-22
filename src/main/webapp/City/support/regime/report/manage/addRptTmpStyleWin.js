/**
 * Created by wxl on 2016/1/18 0018.
 */
createModel('Ext.addRptTmpStyleWin', function () {
    Ext.define('Ext.addRptTmpStyleWin', {
        extend: 'Ext.window.Window',
        width: 600,
        modal: true,
        title: '添加表样窗口'
    });
});
/**
 * 添加表扬窗口,初始化
 * @param tmpRecord 报表模板
 * @param record 表样,为空时即为添加
 * @param curStyles 所有当前模板的表样
 * @param fn 回调
 */
Ext.addRptTmpStyleWin.init = function (tmpRecord, record, curStyles, fn) {
    //频度的数据
    var p = tmpRecord.get('period');
    var periodData = p == 1 ? yearFres : (p == 2 ? halfFres : (p == 3 ? quarterFres : monthFres));
    var beginPeriodStore = new Ext.data.Store({
        fields: ['text', 'value'],
        data: periodData
    });
    var endPeriodStore = new Ext.data.Store({
        fields: ['text', 'value'],
        data: periodData.reverse()
    });
    periodData.reverse();//翻转回来!!!
    //表单
    var form = new Ext.form.Panel({
        width: '100%',
        layout: 'column',
        items: [{
            xtype: 'hidden',
            name: 'id'
        }, {
            xtype: 'hidden',
            name: 'tmpId',
            value: tmpRecord.get('id')
        }, {
            xtype: 'textfield',
            name: 'name',
            allowBlank: false,
            fieldLabel: '表样名<b style="color:red">*</b>',
            labelWidth: 70,
            labelAlign: 'right',
            columnWidth: 0.45,
            margin: '20 0 0 0'
        }, {
            xtype: 'combobox',
            name: 'styleType',
            allowBlank: false,
            fieldLabel: '表样类型<b style="color:red">*</b>',
            labelWidth: 70,
            labelAlign: 'right',
            displayField: 'text',
            valueField: 'value',
            store: new Ext.data.Store({
                fields: ['text', 'value'],
                data: styleTypes
            }),
            value: styleTypes[0].value,
            columnWidth: 0.45,
            margin: '20 0 0 0',
            queryMode: 'local'
        }, {
            xtype: 'combobox',
            name: 'beginYear',
            allowBlank: false,
            fieldLabel: '开始时间<b style="color:red">*</b>',
            labelWidth: 70,
            labelAlign: 'right',
            displayField: 'text',
            valueField: 'value',
            store: new Ext.data.Store({
                fields: ['text', 'value'],
                proxy: {
                    type: 'ajax',
                    url: GLOBAL_PATH + '/support/manage/metadata/getAllYears',
                    extraParams: {
                        sortType: 1
                    }
                },
                autoLoad: true
            }),
            columnWidth: 0.45,
            margin: '20 0 0 0'
        }, {//开始频度
            xtype: 'combobox',
            name: 'beginPeriod',
            allowBlank: false,
            fieldLabel: '开始频度<b style="color:red">*</b>',
            labelWidth: 70,
            labelAlign: 'right',
            displayField: 'text',
            valueField: 'value',
            store: beginPeriodStore,
            columnWidth: 0.45,
            margin: '20 0 0 0',
            queryMode: 'local'
        }, {
            xtype: 'combobox',
            name: 'endYear',
            allowBlank: false,
            fieldLabel: '结束时间<b style="color:red">*</b>',
            labelWidth: 70,
            labelAlign: 'right',
            displayField: 'text',
            valueField: 'value',
            store: new Ext.data.Store({
                fields: ['text', 'value'],
                proxy: {
                    type: 'ajax',
                    url: GLOBAL_PATH + '/support/manage/metadata/getAllYears',
                    extraParams: {
                        sortType: -1
                    }
                },
                autoLoad: true
            }),
            columnWidth: 0.45,
            margin: '20 0 20 0',
            listeners: {
                select: function (_this, record) {
                    if (record.get('value') == 0) {//一直有效
                        form.down('[name=endPeriod]').setValue(12);
                    }
                }
            }
        }, {//结束频度
            xtype: 'combobox',
            name: 'endPeriod',
            fieldLabel: '结束频度<b style="color:red">*</b>',
            labelWidth: 70,
            labelAlign: 'right',
            allowBlank: false,
            displayField: 'text',
            valueField: 'value',
            store: endPeriodStore,
            columnWidth: 0.45,
            margin: '20 0 20 0',
            queryMode: 'local'
        }]
    });
    var win = new Ext.addRptTmpStyleWin({
        items: [form],
        buttons: [{
            text: '保存',
            handler: function () {
                if (form.isValid()) {
                    var values = form.getValues();
                    if (values.endYear != 0) {
                        if ((values.beginYear > values.endYear) || (values.beginYear == values.endYear && values.beginPeriod > values.endPeriod)) {
                            Ext.Msg.alert('提示', '结束时间不能在开始时间之前');
                            return;
                        }
                    }
                    var checkResult = checkRange(curStyles, tmpRecord, values);
                    if (!checkResult.success) {
                        Ext.Msg.alert('提示', checkResult.msg);
                        return;
                    }
                    form.submit({
                        url: Global_Path + '/saveRptStyle',
                        success: function (form, action) {
                            Ext.Msg.alert('成功', action.result.msg);
                            win.close();
                            if (fn) {
                                fn();
                            }
                        },
                        failure: function () {
                            Ext.Msg.alert('错误', '保存失败');
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
        win.setTitle('修改表样窗口');
    }
    win.show();
};
/**
 * 测试时间范围
 */
function checkRange(curStyles, tmpRecord, record) {
    //监测是否超出报表模板的报告期
    var beginYear = record.beginYear;
    var beginPeriod = record.beginPeriod;
    //模板的开始时间
    var tmpBeginYear = tmpRecord.get('beginYear');
    var tmpBeginPeriod = tmpRecord.get('beginPeriod');
    if (beginYear <= tmpBeginYear) {//检测开始时间
        if ((beginYear < tmpBeginYear) || (beginPeriod < tmpBeginPeriod)) {
            return {
                success: false,
                msg: '表样报告期超出限定范围'
            };
        }
    }
    //结束时间
    var endYear = record.endYear;
    var endPeriod = record.endPeriod;
    var tmpEndYear = tmpRecord.get('endYear');
    var tmpEndPeriod = tmpRecord.get('endPeriod');
    if (endPeriod != 0 && tmpEndYear != 0) {//检测结束时间
        if (endYear >= tmpEndYear) {
            if ((endYear > tmpEndYear) || (endPeriod > tmpEndPeriod)) {
                return {
                    success: false,
                    msg: '结束时间超出限定时间范围'
                };
            }
        }
    }
    //在模板的时间范围内,检测是否有重复的报告期
    var flag = false;
    if (curStyles.length) {
        for (var i = 0; i < curStyles.length; i++) {
            flag = flag || checkIntersection(curStyles[i].raw, record);
        }
    }
    if (flag) {
        return {
            success: false,
            msg: '表样报告期重复'
        };
    }
    return {success: true};
}
/**
 * 检测俩个时间范围是否含有交集
 * @return Boolean true表示有交集,false表示没有交集
 */
function checkIntersection(range1, range2) {
    var beginYear1 = range1.beginYear;
    var beginYear2 = range2.beginYear;
    var endYear1 = range1.endYear;
    var endYear2 = range2.endYear;
    var beginPeriod1 = range1.beginPeriod;
    var beginPeriod2 = range2.beginPeriod;
    var endPeriod1 = range1.endPeriod;
    var endPeriod2 = range2.endPeriod;
    if (endYear1 != 0 && endYear2 != 0) {//没有一直有效的
        if ((endYear1 < beginYear2) || (endYear1 == beginYear2 && endPeriod1 < beginPeriod2)) {//range1在range2之前
            return false;
        }
        if ((beginYear1 > endYear2) || (beginYear1 == endYear2 && beginPeriod1 > endPeriod2)) {//range1在range2之后
            return false;
        }
        //在之间
        return true;
    } else {//包含一直有效,则只判断开始
        if (endYear1 == 0 && endYear2 == 0) {//都一直有效,必然有交集
            return true;
        }
        if (endYear1 == 0) {//range1一直有效
            if ((endYear2 < beginYear1) || (endYear2 == beginYear1 && endPeriod2 < beginPeriod1)) {//range1在range2之前
                return false;
            }
            return true;
        }
        //range2一直有效
        if ((endYear1 < beginYear2) || (endYear1 == beginYear2 && endPeriod1 < beginPeriod2)) {//range2在range1之前
            return false;
        }
        return true;
    }
}
