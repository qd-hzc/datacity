/**
 * Created by Administrator on 2015/12/31 0031.
 * 添加指标口径窗口
 */
createModel('Ext.addCaliberWin', function () {
    Ext.define('Ext.addCaliberWin', {
        extend: 'Ext.window.Window',
        width: 600,
        modal: true,
        title: '添加指标口径窗口'
    });
});
/**
 * 初始化
 */
Ext.addCaliberWin.init = function (itemId, record, fn) {
    var form = new Ext.form.Panel({
        width: '100%',
        layout: 'column',
        items: [{
            xtype: 'hidden',
            name: 'id'
        }, {
            xtype: 'hidden',
            name: 'itemId',
            value: itemId
        }, {
            xtype: 'hidden',
            name: 'sortIndex'
        }, {
            xtype: 'textfield',
            name: 'name',
            allowBlank: false,
            fieldLabel: '口径名<b style="color:red">*</b>',
            labelWidth: 70,
            labelAlign: 'right',
            columnWidth: 0.9,
            margin: '20 0 0 0'
        }, {
            xtype: 'textarea',
            name: 'itemExplain',
            fieldLabel: '指标解释',
            labelWidth: 70,
            labelAlign: 'right',
            columnWidth: 0.45,
            margin: '20 0 0 0',
            maxLength: 250
        }, {
            xtype: 'textarea',
            name: 'statisticsScope',
            fieldLabel: '统计范围',
            labelWidth: 70,
            labelAlign: 'right',
            columnWidth: 0.45,
            margin: '20 0 0 0',
            maxLength: 250
        }, {
            xtype: 'textarea',
            name: 'statisticsMethod',
            fieldLabel: '统计方法',
            labelWidth: 70,
            labelAlign: 'right',
            columnWidth: 0.45,
            margin: '20 0 20 0',
            maxLength: 250
        }, {
            xtype: 'textarea',
            name: 'countMethod',
            fieldLabel: '计算方法',
            labelWidth: 70,
            labelAlign: 'right',
            columnWidth: 0.45,
            margin: '20 0 20 0',
            maxLength: 250
        }]
    });
    if (record) {
        form.loadRecord(record);
    }
    var win = new Ext.addCaliberWin({
        items: [form],
        buttons: [{
            text: '保存',
            handler: function () {
                if (form.isValid()) {
                    form.submit({
                        url: Global_Path + '/saveCaliber',
                        success: function (form, action) {
                            Ext.Msg.alert('成功', action.result.msg);
                            win.close();
                            if (fn) {
                                fn(action.result.datas);
                            }
                        },
                        failure: function (form, action) {
                            Ext.Msg.alert('失败', "保存失败");
                        }
                    });
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
        win.setTitle('修改指标口径窗口');
    }
    win.show();
    return win;
};

