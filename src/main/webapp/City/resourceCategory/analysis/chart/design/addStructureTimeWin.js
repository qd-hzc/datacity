/**
 * Created by wgx on 2016/3/3.
 */
createModel('Ext.addStructureTime', function () {
    Ext.define('Ext.addStructureTime', {
        extend: 'Ext.window.Window',
        width: 180,
        closeAction: 'destroy',
        resizable: false,
        modal: true,
        title: "时间"
    });
});
/**
 *
 * @param record
 * @param fn           回调函数
 */
Ext.addStructureTime.init = function (record, fn) {

    var radioContainer = new Ext.form.FieldContainer({
        defaultType: 'radiofield',
        labelWidth: 80,
        margin: '25 15 ',
        defaults: {
            flex: 1,
            margin: '0 55 0 30'
        },
        layout: 'vbox',
        items: [
            {
                boxLabel: '连续报告期',
                name: 'time',
                checked: true,
                inputValue: '0'
            }, {
                boxLabel: '选择报告期',
                name: 'time',
                inputValue: '1'
            }, {
                boxLabel: '最新报告期',
                name: 'time',
                inputValue: '2'
            }
        ]
    });
    var form = new Ext.form.Panel({
        layout: 'hbox',
        items: [radioContainer]
    });
    var win = new Ext.addStructureTime({
        width: 500,
        height: 350,
        layout: 'fit',
        items: [form],
        buttons: [{
            text: '保存',
            handler: function () {
                if (fn && form.isValid()) {
                    fn(form.getForm().getValues());
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
