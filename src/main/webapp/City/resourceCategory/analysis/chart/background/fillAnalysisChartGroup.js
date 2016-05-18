/**
 * Created by wys on 2016/1/21.
 */
createModel('Ext.fillAnalysisChartGroup', function () {
    Ext.define('Ext.fillAnalysisChartGroup', {
        extend: 'Ext.window.Window',
        width: 300,
        closeAction: 'destroy',
        modal: true
    });
});
Ext.fillAnalysisChartGroup.init = function (fnt, record) {
    var isCreate = false;
    if (!record) {
        isCreate = true;
    }
    var textField = new Ext.form.field.Text({
        name: 'name',
        fieldLabel: '分组名称<b style="color:red">*</b>',
        labelWidth: 80,
        margin: '5 15',
        maxLength:50,
        allowBlank: false,
        validator:function(text){
            if(text.length && text.replace(/\s+/g, "").length<text.length){
                return "不允许输入空格！";
            }else {
                return true;
            }
        }
    });

    var textContainer = new Ext.form.FieldContainer({
        labelWidth: 80,
        margin: '5 15',
        items: [textField]
    });
    var form = new Ext.form.Panel({
        layout: 'vbox',
        items:[textContainer]
    });
    //form.add(textField);
    if (isCreate) {
        var radioContainer = new Ext.form.FieldContainer({
            defaultType: 'radiofield',
            labelWidth: 80,
            margin: '5 15',
            defaults: {
                flex: 1,
                margin: '0 15'
            },
            layout: 'hbox',
            items: [
                {
                    boxLabel: '添加同级',
                    name: 'level',
                    checked: true,
                    inputValue: '0'
                }, {
                    boxLabel: '添加下级',
                    name: 'level',
                    inputValue: '1'
                }
            ]
        });
        form.add(radioContainer);
    } else
        form.loadRecord(record);

    var win = new Ext.fillAnalysisChartGroup({
        title: "分析图表分组",
        width: 300,
        height: 150,
        layout: 'fit',
        items: [form],
        buttons: [{
            text: '保存',
            handler: function () {
                if (fnt && form.isValid()) {
                    fnt(form.getForm().getValues());
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