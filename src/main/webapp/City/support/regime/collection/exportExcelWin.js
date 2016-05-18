/**
 * Created by wgx on 2016/5/12.
 */

Ext.define('Ext.exportExcelWin', {
    extend: 'Ext.window.Window',
    width: 600,
    modal: true,
    title: '批量导出Excel'
});

Ext.exportExcelWin.init = function (rptInfos, years, period, fn) {
    // 计算报告期年份数
    var yearList = new Array(); //年的集合对象
    $.each(years, function (i, year) {
        var yearFieldSet = Ext.create('Ext.form.FieldSet', {
            title: year.value,//放的是年的名称，例如2012年
            margin: '10 10 10 10',
            height: 90,//根据需要自行判断
            items: []
        });
        yearFieldSet.add(getCheckGroup(year.value, period));
        yearList.push(yearFieldSet);
    })
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
                $.each(years, function (i, year) {//循环年
                    var array = form.getForm().findField('checkbox' + year.value).items;
                    array.each(function (item) {
                        if (!item.getValue()) {
                            item.setValue(true);
                        }

                    });
                });

            } else {
                $.each(years, function (i, year) {//循环年
                    var array = form.getForm().findField('checkbox' + year.value).items;
                    array.each(function (item) {
                        if (item.disabled == false) {
                            item.setValue(false);
                        }
                    });
                })
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
    // 已生成的报表设置为可选
    $.each(rptInfos,function(i,rptInfo){
        var item = form.getForm().findField('item' + rptInfo.year+rptInfo.month);
        item.disabled = false;
    });
    var win = new Ext.exportExcelWin({
        items: [form],
        buttons: [{
            text: '保存',
            handler: function () {
                var rptIds=[];
                if (form.isValid()) {
                    $.each(years,function(i,yearMap){
                        var array = form.getForm().findField("checkbox" + yearMap.value).items;
                        var year = form.getForm().findField("checkbox" + yearMap.value).name;
                        array.each(function (item) {
                            if (item.getValue() && item.inputValue.indexOf("checkbox_item_all_" + + year) < 0) {
                                var value = item.inputValue;
                                if (item.disabled == false) {
                                    $.each(rptInfos,function(index,rptInfo){
                                        if(rptInfo.year==year&&rptInfo.month==value){
                                            rptIds.push(rptInfo.id);
                                        }
                                    })
                                }
                            }
                        });
                    })
                    if (fn) {
                        fn(rptIds);
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

    function getCheckGroup(year, period) {
        var checkbox_item_all = Ext.create('Ext.form.field.Checkbox', {
            name: 'checkbox_item_all',
            checked: true,
            width: 46,
            inputValue: 'checkbox_item_all_' + year,
            boxLabel: '全部',
            handler: function (checkbox, checked) {
                if (checked) {
                    var array = Ext.getCmp('checkbox' + year).items;
                    array.each(function (item) {
                        item.setValue(true);
                    });
                } else {
                    var array = Ext.getCmp('checkbox' + year).items;
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
                    name: year,
                    id: 'checkbox' + year,
                    items: [{
                        id:'item'+year+12,
                        xtype: 'checkbox',
                        name: 'yearGroup',
                        boxLabel: '全年',
                        inputValue: '12',
                        disabled:true,
                        checked: true,
                        width: 45,
                        hidden: false
                    }]
                });
                break;
            case 2://半年报的选项
                return new Ext.form.CheckboxGroup({
                    columns: 6,
                    name: year,
                    id: 'checkbox' + year,
                    items: [
                        {
                            id:'item'+year+6,
                            xtype: 'checkbox',
                            name: 'halfGroup',
                            boxLabel: '上半年',
                            inputValue: '6',
                            disabled:true,
                            checked: true,
                            width: 60
                        },
                        {
                            id:'item'+year+12,
                            xtype: 'checkbox',
                            name: 'halfGroup',
                            boxLabel: '下半年',
                            inputValue: '12',
                            disabled:true,
                            checked: true,
                            width: 60
                        }, checkbox_item_all
                    ]
                });
                break;
            case 3://季报的选项
                return new Ext.form.CheckboxGroup({
                    columns: 4,
                    name: year,
                    id: 'checkbox' + year,
                    items: [
                        {
                            id:'item'+year+3,
                            xtype: 'checkbox',
                            name: 'quarterGroup',
                            boxLabel: '第一季度',
                            inputValue: '3',
                            disabled:true,
                            checked: true,
                            width: 75
                        },
                        {
                            id:'item'+year+6,
                            xtype: 'checkbox',
                            name: 'quarterGroup',
                            boxLabel: '第二季度',
                            inputValue: '6',
                            disabled:true,
                            checked: true,
                            width: 75
                        },
                        {
                            id:'item'+year+9,
                            xtype: 'checkbox',
                            name: 'quarterGroup',
                            boxLabel: '第三季度',
                            inputValue: '9',
                            disabled:true,
                            checked: true,
                            width: 75
                        },
                        {
                            id:'item'+year+12,
                            xtype: 'checkbox',
                            name: 'quarterGroup',
                            boxLabel: '第四季度',
                            inputValue: '12',
                            disabled:true,
                            checked: true,
                            width: 75
                        }, checkbox_item_all
                    ]
                });
                break;
            case 4://月报的选项
                return new Ext.form.CheckboxGroup({
                    columns: 6,
                    name: year,
                    id: 'checkbox' + year,
                    items: [
                        {
                            id:'item'+year+1,
                            xtype: 'checkbox',
                            name: 'monthGroup',
                            boxLabel: '1月',
                            inputValue: '1',
                            disabled:true,
                            checked: true,
                            width: 45
                        },
                        {
                            id:'item'+year+2,
                            xtype: 'checkbox',
                            name: 'monthGroup',
                            boxLabel: '2月',
                            inputValue: '2',
                            disabled:true,
                            checked: true,
                            width: 45
                        },
                        {
                            id:'item'+year+3,
                            xtype: 'checkbox',
                            name: 'monthGroup',
                            boxLabel: '3月',
                            inputValue: '3',
                            disabled:true,
                            checked: true,
                            width: 45
                        },
                        {
                            id:'item'+year+4,
                            xtype: 'checkbox',
                            name: 'monthGroup',
                            boxLabel: '4月',
                            inputValue: '4',
                            disabled:true,
                            checked: true,
                            width: 45
                        },
                        {
                            id:'item'+year+5,
                            xtype: 'checkbox',
                            name: 'monthGroup',
                            boxLabel: '5月',
                            inputValue: '5',
                            disabled:true,
                            checked: true,
                            width: 45
                        },
                        {
                            id:'item'+year+6,
                            xtype: 'checkbox',
                            name: 'monthGroup',
                            boxLabel: '6月',
                            inputValue: '6',
                            disabled:true,
                            checked: true,
                            width: 45
                        },
                        {
                            id:'item'+year+7,
                            xtype: 'checkbox',
                            name: 'monthGroup',
                            boxLabel: '7月',
                            inputValue: '7',
                            disabled:true,
                            checked: true,
                            width: 45
                        },
                        {
                            id:'item'+year+8,
                            xtype: 'checkbox',
                            name: 'monthGroup',
                            boxLabel: '8月',
                            inputValue: '8',
                            disabled:true,
                            checked: true,
                            width: 45
                        },
                        {
                            id:'item'+year+9,
                            xtype: 'checkbox',
                            name: 'monthGroup',
                            boxLabel: '9月',
                            inputValue: '9',
                            disabled:true,
                            checked: true,
                            width: 45
                        },
                        {
                            id:'item'+year+10,
                            xtype: 'checkbox',
                            name: 'monthGroup',
                            boxLabel: '10月',
                            inputValue: '10',
                            disabled:true,
                            checked: true,
                            width: 46
                        },
                        {
                            id:'item'+year+11,
                            xtype: 'checkbox',
                            name: 'monthGroup',
                            boxLabel: '11月',
                            inputValue: '11',
                            disabled:true,
                            checked: true,
                            width: 46
                        },
                        {
                            id:'item'+year+12,
                            xtype: 'checkbox',
                            name: 'monthGroup',
                            boxLabel: '12月',
                            inputValue: '12',
                            disabled:true,
                            checked: true,
                            width: 46
                        }, checkbox_item_all
                    ]
                });
                break;
        }
    }
}