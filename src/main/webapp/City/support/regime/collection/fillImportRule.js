/**
 * Created by wys on 2016/2/3.
 */

createModel('Ext.fillImportRule', function () {
    Ext.define('Ext.fillImportRule', {
        extend: 'Ext.window.Window',
        width: 325,
        height: 135,
        modal: true
    });
});
/**
 * 初始化
 */
Ext.fillImportRule.init = function (fnt, isCreate, record) {

    var form = new Ext.form.Panel({
        height: '100%',

        items: [{
            xtype: 'numberfield',
            hidden: true,
            name: 'id'
        }, {
            xtype: 'textfield',
            margin:'5 15',
            labelWidth:80,
            fieldLabel: '规则名称',
            name: 'ruleName'

        }]
    });
    if (!isCreate) {
        form.loadRecord(record)
    }
    var win = new Ext.fillImportRule({
        layout:'fit',
        items: [form],
        buttons: [{
            text: '保存',
            handler: function () {
                if (fnt) {
                    if (form.isValid()) {
                        fnt(form.getForm().getFieldValues());
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
};