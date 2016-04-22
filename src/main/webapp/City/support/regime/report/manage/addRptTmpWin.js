/**
 * Created by wxl on 2016/1/15 0015.
 * 添加报表模板窗口
 */
createModel('Ext.addRptTmpWin', function () {
    Ext.define('Ext.addRptTmpWin', {
        extend: 'Ext.window.Window',
        width: 800,
        modal: true,
        title: '添加报表模板窗口'
    });
});
/**
 * 初始化窗口
 * @param record 若是修改,则传入要修改的记录
 * @param group 综合表分组信息
 * @param fn 回调函数
 */
Ext.addRptTmpWin.init = function (record, group, fn) {
    //年报选项
    var yearGroup = new Ext.form.CheckboxGroup({
        columns: 6,
        name: 'frequency',
        items: [{
            xtype: 'checkbox',
            name: 'frequency',
            boxLabel: '全年',
            inputValue: '12',
            checked: true,
            width: 45,
            hidden: false
        }]
    });
    //半年报的选项
    var halfGroup = new Ext.form.CheckboxGroup({
        columns: 6,
        name: 'frequency',
        items: [
            {xtype: 'checkbox', name: 'frequency', boxLabel: '上半年', inputValue: '6', disabled: true, width: 60},
            {xtype: 'checkbox', name: 'frequency', boxLabel: '下半年', inputValue: '12', disabled: true, width: 60}
        ]
    });
    //季报的选项
    var quarterGroup = new Ext.form.CheckboxGroup({
        columns: 6,
        name: 'frequency',
        items: [
            {xtype: 'checkbox', name: 'frequency', boxLabel: '第一季度', inputValue: '3', disabled: true, width: 75},
            {xtype: 'checkbox', name: 'frequency', boxLabel: '第二季度', inputValue: '6', disabled: true, width: 75},
            {xtype: 'checkbox', name: 'frequency', boxLabel: '第三季度', inputValue: '9', disabled: true, width: 75},
            {xtype: 'checkbox', name: 'frequency', boxLabel: '第四季度', inputValue: '12', disabled: true, width: 75}
        ]
    });
    //月报的选项
    var monthGroup = new Ext.form.CheckboxGroup({
        columns: 12,
        name: 'frequency',
        items: [
            {xtype: 'checkbox', name: 'frequency', boxLabel: '1月', inputValue: '1', disabled: true, width: 45},
            {xtype: 'checkbox', name: 'frequency', boxLabel: '2月', inputValue: '2', disabled: true, width: 45},
            {xtype: 'checkbox', name: 'frequency', boxLabel: '3月', inputValue: '3', disabled: true, width: 45},
            {xtype: 'checkbox', name: 'frequency', boxLabel: '4月', inputValue: '4', disabled: true, width: 45},
            {xtype: 'checkbox', name: 'frequency', boxLabel: '5月', inputValue: '5', disabled: true, width: 45},
            {xtype: 'checkbox', name: 'frequency', boxLabel: '6月', inputValue: '6', disabled: true, width: 45},
            {xtype: 'checkbox', name: 'frequency', boxLabel: '7月', inputValue: '7', disabled: true, width: 45},
            {xtype: 'checkbox', name: 'frequency', boxLabel: '8月', inputValue: '8', disabled: true, width: 45},
            {xtype: 'checkbox', name: 'frequency', boxLabel: '9月', inputValue: '9', disabled: true, width: 45},
            {xtype: 'checkbox', name: 'frequency', boxLabel: '10月', inputValue: '10', disabled: true, width: 46},
            {xtype: 'checkbox', name: 'frequency', boxLabel: '11月', inputValue: '11', disabled: true, width: 46},
            {xtype: 'checkbox', name: 'frequency', boxLabel: '12月', inputValue: '12', disabled: true, width: 46}
        ]
    });
    //频度的store
    var beginPeriodStore = new Ext.data.Store({
        fields: ['text', 'value'],
        data: yearFres
    });
    var endPeriodStore = new Ext.data.Store({
        fields: ['text', 'value'],
        data: yearFres
    });
    var form = new Ext.form.Panel({
        width: '100%',
        layout: 'column',
        items: [{//主键
            xtype: 'hidden',
            name: 'id'
        }, {
            xtype: 'hidden',
            name: 'groupId',
            value: group.get('id')
        }, {
            xtype: 'hidden',
            name: 'researchObjType',
            value: researchObjTypes[0].value
        }, {
            xtype: 'textfield',
            name: 'name',
            allowBlank: false,
            fieldLabel: '报表名<b style="color:red">*</b>',
            labelWidth: 90,
            labelAlign: 'right',
            columnWidth: 0.45,
            margin: '20 0 0 0'
        }, {
            xtype: 'combobox',
            name: 'status',
            fieldLabel: '状态',
            labelWidth: 70,
            allowBlank: false,
            forceSelection: true,
            editable: false,
            labelAlign: 'right',
            displayField: 'text',
            valueField: 'value',
            store: new Ext.data.Store({
                fields: ['text', 'value'],
                data: [{text: '启用', value: 1}, {text: '废弃', value: 0}]
            }),
            value: 1,
            columnWidth: 0.25,
            margin: '20 0 0 0'
        }, {
            xtype: 'combobox',
            name: 'rptType',
            allowBlank: false,
            forceSelection: true,
            editable: false,
            fieldLabel: '报表类型<b style="color:red">*</b>',
            labelWidth: 90,
            labelAlign: 'right',
            displayField: 'text',
            valueField: 'value',
            store: new Ext.data.Store({
                fields: ['text', 'value'],
                data: rptTypes
            }),
            columnWidth: 0.25,
            margin: '20 0 0 0',
            value: rptTypes[0].value
        }, {//部门
            xtype: 'querypicker',
            name: 'depId',
            allowBlank: false,
            fieldLabel: '部门<b style="color:red">*</b>',
            labelWidth: 90,
            labelAlign: 'right',
            columnWidth: 0.45,
            margin: '20 0 0 0',
            valueField: 'id',
            displayField: 'depName',
            store: new Ext.data.TreeStore({
                fields: ['id', 'depName'],
                proxy: {
                    type: 'ajax',
                    url: GLOBAL_PATH + '/support/sys/dep/queryDepTreeByName'
                },
                root: {
                    id: 0,
                    depName: '组织机构',
                    expanded: true
                },
                autoLoad: true
            }),
            queryParam: 'depName',
            value: depId
        }, {
            xtype: 'textfield',
            name: 'rptCode',
            fieldLabel: '表号',
            labelWidth: 70,
            labelAlign: 'right',
            columnWidth: 0.25,
            margin: '20 0 0 0'
        }, {//期后上报天数
            xtype: 'numberfield',
            name: 'submitDaysDelay',
            allowBlank: false,
            fieldLabel: '期后上报<b style="color:red">*</b>',
            labelWidth: 90,
            labelAlign: 'right',
            columnWidth: 0.25,
            margin: '20 0 0 0',
            minValue: 1
        }, {
            xtype: 'querypicker',
            name: 'researchObjId',
            fieldLabel: '默认调查地区',
            labelWidth: 90,
            labelAlign: 'right',
            columnWidth: 0.45,
            margin: '20 0 0 0',
            valueField: 'id',
            displayField: 'name',
            store: new Ext.data.TreeStore({
                fields: ['id', 'name'],
                proxy: {
                    type: 'ajax',
                    url: GLOBAL_PATH + '/area/queryAreaByName'
                },
                root: {
                    id: 0,
                    name: '地区',
                    expanded: true
                },
                autoLoad: true
            }),
            value: surObjId,//默认调查对象
            queryParam: 'areaName',
            queryMode: 'remote'
        }, {//时间范围
            xtype: 'combobox',
            name: 'beginYear',
            allowBlank: false,
            forceSelection: true,
            editable: false,
            fieldLabel: '制度年度<b style="color:red">*</b>',
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
            columnWidth: 0.19,
            margin: '20 0 0 0'
        }, {//开始频度
            xtype: 'combobox',
            name: 'beginPeriod',
            allowBlank: false,
            forceSelection: true,
            editable: false,
            displayField: 'text',
            valueField: 'value',
            store: beginPeriodStore,
            columnWidth: 0.09,
            margin: '20 0 0 0',
            value: 12,
            queryMode: 'local'
        }, {//结束年
            xtype: 'combobox',
            name: 'endYear',
            allowBlank: false,
            forceSelection: true,
            editable: false,
            fieldLabel: '到',
            labelWidth: 20,
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
            columnWidth: 0.12,
            margin: '20 0 0 0',
            listeners: {
                select: function (_this, record) {
                    if (record.get('value') == 0) {//一直有效
                        form.down('[name=endPeriod]').setValue(12);
                    }
                }
            }
        }, {//结束时间频度
            xtype: 'combobox',
            name: 'endPeriod',
            allowBlank: false,
            forceSelection: true,
            editable: false,
            displayField: 'text',
            valueField: 'value',
            store: endPeriodStore,
            columnWidth: 0.1,
            margin: '20 0 0 0',
            value: 12,
            queryMode: 'local'
        }, {//周期和频度
            xtype: 'fieldcontainer',
            columnWidth: 0.95,
            fieldLabel: '报送频率<b style="color:red">*</b>',
            labelWidth: 90,
            labelAlign: 'right',
            margin: '20 0 0 0',
            items: [{//年报的
                xtype: 'fieldset',
                border: 0,
                layout: 'hbox',
                items: [{
                    xtype: 'radio',
                    name: 'period',
                    boxLabel: '年报',
                    checked: true,
                    inputValue: '1',
                    handler: function (_this, checked) {
                        if (checked) {//选中
                            //所有选中的数据
                            yearGroup.items.each(function (item) {
                                item.setValue(true);
                            });
                            //加载频度数据
                            var beginPeriod = form.down('[name="beginPeriod"]');
                            beginPeriodStore.loadRawData(yearFres);
                            beginPeriod.setValue(yearFres[0].value);
                            var endPeriod = form.down('[name="endPeriod"]');
                            endPeriodStore.loadRawData(yearFres.reverse());
                            yearFres.reverse();//翻转回来!!!
                            endPeriod.setValue(yearFres[0].value);
                        } else {
                            yearGroup.items.each(function (item) {
                                item.setValue(false);
                            });
                        }
                    }
                }, yearGroup]
            }, {//半年报的
                xtype: 'fieldset',
                border: 0,
                layout: 'hbox',
                items: [{
                    xtype: 'radio',
                    name: 'period',
                    boxLabel: '半年',
                    inputValue: '2',
                    handler: function (_this, checked) {
                        if (checked) {//选中
                            halfGroup.items.each(function (item) {
                                item.setDisabled(false);
                                item.setValue(true);
                            });
                            //加载频度数据
                            var beginPeriod = form.down('[name="beginPeriod"]');
                            beginPeriodStore.loadRawData(halfFres);
                            beginPeriod.setValue('');
                            var endPeriod = form.down('[name="endPeriod"]');
                            endPeriodStore.loadRawData(halfFres.reverse());
                            halfFres.reverse();//翻转回来!!!
                            endPeriod.setValue('');
                        } else {
                            halfGroup.items.each(function (item) {
                                item.setDisabled(true);
                                item.setValue(false);
                            });
                        }
                    }
                }, halfGroup]
            }, {//季报的
                xtype: 'fieldset',
                border: 0,
                layout: 'hbox',
                items: [{
                    xtype: 'radio',
                    name: 'period',
                    boxLabel: '季报',
                    inputValue: '3',
                    handler: function (_this, checked) {
                        if (checked) {//选中
                            quarterGroup.items.each(function (item) {
                                item.setDisabled(false);
                                item.setValue(true);
                            });
                            //加载频度数据
                            var beginPeriod = form.down('[name="beginPeriod"]');
                            beginPeriodStore.loadRawData(quarterFres);
                            beginPeriod.setValue('');
                            var endPeriod = form.down('[name="endPeriod"]');
                            endPeriodStore.loadRawData(quarterFres.reverse());
                            quarterFres.reverse();//翻转回来!!!
                            endPeriod.setValue('');
                        } else {
                            quarterGroup.items.each(function (item) {
                                item.setDisabled(true);
                                item.setValue(false);
                            });
                        }
                    }
                }, quarterGroup]
            }, {//月报的
                xtype: 'fieldset',
                border: 0,
                layout: 'hbox',
                items: [{
                    xtype: 'radio',
                    name: 'period',
                    boxLabel: '月报',
                    inputValue: '4',
                    handler: function (_this, checked) {
                        if (checked) {//选中
                            monthGroup.items.each(function (item) {
                                item.setDisabled(false);
                                item.setValue(true);
                            });
                            //加载频度数据
                            var beginPeriod = form.down('[name="beginPeriod"]');
                            beginPeriodStore.loadRawData(monthFres);
                            beginPeriod.setValue('');
                            var endPeriod = form.down('[name="endPeriod"]');
                            endPeriodStore.loadRawData(monthFres.reverse());
                            monthFres.reverse();//翻转回来!!!
                            endPeriod.setValue('');
                        } else {
                            monthGroup.items.each(function (item) {
                                item.setDisabled(true);
                                item.setValue(false);
                            });
                        }
                    }
                }, monthGroup]
            }]
        }, {//报表说明
            xtype: 'textarea',
            name: 'rptExplain',
            fieldLabel: '报表说明',
            labelWidth: 70,
            labelAlign: 'right',
            columnWidth: 0.45,
            margin: '20 0 20 0'
        }, {//报表说明
            xtype: 'textarea',
            name: 'rptComments',
            fieldLabel: '备注',
            labelWidth: 70,
            labelAlign: 'right',
            columnWidth: 0.5,
            margin: '20 0 20 0'
        }]
    });
    var win = new Ext.addRptTmpWin({
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
                    var frequency = values.frequency;
                    form.submit({
                        url: Global_Path + '/saveRptTmp',
                        params: {
                            frequency: (frequency instanceof Array ? frequency.join(',') : frequency)
                        },
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
                } else {
                    Ext.Msg.alert('错误', '请完善报表信息');
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
        //部门
        form.down('[name="depId"]').setValue(record.get('department').id);
        //设置报送频率
        var p = record.get('period');
        if (p != 1) {
            var arrGroup = p == 2 ? halfGroup : (p == 3 ? quarterGroup : monthGroup);
            var fres = ',' + record.get('frequency') + ',';
            arrGroup.items.each(function (item) {
                //判断inputValue是否在里面
                if (fres.indexOf(',' + item.inputValue + ',') < 0) {
                    item.setValue(false);
                }
            });
        } else {
            yearGroup.down('checkbox').setValue(true);
        }
        win.setTitle('修改报表模板窗口');
    }
    win.show();
};
