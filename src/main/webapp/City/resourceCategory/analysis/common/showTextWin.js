/**
 * Created by HZC on 2016/5/17.
 */
createModel('Ext.showTextWin', function () {
    Ext.define('Ext.showTextWin', {
        extend: 'Ext.window.Window',
        width: 800,
        closeAction: 'destroy',
        modal: true
    });
});
Ext.showTextWin.init = function (record) {
    var isChecked = record.status == 4;
    var form = new Ext.form.Panel({
        width: '100%',
        layout: 'column',
        border: false,
        items: [{
            xtype: 'hidden',
            name: 'id',
            value: record.id
        }, {
            xtype: 'hidden',
            name: 'themeId',
            value: record.theme.id
        }, {
            xtype: 'ueditor',
            name: 'content',
            columnWidth: 1,
            height: 300,
            margin: '0 0 20 0',
            ueditorConfig: {
                readonly: isChecked
            },
            value: record.content
        }]
    });
    var win = new Ext.window.Window({
        title: '添加内容窗口',
        width: 800,
        modal: true,
        items: [form],
        buttons: [{
            id: "formSave",
            text: '保存',
            hidden: isChecked,
            handler: function () {
                if (form.isValid()) {
                    var fieldValues = form.getValues();
                    Ext.Ajax.request({
                        url: GLOBAL_PATH + "/support/resourceCategory/analysis/text/updateTextContent",
                        method: 'POST',
                        jsonData: fieldValues,
                        params: {
                            themeId: record.theme.id
                        },
                        success: function (response, opts) {
                            var result = Ext.JSON.decode(response.responseText);
                            if (result.success) {
                                Ext.Msg.alert('提示', '成功');
                                win.close();
                            } else {
                                Ext.Msg.alert('提示', '失败，请重试');
                            }
                        }
                    })
                }
            }
        }, {
            text: '关闭',
            handler: function () {
                win.close();
            }
        }]
    });
    win.setTitle('查看');
    win.show();
};
