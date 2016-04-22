/**
 * Created by wgx on 2016/2/1.
 */
Ext.define('Ext.addAllReportInfosWin', {
    extend: 'Ext.window.Window',
    width: 600,
    modal: true,
    title: '生成往期报表'
});
/**
 * 初始化
 * @param record     系统元数据类型
 * @param fn
 */
Ext.addAllReportInfosWin.init = function (record, fn) {
    /*var form = new Ext.form.Panel({
     width:'100%',
     layout:'column',
     items:[]
     });*/
    var period = record.get('period');
    var beginYear = record.get('beginYear');
    var endYear = record.get('endYear');
    var beginPeriod = record.get('beginPeriod');
    var endPeriod = record.get('endPeriod');
    var frequency = []
    if(record.get('frequency')){
        frequency =record.get('frequency').split(',');
    };
    if (beginYear) {

    }
    Ext.Ajax.request({
        url: GLOBAL_PATH + '/support/regime/collection/isReportInfosExist',
        params: {
            rptTmpId: record.get('id')
        },
        method: 'post',
        success: function (response, action) {
            var result = Ext.decode(response.responseText);
            var nowYear = result.nowyear;//当前年
            var nowMonth = result.nowmonth;//当前月
            var timeExistList = result.timeExistList;//已经生成的报表
            //console.log(Ext.encode(timeExistList));
            var yearLength;
            var monthLength;
            if(endYear>=nowYear||endYear==0){
                endPeriod = nowMonth;
            }
            // 计算报告期年份数
            if (beginYear && (endYear||endYear==0) && nowYear) {
                if (endYear == 0) {
                    yearLength = Math.max(nowYear - beginYear + 1, 0);
                } else {
                    yearLength = Math.max(Math.min(endYear, nowYear) - beginYear + 1, 0);
                }
            }
            var yearList = new Array(); //年的集合对象
            for (var i = 0; i < yearLength; i++) {
                var yearFieldSet = Ext.create('Ext.form.FieldSet', {
                    title: parseInt(beginYear) + i,//放的是年的名称，例如2012年
                    margin: '10 10 10 10',
                    height: 90,//根据需要自行判断
                    items: []
                });

                yearFieldSet.add(getCheckGroup(beginYear, period, i,frequency));
                yearList.push(yearFieldSet);
            }
            //
            var checkboxGroup_all = Ext.create('Ext.form.CheckboxGroup', {
                name: 'checkboxGroup_all',
                columns: 2,
                margin: '0 0 0 5',
                items: []
            });
            var checkbox_all = Ext.create('Ext.form.field.Checkbox', {
                name: 'checkbox_all',
                checked: true,
                inputValue: 'checkbox_all',
                boxLabel: '全部选中',
                handler: function (checkbox, checked) {
                    if (checked) {
                        for (var i = 0; i < yearLength; i++) {//循环年
                            var array = form.getForm().findField('checkbox' + i).items;
                            array.each(function (item) {
                                if (!item.getValue()) {
                                    item.setValue(true);
                                }

                            });
                        }
                    } else {
                        for (var i = 0; i < yearLength; i++) {//循环年
                            var array = form.getForm().findField('checkbox' + i).items;
                            array.each(function (item) {
                                if (item.disabled == false) {
                                    item.setValue(false);
                                }
                            });
                        }
                    }


                }
            });
            var yearFieldSet_all = Ext.create('Ext.form.FieldSet', {
                title: '全部操作',
                margin: '10 10 10 10',
                height: 50,
                items: []
            });
            checkboxGroup_all.add(checkbox_all);
            yearFieldSet_all.add(checkboxGroup_all);
            yearList.push(yearFieldSet_all);
            var form = Ext.create('Ext.form.Panel', {
                border: 0,
                height: 315,
                width: '100%',
                autoScroll: true,
                fieldDefaults: {
                    labelAlign: "right",
                    labelWidth: 85
                },
                items: []
            });
            form.add(yearList);
            console.log(timeExistList)
            //已经生成的报表报告期不可选
            for (var j = 0; j < timeExistList.length; j++) {
                var existyear = timeExistList[j].year;
                var existmonth = timeExistList[j].month;
                if (existyear && existmonth) {
                    var array = form.getForm().findField('checkbox' + (parseInt(existyear) - beginYear)).items;
                    array.each(function (item) {
                        if (item.inputValue == existmonth) {
                            item.disabled = true;
                        }
                    });
                }
            }
            if(beginPeriod>1){
                var beginarray = form.getForm().findField('checkbox'+0).items;
                beginarray.each(function (item) {
                    if (item.inputValue < beginPeriod||frequency.indexOf(item.inputValue)==-1) {
                        item.disabled = true;
                    }

                });
            }
            if(endPeriod<12){
                var endarray = form.getForm().findField('checkbox'+(yearLength-1)).items;
                endarray.each(function (item) {
                    if (item.inputValue > endPeriod||frequency.indexOf(item.inputValue)==-1) {
                        item.disabled = true;
                    }

                });
            }
            var win = new Ext.addAllReportInfosWin({
                items: [form],
                buttons: [{
                    text: '保存',
                    handler: function () {
                        if (form.isValid()) {
                            var yearArray = new Array();//年

                            for (var i = 0; i < yearLength; i++) {
                                var monthArray = new Array();//月、季、半年
                                var array = form.getForm().findField("checkbox" + i).items;
                                var year = form.getForm().findField("checkbox" + i).name;
                                array.each(function (item) {
                                    if (item.getValue() && item.inputValue.indexOf("checkbox_item_all_" + i) < 0) {
                                        var value = item.inputValue;
                                        if (item.disabled == false) {
                                            monthArray.push(value);
                                        }
                                    }
                                });
                                if (monthArray.length > 0) {
                                    var obj = {
                                        year: year,
                                        month: monthArray.join(",")
                                    }
                                    yearArray.push(obj);
                                }
                            }
                            if(yearArray.length>0){
                                form.submit({
                                    url: GLOBAL_PATH + '/support/regime/collection/createAllReportInfos',
                                    params: {
                                        rptTmpId:record.get('id'),
                                        yearArray: Ext.encode(yearArray)
                                    },
                                    wait: true,
                                    success: function (form, action) {
                                        Ext.Msg.alert('成功', action.result.msg);
                                        win.close();
                                        if (fn) {
                                            fn(action.result.datas);
                                        }
                                    },
                                    failure: function (form, action) {
                                        Ext.Msg.alert('失败', "保存失败");
                                    }
                                });
                            }else{
                                Ext.Msg.alert('提示', "未选择报告期");
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
    })
    function getCheckGroup(beginYear, period, i,frequency) {
        var checkbox_item_all = Ext.create('Ext.form.field.Checkbox', {
            name: 'checkbox_item_all',
            checked: true,
            width: 46,
            inputValue: 'checkbox_item_all_' + i,
            boxLabel: '全部',
            handler: function (checkbox, checked) {
                if (checked) {
                    var array = Ext.getCmp('checkbox' + i).items;
                    array.each(function (item) {
                        item.setValue(true);
                    });
                } else {
                    var array = Ext.getCmp('checkbox' + i).items;
                    array.each(function (item) {
                        item.setValue(false);
                    });
                }


            }
        });
        switch (period) {
            case 1://年报选项
                return new Ext.form.CheckboxGroup({
                    columns: 3,
                    name: parseInt(beginYear) + i,
                    id: 'checkbox' + i,
                    items: [{
                        xtype: 'checkbox',
                        name: 'yearGroup',
                        boxLabel: '全年',
                        inputValue: '12',
                        disabled:frequency.indexOf('12')==-1,
                        checked: true,
                        width: 45,
                        hidden: false
                    }]
                });
                break;
            case 2://半年报的选项
                return new Ext.form.CheckboxGroup({
                    columns: 6,
                    name: parseInt(beginYear) + i,
                    id: 'checkbox' + i,
                    items: [
                        {
                            xtype: 'checkbox',
                            name: 'halfGroup',
                            boxLabel: '上半年',
                            inputValue: '6',
                            disabled:frequency.indexOf('6')==-1,
                            checked: true,
                            width: 60
                        },
                        {
                            xtype: 'checkbox',
                            name: 'halfGroup',
                            boxLabel: '下半年',
                            inputValue: '12',
                            disabled:frequency.indexOf('12')==-1,
                            checked: true,
                            width: 60
                        }, checkbox_item_all
                    ]
                });
                break;
            case 3://季报的选项
                return new Ext.form.CheckboxGroup({
                    columns: 4,
                    name: parseInt(beginYear) + i,
                    id: 'checkbox' + i,
                    items: [
                        {
                            xtype: 'checkbox',
                            name: 'quarterGroup',
                            boxLabel: '第一季度',
                            inputValue: '3',
                            disabled:frequency.indexOf('3')==-1,
                            checked: true,
                            width: 75
                        },
                        {
                            xtype: 'checkbox',
                            name: 'quarterGroup',
                            boxLabel: '第二季度',
                            inputValue: '6',
                            disabled:frequency.indexOf('6')==-1,
                            checked: true,
                            width: 75
                        },
                        {
                            xtype: 'checkbox',
                            name: 'quarterGroup',
                            boxLabel: '第三季度',
                            inputValue: '9',
                            disabled:frequency.indexOf('9')==-1,
                            checked: true,
                            width: 75
                        },
                        {
                            xtype: 'checkbox',
                            name: 'quarterGroup',
                            boxLabel: '第四季度',
                            disabled:frequency.indexOf('12')==-1,
                            inputValue: '12',
                            checked: true,
                            width: 75
                        }, checkbox_item_all
                    ]
                });
                break;
            case 4://月报的选项
                return new Ext.form.CheckboxGroup({
                    columns: 6,
                    name: parseInt(beginYear) + i,
                    id: 'checkbox' + i,
                    items: [
                        {
                            xtype: 'checkbox',
                            name: 'monthGroup',
                            boxLabel: '1月',
                            inputValue: '1',
                            disabled:frequency.indexOf('1')==-1,
                            checked: true,
                            width: 45
                        },
                        {
                            xtype: 'checkbox',
                            name: 'monthGroup',
                            boxLabel: '2月',
                            inputValue: '2',
                            disabled:frequency.indexOf('2')==-1,
                            checked: true,
                            width: 45
                        },
                        {
                            xtype: 'checkbox',
                            name: 'monthGroup',
                            boxLabel: '3月',
                            inputValue: '3',
                            disabled:frequency.indexOf('3')==-1,
                            checked: true,
                            width: 45
                        },
                        {
                            xtype: 'checkbox',
                            name: 'monthGroup',
                            boxLabel: '4月',
                            inputValue: '4',
                            disabled:frequency.indexOf('4')==-1,
                            checked: true,
                            width: 45
                        },
                        {
                            xtype: 'checkbox',
                            name: 'monthGroup',
                            boxLabel: '5月',
                            inputValue: '5',
                            disabled:frequency.indexOf('5')==-1,
                            checked: true,
                            width: 45
                        },
                        {
                            xtype: 'checkbox',
                            name: 'monthGroup',
                            boxLabel: '6月',
                            inputValue: '6',
                            disabled:frequency.indexOf('6')==-1,
                            checked: true,
                            width: 45
                        },
                        {
                            xtype: 'checkbox',
                            name: 'monthGroup',
                            boxLabel: '7月',
                            inputValue: '7',
                            disabled:frequency.indexOf('7')==-1,
                            checked: true,
                            width: 45
                        },
                        {
                            xtype: 'checkbox',
                            name: 'monthGroup',
                            boxLabel: '8月',
                            inputValue: '8',
                            disabled:frequency.indexOf('8')==-1,
                            checked: true,
                            width: 45
                        },
                        {
                            xtype: 'checkbox',
                            name: 'monthGroup',
                            boxLabel: '9月',
                            inputValue: '9',
                            disabled:frequency.indexOf('9')==-1,
                            checked: true,
                            width: 45
                        },
                        {
                            xtype: 'checkbox',
                            name: 'monthGroup',
                            boxLabel: '10月',
                            inputValue: '10',
                            disabled:frequency.indexOf('10')==-1,
                            checked: true,
                            width: 46
                        },
                        {
                            xtype: 'checkbox',
                            name: 'monthGroup',
                            boxLabel: '11月',
                            inputValue: '11',
                            disabled:frequency.indexOf('11')==-1,
                            checked: true,
                            width: 46
                        },
                        {
                            xtype: 'checkbox',
                            name: 'monthGroup',
                            boxLabel: '12月',
                            inputValue: '12',
                            disabled:frequency.indexOf('12')==-1,
                            checked: true,
                            width: 46
                        }, checkbox_item_all
                    ]
                });
                break;
        }
    }
}
