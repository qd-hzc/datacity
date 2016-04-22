/**
 * Created by wgx on 2016/3/18.
 */
createModel('Ext.addContentWin', function () {
    Ext.define('Ext.addContentWin', {
        extend: 'Ext.window.Window',
        width: 800,
        closeAction: 'destroy',
        modal: true
    });
});
Ext.addContentWin.init = function (record, fn) {
    var isChecked = record.get("status") == TEXT_CONTENT_STATUS.CHECKED;
    var form = new Ext.form.Panel({
        width: '100%',
        layout: 'column',
        border: false,
        items: [{
            xtype: 'hidden',
            name: 'id'
        }, {
            xtype: 'ueditor',
            name: 'content',
            columnWidth: 1,
            height: 300,
            margin: '0 0 20 0',
            ueditorConfig: {
                readonly: isChecked
            }
        }]
    });
    var win = new Ext.window.Window({
        title: '添加内容窗口',
        width: 800,
        modal: true,
        items: [form],
        buttons: [{
            id:"formSave",
            text: '保存',
            hidden: isChecked,
            handler: function () {
                if (form.isValid()) {
                    if (fn) {
                        fn(form.getForm().getFieldValues());
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
    console.log(record);
    form.loadRecord(record);
    if (isChecked) {
        win.setTitle('查看');
    } else {
        win.setTitle('添加');
    }
    win.show();
    return win;

}
