/**
 * Created by wxl on 2016/2/23.
 * 保存数据集窗口
 */
createModel('Ext.saveDataSetWin', function () {
    Ext.define('Ext.saveDataSetWin', {
        extend: 'Ext.window.Window',
        width: 450,
        modal: true,
        title: '保存数据集窗口'
    });
});
/**
 * 初始化窗口
 * @param record 要修改的数据集,若为空,则添加数据集
 * @param fn 回调函数
 */
Ext.saveDataSetWin.init = function (record, fn) {
    var form = new Ext.form.Panel({
        width: '100%',
        layout: 'column',
        items: [{
            xtype: 'hidden',
            name: 'id'
        }, {
            xtype: 'textfield',
            name: 'name',
            fieldLabel: '数据集名<b style="color: red;">*</b>',
            labelWidth: 70,
            labelAlign: 'right',
            columnWidth: 0.9,
            margin: '20 20',
            allowBlank: false
        }, {
            xtype: 'textarea',
            name: 'comments',
            fieldLabel: '说明',
            labelWidth: 70,
            labelAlign: 'right',
            columnWidth: 0.9,
            margin: '0 20 20 20'
        }]
    });
    if (record) {
        form.loadRecord(record);
    }
    var win = new Ext.saveDataSetWin({
        items: [form],
        buttons: [{
            text: '保存',
            handler: function () {
                var nameValue = form.getValues().name;
                if (form.isValid() && (nameValue.trim().length > 0)) {
                    form.submit({
                        url: baseUrl + '/saveDataSet',
                        success: function (form, action) {
                            win.close();
                            if (fn) {
                                fn();
                            }
                        },
                        failure: function (form, action) {
                            Ext.Msg.alert('失败', '保存失败');
                        }
                    });
                } else {
                    Ext.Msg.alert('提示', '数据集名称不能为空');
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
};
