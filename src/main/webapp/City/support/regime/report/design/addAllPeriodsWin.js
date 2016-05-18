/**
 * Created by CRX on 2016/5/12.
 */

Ext.define('Ext.addAllPeriodsWin', {
    extend: 'Ext.window.Window',
    width: 600,
    modal: true,
    title: '批量导出报表'
});
Ext.addAllPeriodsWin.init = function (research, time, fn) {
    //表单
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
    for (var i = 0; i < time.length; i++) {
        var year = time[i].year;
        //根据年份循环创建
        var yearFieldSet = Ext.create('Ext.form.FieldSet', {
            title: year,
            margin: '10 10 10 10',
            //height: 80,
            items: [],
        });

        var period = time[i].period;
        var checkboxGroup = new Ext.form.CheckboxGroup({
            name: year,
            items: [],
            column: 6,
            id: 'checkbox' + i
        });
        //var array = [];
        for (var j = 0; j < period.length; j++) {
            var prd = period[j];
            var inputValue = prd.id;
            var boxLabel = prd.name;
            var id = prd.id + '-' + year;
//          根据时间频度创建checkBox
            var periodCheckBox = new Ext.form.field.Checkbox({
                inputValue: inputValue,
                boxLabel: boxLabel,
                id: id,
                width: 50,
                checked: true,
                name: year
            });
            //array.push(periodCheckBox)
            checkboxGroup.add(periodCheckBox);
        }
        //checkboxGroup.add(array);
        yearFieldSet.add(checkboxGroup);
        form.add(yearFieldSet);
    }
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
                $.each(time, function (i) {//循环年
                    var array = form.getForm().findField('checkbox' + i).items;
                    array.each(function (item) {
                        if (!item.getValue()) {
                            item.setValue(true);
                        }
                    });
                });

            } else {
                $.each(time, function (i) {//循环年
                    var array = form.getForm().findField('checkbox' + i).items;
                    array.each(function (item) {
                        item.setValue(false);
                    });
                })
            }
        },

    });
    var yearFieldSet_all = Ext.create('Ext.form.FieldSet', {
        title: '全部操作',
        margin: '10 10 10 10',
        height: 50,
        items: []
    });
    checkboxGroup_all.add(checkbox_all);
    yearFieldSet_all.add(checkboxGroup_all);
    form.add(yearFieldSet_all)
    var win = new Ext.addAllPeriodsWin({
        items: [form],
        buttons: [{
            text: '导出',
            handler: function () {
                var yearArray = new Array();//年
                for (var i = 0; i < time.length; i++) {
                    var monthArray = new Array();
                    var nameArray = new Array();//月、季、半年

//                  获取选中的checkbox及所属年份
                    var array = form.getForm().findField("checkbox" + i).items;
                    var year = form.getForm().findField("checkbox" + i).name;
                    array.each(function (item) {
                        if (item.getValue()) {
                            var value = item.inputValue;
                            var name = item.boxLabel;
                            if (item.disabled == false) {
                                monthArray.push(value);
                                nameArray.push(name);
                            }
                        }
                    });
                    if (monthArray.length > 0) {
                        var obj = {
                            year: year,
                            month: monthArray.join(","),
                            name: nameArray.join(","),
                        }
                        yearArray.push(obj);
                    }
                    if (yearArray.length > 0) {
                        window.location.href = GLOBAL_PATH +
                            '/resourcecategory/analysis/report/designCustomResearch/checkExportToExcel?research='
                            + JSON.stringify(research) + '&yearArray=' + JSON.stringify(yearArray);
                        win.close();
                    } else {
                        Ext.Msg.alert('提示', "请选择要导出的报表时间");
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
}