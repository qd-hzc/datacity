/**
 * Created by wgx on 2016/1/14.
 */
/**
 * Created by wgx on 2016/1/14.
 */
Ext.define('Ext.addUnitdataInfoWin', {
    extend: 'Ext.window.Window',
    width: 600,
    modal: true,
    title: '添加元数据窗口'
});
/**
 * 初始化
 * @param record     系统元数据类型
 * @param path       地址
 * @param fn
 */
Ext.addUnitdataInfoWin.init = function (record, type, fn) {
    var form = new Ext.form.Panel({
        width: '100%',
        layout: 'column',
        items: [{
            xtype: 'hidden',
            name: 'id'
        }, {
            xtype: 'hidden',
            name: 'type',
            value: type
        }, {
            xtype: 'textfield',
            name: 'name',
            allowBlank: false,
            fieldLabel: '名称<b style="color:red">*</b>',
            labelWidth: 70,
            labelAlign: 'right',
            columnWidth: 0.45,
            maxLength: 50,
            margin: '20 0 0 0',
            validator: function (text) {
                if (text.length && text.replace(/\s+/g, "").length < text.length) {
                    return "不允许输入空格！";
                } else {
                    return true;
                }
            }
        }, {
            xtype: 'textfield',
            name: 'code',
            fieldLabel: '代码',
            labelWidth: 70,
            labelAlign: 'right',
            columnWidth: 0.45,
            margin: '20 0 0 0'
        }, {
            xtype: 'textfield',
            name: 'r1',
            allowBlank: false,
            fieldLabel: '数据格式<b style="color:red">*</b>',
            labelWidth: 70,
            labelAlign: 'right',
            columnWidth: 0.45,
            margin: '20 0 0 0'
        }, {
            xtype: 'textarea',
            name: 'comments',
            fieldLabel: '备注',
            labelWidth: 70,
            labelAlign: 'right',
            maxLength: 1000,
            columnWidth: 0.9,
            margin: '20 0 20 0'
        }]
    });
    var win = new Ext.addUnitdataInfoWin({
        items: [form],
        buttons: [{
            text: '保存',
            handler: function () {
                if (form.isValid()) {
                    if (fn) {
                        if (form.isValid()) {
                            if (record)
                                fn(form.getForm().getFieldValues());
                            else
                                fn(form.getForm().getFieldValues());
                        }
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
    if (record) {
        form.loadRecord(record);
        win.setTitle('修改元数据窗口');
    }
    win.show();
    return win;
}
