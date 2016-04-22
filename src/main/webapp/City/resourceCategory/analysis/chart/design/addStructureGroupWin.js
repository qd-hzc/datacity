/**
 * Created by wgx on 2016/3/3.
 */
createModel('Ext.addStructureGroup', function () {
    Ext.define('Ext.addStructureGroup', {
        extend: 'Ext.window.Window',
        width: 180,
        closeAction: 'destroy',
        resizable: false,
        modal: true,
        title: "是否添加下级"
    });
});
/**
 *
 * @param record
 * @param fn           回调函数
 */
Ext.addStructureGroup.init = function (record, fn) {

    var radioContainer = new Ext.form.FieldContainer({
        defaultType: 'radiofield',
        labelWidth: 80,
        margin: '25 15 ',
        defaults: {
            flex: 1,
            margin: '0 55 0 30'
        },
        layout: 'hbox',
        items: [
            {
                boxLabel: '是',
                name: 'group',
                checked: true,
                inputValue: '1'
            }, {
                boxLabel: '否',
                name: 'group',
                inputValue: '0'
            }
        ]
    });
    var form = new Ext.form.Panel({
        layout: 'vbox',
        items: [radioContainer]
    });
    var win = new Ext.addStructureGroup({
        width: 300,
        height: 150,
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
